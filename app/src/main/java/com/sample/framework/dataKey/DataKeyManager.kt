/*
 * Copyright (c) 2019 AirWatch, LLC. All rights reserved.
 *  This product is protected by copyright and intellectual property laws in  the United States
 *  and other countries as well as by international treaties.
 *  AirWatch products may be covered by one or more patents listed at
 *  http://www.vmware.com/go/patents.
 */

package com.sample.framework.dataKey

import androidx.annotation.WorkerThread
import com.airwatch.crypto.openssl.OpenSSLCryptUtil
import com.airwatch.crypto.provider.AWSecurityProvider
import com.airwatch.crypto.util.KeyGuard
import com.airwatch.sdk.AirWatchSDKException
import com.airwatch.sdk.SDKStatusCode
import com.airwatch.sdk.context.SDKAction
import com.airwatch.sdk.context.SDKContextManager
import com.airwatch.sdk.context.state.SDKActionListener
import com.airwatch.util.*
import com.sample.main.SampleAppDB
import kotlinx.coroutines.runBlocking
import java.security.SecureRandom

internal const val MASTER_DATA_KEY = "MasterDataKey"
private const val TAG = "KeyManager"

/**
 * A utility class to manage keys for both SDK and third-party applications
 */
object DataKeyManager: SDKActionListener {
    const val DEFAULT_KEY_CATEGORY = "DEFAULT"

    private val dataKeyDao = SampleAppDB.getInstance().getDataKeyRecordDao()
    private val cryptUtil = OpenSSLCryptUtil.getInstance()!!

    private var masterDataKey: ByteArray = ByteArray(0)
        get() {
            if (!field.isEmptyOrZero())
                return field
            assertSDKIsReadyChecked()
            val appSecurePrefs = SDKContextManager.getSDKContext().appSecurePreferences
            var key = appSecurePrefs.getString(MASTER_DATA_KEY, null)
            if (key == null) {
                key = generateKey().toBase64()
                appSecurePrefs.edit().putString(MASTER_DATA_KEY, key).apply()
            }
            field = KeyGuard.secure(key.fromBase64(), KeyGuard.KeyLifespan.CONTEXT)
            return field
        }
        set(value) {
            val sharedPrefs = SDKContextManager.getSDKContext().appSecurePreferences.edit()
            if (value.isEmptyOrZero())
                sharedPrefs.remove(MASTER_DATA_KEY).apply()
            else sharedPrefs.putString(MASTER_DATA_KEY, value.toBase64()).apply()

            field = KeyGuard.secure(value, KeyGuard.KeyLifespan.CONTEXT)
        }

    init {
        SDKContextManager.getSDKContext().stateManager.registerListener(this)
    }

    /**
     * Gets a key associated with the given [alias] and [category] or null if none present. The lifecycle of this key
     * follows that of SDK session. It is caller's responsibility to manage keys that are derived from
     * this (like cloned keys, derived keys, etc.)
     * @param alias Alias for the key
     * @param category for the key to be retrieved. [DEFAULT_KEY_CATEGORY] is the default value
     * @throws AirWatchSDKException if SDK session is not active
     */
    @Throws(AirWatchSDKException::class)
    @WorkerThread
    @JvmStatic
    @JvmOverloads
    fun getKeyOrNull(alias: String, category: String = DEFAULT_KEY_CATEGORY): ByteArray? {
        assertSDKIsReadyChecked()
        require(alias.isNotEmpty()) { "Key alias cannot be empty" }

        return runBlocking {
            dataKeyDao.getRecord(alias, category)?.let {
                val unWrappedKey = cryptUtil.aesUnwrapKey(masterDataKey, it.wrappedKey.fromBase64())
                KeyGuard.secure(unWrappedKey, KeyGuard.KeyLifespan.CONTEXT)
            }
        }
    }

    /**
     * Gets a key associated with the given [alias] or creates a new one with given [category], stores and returns if none present.
     * The lifecycle of this key follows that of SDK session. It is caller's responsibility to manage
     * keys that are derived from this (like cloned keys, derived keys, etc.)
     *
     * Note: keySize has to be a multiple of 8. Throws [IllegalArgumentException] otherwise
     * @param alias Alias for the key
     * @param keySize size of the key(in bytes) if key is created
     * @param category for the key to be retrieved. [DEFAULT_KEY_CATEGORY] is the default value
     * @throws AirWatchSDKException if SDK session is not active
     */
    @JvmOverloads
    @Throws(AirWatchSDKException::class)
    @WorkerThread
    @JvmStatic
    fun getKeyOrCreateNew(alias: String, keySize: Byte = 32, category: String = DEFAULT_KEY_CATEGORY): ByteArray {
        assertSDKIsReadyChecked()
        require(alias.isNotEmpty()) { "Key alias cannot be empty" }
        require(category.isNotEmpty()) { "Key category cannot be empty" }
        return runBlocking {
            val key = dataKeyDao.getRecord(alias, category)?.let {
                val unWrappedKey = cryptUtil.aesUnwrapKey(masterDataKey, it.wrappedKey.fromBase64())
                val temp: ByteArray? = KeyGuard.secure(unWrappedKey, KeyGuard.KeyLifespan.CONTEXT)
                if (temp.isEmptyOrZero()) {
                    Logger.e(TAG, "Unable to unwrap key in storage")
                    throw AirWatchSDKException(SDKStatusCode.SDK_RES_UNEXPECTED_EXCEPTION)
                }
                temp
            }

            if (key == null) {
                val temp = generateKey(keySize)
                storeKey(alias, temp)
                temp
            } else key
        }
    }

    /**
     * Generates a cryptographically strong key of size defined by [keySize] in bytes. The lifecycle
     * of this key follows that of SDK session. It is caller's responsibility to manage
     * keys that are derived from this (like cloned keys, derived keys, etc.)
     *
     * Note: [keySize] has to be a multiple of 8. Throws [IllegalArgumentException] otherwise
     */
    @JvmOverloads
    @JvmStatic
    fun generateKey(keySize: Byte = 32): ByteArray {
        require(keySize % 8 == 0) { "Keysize has to be multiple of 8" }
        val key = ByteArray(keySize.toInt())
        SecureRandom.getInstance(AWSecurityProvider.AW_OPENSSL_SECURE_RANDOM).nextBytes(key)
        return KeyGuard.secure(key, KeyGuard.KeyLifespan.CONTEXT)
    }

    /**
     * Stores [key] in SDK storage and associates them with [alias] and [category]. If [key] is null or full of zeros,
     * key entry is removed from SDK storage. The lifecycle of this key follows that of SDK session. It is caller's responsibility
     * to manage keys that are derived from this (like cloned keys, derived keys, etc.)
     *
     * Note: keySize has to be a multiple of 8. Throws [IllegalArgumentException] otherwise
     * @param alias Alias for the key
     * @param key Key to be stored
     * @param category for the key to be stored. [DEFAULT_KEY_CATEGORY] is the default value
     * @throws AirWatchSDKException if SDK session is not active
     * @return [true] if the operation was successful or any key entry was modified, [false] otherwise
     */
    @Throws(AirWatchSDKException::class)
    @WorkerThread
    @JvmStatic
    @JvmOverloads
    fun storeKey(alias: String, key: ByteArray?, category: String = DEFAULT_KEY_CATEGORY): Boolean {
        assertSDKIsReadyChecked()
        require(alias.isNotEmpty()) { "Key alias cannot be empty" }
        require(category.isNotEmpty()) { "Key category cannot be empty" }
        return runBlocking {
            if (key.isEmptyOrZero()) {
                removeKey(alias)
            } else {
                KeyGuard.secure(key, KeyGuard.KeyLifespan.CONTEXT)
                val wrappedKey = cryptUtil.aesWrapKey(masterDataKey, key!!)
                dataKeyDao.insertDataKeyRecord(DataKeyRecord(alias, wrappedKey.toBase64(), category)) != 0L
            }
        }
    }

    /**
     * Removes key associated with [alias] and [category] in key storage. If key doesn't exist, no change is expected.
     * @param alias of the key to be removed
     * @param category for the key to be stored. [DEFAULT_KEY_CATEGORY] is the default value
     * @return [true] if a key was removed, [false] otherwise
     */
    @JvmStatic
    @JvmOverloads
    @WorkerThread
    fun removeKey(alias: String, category: String = DEFAULT_KEY_CATEGORY): Boolean {
        require(alias.isNotEmpty()) { "Key alias cannot be empty" }
        require(category.isNotEmpty()) { "Key category cannot be empty" }
        return runBlocking {
            dataKeyDao.deleteDataKeyRecord(alias, category) != 0
        }
    }

    /**
     * Clears all keys from SDK storage
     */
    @WorkerThread
    @JvmStatic
    fun clearAllKeys() {
        runBlocking {
            dataKeyDao.deleteAllRecords()
        }
    }

    /**
     * Clears all keys from SDK storage for given [category]
     * @param category for the key to be stored
     */
    @WorkerThread
    @JvmStatic
    fun clearAllKeysFromCategory(category: String) {
        runBlocking {
            dataKeyDao.deleteAllRecordsFromCategory(category)
        }
    }

    override fun onAction(sdkAction: SDKAction, params: MutableMap<String, Any>?) {
        //Clear master data key and all of application's key when SDK wipes itself
        if (sdkAction == SDKAction.SDK_RESET) {
            clearAllKeys()
            masterDataKey = ByteArray(0)
        }
    }
}

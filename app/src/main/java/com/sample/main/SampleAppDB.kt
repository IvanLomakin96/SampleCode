package com.sample.main

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.airwatch.sdk.context.SDKContextManager
import com.sample.framework.dataKey.DataKeyRecord
import com.sample.framework.dataKey.DataKeyRecordDao

private const val DATABASE_VERSION = 1
private const val DATABASE_NAME = "sampleApp.db"

@Database(
        entities = [DataKeyRecord::class],
        version = DATABASE_VERSION)
abstract class SampleAppDB : RoomDatabase() {

    abstract fun getDataKeyRecordDao(): DataKeyRecordDao

    companion object {
        @Volatile
        private var INSTANCE: SampleAppDB? = null
        private const val TAG = "UnSecureDB"

        @JvmStatic
        fun getInstance(): SampleAppDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(SDKContextManager.getSDKContext().context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): SampleAppDB {
            return Room.databaseBuilder(
                    context,
                    SampleAppDB::class.java,
                    DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build()
        }

        @JvmStatic
        fun closeUnSecureDB(){
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
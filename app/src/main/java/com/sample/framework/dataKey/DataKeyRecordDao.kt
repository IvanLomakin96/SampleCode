package com.sample.framework.dataKey

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface DataKeyRecordDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertDataKeyRecord(dataKeyRecord: DataKeyRecord): Long

    @Query("DELETE FROM $KEYS_TABLE_NAME")
    suspend fun deleteAllRecords(): Int

    @Query("DELETE FROM $KEYS_TABLE_NAME WHERE alias LIKE (:alias) AND category LIKE (:category)")
    suspend fun deleteDataKeyRecord(alias: String, category: String): Int

    @Query("SELECT * FROM $KEYS_TABLE_NAME")
    suspend fun getAllRecords(): List<DataKeyRecord>

    @Query("DELETE FROM $KEYS_TABLE_NAME WHERE category LIKE (:category)")
    suspend fun deleteAllRecordsFromCategory(category: String): Int

    @Query("SELECT * FROM $KEYS_TABLE_NAME WHERE alias LIKE (:alias) AND category LIKE (:category)")
    suspend fun getRecord(alias: String, category: String): DataKeyRecord?
}
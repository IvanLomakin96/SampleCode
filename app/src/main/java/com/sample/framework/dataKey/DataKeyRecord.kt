package com.sample.framework.dataKey

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

internal const val KEYS_TABLE_NAME = "DataKeyTable"
internal const val KEYS_TABLE_CREATE_STMT = "CREATE TABLE IF NOT EXISTS $KEYS_TABLE_NAME (alias TEXT NOT NULL, " +
        "wrappedKey TEXT NOT NULL, " +
        "category TEXT NOT NULL, " +
        "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)"

@Entity(tableName = KEYS_TABLE_NAME)
data class DataKeyRecord(
        val alias: String,
        val wrappedKey: String,
        val category: String,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id") val id: Long = 0)
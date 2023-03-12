package com.example.naversearch

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id :Int?,

    @ColumnInfo(name = "title")
    var title: String?

)

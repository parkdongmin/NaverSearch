package com.example.naversearch

import androidx.room.*

@Dao
interface HistoryDAO {
    @Query("SELECT * FROM table_history")
    fun getAll(): List<HistoryEntity>

    // title 에 해당하는 selected 값 가져오기
    @Query("SELECT * FROM table_history WHERE title IN (:title)")
    fun getTitle(title: String) : Boolean

    // title 저장 - 중복 값 충돌 발생 시 새로 들어온 데이터로 교체.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTitle(userHistoryEntity: HistoryEntity)

    // title 삭제
    @Delete
    fun deleteTitle(userHistoryEntity: HistoryEntity)

    @Query("SELECT COUNT(*) FROM table_history")
    fun getCount(): Int
}
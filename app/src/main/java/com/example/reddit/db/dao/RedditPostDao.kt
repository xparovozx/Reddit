package com.example.reddit.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.reddit.data.RedditItem
import com.example.reddit.db.DataBaseContract

@Dao
interface RedditPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPosts(posts: List<RedditItem.RedditPost>)

    @Update
    fun updatePosts(posts: List<RedditItem.RedditPost>)

    @Query("SELECT * FROM ${DataBaseContract.RedditPostContract.TABLE_NAME} WHERE ${DataBaseContract.RedditPostContract.Columns.CATEGORY} =:postCategory ")
    fun getPosts(postCategory: String): PagingSource<Int, RedditItem.RedditPost>

    @Query("SELECT * FROM ${DataBaseContract.RedditPostContract.TABLE_NAME} WHERE ${DataBaseContract.RedditPostContract.Columns.TITLE} = :subQuery")
    fun getSearchedNews(subQuery: String): PagingSource<Int, RedditItem.RedditPost>

    @Query("DELETE FROM ${DataBaseContract.RedditPostContract.TABLE_NAME}  WHERE ${DataBaseContract.RedditPostContract.Columns.CATEGORY} =:postCategory")
    fun cleanRedditTableByCategory(postCategory: String)
}
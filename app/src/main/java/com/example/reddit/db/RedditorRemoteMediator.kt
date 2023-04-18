package com.example.reddit.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.reddit.data.RedditItem
import com.example.reddit.networking.RedditApi
import retrofit2.HttpException
import java.io.IOException

@ExperimentalPagingApi
@OptIn(ExperimentalPagingApi::class)
class RedditorRemoteMediator(
    private val redditApi: RedditApi,
    private val dataBase: RedditDataBase,
) : RemoteMediator<Int, RedditItem.Redditor>() {

    private val redditorDao = dataBase.redditorDao()
    private val remoteKeyDao = dataBase.redditorRemoteKeyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RedditItem.Redditor>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = dataBase.withTransaction {
                        remoteKeyDao.getRedditorRemoteKeys(redditorCategory = "redditor")
                    }
                    if (remoteKey?.nextKey == null) return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                    remoteKey.nextKey
                }
            }

            val apiResponse = redditApi.getUserFriendsList(
                after = loadKey,
                before = null,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )
            val friendNamesList =apiResponse.dataResponse.children
            val listOfFriends = mutableListOf<RedditItem.Redditor>()

            friendNamesList.forEach {
                val redditor = redditApi.getRedditorInfo(redditorName = it.name).dataResponse
                listOfFriends.add(redditor)
            }

            dataBase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    redditorDao.cleanRedditors()
                    remoteKeyDao.clearRemoteKeys(redditorCategory = "redditor")
                }
                remoteKeyDao.insertAll(
                    listOf(
                        RemoteRedditorKeys(
                            "redditor",
                            apiResponse.dataResponse.after
                        )
                    )
                )
                redditorDao.insertRedditors(listOfFriends)
            }
            return MediatorResult.Success(endOfPaginationReached = listOfFriends.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}

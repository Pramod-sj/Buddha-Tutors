package com.buddhatutors.core.data.firebase.firestore.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.buddhatutors.core.data.model.UserEntity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

internal class FireStoreUsersPagingSource(
    private val queryUsers: Query,
) : PagingSource<QuerySnapshot, UserEntity>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, UserEntity>): QuerySnapshot? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, UserEntity> {
        return try {
            val currentPage = params.key ?: queryUsers.get().await()
            val lastVisibleProduct = currentPage.documents.lastOrNull()

            val nextPage = lastVisibleProduct?.let {
                queryUsers.startAfter(it).get().await()
            }

            val data = currentPage.toObjects(UserEntity::class.java)
            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = if (nextPage != null && nextPage.isEmpty.not()) nextPage else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
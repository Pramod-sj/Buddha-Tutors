package com.buddhatutors.core.data.firebase.firestore.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.buddhatutors.core.data.model.tutorlisting.TutorListingEntity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

internal class FireStoreAllTutorPagingSource(
    private val queryAllTutors: Query,
) : PagingSource<QuerySnapshot, TutorListingEntity>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TutorListingEntity>): QuerySnapshot? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TutorListingEntity> {
        return try {
            val currentPage = params.key ?: queryAllTutors.get().await()
            val lastVisibleProduct = currentPage.documents.lastOrNull()

            val nextPage = lastVisibleProduct?.let {
                queryAllTutors.startAfter(it).get().await()
            }

            val data = currentPage.toObjects(TutorListingEntity::class.java)
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
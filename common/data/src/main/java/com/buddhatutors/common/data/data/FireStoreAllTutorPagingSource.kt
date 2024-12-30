package com.buddhatutors.common.data.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.buddhatutors.common.data.data.model.tutorlisting.TutorListingE
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

internal class FireStoreAllTutorPagingSource(
    private val queryAllTutors: Query,
) : PagingSource<QuerySnapshot, TutorListingE>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TutorListingE>): QuerySnapshot? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TutorListingE> {
        return try {
            val currentPage = params.key ?: queryAllTutors.get().await()
            val lastVisibleProduct = currentPage.documents.lastOrNull()

            val nextPage = lastVisibleProduct?.let {
                queryAllTutors.startAfter(it).get().await()
            }

            val data = currentPage.toObjects(TutorListingE::class.java)
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
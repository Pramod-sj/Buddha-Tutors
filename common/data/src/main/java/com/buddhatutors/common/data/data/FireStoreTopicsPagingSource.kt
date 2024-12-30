package com.buddhatutors.common.data.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.buddhatutors.common.data.data.model.TopicE
import com.buddhatutors.common.data.data.model.tutorlisting.TutorListingE
import com.buddhatutors.common.domain.model.Topic
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

internal class FireStoreTopicsPagingSource(
    private val queryAllTopics: Query,
) : PagingSource<QuerySnapshot, TopicE>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, TopicE>): QuerySnapshot? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TopicE> {
        return try {
            val currentPage = params.key ?: queryAllTopics.get().await()
            val lastVisibleProduct = currentPage.documents.lastOrNull()
            val nextPage = lastVisibleProduct?.let {
                queryAllTopics.startAfter(it).get().await()
            }

            val data = currentPage.toObjects(TopicE::class.java)
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
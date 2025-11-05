package com.pbl6.pbl6_cinestech.ui.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.repository.MovieRepository

class MoviePagingSource(
    private val repository: MovieRepository,
    private val pageSize: Int
) : PagingSource<Int, MovieResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResponse> {
        return try {

            val offset = params.key ?: 0
            val response = repository.getAllMovieNowShowing(
                limit = pageSize,
                offset = offset
            )

            val movies = response.data?.items
            val nextKey = if ((movies?.size?:0) < pageSize) null else offset + pageSize

            LoadResult.Page(
                data = movies?:listOf(),
                prevKey = if (offset == 0) null else offset - pageSize,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResponse>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(pageSize)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(pageSize)
        }
    }
}

package com.gchristov.thecodinglove.kmpsearchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpsearchdata.SearchException
import com.gchristov.thecodinglove.kmpsearchdata.model.Post

/**
Use-case to search for a random post, given a search session. Implementations should:
- obtain the total results for the given query, if not provided
- choose a random page index based on the total number of posts and posts per page
- obtain all posts for the given page
- choose a random post from the page
- return a summary of the search
 */
interface SearchWithHistoryUseCase {
    suspend operator fun invoke(
        query: String,
        totalPosts: Int? = null,
        searchHistory: Map<Int, List<Int>>,
    ) : Either<SearchException, Result>

    data class Result(
        val query: String,
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    )
}
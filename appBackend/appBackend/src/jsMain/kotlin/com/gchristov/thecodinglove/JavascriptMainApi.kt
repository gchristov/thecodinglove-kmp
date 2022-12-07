package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.kmpsearch.SearchModule
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal actual fun serveApi(args: Array<String>) {
    exports.myTestFun = FirebaseFunctions.https.onRequest { request, response ->
        val searchQuery: String = request.query["searchQuery"] ?: "release"
        val searchSessionId: String? = request.query["searchSessionId"]
        val test: String? = request.body["channel_name"]
        println(test)

        // TODO: Do not use GlobalScope
        GlobalScope.launch {
            println("Performing search")
            val search = SearchModule.injectSearchWithSessionUseCase()
            val searchType = searchSessionId?.let {
                SearchWithSessionUseCase.Type.WithSessionId(
                    query = searchQuery,
                    sessionId = it
                )
            } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
            search(searchType)
                .fold(
                    ifLeft = {
                        it.printStackTrace()
                        val jsonResponse = Json.encodeToString(Result.Empty)
                        response.send(jsonResponse)
                    },
                    ifRight = { searchResult ->
                        val jsonResponse = Json.encodeToString(searchResult.toResult())
                        response.send(jsonResponse)
                        println("Preloading next result")
                        val preload = SearchModule.injectPreloadSearchResultUseCase()
                        preload(searchResult.searchSessionId)
                            .fold(
                                ifLeft = { it.printStackTrace() },
                                ifRight = { println("Preload complete") }
                            )
                    }
                )
        }
    }
}

@Serializable
sealed class Result {
    @Serializable
    @SerialName("empty")
    object Empty : Result()

    @Serializable
    @SerialName("valid")
    data class Valid(
        val searchSessionId: String,
        val query: String,
        val postTitle: String,
        val postUrl: String,
        val postImageUrl: String,
        val totalPosts: Int,
    ) : Result()
}

private fun SearchWithSessionUseCase.Result.toResult() = Result.Valid(
    searchSessionId = searchSessionId,
    query = query,
    postTitle = post.title,
    postUrl = post.url,
    postImageUrl = post.imageUrl,
    totalPosts = totalPosts
)
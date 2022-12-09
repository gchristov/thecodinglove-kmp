package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.SlackSlashCommandRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SlackSlashCommandService(
    private val slackSlashCommandRepository: SlackSlashCommandRepository,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) {
    fun register() {
        exports.slackSlashCommand = slackSlashCommandRepository.observeSlashCommandRequest { request, response ->
            request.fold(
                ifLeft = { error ->
                    error.printStackTrace()
                    slackSlashCommandRepository.sendSlashCommandErrorResponse(response)
                },
                ifRight = { command ->
                    println(command)
                    val searchSessionId: String? = null

                    // TODO: Do not use GlobalScope
                    GlobalScope.launch {
                        println("Performing search")
                        val searchType = searchSessionId?.let {
                            SearchWithSessionUseCase.Type.WithSessionId(
                                query = command.text,
                                sessionId = it
                            )
                        } ?: SearchWithSessionUseCase.Type.NewSession(command.text)
                        searchWithSessionUseCase(searchType)
                            .fold(
                                ifLeft = {
                                    // TODO: Send better error responses
                                    slackSlashCommandRepository.sendSlashCommandErrorResponse(response)
                                },
                                ifRight = { searchResult ->
                                    // TODO: Send correct success responses
                                    slackSlashCommandRepository.sendSlashCommandResponse(
                                        result = searchResult,
                                        response = response
                                    )
                                    println("Preloading next result")
                                    preloadSearchResultUseCase(searchResult.searchSessionId)
                                        .fold(
                                            ifLeft = { it.printStackTrace() },
                                            ifRight = { println("Preload complete") }
                                        )
                                }
                            )
                    }
                }
            )
        }
    }
}
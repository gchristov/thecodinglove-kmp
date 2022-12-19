package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.bodyAsJson
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.slack.usecase.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    private val jsonSerializer: Json,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
) : ApiService(jsonSerializer) {
    override fun register() {
        exports.slackSlashCommand = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ) {
        verifySlackRequestUseCase(request).fold(
            ifLeft = {
                sendError(
                    error = it,
                    response = response
                )
            },
            ifRight = {
                try {
                    // TODO: Handle valid request
                    val command: ApiSlackSlashCommand =
                        requireNotNull(request.bodyAsJson(jsonSerializer))
                    response.sendJson(
                        data = command,
                        jsonSerializer = jsonSerializer
                    )
                } catch (error: Throwable) {
                    sendError(
                        error = error,
                        response = response
                    )
                }
            }
        )
    }
}
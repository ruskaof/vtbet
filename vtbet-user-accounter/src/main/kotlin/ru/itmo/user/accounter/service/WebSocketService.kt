package ru.itmo.user.accounter.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.stereotype.Service
import ru.itmo.common.dto.FileDto
import ru.itmo.user.accounter.configuration.WebSocketClient
import java.lang.reflect.Type
import java.util.concurrent.CompletableFuture

@Service
class WebSocketService(
    private val webSocketClient: WebSocketClient,
) {
    private val objectMapper = ObjectMapper()

    fun uploadFile(fileDto: FileDto): CompletableFuture<String> {
        val payload = objectMapper.writeValueAsString(fileDto)
        return sendAndGetString(
            "/file.upload",
            payload,
            "/file/url",
        )
    }

    fun downloadFile(fileName: String): CompletableFuture<String> {
        return sendAndGetString(
            "/file.download",
            fileName,
            "/file/file",
        )
    }

    fun getUrl(fileName: String): CompletableFuture<String> = sendAndGetString(
        "/file.get",
        fileName,
        "/file/url",
    )

    fun deleteFile(fileName: String): CompletableFuture<String> =
        sendAndGetString(
            "/file.delete",
            fileName,
            "/file/deleted",
        )

    private fun sendAndGetString(destination: String, payload: Any, subscription: String): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        val handler = object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = String::class.java

            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                try {
                    future.complete(payload as String)
                } catch (e: Exception) {
                    future.completeExceptionally(e)
                }
            }
        }
        webSocketClient.sendMessage(
            destination,
            payload,
            subscription,
            handler,
        )
        return future
    }
}
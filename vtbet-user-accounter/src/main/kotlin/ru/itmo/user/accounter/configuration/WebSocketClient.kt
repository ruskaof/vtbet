package ru.itmo.user.accounter.configuration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.naming.ServiceUnavailableException


@Component
class WebSocketClient(
    private val stompClient: WebSocketStompClient,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketClient::class.java)
    }

    private var session: StompSession? = null
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    @Value("\${websocket.s3-address}")
    private lateinit var url: String

    @Value("\${websocket.s3-topic}")
    private lateinit var s3Topic: String

    @Value("\${websocket.s3-app}")
    private lateinit var s3App: String

    private val handler = CustomStompSessionHandler()

    @PostConstruct
    fun init() {
        connectWithRetries()
        Runtime.getRuntime().addShutdownHook(Thread {
            session?.disconnect()
        })
    }

    private fun connectWithRetries() {
        scheduler.scheduleAtFixedRate({
            try {
                if (session == null || !session!!.isConnected) {
                    val task = ThreadPoolTaskScheduler()
                    task.initialize()
                    stompClient.taskScheduler = task
                    session = stompClient.connectAsync(url, handler).get()
                }
            } catch (ex: Exception) {
                logger.warn("Failed to connect to WebSocket: ${ex.message}")
            }
        }, 0, 10, TimeUnit.SECONDS)
    }


    fun sendMessage(
        destination: String,
        payload: Any,
        subscribeDestination: String? = null,
        handler: StompFrameHandler? = null,
    ) {
        try {
            session?.also {
                subscribeDestination?.also { subscription ->
                    it.subscribe("/$s3Topic$subscription", handler!!)
                }
                it.send("/$s3App$destination", payload)
            } ?: throw ServiceUnavailableException()
        } catch (ex: Exception) {
            logger.error("Failed to send message: ${ex.message}")
            throw ServiceUnavailableException("Code exec service is unavailable")
        }
    }
}
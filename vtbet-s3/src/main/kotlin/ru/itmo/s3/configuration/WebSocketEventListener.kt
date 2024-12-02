package ru.itmo.s3.configuration

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent


@Component
class WebSocketEventListener {
    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectEvent) {
        logger.info("A new WebSocket connection is being established. Session ID: {}", event.message.headers.id)
    }

    @EventListener
    fun handleWebSocketConnectedListener(event: SessionConnectedEvent) {
        logger.info("A new WebSocket session has been established. Session ID: {}", event.message.headers["simpSessionId"])
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        logger.info("A WebSocket session has been disconnected. Session ID: {}", event.sessionId)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(WebSocketEventListener::class.java)
    }
}
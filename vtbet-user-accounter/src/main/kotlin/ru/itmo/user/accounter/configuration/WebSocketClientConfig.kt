package ru.itmo.user.accounter.configuration

import jakarta.websocket.ContainerProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.CompositeMessageConverter
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.converter.StringMessageConverter
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient


@Configuration
class WebSocketClientConfig {
    @Bean
    fun stompClient(): WebSocketStompClient {
        val container = ContainerProvider.getWebSocketContainer()
        container.defaultMaxBinaryMessageBufferSize = 20 * 1024 * 1024
        container.defaultMaxTextMessageBufferSize = 20 * 1024 * 1024
        val transport = StandardWebSocketClient(container)
        val stompClient = WebSocketStompClient(transport)

        val converters: MutableList<MessageConverter> = ArrayList()
        converters.add(StringMessageConverter())
        converters.add(MappingJackson2MessageConverter())

        stompClient.messageConverter = CompositeMessageConverter(converters)
        stompClient.inboundMessageSizeLimit = 20 * 1024 * 1024
        return stompClient
    }
}
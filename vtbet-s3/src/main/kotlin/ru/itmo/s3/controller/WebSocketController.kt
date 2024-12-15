package ru.itmo.s3.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import ru.itmo.s3.service.MinioService


@Controller
class WebSocketController {
    @Autowired
    private lateinit var minioService: MinioService

    @MessageMapping("/file.get")
    @SendTo("/topic/file/url")
    fun getUrl(@Payload fileName: String): String = minioService.getUrl(fileName)

    @MessageMapping("/file.download")
    @SendTo("/topic/file/file")
    fun downloadFile(@Payload fileName: String): String = minioService.getFile(fileName)

    @MessageMapping("/file.upload")
    @SendTo("/topic/file/url")
    fun uploadFile(@Payload payload: String) = minioService.uploadFile(payload)

    @MessageMapping("/file.delete")
    @SendTo("/topic/file/deleted")
    fun deleteFile(@Payload fileName: String): String = minioService.deleteFile(fileName)
}

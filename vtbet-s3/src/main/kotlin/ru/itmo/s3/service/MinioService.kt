package ru.itmo.s3.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.itmo.common.dto.FileDto
import ru.itmo.s3.datasource.MinioAdapter
import java.util.*

@Service
class MinioService(
    private val minioAdapter: MinioAdapter,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(MinioService::class.java)

    fun getUrl(fileName: String): String {
        try {
            return minioAdapter.getPresignedObjectUrl(fileName)
        } catch (e: Exception) {
            logger.error(e.message)
            return "error.code"
        }
    }

    fun getFile(fileName: String): String {
        try {
            val stream = minioAdapter.getObject(fileName)
            val fileDto = FileDto(
                fileName = fileName,
                fileBase64 = Base64.getEncoder().encodeToString(stream.readAllBytes())
            )
            val payload = objectMapper.writeValueAsString(fileDto)
            return payload
        } catch (e: Exception) {
            logger.error(e.message)
            return "error.code"
        }
    }

    fun uploadFile(fileInput: String): String {
        try {
            val fileDto = objectMapper.readValue(fileInput, FileDto::class.java)
            val fileBytes = fileDto.toBytes()
            minioAdapter.putObject(fileBytes.inputStream(), fileDto.fileName)
            return minioAdapter.getPresignedObjectUrl(fileDto.fileName)
        } catch (ex: Exception) {
            logger.error(ex.message)
            return "error.code"
        }
    }

    fun deleteFile(fileName: String): String {
        try {
            minioAdapter.deleteImage(fileName)
            return "success"
        } catch (ex: Exception) {
            logger.error(ex.message)
            return "error.code"
        }
    }
}

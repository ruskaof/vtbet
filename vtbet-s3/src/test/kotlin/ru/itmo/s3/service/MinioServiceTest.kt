package ru.itmo.s3.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import ru.itmo.common.dto.FileDto
import ru.itmo.s3.datasource.MinioAdapter
import java.io.ByteArrayInputStream
import java.util.*
import kotlin.test.assertEquals

class MinioServiceTest {

    private val minioAdapter = mock(MinioAdapter::class.java)
    private val objectMapper = mock(ObjectMapper::class.java)
    private val minioService = MinioService(minioAdapter, objectMapper)

    @Test
    fun `getUrl returns URL successfully`() {
        val fileName = "test-file.txt"
        val expectedUrl = "http://example.com/test-file.txt"
        `when`(minioAdapter.getPresignedObjectUrl(fileName)).thenReturn(expectedUrl)

        val result = minioService.getUrl(fileName)

        assertEquals(expectedUrl, result)
        verify(minioAdapter).getPresignedObjectUrl(fileName)
    }

    @Test
    fun `getUrl returns error code when exception occurs`() {
        val fileName = "test-file.txt"
        `when`(minioAdapter.getPresignedObjectUrl(fileName))
            .thenThrow(RuntimeException("Failed to generate URL"))

        val result = minioService.getUrl(fileName)

        assertEquals("error.code", result)
        verify(minioAdapter).getPresignedObjectUrl(fileName)
    }

    @Test
    fun `getFile returns encoded file successfully`() {
        val fileName = "test-file.txt"
        val fileContent = "Sample file content".toByteArray()
        val inputStream = ByteArrayInputStream(fileContent)
        val fileDto = FileDto(
            fileName = fileName,
            fileBase64 = Base64.getEncoder().encodeToString(fileContent)
        )
        val expectedPayload = """{"fileName":"$fileName","fileBase64":"${fileDto.fileBase64}"}"""

        `when`(minioAdapter.getObject(fileName)).thenReturn(inputStream)
        `when`(objectMapper.writeValueAsString(fileDto)).thenReturn(expectedPayload)

        val result = minioService.getFile(fileName)

        assertEquals(expectedPayload, result)
        verify(minioAdapter).getObject(fileName)
        verify(objectMapper).writeValueAsString(fileDto)
    }

    @Test
    fun `getFile returns error code when exception occurs`() {
        val fileName = "test-file.txt"
        `when`(minioAdapter.getObject(fileName))
            .thenThrow(RuntimeException("File not found"))

        val result = minioService.getFile(fileName)

        assertEquals("error.code", result)
        verify(minioAdapter).getObject(fileName)
        verifyNoInteractions(objectMapper)
    }

    @Test
    fun `uploadFile uploads file successfully and returns URL`() {
        val fileInput = """{"fileName":"test-file.txt","fileBase64":"U2FtcGxlIGZpbGUgY29udGVudA=="}""" // Base64-encoded "Sample file content"
        val fileDto = FileDto("test-file.txt", "U2FtcGxlIGZpbGUgY29udGVudA==")
        val expectedUrl = "http://example.com/test-file.txt"

        `when`(objectMapper.readValue(fileInput, FileDto::class.java)).thenReturn(fileDto)
        `when`(minioAdapter.getPresignedObjectUrl(fileDto.fileName)).thenReturn(expectedUrl)

        val result = minioService.uploadFile(fileInput)

        assertEquals(expectedUrl, result)
        verify(objectMapper).readValue(fileInput, FileDto::class.java)
        verify(minioAdapter).getPresignedObjectUrl(fileDto.fileName)
    }

    @Test
    fun `uploadFile returns error code when exception occurs`() {
        val fileInput = """{"fileName":"test-file.txt","fileBase64":"U2FtcGxlIGZpbGUgY29udGVudA=="}"""
        `when`(objectMapper.readValue(fileInput, FileDto::class.java))
            .thenThrow(RuntimeException("Invalid input"))

        val result = minioService.uploadFile(fileInput)

        assertEquals("error.code", result)
        verify(objectMapper).readValue(fileInput, FileDto::class.java)
        verifyNoInteractions(minioAdapter)
    }

    @Test
    fun `deleteFile deletes file successfully and returns success`() {
        val fileName = "test-file.txt"

        doNothing().`when`(minioAdapter).deleteImage(fileName)

        val result = minioService.deleteFile(fileName)

        assertEquals("success", result)
        verify(minioAdapter).deleteImage(fileName)
    }

    @Test
    fun `deleteFile returns error code when exception occurs`() {
        val fileName = "test-file.txt"
        `when`(minioAdapter.deleteImage(fileName)).thenThrow(RuntimeException("File not found"))

        val result = minioService.deleteFile(fileName)

        assertEquals("error.code", result)
        verify(minioAdapter).deleteImage(fileName)
    }
}
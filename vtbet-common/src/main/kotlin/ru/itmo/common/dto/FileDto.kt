package ru.itmo.common.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class FileDto(
    @JsonProperty("fileName") val fileName: String,
    @JsonProperty("fileBase64") val fileBase64: String // Base64-encoded file content
) {
    fun toBytes(): ByteArray {
        return Base64.getDecoder().decode(fileBase64)
    }
}
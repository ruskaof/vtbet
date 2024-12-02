package ru.itmo.s3.configuration

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails

interface MinioConnectionDetails : ConnectionDetails {
    val url: String?

    val accessKey: String?

    val secretKey: String?

    val bucket: String?
}
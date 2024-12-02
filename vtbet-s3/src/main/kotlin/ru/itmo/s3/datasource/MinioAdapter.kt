package ru.itmo.s3.datasource

import io.minio.*
import io.minio.http.Method
import org.springframework.stereotype.Service
import ru.itmo.s3.configuration.MinioConnectionDetails
import java.io.InputStream
import java.util.concurrent.TimeUnit


@Service
class MinioAdapter(
    private val minioClient: MinioClient,
    private val minioConnectionDetails: MinioConnectionDetails
) {

    fun getPresignedObjectUrl(name: String): String {
        val args = GetPresignedObjectUrlArgs.builder()
            .bucket(minioConnectionDetails.bucket)
            .`object`(name)
            .expiry(1, TimeUnit.DAYS)
            .method(Method.GET)
            .build()
        return minioClient.getPresignedObjectUrl(args)
    }

    fun getObject(name: String): InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(minioConnectionDetails.bucket)
                .`object`(name)
                .build()
        )
    }

    fun deleteImage(fileName: String) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(minioConnectionDetails.bucket)
                .`object`(fileName)
                .build()
        )
    }

    fun putObject(file: InputStream, fileName: String) {
        minioClient.putObject(
            PutObjectArgs.builder().bucket(minioConnectionDetails.bucket).`object`(fileName).stream(
                file, -1, 10485760
            )
                .contentType("image/jpg")
                .build()
        )
    }
}
package ru.itmo.vtbet.integration

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager


abstract class BaseIntegrationTest {
    companion object {

        @JvmStatic
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))

        init {
            postgres.start()
        }

        @JvmStatic
        val connection: Connection = DriverManager.getConnection(
            postgres.jdbcUrl,
            postgres.username,
            postgres.password
        )

        init {
            runMigrations()
        }

        private fun runMigrations() {
            val parentDirectory: Path = File("..").toPath()
            val changeLogPath: Path = parentDirectory.resolve("/migrations/master.xml")

            val database: liquibase.database.Database? =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(connection))
            val liquibase = Liquibase(changeLogPath.toString(), DirectoryResourceAccessor(parentDirectory), database)
            liquibase.update(Contexts(), LabelExpression())
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }
}
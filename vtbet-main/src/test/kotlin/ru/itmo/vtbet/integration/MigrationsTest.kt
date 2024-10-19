package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class MigrationsTest : BaseIntegrationTest() {

    @Test
    fun `migrations create tables successfully`() {
        assertAll(
            { assertTrue(tableExists("users")) },
            { assertTrue(tableExists("users_accounts")) },
            { assertTrue(tableExists("sports")) },
            { assertTrue(tableExists("matches")) },
            { assertTrue(tableExists("bets_groups")) },
            { assertTrue(tableExists("available_bets")) },
            { assertTrue(tableExists("bets")) },
        )
    }

    private fun tableExists(tableName: String): Boolean {
        connection.metaData.getTables(null, null, tableName, null).use { resultSet ->
            return resultSet.next()
        }
    }
}
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
            { assertTrue(tableExists("user_account")) },
            { assertTrue(tableExists("sport")) },
            { assertTrue(tableExists("matches")) },
            { assertTrue(tableExists("bet_group")) },
            { assertTrue(tableExists("type_of_bet")) },
            { assertTrue(tableExists("type_of_bet_match")) },
            { assertTrue(tableExists("bets")) },
        )
    }

    private fun tableExists(tableName: String): Boolean {
        connection.metaData.getTables(null, null, tableName, null).use { resultSet ->
            return resultSet.next()
        }
    }
}
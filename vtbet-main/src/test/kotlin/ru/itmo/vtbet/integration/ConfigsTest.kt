package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.autoconfigure.transaction.TransactionProperties
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [
        HibernateProperties::class,
        JpaProperties::class,
        TransactionProperties::class,
        ServerProperties::class,
    ]
)
@EnableConfigurationProperties(
    HibernateProperties::class,
    JpaProperties::class,
    TransactionProperties::class,
    ServerProperties::class
)
class ConfigsTest {

    @Autowired
    private lateinit var hibernateProperties: HibernateProperties

    @Autowired
    private lateinit var jpaProperties: JpaProperties

    @Autowired
    private lateinit var transactionProperties: TransactionProperties

    @Autowired
    private lateinit var serverProperties: ServerProperties

    @Test
    fun `test set configs`() {
        assertEquals("none", hibernateProperties.ddlAuto)
        assertTrue(jpaProperties.properties["hibernate.show_sql"]!!.toBoolean())
        assertEquals(Duration.ofSeconds(5), transactionProperties.defaultTimeout)
        assertEquals(8080, serverProperties.port)
    }
}
package ru.itmo.vtbet.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties("auth.keys")
class SecurityProperties (
    val publicKey: RSAPublicKey,
)

package ru.itmo.auth.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@ConfigurationProperties("auth.keys")
class SecurityProperties (
    val publicKey: RSAPublicKey,
    val privateKey: RSAPrivateKey
)
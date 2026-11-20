package com.tomazbr9.autonotaadmin.licensing

import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

object EmbeddedLicenseKeyLoader {

    fun carregar(): PrivateKey {

        // Chave privada incorporada ao aplicativo.
        val pkcs8 = Base64.getDecoder().decode(
            EmbeddedPrivateKey.PKCS8_BASE64
        )

        try {
            val factory = KeyFactory.getInstance(
                "Ed25519"
            )

            val spec = PKCS8EncodedKeySpec(pkcs8)

            return factory.generatePrivate(spec)

        } finally {

            // Limpa a copia temporaria.
            // Nao elimina a chave embutida no APK.
            pkcs8.fill(0)
        }
    }
}

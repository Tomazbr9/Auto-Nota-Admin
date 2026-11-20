package com.tomazbr9.autonotaadmin.licensing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.time.LocalDate
import java.util.Properties

class LicenseFileCodecTest {

    @Test
    fun deveGerarLicencaCompativelComProperties() {

        val chaves = KeyPairGenerator
            .getInstance("Ed25519")
            .generateKeyPair()

        val license = License(
            schemaVersion = 1,
            licenseId = "AN-TEST-0003",
            productId = License.PRODUCT_ID,
            customerName = "Posto São Pedro",
            installationId =
                "AN1-11111111-22222222-33333333-44444444",
            issuedAt = LocalDate.of(2026, 11, 19),
            type = LicenseType.PERPETUAL
        )

        val assinada = LicenseSigner.assinar(
            license,
            chaves.private
        )

        val bytes = LicenseFileCodec.codificar(
            assinada
        )

        val properties = Properties()

        InputStreamReader(
            ByteArrayInputStream(bytes),
            StandardCharsets.UTF_8
        ).use { reader ->
            properties.load(reader)
        }

        val recuperada = SignedLicense(
            payloadBase64 =
                properties.getProperty("payloadBase64"),
            signatureBase64 =
                properties.getProperty("signatureBase64"),
            keyId =
                properties.getProperty("keyId")
        )

        assertEquals(assinada, recuperada)

        assertTrue(
            LicenseSigner.verificar(
                recuperada,
                chaves.public
            )
        )
    }
}

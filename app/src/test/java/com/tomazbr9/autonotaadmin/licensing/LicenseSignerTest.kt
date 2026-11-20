package com.tomazbr9.autonotaadmin.licensing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

import java.security.KeyPairGenerator
import java.time.LocalDate
import java.util.Base64

class LicenseSignerTest {

    private fun criarLicenca() = License(
        schemaVersion = 1,
        licenseId = "AN-TEST-0002",
        productId = License.PRODUCT_ID,
        customerName = "Posto Exemplo",
        installationId =
            "AN1-11111111-22222222-33333333-44444444",
        issuedAt = LocalDate.of(2026, 11, 19),
        type = LicenseType.PERPETUAL
    )

    @Test
    fun deveAssinarEVerificarLicenca() {

        val chaves = KeyPairGenerator
            .getInstance("Ed25519")
            .generateKeyPair()

        val assinada = LicenseSigner.assinar(
            criarLicenca(),
            chaves.private
        )

        assertTrue(
            LicenseSigner.verificar(
                assinada,
                chaves.public
            )
        )
    }

    @Test
    fun deveRejeitarConteudoAlterado() {

        val chaves = KeyPairGenerator
            .getInstance("Ed25519")
            .generateKeyPair()

        val assinada = LicenseSigner.assinar(
            criarLicenca(),
            chaves.private
        )

        val alterado = Base64.getDecoder()
            .decode(assinada.payloadBase64)

        alterado[10] = (
                alterado[10].toInt() xor 1
                ).toByte()

        val adulterada = assinada.copy(
            payloadBase64 = Base64.getEncoder()
                .encodeToString(alterado)
        )

        assertFalse(
            LicenseSigner.verificar(
                adulterada,
                chaves.public
            )
        )
    }

    @Test
    fun deveRejeitarOutraChavePublica() {

        val gerador = KeyPairGenerator
            .getInstance("Ed25519")

        val chavesOriginais = gerador.generateKeyPair()
        val outrasChaves = gerador.generateKeyPair()

        val assinada = LicenseSigner.assinar(
            criarLicenca(),
            chavesOriginais.private
        )

        assertFalse(
            LicenseSigner.verificar(
                assinada,
                outrasChaves.public
            )
        )
    }

    @Test
    fun deveRejeitarKeyIdDesconhecido() {

        val chaves = KeyPairGenerator
            .getInstance("Ed25519")
            .generateKeyPair()

        val assinada = LicenseSigner.assinar(
            criarLicenca(),
            chaves.private
        )

        val modificada = assinada.copy(
            keyId = "OUTRA_CHAVE"
        )

        assertFalse(
            LicenseSigner.verificar(
                modificada,
                chaves.public
            )
        )
    }
}

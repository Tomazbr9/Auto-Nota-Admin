package com.tomazbr9.autonotaadmin.licensing

import java.time.LocalDate
import java.util.Locale
import java.util.UUID

data class IssuedLicense(
    val nomeArquivo: String,
    val conteudo: ByteArray
)

object LicenseIssuer {

    private val INSTALLATION_ID_PATTERN = Regex(
        "^AN1-[0-9A-F]{8}(-[0-9A-F]{8}){3}$"
    )

    fun emitir(
        nomeCliente: String,
        identificacaoComputador: String
    ): IssuedLicense {

        val cliente = nomeCliente.trim()

        val instalacao = identificacaoComputador
            .trim()
            .uppercase(Locale.ROOT)

        require(cliente.isNotBlank()) {
            "Informe o nome do cliente."
        }

        require(
            INSTALLATION_ID_PATTERN.matches(instalacao)
        ) {
            "Identificacao do computador invalida."
        }

        val licenseId = "AN-" +
                UUID.randomUUID()
                    .toString()
                    .uppercase(Locale.ROOT)

        val license = License(
            schemaVersion = License.CURRENT_SCHEMA_VERSION,
            licenseId = licenseId,
            productId = License.PRODUCT_ID,
            customerName = cliente,
            installationId = instalacao,
            issuedAt = LocalDate.now(),
            type = LicenseType.PERPETUAL
        )

        val privateKey =
            EmbeddedLicenseKeyLoader.carregar()

        val signedLicense = LicenseSigner.assinar(
            license,
            privateKey
        )

        val arquivo = LicenseFileCodec.codificar(
            signedLicense
        )

        return IssuedLicense(
            nomeArquivo = "AutoNota-$licenseId.lic",
            conteudo = arquivo
        )
    }
}

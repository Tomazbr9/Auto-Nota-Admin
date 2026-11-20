package com.tomazbr9.autonotaadmin.licensing

import org.junit.Assert.assertEquals
import org.junit.Test

import java.io.IOException
import java.time.LocalDate
import java.util.Base64

class LicensePayloadCodecTest {

    private fun criarLicencaTeste() = License(
        schemaVersion = 1,
        licenseId = "AN-TEST-0001",
        productId = "AUTO_NOTA",
        customerName = "Posto São Pedro",
        installationId =
            "AN1-7F29C4B1-83D209AF-62E4B150-A5C781D3",
        issuedAt = LocalDate.of(2026, 11, 19),
        type = LicenseType.PERPETUAL
    )

    @Test
    fun deveGerarMesmoPayloadDoJava() {

        val payload = LicensePayloadCodec.codificar(
            criarLicencaTeste()
        )

        // Referencia gerada pela implementacao Java
        // existente no Auto-Nota Windows.
        val esperado =
            "QU5MMQAAAAEADEFOLVRFU1QtMDAwMQAJQVVUT19OT1RB" +
                    "ABBQb3N0byBTw6NvIFBlZHJvACdBTjEtN0YyOUM0QjEt" +
                    "ODNEMjA5QUYtNjJFNEIxNTAtQTVDNzgxRDMAAAAAAABR" +
                    "KAAJUEVSUEVUVUFM"

        val resultado = Base64.getEncoder()
            .encodeToString(payload)

        assertEquals(esperado, resultado)
    }

    @Test
    fun deveRecuperarOsDadosOriginais() {

        val original = criarLicencaTeste()

        val payload = LicensePayloadCodec.codificar(
            original
        )

        val recuperada = LicensePayloadCodec.decodificar(
            payload
        )

        assertEquals(original, recuperada)
    }

    @Test(expected = IOException::class)
    fun deveRejeitarPayloadInvalido() {

        LicensePayloadCodec.decodificar(
            byteArrayOf(0, 1, 2)
        )
    }
}

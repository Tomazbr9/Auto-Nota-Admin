package com.tomazbr9.autonotaadmin.licensing

import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.Properties

object LicenseFileCodec {

    private const val MAX_FILE_BYTES = 32 * 1024

    fun codificar(license: SignedLicense): ByteArray {

        require(license.keyId == LicenseSigner.KEY_ID) {
            "Identificador de chave invalido."
        }

        val properties = Properties().apply {
            setProperty(
                "payloadBase64",
                license.payloadBase64
            )
            setProperty(
                "signatureBase64",
                license.signatureBase64
            )
            setProperty(
                "keyId",
                license.keyId
            )
        }

        val output = ByteArrayOutputStream()

        OutputStreamWriter(
            output,
            StandardCharsets.UTF_8
        ).use { writer ->

            properties.store(
                writer,
                "Auto-Nota License v1"
            )
        }

        val bytes = output.toByteArray()

        require(bytes.size <= MAX_FILE_BYTES) {
            "Arquivo de licenca muito grande."
        }

        return bytes
    }
}

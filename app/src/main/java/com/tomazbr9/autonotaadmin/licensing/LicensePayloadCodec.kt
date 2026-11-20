package com.tomazbr9.autonotaadmin.licensing

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.time.LocalDate

object LicensePayloadCodec {

    // Mesmo identificador usado no Auto-Nota Windows.
    private const val MAGIC = 0x414E4C31 // ANL1

    private const val MAX_BYTES = 16 * 1024

    // Converte a licenca em bytes para assinatura.
    fun codificar(license: License): ByteArray {

        val buffer = ByteArrayOutputStream()

        DataOutputStream(buffer).use { out ->

            out.writeInt(MAGIC)
            out.writeInt(license.schemaVersion)

            out.writeUTF(license.licenseId)
            out.writeUTF(license.productId)
            out.writeUTF(license.customerName)
            out.writeUTF(license.installationId)

            out.writeLong(
                license.issuedAt.toEpochDay()
            )

            out.writeUTF(license.type.name)
        }

        val payload = buffer.toByteArray()

        require(payload.size <= MAX_BYTES) {
            "Licenca muito grande."
        }

        return payload
    }

    // Reconstrói uma licença a partir dos bytes.
    // Útil para testes e conferência do formato.
    @Throws(IOException::class)
    fun decodificar(payload: ByteArray): License {

        if (payload.isEmpty()
            || payload.size > MAX_BYTES
        ) {
            throw IOException(
                "Conteudo da licenca ausente ou excessivo."
            )
        }

        try {
            DataInputStream(
                ByteArrayInputStream(payload)
            ).use { input ->

                if (input.readInt() != MAGIC) {
                    throw IOException(
                        "Formato de licenca nao reconhecido."
                    )
                }

                val versao = input.readInt()

                val id = input.readUTF()
                val produto = input.readUTF()
                val cliente = input.readUTF()
                val instalacao = input.readUTF()

                val emissao = LocalDate.ofEpochDay(
                    input.readLong()
                )

                val tipo = LicenseType.valueOf(
                    input.readUTF()
                )

                if (input.available() != 0) {
                    throw IOException(
                        "Dados extras no conteudo da licenca."
                    )
                }

                return License(
                    schemaVersion = versao,
                    licenseId = id,
                    productId = produto,
                    customerName = cliente,
                    installationId = instalacao,
                    issuedAt = emissao,
                    type = tipo
                )
            }

        } catch (e: IllegalArgumentException) {
            throw IOException(
                "Campos invalidos na licenca.",
                e
            )
        }
    }
}

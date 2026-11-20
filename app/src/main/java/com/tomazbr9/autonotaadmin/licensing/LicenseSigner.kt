package com.tomazbr9.autonotaadmin.licensing

import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.util.Base64

object LicenseSigner {

    const val KEY_ID = "AUTO_NOTA_KEY_01"

    fun assinar(
        license: License,
        privateKey: PrivateKey
    ): SignedLicense {

        // 1. Serializar os dados exatamente como
        // o Auto-Nota Windows faz.
        val payload = LicensePayloadCodec.codificar(
            license
        )

        // 2. Inicializar assinatura Ed25519.
        val signer = Signature.getInstance("Ed25519")

        signer.initSign(privateKey)
        signer.update(payload)

        // 3. Gerar a assinatura.
        val assinatura = signer.sign()

        // 4. Codificar o conteúdo e a assinatura
        // para o formato utilizado no arquivo .lic.
        return SignedLicense(
            payloadBase64 = Base64.getEncoder()
                .encodeToString(payload),

            signatureBase64 = Base64.getEncoder()
                .encodeToString(assinatura),

            keyId = KEY_ID
        )
    }

    fun verificar(
        signedLicense: SignedLicense,
        publicKey: PublicKey
    ): Boolean {

        if (signedLicense.keyId != KEY_ID) {
            return false
        }

        return try {
            val payload = Base64.getDecoder().decode(
                signedLicense.payloadBase64
            )

            val assinatura = Base64.getDecoder().decode(
                signedLicense.signatureBase64
            )

            if (assinatura.size != 64) {
                return false
            }

            // Primeiro verifica a assinatura.
            val verifier = Signature.getInstance(
                "Ed25519"
            )

            verifier.initVerify(publicKey)
            verifier.update(payload)

            if (!verifier.verify(assinatura)) {
                return false
            }

            // Depois verifica o formato e as
            // regras do conteúdo autenticado.
            LicensePayloadCodec.decodificar(payload)

            true

        } catch (e: Exception) {
            false
        }
    }
}

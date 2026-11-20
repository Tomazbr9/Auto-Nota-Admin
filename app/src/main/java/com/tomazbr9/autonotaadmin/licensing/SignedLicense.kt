package com.tomazbr9.autonotaadmin.licensing

data class SignedLicense(
    val payloadBase64: String,
    val signatureBase64: String,
    val keyId: String
) {
    init {
        require(payloadBase64.isNotBlank()) {
            "Conteudo da licenca obrigatorio."
        }

        require(signatureBase64.isNotBlank()) {
            "Assinatura digital obrigatoria."
        }

        require(keyId.isNotBlank()) {
            "Identificador da chave obrigatorio."
        }
    }
}

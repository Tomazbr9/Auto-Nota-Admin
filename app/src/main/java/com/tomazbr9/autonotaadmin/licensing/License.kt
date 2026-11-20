
package com.tomazbr9.autonotaadmin.licensing

import java.time.LocalDate

data class License(
    val schemaVersion: Int,
    val licenseId: String,
    val productId: String,
    val customerName: String,
    val installationId: String,
    val issuedAt: LocalDate,
    val type: LicenseType
) {

    init {
        require(schemaVersion == CURRENT_SCHEMA_VERSION) {
            "Versao de licenca nao suportada."
        }

        require(licenseId.isNotBlank()) {
            "Identificador da licenca obrigatorio."
        }

        require(productId == PRODUCT_ID) {
            "Produto da licenca invalido."
        }

        require(customerName.isNotBlank()) {
            "Nome do cliente obrigatorio."
        }

        require(installationId.isNotBlank()) {
            "Identificador da instalacao obrigatorio."
        }

        require(type == LicenseType.PERPETUAL) {
            "Tipo de licenca nao suportado."
        }
    }

    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
        const val PRODUCT_ID = "AUTO_NOTA"
    }
}

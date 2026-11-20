package com.tomazbr9.autonotaadmin

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tomazbr9.autonotaadmin.licensing.IssuedLicense
import com.tomazbr9.autonotaadmin.licensing.LicenseIssuer
import com.tomazbr9.autonotaadmin.ui.theme.AutoNotaAdminTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AutoNotaAdminTheme {
                TelaEmissao()
            }
        }
    }
}

@Composable
private fun TelaEmissao() {

    val context = LocalContext.current

    var cliente by rememberSaveable {
        mutableStateOf("")
    }

    var identificacao by rememberSaveable {
        mutableStateOf("")
    }

    var mensagem by rememberSaveable {
        mutableStateOf("")
    }

    var pendente by remember {
        mutableStateOf<IssuedLicense?>(null)
    }

    var arquivoSalvo by remember {
        mutableStateOf<Uri?>(null)
    }

    // Abre o seletor de arquivos do Android.
    val salvarArquivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/octet-stream"
        )
    ) { uri ->

        val emissao = pendente
        pendente = null

        if (uri == null) {
            mensagem = "Salvamento cancelado."

        } else if (emissao == null) {
            mensagem = "Nenhuma licenca pendente."

        } else {
            try {
                val output = context.contentResolver
                    .openOutputStream(uri, "w")
                    ?: error("Nao foi possivel abrir o arquivo.")

                output.use {
                    it.write(emissao.conteudo)
                }

                arquivoSalvo = uri

                mensagem =
                    "Licenca salva com sucesso!"

            } catch (e: Exception) {
                arquivoSalvo = null

                mensagem =
                    "Erro ao salvar: " +
                            (e.message ?: "erro desconhecido")
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Auto-Nota Admin",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Emissao de licencas offline",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = cliente,
                onValueChange = {
                    cliente = it
                },
                label = {
                    Text("Nome do cliente")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = identificacao,
                onValueChange = {
                    identificacao = it
                },
                label = {
                    Text("Identificacao do computador")
                },
                supportingText = {
                    Text(
                        "Exemplo: AN1-XXXXXXXX-XXXXXXXX-XXXXXXXX-XXXXXXXX"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Licenca: Vitalicia",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = {
                    try {
                        val emissao = LicenseIssuer.emitir(
                            nomeCliente = cliente,
                            identificacaoComputador = identificacao
                        )

                        pendente = emissao
                        arquivoSalvo = null
                        mensagem = ""

                        salvarArquivo.launch(
                            emissao.nomeArquivo
                        )

                    } catch (e: Exception) {
                        pendente = null

                        mensagem =
                            "Nao foi possivel gerar: " +
                                    (e.message ?: "erro desconhecido")
                    }
                },

                enabled = cliente.isNotBlank() &&
                        identificacao.isNotBlank(),

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gerar e salvar licenca")
            }

            if (mensagem.isNotBlank()) {
                Text(
                    text = mensagem,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            arquivoSalvo?.let { uri ->

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_SEND
                        ).apply {

                            type = "application/octet-stream"

                            putExtra(
                                Intent.EXTRA_STREAM,
                                uri
                            )

                            clipData = ClipData.newUri(
                                context.contentResolver,
                                "Licenca Auto-Nota",
                                uri
                            )

                            addFlags(
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        }

                        context.startActivity(
                            Intent.createChooser(
                                intent,
                                "Compartilhar licenca"
                            )
                        )
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartilhar licenca")
                }
            }
        }
    }
}

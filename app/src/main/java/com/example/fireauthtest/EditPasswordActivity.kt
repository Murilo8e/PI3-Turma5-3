package com.example.fireauthtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class EditPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            editPasswordScreen()
        }
    }
}
//TODO: Tela igual de cadastrar senha nova, porém precisa estar com tudo preenchido com as informações da senha selecionada.

@Preview(showBackground = true)
@Composable
fun editPasswordScreen() {
    val context = LocalContext.current
    var nome by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.LightGray,
                        Color(0xFF37395C),

                        )
                )
            )
    ) {
        Text("Cadastrar Senha",
            fontSize = 30.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 15.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Adicionar",
                        tint = Color(0xFF37395C),
                        modifier = Modifier
                            .size(100.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(30.dp)

            ){
                //Campo do Nome
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Nome",
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(4.dp) // Ajusta o padding para envolver o texto com o fundo
                    )
                }
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Digite o nome do Serviço", color = Color.LightGray) }, // Cor do label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White) // Cor do texto digitado
                )

                Spacer(modifier = Modifier.height(16.dp))
                //Campo do Login
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Login (Opcional)",
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(4.dp) // Ajusta o padding para envolver o texto com o fundo
                    )
                }
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Digite o login", color = Color.LightGray) }, // Cor do label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White) // Cor do texto digitado
                )

                Spacer(modifier = Modifier.height(16.dp))
                //Campo da Senha
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Senha",
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(4.dp) // Ajusta o padding para envolver o texto com o fundo
                    )
                }
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    label = { Text("Digite a Senha", color = Color.LightGray) }, // Cor do label
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White) // Cor do texto digitado
                )

                Spacer(modifier = Modifier.height(16.dp))

                //TODO: Colocar o dropdown aqui


                //Campo da descrição
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Descrição (Opcional)",
                        textAlign = TextAlign.Start,
                        color = Color.LightGray,
                        modifier = Modifier
                            .background(Color.Black)
                            .padding(4.dp) // Ajusta o padding para envolver o texto com o fundo
                    )
                }
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Digite a Descrição", color = Color.LightGray) }, // Cor do label
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White) // Cor do texto digitado
                )

                Spacer(modifier = Modifier.height(32.dp))
                //Campo dos botões
                Row(modifier = Modifier
                    .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Um item vai para o start, outro para o end
                ){
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(context, HomePageActivity::class.java)
                            context.startActivity(intent) },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.DarkGray
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Cancelar", color = Color.LightGray)
                    }
                    Button(
                        onClick = { /* TODO: SALVAR AS INFORMAÇÕES */ },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37395C),
                            contentColor = Color.LightGray// Cor do texto
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Salvar")
                    }
                }
            }
            }
        }
    }



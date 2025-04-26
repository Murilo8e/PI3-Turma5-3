package com.example.fireauthtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            signInScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun signInScreen() {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val context = LocalContext.current

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
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(60.dp)
                .align(Alignment.Center)
        ){
            Image(
                contentDescription = "Logo do App",
                painter = painterResource(id = R.drawable.superid_icon),
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            // Campo de Email
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "E-mail",
                    textAlign = TextAlign.Start,
                    color = Color.LightGray,
                    modifier = Modifier
                        .background(Color.Black)
                        .padding(4.dp) // Ajusta o padding para envolver o texto com o fundo
                )
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Digite seu E-mail") },
                placeholder = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Senha
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
                label = { Text("Digite sua senha") },
                placeholder = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Entrar
            Button(
                onClick = { /* TODO: Fazer login */ },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF37395C), // Azul forte
                    contentColor = Color.LightGray          // Cor do texto
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Criar Conta
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent) },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Criar Conta")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Esqueci a senha
            OutlinedButton(
                onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent) },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Esqueci a senha")
            }
        }
    }
}
package com.example.fireauthtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            welcomeScreen()
        }
    }
}

@Preview
@Composable
fun welcomeScreen(modifier: Modifier = Modifier){

    /* Usando o sistema de navegação legado
       Através de intents.

       A variável context recebe o objeto do contexto atual.
       O LocalContext serve APENAS em Composables.

       O Context agora informa o ambiente composable que se está.
       Ele servirá para construir o Intent futuramente
       (estou aqui (context) e quero ir para tal activity (classe da Activity))
       
    */

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Testando o Firebase Auth :)",
            style = TextStyle(
                fontSize = 25.sp
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF374CCE)
            )
        ) {
            Text(
                text = "Faça seu Cadastro"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val intent = Intent(context, SignInActivity::class.java)
                context.startActivity(intent)
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4ACC4E)
            )
        ) {
            Text(
                text = "Entrar"
            )
        }


    }
}




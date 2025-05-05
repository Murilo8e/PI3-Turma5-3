package com.example.fireauthtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val auth = FirebaseAuth.getInstance()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            signInScreen(
                auth = auth
            )
        }
    }
}

class LoginTextFieldController(
    val email : MutableState<String>,
    val senha : MutableState<String>
){

    var emailValue : String
        get() = email.value
        set(value) { email.value = value }

    var senhaValue : String
        get() = senha.value
        set(value) { senha.value = value }

    val emailIsValid : Boolean
        get() = android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()

    val passwordIsValid : Boolean
        get() = senhaValue.isNotEmpty()

    val validFields
        get() = emailIsValid && passwordIsValid

}


@Composable
fun signInScreen(
    auth : FirebaseAuth
) {
    var email = remember { mutableStateOf("") }
    var senha = remember { mutableStateOf("") }
    var loginError = remember { mutableStateOf(false) }
    val FieldController = remember { LoginTextFieldController(email, senha) }

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

            if(loginError.value){
                Text(
                    text = "Usuário e/ou Senha inválido(s)!",
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(5.dp))
            }

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
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Digite seu E-mail") },
                placeholder = { Text("E-mail") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = loginError.value
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
                value = senha.value,
                onValueChange = { senha.value = it },
                label = { Text("Digite sua senha") },
                placeholder = { Text("Senha") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = loginError.value
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Entrar
            LoginButton(
                auth = auth,
                email = email,
                senha = senha,
                loginError = loginError,
                fieldController = FieldController
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Esqueci a senha
            OutlinedButton(
                onClick = {

                     },
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

@Composable
fun LoginButton(
    auth : FirebaseAuth,
    email : MutableState<String>,
    senha : MutableState<String>,
    loginError : MutableState<Boolean>,
    fieldController : LoginTextFieldController
){

    val context = LocalContext.current

    Button(
        onClick = {
            auth.signInWithEmailAndPassword(email.value, senha.value)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("SignInActivity", "User logged in successfully")

                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)

                    }
                    else{
                        Log.w("SignInActivity", "User log in failed. Exception = ${task.exception?.message}")

                        loginError.value = true
                    }
                }
        },
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF37395C), // Azul forte
            contentColor = Color.LightGray          // Cor do texto
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = fieldController.validFields
    ) {
        Text("Entrar")
    }


}
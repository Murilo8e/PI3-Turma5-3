package com.example.fireauthtest

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

/*

TO DO
    - Colocar as strings no formato XML e busca-las ao inves de inserir direto no codigo.
    - Sanitizacao das entradas de senha
    - Melhorar o posicionamento do card de requisitos de senha
    - Enviar cadastro ao firebase auth

*/

class SignUpActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContent {
            SignUpScreen()
        }

    }

}


// Composable Pai
@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(modifier: Modifier = Modifier){

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    var firstNameInput by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf(false) }

    var surnameInput by remember { mutableStateOf("") }
    var surnameError by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("") }
    var emailTextFieldTouched by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    var showPasswordRequirements by remember { mutableStateOf(false) }

    var firstPasswordInput by remember { mutableStateOf("") }
    var firstPasswordError by remember { mutableStateOf(false) }
    var firstPasswordFieldTouched by remember { mutableStateOf(false) }


    class PasswordValidationState{
        var validLength by mutableStateOf(false)
        var hasUpperAndLowercase by mutableStateOf(false)
        var hasNumber by mutableStateOf(false)
        var hasSpecialChar by mutableStateOf(false)

        val passwordError : Boolean
            get() = !validLength
                        || !hasUpperAndLowercase
                            || !hasNumber
                                || !hasSpecialChar

        fun validate(input: String){

            validLength = input.length in 8..36

            hasUpperAndLowercase =
                input.contains(Regex("[A-Z]"))
                        && input.contains(Regex("[a-z]"))

            hasNumber = input.contains(Regex("[0-9]"))

            hasSpecialChar = input.contains(Regex("[^a-zA-Z0-9]"))

        }
    }

    val passwordValidation = remember { PasswordValidationState() }

    var secondPasswordInput by remember { mutableStateOf("") }
    var secondPasswordError by remember { mutableStateOf(false) }

    var visiblePassword by remember { mutableStateOf(false) }
    val visiblePwIcon =     if (visiblePassword)
                                Icons.Rounded.VisibilityOff
                            else
                                Icons.Rounded.Visibility

    val allFieldsNotEmpty : Boolean = listOf(
        firstNameInput,
        surnameInput,
        emailInput,
        firstPasswordInput,
        secondPasswordInput
    ).all { it.isNotEmpty() }

    val noFieldErrors : Boolean = listOf(
        firstNameError,
        surnameError,
        emailError,
        firstPasswordError,
        secondPasswordError
    ).all { !it }

    val enabledButton = allFieldsNotEmpty && noFieldErrors


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Criar Conta",
            fontSize = 35.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(55.dp, 10.dp, 10.dp, 10.dp)

        )

        HorizontalDivider(
            modifier = Modifier.size(300.dp, 20.dp),
            thickness = 3.dp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de texto para o primeiro nome
        FirstNameTextField(
            value = firstNameInput,
            error = firstNameError,
            onValueChange = {
                if (it.length <= 30){
                    firstNameInput = it
                    firstNameError = firstNameValidation(it)
                }
            },
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de texto para o sobrenome
        SurnameTextField(
            value = surnameInput,
            onValueChange = {
                if(it.length <= 50){
                    surnameInput = it
                    surnameError = surnameValidation(it)
                }
            },
            error = surnameError,
            focusManager = focusManager
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Campo de entrada de texto de Email
        EmailTextField(
            value = emailInput,
            onValueChange = {
                emailInput = it
                emailError = emailValidation(it)
            },
            error = emailError,
            focusManager = focusManager
        )

        AnimatedVisibility(showPasswordRequirements){
            PasswordRequirementsCard(
                pwValidLength = passwordValidation.validLength,
                pwHasUpperAndLowercase = passwordValidation.hasUpperAndLowercase,
                pwHasNumber = passwordValidation.hasNumber,
                pwHasSpecialChar = passwordValidation.hasSpecialChar
            )
        }



        Spacer(modifier = Modifier.height(10.dp))

        // Primeiro campo de entrada de Senha

        FirstPasswordTextField(
            value = firstPasswordInput,

            onValueChange =  {
                if(it.length <= 36){
                    firstPasswordInput = it
                    passwordValidation.validate(it)
                    firstPasswordError = passwordValidation.passwordError
                }
            },

            visiblePassword = visiblePassword,
            trailingIcon = {
                IconButton(onClick = { visiblePassword = !visiblePassword})
                {
                    Icon(
                        imageVector = visiblePwIcon,
                        contentDescription = "Ícone de Ocultar Senha"
                    )
                }
            },

            error = firstPasswordError,
            focusManager = focusManager,
            modifier = Modifier
                .onFocusChanged(onFocusChanged = { FocusState ->
                    showPasswordRequirements = FocusState.isFocused
                })
        )



        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = secondPasswordInput,
            label = { Text("Confirmar Senha") },
            singleLine = true,

            onValueChange = {
                secondPasswordInput = it

                if(secondPasswordInput.isNotEmpty()){
                    if(!(firstPasswordInput == secondPasswordInput)){
                        secondPasswordError = true
                    }
                }
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            ),

            visualTransformation =  if (visiblePassword)
                                        VisualTransformation.None
                                    else
                                        PasswordVisualTransformation(),



            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Password,
                    contentDescription = "Ícone de senha"
                )
            }

        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { TODO() },
            enabled = enabledButton
        ){
            Text("Criar Conta")
        }
    }
}

// Composables filho
@Composable
fun FirstNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean,
    focusManager: FocusManager
){
    OutlinedTextField(
        value = value,
        label = { Text("Primeiro Nome") },
        singleLine = true,

        onValueChange = onValueChange,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),

        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Ícone de Pessoa"
            )
        }
    )
}

@Composable
fun SurnameTextField(
    value : String,
    onValueChange: (String) -> Unit,
    error: Boolean,
    focusManager: FocusManager
){
    OutlinedTextField(
        value = value,
        label = { Text("Sobrenome") },
        singleLine = true,

        onValueChange = onValueChange,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean,
    focusManager: FocusManager
){

    OutlinedTextField(
        value = value,
        label = { Text("Email") },
        singleLine = true,

        onValueChange = onValueChange,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),

        // Leading icons sao os icones mostrados a direita e geralmente indicam o tipo de campo
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Email,
                contentDescription = "Ícone de Email"
            )
        }
    )
}

@Composable
fun PasswordRequirementsCard(
    pwValidLength : Boolean,
    pwHasUpperAndLowercase : Boolean,
    pwHasNumber : Boolean,
    pwHasSpecialChar : Boolean
){
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3CD)
        ),
        modifier = Modifier
            .size(290.dp, 170.dp)
            .padding(2.dp, 15.dp, 2.dp, 15.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = "Sua senha deve conter:",
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(){
                if (!pwValidLength){
                    Text(
                        text = "❗ De 8 a 24 caracteres"
                    )
                }else{
                    Text(
                        text = "✅ De 8 a 24 caracteres",
                        fontWeight = FontWeight.Medium,
                        color = Color.Green
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(){
                if (!pwHasUpperAndLowercase){
                    Text(
                        text = "❗ Letras maísuclas e minúsculas"
                    )
                }else{
                    Text(
                        text = "✅ Letras maíusculas e minúsculas",
                        fontWeight = FontWeight.Medium,
                        color = Color.Green
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(){
                if(!pwHasNumber){
                    Text(
                        text="❗ Pelo menos um número"
                    )
                }
                else{
                    Text(
                        text = "✅ Pelo menos um número",
                        fontWeight = FontWeight.Medium,
                        color = Color.Green
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(){
                if(!pwHasSpecialChar){
                    Text(text= "❗ Caractere especial (ex: !@#$%&*)")
                }else{
                    Text(
                        text = "✅ Caractere especial (ex: !@#$%&*)",
                        fontWeight = FontWeight.Medium,
                        color = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun FirstPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    visiblePassword: Boolean,
    trailingIcon: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    error: Boolean,
    focusManager: FocusManager
){
    OutlinedTextField(
        value = value,
        label = { Text("Senha") },
        singleLine = true,

        onValueChange = onValueChange,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),

        visualTransformation =  if (visiblePassword)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),

        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Password,
                contentDescription = "Ícone de Senha"
            )
        },

        trailingIcon = trailingIcon,
        modifier = modifier
    )
}

@Composable
fun SecondPasswordTextField(

){

}

/*
Funções de validação de entrada dos campos de texto
Lembrando que o primeiro campo de senha é validado pelo metodo da classe passwordValidation
*/

fun firstNameValidation(input: String): Boolean{
    var error: Boolean

    error = input.length in 2..30 || input.matches(Regex(".*[^a-zA-ZÀ-ÿ\\-].*"))

    return error
}

fun surnameValidation(input: String): Boolean{
    var error: Boolean

    error = input.length in 2..50 || input.matches(Regex(".*[^a-zA-ZÀ-ÿ\\-].*"))

    return error
}

fun emailValidation(input: String): Boolean{
    var error: Boolean

    error = !(Patterns.EMAIL_ADDRESS.matcher(input).matches())

    return error
}



//Criação do usuario no Firebase Auth
fun createUserAuthentication(email: String, password:String){
    TODO()
}


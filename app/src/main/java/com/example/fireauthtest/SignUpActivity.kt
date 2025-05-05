package com.example.fireauthtest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore


/*

!!!  USANDO GOOGLE-SERVICES API DA MINHA CONTA FIREBASE.
DEPOIS ALTERAR PARA O FIREBASE DO PROJETO!

TO DO
    - Adicionar uma seta ao topo para retornar ao WelcomeScreen
    - Encapsular a função de criar usuario (talvez?)
    - Adicionar tema/elementos de UI do app
*/

class SignUpActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SignUpScreen()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

    }
}

class FieldState(
    /* Classe que serve para informar os campos de erro se um erro esta apto para ser
    apresentado (displayable).
    O erro so vai ser exibido apos o usuario ganhar o foco do campo e sair dele.
     */

    private val fieldError: () -> Boolean,
    private val fieldIsNotEmpty: () -> Boolean
)
{

    var wasTouched by mutableStateOf(false)
    var displayableError by mutableStateOf(false)

    val showError: Boolean
        get() = fieldError() && fieldIsNotEmpty() && displayableError

    fun onFocusChange(isFocused: Boolean) {
        if (isFocused) {
            wasTouched = true
        } else {
            if (wasTouched) {
                displayableError = true
            }
        }
    }
}


// Composable Pai
@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignUpScreen(
    modifier : Modifier = Modifier
){

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

    // Objetos Firebase
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val keyboardController = LocalSoftwareKeyboardController.current

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()


    // Variaveis de input dos campos de texto
    var firstNameInput by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf(true) }
    val FirstNameFieldState =
        remember {
            FieldState(
                { firstNameError },
                { firstNameInput.isNotEmpty() }
        )
    }

    var surnameInput by remember { mutableStateOf("") }
    var surnameError by remember { mutableStateOf(false) }
    val SurnameFieldState =
        remember {
            FieldState(
                { surnameError },
                { surnameInput.isNotEmpty() }
            )
        }

    var emailInput by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    val EmailFieldState =
        remember {
            FieldState(
                { emailError },
                { emailInput.isNotEmpty() }
            )
        }

    var showPasswordRequirements by remember { mutableStateOf(false) }

    var firstPasswordInput by remember { mutableStateOf("") }
    var firstPasswordError by remember { mutableStateOf(false) }
    val passwordValidation = remember { PasswordValidationState() }

    var secondPasswordInput by remember { mutableStateOf("") }
    var secondPasswordError by remember { mutableStateOf(false) }

    var visiblePassword by remember { mutableStateOf(false) }
    val visiblePwIcon =     if (visiblePassword)
                                Icons.Rounded.VisibilityOff
                            else
                                Icons.Rounded.Visibility


    // Variaveis de controle de dialogs (avisos)

    var termsOfUseIsChecked by remember { mutableStateOf(false) }
    var showTermsOfUseDialog by remember { mutableStateOf(false) }
    var showAccountCreatedDialog = remember { mutableStateOf(false) }
    var showAccountCollisionDialog = remember { mutableStateOf(false) }
    var showAccountCreationFailedDialog = remember { mutableStateOf(false) }


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
        secondPasswordError,
        !termsOfUseIsChecked
    ).all { !it }

    var enabledButton = allFieldsNotEmpty && noFieldErrors


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
                    firstNameError = !NameFormatValidation(it, 2, 30)
                    Log.d("firstNameError value", "${firstNameError}")
                }
            },
            modifier = Modifier
                .onFocusChanged(){ FocusState ->
                    if (FocusState.isFocused){
                        FirstNameFieldState.onFocusChange(isFocused = true)
                        Log.d("firstNameFieldState was touched?", "${ FirstNameFieldState.wasTouched }")
                    }
                    else{
                        FirstNameFieldState.onFocusChange(isFocused = false)
                        Log.d("firstNameFieldState displayableError?", "${ FirstNameFieldState.displayableError}")
                        Log.d("firstNameFieldState showError?", "${ FirstNameFieldState.showError} ")
                    }
                },
            focusManager = focusManager,
            fieldState = FirstNameFieldState
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de texto para o sobrenome
        SurnameTextField(
            value = surnameInput,
            onValueChange = {
                if(it.length <= 50){
                    surnameInput = it
                    surnameError = !NameFormatValidation(it, 2, 50)
                    Log.d("surnameError value", "${surnameError}")
                }
            },
            modifier = Modifier
                .onFocusChanged(){ FocusState ->
                    if (FocusState.isFocused){
                        SurnameFieldState.onFocusChange(isFocused = true)
                        Log.d("firstNameFieldState was touched?", "${ SurnameFieldState.wasTouched }")
                    }
                    else{
                        SurnameFieldState.onFocusChange(isFocused = false)
                        Log.d("firstNameFieldState displayableError?", "${ SurnameFieldState.displayableError}")
                        Log.d("firstNameFieldState showError?", "${ SurnameFieldState.showError} ")
                    }
                },
            error = surnameError,
            focusManager = focusManager,
            fieldState = SurnameFieldState
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Campo de entrada de texto de Email
        EmailTextField(
            value = emailInput,
            onValueChange = {
                emailInput = it
                emailError = !EmailFormatValidation(it)
                Log.d("emailError value", "${emailError}")
            },
            modifier = Modifier
                .onFocusChanged() { FocusState ->
                    if (FocusState.isFocused){
                        EmailFieldState.onFocusChange(isFocused = true)
                    }
                    else{
                        EmailFieldState.onFocusChange(isFocused = false)
                    }
                },
            error = emailError,
            focusManager = focusManager,
            fieldState = EmailFieldState
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
                    Log.d("firstPasswordError value", "${firstPasswordError}")
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

        SecondPasswordTextField(
            value = secondPasswordInput,
            onValueChange = {
                secondPasswordInput = it
                secondPasswordError = !passwordMatches(firstPasswordInput, it)
                Log.d("secondPasswordError value", "${secondPasswordError}")
            },
            visiblePassword = visiblePassword,
            error = secondPasswordError,
            focusManager = focusManager,
            keyboardController = keyboardController
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = termsOfUseIsChecked,
                onCheckedChange = { termsOfUseIsChecked = it}
            )

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Li e Aceito os"
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Termos de Uso",
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable{
                            showTermsOfUseDialog = true
                        }
                )
            }
        }


        Spacer(modifier = Modifier.height(30.dp))

        CreateAccountButton(
            enabledButton = enabledButton,
            showAccountCreatedDialog = showAccountCreatedDialog,
            showAccountCollisionDialog = showAccountCollisionDialog,
            showAccountCreationFailedDialog = showAccountCreationFailedDialog,
            auth = auth,
            db = db,
            firstName = firstNameInput,
            surname = surnameInput,
            email = emailInput,
            password = firstPasswordInput
        )

        if(showTermsOfUseDialog){
            TermsOfUseDialog(
                onDissmissRequest = { showTermsOfUseDialog = false }
            )
        }

        if(showAccountCreatedDialog.value){
            AccountCreatedDialog(
                onDissmissRequest = {}
            )
        }

        if(showAccountCollisionDialog.value){
            AccountCollisionDialog(
                onDissmissRequest = { showAccountCollisionDialog.value = false }
            )
        }

        if(showAccountCreationFailedDialog.value){
            AccountCreationFailedDialog(
                onDissmissRequest = { showAccountCreationFailedDialog.value = false }
            )
        }

    }
}

// Composables filho
@Composable
fun FirstNameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldState: FieldState,
    error: Boolean,
    focusManager: FocusManager
){
    OutlinedTextField(
        value = value,
        label = { Text("Primeiro Nome") },
        singleLine = true,

        onValueChange = onValueChange,
        modifier = modifier,

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
        },

        supportingText = {
            if(fieldState.showError){
                Text(
                    text = "Formato de nome inválido!",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}

@Composable
fun SurnameTextField(
    value : String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldState: FieldState,
    error: Boolean,
    focusManager: FocusManager
){
    OutlinedTextField(
        value = value,
        label = { Text("Sobrenome") },
        singleLine = true,

        onValueChange = onValueChange,
        modifier = modifier,

        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),

        supportingText = {
            if(fieldState.showError){
                Text(
                    text = "Formato de Sobrenome Inválido!",
                    color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldState: FieldState,
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
        },

        supportingText = {
            if(fieldState.showError){
                Text(
                    text = "Formato de Email Inválido!",
                    color = MaterialTheme.colorScheme.error
                )
            }
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
    value: String,
    onValueChange: (String) -> Unit,
    visiblePassword: Boolean,
    error: Boolean,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
){

    OutlinedTextField(
        value = value,
        label = {Text("Confirmar Senha")},
        singleLine = true,

        onValueChange = onValueChange,

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


}

@Composable
fun TermsOfUseDialog(
    onDissmissRequest : () -> Unit
) {

    val context = LocalContext.current
    val termsOfUseText = remember {
        context.resources.openRawResource(R.raw.terms_of_use)
            .bufferedReader().use { it.readText() }
    }

    Dialog(
        onDismissRequest = { onDissmissRequest() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray),
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Termos de Uso",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(10f)
                        .padding(16.dp)
                        .border(1.dp, Color.DarkGray, RectangleShape)
                        .background(Color.White)
                        .padding(16.dp)
                ){

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                    ) {
                        Text(text = termsOfUseText)
                    }

                    if(scrollState.canScrollForward){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf
                                            (Color.Transparent,
                                            Color.White)
                                    )
                                )
                                .align(Alignment.BottomCenter),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Mais conteúdo abaixo",
                                tint = Color.DarkGray
                            )
                        }
                    }

                    if(scrollState.canScrollBackward){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White,
                                            Color.Transparent)
                                    )
                                )
                                .align(Alignment.TopCenter),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowUp,
                                contentDescription = "Mais conteúdo acima",
                                tint = Color.DarkGray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = onDissmissRequest
                    ) {
                        Text("Voltar")
                    }
                }
            }
        }


    }
}

@Composable
fun CreateAccountButton(
    enabledButton : Boolean,
    showAccountCreatedDialog : MutableState<Boolean>,
    showAccountCollisionDialog : MutableState<Boolean>,
    showAccountCreationFailedDialog : MutableState<Boolean>,
    auth : FirebaseAuth,
    db : FirebaseFirestore,
    firstName : String,
    surname : String,
    email : String,
    password : String,

    ){
    var showProgressIndicator by remember { mutableStateOf(false) }

    Button(
        enabled = enabledButton,
        onClick = {
            // Criar uma funcao pra isso aq...

            showProgressIndicator = true

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task: Task<AuthResult> ->
                    if(task.isSuccessful){
                        Log.d("SignUpActivity", "Usuário criado com sucesso!")
                        val user = auth.currentUser
                        val userUid = user?.uid ?: return@addOnCompleteListener

                        val userData = hashMapOf(
                            "firstName" to firstName,
                            "surname" to surname,
                            "deviceId" to ""
                        )

                        db.collection("users")   // Acessa a colecao users
                            .document(userUid)  // Gera o documento cujo Id é o userId
                            .set(userData)      // Atribui os dados do documento conforme o HashMap
                            .addOnSuccessListener { // O listener de Firestore nao retorna um objeto do tipo Task como em Auth
                                Log.d("SignUpActivity", "Firestore user doc created sucessfully")
                                // Exibir AlertDialog de usuario criado
                                showAccountCreatedDialog.value = true
                                // Mudar depois para redirecionar a activity de verificar email.
                            }
                            .addOnFailureListener {
                                Log.d("SignUpActivity", "Firestore user doc creation failed")
                                // Exibir AlertDialog de erro
                            }

                    }
                    else {
                        Log.w("SignUpActivity", "Criação de usúario falhou. Exception = ${task.exception?.message}")
                        if(task.exception is FirebaseAuthUserCollisionException){
                            showAccountCollisionDialog.value = true
                            showProgressIndicator = false
                        }
                        else{
                            showAccountCreationFailedDialog.value = true
                            showProgressIndicator = false
                        }
                    }
                }
        }
    ){

        if(showProgressIndicator){
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        else{
            Text("Criar Conta")
        }
    }
}

@Composable
fun AccountCreatedDialog(
    onDissmissRequest: () -> Unit
){
    val context = LocalContext.current

    Dialog(
        onDismissRequest = {}
    ) {

        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .size(275.dp)
                .padding(26.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ícone de Sucesso",
                    tint = Color.Green,
                    modifier = Modifier
                        .size(60.dp)
                )

                Text(
                    text = "Conta criada com sucesso!",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )

                Button(
                    onClick = {
                        val intent = Intent(context, SignInActivity::class.java)
                        context.startActivity(intent)
                    },
                ){
                    Text("Fazer Login")
                }
            }
        }
    }
}

@Composable
fun AccountCollisionDialog(
    onDissmissRequest: () -> Unit
){

    val context = LocalContext.current

    Dialog(
        onDismissRequest = { onDissmissRequest() }
    ) {

        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .size(275.dp)
                .padding(26.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ){
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Ícone de Alerta",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(60.dp)
                )

                Text(
                    text = "Essa conta de email já está em uso!",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ){
                    Button(
                        onClick = {
                            val intent = Intent(context, SignInActivity::class.java)
                            context.startActivity(intent)
                        },
                    ) {
                        Text("Fazer Login")
                    }
                    Button(
                        onClick = onDissmissRequest
                    ) {
                        Text("Voltar")
                    }
                }
            }
        }
    }
}

@Composable
fun AccountCreationFailedDialog(
    onDissmissRequest: () -> Unit
){
    Dialog(
        onDismissRequest = { onDissmissRequest() }
    ) {

        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(16.dp))
                .size(275.dp)
                .padding(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Icon(
                    painter = painterResource(R.drawable.error_icon),
                    contentDescription = "Ícone de Erro",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(60.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "Não foi possível criar a conta",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Tente novamente mais tarde"
                    )
                }

                Button(
                    onClick = onDissmissRequest
                ) {
                    Text("Fechar")
                }
            }
        }
    }
}

/*
Funções de validação de entrada dos campos de texto
Lembrando que o primeiro campo de senha é validado pelo metodo da classe passwordValidation
*/

fun NameFormatValidation(
    input: String,
    minLength : Int,
    maxLength : Int) : Boolean {

    fun isValidLength(input: String) = input.length in minLength..maxLength
    fun hasOnlyValidChars(input: String) = !input.contains(Regex("[^a-zA-ZÀ-ÿ -]"))

    return isValidLength(input) && hasOnlyValidChars(input)
}

fun EmailFormatValidation(input: String) : Boolean{
    return Patterns.EMAIL_ADDRESS.matcher(input).matches()
}

fun passwordMatches(firstPw: String, secondPw: String): Boolean {

    return (firstPw == secondPw)
}

fun createUserAccount(auth: FirebaseAuth, email: String, password: String){
    TODO()
}



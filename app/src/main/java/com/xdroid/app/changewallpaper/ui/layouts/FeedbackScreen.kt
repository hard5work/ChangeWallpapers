package com.xdroid.app.changewallpaper.ui.layouts

import android.annotation.SuppressLint
import android.provider.CalendarContract.Colors
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xdroid.app.changewallpaper.R
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialog
import com.xdroid.app.changewallpaper.ui.dialogs.InfoAlertDialogWithAds
import com.xdroid.app.changewallpaper.ui.dialogs.LoadingAlertDialog
import com.xdroid.app.changewallpaper.ui.theme.black
import com.xdroid.app.changewallpaper.ui.theme.colorPrimary
import com.xdroid.app.changewallpaper.ui.theme.white
import com.xdroid.app.changewallpaper.utils.constants.StringCoded
import com.xdroid.app.changewallpaper.utils.enums.Resource
import com.xdroid.app.changewallpaper.utils.enums.Status
import com.xdroid.app.changewallpaper.utils.helpers.DebugMode
import com.xdroid.app.changewallpaper.utils.helpers.isNull
import com.xdroid.app.changewallpaper.utils.vm.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FeedbackScreen(navController: NavController) {

    val postConfession: HomeViewModel = koinViewModel()

    var isChecked by remember {
        mutableStateOf(false)
    }

    val postData by postConfession.feedbackResponse.collectAsState(Resource.idle())

    var title by remember { mutableStateOf("") }
    var conte by remember { mutableStateOf("") }


    val titleErr by postConfession.title
    val contentErr by postConfession.content

    var isLoading by remember {
        mutableStateOf(false)
    }

    var isError by remember {
        mutableStateOf(false)
    }

    var showSuccess by remember {
        mutableStateOf(false)
    }


    var errMsg by remember {
        mutableStateOf("")
    }
    when (postData.status) {
        Status.LOADING -> {
            isLoading = true

        }

        Status.SUCCESS -> {
            LaunchedEffect(Unit) {
                showSuccess = true
                errMsg = StringCoded.feedbackMsg


            }

            isLoading = false
        }

        Status.IDLE -> {
            isLoading = false

        }

        Status.ERROR -> {
            isLoading = false
            LaunchedEffect(Unit) {
                isError = true
                errMsg = postData.message.isNull()
            }

        }
    }


    if (isError) {
        InfoAlertDialogWithAds (message = errMsg,) {
            isError = false
        }
    }

    if (showSuccess) {
        InfoAlertDialogWithAds(message = errMsg) {
            showSuccess = false
            navController.navigateUp()
        }
    }
    if (isLoading) {
        LoadingAlertDialog()
    }
    BackHandler {
        navController.navigateUp()
    }


    Scaffold {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            BasicToolbar(navController = {
                navController.navigateUp()
            }, title = "Share your feedback")
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = title,
                    onValueChange ={text ->
                        title = text
                        postConfession.clearLoginError()

                    },

                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                if(titleErr.isNull().isNotEmpty()){
                    Text(titleErr.isNull(), color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = conte,
                    onValueChange = {
                        text ->
                        conte = text

                        postConfession.clearLoginError()
                    },
                    label = { Text("Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 10
                )
                if(contentErr.isNull().isNotEmpty()){
                    Text(contentErr.isNull(), color = Color.Red)
                }
                Spacer(modifier = Modifier.height(24.dp))
                FullButton(label = "Send Feedback", modifier = Modifier, {
                    postConfession.verifyPosts(
                        title = title,
                        content = conte
                    )
                })


                Spacer(modifier = Modifier.height(40.dp))


            }


        }
    }
}

@Composable
fun BasicToolbar(navController: () -> Boolean, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController()}) {
            Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "BackIcon")
        }
        Text(
            text = title,
            fontSize = 18.sp,
            color = white,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

}



@Composable
fun FullButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    btnColor: Color = colorPrimary,
) {

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = btnColor)

    ) {
        Text(label, color = white)
    }
}

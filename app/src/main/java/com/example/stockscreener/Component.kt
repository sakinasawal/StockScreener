package com.example.stockscreener

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*

@Composable
@SuppressLint("ModifierParameter")
fun TextLabel(
    text : Int,
    modifier : Modifier = Modifier,
    typographyStyle: TextStyle = LocalTextStyle.current
){
    Text(
        text = stringResource(id = text),
        style = typographyStyle,
        modifier = modifier
    )
}

@Composable
fun InputTextField(
    value : String,
    onValueChange : (String)->Unit,
    placeholder: String,
    onClear: () -> Unit,
    modifier : Modifier = Modifier,
){
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear input")
                }
            }
        }
    )
}
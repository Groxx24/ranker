package com.ranker.countries.ui.rankings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.ranker.countries.resources.Res
import com.ranker.countries.resources.debug_publish
import com.ranker.countries.resources.debug_publish_failed
import com.ranker.countries.resources.debug_published
import com.ranker.countries.resources.debug_unlock_button
import com.ranker.countries.resources.debug_unlock_cancel
import com.ranker.countries.resources.debug_unlock_hint
import com.ranker.countries.resources.debug_unlock_password
import com.ranker.countries.resources.debug_unlock_title
import com.ranker.countries.resources.debug_unlock_wrong
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * Debug builds only: a floating button that replaces the stats in the backend with a new set,
 * asking for the publisher's password first if not signed in yet, and a snackbar that says how it
 * went. Laid over the whole screen so it works on the empty and failed states too.
 */
@Composable
fun PublishSeedStats(
    state: PublishState,
    onPublish: () -> Unit,
    onUnlock: (passphrase: String) -> Unit,
    onUnlockDismissed: () -> Unit,
    onResultShown: () -> Unit,
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(state) {
        val message = when (state) {
            is PublishState.Published -> getString(Res.string.debug_published, state.count)
            PublishState.Failed -> getString(Res.string.debug_publish_failed)
            else -> return@LaunchedEffect
        }
        snackbar.showSnackbar(message)
        onResultShown()
    }
    if (state is PublishState.Unlocking) UnlockDialog(state, onUnlock, onUnlockDismissed)
    Box(Modifier.fillMaxSize().navigationBarsPadding().padding(16.dp)) {
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp))
        val busy = state == PublishState.Publishing || (state as? PublishState.Unlocking)?.checking == true
        ExtendedFloatingActionButton(
            onClick = onPublish,
            modifier = Modifier.align(Alignment.BottomEnd),
            icon = {
                if (busy) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("☁️")
            },
            text = { Text(stringResource(Res.string.debug_publish)) },
        )
    }
}

/** Asks for the publisher account's password; never saved, gone once the dialog closes. */
@Composable
private fun UnlockDialog(state: PublishState.Unlocking, onUnlock: (String) -> Unit, onDismiss: () -> Unit) {
    var passphrase by remember { mutableStateOf("") }
    val canSubmit = !state.checking && passphrase.isNotEmpty()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.debug_unlock_title)) },
        text = {
            Column {
                Text(
                    stringResource(Res.string.debug_unlock_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text(stringResource(Res.string.debug_unlock_password)) },
                    singleLine = true,
                    enabled = !state.checking,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (canSubmit) onUnlock(passphrase) }),
                    isError = state.wrong,
                    supportingText = if (state.wrong) {
                        { Text(stringResource(Res.string.debug_unlock_wrong)) }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onUnlock(passphrase) }, enabled = canSubmit) {
                Text(stringResource(Res.string.debug_unlock_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !state.checking) {
                Text(stringResource(Res.string.debug_unlock_cancel))
            }
        },
    )
}

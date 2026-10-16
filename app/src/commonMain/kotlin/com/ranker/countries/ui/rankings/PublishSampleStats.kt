package com.ranker.countries.ui.rankings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ranker.countries.resources.Res
import com.ranker.countries.resources.debug_publish
import com.ranker.countries.resources.debug_publish_failed
import com.ranker.countries.resources.debug_published
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * Debug builds only: a floating button that writes the sample stats to the backend, and a snackbar
 * that says how it went. Laid over the whole screen so it works on the empty and failed states too.
 */
@Composable
fun PublishSampleStats(state: PublishState, onPublish: () -> Unit, onResultShown: () -> Unit) {
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
    Box(Modifier.fillMaxSize().navigationBarsPadding().padding(16.dp)) {
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp))
        val publishing = state == PublishState.Publishing
        ExtendedFloatingActionButton(
            onClick = { if (!publishing) onPublish() },
            modifier = Modifier.align(Alignment.BottomEnd),
            icon = {
                if (publishing) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp) else Text("☁️")
            },
            text = { Text(stringResource(Res.string.debug_publish)) },
        )
    }
}

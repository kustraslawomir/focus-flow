package skustra.focusflow.ui.composables.session.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import skustra.focusflow.data.timer.TimerState
import skustra.focusflow.ui.composables.session.SessionViewModel
import skustra.focusflow.ui.localization.LocalizationKey
import skustra.focusflow.ui.localization.LocalizationManager
import skustra.focusflow.ui.theme.ButtonColor

@Composable
fun SessionPanelComposable(viewModel: SessionViewModel = viewModel()) {

    val sessionState by viewModel
        .sessionStateFlow
        .collectAsStateWithLifecycle(viewModel.currentSessionState)

    when (sessionState.currentTimerState) {
        TimerState.Completed, TimerState.Idle -> {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SkipBreaksCheckBox()
                CreateSessionButton()
            }
        }

        is TimerState.InProgress -> PauseSessionButton()
        is TimerState.Paused -> ResumeSession()
    }
}

@Composable
private fun CreateSessionButton(viewModel: SessionViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
            .clip(shape = RoundedCornerShape(size = 12.dp))
            .background(color = ButtonColor)
            .clickable { viewModel.createSession() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.createSession() }) {
                Icon(
                    painter = painterResource(id = viewModel.resourceManager.getPlayIcon()),
                    contentDescription = "todo",
                    tint = Color.Black
                )
            }
            Text(
                text = LocalizationManager.getText(LocalizationKey.CreateSession),
                modifier = Modifier.padding(end = 16.dp, bottom = 2.dp),
                color = Color.Black
            )
        }
    }
}

@Composable
private fun PauseSessionButton(viewModel: SessionViewModel = viewModel()) {
    CircleButton(
        onClick = {
            viewModel.pauseSession()
        },
        icon = viewModel.resourceManager.getPauseIcon()
    )
}

@Composable
private fun ResumeSessionButton(viewModel: SessionViewModel = viewModel()) {
    CircleButton(
        onClick = {
            viewModel.resumeSession()
        },
        icon = viewModel.resourceManager.getResumeIcon()
    )
}

@Composable
private fun ResumeSession() {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        StopSessionButton()
        Spacer(modifier = Modifier.width(16.dp))
        ResumeSessionButton()
    }
}

@Composable
private fun StopSessionButton(viewModel: SessionViewModel = viewModel()) {
    CircleButton(
        onClick = {
            viewModel.stopSession()
        },
        icon = viewModel.resourceManager.getStopIcon(),
        color = Color.White
    )
}

@Composable
private fun CircleButton(onClick: () -> Unit, icon: Int, color: Color = ButtonColor) {
    IconButton(
        onClick = { onClick() },
        modifier = Modifier
            .size(46.dp)
            .background(color, CircleShape)
            .padding(6.dp),
        content = {
            Icon(
                painter = painterResource(id = icon),
                "$icon",
                tint = Color.Black
            )
        }
    )
}

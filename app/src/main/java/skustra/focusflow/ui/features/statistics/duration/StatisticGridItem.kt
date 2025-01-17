package skustra.focusflow.ui.features.statistics.duration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import skustra.focusflow.data.model.statistics.DurationUiModel
import skustra.focusflow.ui.theme.CustomDimensions.DEFAULT_CORNERS_RADIUS

@Composable
fun StatisticGridItem(durationUiModel: DurationUiModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1.5f)
            .padding(all = 5.dp)
            .clip(
                shape = RoundedCornerShape(DEFAULT_CORNERS_RADIUS.dp)
            )
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = durationUiModel.name,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = durationUiModel.value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
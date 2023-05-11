package skustra.focusflow.ui.composables.statistics

import com.patrykandpatrick.vico.core.entry.ChartEntry
import skustra.focusflow.data.database.entity.SessionArchiveEntity
import skustra.focusflow.data.model.alias.Minute


class SessionArchiveEntry(
    val sessionArchiveEntryDataModel: SessionArchiveEntryDataModel,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = SessionArchiveEntry(sessionArchiveEntryDataModel, x, y)
}

class SessionArchiveEntryDataModel constructor(val summedDayDuration : Minute, val date : String)
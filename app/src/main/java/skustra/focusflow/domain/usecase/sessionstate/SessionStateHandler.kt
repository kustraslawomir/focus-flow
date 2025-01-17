package skustra.focusflow.domain.usecase.sessionstate

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import skustra.focusflow.data.database.entity.SessionArchiveEntity
import skustra.focusflow.data.model.alias.Minute
import skustra.focusflow.data.model.session.Session
import skustra.focusflow.data.model.session.SessionPartType
import skustra.focusflow.data.model.timer.TimerState
import skustra.focusflow.data.repositories.sessionarchive.SessionArchiveRepository
import skustra.focusflow.domain.usecase.session.SessionCreator
import skustra.focusflow.domain.usecase.statenotification.BreakCompletedNotification
import skustra.focusflow.domain.usecase.statenotification.SessionCompletedNotification
import skustra.focusflow.domain.usecase.statenotification.SessionStartedNotification
import skustra.focusflow.domain.usecase.statenotification.WorkCompletedNotification
import skustra.focusflow.ui.service.SessionForegroundService
import skustra.focusflow.ui.utilities.logs.AppLog
import timber.log.Timber

class SessionStateHandler(
    private val applicationContext: Context,
    private val workCompletedNotification: WorkCompletedNotification,
    private val breakCompletedNotification: BreakCompletedNotification,
    private val sessionCompletedNotification: SessionCompletedNotification,
    private val sessionStartedNotification: SessionStartedNotification,
    private val sessionStateEmitter: SessionStateEmitter,
    private val sessionArchiveRepository: SessionArchiveRepository
) {

    private lateinit var sessionScope: CoroutineScope
    private var currentSessionState = SessionCreator.generate()

    var sessionStateFlow: MutableStateFlow<Session> = MutableStateFlow(currentSessionState)
        private set

    fun init(scope: CoroutineScope) {
        this.sessionScope = scope
        sessionScope.launch {
            sessionStateEmitter.getCurrentState().stateIn(
                scope = sessionScope,
                initialValue = currentSessionState,
                started = SharingStarted.WhileSubscribed(5000)
            ).collect { timerState ->
                AppLog.logTimerStateCollection(timerState)
                if (timerState is TimerState) {
                    handleNewTimerState(timerState)
                }
            }
        }
    }

    fun startSession(durationChosenByUser: Minute, skipBreaks: Boolean) {
        sessionScope.launch {
            currentSessionState = SessionCreator.generate(durationChosenByUser, skipBreaks)
            sessionStateEmitter.start(
                sessionPart = currentSessionState.currentSessionPart(), scope = sessionScope
            )
            sessionStartedNotification.notifyUser()
        }
    }

    suspend fun emitSession(session: Session) {
        sessionStateFlow.emit(session.copy())
    }

    fun pauseSession() {
        sessionStateEmitter.pause()
    }

    fun resumeSession() {
        sessionStateEmitter.resume()
    }

    fun stopSession() {
        sessionScope.launch {
            sessionStateEmitter.stop()
            applicationContext.stopService(
                Intent(
                    applicationContext, SessionForegroundService::class.java
                )
            )
        }
    }

    private suspend fun handleNewTimerState(timerState: TimerState) {
        if (timerState is TimerState.Completed) {
            currentSessionState.activateTheNextPartOfTheSession(
                onCurrentSessionPartIncremented = {
                    sessionStateEmitter.start(
                        sessionPart = currentSessionState.currentSessionPart(),
                        scope = sessionScope
                    )
                    when (timerState.type) {
                        SessionPartType.Break -> breakCompletedNotification.notifyUser()
                        SessionPartType.Work -> workCompletedNotification.notifyUser()
                    }
                }, onSessionCompleted = {
                    val data = SessionArchiveEntity.create(sessionStateFlow.value)
                    storeSession(data)
                    sessionCompletedNotification.notifyUser()
                    stopSession()
                })
        }

        emiTimerState(timerState)
        AppLog.debugSession(currentSessionState)
    }

    private fun storeSession(data: SessionArchiveEntity) {
        sessionScope.launch(Dispatchers.IO) {
            Timber.d("Insert: ${data.formattedDate} ${data.minutes}")
            sessionArchiveRepository.insert(data)
        }
    }

    private suspend fun emiTimerState(state: TimerState) {
        Timber.d("Emit: $state ${state is TimerState.Completed}")
        sessionStateFlow.emit(currentSessionState.apply {
            currentTimerState = state
        }.copy())
    }
}
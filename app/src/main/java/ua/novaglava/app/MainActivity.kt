package ua.novaglava.app

import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF23342F)
private val Muted = Color(0xFF718078)
private val CanvasBg = Color(0xFFFAFBF8)
private val Mint = Color(0xFFDFF1E5)
private val MintStrong = Color(0xFF76B587)
private val MintDark = Color(0xFF436D57)
private val Lilac = Color(0xFFEDE7FF)
private val LilacStrong = Color(0xFF8C74C7)
private val Peach = Color(0xFFFFE9D2)
private val Sun = Color(0xFFF7C96E)
private val Rose = Color(0xFFFFE7EC)
private val CardBorder = Color(0xFFE8ECE7)

private enum class Page {
    HOME, CHAPTER, FRONTLINE, ANALYTICS, PROFILE, MORNING, CHECKOUT, CHAPTER_EDITOR,
    WEEKLY, VISION, HISTORY, COMPLETE, REMINDERS, SWITCHES, BACKUP, FOCUS_STATS,
    METHODS, DOSSIER, FRONTLINE_AUDIT, DETONATOR, REBOOT, LIFE_SYSTEM
}

private data class ChapterConfig(
    val title: String,
    val theme: String,
    val identity: String,
    val frontline: String,
    val duration: Int,
    val result1: String,
    val result2: String,
    val result3: String
)

private data class EvidenceEntry(
    val day: Int,
    val action: String,
    val result: String,
    val reflection: String,
    val mood: String = "",
    val switchDone: Boolean = false,
    val switchAction: String = "",
    val tasksDone: Int = 0,
    val frontlineMinutes: Int = 0
)

private data class ChapterArchive(
    val title: String,
    val theme: String,
    val duration: Int,
    val evidenceCount: Int,
    val frontlineMinutes: Int,
    val finishedAt: Long
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovaGlavaTheme { NovaGlavaApp() }
        }
    }
}

@Composable
private fun NovaGlavaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = MintDark,
            secondary = LilacStrong,
            background = CanvasBg,
            surface = Color.White,
            onBackground = Ink,
            onSurface = Ink
        ),
        content = content
    )
}

@Composable
private fun NovaGlavaApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("nova_glava", Context.MODE_PRIVATE) }

    var configured by rememberSaveable { mutableStateOf(prefs.getBoolean("configured", false)) }
    var page by rememberSaveable { mutableStateOf(if (configured) Page.HOME else Page.CHAPTER_EDITOR) }

    var title by rememberSaveable { mutableStateOf(prefs.getString("chapter_title", "Глава 01") ?: "Глава 01") }
    var theme by rememberSaveable { mutableStateOf(prefs.getString("chapter_theme", "Більше себе") ?: "Більше себе") }
    var identity by rememberSaveable {
        mutableStateOf(
            prefs.getString(
                "chapter_identity",
                "Я завершую важливе, бережу енергію та щодня рухаю те, що дає реальний результат."
            ) ?: ""
        )
    }
    var frontline by rememberSaveable {
        mutableStateOf(prefs.getString("chapter_frontline", "90 хвилин на головній справі") ?: "90 хвилин на головній справі")
    }
    var duration by rememberSaveable { mutableIntStateOf(prefs.getInt("duration", 30).coerceIn(30, 90)) }
    var result1 by rememberSaveable { mutableStateOf(prefs.getString("result_1", "Завершувати, а не лише починати") ?: "") }
    var result2 by rememberSaveable { mutableStateOf(prefs.getString("result_2", "Більше руху й енергії") ?: "") }
    var result3 by rememberSaveable { mutableStateOf(prefs.getString("result_3", "Більше часу на Передовій") ?: "") }
    var dossierBrief by rememberSaveable { mutableStateOf(prefs.getString("dossier_brief", "") ?: "") }

    var day by rememberSaveable { mutableIntStateOf(prefs.getInt("day", 1).coerceIn(1, duration)) }
    var mood by rememberSaveable { mutableStateOf(prefs.getString("mood", "Спокійна") ?: "Спокійна") }
    var switchOn by rememberSaveable { mutableStateOf(prefs.getBoolean("switch_on", false)) }
    var dayStarted by rememberSaveable { mutableStateOf(prefs.getBoolean("day_started", false)) }
    var selectedSwitchAction by rememberSaveable { mutableStateOf(prefs.getString("selected_switch_action", "") ?: "") }

    val switchActions = remember {
        mutableStateListOf<String>().apply {
            val defaults = listOf(
                "Почати найважливішу задачу до перевірки соцмереж",
                "20 хвилин руху або прогулянки",
                "Завершити одну відкладену справу",
                "Зробити один прямий крок до результату",
                "30 хвилин без інформаційного шуму",
                "Написати або подзвонити людині, з якою відкладаю контакт"
            )
            repeat(6) { i -> add(prefs.getString("switch_action_$i", defaults[i]) ?: defaults[i]) }
        }
    }

    val frontlineTaskTexts = remember {
        mutableStateListOf(
            prefs.getString("frontline_task_0", frontline) ?: frontline,
            prefs.getString("frontline_task_1", "Завершити одну задачу, яку давно відкладаю") ?: "",
            prefs.getString("frontline_task_2", "Зробити одну дію, що прямо дає результат") ?: ""
        )
    }

    var morningReminderEnabled by rememberSaveable { mutableStateOf(prefs.getBoolean("reminder_morning_enabled", false)) }
    var eveningReminderEnabled by rememberSaveable { mutableStateOf(prefs.getBoolean("reminder_evening_enabled", false)) }
    var morningReminderTime by rememberSaveable { mutableStateOf(prefs.getString("reminder_morning_time", "08:30") ?: "08:30") }
    var eveningReminderTime by rememberSaveable { mutableStateOf(prefs.getString("reminder_evening_time", "21:00") ?: "21:00") }

    val tasks = remember {
        mutableStateListOf(
            prefs.getBoolean("task_0", false),
            prefs.getBoolean("task_1", false),
            prefs.getBoolean("task_2", false)
        )
    }

    var timerMinutes by rememberSaveable { mutableIntStateOf(prefs.getInt("timer_minutes", 45)) }
    var timerSeconds by rememberSaveable { mutableIntStateOf(prefs.getInt("timer_seconds", timerMinutes * 60).coerceAtLeast(0)) }
    var timerRunning by rememberSaveable { mutableStateOf(false) }
    var dailyFrontlineSeconds by rememberSaveable { mutableIntStateOf(prefs.getInt("daily_frontline_seconds", 0).coerceAtLeast(0)) }

    val evidence = remember {
        mutableStateListOf<EvidenceEntry>().apply {
            val count = prefs.getInt("evidence_count", 0)
            for (i in 0 until count) {
                add(
                    EvidenceEntry(
                        day = prefs.getInt("evidence_${i}_day", 1),
                        action = prefs.getString("evidence_${i}_action", "") ?: "",
                        result = prefs.getString("evidence_${i}_result", "") ?: "",
                        reflection = prefs.getString("evidence_${i}_reflection", "") ?: "",
                        mood = prefs.getString("evidence_${i}_mood", "") ?: "",
                        switchDone = prefs.getBoolean("evidence_${i}_switch", false),
                        switchAction = prefs.getString("evidence_${i}_switch_action", "") ?: "",
                        tasksDone = prefs.getInt("evidence_${i}_tasks", 0),
                        frontlineMinutes = prefs.getInt("evidence_${i}_minutes", 0)
                    )
                )
            }
        }
    }

    val archives = remember {
        mutableStateListOf<ChapterArchive>().apply {
            val count = prefs.getInt("archive_count", 0)
            for (i in 0 until count) {
                add(ChapterArchive(
                    title = prefs.getString("archive_${i}_title", "Глава") ?: "Глава",
                    theme = prefs.getString("archive_${i}_theme", "") ?: "",
                    duration = prefs.getInt("archive_${i}_duration", 30),
                    evidenceCount = prefs.getInt("archive_${i}_evidence", 0),
                    frontlineMinutes = prefs.getInt("archive_${i}_minutes", 0),
                    finishedAt = prefs.getLong("archive_${i}_finished", 0L)
                ))
            }
        }
    }

    val visionTexts = remember { mutableStateListOf<String>().apply { repeat(6) { add(prefs.getString("vision_${it}_text", "") ?: "") } } }
    val visionUris = remember { mutableStateListOf<String>().apply { repeat(6) { add(prefs.getString("vision_${it}_uri", "") ?: "") } } }

    val chapter = ChapterConfig(title, theme, identity, frontline, duration, result1, result2, result3)

    LaunchedEffect(day, mood, switchOn, dayStarted, selectedSwitchAction, timerMinutes, timerSeconds, dailyFrontlineSeconds) {
        prefs.edit()
            .putInt("day", day)
            .putString("mood", mood)
            .putBoolean("switch_on", switchOn)
            .putBoolean("day_started", dayStarted)
            .putString("selected_switch_action", selectedSwitchAction)
            .putInt("timer_minutes", timerMinutes)
            .putInt("timer_seconds", timerSeconds)
            .putInt("daily_frontline_seconds", dailyFrontlineSeconds)
            .apply()
        updateNovaGlavaWidgets(context)
    }

    LaunchedEffect(timerRunning) {
        while (timerRunning && timerSeconds > 0) {
            delay(1000)
            timerSeconds -= 1
            dailyFrontlineSeconds += 1
        }
        if (timerSeconds <= 0) timerRunning = false
    }

    BackHandler(enabled = page in listOf(Page.FRONTLINE, Page.MORNING, Page.CHECKOUT, Page.CHAPTER_EDITOR, Page.WEEKLY, Page.VISION, Page.HISTORY, Page.COMPLETE, Page.REMINDERS, Page.SWITCHES, Page.BACKUP, Page.FOCUS_STATS, Page.METHODS, Page.DOSSIER, Page.FRONTLINE_AUDIT, Page.DETONATOR, Page.REBOOT, Page.LIFE_SYSTEM) && configured) {
        page = Page.HOME
    }

    val showBottomBar = configured && page in listOf(Page.HOME, Page.CHAPTER, Page.FRONTLINE, Page.ANALYTICS, Page.PROFILE)

    Scaffold(
        containerColor = CanvasBg,
        bottomBar = {
            if (showBottomBar) BottomNav(page = page, onPage = { page = it })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (page) {
                Page.HOME -> HomeScreen(
                    chapter = chapter,
                    selfVersion = currentSelfVersion(prefs),
                    day = day,
                    mood = mood,
                    switchOn = switchOn,
                    dayStarted = dayStarted,
                    doneTasks = tasks.count { it },
                    timerSeconds = timerSeconds,
                    switchSuggestion = selectedSwitchAction.ifBlank { switchActions.firstOrNull { it.isNotBlank() } ?: chapter.frontline },
                    onMood = { mood = it },
                    onSwitch = { checked ->
                        if (checked && selectedSwitchAction.isBlank()) selectedSwitchAction = switchActions.firstOrNull { it.isNotBlank() } ?: chapter.frontline
                        switchOn = checked
                    },
                    onFrontline = { page = Page.FRONTLINE },
                    onAnalytics = { page = Page.ANALYTICS },
                    onChapter = { page = Page.CHAPTER },
                    onMorning = { page = Page.MORNING }
                )

                Page.CHAPTER -> ChapterScreen(
                    chapter = chapter,
                    selfVersion = currentSelfVersion(prefs),
                    day = day,
                    switchOn = switchOn,
                    dayStarted = dayStarted,
                    doneTasks = tasks.count { it },
                    onMorning = { page = Page.MORNING },
                    onCheckout = { page = Page.CHECKOUT },
                    onEdit = { page = Page.CHAPTER_EDITOR }
                )

                Page.FRONTLINE -> FrontlineScreen(
                    chapter = chapter,
                    tasks = tasks,
                    taskTexts = frontlineTaskTexts,
                    timerMinutes = timerMinutes,
                    timerSeconds = timerSeconds,
                    timerRunning = timerRunning,
                    dailyFrontlineMinutes = dailyFrontlineSeconds / 60,
                    onToggle = { index, checked ->
                        tasks[index] = checked
                        prefs.edit().putBoolean("task_$index", checked).apply()
                    },
                    onTaskText = { index, value ->
                        frontlineTaskTexts[index] = value
                        prefs.edit().putString("frontline_task_$index", value).apply()
                    },
                    onMinutes = { minutes ->
                        timerMinutes = minutes
                        timerSeconds = minutes * 60
                        timerRunning = false
                    },
                    onStartPause = { timerRunning = !timerRunning },
                    onReset = {
                        timerRunning = false
                        timerSeconds = timerMinutes * 60
                    }
                )

                Page.ANALYTICS -> AnalyticsScreen(
                    chapter = chapter,
                    day = day,
                    switchOn = switchOn,
                    doneTasks = tasks.count { it },
                    evidence = evidence,
                    currentDailyMinutes = dailyFrontlineSeconds / 60,
                    onWeekly = { page = Page.WEEKLY },
                    onVision = { page = Page.VISION },
                    onFocusStats = { page = Page.FOCUS_STATS }
                )

                Page.PROFILE -> ProfileScreen(
                    chapter = chapter,
                    evidenceCount = evidence.size,
                    archiveCount = archives.size,
                    onEditChapter = { page = Page.CHAPTER_EDITOR },
                    onVision = { page = Page.VISION },
                    onHistory = { page = Page.HISTORY },
                    onWeekly = { page = Page.WEEKLY },
                    onReminders = { page = Page.REMINDERS },
                    onSwitches = { page = Page.SWITCHES },
                    onBackup = { page = Page.BACKUP },
                    onMethods = { page = Page.METHODS },
                    onLifeSystem = { page = Page.LIFE_SYSTEM }
                )

                Page.MORNING -> MorningStartScreen(
                    chapter = chapter,
                    day = day,
                    mood = mood,
                    switchOn = switchOn,
                    switchActions = switchActions,
                    selectedSwitchAction = selectedSwitchAction,
                    dossierBrief = dossierBrief,
                    onMood = { mood = it },
                    onSelectSwitch = { selectedSwitchAction = it },
                    onSwitch = { switchOn = it },
                    onStart = {
                        dayStarted = true
                        prefs.edit().putBoolean("day_started", true).apply()
                        page = Page.HOME
                    }
                )

                Page.CHECKOUT -> CheckoutScreen(
                    day = day,
                    doneTasks = tasks.count { it },
                    onCancel = { page = Page.CHAPTER },
                    onComplete = { action, result, reflection ->
                        val focusedMinutes = dailyFrontlineSeconds / 60
                        val entry = EvidenceEntry(
                            day = day,
                            action = action,
                            result = result,
                            reflection = reflection,
                            mood = mood,
                            switchDone = switchOn,
                            switchAction = selectedSwitchAction,
                            tasksDone = tasks.count { it },
                            frontlineMinutes = focusedMinutes
                        )
                        evidence.add(0, entry)
                        persistEvidence(prefs, evidence)
                        val finalDay = day >= duration
                        if (!finalDay) day += 1
                        dayStarted = false
                        switchOn = false
                        selectedSwitchAction = ""
                        timerRunning = false
                        timerSeconds = timerMinutes * 60
                        dailyFrontlineSeconds = 0
                        for (i in tasks.indices) {
                            tasks[i] = false
                            prefs.edit().putBoolean("task_$i", false).apply()
                        }
                        updateNovaGlavaWidgets(context)
                        page = if (finalDay) Page.COMPLETE else Page.HOME
                    }
                )

                Page.WEEKLY -> WeeklyReportScreen(
                    chapter = chapter,
                    day = day,
                    evidence = evidence,
                    onBack = { page = Page.ANALYTICS }
                )

                Page.VISION -> VisionScreen(
                    texts = visionTexts,
                    uris = visionUris,
                    onBack = { page = Page.PROFILE },
                    onChanged = { persistVision(prefs, visionTexts, visionUris) }
                )

                Page.HISTORY -> HistoryScreen(
                    archives = archives,
                    onBack = { page = Page.PROFILE }
                )

                Page.COMPLETE -> ChapterCompleteScreen(
                    chapter = chapter,
                    evidence = evidence,
                    onArchive = {
                        snapshotProjectsForCompletedChapter(prefs, chapter.title)
                        val archive = ChapterArchive(
                            title = chapter.title,
                            theme = chapter.theme,
                            duration = chapter.duration,
                            evidenceCount = evidence.size,
                            frontlineMinutes = evidence.sumOf { it.frontlineMinutes },
                            finishedAt = System.currentTimeMillis()
                        )
                        archives.add(0, archive)
                        persistArchives(prefs, archives)
                        resetWeeklyActionsForNewChapter(prefs)
                        evidence.clear()
                        persistEvidence(prefs, evidence)
                        for (i in visionTexts.indices) { visionTexts[i] = ""; visionUris[i] = "" }
                        persistVision(prefs, visionTexts, visionUris)
                        frontlineTaskTexts[0] = ""
                        frontlineTaskTexts[1] = "Завершити одну задачу, яку давно відкладаю"
                        frontlineTaskTexts[2] = "Зробити одну дію, що прямо дає результат"
                        prefs.edit()
                            .putString("frontline_task_0", frontlineTaskTexts[0])
                            .putString("frontline_task_1", frontlineTaskTexts[1])
                            .putString("frontline_task_2", frontlineTaskTexts[2])
                            .apply()
                        title = "Нова глава"
                        theme = ""
                        identity = ""
                        frontline = ""
                        duration = 30
                        result1 = ""
                        result2 = ""
                        result3 = ""
                        day = 1
                        configured = false
                        prefs.edit()
                            .putBoolean("configured", false)
                            .putInt("day", 1)
                            .putString("chapter_title", "Нова глава")
                            .putString("chapter_theme", "")
                            .putString("chapter_identity", "")
                            .putString("chapter_frontline", "")
                            .putString("result_1", "")
                            .putString("result_2", "")
                            .putString("result_3", "")
                            .putInt("duration", 30)
                            .apply()
                        updateNovaGlavaWidgets(context)
                        page = Page.CHAPTER_EDITOR
                    }
                )

                Page.REMINDERS -> ReminderSettingsScreen(
                    morningEnabled = morningReminderEnabled,
                    eveningEnabled = eveningReminderEnabled,
                    morningTime = morningReminderTime,
                    eveningTime = eveningReminderTime,
                    onBack = { page = Page.PROFILE },
                    onSave = { mEnabled, eEnabled, mTime, eTime ->
                        morningReminderEnabled = mEnabled
                        eveningReminderEnabled = eEnabled
                        morningReminderTime = mTime
                        eveningReminderTime = eTime
                        prefs.edit()
                            .putBoolean("reminder_morning_enabled", mEnabled)
                            .putBoolean("reminder_evening_enabled", eEnabled)
                            .putString("reminder_morning_time", mTime)
                            .putString("reminder_evening_time", eTime)
                            .apply()
                        applyReminderSettings(context, mEnabled, eEnabled, mTime, eTime)
                        page = Page.PROFILE
                    }
                )

                Page.SWITCHES -> SwitchActionsScreen(
                    actions = switchActions,
                    onBack = { page = Page.PROFILE },
                    onSave = { values ->
                        values.forEachIndexed { index, value -> switchActions[index] = value }
                        val editor = prefs.edit()
                        switchActions.forEachIndexed { index, value -> editor.putString("switch_action_$index", value) }
                        editor.apply()
                        page = Page.PROFILE
                    }
                )

                Page.BACKUP -> BackupRestoreScreen(
                    prefs = prefs,
                    onBack = { page = Page.PROFILE },
                    onImported = { (context as? Activity)?.recreate() }
                )

                Page.FOCUS_STATS -> FocusAnalyticsScreen(
                    day = day,
                    evidence = evidence,
                    currentDailyMinutes = dailyFrontlineSeconds / 60,
                    onBack = { page = Page.ANALYTICS }
                )

                Page.LIFE_SYSTEM -> LifeSystemScreen(
                    prefs = prefs,
                    onBack = { page = Page.PROFILE }
                )

                Page.METHODS -> MethodsHubScreen(
                    onBack = { page = Page.PROFILE },
                    onDossier = { page = Page.DOSSIER },
                    onFrontlineAudit = { page = Page.FRONTLINE_AUDIT },
                    onDetonator = { page = Page.DETONATOR },
                    onReboot = { page = Page.REBOOT },
                    onVision = { page = Page.VISION }
                )

                Page.DOSSIER -> DossierScreen(
                    prefs = prefs,
                    onBack = { page = Page.METHODS },
                    onSaved = { brief ->
                        dossierBrief = brief
                        page = Page.METHODS
                    }
                )

                Page.FRONTLINE_AUDIT -> FrontlineAuditScreen(
                    prefs = prefs,
                    onBack = { page = Page.METHODS },
                    onUseFrontline = { action ->
                        frontline = action
                        frontlineTaskTexts[0] = action
                        prefs.edit()
                            .putString("chapter_frontline", action)
                            .putString("frontline_task_0", action)
                            .apply()
                        page = Page.FRONTLINE
                    }
                )

                Page.DETONATOR -> DesireDetonatorScreen(
                    prefs = prefs,
                    onBack = { page = Page.METHODS },
                    onUseWish = { wish ->
                        result1 = wish
                        prefs.edit().putString("result_1", wish).apply()
                        page = Page.CHAPTER
                    }
                )

                Page.REBOOT -> RebootScreen(
                    prefs = prefs,
                    onBack = { page = Page.METHODS },
                    onUseSwitch = { action ->
                        switchActions[0] = action
                        selectedSwitchAction = action
                        switchOn = true
                        prefs.edit()
                            .putString("switch_action_0", action)
                            .putString("selected_switch_action", action)
                            .putBoolean("switch_on", true)
                            .apply()
                        page = Page.HOME
                    }
                )

                Page.CHAPTER_EDITOR -> ChapterEditorScreen(
                    initial = chapter,
                    firstRun = !configured,
                    onCancel = { if (configured) page = Page.PROFILE },
                    onSave = { newChapter ->
                        title = newChapter.title
                        theme = newChapter.theme
                        identity = newChapter.identity
                        val oldFrontline = frontline
                        frontline = newChapter.frontline
                        if (frontlineTaskTexts[0].isBlank() || frontlineTaskTexts[0] == oldFrontline) {
                            frontlineTaskTexts[0] = newChapter.frontline
                            prefs.edit().putString("frontline_task_0", newChapter.frontline).apply()
                        }
                        duration = newChapter.duration
                        if (day > newChapter.duration) day = newChapter.duration
                        result1 = newChapter.result1
                        result2 = newChapter.result2
                        result3 = newChapter.result3
                        prefs.edit()
                            .putBoolean("configured", true)
                            .putString("chapter_title", title)
                            .putString("chapter_theme", theme)
                            .putString("chapter_identity", identity)
                            .putString("chapter_frontline", frontline)
                            .putInt("duration", duration)
                            .putString("result_1", result1)
                            .putString("result_2", result2)
                            .putString("result_3", result3)
                            .apply()
                        if (!configured) {
                            day = 1
                            dayStarted = false
                            switchOn = false
                        }
                        configured = true
                        updateNovaGlavaWidgets(context)
                        page = Page.HOME
                    }
                )
            }
        }
    }
}

private fun persistEvidence(prefs: android.content.SharedPreferences, entries: List<EvidenceEntry>) {
    val editor = prefs.edit().putInt("evidence_count", entries.size)
    entries.forEachIndexed { i, entry ->
        editor
            .putInt("evidence_${i}_day", entry.day)
            .putString("evidence_${i}_action", entry.action)
            .putString("evidence_${i}_result", entry.result)
            .putString("evidence_${i}_reflection", entry.reflection)
            .putString("evidence_${i}_mood", entry.mood)
            .putBoolean("evidence_${i}_switch", entry.switchDone)
            .putString("evidence_${i}_switch_action", entry.switchAction)
            .putInt("evidence_${i}_tasks", entry.tasksDone)
            .putInt("evidence_${i}_minutes", entry.frontlineMinutes)
    }
    editor.apply()
}

private fun persistVision(prefs: android.content.SharedPreferences, texts: List<String>, uris: List<String>) {
    val editor = prefs.edit()
    for (i in 0 until minOf(texts.size, uris.size)) {
        editor.putString("vision_${i}_text", texts[i]).putString("vision_${i}_uri", uris[i])
    }
    editor.apply()
}

private fun persistArchives(prefs: android.content.SharedPreferences, entries: List<ChapterArchive>) {
    val editor = prefs.edit().putInt("archive_count", entries.size)
    entries.forEachIndexed { i, entry ->
        editor.putString("archive_${i}_title", entry.title)
            .putString("archive_${i}_theme", entry.theme)
            .putInt("archive_${i}_duration", entry.duration)
            .putInt("archive_${i}_evidence", entry.evidenceCount)
            .putInt("archive_${i}_minutes", entry.frontlineMinutes)
            .putLong("archive_${i}_finished", entry.finishedAt)
    }
    editor.apply()
}

@Composable
private fun HomeScreen(
    chapter: ChapterConfig,
    selfVersion: String,
    day: Int,
    mood: String,
    switchOn: Boolean,
    dayStarted: Boolean,
    doneTasks: Int,
    timerSeconds: Int,
    switchSuggestion: String,
    onMood: (String) -> Unit,
    onSwitch: (Boolean) -> Unit,
    onFrontline: () -> Unit,
    onAnalytics: () -> Unit,
    onChapter: () -> Unit,
    onMorning: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { AppHeader() }
        item { SeasonCard(chapter = chapter, selfVersion = selfVersion, day = day, onClick = onChapter) }
        item {
            MorningStatusCard(
                dayStarted = dayStarted,
                identity = chapter.identity,
                onClick = onMorning
            )
        }
        item { MoodSection(selected = mood, onSelected = onMood) }
        item { SwitchCard(checked = switchOn, onChecked = onSwitch, suggestion = switchSuggestion) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Передова",
                    subtitle = if (timerSeconds < 45 * 60) "Таймер ${formatTime(timerSeconds)}" else "$doneTasks із 3 дій виконано",
                    icon = Icons.Outlined.Flag,
                    tint = LilacStrong,
                    background = Lilac,
                    onClick = onFrontline
                )
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Результати",
                    subtitle = "Evidence і прогрес",
                    icon = Icons.Outlined.BarChart,
                    tint = MintDark,
                    background = Mint,
                    onClick = onAnalytics
                )
            }
        }
        item { FocusSection() }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun AppHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Mint),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.LocalFlorist, contentDescription = null, tint = MintDark)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text("Нова глава", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("Твій простір для змін", fontSize = 12.sp, color = Muted)
        }
        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = LilacStrong)
    }
}

@Composable
private fun SeasonCard(chapter: ChapterConfig, selfVersion: String, day: Int, onClick: () -> Unit) {
    val progress = day / chapter.duration.toFloat()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Mint)
    ) {
        Box(Modifier.fillMaxSize()) {
            MountainScene()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xAA284438)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(chapter.title, color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                        Text(chapter.theme, color = Color.White.copy(alpha = 0.92f), fontSize = 16.sp)
                    }
                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.92f), modifier = Modifier.size(46.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.ArrowForwardIos, null, tint = Ink, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("День $day із ${chapter.duration}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text("${(progress * 100).toInt()}%", color = Color.White.copy(alpha = 0.9f))
                }
                Spacer(Modifier.height(7.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(CircleShape),
                    color = Color(0xFFF4D87A),
                    trackColor = Color.White.copy(alpha = 0.35f)
                )
            }
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White.copy(alpha = 0.88f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocalFlorist, null, tint = MintDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text("Я " + selfVersion, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Ink)
                        Text("Сезон · ${chapter.duration} днів", fontSize = 11.sp, color = Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun MountainScene() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color(0xFFE8E1FF), Color(0xFFFFE8CE), Color(0xFFE4F2E4))))
        drawCircle(color = Color(0xFFFFD889), radius = size.minDimension * 0.09f, center = Offset(size.width * 0.77f, size.height * 0.34f))

        val far = Path().apply {
            moveTo(0f, size.height * 0.55f)
            lineTo(size.width * 0.18f, size.height * 0.40f)
            lineTo(size.width * 0.33f, size.height * 0.52f)
            lineTo(size.width * 0.52f, size.height * 0.31f)
            lineTo(size.width * 0.72f, size.height * 0.52f)
            lineTo(size.width, size.height * 0.37f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(far, Color(0xFF8FA8A2).copy(alpha = 0.55f))

        val near = Path().apply {
            moveTo(0f, size.height * 0.70f)
            lineTo(size.width * 0.22f, size.height * 0.55f)
            lineTo(size.width * 0.40f, size.height * 0.67f)
            lineTo(size.width * 0.58f, size.height * 0.50f)
            lineTo(size.width * 0.77f, size.height * 0.64f)
            lineTo(size.width, size.height * 0.53f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(near, Color(0xFF547565).copy(alpha = 0.78f))

        repeat(16) { i ->
            val x = size.width * (i / 15f)
            val y = size.height * (0.63f + (i % 3) * 0.025f)
            drawCircle(Color.White.copy(alpha = 0.55f), radius = 4f, center = Offset(x, y))
        }
    }
}

@Composable
private fun MorningStatusCard(dayStarted: Boolean, identity: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = if (dayStarted) Color.White else Peach),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (dayStarted) Mint else Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(if (dayStarted) Icons.Outlined.CheckCircle else Icons.Outlined.WbSunny, null, tint = if (dayStarted) MintDark else Color(0xFFA66C22))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(if (dayStarted) "День запущено" else "Ранковий запуск", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(if (dayStarted) identity else "1–2 хвилини: роль → стан → SWITCH → Передова", color = Muted, fontSize = 11.sp, maxLines = 2, lineHeight = 15.sp)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = Muted)
        }
    }
}

private data class MoodItem(val label: String, val icon: ImageVector, val bg: Color, val tint: Color)

@Composable
private fun MoodSection(selected: String, onSelected: (String) -> Unit) {
    val moods = listOf(
        MoodItem("Спокійна", Icons.Outlined.Mood, Mint, MintDark),
        MoodItem("Мотивована", Icons.Outlined.LocalFlorist, Color(0xFFE9F5E9), MintDark),
        MoodItem("Продуктивна", Icons.Outlined.WbSunny, Peach, Color(0xFFA66C22)),
        MoodItem("Вдячна", Icons.Outlined.FavoriteBorder, Rose, Color(0xFFB86B7B))
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Я сьогодні", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.weight(1f))
            Text("Обери стан", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            moods.forEach { item ->
                val isSelected = selected == item.label
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) item.bg else Color.White)
                        .clickable { onSelected(item.label) }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(item.icon, null, tint = item.tint, modifier = Modifier.size(23.dp))
                    Spacer(Modifier.height(7.dp))
                    Text(item.label, fontSize = 10.sp, color = if (isSelected) Ink else Muted, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun SwitchCard(checked: Boolean, onChecked: (Boolean) -> Unit, suggestion: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Mint),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Outlined.AutoAwesome, null, tint = MintStrong) }
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text("SWITCH", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text(if (checked) "Сьогоднішня дія прийнята" else "Обери одну дію проти старого автопілота", fontSize = 12.sp, color = Muted)
                }
                Switch(
                    checked = checked,
                    onCheckedChange = onChecked,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MintStrong, uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFC8D3CC))
                )
            }
            if (checked) {
                Spacer(Modifier.height(10.dp))
                Surface(shape = RoundedCornerShape(15.dp), color = Color.White.copy(alpha = 0.66f)) {
                    Text("Сьогодні: $suggestion — почати до того, як з’явиться бажання відкласти.", modifier = Modifier.padding(12.dp), color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    background: Color,
    onClick: () -> Unit
) {
    Card(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = background)) {
        Column(Modifier.padding(15.dp)) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(11.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Ink)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, fontSize = 11.sp, color = Muted, lineHeight = 15.sp)
            Spacer(Modifier.height(12.dp))
            Icon(Icons.Outlined.ChevronRight, null, tint = tint, modifier = Modifier.align(Alignment.End))
        }
    }
}

private data class FocusItem(val title: String, val subtitle: String, val icon: ImageVector, val bg: Color)

@Composable
private fun FocusSection() {
    val items = listOf(
        FocusItem("Ранковий запуск", "1–2 хвилини", Icons.Outlined.WbSunny, Peach),
        FocusItem("Передова", "Фокус-сесія", Icons.Outlined.Flag, Mint),
        FocusItem("Check-out", "Факт результату", Icons.Outlined.Spa, Lilac)
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ритм дня", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.weight(1f))
            Text("3 опори", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = item.bg)) {
                    Column(Modifier.padding(11.dp)) {
                        Icon(item.icon, null, tint = Ink, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.height(18.dp))
                        Text(item.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Ink, lineHeight = 14.sp)
                        Text(item.subtitle, fontSize = 9.sp, color = Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun MorningStartScreen(
    chapter: ChapterConfig,
    day: Int,
    mood: String,
    switchOn: Boolean,
    switchActions: List<String>,
    selectedSwitchAction: String,
    dossierBrief: String,
    onMood: (String) -> Unit,
    onSelectSwitch: (String) -> Unit,
    onSwitch: (Boolean) -> Unit,
    onStart: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionHeader("Запуск дня $day", "Не планер. Коротке перемикання в поточну главу.") }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Lilac)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Ким я є в цій главі", color = LilacStrong, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(chapter.identity, fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
                }
            }
        }
        if (dossierBrief.isNotBlank()) {
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Вижимка Я 2.0", color = MintDark, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(dossierBrief, color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
                    }
                }
            }
        }
        item { MoodSection(selected = mood, onSelected = onMood) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("SWITCH на сьогодні", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Text("обери 1 дію", color = Muted, fontSize = 11.sp)
                }
                switchActions.filter { it.isNotBlank() }.forEach { action ->
                    val selected = action == selectedSwitchAction
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onSelectSwitch(action); onSwitch(true) },
                        shape = RoundedCornerShape(17.dp),
                        colors = CardDefaults.cardColors(containerColor = if (selected) Mint else Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = selected, onClick = { onSelectSwitch(action); onSwitch(true) })
                            Spacer(Modifier.width(5.dp))
                            Text(action, modifier = Modifier.weight(1f), lineHeight = 19.sp, color = Ink)
                        }
                    }
                }
            }
        }
        item { SwitchCard(checked = switchOn, onChecked = onSwitch, suggestion = selectedSwitchAction.ifBlank { chapter.frontline }) }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Передова сьогодні", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(7.dp))
                    Text(chapter.frontline, color = Ink, fontSize = 16.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("Результат сезону: ${chapter.result1}", color = Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text("Почати день", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChapterScreen(
    chapter: ChapterConfig,
    selfVersion: String,
    day: Int,
    switchOn: Boolean,
    dayStarted: Boolean,
    doneTasks: Int,
    onMorning: () -> Unit,
    onCheckout: () -> Unit,
    onEdit: () -> Unit
) {
    val progress = day / chapter.duration.toFloat()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { SectionHeader("Поточна глава", "${chapter.title} · ${chapter.theme}") }
                IconButton(onClick = onEdit) { Icon(Icons.Outlined.Edit, "Редагувати") }
            }
        }
        item { SeasonCard(chapter = chapter, selfVersion = selfVersion, day = day, onClick = {}) }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Я в цій главі", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(chapter.identity, color = Ink, lineHeight = 20.sp)
                    Spacer(Modifier.height(14.dp))
                    MetricRow("Прогрес сезону", "${(progress * 100).toInt()}%")
                    MetricRow("Передова", "$doneTasks / 3")
                    MetricRow("SWITCH", if (switchOn) "прийнято" else "ще ні")
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Результати, які мають стати нормою", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                listOf(chapter.result1, chapter.result2, chapter.result3).filter { it.isNotBlank() }.forEach { result ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = MintDark, modifier = Modifier.size(19.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(result, color = Ink, modifier = Modifier.weight(1f), lineHeight = 19.sp)
                    }
                }
            }
        }
        item {
            if (!dayStarted) {
                Button(onClick = onMorning, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = LilacStrong)) {
                    Icon(Icons.Outlined.WbSunny, null)
                    Spacer(Modifier.width(9.dp))
                    Text("Запустити день", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MintDark)) {
                    Icon(Icons.Outlined.CheckCircle, null)
                    Spacer(Modifier.width(9.dp))
                    Text("Вечірній check-out", fontWeight = FontWeight.Bold)
                }
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun FrontlineScreen(
    chapter: ChapterConfig,
    tasks: List<Boolean>,
    taskTexts: List<String>,
    timerMinutes: Int,
    timerSeconds: Int,
    timerRunning: Boolean,
    dailyFrontlineMinutes: Int,
    onToggle: (Int, Boolean) -> Unit,
    onTaskText: (Int, String) -> Unit,
    onMinutes: (Int) -> Unit,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeader("Передова", "Те, що прямо рухає результат") }
        item {
            Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Lilac)) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, null, tint = LilacStrong)
                        Spacer(Modifier.width(8.dp))
                        Text("Фокус-сесія", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    Text(formatTime(timerSeconds), fontSize = 50.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text("Сьогодні накопичено: $dailyFrontlineMinutes хв", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(25, 45, 60).forEach { minutes ->
                            FilterChip(selected = timerMinutes == minutes, onClick = { onMinutes(minutes) }, label = { Text("$minutes хв") })
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onStartPause, colors = ButtonDefaults.buttonColors(containerColor = MintDark), shape = RoundedCornerShape(16.dp)) {
                            Icon(if (timerRunning) Icons.Outlined.Pause else Icons.Outlined.PlayArrow, null)
                            Spacer(Modifier.width(6.dp))
                            Text(if (timerRunning) "Пауза" else "Старт")
                        }
                        OutlinedButton(onClick = onReset, shape = RoundedCornerShape(16.dp)) {
                            Icon(Icons.Outlined.Refresh, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Скинути")
                        }
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Головна Передова", color = MintDark, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(5.dp))
                    Text(chapter.frontline, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 27.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("Не зайнятість. Результат.", color = Muted)
                }
            }
        }
        item { Text("Дії Передової на сьогодні", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
        itemsIndexed(taskTexts) { index, itemTitle ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = tasks.getOrElse(index) { false }, onCheckedChange = { onToggle(index, it) }, colors = CheckboxDefaults.colors(checkedColor = MintDark))
                    Spacer(Modifier.width(4.dp))
                    OutlinedTextField(
                        value = itemTitle,
                        onValueChange = { onTaskText(index, it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Конкретна дія Передової") },
                        minLines = 1,
                        maxLines = 3
                    )
                }
            }
        }
        item {
            val done = tasks.count { it }
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Peach)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.TaskAlt, null, tint = Color(0xFFA66C22), modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("$done із 3 виконано", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Увечері зафіксуй не галочку, а те, що змінилося в реальності.", color = Muted, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun CheckoutScreen(
    day: Int,
    doneTasks: Int,
    onCancel: () -> Unit,
    onComplete: (String, String, String) -> Unit
) {
    var action by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }
    var reflection by rememberSaveable { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Check-out · День $day", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Evidence: дія → факт результату", color = Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Flag, null, tint = MintDark)
                    Spacer(Modifier.width(10.dp))
                    Text("Передова сьогодні: $doneTasks із 3 дій", fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                label = { Text("Що я реально зробив?") },
                placeholder = { Text("Напр.: опублікував 5 сторінок") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
        item {
            OutlinedTextField(
                value = result,
                onValueChange = { result = it },
                label = { Text("Що змінилося в реальності?") },
                placeholder = { Text("Напр.: 2 заявки / +37 переходів / завершив задачу") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
        item {
            OutlinedTextField(
                value = reflection,
                onValueChange = { reflection = it },
                label = { Text("Що сьогодні було не схоже на старого мене?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
        item {
            Button(
                onClick = { onComplete(action.trim(), result.trim(), reflection.trim()) },
                enabled = action.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Зберегти й завершити день", fontWeight = FontWeight.Bold)
            }
        }
        item { Text("Достатньо одного факту. Мета — накопичувати докази того, які дії справді працюють.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp) }
    }
}

@Composable
private fun AnalyticsScreen(
    chapter: ChapterConfig,
    day: Int,
    switchOn: Boolean,
    doneTasks: Int,
    evidence: List<EvidenceEntry>,
    currentDailyMinutes: Int,
    onWeekly: () -> Unit,
    onVision: () -> Unit,
    onFocusStats: () -> Unit
) {
    val seasonProgress = day / chapter.duration.toFloat()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeader("Результати", "Факти замість відчуття, що нічого не змінюється") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard(Modifier.weight(1f), "7 днів", "Тижневий звіт", Icons.Outlined.Insights, LilacStrong, Lilac, onWeekly)
                ActionCard(Modifier.weight(1f), "Фокус", "Графік Передової", Icons.Outlined.ShowChart, MintDark, Mint, onFocusStats)
            }
        }
        item {
            ActionCard(Modifier.fillMaxWidth(), "Vision", "Образ нормального життя цієї глави", Icons.Outlined.PhotoLibrary, LilacStrong, Lilac, onVision)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), "${(seasonProgress * 100).toInt()}%", "сезону", Mint)
                StatCard(Modifier.weight(1f), "$doneTasks/3", "передова", Lilac)
                StatCard(Modifier.weight(1f), evidence.size.toString(), "evidence", Peach)
            }
        }
        item {
            val recent = evidence.filter { it.day in (day - 6).coerceAtLeast(1)..day }
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder(), modifier = Modifier.clickable(onClick = onFocusStats)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Фокус Передової · 7 днів", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, null, tint = Muted)
                    }
                    Spacer(Modifier.height(12.dp))
                    MiniFocusBars(day = day, evidence = recent, range = 7, currentDailyMinutes = currentDailyMinutes)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Карта сезону", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(14.dp))
                    DayGrid(day = day, duration = chapter.duration)
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Analytics, null, tint = MintDark)
                Spacer(Modifier.width(9.dp))
                Text("Evidence", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                Spacer(Modifier.weight(1f))
                Text(if (switchOn) "SWITCH активний" else "", color = MintDark, fontSize = 11.sp)
            }
        }
        if (evidence.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                    Text("Перший запис з’явиться після вечірнього check-out: «що зробив → що отримав».", modifier = Modifier.padding(18.dp), color = Muted, lineHeight = 18.sp)
                }
            }
        } else {
            items(evidence.take(12)) { entry -> EvidenceCard(entry) }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun EvidenceCard(entry: EvidenceEntry) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("День ${entry.day}", color = MintDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                if (entry.frontlineMinutes > 0) Text("${entry.frontlineMinutes} хв Передової", color = Muted, fontSize = 10.sp)
            }
            if (entry.switchDone && entry.switchAction.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text("SWITCH · ${entry.switchAction}", color = LilacStrong, fontSize = 10.sp, lineHeight = 14.sp)
            }
            Spacer(Modifier.height(7.dp))
            Text(entry.action, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 21.sp)
            if (entry.result.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(shape = RoundedCornerShape(14.dp), color = Mint) {
                    Column(Modifier.padding(11.dp)) {
                        Text("Результат", color = Muted, fontSize = 10.sp)
                        Text(entry.result, color = Ink, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }
            if (entry.reflection.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Анти-автопілот: ${entry.reflection}", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun DayGrid(day: Int, duration: Int) {
    val columns = 6
    val rows = (duration + columns - 1) / columns
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in 0 until rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (col in 0 until columns) {
                    val number = row * columns + col + 1
                    if (number <= duration) {
                        val completed = number < day
                        val current = number == day
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(when {
                                    completed -> MintDark
                                    current -> Sun
                                    else -> Color(0xFFF0F3EF)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            if (completed) Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(17.dp))
                            else Text(number.toString(), color = if (current) Ink else Muted, fontWeight = if (current) FontWeight.Bold else FontWeight.Normal)
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    chapter: ChapterConfig,
    evidenceCount: Int,
    archiveCount: Int,
    onEditChapter: () -> Unit,
    onVision: () -> Unit,
    onHistory: () -> Unit,
    onWeekly: () -> Unit,
    onReminders: () -> Unit,
    onSwitches: () -> Unit,
    onBackup: () -> Unit,
    onMethods: () -> Unit,
    onLifeSystem: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeader("Профіль", "MVP без реєстрації — усе зберігається на цьому телефоні") }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.PersonOutline, null, tint = MintDark, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(chapter.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(chapter.theme, color = Muted)
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column {
                    SettingsRow(Icons.Outlined.Spa, "Моя система", "Герой · здоров’я · KDP/PFU · тиждень · міні-пригоди", onLifeSystem)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.AutoAwesome, "Метод глави", "Я 2.0 · Передова · Детонатор · Перепрошивка · Фото", onMethods)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.Edit, "Налаштувати главу", "Роль, Передова, результати й тривалість", onEditChapter)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.Tune, "SWITCH-дії", "Власний набір дій проти автопілота", onSwitches)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.Notifications, "Нагадування", "Ранковий запуск і вечірній check-out", onReminders)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.Analytics, "Evidence", "$evidenceCount записів накопичено", onWeekly)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.PhotoLibrary, "Vision Board", "Фото й образи поточної глави", onVision)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.History, "Архів глав", "$archiveCount завершених глав", onHistory)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.Backup, "Резервна копія", "Експорт і відновлення даних у JSON", onBackup)
                    HorizontalDivider(color = CardBorder)
                    SettingsRow(Icons.Outlined.CalendarMonth, "Віджет Android", "День і Передова прямо на головному екрані", {})
                }
            }
        }
        item {
            Text("Версія 0.9.0 · офлайн-first", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ChapterEditorScreen(initial: ChapterConfig, firstRun: Boolean, onCancel: () -> Unit, onSave: (ChapterConfig) -> Unit) {
    var title by rememberSaveable { mutableStateOf(initial.title) }
    var theme by rememberSaveable { mutableStateOf(initial.theme) }
    var identity by rememberSaveable { mutableStateOf(initial.identity) }
    var frontline by rememberSaveable { mutableStateOf(initial.frontline) }
    var duration by rememberSaveable { mutableIntStateOf(initial.duration) }
    var result1 by rememberSaveable { mutableStateOf(initial.result1) }
    var result2 by rememberSaveable { mutableStateOf(initial.result2) }
    var result3 by rememberSaveable { mutableStateOf(initial.result3) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!firstRun) IconButton(onClick = onCancel) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text(if (firstRun) "Створи свою главу" else "Редагувати главу", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Не просто ціль. Опиши сезон, у якому ти живеш і дієш інакше.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Column(Modifier.padding(16.dp)) {
                    Text("1 · Назва сезону", color = MintDark, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Назва") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = theme, onValueChange = { theme = it }, label = { Text("Тема") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(Modifier.height(12.dp))
                    Text("Тривалість", fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(30, 60, 90).forEach { days -> FilterChip(selected = duration == days, onClick = { duration = days }, label = { Text("$days днів") }) }
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Lilac)) {
                Column(Modifier.padding(16.dp)) {
                    Text("2 · Ким я є", color = LilacStrong, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(9.dp))
                    OutlinedTextField(value = identity, onValueChange = { identity = it }, label = { Text("Опис нової версії себе") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Peach)) {
                Column(Modifier.padding(16.dp)) {
                    Text("3 · Моя Передова", color = Color(0xFFA66C22), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(9.dp))
                    OutlinedTextField(value = frontline, onValueChange = { frontline = it }, label = { Text("Дія, яка прямо рухає результат") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(16.dp)) {
                    Text("4 · Що має стати нормою", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(9.dp))
                    OutlinedTextField(value = result1, onValueChange = { result1 = it }, label = { Text("Результат 1") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = result2, onValueChange = { result2 = it }, label = { Text("Результат 2") }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = result3, onValueChange = { result3 = it }, label = { Text("Результат 3") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        item {
            Button(
                onClick = { onSave(ChapterConfig(title.trim(), theme.trim(), identity.trim(), frontline.trim(), duration, result1.trim(), result2.trim(), result3.trim())) },
                enabled = title.isNotBlank() && theme.isNotBlank() && identity.isNotBlank() && frontline.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text(if (firstRun) "Почати главу" else "Зберегти зміни", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun WeeklyReportScreen(chapter: ChapterConfig, day: Int, evidence: List<EvidenceEntry>, onBack: () -> Unit) {
    val fromDay = (day - 6).coerceAtLeast(1)
    val week = evidence.filter { it.day in fromDay..day }
    val focus = week.sumOf { it.frontlineMinutes }
    val switchDays = week.count { it.switchDone }
    val avgTasks = if (week.isEmpty()) 0f else week.map { it.tasksDone }.average().toFloat()
    val withResult = week.count { it.result.isNotBlank() }
    val insight = when {
        week.isEmpty() -> "Заверши хоча б один день через check-out — тоді звіт почне бачити закономірності."
        focus < 60 -> "Передова поки отримує мало захищеного часу. Наступного тижня спробуй поставити хоча б одну 25–45 хв фокус-сесію на день."
        switchDays < maxOf(1, week.size / 2) -> "SWITCH спрацьовує не щодня. Краще одна маленька незвична дія, ніж складний ритуал, який відкладається."
        withResult < maxOf(1, week.size / 2) -> "Дій уже достатньо, але результат часто не зафіксований. Формулюй Evidence так, щоб було видно зміну в реальності."
        else -> "Ритм стабілізується: є Передова, SWITCH і вимірювані результати. Наступний крок — прибрати одну дію, що забирає час і не дає Evidence."
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Тижневий звіт", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Дні $fromDay–$day · ${chapter.title}", color = Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), "$focus хв", "Передова", Mint)
                StatCard(Modifier.weight(1f), "$switchDays/${week.size}", "SWITCH", Lilac)
                StatCard(Modifier.weight(1f), String.format("%.1f", avgTasks), "дій/день", Peach)
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Lilac)) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Insights, null, tint = LilacStrong)
                        Spacer(Modifier.width(8.dp))
                        Text("Що видно цього тижня", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(insight, color = Ink, lineHeight = 20.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(16.dp)) {
                    MetricRow("Днів із check-out", week.size.toString())
                    MetricRow("Evidence з результатом", "$withResult/${week.size}")
                    MetricRow("Фокус Передової", "$focus хв")
                    MetricRow("SWITCH виконано", "$switchDays дн.")
                }
            }
        }
        if (week.isNotEmpty()) {
            item { Text("Останні докази", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
            items(week.take(7)) { EvidenceCard(it) }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun VisionScreen(texts: MutableList<String>, uris: MutableList<String>, onBack: () -> Unit, onChanged: () -> Unit) {
    val context = LocalContext.current
    var selectedSlot by remember { mutableIntStateOf(-1) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null && selectedSlot in 0 until uris.size) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
            uris[selectedSlot] = uri.toString()
            onChanged()
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Норма нового рівня · Фото", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("5 сфер із матеріалів автора: ти всередині бажаного життя. Спокійний, буденний кадр — не предмет сам по собі.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        items(6) { index ->
            val sphereNames = listOf("Стосунки", "Простір", "Предмети нового рівня", "Подорожі", "Я у своїй справі", "Свій кадр")
            val sphereHints = listOf(
                "Теплий звичайний момент із близькими",
                "Ти реально живеш або працюєш у цьому просторі",
                "Головний у кадрі — ти; річ просто давно твоя",
                "Ти не турист біля пам’ятки, а живеш моментом",
                "Найважливіший кадр: твоя діяльність у новому масштабі",
                "Будь-яка інша сцена, яка має стати буденною нормою"
            )
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = if (index % 2 == 0) Mint else Lilac)) {
                Column(Modifier.padding(14.dp)) {
                    Text("${index + 1} · ${sphereNames[index]}", fontWeight = FontWeight.Bold, color = if (index % 2 == 0) MintDark else LilacStrong)
                    Text(sphereHints[index], color = Muted, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    if (uris[index].isNotBlank()) {
                        AndroidView(
                            factory = { ctx -> ImageView(ctx).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                            update = { it.setImageURI(Uri.parse(uris[index])) },
                            modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(18.dp))
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    OutlinedTextField(
                        value = texts[index],
                        onValueChange = { texts[index] = it; onChanged() },
                        label = { Text("Опиши сцену") },
                        placeholder = { Text("Що відбувається, де ти, з ким, що робиш — як звичайний день") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { selectedSlot = index; picker.launch(arrayOf("image/*")) }) {
                            Icon(Icons.Outlined.AddPhotoAlternate, null)
                            Spacer(Modifier.width(6.dp))
                            Text(if (uris[index].isBlank()) "Додати фото" else "Замінити")
                        }
                        OutlinedButton(onClick = { copyText(context, "Vision prompt", buildVisionPrompt(index, texts[index])) }) {
                            Icon(Icons.Outlined.AutoAwesome, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Prompt")
                        }
                        if (uris[index].isNotBlank() || texts[index].isNotBlank()) {
                            TextButton(onClick = { uris[index] = ""; texts[index] = ""; onChanged() }) {
                                Icon(Icons.Outlined.DeleteOutline, null)
                                Spacer(Modifier.width(4.dp))
                                Text("Очистити")
                            }
                        }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun HistoryScreen(archives: List<ChapterArchive>, onBack: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Архів глав", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Не streak. Історія завершених періодів життя.", color = Muted, fontSize = 12.sp)
                }
            }
        }
        if (archives.isEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                    Text("Після завершення першої глави тут збережеться її підсумок: тривалість, Evidence та час Передової.", modifier = Modifier.padding(18.dp), color = Muted, lineHeight = 19.sp)
                }
            }
        } else {
            items(archives) { item ->
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                    Column(Modifier.padding(17.dp)) {
                        Text(item.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(item.theme, color = Muted)
                        Spacer(Modifier.height(12.dp))
                        MetricRow("Тривалість", "${item.duration} днів")
                        MetricRow("Evidence", item.evidenceCount.toString())
                        MetricRow("Передова", "${item.frontlineMinutes} хв")
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun ChapterCompleteScreen(chapter: ChapterConfig, evidence: List<EvidenceEntry>, onArchive: () -> Unit) {
    val focus = evidence.sumOf { it.frontlineMinutes }
    val switchDays = evidence.count { it.switchDone }
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionHeader("Глава завершена", "${chapter.title} · ${chapter.duration} днів") }
        item {
            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Column(Modifier.padding(20.dp)) {
                    Icon(Icons.Outlined.AutoAwesome, null, tint = MintDark, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.height(10.dp))
                    Text(chapter.theme, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Тепер це не намір, а збережена частина твоєї історії.", color = Muted, lineHeight = 19.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), evidence.size.toString(), "Evidence", Lilac)
                StatCard(Modifier.weight(1f), "$focus хв", "Передова", Peach)
                StatCard(Modifier.weight(1f), "$switchDays", "SWITCH", Mint)
            }
        }
        if (evidence.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                    Column(Modifier.padding(17.dp)) {
                        Text("Останній доказ", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(7.dp))
                        Text(evidence.first().action, fontWeight = FontWeight.SemiBold)
                        if (evidence.first().result.isNotBlank()) Text(evidence.first().result, color = Muted, modifier = Modifier.padding(top = 5.dp))
                    }
                }
            }
        }
        item {
            Button(onClick = onArchive, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MintDark)) {
                Icon(Icons.Outlined.Collections, null)
                Spacer(Modifier.width(8.dp))
                Text("Зберегти главу й почати нову", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ReminderSettingsScreen(
    morningEnabled: Boolean,
    eveningEnabled: Boolean,
    morningTime: String,
    eveningTime: String,
    onBack: () -> Unit,
    onSave: (Boolean, Boolean, String, String) -> Unit
) {
    val context = LocalContext.current
    var morningOn by rememberSaveable { mutableStateOf(morningEnabled) }
    var eveningOn by rememberSaveable { mutableStateOf(eveningEnabled) }
    var morning by rememberSaveable { mutableStateOf(morningTime) }
    var evening by rememberSaveable { mutableStateOf(eveningTime) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    val validMorning = !morningOn || parseReminderTime(morning) != null
    val validEvening = !eveningOn || parseReminderTime(evening) != null

    fun requestNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Нагадування", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Системні Android-сповіщення працюють, навіть коли застосунок закритий.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        item {
            ReminderTimeCard(
                title = "Ранковий запуск",
                subtitle = "Роль → стан → SWITCH → Передова",
                enabled = morningOn,
                time = morning,
                valid = validMorning,
                icon = Icons.Outlined.WbSunny,
                bg = Peach,
                onEnabled = { value -> morningOn = value; if (value) requestNotificationsIfNeeded() },
                onTime = { morning = it }
            )
        }
        item {
            ReminderTimeCard(
                title = "Вечірній check-out",
                subtitle = "Дія → результат → Evidence",
                enabled = eveningOn,
                time = evening,
                valid = validEvening,
                icon = Icons.Outlined.NightsStay,
                bg = Lilac,
                onEnabled = { value -> eveningOn = value; if (value) requestNotificationsIfNeeded() },
                onTime = { evening = it }
            )
        }
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Text("Час вводиться у форматі 24 годин, наприклад 08:30 або 21:00. Android може доставити системне нагадування з невеликим відхиленням для економії батареї.", modifier = Modifier.padding(15.dp), color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
        item {
            Button(
                onClick = { onSave(morningOn, eveningOn, morning.trim(), evening.trim()) },
                enabled = validMorning && validEvening,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Зберегти нагадування", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ReminderTimeCard(
    title: String,
    subtitle: String,
    enabled: Boolean,
    time: String,
    valid: Boolean,
    icon: ImageVector,
    bg: Color,
    onEnabled: (Boolean) -> Unit,
    onTime: (String) -> Unit
) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = bg)) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Ink, modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(subtitle, color = Muted, fontSize = 11.sp)
                }
                Switch(checked = enabled, onCheckedChange = onEnabled, colors = SwitchDefaults.colors(checkedTrackColor = MintStrong))
            }
            if (enabled) {
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = time,
                    onValueChange = { onTime(formatReminderInput(it)) },
                    label = { Text("Час · HH:MM") },
                    isError = !valid,
                    supportingText = { if (!valid) Text("Наприклад 08:30") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}

@Composable
private fun SwitchActionsScreen(actions: List<String>, onBack: () -> Unit, onSave: (List<String>) -> Unit) {
    val drafts = remember { mutableStateListOf<String>().apply { addAll(actions) } }
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("SWITCH-дії", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Твій власний список коротких дій проти звичного автопілота.", color = Muted, fontSize = 12.sp)
                }
            }
        }
        items(6) { index ->
            OutlinedTextField(
                value = drafts[index],
                onValueChange = { drafts[index] = it },
                label = { Text("SWITCH ${index + 1}") },
                placeholder = { Text("Конкретна дія на 5–30 хвилин") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
        }
        item {
            Button(
                onClick = { onSave(drafts.toList()) },
                enabled = drafts.any { it.isNotBlank() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.width(8.dp))
                Text("Зберегти SWITCH-дії", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun FocusAnalyticsScreen(
    day: Int,
    evidence: List<EvidenceEntry>,
    currentDailyMinutes: Int,
    onBack: () -> Unit
) {
    var range by rememberSaveable { mutableIntStateOf(7) }
    val from = (day - range + 1).coerceAtLeast(1)
    val historical = evidence.filter { it.day in from..day }
    val completedToday = historical.filter { it.day == day }.sumOf { it.frontlineMinutes }
    val effectiveToday = maxOf(completedToday, currentDailyMinutes)
    val total = historical.filter { it.day != day }.sumOf { it.frontlineMinutes } + effectiveToday
    val activeDays = ((from..day).count { d -> historical.any { it.day == d && it.frontlineMinutes > 0 } || (d == day && effectiveToday > 0) }).coerceAtLeast(1)
    val best = maxOf(historical.maxOfOrNull { it.frontlineMinutes } ?: 0, effectiveToday)
    val average = if (total == 0) 0 else total / activeDays

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Фокус Передової", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Не загальний екранний час, а хвилини роботи, що прямо рухають результат.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7, 14, 30).forEach { days ->
                    FilterChip(selected = range == days, onClick = { range = days }, label = { Text("$days днів") })
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(Modifier.weight(1f), "$total хв", "разом", Mint)
                StatCard(Modifier.weight(1f), "$average хв", "середнє", Lilac)
                StatCard(Modifier.weight(1f), "$best хв", "найкращий", Peach)
            }
        }
        item {
            Card(shape = RoundedCornerShape(25.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(18.dp)) {
                    Text("Дні $from–$day", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Spacer(Modifier.height(15.dp))
                    MiniFocusBars(day = day, evidence = evidence, range = range, currentDailyMinutes = currentDailyMinutes)
                    Spacer(Modifier.height(10.dp))
                    Text("Жовтий стовпчик — сьогодні. Висота показує хвилини Передової за день.", color = Muted, fontSize = 11.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = if (average >= 45) Mint else Peach)) {
                Text(
                    when {
                        total == 0 -> "Почни першу фокус-сесію. Графік будується лише з реально відпрацьованого часу."
                        average < 25 -> "Фокус поки короткий. Спробуй захистити хоча б одну 25-хвилинну сесію Передової в активний день."
                        average < 45 -> "Ритм уже формується. Наступна сходинка — стабільна 45-хвилинна сесія без перемикань."
                        else -> "Передова вже отримує помітний обсяг часу. Тепер дивись, які саме сесії створюють Evidence, а не лише години."
                    },
                    modifier = Modifier.padding(17.dp),
                    color = Ink,
                    lineHeight = 20.sp
                )
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun MiniFocusBars(
    day: Int,
    evidence: List<EvidenceEntry>,
    range: Int,
    currentDailyMinutes: Int = 0
) {
    val from = (day - range + 1).coerceAtLeast(1)
    val days = (from..day).toList()
    val values = days.map { d ->
        val saved = evidence.filter { it.day == d }.sumOf { it.frontlineMinutes }
        if (d == day) maxOf(saved, currentDailyMinutes) else saved
    }
    val max = maxOf(1, values.maxOrNull() ?: 1)
    Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
        val gap = if (values.size <= 7) 8f else 4f
        val slot = size.width / values.size.coerceAtLeast(1)
        val barWidth = (slot - gap).coerceAtLeast(3f)
        drawRect(color = CardBorder, topLeft = Offset(0f, size.height - 2f), size = androidx.compose.ui.geometry.Size(size.width, 2f))
        values.forEachIndexed { index, minutes ->
            val ratio = minutes / max.toFloat()
            val barHeight = (size.height - 12f) * ratio
            val x = index * slot + (slot - barWidth) / 2f
            val y = size.height - barHeight
            drawRect(
                color = if (days[index] == day) Sun else MintStrong,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight.coerceAtLeast(if (minutes > 0) 4f else 0f))
            )
        }
    }
    Row(Modifier.fillMaxWidth()) {
        Text("$from", color = Muted, fontSize = 10.sp)
        Spacer(Modifier.weight(1f))
        Text("день $day", color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun BackupRestoreScreen(prefs: SharedPreferences, onBack: () -> Unit, onImported: () -> Unit) {
    val context = LocalContext.current
    var status by rememberSaveable { mutableStateOf("") }
    val timestamp = remember { SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date()) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                    writer.write(preferencesToBackupJson(prefs).toString(2))
                }
                status = "Резервну копію збережено."
            } catch (e: Exception) {
                status = "Не вдалося зберегти: ${e.message ?: "помилка"}"
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: error("Порожній файл")
                restorePreferencesFromBackup(prefs, JSONObject(text))
                applyReminderSettings(
                    context,
                    prefs.getBoolean("reminder_morning_enabled", false),
                    prefs.getBoolean("reminder_evening_enabled", false),
                    prefs.getString("reminder_morning_time", "08:30") ?: "08:30",
                    prefs.getString("reminder_evening_time", "21:00") ?: "21:00"
                )
                updateNovaGlavaWidgets(context)
                Toast.makeText(context, "Дані відновлено", Toast.LENGTH_SHORT).show()
                onImported()
            } catch (e: Exception) {
                status = "Не вдалося відновити: ${e.message ?: "невірний файл"}"
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
                Column {
                    Text("Резервна копія", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Без акаунта й сервера: один JSON-файл з главою, Evidence, архівом і налаштуваннями.", color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Mint)) {
                Column(Modifier.padding(18.dp)) {
                    Icon(Icons.Outlined.Backup, null, tint = MintDark, modifier = Modifier.size(31.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Створити backup", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Збережи файл у Downloads, Google Drive або будь-яку папку Android.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                    Spacer(Modifier.height(14.dp))
                    Button(onClick = { exportLauncher.launch("nova-glava-$timestamp.json") }, colors = ButtonDefaults.buttonColors(containerColor = MintDark), shape = RoundedCornerShape(16.dp)) {
                        Text("Експортувати JSON")
                    }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Lilac)) {
                Column(Modifier.padding(18.dp)) {
                    Icon(Icons.Outlined.Restore, null, tint = LilacStrong, modifier = Modifier.size(31.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Відновити з backup", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text("Відновлення замінить локальні дані поточного застосунку даними з файлу.", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                    Spacer(Modifier.height(14.dp))
                    OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/plain")) }, shape = RoundedCornerShape(16.dp)) {
                        Text("Обрати JSON-файл")
                    }
                }
            }
        }
        item {
            Text("Фото Vision Board не копіюються всередину JSON: зберігаються тексти образів, але фото після перенесення на інший телефон потрібно вибрати заново.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
        if (status.isNotBlank()) item { Text(status, color = MintDark, fontWeight = FontWeight.SemiBold) }
    }
}

private fun formatReminderInput(raw: String): String {
    val digits = raw.filter { it.isDigit() }.take(4)
    return when {
        digits.length <= 2 -> digits
        else -> digits.take(2) + ":" + digits.drop(2)
    }
}

private fun preferencesToBackupJson(prefs: SharedPreferences): JSONObject {
    val root = JSONObject()
    root.put("app", "NovaGlava")
    root.put("backupVersion", 1)
    root.put("createdAt", System.currentTimeMillis())
    val data = JSONObject()
    prefs.all.toSortedMap().forEach { (key, value) ->
        if (key.startsWith("vision_") && key.endsWith("_uri")) return@forEach
        val item = JSONObject()
        when (value) {
            is String -> { item.put("type", "string"); item.put("value", value) }
            is Int -> { item.put("type", "int"); item.put("value", value) }
            is Long -> { item.put("type", "long"); item.put("value", value) }
            is Float -> { item.put("type", "float"); item.put("value", value.toDouble()) }
            is Boolean -> { item.put("type", "boolean"); item.put("value", value) }
            is Set<*> -> {
                item.put("type", "stringSet")
                val array = JSONArray()
                value.filterIsInstance<String>().forEach { array.put(it) }
                item.put("value", array)
            }
            else -> return@forEach
        }
        data.put(key, item)
    }
    root.put("data", data)
    return root
}

private fun restorePreferencesFromBackup(prefs: SharedPreferences, root: JSONObject) {
    require(root.optString("app") == "NovaGlava") { "Це не backup «Нова глава»" }
    val data = root.getJSONObject("data")
    val editor = prefs.edit().clear()
    val keys = data.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val item = data.getJSONObject(key)
        when (item.getString("type")) {
            "string" -> editor.putString(key, item.optString("value", ""))
            "int" -> editor.putInt(key, item.getInt("value"))
            "long" -> editor.putLong(key, item.getLong("value"))
            "float" -> editor.putFloat(key, item.getDouble("value").toFloat())
            "boolean" -> editor.putBoolean(key, item.getBoolean("value"))
            "stringSet" -> {
                val array = item.getJSONArray("value")
                val values = mutableSetOf<String>()
                for (i in 0 until array.length()) values.add(array.getString(i))
                editor.putStringSet(key, values)
            }
        }
    }
    editor.apply()
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Lilac), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = LilacStrong, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, fontSize = 11.sp)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = Muted)
    }
}

@Composable
private fun StatCard(modifier: Modifier, value: String, label: String, bg: Color) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = bg)) {
        Column(Modifier.padding(14.dp)) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text(label, fontSize = 11.sp, color = Muted)
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Muted, modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.SemiBold, color = Ink)
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(Modifier.padding(top = 14.dp)) {
        Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Ink)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun BottomNav(page: Page, onPage: (Page) -> Unit) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp, modifier = Modifier.navigationBarsPadding()) {
        val items = listOf(
            Triple(Page.HOME, "Головна", Icons.Outlined.Home),
            Triple(Page.CHAPTER, "Глава", Icons.Outlined.MenuBook),
            Triple(Page.FRONTLINE, "Передова", Icons.Outlined.Flag),
            Triple(Page.ANALYTICS, "Результати", Icons.Outlined.Analytics),
            Triple(Page.PROFILE, "Профіль", Icons.Outlined.PersonOutline)
        )
        items.forEach { (target, label, icon) ->
            NavigationBarItem(
                selected = page == target,
                onClick = { onPage(target) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 9.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MintDark,
                    selectedTextColor = MintDark,
                    indicatorColor = Mint,
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%02d:%02d".format(minutes, seconds)
}

package ua.novaglava.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

private enum class Page { HOME, CHAPTER, FRONTLINE, ANALYTICS, PROFILE }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NovaGlavaTheme {
                NovaGlavaApp()
            }
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

    var page by rememberSaveable { mutableStateOf(Page.HOME) }
    var day by rememberSaveable { mutableIntStateOf(prefs.getInt("day", 7).coerceIn(1, 30)) }
    var mood by rememberSaveable { mutableStateOf(prefs.getString("mood", "Спокійна") ?: "Спокійна") }
    var switchOn by rememberSaveable { mutableStateOf(prefs.getBoolean("switch_on", false)) }
    val tasks = remember {
        mutableStateListOf(
            prefs.getBoolean("task_0", false),
            prefs.getBoolean("task_1", false),
            prefs.getBoolean("task_2", false)
        )
    }

    LaunchedEffect(day, mood, switchOn) {
        prefs.edit()
            .putInt("day", day)
            .putString("mood", mood)
            .putBoolean("switch_on", switchOn)
            .apply()
    }

    BackHandler(enabled = page == Page.FRONTLINE) { page = Page.HOME }

    Scaffold(
        containerColor = CanvasBg,
        bottomBar = {
            BottomNav(
                page = page,
                onPage = { page = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (page) {
                Page.HOME -> HomeScreen(
                    day = day,
                    mood = mood,
                    switchOn = switchOn,
                    doneTasks = tasks.count { it },
                    onMood = { mood = it },
                    onSwitch = { switchOn = it },
                    onFrontline = { page = Page.FRONTLINE },
                    onAnalytics = { page = Page.ANALYTICS },
                    onChapter = { page = Page.CHAPTER }
                )

                Page.CHAPTER -> ChapterScreen(
                    day = day,
                    switchOn = switchOn,
                    doneTasks = tasks.count { it },
                    onCompleteDay = {
                        if (day < 30) day += 1
                        switchOn = false
                        for (i in tasks.indices) {
                            tasks[i] = false
                            prefs.edit().putBoolean("task_$i", false).apply()
                        }
                    }
                )

                Page.FRONTLINE -> FrontlineScreen(
                    tasks = tasks,
                    onBack = { page = Page.HOME },
                    onToggle = { index, checked ->
                        tasks[index] = checked
                        prefs.edit().putBoolean("task_$index", checked).apply()
                    }
                )

                Page.ANALYTICS -> AnalyticsScreen(
                    day = day,
                    switchOn = switchOn,
                    doneTasks = tasks.count { it }
                )

                Page.PROFILE -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun HomeScreen(
    day: Int,
    mood: String,
    switchOn: Boolean,
    doneTasks: Int,
    onMood: (String) -> Unit,
    onSwitch: (Boolean) -> Unit,
    onFrontline: () -> Unit,
    onAnalytics: () -> Unit,
    onChapter: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { AppHeader() }
        item {
            SeasonCard(
                day = day,
                onClick = onChapter
            )
        }
        item {
            MoodSection(
                selected = mood,
                onSelected = onMood
            )
        }
        item {
            SwitchCard(
                checked = switchOn,
                onChecked = onSwitch
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Передова",
                    subtitle = "$doneTasks із 3 дій виконано",
                    icon = Icons.Outlined.AutoAwesome,
                    tint = LilacStrong,
                    background = Lilac,
                    onClick = onFrontline
                )
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Результати",
                    subtitle = "Відстежуй свій прогрес",
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
        IconButton(onClick = { }) {
            Icon(Icons.Outlined.Settings, contentDescription = "Налаштування", tint = Ink)
        }
    }
}

@Composable
private fun SeasonCard(day: Int, onClick: () -> Unit) {
    val progress = day / 30f
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
                        Text("Глава 01", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                        Text("Більше себе", color = Color.White.copy(alpha = 0.92f), fontSize = 16.sp)
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.92f),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.ArrowForwardIos, null, tint = Ink, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("День $day із 30", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.weight(1f))
                    Text("${(progress * 100).toInt()}%", color = Color.White.copy(alpha = 0.9f))
                }
                Spacer(Modifier.height(7.dp))
                LinearProgressIndicator(
                    progress = { progress },
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
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.LocalFlorist, null, tint = MintDark, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        Text("Весна", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Ink)
                        Text("Початок", fontSize = 11.sp, color = Muted)
                    }
                }
            }
        }
    }
}

@Composable
private fun MountainScene() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE8E1FF), Color(0xFFFFE8CE), Color(0xFFE4F2E4))
            )
        )
        drawCircle(
            color = Color(0xFFFFD889),
            radius = size.minDimension * 0.09f,
            center = Offset(size.width * 0.77f, size.height * 0.34f)
        )

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                    Text(
                        item.label,
                        fontSize = 10.sp,
                        color = if (isSelected) Ink else Muted,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SwitchCard(checked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Mint),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.AutoAwesome, null, tint = MintStrong)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text("SWITCH", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text(
                    if (checked) "Режим нової версії увімкнено" else "Одна нетипова корисна дія сьогодні",
                    fontSize = 12.sp,
                    color = Muted
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onChecked,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MintStrong,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFC8D3CC)
                )
            )
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
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = background)
    ) {
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
        FocusItem("Ранковий ритуал", "5 хвилин тиші", Icons.Outlined.WbSunny, Peach),
        FocusItem("Рух = настрій", "15 хвилин руху", Icons.Outlined.SelfImprovement, Mint),
        FocusItem("Вдячність", "3 речі ввечері", Icons.Outlined.Spa, Lilac)
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Сьогодні у фокусі", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.weight(1f))
            Text("3 кроки", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = item.bg)
                ) {
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
private fun ChapterScreen(day: Int, switchOn: Boolean, doneTasks: Int, onCompleteDay: () -> Unit) {
    val progress = day / 30f
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionHeader("Поточна глава", "Глава 01 · Більше себе") }
        item { SeasonCard(day = day, onClick = {}) }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Сьогоднішній стан", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    MetricRow("Прогрес сезону", "${(progress * 100).toInt()}%")
                    MetricRow("Передова", "$doneTasks / 3")
                    MetricRow("SWITCH", if (switchOn) "увімкнено" else "ще ні")
                }
            }
        }
        item {
            Button(
                onClick = onCompleteDay,
                enabled = day < 30,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MintDark)
            ) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.width(9.dp))
                Text(if (day < 30) "Завершити день" else "Глава завершена", fontWeight = FontWeight.Bold)
            }
        }
        item {
            Text(
                "Завершення дня збільшує лічильник сезону та очищує щоденні дії. У наступній версії тут буде вечірня рефлексія.",
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun FrontlineScreen(
    tasks: List<Boolean>,
    onBack: () -> Unit,
    onToggle: (Int, Boolean) -> Unit
) {
    val taskText = listOf(
        "90 хвилин на головній справі без перемикань",
        "Завершити одну задачу, яку давно відкладаю",
        "Зробити одну дію, що прямо дає результат"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, "Назад")
                }
                Column {
                    Text("Передова", fontSize = 27.sp, fontWeight = FontWeight.Bold)
                    Text("Те, що прямо рухає результат", fontSize = 12.sp, color = Muted)
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Lilac)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("Фокус дня", color = LilacStrong, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Не зайнятість. Результат.", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Обери кілька дій, після яких увечері можна побачити факт зміни в реальності.", color = Muted, lineHeight = 18.sp)
                }
            }
        }
        itemsIndexed(taskText) { index, title ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(index, !tasks[index]) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = tasks[index],
                        onCheckedChange = { onToggle(index, it) },
                        colors = CheckboxDefaults.colors(checkedColor = MintDark)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        title,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (tasks[index]) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (tasks[index]) MintDark else Ink,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        item {
            val done = tasks.count { it }
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Mint)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.TaskAlt, null, tint = MintDark, modifier = Modifier.size(34.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("$done із 3 виконано", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Не обов’язково закривати все. Важливо рухати головне.", color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun AnalyticsScreen(day: Int, switchOn: Boolean, doneTasks: Int) {
    val seasonProgress = day / 30f
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
                StatCard(Modifier.weight(1f), "${(seasonProgress * 100).toInt()}%", "сезону", Mint)
                StatCard(Modifier.weight(1f), "$doneTasks/3", "передова", Lilac)
                StatCard(Modifier.weight(1f), if (switchOn) "1" else "0", "SWITCH", Peach)
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("30-денна карта", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(14.dp))
                    DayGrid(day)
                }
            }
        }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Mint)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Analytics, null, tint = MintDark)
                        Spacer(Modifier.width(9.dp))
                        Text("Evidence", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("У версії 0.2 тут з’явиться зв’язка «що зробив → що отримав», щоб бачити, які дії реально дають результат.", color = Muted, lineHeight = 18.sp)
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun DayGrid(day: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in 0 until 5) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (col in 0 until 6) {
                    val number = row * 6 + col + 1
                    val completed = number < day
                    val current = number == day
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    completed -> MintDark
                                    current -> Sun
                                    else -> Color(0xFFF0F3EF)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completed) {
                            Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(17.dp))
                        } else {
                            Text(number.toString(), color = if (current) Ink else Muted, fontWeight = if (current) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { SectionHeader("Профіль", "MVP без реєстрації — усе зберігається на цьому телефоні") }
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Mint)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.PersonOutline, null, tint = MintDark, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Твоя нова глава", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Локальний профіль", color = Muted)
                    }
                }
            }
        }
        item {
            SettingsRow(Icons.Outlined.PsychologyAlt, "Роль сезону", "Додамо редактор у 0.2")
            HorizontalDivider(color = CardBorder)
            SettingsRow(Icons.Outlined.MenuBook, "Vision Feed", "Фото майбутньої версії")
            HorizontalDivider(color = CardBorder)
            SettingsRow(Icons.Outlined.NightsStay, "Нагадування", "Ранок і вечір")
        }
        item {
            Text(
                "Версія 0.1.0 · офлайн MVP",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Muted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Lilac),
            contentAlignment = Alignment.Center
        ) {
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text(label, fontSize = 11.sp, color = Muted)
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            Triple(Page.HOME, "Головна", Icons.Outlined.Home),
            Triple(Page.CHAPTER, "Глава", Icons.Outlined.MenuBook),
            Triple(Page.ANALYTICS, "Результати", Icons.Outlined.Analytics),
            Triple(Page.PROFILE, "Профіль", Icons.Outlined.PersonOutline)
        )
        items.forEach { (target, label, icon) ->
            NavigationBarItem(
                selected = page == target,
                onClick = { onPage(target) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
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

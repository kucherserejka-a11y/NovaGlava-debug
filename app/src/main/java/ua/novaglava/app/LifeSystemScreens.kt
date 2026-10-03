package ua.novaglava.app

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LifeInk = Color(0xFF23342F)
private val LifeMuted = Color(0xFF718078)
private val LifeBg = Color(0xFFFAFBF8)
private val LifeMint = Color(0xFFDFF1E5)
private val LifeMintDark = Color(0xFF436D57)
private val LifeLilac = Color(0xFFEDE7FF)
private val LifeLilacStrong = Color(0xFF8C74C7)
private val LifePeach = Color(0xFFFFE9D2)

private enum class LifeTab(val label: String) {
    HERO("Герой"), TODAY("Сьогодні"), WEEK("Тиждень"), HEALTH("Здоров’я"), ADVENTURES("Пригоди")
}

private data class WeeklyAction(
    val id: String,
    val sphere: String,
    val title: String,
    val target: Int,
    val minutes: Int,
    val frontline: Boolean,
    val completed: Int
)

private data class HealthCheckin(
    val at: Long,
    val weight: String,
    val energy: Int,
    val wellbeing: Int,
    val sleep: String,
    val keto: String,
    val fastingProtocol: String,
    val fastingDone: Boolean,
    val steps: Int,
    val note: String
)

private data class MiniAdventure(
    val id: String,
    val title: String,
    val done: Boolean,
    val recurring: Boolean,
    val note: String
)

@Composable
internal fun LifeSystemScreen(prefs: SharedPreferences, onBack: () -> Unit) {
    var tabName by rememberSaveable { mutableStateOf(prefs.getString("life_tab", LifeTab.HERO.name) ?: LifeTab.HERO.name) }
    val tab = LifeTab.values().firstOrNull { it.name == tabName } ?: LifeTab.HERO
    var actions by remember { mutableStateOf(loadWeeklyActions(prefs)) }
    var checkins by remember { mutableStateOf(loadHealthCheckins(prefs)) }
    var adventures by remember { mutableStateOf(loadAdventures(prefs)) }
    var xp by remember { mutableIntStateOf(prefs.getInt("life_xp", 0).coerceAtLeast(0)) }

    fun saveActions(next: List<WeeklyAction>) {
        actions = next
        persistWeeklyActions(prefs, next)
    }
    fun saveAdventures(next: List<MiniAdventure>) {
        adventures = next
        persistAdventures(prefs, next)
    }
    fun addXp(points: Int) {
        xp = (xp + points).coerceAtLeast(0)
        prefs.edit().putInt("life_xp", xp).apply()
    }

    Column(modifier = Modifier.fillMaxSize().background(LifeBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, start = 10.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
            Column(Modifier.weight(1f)) {
                Text("Моя система", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = LifeInk)
                Text("Герой · сфери · тиждень · дії", fontSize = 11.sp, color = LifeMuted)
            }
            val level = xp / 100 + 1
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = LifeLilac)) {
                Text("LVL $level", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = LifeLilacStrong, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    LifeTab.values().forEach { item ->
                        FilterChip(
                            selected = tab == item,
                            onClick = {
                                tabName = item.name
                                prefs.edit().putString("life_tab", item.name).apply()
                            },
                            label = { Text(item.label, fontSize = 9.sp) }
                        )
                    }
                }
            }

            when (tab) {
                LifeTab.HERO -> heroItems(prefs, xp, actions, checkins, adventures)
                LifeTab.TODAY -> todayItems(
                    actions = actions,
                    adventures = adventures,
                    onActionDone = { id ->
                        val index = actions.indexOfFirst { it.id == id }
                        if (index >= 0 && actions[index].completed < actions[index].target) {
                            val a = actions[index]
                            val next = actions.toMutableList()
                            next[index] = a.copy(completed = a.completed + 1)
                            saveActions(next)
                            addXp(if (a.frontline) 25 else 10)
                        }
                    },
                    onAdventureDone = { id ->
                        val index = adventures.indexOfFirst { it.id == id }
                        if (index >= 0 && !adventures[index].done) {
                            val next = adventures.toMutableList()
                            next[index] = adventures[index].copy(done = true)
                            saveAdventures(next)
                            addXp(20)
                        }
                    }
                )
                LifeTab.WEEK -> weekItems(
                    prefs = prefs,
                    actions = actions,
                    adventures = adventures,
                    onActionsChanged = ::saveActions,
                    onAdventuresChanged = ::saveAdventures
                )
                LifeTab.HEALTH -> healthItems(
                    prefs = prefs,
                    checkins = checkins,
                    onSaved = { item ->
                        checkins = (listOf(item) + checkins).take(120)
                        persistHealthCheckins(prefs, checkins)
                        prefs.edit().putString("life_weight", item.weight).apply()
                        addXp(5 + (if (item.keto == "Так") 5 else 0) + (if (item.fastingDone) 5 else 0))
                    }
                )
                LifeTab.ADVENTURES -> adventureItems(
                    adventures = adventures,
                    onChanged = ::saveAdventures,
                    onDone = { id ->
                        val index = adventures.indexOfFirst { it.id == id }
                        if (index >= 0 && !adventures[index].done) {
                            val next = adventures.toMutableList()
                            next[index] = adventures[index].copy(done = true)
                            saveAdventures(next)
                            addXp(20)
                        }
                    }
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.heroItems(
    prefs: SharedPreferences,
    xp: Int,
    actions: List<WeeklyAction>,
    checkins: List<HealthCheckin>,
    adventures: List<MiniAdventure>
) {
    item { HeroCard(prefs, xp, checkins.firstOrNull()) }
    item { Text("Сфери цієї глави", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LifeInk) }
    val spheres = actions.groupBy { it.sphere }
    if (spheres.isEmpty()) {
        item { EmptyLifeCard("Додай дії у «Тиждень», і тут з’явиться прогрес по сферах.") }
    } else {
        spheres.forEach { (sphere, sphereActions) ->
            item {
                val total = sphereActions.sumOf { it.target }.coerceAtLeast(1)
                val done = sphereActions.sumOf { it.completed }.coerceAtMost(total)
                val progress = done / total.toFloat()
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                    Column(Modifier.padding(15.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sphere, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = LifeInk)
                            Text(done.toString() + "/" + total, color = LifeMuted)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                            color = if (sphere == "KDP" || sphere == "PFU") LifeLilacStrong else LifeMintDark,
                            trackColor = LifeMint
                        )
                        val minutes = sphereActions.sumOf { it.minutes * it.target }
                        Text("План: " + formatLifeMinutes(minutes), color = LifeMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
    item {
        val doneAdventures = adventures.count { it.done }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = LifePeach)) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Landscape, null, tint = Color(0xFFA66C22))
                    Spacer(Modifier.width(8.dp))
                    Text("Міні-пригоди", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Text(doneAdventures.toString() + " із " + adventures.size + " виконано цього тижня", color = LifeMuted, modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}

@Composable
private fun HeroCard(prefs: SharedPreferences, xp: Int, latest: HealthCheckin?) {
    val context = LocalContext.current
    var heroUri by remember { mutableStateOf(prefs.getString("life_hero_uri", "") ?: "") }
    var height by rememberSaveable { mutableStateOf(prefs.getString("life_height", "") ?: "") }
    var targetWeight by rememberSaveable { mutableStateOf(prefs.getString("life_target_weight", "") ?: "") }
    var waist by rememberSaveable { mutableStateOf(prefs.getString("life_waist", "") ?: "") }
    var editing by rememberSaveable { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
            heroUri = uri.toString()
            prefs.edit().putString("life_hero_uri", heroUri).apply()
        }
    }

    val level = xp / 100 + 1
    val levelProgress = (xp % 100) / 100f
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = LifeMint)) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(96.dp).clip(RoundedCornerShape(24.dp)).background(Color.White.copy(alpha = 0.8f)).clickable { picker.launch(arrayOf("image/*")) },
                    contentAlignment = Alignment.Center
                ) {
                    if (heroUri.isNotBlank()) {
                        AndroidView(
                            factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                            update = { view -> try { view.setImageURI(Uri.parse(heroUri)) } catch (_: Exception) {} },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.PersonOutline, null, tint = LifeMintDark, modifier = Modifier.size(38.dp))
                            Text("Додати фото", fontSize = 9.sp, color = LifeMuted)
                        }
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text("Мій герой", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LifeInk)
                    Text("Рівень $level · $xp XP", color = LifeMintDark, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                        color = LifeMintDark,
                        trackColor = Color.White.copy(alpha = 0.7f)
                    )
                    Text((100 - (xp % 100)).toString() + " XP до нового рівня", color = LifeMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                }
                IconButton(onClick = { editing = !editing }) { Icon(Icons.Outlined.Edit, "Редагувати", tint = LifeMintDark) }
            }

            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroMetric(Modifier.weight(1f), "Зріст", if (height.isBlank()) "—" else "$height см")
                HeroMetric(Modifier.weight(1f), "Вага", latest?.weight?.takeIf { it.isNotBlank() }?.let { "$it кг" } ?: "—")
                HeroMetric(Modifier.weight(1f), "Ціль", if (targetWeight.isBlank()) "—" else "$targetWeight кг")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                HeroMetric(Modifier.weight(1f), "Енергія", latest?.let { it.energy.toString() + "/10" } ?: "—")
                HeroMetric(Modifier.weight(1f), "Стан", latest?.let { it.wellbeing.toString() + "/10" } ?: "—")
                HeroMetric(Modifier.weight(1f), "Кроки", latest?.steps?.takeIf { it > 0 }?.toString() ?: "—")
            }

            if (editing) {
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { c -> c.isDigit() }.take(3) },
                    label = { Text("Зріст, см") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { targetWeight = lifeNumberInput(it) },
                    label = { Text("Цільова вага, кг") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = waist,
                    onValueChange = { waist = lifeNumberInput(it) },
                    label = { Text("Талія, см — необов’язково") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        prefs.edit().putString("life_height", height).putString("life_target_weight", targetWeight).putString("life_waist", waist).apply()
                        editing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LifeMintDark)
                ) { Text("Зберегти героя") }
            }
        }
    }
}

@Composable
private fun HeroMetric(modifier: Modifier, label: String, value: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.78f))) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = LifeInk, fontSize = 15.sp)
            Text(label, color = LifeMuted, fontSize = 9.sp)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.todayItems(
    actions: List<WeeklyAction>,
    adventures: List<MiniAdventure>,
    onActionDone: (String) -> Unit,
    onAdventureDone: (String) -> Unit
) {
    item { LifeSectionTitle("Сьогодні", "Не весь тиждень одразу. Обери одну дію й закрий її.") }
    val active = actions.filter { it.completed < it.target }
    if (active.isEmpty()) {
        item { EmptyLifeCard("Усі заплановані дії тижня вже виконані. Можеш додати нову у вкладці «Тиждень».") }
    } else {
        items(active, key = { it.id }) { action -> TodayActionCard(action) { onActionDone(action.id) } }
    }
    val adventure = adventures.firstOrNull { !it.done }
    if (adventure != null) {
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = LifePeach)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Landscape, null, tint = Color(0xFFA66C22))
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Міні-пригода", fontWeight = FontWeight.Bold)
                            Text(adventure.title, color = LifeInk)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onAdventureDone(adventure.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA66C22)), shape = RoundedCornerShape(14.dp)) {
                        Text("Зроблено · +20 XP")
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayActionCard(action: WeeklyAction, onDone: () -> Unit) {
    val bg = when (action.sphere) {
        "KDP", "PFU" -> LifeLilac
        "Здоров’я" -> LifeMint
        "Собака" -> LifePeach
        else -> Color.White
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = bg), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SphereIcon(action.sphere)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(action.sphere, color = LifeMuted, fontSize = 11.sp)
                        if (action.frontline) {
                            Spacer(Modifier.width(6.dp))
                            Text("ПЕРЕДОВА", color = LifeLilacStrong, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(action.title, fontWeight = FontWeight.Bold, color = LifeInk, fontSize = 17.sp)
                }
                Text(action.completed.toString() + "/" + action.target, fontWeight = FontWeight.Bold, color = LifeInk)
            }
            val info = if (action.minutes > 0) action.minutes.toString() + " хв · залишилось " + (action.target - action.completed) + " раз(и)"
            else "Залишилось " + (action.target - action.completed) + " раз(и)"
            Text(info, color = LifeMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = if (action.frontline) LifeLilacStrong else LifeMintDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.CheckCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (action.frontline) "Виконано · +25 XP" else "Виконано · +10 XP")
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.weekItems(
    prefs: SharedPreferences,
    actions: List<WeeklyAction>,
    adventures: List<MiniAdventure>,
    onActionsChanged: (List<WeeklyAction>) -> Unit,
    onAdventuresChanged: (List<MiniAdventure>) -> Unit
) {
    item { LifeSectionTitle("Тижневий штаб", "Плануй у неділю, але змінювати план можна будь-коли.") }
    item {
        val totalMinutes = actions.sumOf { it.target * it.minutes }
        val frontlineMinutes = actions.filter { it.frontline }.sumOf { it.target * it.minutes }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = LifeMint)) {
            Column(Modifier.padding(16.dp)) {
                Text("План тижня", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Text(actions.sumOf { it.target }.toString() + " дій · " + formatLifeMinutes(totalMinutes), color = LifeMuted)
                Text("Передова: " + formatLifeMinutes(frontlineMinutes), color = LifeLilacStrong, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        appendWeekHistory(prefs, actions)
                        onActionsChanged(actions.map { it.copy(completed = 0) })
                        onAdventuresChanged(adventures.map { it.copy(done = false) })
                        prefs.edit().putLong("life_week_started", System.currentTimeMillis()).apply()
                    },
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Почати новий тиждень") }
            }
        }
    }
    items(actions, key = { it.id }) { action ->
        WeekActionEditor(
            action = action,
            onUpdate = { updated -> onActionsChanged(actions.map { if (it.id == action.id) updated else it }) },
            onDelete = { onActionsChanged(actions.filterNot { it.id == action.id }) }
        )
    }
    item { AddWeeklyActionCard { action -> onActionsChanged(actions + action) } }
}

@Composable
private fun WeekActionEditor(action: WeeklyAction, onUpdate: (WeeklyAction) -> Unit, onDelete: () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SphereIcon(action.sphere)
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    Text(action.sphere, color = LifeMuted, fontSize = 11.sp)
                    Text(action.title, fontWeight = FontWeight.Bold, color = LifeInk)
                }
                IconButton(onClick = onDelete) { Icon(Icons.Outlined.DeleteOutline, "Видалити", tint = Color(0xFFB66A6A)) }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Text("Разів/тиждень", modifier = Modifier.weight(1f), color = LifeMuted, fontSize = 12.sp)
                OutlinedButton(onClick = {
                    val nextTarget = (action.target - 1).coerceAtLeast(1)
                    onUpdate(action.copy(target = nextTarget, completed = action.completed.coerceAtMost(nextTarget)))
                }) { Text("−") }
                Text(action.target.toString(), modifier = Modifier.width(36.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = { onUpdate(action.copy(target = (action.target + 1).coerceAtMost(14))) }) { Text("+") }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                Text("Час однієї дії", modifier = Modifier.weight(1f), color = LifeMuted, fontSize = 12.sp)
                Text(action.minutes.toString() + " хв", fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Передова", modifier = Modifier.weight(1f), color = LifeMuted)
                Switch(checked = action.frontline, onCheckedChange = { onUpdate(action.copy(frontline = it)) })
            }
            Text("Виконано " + action.completed + " із " + action.target, color = LifeMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AddWeeklyActionCard(onAdd: (WeeklyAction) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var sphere by rememberSaveable { mutableStateOf("KDP") }
    var customSphere by rememberSaveable { mutableStateOf("") }
    var target by rememberSaveable { mutableIntStateOf(1) }
    var minutes by rememberSaveable { mutableIntStateOf(60) }
    var frontline by rememberSaveable { mutableStateOf(false) }
    val sphereOptions = listOf("KDP", "PFU", "Здоров’я", "Собака", "Особисте", "Інше")
    val minuteOptions = listOf(15, 30, 45, 60, 90, 120)

    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = LifeLilac)) {
        Column(Modifier.padding(16.dp)) {
            Text("Додати дію", fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Text("Постійна дія KDP/PFU або будь-яка нова сфера.", color = LifeMuted, fontSize = 11.sp)
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Наприклад: зробити книжку") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text("Сфера", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                sphereOptions.take(3).forEach { value -> FilterChip(selected = sphere == value, onClick = { sphere = value }, label = { Text(value, fontSize = 10.sp) }) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                sphereOptions.drop(3).forEach { value -> FilterChip(selected = sphere == value, onClick = { sphere = value }, label = { Text(value, fontSize = 10.sp) }) }
            }
            if (sphere == "Інше") {
                OutlinedTextField(value = customSphere, onValueChange = { customSphere = it }, label = { Text("Назва сфери") }, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Кількість", modifier = Modifier.weight(1f))
                OutlinedButton(onClick = { target = (target - 1).coerceAtLeast(1) }) { Text("−") }
                Text(target.toString(), modifier = Modifier.width(38.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = { target = (target + 1).coerceAtMost(14) }) { Text("+") }
            }
            Text("Тривалість однієї сесії", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                minuteOptions.forEach { value ->
                    val label = if (value < 60) value.toString() else if (value == 60) "1г" else if (value == 90) "1.5г" else "2г"
                    FilterChip(selected = minutes == value, onClick = { minutes = value }, label = { Text(label, fontSize = 9.sp) })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Це Передова", fontWeight = FontWeight.SemiBold)
                    Text("Дія прямо створює результат", color = LifeMuted, fontSize = 10.sp)
                }
                Switch(checked = frontline, onCheckedChange = { frontline = it })
            }
            Button(
                onClick = {
                    val finalSphere = if (sphere == "Інше") customSphere.trim().ifBlank { "Інше" } else sphere
                    onAdd(WeeklyAction("a" + System.currentTimeMillis(), finalSphere, title.trim(), target, minutes, frontline, 0))
                    title = ""
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LifeLilacStrong),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.Add, null)
                Spacer(Modifier.width(6.dp))
                Text("Додати в тиждень")
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.healthItems(
    prefs: SharedPreferences,
    checkins: List<HealthCheckin>,
    onSaved: (HealthCheckin) -> Unit
) {
    item { HealthCheckinCard(prefs, onSaved) }
    item { Text("Історія", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LifeInk) }
    if (checkins.isEmpty()) {
        item { EmptyLifeCard("Ще немає health check-in. Можеш робити короткий щодня або повний раз на тиждень.") }
    } else {
        items(checkins.take(12)) { c ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(14.dp)) {
                    Row {
                        Text(lifeDate(c.at), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text(c.weight.takeIf { it.isNotBlank() }?.let { "$it кг" } ?: "вага —", color = LifeMuted)
                    }
                    Text("Енергія " + c.energy + "/10 · стан " + c.wellbeing + "/10 · сон " + c.sleep.ifBlank { "—" }, color = LifeMuted, fontSize = 11.sp)
                    Text("Кето: " + c.keto + " · IF " + c.fastingProtocol + ": " + (if (c.fastingDone) "виконано" else "ні") + " · кроки " + c.steps, color = LifeMuted, fontSize = 11.sp)
                    if (c.note.isNotBlank()) Text(c.note, color = LifeInk, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
private fun HealthCheckinCard(prefs: SharedPreferences, onSaved: (HealthCheckin) -> Unit) {
    var weight by rememberSaveable { mutableStateOf(prefs.getString("life_weight", "") ?: "") }
    var energy by rememberSaveable { mutableIntStateOf(7) }
    var wellbeing by rememberSaveable { mutableIntStateOf(7) }
    var sleep by rememberSaveable { mutableStateOf("") }
    var keto by rememberSaveable { mutableStateOf("Так") }
    var fastingProtocol by rememberSaveable { mutableStateOf(prefs.getString("life_fasting_protocol", "16/8") ?: "16/8") }
    var fastingDone by rememberSaveable { mutableStateOf(false) }
    var steps by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = LifeMint)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.HealthAndSafety, null, tint = LifeMintDark)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Health check-in", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Щоденно — коротко. Раз на тиждень — вага й повний стан.", color = LifeMuted, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = lifeNumberInput(it) },
                label = { Text("Вага, кг") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            ScoreStepper("Енергія", energy) { energy = it }
            ScoreStepper("Самопочуття", wellbeing) { wellbeing = it }
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = sleep,
                onValueChange = { sleep = lifeNumberInput(it) },
                label = { Text("Сон, годин") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Кето сьогодні", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Так", "Частково", "Ні").forEach { value -> FilterChip(selected = keto == value, onClick = { keto = value }, label = { Text(value) }) }
            }
            Text("Інтервальне голодування", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf("12/12", "14/10", "16/8", "18/6").forEach { value ->
                    FilterChip(
                        selected = fastingProtocol == value,
                        onClick = {
                            fastingProtocol = value
                            prefs.edit().putString("life_fasting_protocol", value).apply()
                        },
                        label = { Text(value, fontSize = 10.sp) }
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Вікно витримано", modifier = Modifier.weight(1f))
                Switch(checked = fastingDone, onCheckedChange = { fastingDone = it })
            }
            OutlinedTextField(
                value = steps,
                onValueChange = { steps = it.filter { c -> c.isDigit() }.take(6) },
                label = { Text("Кроки сьогодні — поки вручну") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Автокроки через Health Connect заплановані на v0.7.", color = LifeMuted, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Нотатка про стан") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    onSaved(HealthCheckin(System.currentTimeMillis(), weight, energy, wellbeing, sleep, keto, fastingProtocol, fastingDone, steps.toIntOrNull() ?: 0, note.trim()))
                    note = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = LifeMintDark),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Зберегти check-in") }
            Text("Застосунок лише веде журнал режиму й самопочуття; він не визначає медичну безпечність дієти чи голодування.", color = LifeMuted, fontSize = 9.sp, lineHeight = 13.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun ScoreStepper(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(1f), color = LifeInk)
        OutlinedButton(onClick = { onChange((value - 1).coerceAtLeast(1)) }) { Text("−") }
        Text(value.toString() + "/10", modifier = Modifier.width(54.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        OutlinedButton(onClick = { onChange((value + 1).coerceAtMost(10)) }) { Text("+") }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.adventureItems(
    adventures: List<MiniAdventure>,
    onChanged: (List<MiniAdventure>) -> Unit,
    onDone: (String) -> Unit
) {
    item { LifeSectionTitle("Міні-пригоди", "Живі події глави. Не продуктивність, а докази, що життя вже змінилось.") }
    items(adventures, key = { it.id }) { adventure ->
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (adventure.done) LifeMint else LifePeach)) {
            Column(Modifier.padding(15.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Landscape, null, tint = if (adventure.done) LifeMintDark else Color(0xFFA66C22))
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        Text(adventure.title, fontWeight = FontWeight.Bold)
                        Text(if (adventure.recurring) "Повторюється щотижня" else "Разова пригода", color = LifeMuted, fontSize = 10.sp)
                    }
                    if (adventure.done) Icon(Icons.Outlined.CheckCircle, "Готово", tint = LifeMintDark)
                    else Button(onClick = { onDone(adventure.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA66C22))) { Text("+20 XP") }
                }
                if (adventure.note.isNotBlank()) Text(adventure.note, color = LifeMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                Text("Видалити", color = Color(0xFFB66A6A), fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp).clickable { onChanged(adventures.filterNot { it.id == adventure.id }) })
            }
        }
    }
    item {
        var title by rememberSaveable { mutableStateOf("") }
        var recurring by rememberSaveable { mutableStateOf(true) }
        var note by rememberSaveable { mutableStateOf("") }
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
            Column(Modifier.padding(16.dp)) {
                Text("Нова міні-пригода", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Що зробити") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Навіщо / де / з ким — необов’язково") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Повторювати щотижня", modifier = Modifier.weight(1f))
                    Switch(checked = recurring, onCheckedChange = { recurring = it })
                }
                Button(
                    onClick = {
                        onChanged(adventures + MiniAdventure("m" + System.currentTimeMillis(), title.trim(), false, recurring, note.trim()))
                        title = ""
                        note = ""
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = LifeMintDark),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Додати пригоду") }
            }
        }
    }
}

@Composable
private fun LifeSectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold, color = LifeInk)
        Text(subtitle, color = LifeMuted, fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun EmptyLifeCard(text: String) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
        Text(text, modifier = Modifier.padding(16.dp), color = LifeMuted, fontSize = 12.sp)
    }
}

@Composable
private fun SphereIcon(sphere: String) {
    val icon = when (sphere) {
        "KDP", "PFU" -> Icons.Outlined.WorkOutline
        "Здоров’я" -> Icons.Outlined.FitnessCenter
        "Собака" -> Icons.Outlined.Pets
        "Особисте" -> Icons.Outlined.AutoAwesome
        else -> Icons.Outlined.TaskAlt
    }
    Box(modifier = Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(LifeMint), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = LifeMintDark, modifier = Modifier.size(21.dp))
    }
}

private fun loadWeeklyActions(prefs: SharedPreferences): List<WeeklyAction> {
    val raw = prefs.getString("life_week_actions", null)
    if (!raw.isNullOrBlank()) {
        try {
            val arr = JSONArray(raw)
            return List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                WeeklyAction(
                    id = o.optString("id", "a$i"),
                    sphere = o.optString("sphere", "Інше"),
                    title = o.optString("title", "Дія"),
                    target = o.optInt("target", 1).coerceAtLeast(1),
                    minutes = o.optInt("minutes", 30).coerceAtLeast(0),
                    frontline = o.optBoolean("frontline", false),
                    completed = o.optInt("completed", 0).coerceAtLeast(0)
                )
            }
        } catch (_: Exception) {}
    }
    val defaults = listOf(
        WeeklyAction("health_sport", "Здоров’я", "Спорт / тренування", 3, 30, false, 0),
        WeeklyAction("health_keto", "Здоров’я", "Кето-день", 7, 0, false, 0),
        WeeklyAction("health_if", "Здоров’я", "Інтервальне голодування", 7, 0, false, 0),
        WeeklyAction("dog_walk", "Собака", "Довша прогулянка з собакою", 4, 45, false, 0),
        WeeklyAction("kdp_books", "KDP", "Зробити книжку", 2, 120, true, 0),
        WeeklyAction("pfu_publish", "PFU", "Опублікувати корисний матеріал / локації", 3, 60, true, 0)
    )
    persistWeeklyActions(prefs, defaults)
    return defaults
}

private fun persistWeeklyActions(prefs: SharedPreferences, items: List<WeeklyAction>) {
    val arr = JSONArray()
    items.forEach { a ->
        arr.put(JSONObject().apply {
            put("id", a.id); put("sphere", a.sphere); put("title", a.title); put("target", a.target)
            put("minutes", a.minutes); put("frontline", a.frontline); put("completed", a.completed)
        })
    }
    prefs.edit().putString("life_week_actions", arr.toString()).apply()
}

private fun loadHealthCheckins(prefs: SharedPreferences): List<HealthCheckin> {
    val raw = prefs.getString("life_health_history", "[]") ?: "[]"
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            HealthCheckin(
                o.optLong("at", 0L), o.optString("weight", ""), o.optInt("energy", 5), o.optInt("wellbeing", 5),
                o.optString("sleep", ""), o.optString("keto", "—"), o.optString("fastingProtocol", "16/8"),
                o.optBoolean("fastingDone", false), o.optInt("steps", 0), o.optString("note", "")
            )
        }
    } catch (_: Exception) { emptyList() }
}

private fun persistHealthCheckins(prefs: SharedPreferences, items: List<HealthCheckin>) {
    val arr = JSONArray()
    items.forEach { c ->
        arr.put(JSONObject().apply {
            put("at", c.at); put("weight", c.weight); put("energy", c.energy); put("wellbeing", c.wellbeing)
            put("sleep", c.sleep); put("keto", c.keto); put("fastingProtocol", c.fastingProtocol)
            put("fastingDone", c.fastingDone); put("steps", c.steps); put("note", c.note)
        })
    }
    prefs.edit().putString("life_health_history", arr.toString()).apply()
}

private fun loadAdventures(prefs: SharedPreferences): List<MiniAdventure> {
    val raw = prefs.getString("life_adventures", null)
    if (!raw.isNullOrBlank()) {
        try {
            val arr = JSONArray(raw)
            return List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                MiniAdventure(o.optString("id", "m$i"), o.optString("title", "Міні-пригода"), o.optBoolean("done", false), o.optBoolean("recurring", true), o.optString("note", ""))
            }
        } catch (_: Exception) {}
    }
    val defaults = listOf(
        MiniAdventure("m_dog", "Новий маршрут із собакою", false, true, "Не той самий звичний маршрут."),
        MiniAdventure("m_work", "Попрацювати 1–2 години не вдома", false, true, "Кафе, бібліотека, коворкінг або нове місце."),
        MiniAdventure("m_sport", "Спробувати нову спортивну активність", false, false, "")
    )
    persistAdventures(prefs, defaults)
    return defaults
}

private fun persistAdventures(prefs: SharedPreferences, items: List<MiniAdventure>) {
    val arr = JSONArray()
    items.forEach { a ->
        arr.put(JSONObject().apply { put("id", a.id); put("title", a.title); put("done", a.done); put("recurring", a.recurring); put("note", a.note) })
    }
    prefs.edit().putString("life_adventures", arr.toString()).apply()
}

private fun appendWeekHistory(prefs: SharedPreferences, actions: List<WeeklyAction>) {
    val raw = prefs.getString("life_week_history", "[]") ?: "[]"
    val arr = try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
    arr.put(JSONObject().apply {
        put("at", System.currentTimeMillis())
        put("planned", actions.sumOf { it.target })
        put("done", actions.sumOf { it.completed })
        put("plannedMinutes", actions.sumOf { it.target * it.minutes })
        put("frontlinePlannedMinutes", actions.filter { it.frontline }.sumOf { it.target * it.minutes })
    })
    prefs.edit().putString("life_week_history", arr.toString()).apply()
}

private fun lifeNumberInput(raw: String): String {
    var out = raw.replace(',', '.').filter { it.isDigit() || it == '.' }
    val firstDot = out.indexOf('.')
    if (firstDot >= 0) out = out.substring(0, firstDot + 1) + out.substring(firstDot + 1).replace(".", "")
    return out.take(6)
}

private fun lifeDate(at: Long): String {
    if (at <= 0L) return "—"
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(at))
}

private fun formatLifeMinutes(minutes: Int): String {
    if (minutes <= 0) return "без таймера"
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h == 0 -> "$m хв"
        m == 0 -> "$h год"
        else -> "$h год $m хв"
    }
}

package ua.novaglava.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.PsychologyAlt
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val MInk = Color(0xFF23342F)
private val MMuted = Color(0xFF718078)
private val MMint = Color(0xFFDFF1E5)
private val MMintDark = Color(0xFF436D57)
private val MLilac = Color(0xFFEDE7FF)
private val MLilacStrong = Color(0xFF8C74C7)
private val MPeach = Color(0xFFFFE9D2)
private val MRose = Color(0xFFFFE7EC)
private val MBorder = Color(0xFFE8ECE7)

internal fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "Скопійовано", Toast.LENGTH_SHORT).show()
}

@Composable
private fun ToolHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.Outlined.ArrowBack, "Назад") }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 27.sp, fontWeight = FontWeight.Bold, color = MInk)
            Text(subtitle, color = MMuted, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun ToolRow(icon: ImageVector, title: String, subtitle: String, bg: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Color.White.copy(alpha = .72f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = if (bg == MLilac) MLilacStrong else MMintDark)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MInk)
                Text(subtitle, color = MMuted, fontSize = 11.sp, lineHeight = 15.sp)
            }
            Icon(Icons.Outlined.ChevronRight, null, tint = MMuted)
        }
    }
}

@Composable
internal fun MethodsHubScreen(
    onBack: () -> Unit,
    onDossier: () -> Unit,
    onFrontlineAudit: () -> Unit,
    onDetonator: () -> Unit,
    onReboot: () -> Unit,
    onVision: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ToolHeader("Метод глави", "П’ять практик із доданих матеріалів автора — перетворені на робочі сценарії в застосунку.", onBack) }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MPeach)) {
                Text(
                    "Це практичні вправи за матеріалами автора, а не медична або психологічна діагностика. Твердження про роботу мозку залишаємо як авторське пояснення, а в застосунку фокусуємося на діях, записах і перевірці результатом.",
                    modifier = Modifier.padding(16.dp), color = MInk, fontSize = 12.sp, lineHeight = 18.sp
                )
            }
        }
        item { ToolRow(Icons.Outlined.PsychologyAlt, "Я 2.0 · Досьє", "Поточний я → прайм → герой → почерк → смисли → мініпригоди", MLilac, onDossier) }
        item { ToolRow(Icons.Outlined.Flag, "Знайди Передову", "6 запитань, фільтр незворотності, 50/50 та гра на 20 очок", MMint, onFrontlineAudit) }
        item { ToolRow(Icons.Outlined.FavoriteBorder, "Детонатор бажань", "40 запитань у 4 категоріях, ⚡-мітки, 2–3 фінальні бажання", MRose, onDetonator) }
        item { ToolRow(Icons.Outlined.Refresh, "Перепрошивка", "Безпечні дії проти автопілота: тіло, простір, сприйняття, люди, робота, цифрове", MPeach, onReboot) }
        item { ToolRow(Icons.Outlined.PhotoLibrary, "Норма нового рівня · Фото", "5 сфер Vision Board: ти всередині бажаного життя, а не окремі предмети", MLilac, onVision) }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

private fun pref(prefs: SharedPreferences, key: String) = prefs.getString(key, "") ?: ""

@Composable
internal fun DossierScreen(prefs: SharedPreferences, onBack: () -> Unit, onSaved: (String) -> Unit) {
    var nowMorning by rememberSaveable { mutableStateOf(pref(prefs, "dossier_now_morning")) }
    var primeMorning by rememberSaveable { mutableStateOf(pref(prefs, "dossier_prime_morning")) }
    var nowDecision by rememberSaveable { mutableStateOf(pref(prefs, "dossier_now_decision")) }
    var primeDecision by rememberSaveable { mutableStateOf(pref(prefs, "dossier_prime_decision")) }
    var nowEnergy by rememberSaveable { mutableStateOf(pref(prefs, "dossier_now_energy")) }
    var primeEnergy by rememberSaveable { mutableStateOf(pref(prefs, "dossier_prime_energy")) }
    var nowWork by rememberSaveable { mutableStateOf(pref(prefs, "dossier_now_work")) }
    var primeWork by rememberSaveable { mutableStateOf(pref(prefs, "dossier_prime_work")) }
    var nowEvening by rememberSaveable { mutableStateOf(pref(prefs, "dossier_now_evening")) }
    var primeEvening by rememberSaveable { mutableStateOf(pref(prefs, "dossier_prime_evening")) }
    var peakMoments by rememberSaveable { mutableStateOf(pref(prefs, "dossier_peak_moments")) }
    var peakQualities by rememberSaveable { mutableStateOf(pref(prefs, "dossier_peak_qualities")) }
    var heroes by rememberSaveable { mutableStateOf(pref(prefs, "dossier_heroes")) }
    var heroTraits by rememberSaveable { mutableStateOf(pref(prefs, "dossier_traits")) }
    var hiddenTraits by rememberSaveable { mutableStateOf(pref(prefs, "dossier_hidden")) }
    var fearsToStrength by rememberSaveable { mutableStateOf(pref(prefs, "dossier_fears")) }
    var shadow by rememberSaveable { mutableStateOf(pref(prefs, "dossier_shadow")) }
    var versionName by rememberSaveable { mutableStateOf(pref(prefs, "dossier_name")) }
    var tempo by rememberSaveable { mutableStateOf(pref(prefs, "dossier_tempo")) }
    var decisionStyle by rememberSaveable { mutableStateOf(pref(prefs, "dossier_decision_style")) }
    var peopleRole by rememberSaveable { mutableStateOf(pref(prefs, "dossier_people_role")) }
    var posture by rememberSaveable { mutableStateOf(pref(prefs, "dossier_posture")) }
    var visualStyle by rememberSaveable { mutableStateOf(pref(prefs, "dossier_visual_style")) }
    var dayStart by rememberSaveable { mutableStateOf(pref(prefs, "dossier_day_start")) }
    var stress by rememberSaveable { mutableStateOf(pref(prefs, "dossier_stress")) }
    var boundaries by rememberSaveable { mutableStateOf(pref(prefs, "dossier_boundaries")) }
    var taboos by rememberSaveable { mutableStateOf(pref(prefs, "dossier_taboos")) }
    var beliefs by rememberSaveable { mutableStateOf(pref(prefs, "dossier_beliefs")) }
    var values by rememberSaveable { mutableStateOf(pref(prefs, "dossier_values")) }
    var defend by rememberSaveable { mutableStateOf(pref(prefs, "dossier_defend")) }
    var dreams by rememberSaveable { mutableStateOf(pref(prefs, "dossier_dreams")) }
    var topActivity by rememberSaveable { mutableStateOf(pref(prefs, "dossier_activity")) }
    var weeklyAdventure by rememberSaveable { mutableStateOf(pref(prefs, "dossier_weekly_adventure")) }
    var adventures by rememberSaveable { mutableStateOf(pref(prefs, "dossier_adventures")) }

    fun saveAndBrief() {
        val e = prefs.edit()
        mapOf(
            "dossier_now_morning" to nowMorning, "dossier_prime_morning" to primeMorning,
            "dossier_now_decision" to nowDecision, "dossier_prime_decision" to primeDecision,
            "dossier_now_energy" to nowEnergy, "dossier_prime_energy" to primeEnergy,
            "dossier_now_work" to nowWork, "dossier_prime_work" to primeWork,
            "dossier_now_evening" to nowEvening, "dossier_prime_evening" to primeEvening,
            "dossier_peak_moments" to peakMoments, "dossier_peak_qualities" to peakQualities,
            "dossier_heroes" to heroes, "dossier_traits" to heroTraits, "dossier_hidden" to hiddenTraits,
            "dossier_fears" to fearsToStrength, "dossier_shadow" to shadow, "dossier_name" to versionName,
            "dossier_tempo" to tempo, "dossier_decision_style" to decisionStyle, "dossier_people_role" to peopleRole,
            "dossier_posture" to posture, "dossier_visual_style" to visualStyle, "dossier_day_start" to dayStart,
            "dossier_stress" to stress, "dossier_boundaries" to boundaries, "dossier_taboos" to taboos,
            "dossier_beliefs" to beliefs, "dossier_values" to values, "dossier_defend" to defend,
            "dossier_dreams" to dreams, "dossier_activity" to topActivity,
            "dossier_weekly_adventure" to weeklyAdventure, "dossier_adventures" to adventures
        ).forEach { (k, v) -> e.putString(k, v) }
        val brief = buildString {
            if (versionName.isNotBlank()) append(versionName.trim())
            if (heroTraits.isNotBlank()) append(if (isNotEmpty()) " · " else "").append(heroTraits.trim())
            if (tempo.isNotBlank()) append(if (isNotEmpty()) "\n" else "").append("Темп: ").append(tempo.trim())
            if (taboos.isNotBlank()) append(if (isNotEmpty()) "\n" else "").append("Табу: ").append(taboos.trim())
            if (topActivity.isNotBlank()) append(if (isNotEmpty()) "\n" else "").append("Сенс: ").append(topActivity.trim())
            if (weeklyAdventure.isNotBlank()) append(if (isNotEmpty()) "\n" else "").append("Узор тижня: ").append(weeklyAdventure.trim())
        }
        e.putString("dossier_brief", brief).apply()
        onSaved(brief)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ToolHeader("Я 2.0 · Досьє", "Не вигадуй ідеального персонажа: віднови конкретні риси свого сильного періоду й дособери їх під нову главу.", onBack) }
        item {
            DossierCard("1 · Я зараз / я в праймі", MMint) {
                TwoColumnField("Як прокидаюсь / перша думка", nowMorning, { nowMorning = it }, primeMorning, { primeMorning = it })
                TwoColumnField("Як приймаю рішення", nowDecision, { nowDecision = it }, primeDecision, { primeDecision = it })
                TwoColumnField("Енергія 0–10", nowEnergy, { nowEnergy = it }, primeEnergy, { primeEnergy = it })
                TwoColumnField("Що відчуваю до своїх справ", nowWork, { nowWork = it }, primeWork, { primeWork = it })
                TwoColumnField("Як закінчується день", nowEvening, { nowEvening = it }, primeEvening, { primeEvening = it })
                Field("3–5 найсильніших моментів життя", peakMoments) { peakMoments = it }
                Field("Що їх об’єднує — які якості вмикались", peakQualities) { peakQualities = it }
            }
        }
        item {
            DossierCard("2 · Риси героя", MLilac) {
                Field("Герої / персонажі, на яких схожа ця версія", heroes) { heroes = it }
                Field("Якості, за які я їх обрав", heroTraits) { heroTraits = it }
                Field("Якості, які приховую або пригнічую", hiddenTraits) { hiddenTraits = it }
                Field("Страхи й слабкості, які перетворюю на силу", fearsToStrength) { fearsToStrength = it }
                Field("Тінь персонажа — його «занадто»", shadow) { shadow = it }
                Field("Ім’я / прізвисько версії", versionName) { versionName = it }
            }
        }
        item {
            DossierCard("3 · Як проявляється", MPeach) {
                Field("Темп", tempo) { tempo = it }
                Field("Як приймає рішення", decisionStyle) { decisionStyle = it }
                Field("Роль із людьми", peopleRole) { peopleRole = it }
                Field("Постава, фірмова поза, міміка", posture) { posture = it }
                Field("Стиль, зовнішній вигляд, деталі", visualStyle) { visualStyle = it }
                Field("Як починає день", dayStart) { dayStart = it }
                Field("Як реагує на стрес, страх, критику", stress) { stress = it }
                Field("Межі, яких дотримується завжди", boundaries) { boundaries = it }
                Field("3 табу цієї версії", taboos) { taboos = it }
            }
        }
        item {
            DossierCard("4 · Смисли глави", MMint) {
                Field("У що вірить ця версія: про себе, світ, гроші", beliefs) { beliefs = it }
                Field("Недоторканні цінності", values) { values = it }
                Field("Що готова захищати", defend) { defend = it }
                Field("Мрії цієї глави", dreams) { dreams = it }
                Field("Діяльність, у якій найвище задоволення", topActivity) { topActivity = it }
            }
        }
        item {
            DossierCard("5 · Узори реальності", MLilac) {
                Field("Одна повторювана пригода щотижня", weeklyAdventure) { weeklyAdventure = it }
                Field("Ще 4–6 мініпригод цієї глави", adventures) { adventures = it }
                Text("Після збереження застосунок зробить коротку ранкову вижимку: ім’я версії, риси, темп, табу, сенс і узор тижня.", color = MMuted, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
        item {
            Button(onClick = { saveAndBrief() }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(18.dp), colors = ButtonDefaults.buttonColors(containerColor = MMintDark)) {
                Icon(Icons.Outlined.CheckCircle, null); Spacer(Modifier.width(8.dp)); Text("Зберегти досьє", fontWeight = FontWeight.Bold)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun DossierCard(title: String, bg: Color, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = bg)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MInk)
            content()
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
}

@Composable
private fun TwoColumnField(label: String, left: String, onLeft: (String) -> Unit, right: String, onRight: (String) -> Unit) {
    Text(label, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MInk)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(left, onLeft, label = { Text("Я зараз") }, modifier = Modifier.weight(1f), minLines = 2)
        OutlinedTextField(right, onRight, label = { Text("Я в праймі") }, modifier = Modifier.weight(1f), minLines = 2)
    }
}

@Composable
internal fun FrontlineAuditScreen(
    prefs: SharedPreferences,
    onBack: () -> Unit,
    onUseFrontline: (String) -> Unit
) {
    val context = LocalContext.current
    val questions = listOf(
        "Чим ти займаєшся і що є твоїм результатом? Своє діло, фриланс чи найм?",
        "Опиши звичайний робочий тиждень: заняття → приблизно годин на тиждень.",
        "Скільки годин іде на підготовку: навчання, планування, аналіз, розмови про роботу замість роботи?",
        "Що за останні 7 днів пішло у зовнішній світ і це вже не можна скасувати?",
        "Звідки про тебе дізнаються люди / хто бачить твої результати — і чому обирають саме тебе?",
        "Яку робочу дію відкладаєш найдовше? Де виникає «потім» або бажання ще підготуватися?"
    )
    val answers = remember { mutableStateListOf<String>().apply { repeat(6) { add(pref(prefs, "front_a_$it")) } } }
    var prepHours by rememberSaveable { mutableStateOf(pref(prefs, "front_prep_hours")) }
    var routineHours by rememberSaveable { mutableStateOf(pref(prefs, "front_routine_hours")) }
    var score by rememberSaveable { mutableIntStateOf(prefs.getInt("front_game_score", 0)) }
    val prep = prepHours.replace(',', '.').toDoubleOrNull() ?: 0.0
    val routine = routineHours.replace(',', '.').toDoubleOrNull() ?: 0.0
    val loopWeek = prep + routine
    val loopYear = (loopWeek * 50).roundToInt()

    fun persist() {
        val e = prefs.edit()
        answers.forEachIndexed { i, a -> e.putString("front_a_$i", a) }
        e.putString("front_prep_hours", prepHours).putString("front_routine_hours", routineHours).putInt("front_game_score", score).apply()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ToolHeader("Знайди Передову", "Рентген тижня: що прямо створює результат, а що залишається підготовкою й рутиною.", onBack) }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MMint)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Фільтр Передової", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(7.dp))
                    Text("Дія проходить фільтр, якщо після неї щось незворотно сталося у зовнішньому світі: відправлено, опубліковано, сказано, запропоновано, зустріч відбулася.", color = MMuted, fontSize = 12.sp, lineHeight = 18.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("4 категорії: увага · зближення · пропозиція · повтор", color = MMintDark, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
        itemsIndexed(questions) { index, q ->
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(14.dp)) {
                    Text("${index + 1}. $q", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(answers[index], { answers[index] = it; persist() }, label = { Text("Твоя відповідь") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MPeach)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Часова петля", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    OutlinedTextField(prepHours, { prepHours = it; persist() }, label = { Text("Годин підготовки / тиждень") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(routineHours, { routineHours = it; persist() }, label = { Text("Годин повторюваної рутини без нового результату") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    Text("≈ ${"%.1f".format(loopWeek)} год/тиждень · ≈ $loopYear год/рік", color = MInk, fontWeight = FontWeight.Bold)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MLilac)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Гра на 3 дні · 20 очок", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("1 — мале незворотне торкання · 2 — дія, що може дати вхідний інтерес · 5 — жива розмова / зустріч / пряма пропозиція.", color = MMuted, fontSize = 11.sp, lineHeight = 16.sp)
                    Text("$score / 20", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = if (score >= 20) MMintDark else MLilacStrong)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 5).forEach { pts -> OutlinedButton(onClick = { score += pts; persist() }) { Text("+$pts") } }
                        TextButton(onClick = { score = 0; persist() }) { Text("Скинути") }
                    }
                }
            }
        }
        item {
            if (answers[5].isNotBlank()) {
                Button(onClick = { onUseFrontline(answers[5].trim()) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MMintDark), shape = RoundedCornerShape(18.dp)) {
                    Icon(Icons.Outlined.Flag, null); Spacer(Modifier.width(8.dp)); Text("Взяти №6 як Передову")
                }
            }
        }
        item {
            OutlinedButton(
                onClick = {
                    persist()
                    val packet = buildString {
                        append("Допоможи знайти мою Передову. Аналізуй лише мої відповіді. Передова — це конкретні незворотні дії, які прямо створюють результат. Розділи: моя неділя зі сторони; часова петля; 3–5 дій Передової з категорією (увага/зближення/пропозиція/повтор) і перевіркою незворотності; схема 50/50; гра на 3 дні — 20 очок.\n\n")
                        questions.forEachIndexed { i, q -> append("${i + 1}. $q\nВідповідь: ${answers[i]}\n\n") }
                        append("Підготовка: $prepHours год/тиждень. Рутинна петля: $routineHours год/тиждень.")
                    }
                    copyText(context, "Передова", packet)
                },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)
            ) { Icon(Icons.Outlined.AutoAwesome, null); Spacer(Modifier.width(8.dp)); Text("Скопіювати пакет для AI") }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

private val desireQuestions = listOf(
    "Кому ти заздриш так, що неприємно це визнавати? Чому саме?",
    "Чий контент дивишся зі сумішшю захоплення й роздратування? Що ця людина собі дозволяє?",
    "Чию життєву картинку листаєш із відчуттям «це мало бути моїм»? Який саме шматок?",
    "Чиї перемоги ти знецінюєш? Що саме там зачіпає?",
    "Якби прокинувся в житті людини, якій заздриш, що зробив би в перший день?",
    "Яка чужа новина за останній місяць коротко й неприємно кольнула?",
    "Чого тобі «вже пізно» хотіти?",
    "На що всередині звучить «це для інших, не для таких, як я»?",
    "Яку чужу суму доходу на місяць тобі заздрісно чути?",
    "Людина твого віку й рівня через рік зробила те, про що ти давно думаєш. Що саме?",
    "Що найбільше бісить у людях твоєї ніші / професії?",
    "На що у своїй поточній життєвій ситуації регулярно злишся й терпиш? Скільки років?",
    "Кому ти щось доводиш? Що ця людина має побачити?",
    "Що в дитинстві чи юності забороняли / висміювали, а тебе досі туди тягне?",
    "Якби твоя злість могла сказати світові одну фразу без наслідків — яку?",
    "Яке минуле «ні» досі злить? Чого воно тебе позбавило?",
    "Що в роботі робиш, зціпивши зуби? Що залишив би, навіть якби гроші були закриті?",
    "Хто живе «занадто легко / голосно / нахабно» і чому це тебе зачіпає?",
    "Де ти кажеш «я вище цього»? Це спокій чи прикритий голод?",
    "Один день повної нахабності без наслідків: як виглядає його розклад?",
    "П’ять разів допиши: «Мені соромно зізнатися, але насправді я хочу…»",
    "Яке бажання називаєш несерйозним, егоїзмом, не за віком або примхою?",
    "Чого б захотів, якби гарантовано ніхто ніколи не дізнався?",
    "Яке бажання не вимовляєш уголос, бо близькі «не зрозуміють»? Хто саме?",
    "Чи хочеш слави — щоб упізнавали й бачили? Де саме хочеш бути помітним?",
    "Чи хочеш влади — бути головним, вирішувати, вести? У якому ділі?",
    "Що в тобі «занадто»? Занадто гучний, багато хочеш, дивний, сміливий…",
    "Про що мріяв у 14–17 років і потім закопав як нереалістичне?",
    "Опиши один звичайний день версії себе, яка одночасно лякає й захоплює.",
    "Якби завтра зникла думка всіх людей про тебе, що почав би робити до кінця тижня?",
    "Згадай 3 моменти «ось це — я живу». Що в них спільного?",
    "Від якого заняття в дитинстві втрачав лік часу? Що саме там робив?",
    "Що робиш зараз так, що забуваєш про телефон? Або коли це було востаннє?",
    "Після яких розмов, зустрічей чи справ виходиш із більшою енергією, ніж зайшов?",
    "Про що можеш говорити годину без підготовки — і тебе реально несе?",
    "Від чого мурашки або клубок у горлі? Що спільного в цих моментах?",
    "Куди тягне «просто так», без користі й монетизації, але ти забороняєш собі?",
    "Рік повністю оплаченої життя, але щодня треба займатися одним ділом. Що обереш?",
    "Грошей достатньо назавжди. Після трьох місяців відпочинку стало нудно. Чим займешся?",
    "Яка картинка бажаного життя повертається роками сама? Опиши її максимально конкретно."
)

private val desireCategories = listOf("Заздрість · навігатор", "Злість · енергія", "Заборонене", "Живе")

@Composable
internal fun DesireDetonatorScreen(
    prefs: SharedPreferences,
    onBack: () -> Unit,
    onUseWish: (String) -> Unit
) {
    val context = LocalContext.current
    var category by rememberSaveable { mutableIntStateOf(0) }
    val answers = remember { mutableStateListOf<String>().apply { repeat(40) { add(pref(prefs, "desire_answer_$it")) } } }
    val bolts = remember { mutableStateListOf<Boolean>().apply { repeat(40) { add(prefs.getBoolean("desire_bolt_$it", false)) } } }
    var before by rememberSaveable { mutableStateOf(pref(prefs, "desire_before")) }
    var after by rememberSaveable { mutableStateOf(pref(prefs, "desire_after")) }
    var wish1 by rememberSaveable { mutableStateOf(pref(prefs, "desire_wish_1")) }
    var wish2 by rememberSaveable { mutableStateOf(pref(prefs, "desire_wish_2")) }
    var wish3 by rememberSaveable { mutableStateOf(pref(prefs, "desire_wish_3")) }
    var step72 by rememberSaveable { mutableStateOf(pref(prefs, "desire_step_72")) }

    fun persist() {
        val e = prefs.edit().putString("desire_before", before).putString("desire_after", after)
            .putString("desire_wish_1", wish1).putString("desire_wish_2", wish2).putString("desire_wish_3", wish3).putString("desire_step_72", step72)
        answers.forEachIndexed { i, a -> e.putString("desire_answer_$i", a).putBoolean("desire_bolt_$i", bolts[i]) }
        e.apply()
    }

    val start = category * 10
    val end = start + 10
    LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { ToolHeader("Детонатор бажань", "40 незручних запитань → ⚡-відгуки → повторювані теми → 2–3 формулювання → незворотний крок за 72 години.", onBack) }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MRose)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Замір до", fontWeight = FontWeight.Bold)
                    Text("Наскільки майбутнє зараз тебе вмикає? 1 — порожньо, 10 — тягне діяти.", color = MMuted, fontSize = 11.sp)
                    Spacer(Modifier.height(7.dp))
                    OutlinedTextField(before, { before = it; persist() }, label = { Text("1–10") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                desireCategories.forEachIndexed { i, c ->
                    FilterChip(selected = category == i, onClick = { category = i }, label = { Text("${i + 1}", fontSize = 11.sp) })
                }
            }
            Text(desireCategories[category], fontWeight = FontWeight.Bold, color = MLilacStrong)
        }
        itemsIndexed(desireQuestions.subList(start, end)) { local, q ->
            val index = start + local
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (bolts[index]) MPeach else Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("${index + 1}. $q", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 18.sp)
                        FilterChip(selected = bolts[index], onClick = { bolts[index] = !bolts[index]; persist() }, label = { Text("⚡") })
                    }
                    Spacer(Modifier.height(7.dp))
                    OutlinedTextField(answers[index], { answers[index] = it; persist() }, label = { Text("Відповідь") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MMint)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Збірка", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Text("1) Переглянь лише відповіді з ⚡. 2) Знайди теми, що повторюються хоча б двічі. 3) Сформулюй 2–3 конкретні бажання від першої особи в теперішньому часі, де є дія.", color = MMuted, fontSize = 11.sp, lineHeight = 17.sp)
                    OutlinedTextField(wish1, { wish1 = it; persist() }, label = { Text("Бажання 1") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(wish2, { wish2 = it; persist() }, label = { Text("Бажання 2") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(wish3, { wish3 = it; persist() }, label = { Text("Бажання 3") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    OutlinedTextField(after, { after = it; persist() }, label = { Text("Замір після · 1–10") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(step72, { step72 = it; persist() }, label = { Text("Незворотний крок у найближчі 72 години") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(enabled = wish1.isNotBlank(), onClick = { onUseWish(wish1.trim()) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = MMintDark)) { Text("У главу") }
                OutlinedButton(onClick = {
                    persist()
                    val boltsText = answers.indices.filter { bolts[it] && answers[it].isNotBlank() }.joinToString("\n") { "${it + 1}. ${answers[it]}" }
                    copyText(context, "Детонатор", "Мої відповіді з ⚡:\n$boltsText\n\nФінальні бажання:\n1. $wish1\n2. $wish2\n3. $wish3\n\nКрок 72 год: $step72")
                }, modifier = Modifier.weight(1f)) { Text("Копіювати") }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

private data class RebootAction(val category: String, val text: String)

private val rebootActions = listOf(
    RebootAction("Тіло", "Почистити зуби неведучою рукою"),
    RebootAction("Тіло", "Один прийом їжі — неведучою рукою або паличками"),
    RebootAction("Тіло", "Протягом дня кілька разів перевірити поставу й темп нової версії"),
    RebootAction("Тіло", "Спробувати новий безпечний вид руху: танці, розтяжка, плавання, скеледром"),
    RebootAction("Простір", "Дістатися звичного місця новим маршрутом"),
    RebootAction("Простір", "Поснідати або попрацювати в новому кафе / бібліотеці / коворкінгу"),
    RebootAction("Простір", "Переставити 1–2 речі у робочій зоні, щоб зламати автоматичний сценарій"),
    RebootAction("Простір", "Прибрати з поля зору 10 незмінних дрібниць і додати одну нову"),
    RebootAction("Сприйняття", "Зняти 10 красивих кадрів на звичному маршруті"),
    RebootAction("Сприйняття", "Прослухати один альбом цілком, нічим паралельно не займаючись"),
    RebootAction("Сприйняття", "Подивитися фільм із країни, кіно якої раніше не дивився"),
    RebootAction("Сприйняття", "Прочитати 20–30 сторінок із незнайомої сфери"),
    RebootAction("Сприйняття", "Написати пів сторінки від руки неведучою рукою"),
    RebootAction("Сприйняття", "Вивчити 10 слів мови, яка подобається за звучанням"),
    RebootAction("Люди", "Почати один короткий живий мікророзмову з незнайомцем у безпечній ситуації"),
    RebootAction("Люди", "Подзвонити старому другові, з яким давно не говорив"),
    RebootAction("Люди", "Надіслати конкретну подяку людині за те, що вона колись зробила"),
    RebootAction("Люди", "Поставити одному експерту зі своєї сфери конкретне питання по суті"),
    RebootAction("Люди", "Попросити про допомогу там, де зазвичай усе тягнеш сам"),
    RebootAction("Робота", "Почати робочий день із найстрашнішої важливої задачі"),
    RebootAction("Робота", "30 хвилин створювати щось до першої пошти / месенджерів"),
    RebootAction("Робота", "Зробити перший фізичний крок у справі, яку відкладаєш понад місяць"),
    RebootAction("Робота", "Написати людині, з якою давно «незручно» зв’язатися"),
    RebootAction("Робота", "Запитати у трьох клієнтів або читачів, чому вони обрали саме тебе"),
    RebootAction("Цифрове", "До кінця сніданку не відкривати телефон"),
    RebootAction("Цифрове", "Відписатися від 10–20 акаунтів, які не відповідають новій главі"),
    RebootAction("Цифрове", "На день увімкнути чорно-білий режим екрана"),
    RebootAction("Цифрове", "Видалити одне застосування-пожирач часу на 7 днів"),
    RebootAction("Цифрове", "Вимкнути неважливі автоматичні сповіщення"),
    RebootAction("Цифрове", "Сьогодні перевіряти соцмережі лише у 3 заплановані вікна")
)

@Composable
internal fun RebootScreen(prefs: SharedPreferences, onBack: () -> Unit, onUseSwitch: (String) -> Unit) {
    var first by rememberSaveable { mutableStateOf(pref(prefs, "reboot_today_1").ifBlank { rebootActions[0].text }) }
    var second by rememberSaveable { mutableStateOf(pref(prefs, "reboot_today_2").ifBlank { rebootActions[5].text }) }
    var done1 by rememberSaveable { mutableStateOf(prefs.getBoolean("reboot_done_1", false)) }
    var done2 by rememberSaveable { mutableStateOf(prefs.getBoolean("reboot_done_2", false)) }
    var total by rememberSaveable { mutableIntStateOf(prefs.getInt("reboot_total", 0)) }

    fun persist() { prefs.edit().putString("reboot_today_1", first).putString("reboot_today_2", second).putBoolean("reboot_done_1", done1).putBoolean("reboot_done_2", done2).putInt("reboot_total", total).apply() }
    fun nextPair() {
        val shuffled = rebootActions.shuffled()
        first = shuffled.first().text
        second = shuffled.firstOrNull { it.category != shuffled.first().category }?.text ?: shuffled[1].text
        done1 = false; done2 = false; persist()
    }
    LazyColumn(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { ToolHeader("Перепрошивка", "На день — 1–2 незвичні безпечні дії з різних категорій. Не треба «закрити список»: задача — помітити й зламати автопілот.", onBack) }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MMint)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Сьогодні", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Spacer(Modifier.height(8.dp))
                    RebootTodayItem(first, done1, { checked -> if (checked && !done1) total++; if (!checked && done1) total = (total - 1).coerceAtLeast(0); done1 = checked; persist() }, { onUseSwitch(first) })
                    HorizontalDivider(color = MBorder, modifier = Modifier.padding(vertical = 8.dp))
                    RebootTodayItem(second, done2, { checked -> if (checked && !done2) total++; if (!checked && done2) total = (total - 1).coerceAtLeast(0); done2 = checked; persist() }, { onUseSwitch(second) })
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick = { nextPair() }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Outlined.Refresh, null); Spacer(Modifier.width(6.dp)); Text("Інші 2 дії") }
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MPeach)) {
                Column(Modifier.padding(16.dp)) {
                    Text("$total", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MMintDark)
                    Text("незвичних дій виконано", color = MMuted)
                }
            }
        }
        item {
            Text("Бібліотека безпечних варіантів", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        itemsIndexed(rebootActions.groupBy { it.category }.toList()) { _, pair ->
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Column(Modifier.padding(14.dp)) {
                    Text(pair.first, fontWeight = FontWeight.Bold, color = MLilacStrong)
                    Spacer(Modifier.height(6.dp))
                    pair.second.forEach { action ->
                        Text("• ${action.text}", fontSize = 12.sp, lineHeight = 17.sp, color = MInk, modifier = Modifier.padding(vertical = 3.dp).clickable { onUseSwitch(action.text) })
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun RebootTodayItem(text: String, done: Boolean, onDone: (Boolean) -> Unit, onUseSwitch: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FilterChip(selected = done, onClick = { onDone(!done) }, label = { Text(if (done) "✓" else "○") })
        Spacer(Modifier.width(8.dp))
        Text(text, modifier = Modifier.weight(1f), fontSize = 13.sp, lineHeight = 18.sp, fontWeight = if (done) FontWeight.Normal else FontWeight.SemiBold)
        TextButton(onClick = onUseSwitch) { Text("SWITCH") }
    }
}

internal fun buildVisionPrompt(index: Int, scene: String): String {
    val sphere = when (index) {
        0 -> "теплий буденний момент із близькими"
        1 -> "моє житлове або робоче середовище нового рівня"
        2 -> "звичайне користування предметом нового рівня"
        3 -> "подорож або місто, де я виглядаю як людина, що тут живе, а не турист"
        4 -> "моя діяльність у масштабі нової версії мене"
        else -> "мій власний кадр бажаного повсякденного життя"
    }
    return "Фотореалістичне фото мене. Сцена: ${scene.ifBlank { sphere }}. Я — головний герой кадру, живу цей момент буденно і спокійно. Природне освітлення, звичайний одяг, нейтральний вираз обличчя, жива неідеальна композиція як фото від друга, без рекламного глянцю і позування."
}

package ua.novaglava.app

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

internal data class LifeProject(
    val id: String,
    val name: String,
    val status: String,
    val startMinor: Int,
    val cadence: String,
    val weeklyTarget: Int,
    val minutes: Int,
    val frontline: Boolean,
    val createdAt: Long
)

private val ProjectInk = Color(0xFF23342F)
private val ProjectMuted = Color(0xFF718078)
private val ProjectMint = Color(0xFFDFF1E5)
private val ProjectMintDark = Color(0xFF436D57)
private val ProjectLilac = Color(0xFFEDE7FF)
private val ProjectLilacStrong = Color(0xFF8C74C7)
private val ProjectPeach = Color(0xFFFFE9D2)

internal fun currentSelfMinor(prefs: SharedPreferences): Int =
    prefs.getInt("archive_count", 0).coerceAtLeast(0)

internal fun currentSelfVersion(prefs: SharedPreferences): String =
    "2." + currentSelfMinor(prefs)

internal fun loadLifeProjects(prefs: SharedPreferences): List<LifeProject> {
    val raw = prefs.getString("life_projects", null)
    if (!raw.isNullOrBlank()) {
        try {
            val arr = JSONArray(raw)
            return List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                LifeProject(
                    id = o.optString("id", "project_$i"),
                    name = o.optString("name", "Проєкт"),
                    status = o.optString("status", "ACTIVE"),
                    startMinor = o.optInt("startMinor", 0).coerceAtLeast(0),
                    cadence = o.optString("cadence", "WEEKLY"),
                    weeklyTarget = o.optInt("weeklyTarget", 1).coerceIn(1, 14),
                    minutes = o.optInt("minutes", 60).coerceAtLeast(0),
                    frontline = o.optBoolean("frontline", false),
                    createdAt = o.optLong("createdAt", System.currentTimeMillis())
                )
            }
        } catch (_: Exception) {}
    }

    val defaults = listOf(
        LifeProject("project_kdp", "KDP", "ACTIVE", 0, "WEEKLY", 2, 120, true, System.currentTimeMillis()),
        LifeProject("project_pfu", "PFU", "ACTIVE", 0, "WEEKLY", 3, 60, true, System.currentTimeMillis())
    )
    persistLifeProjects(prefs, defaults)
    return defaults
}

internal fun persistLifeProjects(prefs: SharedPreferences, projects: List<LifeProject>) {
    val arr = JSONArray()
    projects.forEach { p ->
        arr.put(JSONObject().apply {
            put("id", p.id); put("name", p.name); put("status", p.status); put("startMinor", p.startMinor)
            put("cadence", p.cadence); put("weeklyTarget", p.weeklyTarget); put("minutes", p.minutes)
            put("frontline", p.frontline); put("createdAt", p.createdAt)
        })
    }
    prefs.edit().putString("life_projects", arr.toString()).apply()
}

internal fun projectIsAvailable(project: LifeProject, currentMinor: Int): Boolean =
    project.status == "ACTIVE" && project.startMinor <= currentMinor

internal fun projectCadenceTarget(project: LifeProject): Int =
    when (project.cadence) {
        "DAILY" -> 7
        "WEEKLY" -> project.weeklyTarget.coerceAtLeast(1)
        else -> 1
    }

internal fun projectDays(project: LifeProject): String =
    if (project.cadence == "DAILY") "1,2,3,4,5,6,7" else ""

internal fun projectItems(
    scope: androidx.compose.foundation.lazy.LazyListScope,
    prefs: SharedPreferences,
    projects: List<LifeProject>,
    onChanged: (List<LifeProject>) -> Unit,
    onCreateRoutine: (LifeProject) -> Unit
) {
    val currentMinor = currentSelfMinor(prefs)

    scope.item {
        Column {
            Text("Проєкти", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
            Text(
                "Проєкт — довша лінія роботи. Він може жити кілька глав, мати свій ритм і породжувати регулярні дії.",
                color = ProjectMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }

    scope.item {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ProjectMint)) {
            Column(Modifier.padding(16.dp)) {
                Text("Я " + currentSelfVersion(prefs), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
                Text(
                    "Поточна версія · завершення цієї глави відкриє Я 2." + (currentMinor + 1),
                    color = ProjectMuted,
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(10.dp))
                val activeCount = projects.count { projectIsAvailable(it, currentMinor) }
                val futureCount = projects.count { it.status == "ACTIVE" && it.startMinor > currentMinor }
                Text("$activeCount активних проєктів · $futureCount заплановано на майбутні глави", color = ProjectMintDark, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (projects.isEmpty()) {
        scope.item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = CardDefaults.outlinedCardBorder()) {
                Text("Поки немає проєктів. Додай перший нижче.", modifier = Modifier.padding(16.dp), color = ProjectMuted)
            }
        }
    } else {
        scope.items(projects.size, key = { projects[it].id }) { index ->
            val project = projects[index]
            ProjectCard(
                project = project,
                currentMinor = currentMinor,
                onUpdate = { updated ->
                    onChanged(projects.map { if (it.id == project.id) updated else it })
                },
                onCreateRoutine = { onCreateRoutine(project) }
            )
        }
    }

    scope.item {
        NewProjectCard(
            currentMinor = currentMinor,
            onAdd = { project -> onChanged(projects + project) }
        )
    }
}

@Composable
private fun ProjectCard(
    project: LifeProject,
    currentMinor: Int,
    onUpdate: (LifeProject) -> Unit,
    onCreateRoutine: () -> Unit
) {
    val future = project.startMinor > currentMinor
    val done = project.status == "DONE"
    val paused = project.status == "PAUSED"
    val available = projectIsAvailable(project, currentMinor)
    val bg = when {
        done -> Color(0xFFF1F2F1)
        future -> ProjectLilac
        paused -> ProjectPeach
        else -> Color.White
    }
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = bg), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(if (available) ProjectMint else Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.WorkOutline, null, tint = if (available) ProjectMintDark else ProjectMuted)
                }
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, fontSize = 19.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
                    val stateText = when {
                        done -> "Завершено"
                        paused -> "На паузі"
                        future -> "Старт з Я 2." + project.startMinor
                        else -> "Активний · Я 2.$currentMinor"
                    }
                    Text(stateText, color = ProjectMuted, fontSize = 10.sp)
                }
                if (done) Icon(Icons.Outlined.CheckCircle, null, tint = ProjectMintDark)
                else if (paused) Icon(Icons.Outlined.PauseCircle, null, tint = Color(0xFFA66C22))
                else Icon(Icons.Outlined.PlayCircle, null, tint = ProjectMintDark)
            }

            val cadence = when (project.cadence) {
                "DAILY" -> "Щодня"
                "WEEKLY" -> project.weeklyTarget.toString() + " раз(и) на тиждень"
                else -> "Без постійного графіка"
            }
            Text("$cadence · ${project.minutes} хв на сесію", color = ProjectMuted, fontSize = 11.sp)
            if (project.frontline) {
                Text("ПЕРЕДОВА за замовчуванням", color = ProjectLilacStrong, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            if (available) {
                Button(
                    onClick = onCreateRoutine,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ProjectLilacStrong),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.Add, null)
                    Spacer(Modifier.size(6.dp))
                    Text("Додати регулярну дію в тиждень")
                }
            } else if (future && !done && !paused) {
                OutlinedButton(
                    onClick = { onUpdate(project.copy(startMinor = currentMinor)) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Почати вже в цій главі") }
            }

            if (!done) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (paused) {
                        OutlinedButton(
                            onClick = { onUpdate(project.copy(status = "ACTIVE")) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Продовжити") }
                    } else {
                        OutlinedButton(
                            onClick = { onUpdate(project.copy(status = "PAUSED")) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Пауза") }
                    }
                    OutlinedButton(
                        onClick = { onUpdate(project.copy(status = "DONE")) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Завершити") }
                }
            }
        }
    }
}

@Composable
private fun NewProjectCard(currentMinor: Int, onAdd: (LifeProject) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var startMode by rememberSaveable { mutableStateOf("NOW") }
    var cadence by rememberSaveable { mutableStateOf("WEEKLY") }
    var weeklyTarget by rememberSaveable { mutableIntStateOf(1) }
    var minutes by rememberSaveable { mutableIntStateOf(60) }
    var frontline by rememberSaveable { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = ProjectLilac)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Add, null, tint = ProjectLilacStrong)
                Spacer(Modifier.size(8.dp))
                Text("Новий проєкт", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Наприклад: TNT, YouTube, новий сайт") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Коли почати", fontWeight = FontWeight.SemiBold, color = ProjectInk)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(selected = startMode == "NOW", onClick = { startMode = "NOW" }, label = { Text("Зараз") })
                FilterChip(selected = startMode == "NEXT", onClick = { startMode = "NEXT" }, label = { Text("З наступної глави") })
                FilterChip(selected = startMode == "LATER", onClick = { startMode = "LATER" }, label = { Text("Пізніше") })
            }

            Text("Ритм проєкту", fontWeight = FontWeight.SemiBold, color = ProjectInk)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(selected = cadence == "DAILY", onClick = { cadence = "DAILY"; weeklyTarget = 7 }, label = { Text("Щодня") })
                FilterChip(selected = cadence == "WEEKLY", onClick = { cadence = "WEEKLY"; if (weeklyTarget == 7) weeklyTarget = 2 }, label = { Text("N разів/тиждень") })
                FilterChip(selected = cadence == "FLEX", onClick = { cadence = "FLEX"; weeklyTarget = 1 }, label = { Text("Без графіка") })
            }

            if (cadence == "WEEKLY") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Разів на тиждень", modifier = Modifier.weight(1f), color = ProjectMuted)
                    OutlinedButton(onClick = { weeklyTarget = (weeklyTarget - 1).coerceAtLeast(1) }) { Text("−") }
                    Text(weeklyTarget.toString(), modifier = Modifier.padding(horizontal = 14.dp), fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { weeklyTarget = (weeklyTarget + 1).coerceAtMost(14) }) { Text("+") }
                }
            }

            Text("Тривалість типової сесії", fontWeight = FontWeight.SemiBold, color = ProjectInk)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf(15, 30, 45, 60, 90, 120).forEach { value ->
                    FilterChip(
                        selected = minutes == value,
                        onClick = { minutes = value },
                        label = { Text(if (value < 60) "$value хв" else if (value == 60) "1 год" else if (value == 90) "1.5 год" else "2 год", fontSize = 9.sp) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Типово це Передова", fontWeight = FontWeight.SemiBold, color = ProjectInk)
                    Text("Регулярні дії цього проєкту створюють прямий результат", color = ProjectMuted, fontSize = 10.sp)
                }
                Switch(checked = frontline, onCheckedChange = { frontline = it })
            }

            Button(
                onClick = {
                    val startMinor = when (startMode) {
                        "NEXT" -> currentMinor + 1
                        "LATER" -> currentMinor + 99
                        else -> currentMinor
                    }
                    onAdd(
                        LifeProject(
                            id = "project_" + System.currentTimeMillis(),
                            name = name.trim(),
                            status = if (startMode == "LATER") "PAUSED" else "ACTIVE",
                            startMinor = startMinor,
                            cadence = cadence,
                            weeklyTarget = if (cadence == "DAILY") 7 else weeklyTarget,
                            minutes = minutes,
                            frontline = frontline,
                            createdAt = System.currentTimeMillis()
                        )
                    )
                    name = ""
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ProjectLilacStrong),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Outlined.Add, null)
                Spacer(Modifier.size(6.dp))
                Text("Створити проєкт")
            }
        }
    }
}

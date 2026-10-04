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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

internal data class LifeProject(
    val id: String,
    val name: String,
    val status: String,
    val startMinor: Int,
    val cadence: String,
    val weeklyTarget: Int,
    val minutes: Int,
    val frontline: Boolean,
    val createdAt: Long,
    val goal: String = ""
)

internal data class ProjectStage(
    val id: String,
    val projectId: String,
    val title: String,
    val done: Boolean,
    val createdAt: Long
)

private data class ProjectChapterStat(
    val projectId: String,
    val selfMinor: Int,
    val chapterTitle: String,
    val done: Int,
    val planned: Int,
    val minutes: Int,
    val result: String,
    val finishedAt: Long
)

private data class ProjectNumbers(
    val done: Int,
    val planned: Int,
    val minutes: Int
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
                    createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                    goal = o.optString("goal", "")
                )
            }
        } catch (_: Exception) {}
    }

    val defaults = listOf(
        LifeProject("project_kdp", "KDP", "ACTIVE", 0, "WEEKLY", 2, 120, true, System.currentTimeMillis(), "Стабільно створювати й публікувати книжки"),
        LifeProject("project_pfu", "PFU", "ACTIVE", 0, "WEEKLY", 3, 60, true, System.currentTimeMillis(), "Запустити й розвивати Pet Friendly Ukraine")
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
            put("frontline", p.frontline); put("createdAt", p.createdAt); put("goal", p.goal)
        })
    }
    prefs.edit().putString("life_projects", arr.toString()).apply()
}

internal fun loadProjectStages(prefs: SharedPreferences): List<ProjectStage> {
    val raw = prefs.getString("project_stages", "[]") ?: "[]"
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            ProjectStage(
                id = o.optString("id", "stage_$i"),
                projectId = o.optString("projectId", ""),
                title = o.optString("title", "Етап"),
                done = o.optBoolean("done", false),
                createdAt = o.optLong("createdAt", System.currentTimeMillis())
            )
        }
    } catch (_: Exception) { emptyList() }
}

internal fun persistProjectStages(prefs: SharedPreferences, stages: List<ProjectStage>) {
    val arr = JSONArray()
    stages.forEach { stage ->
        arr.put(JSONObject().apply {
            put("id", stage.id); put("projectId", stage.projectId); put("title", stage.title)
            put("done", stage.done); put("createdAt", stage.createdAt)
        })
    }
    prefs.edit().putString("project_stages", arr.toString()).apply()
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

internal fun snapshotProjectWeek(prefs: SharedPreferences) {
    val currentMinor = currentSelfMinor(prefs)
    val current = projectNumbersFromActions(prefs)
    if (current.isEmpty()) return

    val raw = prefs.getString("project_week_history", "[]") ?: "[]"
    val arr = try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
    current.forEach { (projectId, numbers) ->
        if (numbers.done > 0 || numbers.planned > 0) {
            arr.put(JSONObject().apply {
                put("projectId", projectId)
                put("selfMinor", currentMinor)
                put("done", numbers.done)
                put("planned", numbers.planned)
                put("minutes", numbers.minutes)
                put("at", System.currentTimeMillis())
            })
        }
    }
    prefs.edit().putString("project_week_history", arr.toString()).apply()
}

internal fun resetWeeklyActionsForNewChapter(prefs: SharedPreferences) {
    val raw = prefs.getString("life_week_actions", "[]") ?: "[]"
    try {
        val arr = JSONArray(raw)
        for (i in 0 until arr.length()) {
            arr.getJSONObject(i).put("completed", 0)
        }
        prefs.edit().putString("life_week_actions", arr.toString()).apply()
    } catch (_: Exception) {}
}

internal fun snapshotProjectsForCompletedChapter(prefs: SharedPreferences, chapterTitle: String) {
    val currentMinor = currentSelfMinor(prefs)
    val projects = loadLifeProjects(prefs)
    val currentWeek = projectNumbersFromActions(prefs)
    val historicWeeks = projectWeekNumbers(prefs, currentMinor)
    val existing = loadProjectChapterStats(prefs).toMutableList()

    projects.forEach { project ->
        if (project.startMinor > currentMinor) return@forEach
        val a = historicWeeks[project.id] ?: ProjectNumbers(0, 0, 0)
        val b = currentWeek[project.id] ?: ProjectNumbers(0, 0, 0)
        val result = prefs.getString("project_current_result_" + project.id, "") ?: ""
        val total = ProjectNumbers(a.done + b.done, a.planned + b.planned, a.minutes + b.minutes)

        if (project.status == "ACTIVE" || total.planned > 0 || total.done > 0 || result.isNotBlank()) {
            existing.removeAll { it.projectId == project.id && it.selfMinor == currentMinor }
            existing.add(
                ProjectChapterStat(
                    projectId = project.id,
                    selfMinor = currentMinor,
                    chapterTitle = chapterTitle,
                    done = total.done,
                    planned = total.planned,
                    minutes = total.minutes,
                    result = result,
                    finishedAt = System.currentTimeMillis()
                )
            )
            prefs.edit().remove("project_current_result_" + project.id).apply()
        }
    }
    persistProjectChapterStats(prefs, existing)
}

private fun projectNumbersFromActions(prefs: SharedPreferences): Map<String, ProjectNumbers> {
    val raw = prefs.getString("life_week_actions", "[]") ?: "[]"
    val acc = linkedMapOf<String, ProjectNumbers>()
    try {
        val arr = JSONArray(raw)
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            var projectId = o.optString("projectId", "")
            if (projectId.isBlank()) {
                projectId = when (o.optString("sphere", "")) {
                    "KDP" -> "project_kdp"
                    "PFU" -> "project_pfu"
                    else -> ""
                }
            }
            if (projectId.isBlank()) continue
            val target = o.optInt("target", 0).coerceAtLeast(0)
            val done = o.optInt("completed", 0).coerceAtLeast(0)
            val minutes = o.optInt("minutes", 0).coerceAtLeast(0) * done
            val old = acc[projectId] ?: ProjectNumbers(0, 0, 0)
            acc[projectId] = ProjectNumbers(old.done + done, old.planned + target, old.minutes + minutes)
        }
    } catch (_: Exception) {}
    return acc
}

private fun projectWeekNumbers(prefs: SharedPreferences, selfMinor: Int): Map<String, ProjectNumbers> {
    val raw = prefs.getString("project_week_history", "[]") ?: "[]"
    val acc = linkedMapOf<String, ProjectNumbers>()
    try {
        val arr = JSONArray(raw)
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (o.optInt("selfMinor", -1) != selfMinor) continue
            val id = o.optString("projectId", "")
            if (id.isBlank()) continue
            val old = acc[id] ?: ProjectNumbers(0, 0, 0)
            acc[id] = ProjectNumbers(
                old.done + o.optInt("done", 0),
                old.planned + o.optInt("planned", 0),
                old.minutes + o.optInt("minutes", 0)
            )
        }
    } catch (_: Exception) {}
    return acc
}

private fun loadProjectChapterStats(prefs: SharedPreferences): List<ProjectChapterStat> {
    val raw = prefs.getString("project_chapter_history", "[]") ?: "[]"
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            ProjectChapterStat(
                projectId = o.optString("projectId", ""),
                selfMinor = o.optInt("selfMinor", 0),
                chapterTitle = o.optString("chapterTitle", "Глава"),
                done = o.optInt("done", 0),
                planned = o.optInt("planned", 0),
                minutes = o.optInt("minutes", 0),
                result = o.optString("result", ""),
                finishedAt = o.optLong("finishedAt", 0L)
            )
        }
    } catch (_: Exception) { emptyList() }
}

private fun persistProjectChapterStats(prefs: SharedPreferences, stats: List<ProjectChapterStat>) {
    val arr = JSONArray()
    stats.forEach { stat ->
        arr.put(JSONObject().apply {
            put("projectId", stat.projectId); put("selfMinor", stat.selfMinor); put("chapterTitle", stat.chapterTitle)
            put("done", stat.done); put("planned", stat.planned); put("minutes", stat.minutes)
            put("result", stat.result); put("finishedAt", stat.finishedAt)
        })
    }
    prefs.edit().putString("project_chapter_history", arr.toString()).apply()
}

internal fun projectItems(
    scope: androidx.compose.foundation.lazy.LazyListScope,
    prefs: SharedPreferences,
    projects: List<LifeProject>,
    stages: List<ProjectStage>,
    onChanged: (List<LifeProject>) -> Unit,
    onStagesChanged: (List<ProjectStage>) -> Unit,
    onCreateRoutine: (LifeProject) -> Unit
) {
    val currentMinor = currentSelfMinor(prefs)

    scope.item {
        Column {
            Text("Проєкти", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
            Text(
                "Проєкт живе довше за одну главу: ціль → етапи → регулярні дії → результат по кожній версії тебе.",
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
                    "Завершення цієї глави зафіксує результати проєктів у Я 2.$currentMinor і відкриє Я 2." + (currentMinor + 1),
                    color = ProjectMuted,
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(10.dp))
                val activeCount = projects.count { projectIsAvailable(it, currentMinor) }
                val futureCount = projects.count { it.status == "ACTIVE" && it.startMinor > currentMinor }
                Text("$activeCount активних · $futureCount заплановано", color = ProjectMintDark, fontWeight = FontWeight.SemiBold)
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
                prefs = prefs,
                project = project,
                projectStages = stages.filter { it.projectId == project.id },
                currentMinor = currentMinor,
                onUpdate = { updated ->
                    onChanged(projects.map { if (it.id == project.id) updated else it })
                },
                onStagesChanged = { changedForProject ->
                    onStagesChanged(stages.filterNot { it.projectId == project.id } + changedForProject)
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
    prefs: SharedPreferences,
    project: LifeProject,
    projectStages: List<ProjectStage>,
    currentMinor: Int,
    onUpdate: (LifeProject) -> Unit,
    onStagesChanged: (List<ProjectStage>) -> Unit,
    onCreateRoutine: () -> Unit
) {
    val future = project.startMinor > currentMinor
    val doneProject = project.status == "DONE"
    val paused = project.status == "PAUSED"
    val available = projectIsAvailable(project, currentMinor)
    val bg = when {
        doneProject -> Color(0xFFF1F2F1)
        future -> ProjectLilac
        paused -> ProjectPeach
        else -> Color.White
    }

    val archivedStats = remember(project.id, currentMinor) {
        loadProjectChapterStats(prefs).filter { it.projectId == project.id }.sortedByDescending { it.selfMinor }
    }
    val currentWeek = projectNumbersFromActions(prefs)[project.id] ?: ProjectNumbers(0, 0, 0)
    val oldWeeks = projectWeekNumbers(prefs, currentMinor)[project.id] ?: ProjectNumbers(0, 0, 0)
    val currentDone = currentWeek.done + oldWeeks.done
    val currentPlanned = currentWeek.planned + oldWeeks.planned
    val currentMinutes = currentWeek.minutes + oldWeeks.minutes
    val lifetimeDone = archivedStats.sumOf { it.done } + currentDone
    val lifetimeMinutes = archivedStats.sumOf { it.minutes } + currentMinutes
    val chapterCount = archivedStats.map { it.selfMinor }.distinct().size + if (project.startMinor <= currentMinor && !doneProject) 1 else 0
    val stageDone = projectStages.count { it.done }
    val stageProgress = if (projectStages.isEmpty()) 0f else stageDone / projectStages.size.toFloat()

    var goalDraft by rememberSaveable(project.id) { mutableStateOf(project.goal) }
    var newStage by rememberSaveable(project.id) { mutableStateOf("") }
    var resultDraft by rememberSaveable(project.id) { mutableStateOf(prefs.getString("project_current_result_" + project.id, "") ?: "") }

    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = bg), border = CardDefaults.outlinedCardBorder()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(42.dp).background(if (available) ProjectMint else Color.White.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.WorkOutline, null, tint = if (available) ProjectMintDark else ProjectMuted)
                }
                Spacer(Modifier.size(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(project.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProjectInk)
                    val stateText = when {
                        doneProject -> "Завершено"
                        paused -> "На паузі"
                        future -> "Старт з Я 2." + project.startMinor
                        else -> "Активний · Я 2.$currentMinor"
                    }
                    Text(stateText, color = ProjectMuted, fontSize = 10.sp)
                }
                if (doneProject) Icon(Icons.Outlined.CheckCircle, null, tint = ProjectMintDark)
                else if (paused) Icon(Icons.Outlined.PauseCircle, null, tint = Color(0xFFA66C22))
                else Icon(Icons.Outlined.PlayCircle, null, tint = ProjectMintDark)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                ProjectStat(Modifier.weight(1f), formatProjectMinutes(lifetimeMinutes), "всього")
                ProjectStat(Modifier.weight(1f), lifetimeDone.toString(), "дій")
                ProjectStat(Modifier.weight(1f), chapterCount.toString(), "глав")
                ProjectStat(Modifier.weight(1f), if (projectStages.isEmpty()) "—" else (stageProgress * 100).toInt().toString() + "%", "етапи")
            }

            if (projectStages.isNotEmpty()) {
                LinearProgressIndicator(
                    progress = { stageProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(7.dp),
                    color = ProjectMintDark
                )
                Text("Етапів завершено: $stageDone із ${projectStages.size}", color = ProjectMuted, fontSize = 10.sp)
            } else {
                Text("Додай етапи — тоді з’явиться реальний % завершення проєкту.", color = ProjectMuted, fontSize = 10.sp)
            }

            OutlinedTextField(
                value = goalDraft,
                onValueChange = { goalDraft = it },
                label = { Text("Головна ціль проєкту") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            if (goalDraft != project.goal) {
                OutlinedButton(onClick = { onUpdate(project.copy(goal = goalDraft.trim())) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Зберегти ціль")
                }
            }

            val cadence = when (project.cadence) {
                "DAILY" -> "Щодня"
                "WEEKLY" -> project.weeklyTarget.toString() + " раз(и) на тиждень"
                else -> "Без постійного графіка"
            }
            Text("$cadence · ${project.minutes} хв на сесію", color = ProjectMuted, fontSize = 11.sp)
            if (project.frontline) Text("ПЕРЕДОВА за замовчуванням", color = ProjectLilacStrong, fontSize = 10.sp, fontWeight = FontWeight.Bold)

            Text("Етапи / підпроєкти", fontWeight = FontWeight.Bold, color = ProjectInk)
            projectStages.sortedBy { it.createdAt }.forEach { stage ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = stage.done,
                        onClick = {
                            onStagesChanged(projectStages.map { if (it.id == stage.id) it.copy(done = !it.done) else it })
                        },
                        label = { Text(if (stage.done) "✓" else "○") }
                    )
                    Spacer(Modifier.size(6.dp))
                    Text(stage.title, modifier = Modifier.weight(1f), color = if (stage.done) ProjectMuted else ProjectInk, fontSize = 12.sp)
                    IconButton(onClick = { onStagesChanged(projectStages.filterNot { it.id == stage.id }) }) {
                        Icon(Icons.Outlined.DeleteOutline, "Видалити", tint = Color(0xFFB66A6A))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newStage,
                    onValueChange = { newStage = it },
                    label = { Text("Новий етап") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.size(6.dp))
                Button(
                    onClick = {
                        onStagesChanged(projectStages + ProjectStage("stage_" + System.currentTimeMillis(), project.id, newStage.trim(), false, System.currentTimeMillis()))
                        newStage = ""
                    },
                    enabled = newStage.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = ProjectMintDark)
                ) { Icon(Icons.Outlined.Add, null) }
            }

            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ProjectMint)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Поточна глава · Я 2.$currentMinor", fontWeight = FontWeight.Bold, color = ProjectInk)
                    Text("$currentDone/$currentPlanned дій · " + formatProjectMinutes(currentMinutes), color = ProjectMuted, fontSize = 11.sp)
                    OutlinedTextField(
                        value = resultDraft,
                        onValueChange = { resultDraft = it },
                        label = { Text("Що створено / який результат у цій главі") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    OutlinedButton(
                        onClick = { prefs.edit().putString("project_current_result_" + project.id, resultDraft.trim()).apply() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Зберегти результат глави") }
                }
            }

            if (archivedStats.isNotEmpty()) {
                Text("Історія версій", fontWeight = FontWeight.Bold, color = ProjectInk)
                archivedStats.take(4).forEach { stat ->
                    Column(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Row {
                            Text("Я 2." + stat.selfMinor, fontWeight = FontWeight.Bold, color = ProjectLilacStrong)
                            Spacer(Modifier.weight(1f))
                            Text("${stat.done}/${stat.planned} · " + formatProjectMinutes(stat.minutes), color = ProjectMuted, fontSize = 10.sp)
                        }
                        Text(stat.chapterTitle, color = ProjectMuted, fontSize = 10.sp)
                        if (stat.result.isNotBlank()) Text(stat.result, color = ProjectInk, fontSize = 11.sp)
                    }
                }
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
            } else if (future && !doneProject && !paused) {
                OutlinedButton(onClick = { onUpdate(project.copy(startMinor = currentMinor)) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Почати вже в цій главі")
                }
            }

            if (!doneProject) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (paused) {
                        OutlinedButton(onClick = { onUpdate(project.copy(status = "ACTIVE", startMinor = if (project.startMinor > currentMinor) currentMinor else project.startMinor)) }, modifier = Modifier.weight(1f)) { Text("Продовжити") }
                    } else {
                        OutlinedButton(onClick = { onUpdate(project.copy(status = "PAUSED")) }, modifier = Modifier.weight(1f)) { Text("Пауза") }
                    }
                    OutlinedButton(onClick = { onUpdate(project.copy(status = "DONE")) }, modifier = Modifier.weight(1f)) { Text("Завершити") }
                }
            }
        }
    }
}

@Composable
private fun ProjectStat(modifier: Modifier, value: String, label: String) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.75f))) {
        Column(Modifier.padding(vertical = 9.dp, horizontal = 5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = ProjectInk, fontSize = 13.sp)
            Text(label, color = ProjectMuted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun NewProjectCard(currentMinor: Int, onAdd: (LifeProject) -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var goal by rememberSaveable { mutableStateOf("") }
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

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("TNT, YouTube, новий сайт…") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = goal, onValueChange = { goal = it }, label = { Text("Головна ціль проєкту") }, modifier = Modifier.fillMaxWidth(), minLines = 2)

            Text("Коли почати", fontWeight = FontWeight.SemiBold, color = ProjectInk)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = startMode == "NOW", onClick = { startMode = "NOW" }, label = { Text("Зараз") })
                FilterChip(selected = startMode == "NEXT", onClick = { startMode = "NEXT" }, label = { Text("З наступної глави") })
                FilterChip(selected = startMode == "LATER", onClick = { startMode = "LATER" }, label = { Text("Пізніше") })
            }

            Text("Ритм проєкту", fontWeight = FontWeight.SemiBold, color = ProjectInk)
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
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
                            createdAt = System.currentTimeMillis(),
                            goal = goal.trim()
                        )
                    )
                    name = ""
                    goal = ""
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

private fun formatProjectMinutes(minutes: Int): String {
    if (minutes <= 0) return "0 хв"
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h == 0 -> "$m хв"
        m == 0 -> "$h год"
        else -> "$h год $m хв"
    }
}

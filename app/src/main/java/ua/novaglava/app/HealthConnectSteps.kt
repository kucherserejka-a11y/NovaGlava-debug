package ua.novaglava.app

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.PermissionController
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

private val HcInk = Color(0xFF23342F)
private val HcMuted = Color(0xFF718078)
private val HcMint = Color(0xFFDFF1E5)
private val HcMintDark = Color(0xFF436D57)
private val HcPeach = Color(0xFFFFE9D2)
private val HcLilac = Color(0xFFEDE7FF)

private data class DogWalkEntry(
    val at: Long,
    val durationMinutes: Int,
    val steps: Int
)

@Composable
internal fun HealthConnectAndWalkCard(
    prefs: SharedPreferences,
    onStepsUpdated: (Int) -> Unit,
    onWalkCompleted: (Int, Int) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    val sdkStatus = remember { HealthConnectClient.getSdkStatus(context) }
    val client = remember(sdkStatus) {
        if (sdkStatus == HealthConnectClient.SDK_AVAILABLE) HealthConnectClient.getOrCreate(context) else null
    }
    val readStepsPermission = remember { HealthPermission.getReadPermission(StepsRecord::class) }

    var hasPermission by remember { mutableStateOf(false) }
    var todaySteps by remember { mutableIntStateOf(prefs.getInt("life_auto_steps_today", 0)) }
    var status by remember { mutableStateOf("") }
    var walkStart by remember { mutableLongStateOf(prefs.getLong("dog_walk_start_ms", 0L)) }
    var walks by remember { mutableStateOf(loadDogWalks(prefs)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        hasPermission = granted.contains(readStepsPermission)
        if (hasPermission && client != null) {
            scope.launch {
                val steps = runCatching { readTodaySteps(client) }.getOrDefault(0L).toInt()
                todaySteps = steps
                prefs.edit().putInt("life_auto_steps_today", steps).apply()
                onStepsUpdated(steps)
                status = "Health Connect підключено."
            }
        } else {
            status = "Доступ до кроків не надано. Ручний ввід залишається доступним."
        }
    }

    suspend fun refreshPermissionAndSteps() {
        if (client == null) return
        val granted = runCatching { client.permissionController.getGrantedPermissions() }.getOrDefault(emptySet())
        hasPermission = granted.contains(readStepsPermission)
        if (hasPermission) {
            val steps = runCatching { readTodaySteps(client) }.getOrDefault(0L).toInt()
            todaySteps = steps
            prefs.edit().putInt("life_auto_steps_today", steps).apply()
            onStepsUpdated(steps)
        }
    }

    LaunchedEffect(client) {
        if (client != null) refreshPermissionAndSteps()
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (hasPermission) HcMint else HcLilac)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Outlined.HealthAndSafety, null, tint = HcMintDark, modifier = Modifier.size(28.dp))
                Spacer(Modifier.size(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Health Connect", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HcInk)
                    Text(
                        if (sdkStatus == HealthConnectClient.SDK_AVAILABLE) "Автоматичні кроки з телефона та сумісних пристроїв" else "Health Connect недоступний або потребує оновлення",
                        color = HcMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (sdkStatus == HealthConnectClient.SDK_AVAILABLE) {
                if (!hasPermission) {
                    Button(
                        onClick = { permissionLauncher.launch(setOf(readStepsPermission)) },
                        colors = ButtonDefaults.buttonColors(containerColor = HcMintDark),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Дозволити читання кроків")
                    }
                } else {
                    Text(todaySteps.toString(), fontSize = 34.sp, fontWeight = FontWeight.Bold, color = HcInk)
                    Text("кроків сьогодні", color = HcMuted)
                    LinearProgressIndicator(
                        progress = { (todaySteps / 8000f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(7.dp),
                        color = HcMintDark
                    )
                    OutlinedButton(
                        onClick = { scope.launch { refreshPermissionAndSteps() } },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Оновити кроки") }
                }
            }
            if (status.isNotBlank()) Text(status, color = HcMuted, fontSize = 10.sp)
        }
    }

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = HcPeach)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row {
                Icon(Icons.Outlined.Pets, null, tint = Color(0xFFA66C22), modifier = Modifier.size(28.dp))
                Spacer(Modifier.size(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("Прогулянка з собакою", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HcInk)
                    Text(
                        if (walkStart > 0L) "Прогулянка триває · ${elapsedMinutes(walkStart)} хв" else "Запиши окремо час і кроки саме прогулянки",
                        color = HcMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (walkStart == 0L) {
                Button(
                    onClick = {
                        walkStart = System.currentTimeMillis()
                        prefs.edit().putLong("dog_walk_start_ms", walkStart).apply()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA66C22)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Outlined.DirectionsWalk, null)
                    Spacer(Modifier.size(6.dp))
                    Text("Почати прогулянку")
                }
            } else {
                Button(
                    onClick = {
                        val started = walkStart
                        scope.launch {
                            val ended = System.currentTimeMillis()
                            val minutes = ((ended - started) / 60000L).toInt().coerceAtLeast(1)
                            val steps = if (client != null && hasPermission) {
                                runCatching { readStepsBetween(client, Instant.ofEpochMilli(started), Instant.ofEpochMilli(ended)) }.getOrDefault(0L).toInt()
                            } else 0
                            val entry = DogWalkEntry(ended, minutes, steps)
                            walks = listOf(entry) + walks
                            persistDogWalks(prefs, walks.take(100))
                            prefs.edit().putLong("dog_walk_start_ms", 0L).apply()
                            walkStart = 0L
                            onWalkCompleted(steps, minutes)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA66C22)),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Завершити прогулянку") }
            }

            if (walks.isNotEmpty()) {
                Text("Останні прогулянки", fontWeight = FontWeight.SemiBold, color = HcInk)
                walks.take(5).forEach { walk ->
                    val suffix = if (walk.steps > 0) " · ${walk.steps} кроків" else ""
                    Text("• ${walk.durationMinutes} хв$suffix", color = HcMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

private suspend fun readTodaySteps(client: HealthConnectClient): Long {
    val zone = ZoneId.systemDefault()
    val start = LocalDate.now(zone).atStartOfDay(zone).toInstant()
    return readStepsBetween(client, start, Instant.now())
}

private suspend fun readStepsBetween(client: HealthConnectClient, start: Instant, end: Instant): Long {
    if (!end.isAfter(start)) return 0L
    val response = client.aggregate(
        AggregateRequest(
            metrics = setOf(StepsRecord.COUNT_TOTAL),
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )
    )
    return response[StepsRecord.COUNT_TOTAL] ?: 0L
}

private fun elapsedMinutes(start: Long): Long {
    if (start <= 0L) return 0L
    return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start).coerceAtLeast(0)
}

private fun loadDogWalks(prefs: SharedPreferences): List<DogWalkEntry> {
    val raw = prefs.getString("dog_walk_history", "[]") ?: "[]"
    return try {
        val arr = JSONArray(raw)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            DogWalkEntry(
                at = o.optLong("at", 0L),
                durationMinutes = o.optInt("minutes", 0),
                steps = o.optInt("steps", 0)
            )
        }
    } catch (_: Exception) { emptyList() }
}

private fun persistDogWalks(prefs: SharedPreferences, walks: List<DogWalkEntry>) {
    val arr = JSONArray()
    walks.forEach { walk ->
        arr.put(JSONObject().apply {
            put("at", walk.at)
            put("minutes", walk.durationMinutes)
            put("steps", walk.steps)
        })
    }
    prefs.edit().putString("dog_walk_history", arr.toString()).apply()
}

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFFAFBF8)).padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.HealthAndSafety, null, tint = HcMintDark, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Як «Нова глава» використовує Health Connect", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = HcInk)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Застосунок запитує лише читання кількості кроків. Дані використовуються для показу щоденної активності, прогресу та кроків під час прогулянок з собакою.",
                        color = HcInk,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Дані зберігаються локально на телефоні у твоєму профілі «Нова глава» і не передаються рекламодавцям чи стороннім сервісам. Доступ можна відкликати в налаштуваннях Health Connect.",
                        color = HcMuted,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { finish() },
                        colors = ButtonDefaults.buttonColors(containerColor = HcMintDark)
                    ) { Text("Зрозуміло") }
                }
            }
        }
    }
}

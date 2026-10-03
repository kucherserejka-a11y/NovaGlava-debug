# Нова глава — Android MVP 0.1

Перший нативний Android-прототип у стилі обраного дизайну №4.

## Що вже працює
- Головний екран поточного сезону / глави.
- Прогрес «День N із 30».
- Вибір стану «Я сьогодні».
- SWITCH-перемикач.
- Екран «Передова» з 3 щоденними діями та чекбоксами.
- Екран результатів / аналітики.
- Екран глави з кнопкою «Завершити день».
- Локальне збереження стану через SharedPreferences.
- Нижня навігація.
- Повністю офлайн, без облікового запису.

## Запуск
1. Відкрийте папку `NovaGlavaAndroid` в Android Studio.
2. Дочекайтеся Gradle Sync.
3. Запустіть на Android 8.0+ (API 26+) або емуляторі.

## Технології
- Kotlin
- Jetpack Compose
- Material 3
- SharedPreferences для MVP-персистентності

## Наступний етап
Для версії 0.2 варто додати майстер створення сезону, редагування власного тексту/ролі, нагадування, Vision Feed, таймер «Передова» та щотижневий огляд.

## Online APK build via GitHub Actions

This project includes `.github/workflows/build-apk.yml`.

1. Upload the project to a GitHub repository.
2. Open **Actions → Build Android APK**.
3. Click **Run workflow**.
4. When the run finishes, open it and download the **NovaGlava-debug-apk** artifact.
5. Unzip the artifact to get `app-debug.apk`.

No local Android Studio or Android SDK is required for this cloud build.

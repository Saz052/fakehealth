# 📦 Сборка FakeHealth через GitHub Actions

Автоматическая сборка APK при каждом пуше, релизе или вручную. APK доступен в **Artifacts** и **Releases**.

---

## 🚀 Быстрый старт (5 минут)

### 1. Создайте репозиторий на GitHub
```bash
# На github.com → New repository
# Name: fakehealth (или любое)
# Public / Private — на выбор
# НЕ инициализируйте README, .gitignore, license (у нас уже есть)
```

### 2. Запушьте проект
```cmd
cd C:\Users\saz\FakeHealth

git init
git add .
git commit -m "Initial commit: FakeHealth Samsung Health clone with secret editor"

git branch -M main
git remote add origin https://github.com/ВАШ_НИК/fakehealth.git
git push -u origin main
```

### 3. Готово! 🎉
- Зайдите в репозиторий → вкладка **Actions**
- Увидите запущенный workflow **Build Android APK**
- Через 2–4 минуты APK появится в **Artifacts** (справа в сайдбаре)

---

## 📥 Где качать APK

| Способ | Где найти | Хранится |
|--------|-----------|----------|
| **Artifacts** (любой коммит) | Actions → Build Android APK → Artifacts → `fakehealth-debug-apk` | 30 дней |
| **Releases** (только теги `v*`) | Releases → Assets → `app-debug.apk` | Навсегда |
| **Workflow Dispatch** | Actions → Build Android APK → Run workflow | 30 дней |

---

## 🏷 Как сделать релиз (автоматический)

```cmd
# Создайте тег версии
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```

GitHub Actions:
1. Соберёт APK
2. Создаст **Release** с названием `FakeHealth v1.0.0`
3. Прикрепит `app-debug.apk` как asset
4. Сгенерирует заметки релиза (чangelog из коммитов)

---

## ⚙️ Настройка (опционально)

### Подпись APK (Release build)
Добавьте в **Settings → Secrets and variables → Actions → Repository secrets**:

| Secret | Описание |
|--------|----------|
| `KEYSTORE_BASE64` | `base64 -w0 release.keystore` |
| `KEYSTORE_PASSWORD` | Пароль от keystore |
| `KEY_ALIAS` | Алиас ключа |
| `KEY_PASSWORD` | Пароль ключа |

И добавьте в `build.yml` перед `assembleRelease`:

```yaml
- name: Decode Keystore
  run: echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > release.keystore

- name: Build Release APK
  run: ./gradlew assembleRelease --no-daemon
  env:
    KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
    KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
    KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
```

### Кастомный package name / app name
В `app/build.gradle.kts`:
```kotlin
defaultConfig {
    applicationId = "com.ваш.пакет"  // измените
    // ...
}
```
В `strings.xml`:
```xml
<string name="app_name">Ваше Название</string>
```

---

## 🔧 Локальная проверка перед пушем

```cmd
cd C:\Users\saz\FakeHealth

# Проверка форматирования (ktlint)
./gradlew ktlintCheck

# Запуск тестов
./gradlew test

# Локальная сборка
./gradlew assembleDebug
```

---

## 📋 Что делает workflow (`.github/workflows/build.yml`)

| Шаг | Назначение |
|-----|------------|
| `actions/checkout` | Клонирует репозиторий |
| `setup-java` | Устанавливает JDK 17 (Temurin) + кэш Gradle |
| `setup-android` | Android SDK API 35, build-tools, cmdline-tools |
| `cache` | Кэширует `~/.gradle` между запусками |
| `./gradlew assembleDebug` | Собирает debug APK |
| `upload-artifact` | Клаёт APK в Artifacts (30 дней) |
| `release` (при теге `v*`) | Создаёт GitHub Release с APK |

---

## ❗ Типичные проблемы

| Ошибка | Решение |
|--------|---------|
| `SDK location not found` | В workflow используется `setup-android` — SDK качается автоматически |
| `Gradle version mismatch` | Проверьте `gradle/wrapper/gradle-wrapper.properties` (у нас 8.5) |
| `KSP / Room errors` | Убедитесь, что `ksp` версия совпадает с Kotlin (у нас 2.0.0-1.0.13) |
| `Out of memory` | Добавьте в `gradle.properties`: `org.gradle.jvmargs=-Xmx3g` |

---

## 🔒 Приватность

- **Private repo** — Actions работает бесплатно (2000 мин/мес на GitHub Free)
- **Artifacts** видят только участники репозитория
- **Releases** в приватном репо — тоже приватные
- В APK **нет ключей/секретов** — только локальная БД Room + DataStore

---

## 📱 Установка на телефон

1. Скачайте `app-debug.apk` (из Artifacts или Releases)
2. Перешлите на телефон (Telegram, почта, кабель)
3. Откройте файл → «Установить» (разрешите «Неизвестные источники»)
4. Иконка **Samsung Health** появится в лаунчере

---

## 🎯 Полезные ссылки

- [GitHub Actions для Android](https://github.com/android-actions)
- [Gradle кэш в Actions](https://github.com/gradle/gradle-build-action)
- [Подписание APK в CI](https://docs.github.com/en/actions/security-guides/encrypted-secrets)

---

**Готово!** Теперь каждый `git push` даёт свежий APK, а `git push origin v1.0.0` — релиз с установщиком. 🚀
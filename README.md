# Mobile Tests — Wikipedia Android (BrowserStack)

Учебный проект мобильных автотестов курса QA.GURU. Тестовый набор для приложения **Wikipedia Alpha for Android** (`org.wikipedia.alpha`), исполняется удалённо на **BrowserStack App Automate**.

---

## Стек

| Слой | Что используется |
|---|---|
| Язык / сборка | Java 21, Gradle 9 (wrapper) |
| Драйвер | Appium java-client 9.4, Selenium 4.33 |
| Обёртка | Selenide 7.9 |
| Тест-раннер | JUnit 5 (junit-bom) |
| Отчёт | Allure 2.34 (Gradle-plugin 4.0.0), AspectJ weaver |
| Конфиг | Owner 1.0.12 + `credentials.properties` |
| Ферма устройств | BrowserStack App Automate |

---

## Что проверяется

`tests/SearchTests`:

1. **`successfulSearchTest`** — открыть поиск, ввести «Appium», убедиться что есть результаты.
2. **`searchJavaReturnsIslandAndLanguage`** — ввести «Java», убедиться что в выдаче есть статья про остров (`Indonesia`) и язык программирования (`programming language`) одновременно.

Каждый шаг (`Allure.step(...)`) завершается прикреплением скриншота к отчёту; в `@AfterEach` дополнительно сохраняются финальный скриншот, page source и ссылка на видео сессии BrowserStack.

---

## Подготовка

1. JDK 21 (toolchain пинится в `build.gradle`, при необходимости Gradle её скачает сам).
2. Аккаунт BrowserStack с активной App Automate подпиской (можно Free).
3. Загруженный в аккаунт APK — например, BrowserStack-овский WikipediaSample:

   ```bash
   curl -u "USER:KEY" -X POST \
     "https://api-cloud.browserstack.com/app-automate/upload" \
     -F "url=https://www.browserstack.com/app-automate/sample-apps/android/WikipediaSample.apk"
   # → {"app_url":"bs://abc123..."}
   ```

4. Заполнить `src/test/resources/credentials.properties` (шаблон рядом — `credentials.template.properties`):

   ```properties
   browserstack.userName=<your-bs-user>
   browserstack.accessKey=<your-bs-key>
   browserstack.app=bs://<hash>
   browserstack.appiumVersion=2.6.0

   phone=pixel

   device.pixel.name=Google Pixel 7
   device.pixel.osVersion=13.0

   device.samsung.name=Samsung Galaxy S22
   device.samsung.osVersion=12.0

   device.xiaomi.name=Xiaomi Redmi Note 11
   device.xiaomi.osVersion=11.0
   ```

---

## Запуск тестов

```bash
./gradlew test                                                    # все тесты на дефолтном устройстве (pixel)
./gradlew test -Dphone=samsung                                    # переключить профиль устройства
./gradlew test --tests "tests.SearchTests.successfulSearchTest"   # один тест
```

Доступные значения `-Dphone`: `pixel`, `samsung`, `xiaomi`. Добавить новый профиль = дописать пару строк `device.<имя>.name` / `device.<имя>.osVersion` в `credentials.properties`.

---

## Отчёт Allure

```bash
./gradlew allureServe       # сгенерировать и поднять локальный HTTP-сервер (URL выведется в консоль)
./gradlew allureReport      # сгенерировать в build/reports/allure-report/allureReport/
```

> Открывать `index.html` напрямую в браузере (через `file://`) **нельзя** — отчёт это SPA, который через `fetch` подтягивает свои `data/*.json`. Браузер заблокирует запросы. Только через `allureServe` или `python3 -m http.server` из папки отчёта.

В отчёте по каждому тесту видно: шаги Allure, скриншоты после каждого шага, page source, ссылку на запись сессии BrowserStack.

---

## Структура проекта

```
src/test/java/
├── config/
│   └── BrowserstackConfig.java   ← Owner-интерфейс над credentials.properties
├── drivers/
│   └── BrowserstackDriver.java   ← Selenide WebDriverProvider → AndroidDriver
├── helpers/
│   ├── Attach.java               ← @Attachment-обёртки для Allure
│   └── Browserstack.java         ← REST-вызов BrowserStack для URL видео
└── tests/
    ├── TestBase.java             ← @BeforeAll / @BeforeEach / @AfterEach
    └── SearchTests.java          ← сами тесты

src/test/resources/
├── credentials.properties        ← логины, ключи, app_url, профили устройств
└── credentials.template.properties
```

### Поток выполнения теста

1. `@BeforeAll` (`TestBase`) — задать `Configuration.browser = BrowserstackDriver` и таймаут Selenide.
2. `@BeforeEach` — регистрация AllureSelenide listener + `Selenide.open()` (это **точка**, где реально создаётся драйвер).
3. `BrowserstackDriver.createDriver()` читает значения из `BrowserstackConfig`, собирает W3C-capabilities (всё BrowserStack-овское — внутри `bstack:options`, всё Appium-овское — с префиксом `appium:`), возвращает `AndroidDriver` на `https://hub.browserstack.com/wd/hub`.
4. Тестовый метод — последовательность `Allure.step(...)`, каждый завершается `Attach.screenshotAs(...)`.
5. `@AfterEach` — забрать `sessionId` **до** `closeWebDriver()`, приложить last screenshot и page source, закрыть драйвер, дёрнуть BrowserStack REST на видео и приложить его как HTML-`<video>` тег.

---

## Параметры командной строки

| Флаг | Что делает | Дефолт |
|---|---|---|
| `-Dphone=<имя>` | Какой профиль устройства использовать (`pixel`/`samsung`/`xiaomi`) | `pixel` |

`-Dphone` доходит до JVM теста потому, что `tasks.test` в `build.gradle` явно его прокидывает. Если будете добавлять новые `-D…`-флаги — пробросьте их там же.

---

## Запуск в Jenkins

Билд-шаг **Invoke Gradle script**:

| Поле | Значение |
|---|---|
| Tasks | `clean test` |
| Switches | `-Dphone=pixel` *(или параметризованно — см. ниже)* |

**Без пробелов вокруг `=`** — `-Dphone = pixel` шелл разобьёт на три аргумента, и Gradle упадёт с `Task '=' not found`.

Для параметризованной джобы: *This project is parameterized* → *Choice Parameter* `phone` со значениями `pixel`/`samsung`/`xiaomi`, в Switches — `-Dphone=$phone`.

Отчёт — через `allure` Jenkins plugin поверх `build/allure-results/`.

---

## Известные подводные камни

- **Selenide зафиксирован на 7.9.3.** Selenide ≥ 7.10 тянет Selenium ≥ 4.34, где удалён интерфейс `org.openqa.selenium.ContextAware`, на который ещё ссылается Appium java-client 9.4.0 (последний релиз). Поднимать Selenide до выхода Appium 9.5/10 нельзя.
- **`AndroidDriver`, не `RemoteWebDriver`.** Иначе на BrowserStack-овском Appium падают `getPageSource` / `getElementAttribute` / `takeScreenshot` с `UnsupportedCommandException`.
- **`bstack:options.appiumVersion=2.6.0`** обязателен. По умолчанию BrowserStack даёт Appium 1.22, где не хватает половины W3C-эндпоинтов.
- **Allure plugin 4.0.0 + CLI 2.34** — комбинация специально подобрана. CLI 2.34 требует `--clean` для перезаписи папки отчёта, но плагин его не шлёт; в `build.gradle` есть `doFirst { delete ... }` для `allureReport`/`allureServe`, не удалять. CLI 3.x вообще несовместим с тем, как плагин формирует команду.
- Каталог устройств BrowserStack меняется. Перед добавлением профиля проверьте, что устройство там есть:
  ```bash
  curl -u "USER:KEY" https://api-cloud.browserstack.com/app-automate/devices.json
  ```
  Старые модели (Pixel 3, и т.п.) периодически вычищают.
- Локаторы в `SearchTests` верифицированы под `WikipediaSample.apk` от BrowserStack (сборка 2017 года, статьи не открываются, только поиск). На другом APK accessibility-id могут не совпасть.

# Mobile Tests — Wikipedia (BrowserStack, Android + iOS)

Учебный проект мобильных автотестов курса QA.GURU. Тест-сьют для приложения **Wikipedia** на Android (`org.wikipedia.alpha`) и iOS, исполняется удалённо на **BrowserStack App Automate**. Тесты одни и те же для обеих платформ — переключение происходит через параметр командной строки.

---

## Стек

| Слой | Что используется |
|---|---|
| Язык / сборка | Java 21, Gradle 9 (wrapper) |
| Драйвер | Appium java-client 9.4 (Android + iOS), Selenium 4.33 |
| Обёртка | Selenide 7.9 |
| Тест-раннер | JUnit 5 (junit-bom) |
| Отчёт | Allure 2.34 (Gradle-plugin 4.0.0), AspectJ weaver |
| Конфиг | Owner 1.0.12 + `credentials.properties` |
| Ферма устройств | BrowserStack App Automate (Android + iOS) |

---

## Что проверяется

`tests/SearchTests` (один и тот же код для обеих платформ):

1. **`successfulSearchTest`** — открыть поиск, ввести «Appium», убедиться что в выдаче есть результаты.
2. **`searchJavaReturnsIslandAndLanguage`** — ввести «Java», убедиться, что в выдаче одновременно есть статья про остров (`Indonesia`) и про язык программирования (`programming language`).

Каждый шаг (`Allure.step(...)`) завершается прикреплением скриншота к отчёту. В `@AfterEach` дополнительно сохраняются финальный скриншот, page source и ссылка на видео сессии BrowserStack.

---

## Кросс-платформенный дизайн

Тесты никогда не видят локаторов напрямую — только методы `SearchPage`:

```java
private final SearchPage search = PageFactory.searchPage();

step("Type 'Java'", () -> search.openSearchAndType("Java"));
step("Island in Indonesia",  () -> search.resultContaining("Indonesia").shouldBe(visible));
```

`PageFactory` смотрит на `config.platform()` и подсовывает либо `AndroidSearchPage` (UiAutomator2-локаторы), либо `IosSearchPage` (XCUITest-локаторы). Та же логика — у драйвера: `BrowserstackDriver` — это router, который делегирует `AndroidBrowserstackDriver` или `IosBrowserstackDriver`.

Добавить новую платформу = новый драйвер + новые PO-имплементации + строка в роутере и фабрике. Тесты не трогаются.

---

## Подготовка

1. JDK 21 (toolchain пинится в `build.gradle`, при необходимости Gradle её скачает сам).
2. Аккаунт BrowserStack с активной App Automate подпиской (Free достаточно).
3. Загруженные в аккаунт билды:

   ```bash
   # Android sample (Wikipedia)
   curl -u "USER:KEY" -X POST \
     "https://api-cloud.browserstack.com/app-automate/upload" \
     -F "url=https://www.browserstack.com/app-automate/sample-apps/android/WikipediaSample.apk"

   # iOS sample (BrowserStack-овский generic — публичный Wikipedia-iOS у них не лежит)
   curl -u "USER:KEY" -X POST \
     "https://api-cloud.browserstack.com/app-automate/upload" \
     -F "url=https://www.browserstack.com/app-automate/sample-apps/ios/BrowserStack-SampleApp.ipa"
   ```
   В ответе будет `{"app_url":"bs://..."}` — подставите в `credentials.properties`.

4. Заполнить `src/test/resources/credentials.properties` (шаблон рядом — `credentials.template.properties`).

---

## Запуск тестов

```bash
./gradlew test                                                    # default: platform=android → tag=android
./gradlew test -Dphone=samsung                                    # сменить устройство (Android)
./gradlew test -Dplatform=ios -Dphone=iphone14                    # iOS → tag=iphone (автоматически)
./gradlew test -Dtag=android -Dplatform=ios                       # override: гонять Android-тесты на iOS-платформе
./gradlew test --tests "tests.SearchTests.successfulSearchTest"   # один тест
```

| Флаг | Значения | Дефолт |
|---|---|---|
| `-Dplatform` | `android`, `ios` | `android` |
| `-Dphone` | `pixel`, `samsung`, `xiaomi`, `iphone14`, `iphone13`, `iphone17`, `iphone17max` | `pixel` |
| `-Dtag` | `android`, `iphone` (JUnit 5 `@Tag`) | выводится из `-Dplatform`: `android` → `android`, `ios` → `iphone` |

Тесты помечены: `@Tag("android")` — `SearchTests` (Wikipedia), `@Tag("iphone")` — `IosSampleAppTests` (BrowserStack-SampleApp). Тэг по умолчанию выводится из `-Dplatform`, то есть **указал платформу — получил только её тесты**. Если нужно явно фильтровать против дефолта — передай `-Dtag=...`.

Добавить новый профиль устройства = дописать `device.<имя>.name` и `device.<имя>.osVersion` в `credentials.properties`. Добавить новую платформу — секция `<имя>.platformName`/`<имя>.automationName`/`<имя>.app`.

---

## Отчёт Allure

```bash
./gradlew allureServe       # сгенерировать и поднять локальный HTTP-сервер
./gradlew allureReport      # сгенерировать в build/reports/allure-report/allureReport/
```

> Открывать `index.html` напрямую через `file://` **нельзя** — отчёт это SPA, который через `fetch` подтягивает `data/*.json`, и браузер блокирует запросы. Только через `allureServe` или `python3 -m http.server` из папки отчёта.

В отчёте по каждому тесту видно: шаги Allure, скриншоты после каждого шага, page source, ссылку на запись сессии BrowserStack.

---

## Структура проекта

```
src/test/java/
├── config/
│   └── BrowserstackConfig.java       ← Owner-интерфейс над credentials.properties
├── drivers/
│   ├── BrowserstackDriver.java       ← router (по config.platform())
│   ├── AndroidBrowserstackDriver.java
│   ├── IosBrowserstackDriver.java
│   └── CapabilitiesBuilder.java      ← общий код сборки W3C-capabilities
├── pages/
│   ├── SearchPage.java               ← интерфейс
│   ├── AndroidSearchPage.java        ← UiAutomator2 локаторы
│   ├── IosSearchPage.java            ← XCUITest локаторы
│   └── PageFactory.java              ← выбор имплементации
├── helpers/
│   ├── Attach.java                   ← @Attachment-обёртки для Allure
│   └── Browserstack.java             ← REST-вызов BrowserStack для URL видео
└── tests/
    ├── TestBase.java                 ← @BeforeAll / @BeforeEach / @AfterEach
    ├── SearchTests.java              ← Android Wikipedia (@Tag("android"))
    └── IosSampleAppTests.java       ← iOS BrowserStack-SampleApp (@Tag("iphone"))

src/test/resources/
├── credentials.properties            ← логины, ключи, apps, профили
└── credentials.template.properties
```

### Поток выполнения теста

1. `@BeforeAll` (`TestBase`) — задать `Configuration.browser = BrowserstackDriver.class.getName()`, таймаут Selenide.
2. `@BeforeEach` — регистрация AllureSelenide listener + `Selenide.open()` (точка, где реально создаётся драйвер).
3. `BrowserstackDriver.createDriver()` смотрит на `config.platform()` и делегирует `AndroidBrowserstackDriver` или `IosBrowserstackDriver`. Тот через `CapabilitiesBuilder` собирает W3C-capabilities (`bstack:options` блок + `appium:`-префикс) и возвращает `AndroidDriver` или `IOSDriver`.
4. Тестовый метод работает через `PageFactory.searchPage()` — получает реализацию `SearchPage` под текущую платформу. Каждый `step(...)` завершается `Attach.screenshotAs(...)`.
5. `@AfterEach` — забрать `sessionId` **до** `closeWebDriver()`, приложить last screenshot и page source, закрыть драйвер, дёрнуть BrowserStack REST на видео и приложить его как HTML-`<video>` тег.

---

## Запуск в Jenkins

Билд-шаг **Invoke Gradle script**:

| Поле | Значение |
|---|---|
| Tasks | `clean test` |
| Switches | `-Dplatform=android -Dphone=pixel` |

⚠ **Без пробелов вокруг `=`** — `-Dphone = pixel` шелл разобьёт на три аргумента, и Gradle упадёт с `Task '=' not found`.

Для параметризации: *This project is parameterized* → *Choice Parameter* `platform` (`android`/`ios`) и `phone` (`pixel`/`samsung`/`xiaomi`/`iphone14`/`iphone13`/`iphone17`/`iphone17max`). В Switches — `-Dplatform=$platform -Dphone=$phone` (плюс `-Dtag=$tag` если нужно фильтровать).

Отчёт — через `allure` Jenkins plugin поверх `build/allure-results/`.

---

## Известные подводные камни

- **Wikipedia-iOS публичного `.ipa` у BrowserStack нет.** В `credentials.properties` сейчас стоит их generic `BrowserStack-SampleApp.ipa` — этого хватает чтобы доказать что iOS-инфраструктура работает (сессия поднимается, дерево XCUITest читается), но локаторы `IosSearchPage` рассчитаны на реальное приложение Wikipedia. Чтобы Wikipedia-сценарии действительно проходили на iOS — нужно залить настоящий Wikipedia-iOS билд через `/app-automate/upload` и обновить `ios.app`.
- **Selenide пинится на 7.9.3.** Selenide ≥ 7.10 тянет Selenium ≥ 4.34, где удалён интерфейс `org.openqa.selenium.ContextAware`, на который ещё ссылается Appium java-client 9.4.0 (последний релиз). Поднимать Selenide до выхода Appium 9.5/10 нельзя.
- **`AndroidDriver` / `IOSDriver`, не `RemoteWebDriver`.** Иначе на BrowserStack-овском Appium падают `getPageSource` / `getElementAttribute` / `takeScreenshot` с `UnsupportedCommandException`.
- **`bstack:options.appiumVersion=2.6.0`** обязателен. По умолчанию BrowserStack даёт Appium 1.22, где не хватает половины W3C-эндпоинтов.
- **Allure plugin 4.0.0 + CLI 2.34** — комбинация специально подобрана. CLI 2.34 требует `--clean` для перезаписи папки отчёта, но плагин его не шлёт; в `build.gradle` есть `doFirst { delete ... }` для `allureReport`/`allureServe`, не удалять. CLI 3.x вообще несовместим с тем, как плагин формирует команду.
- Каталог устройств BrowserStack меняется. Перед добавлением профиля проверьте, что устройство там есть:
  ```bash
  curl -u "USER:KEY" https://api-cloud.browserstack.com/app-automate/devices.json
  ```
  Старые модели (Pixel 3 и т.п.) периодически вычищают.

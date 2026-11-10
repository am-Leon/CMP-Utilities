# App language

`am.leon.utilities.helpers.appLanguage`

Reads the language the app is being shown in, and sends the user somewhere to change it. Android 13+
and iOS have a per-app language page in Settings; Android 12 and below do not, so there the app
shows its own picker.

```kotlin
val openSettings = rememberAppLanguageSettingsLauncher()

if (openSettings != null) LanguageRow(onClick = openSettings)
else LanguagePicker(AppLanguage.entries, onPick = ::setAppLanguage)
```

`rememberAppLanguageSettingsLauncher()` returning `null` is the whole signal: a launcher means the
system owns the choice, `null` means the app does.

To read the current one:

```kotlin
val language = rememberAppLanguage(AppLanguage.entries) ?: AppLanguage.ENGLISH
```

It is nullable because the device can be set to a language the app does not ship. Which language to
fall back to is the app's decision, so the library does not make it.

---

## Android setup

Three separate things, each solving a different problem. None of them can be done from inside this
library, and nothing fails loudly when they are missing.

### 1. `localeConfig` — so the system page lists your languages

Android 13+ builds its per-app language list from `android:localeConfig`. Without it the page is
empty or missing, and `rememberAppLanguageSettingsLauncher()` sends the user somewhere useless.

```kotlin
// app/build.gradle.kts  (AGP 8.1+)
android {
    androidResources {
        generateLocaleConfig = true
    }
}
```

That generates the XML from the `values-*` folders you actually ship and wires it into the manifest.

### 2. `autoStoreLocales` — so the choice survives a cold start on Android 12 and below

A different problem from the one above, and it is the one people miss: `setAppLanguage` applies
immediately either way, but on API ≤ 32 AppCompat only *persists* it when this service is declared.
Without it the app comes back in the system language on the next launch.

```xml
<!-- app/src/main/AndroidManifest.xml, inside <application> -->
<service
    android:name="androidx.appcompat.app.AppLocalesMetadataHolderService"
    android:enabled="false"
    android:exported="false">
    <meta-data
        android:name="autoStoreLocales"
        android:value="true" />
</service>
```

### 3. `configChanges` — optional, avoids recreating the activity

```xml
<activity
    android:name=".MainActivity"
    android:configChanges="locale|layoutDirection" />
```

Declared, the activity is handed a new configuration and Compose recomposes. Not declared, Android
recreates the activity instead. **Both work** — `rememberAppLanguageTag()` is keyed on the
configuration and re-reads either way. This is only about cost.

### Resource folders

The tag is BCP-47 (`ar-EG`), the folder is not (`values-ar-rEG` — note the `r`).

| Tag       | Android / composeResources folder |
|-----------|-----------------------------------|
| `ar`      | `values-ar`                       |
| `ar-EG`   | `values-ar-rEG`                   |
| `zh-Hans` | `values-b+zh+Hans`                |

---

## iOS setup

List the tags in `Info.plist`. iOS builds the app's page in Settings from this key, and it is what
`currentAppLanguageTag()` reads back through `NSBundle.preferredLocalizations`.

```xml
<key>CFBundleLocalizations</key>
<array>
    <string>en</string>
    <string>ar</string>
</array>
```

A Compose Multiplatform app that keeps its strings in `composeResources` still needs this key —
iOS does not look inside those, so without it the app has no language page at all.

---

## More than two languages

`AppLanguage` is the pair almost every app here ships. An app with its own set declares it:

```kotlin
enum class ShopLanguage(
    override val tag: String, override val nativeName: String
) : IAppLanguage {
    ENGLISH("en", "English"),
    ARABIC("ar", "العربية"),
    TURKISH("tr", "Türkçe"),
    // …as many as you ship
}

val language = rememberAppLanguage(ShopLanguage.entries)
```

Everything takes `Iterable<IAppLanguage>`, so nothing else changes.

### Regions

Regional variants work — `ar-EG` and `ar-SA` are just tags — but matching is lookup, not guessing:

| Device reports | App ships              | Result     |                             |
|----------------|------------------------|------------|-----------------------------|
| `ar-EG`        | `ar`, `ar-EG`, `ar-SA` | `ar-EG`    | exact                       |
| `ar-KW`        | `ar`, `ar-EG`, `ar-SA` | `ar`       | region dropped              |
| `ar-KW`        | `ar-EG`, `ar-SA`       | **`null`** | nothing to fall back to     |
| `zh-Hans-CN`   | `zh-Hans`, `zh-Hant`   | `zh-Hans`  | region dropped, script kept |
| `zh`           | `zh-Hans`, `zh-Hant`   | **`null`** | no right answer             |
| `pt`           | `pt-BR`                | `pt-BR`    | the only `pt` shipped       |

**If you ship regional variants, ship the plain language too.** With `ar` present, every Arabic
device lands somewhere. Without it, only the exact regions you listed do — a Kuwaiti user gets
whatever your `?:` fallback says, which is usually English.

The two `null` rows are deliberate. Picking the first candidate would show a reader of one Chinese
script the other, which is worse than falling back.

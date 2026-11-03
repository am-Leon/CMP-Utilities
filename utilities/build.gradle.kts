import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.mavenPublish)
}

group = libs.versions.library.groupID.get()
version = libs.versions.library.version.get()

kotlin {
    // Explicitly enable sources JAR for all targets (required by Maven Central)
    withSourcesJar(true)

    android {
        namespace = "$group.${libs.versions.library.artifactID.get()}"
        compileSdk = 37
        minSdk = 26

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
        withHostTest { }
    }

    compilerOptions {
        freeCompilerArgs.addAll("-Xexpect-actual-classes", "-opt-in=kotlin.RequiresOptIn")
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.android.material)
        }

        commonMain.dependencies {
            // Compose
            api(libs.compose.ui)
            api(libs.compose.runtime)
            api(libs.compose.components.resources)

            // Kotlin
            api(libs.kotlin.coroutines.core)
            api(libs.kotlin.serialization.json)
            api(libs.kotlin.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}

/**
 * The string catalog is part of this library's API: consumers reach it as
 * `am.leon.utilities.generated.resources.Res.string.*`, the Compose Multiplatform
 * counterpart of the `R.string.*` that `androidMain/res` gives native Android consumers.
 *
 * Because it is API, renaming or removing a key is a breaking change for them.
 */
compose.resources {
    publicResClass = true
}

// StringResourceParityTest reads the string files straight from the source tree, which Gradle
// cannot infer. Without this, editing only a strings.xml leaves the test UP-TO-DATE and the guard
// never runs - exactly the case it exists to catch.
//
// The test itself walks every module in the repository; this list only tells Gradle when to re-run
// it, so a module that gains strings of its own adds its two trees here.
tasks.withType<AbstractTestTask>().configureEach {
    inputs.files(
        rootProject.files(
            "utilities/src/commonMain/composeResources",
            "utilities/src/androidMain/res"
        )
    ).withPropertyName("stringResourceParityInputs").withPathSensitivity(PathSensitivity.RELATIVE)
}

mavenPublishing {
    // Publish to Maven Central Portal
    publishToMavenCentral()

    // Only sign if we are NOT publishing to mavenLocal
    if (!project.gradle.startParameter.taskNames.any { it.contains("Local", ignoreCase = true) }) {
        // GPG sign all publications (required for Maven Central)
        signAllPublications()
    }

    // Coordinates
    coordinates(
        groupId = "io.github.am-leon",
        artifactId = "cmp-utilities",
        version = version.toString()
    )

    // POM metadata
    pom {
        name.set("Leon Utilities")
        description.set(providers.gradleProperty("POM_DESCRIPTION").get())
        inceptionYear.set("2026")
        url.set(providers.gradleProperty("POM_URL").get())

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("am-Leon")
                name.set("Abduelrahman Elemam")
                email.set("abduelrahman.elemam@gmail.com")
                organization.set("am-Leon")
                organizationUrl.set("https://github.com/am-Leon")
            }
        }

        scm {
            url = providers.gradleProperty("POM_URL").get()
            connection = "scm:git:git://github.com/am-Leon/CMP-Utilities.git"
            developerConnection = "scm:git:ssh://git@github.com/am-Leon/CMP-Utilities.git"
        }
    }
}

afterEvaluate {
    // Sets the language version to 21 on java compilation tasks.
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }

    // Sets the test logging to show when it starts and ends each task, this helps on the testing actions.
    tasks.withType<AbstractTestTask>().configureEach {
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            events("started", "skipped", "passed", "failed")
            showStandardStreams = true
        }
    }
}

// A publishing is the one step here that cannot be taken back, so it must never overtake the checks
// that gate it. `dependsOn` alone does not say that - it makes Gradle run both tasks, in whichever
// order it likes, which looks identical on a green build and uploads first on a red one.
tasks.withType<AbstractPublishToMaven>().configureEach {
    mustRunAfter(tasks.withType<AbstractTestTask>())
}

tasks.register("publishKMP") {
    group = libs.versions.library.artifactID.get()
    description = "Compile and test every target, then publish to Maven Central"

    dependsOn("compileEnvironment", "publishAllPublicationsToMavenCentralRepository")
}

tasks.register("compileEnvironment") {
    group = libs.versions.library.artifactID.get()
    description = "Compile every target and run every test"

    dependsOn(
        // IOS frameworks
        "compileKotlinIosArm64",
        "compileKotlinIosSimulatorArm64",
        // Android framework
        "compileAndroidMain",
        // The Kotlin plugin's aggregate: the android host tests plus the iOS simulator ones.
        // Named rather than listed out, so a target added later is covered without anyone
        // remembering to come back here. iosArm64 is not in it and cannot be - there is no device
        // to run on, so compiling it above is the whole check that one target gets.
        "allTests"
    )
}

tasks.register("compileAndMavenLocal") {
    group = libs.versions.library.artifactID.get()
    description = "Compile and test every target, then publish to maven local"

    // dependsOn rather than finalizedBy: a finalizer is built to run whatever the outcome, which is
    // the opposite of a gate. As a dependency it is the build that stops on a failing test, before
    // the publishing, and the rule above keeps the two in that order when both succeed.
    dependsOn("compileEnvironment", "publishToMavenLocal")
}

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

/**
 * The presentation layer of the library: the MVI ViewModel bases, the app-event controller and
 * the Compose entry points that bind them to a screen.
 *
 * It is published separately from `:utilities` on purpose. Everything here drags in Compose,
 * Koin and cmp-navigation, and the pure KMP consumers of `:utilities` (domain/data modules that
 * never touch a screen) should not inherit that graph.
 *
 * iosX64 is deliberately absent: neither `io.github.am-leon:cmp-navigation` nor
 * `io.insert-koin:koin-compose-viewmodel` publishes that target, so it cannot resolve.
 */
kotlin {
    // Explicitly enable sources JAR for all targets (required by Maven Central)
    withSourcesJar(true)

    android {
        namespace = "$group.utilities.presentation"
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
        commonMain.dependencies {
            api(projects.utilities)

            // Compose
            api(libs.compose.runtime)
            api(libs.compose.components.resources)

            // Lifecycle
            api(libs.android.lifecycle.viewmodel.compose)
            api(libs.android.lifecycle.runtime.compose)

            // Koin
            api(libs.koin.core)
            api(libs.koin.compose.viewmodel)

            // Navigation
            api(libs.leon.navigation)
            api(libs.navigation3.runtime)

            // Kotlin
            api(libs.kotlin.coroutines.core)

            implementation(libs.android.annotation)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
        }
    }
}

compose.resources {
    // This module ships no strings of its own: the library names what went wrong, the app words it.
    // So no `Res` class either - `always` generates a public but empty one in this package, sitting
    // next to the real one from :utilities, and an auto-import landing on the wrong of the two is a
    // confusing way for a consumer to find that out. `auto` would generate it just the same, since
    // this module depends on the resources library for `StringResource` itself.
    //
    // The other two stay as the shape this should take again: adding the first string here means
    // flipping one line to `always`, plus its androidMain/res twin, which StringResourceParityTest
    // then demands.
    generateResClass = never
    publicResClass = true
    packageOfResClass = "$group.utilities.presentation.generated.resources"
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
        artifactId = "cmp-utilities-presentation",
        version = version.toString()
    )

    // POM metadata
    pom {
        name.set("Leon Utilities Presentation")
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
    group = libs.versions.library.presentation.artifactID.get()
    description = "Compile and test every target, then publish to Maven Central"

    dependsOn("compileEnvironment", "publishAllPublicationsToMavenCentralRepository")
}

tasks.register("compileEnvironment") {
    group = libs.versions.library.presentation.artifactID.get()
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
    group = libs.versions.library.presentation.artifactID.get()
    description = "Compile and test every target, then publish to maven local"

    // dependsOn rather than finalizedBy: a finalizer is built to run whatever the outcome, which is
    // the opposite of a gate. As a dependency it is the build that stops on a failing test, before
    // the publishing, and the rule above keeps the two in that order when both succeed.
    dependsOn("compileEnvironment", "publishToMavenLocal")
}

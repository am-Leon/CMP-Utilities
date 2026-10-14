import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.touchlab.skie)
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

    val xcf = XCFramework()
    listOf(iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            xcf.add(this)
            baseName = libs.versions.library.artifactID.get()
            // Specify CFBundleIdentifier to uniquely identify the framework
            binaryOption("bundleId", "$group.$baseName")
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.android.material)
        }

        commonMain.dependencies {
            // Compose
            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.components.resources)

            // Kotlin
            implementation(libs.kotlin.coroutines.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
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
        artifactId = "cmp-navigation",
        version = version.toString()
    )

    // POM metadata
    pom {
        name.set("Leon Compose Navigation")
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

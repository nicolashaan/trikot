plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("mirego.kword").version("2.0.1")
}

group = "com.mirego.sample"

configurations {
    create("testApi") {}
    create("testDebugApi") {}
    create("testReleaseApi") {}
}

kword {
    translationFile = file("src/commonMain/resources/translations/translation.en.json")
    enumClassName = "com.mirego.sample.KWordTranslation"
    generatedDir = file("src/commonMain/generated")
}

fun org.jetbrains.kotlin.gradle.plugin.mpp.Framework.configureFramework() {
    baseName = Project.TRIKOT_SAMPLES_FRAMEWORK_NAME
    transitiveExport = true
    export(project(Project.TRIKOT_VIEWMODELS_DECLARATIVE_FLOW))
    export(project(Project.TRIKOT_FOUNDATION))
    export(project(Project.TRIKOT_KWORD))
}

kotlin {
    jvmToolchain(Versions.JVM_TOOLCHAIN)
    androidTarget()

    cocoapods {
        name = Project.TRIKOT_SAMPLES_FRAMEWORK_NAME
        summary = "Trikot-viewmodels-declarative sample"
        homepage = "www.mirego.com"
        license = "BSD-3"
        extraSpecAttributes = mutableMapOf(
            "resources" to "\"src/commonMain/resources/translations/*\"",
            "prepare_command" to """
                <<-CMD
                    ../../../gradlew :trikot-viewmodels-declarative-flow:sample:common:generateDummyFramework
                CMD
            """.trimIndent()
        )

        framework {
            configureFramework()
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(Project.TRIKOT_VIEWMODELS_DECLARATIVE_FLOW))
                api(project(Project.TRIKOT_FOUNDATION))
                api(project(Project.TRIKOT_KWORD))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLINX_COROUTINES}")
            }
            kotlin.srcDir(kword.generatedDir)
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.ANDROIDX_LIFECYCLE}")
            }
        }
    }
}

android {
    namespace = "com.mirego.vmd.flow.sample"
    defaultConfig {
        compileSdk = Versions.Android.COMPILE_SDK
        minSdk = Versions.Android.MIN_SDK
    }
}

project.afterEvaluate {
    tasks
        .filter { task -> task.name.startsWith("compile") && task.name.contains("Kotlin") }
        .forEach { task ->
            task.dependsOn(tasks.withType<com.mirego.kword.KWordEnumGenerate>())
        }
}

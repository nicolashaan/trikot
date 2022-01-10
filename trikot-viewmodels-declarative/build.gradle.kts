buildscript {
    repositories {
        google()
        mavenLocal()
        maven("https://plugins.gradle.org/m2/")
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.androidGradlePlugin}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.0")
    }
}

plugins {
    id("mirego.release").version("2.0")
    id("mirego.publish").version("1.0")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://s3.amazonaws.com/mirego-maven/public")
    }
}

release {
    checkTasks = listOf(
        ":viewmodels-declarative:check",
        ":compose:check"
    )
    buildTasks = listOf(
        ":viewmodels-declarative:publish",
        ":compose:publish"
    )
    updateVersionPart = 2
}

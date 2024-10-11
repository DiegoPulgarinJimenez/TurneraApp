// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath (libs.gradle)
        classpath(libs.kotlin.gradle.plugin.v1910)
    }
}

plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory.get().asFile)
}



// Copyright 2000-2022 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("io.realm.kotlin").version(extra["realm.version"] as String)
        id("com.squareup.sqldelight").version(extra["sqldelight.version"] as String)
        id("com.apollographql.apollo3").version(extra["apollo3.version"] as String)
        id("com.github.gmazzo.buildconfig").version(extra["buildconfig.version"] as String)
        id("com.github.ben-manes.versions").version(extra["ben-manes-versions.version"] as String)
        kotlin("kapt").version(extra["kapt.version"] as String)
    }
}

rootProject.name = "ghd"


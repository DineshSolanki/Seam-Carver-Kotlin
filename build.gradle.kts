import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.dinesh"
version = "1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://jitpack.io") }
    jcenter()
    mavenCentral()
}
dependencies {
    implementation("com.github.kwebio:kweb-core:0.7.32")
    implementation("io.github.microutils:kotlin-logging:1.12.0")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}
plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.quiltmc.loom") version "1.+"
}

group = "dev.isxander"
version = "1.0-SNAPSHOT"

loom {
    runs {
        named("client") {
            vmArg("-Dloader.experimental.allow_loading_plugins=true")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/")
}

val shade: Configuration by configurations.creating

dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings(loom.officialMojangMappings())

    modImplementation("org.quiltmc:quilt-loader:0.19.0-beta.13")
    implementation("org.quiltmc:quilt-json5:1.0.3")

    shade(implementation(kotlin("stdlib-jdk8"))!!)
    shade(implementation(kotlin("reflect"))!!)

    val ktorVersion = "2.3.0"
    shade(implementation("io.ktor:ktor-client-core:$ktorVersion")!!)
    shade(implementation("io.ktor:ktor-client-java:$ktorVersion")!!)
    shade(implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")!!)
    shade(implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")!!)

    shade(implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")!!)
    shade(implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.0-RC")!!)

    shade(implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")!!)
}

kotlin {
    jvmToolchain(17)
}

tasks {
    jar {
        archiveClassifier.set("dev")
    }

    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        relocate("kotlinx", "dev.isxander.bundle.libs.kotlinx")
        relocate("io.ktor", "dev.isxander.bundle.libs.ktor")
        relocate("kotlin", "dev.isxander.bundle.libs.kotlin")
    }
}
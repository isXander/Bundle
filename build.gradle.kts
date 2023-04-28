plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("org.quiltmc.loom") version "1.+"
}

group = "dev.isxander"
version = "0.1.0"

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

    listOf(
        "org.jetbrains.kotlin:kotlin-stdlib:1.8.21",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.21",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.21",
        "org.jetbrains.kotlin:kotlin-reflect:1.8.21",

        "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4",
        "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.4",
        "org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.5.0",
        "org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0",

        "io.ktor:ktor-client-core:2.3.0",
        "io.ktor:ktor-client-core-jvm:2.3.0",
        "io.ktor:ktor-http:2.3.0",
        "io.ktor:ktor-http-jvm:2.3.0",
        "io.ktor:ktor-utils:2.3.0",
        "io.ktor:ktor-utils-jvm:2.3.0",
        "io.ktor:ktor-io:2.3.0",
        "io.ktor:ktor-io-jvm:2.3.0",
        "io.ktor:ktor-events:2.3.0",
        "io.ktor:ktor-events-jvm:2.3.0",
        "io.ktor:ktor-websocket-serialization:2.3.0",
        "io.ktor:ktor-websocket-serialization-jvm:2.3.0",
        "io.ktor:ktor-client-java:2.3.0",
        "io.ktor:ktor-client-java-jvm:2.3.0",
        "io.ktor:ktor-client-content-negotiation:2.3.0",
        "io.ktor:ktor-client-content-negotiation-jvm:2.3.0",
        "io.ktor:ktor-serialization:2.3.0",
        "io.ktor:ktor-serialization-kotlinx-json:2.3.0",
        "io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.0",
        "io.ktor:ktor-serialization-kotlinx:2.3.0",
        "io.ktor:ktor-serialization-kotlinx-jvm:2.3.0",
    ).forEach {
        include(it)
        modApi(it) { isTransitive = false }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    jar {
        archiveClassifier.set("dev")
    }

    shadowJar {
        archiveClassifier.set("dev-fat")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        relocate("kotlinx", "dev.isxander.bundle.libs.kotlinx")
        relocate("io.ktor", "dev.isxander.bundle.libs.ktor")
        relocate("kotlin", "dev.isxander.bundle.libs.kotlin")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)

        archiveClassifier.set(null as String?)
    }
}
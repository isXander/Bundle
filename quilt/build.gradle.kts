plugins {
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

dependencies {
    minecraft("com.mojang:minecraft:1.19.4")
    mappings(loom.officialMojangMappings())

    modImplementation("org.quiltmc:quilt-loader:0.19.0-beta.13")

    implementation(rootProject)
}

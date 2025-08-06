plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
//    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
}

group = "info.infinf"
version = "1.0.0"
//paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.dmulloy2.net/repository/public/" )
}

dependencies {
//    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:16.0.2")
    compileOnly("io.netty:netty-all:4.2.3.Final")
//    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically
        minecraftVersion("1.21.4")
    }
}

val targetJavaVersion = 21
//kotlin {
//    jvmToolchain(targetJavaVersion)
//}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

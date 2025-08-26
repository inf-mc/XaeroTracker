plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "info.infinf"
version = "1.1.2"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.dmulloy2.net/repository/public/" )
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:16.0.2")
    compileOnly("io.netty:netty-all:4.2.3.Final")
    implementation("net.kyori:adventure-api:4.20.0")
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
    }
}

val targetJavaVersion = 21

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

repositories {
  mavenCentral()
  maven("https://jitpack.io")
}

plugins {
  kotlin("jvm") version "1.4.32"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
  implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.2")
  implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
  implementation("io.ktor:ktor-server-netty:1.5.2")
  implementation("io.ktor:ktor-client-cio:1.5.2")
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
  testImplementation("com.natpryce:hamkrest:1.8.0.1")
  testImplementation("io.mockk:mockk:1.10.6")
}

tasks.test {
  useJUnitPlatform()
}

tasks.compileKotlin {
  kotlinOptions.jvmTarget = "15"
}

tasks.jar {
  manifest {
    attributes("Main-Class" to "AppKt")
  }
}
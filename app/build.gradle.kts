plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"

    application
    antlr
}

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.13.0")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.guava:guava:30.1.1-jre")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("sea.AppKt")
}

version = "0.0.0"

tasks {
    generateGrammarSource {
        arguments = arguments + listOf("-visitor")
    }

    compileKotlin {
        dependsOn("generateGrammarSource")
    }

    compileTestKotlin {
        dependsOn("generateTestGrammarSource")
    }

    jar {
        manifest.attributes["Main-Class"] = application.mainClass

        val dependencies = configurations
            .runtimeClasspath
            .get()
            .map { zipTree(it) }

        from(dependencies)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

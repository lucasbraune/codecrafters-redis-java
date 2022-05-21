plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

application {
    // Define the main class for the application.
    mainClass.set("redis.clone.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
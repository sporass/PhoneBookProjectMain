plugins {
    id("java")
    application
}

application {
    mainClass.set("org.example.Main") // upewnij się, że to właściwa klasa
}

tasks {
    // shadowJar automatycznie stworzy fat-jar
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.formdev:flatlaf:3.1")
    implementation("org.apache.derby:derby:10.17.1.0")
}

tasks.test {
    useJUnitPlatform()
}


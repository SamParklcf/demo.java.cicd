import groovy.json.JsonSlurper

plugins {
    `java-library`
    `jvm-test-suite`
}

repositories {
    mavenCentral()
}

group = "demo.java"
version = "0.1.0-SNAPSHOT"

val gitVersionJsonFilePath = "../gitversion.json"

tasks.register<Exec>("gitVersionOutputJSon") {
    commandLine("sh", "-c", "gitversion /output json > $gitVersionJsonFilePath")
    doLast {
        println("---> Project version: ${project.version}")
    }
}

tasks.register("parseGitVersion") {
    dependsOn("gitVersionOutputJSon")
    doLast {
        val jsonSlurper = JsonSlurper()
        val gitVersionFile = file(gitVersionJsonFilePath)
        val gitVersion = jsonSlurper.parse(gitVersionFile) as Map<*, *>
        project.version = gitVersion["SemVer"].toString()
        println("---> Project version set to: ${project.version}")
    }
}

tasks.withType<Jar>().configureEach {
    dependsOn("parseGitVersion")
}

tasks.named<Jar>("jar") {
    from(sourceSets["main"].output)
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.named("assemble") {
    dependsOn("parseGitVersion")
}

tasks.named("build") {
    dependsOn("parseGitVersion")
}

version = project.version

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

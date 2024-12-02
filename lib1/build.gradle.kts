import groovy.json.JsonSlurper

plugins {
    id("java-library")
    id("maven-publish")
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
        project.version = gitVersion["SemVer"] as String
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.example"
            artifactId = "demo"
            version = project.version as String
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            //url = uri("https://maven.pkg.github.com/octocat/hello-world")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

plugins {
    java
    `maven-publish`
    signing
}

group = "com.factset.sdk.eventdriven"
version = "2.0.0-SNAPSHOT"

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.factset.sdk:utils:1.+")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("org.slf4j:slf4j-api:2.0.9")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.9")

    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJava {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

// https://docs.gradle.org/current/userguide/publishing_maven.html
publishing {
    publications {
        create<MavenPublication>("mavenJava") {

            // dependencies
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("FactSet Trading event-driven client library for Java")
                description.set("Event-driven api client for the FactSet Trading API")
                url.set("https://github.com/factset/enterprise-sdk-eventdriven-factsettrading-java")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0'")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("enterprisesdk")
                        organization.set("FactSet")
                        organizationUrl.set("https://developer.factset.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/factset/enterprise-sdk-eventdriven-factsettrading-java.git")
                    developerConnection.set("scm:git:ssh://factset/enterprise-sdk-eventdriven-factsettrading-java.git")
                    url.set("https://github.com/factset/enterprise-sdk-eventdriven-factsettrading-java/")
                }
            }
        }
    }

    repositories {
        maven {
            var releasesRepoUrlEnv = System.getenv("MAVEN_RELEASES_URL")
            var snapshotsRepoUrlEnv = System.getenv("MAVEN_SNAPSHOTS_URL")
            var usernameEnv = System.getenv("MAVEN_USERNAME")
            var passwordEnv = System.getenv("MAVEN_PASSWORD")

            if (releasesRepoUrlEnv == null) {
                releasesRepoUrlEnv = ""
                project.logger.error("MAVEN_RELEASES_URL not set")
            }

            if (snapshotsRepoUrlEnv == null) {
                snapshotsRepoUrlEnv = ""
                project.logger.error("MAVEN_SNAPSHOTS_URL not set")
            }

            if (usernameEnv == null) {
                usernameEnv = ""
                project.logger.error("MAVEN_USERNAME not set")
            }

            if (passwordEnv == null) {
                passwordEnv = ""
                project.logger.error("MAVEN_PASSWORD not set")
            }

            val releasesRepoUrl = uri(releasesRepoUrlEnv)
            val snapshotsRepoUrl = uri(snapshotsRepoUrlEnv)
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = usernameEnv
                password = passwordEnv
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    signing {
        sign(publications["mavenJava"])
    }
}

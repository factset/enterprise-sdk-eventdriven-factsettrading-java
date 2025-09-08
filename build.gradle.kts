import java.time.LocalDate

plugins {
    java
    `maven-publish`
    signing
    id("org.jreleaser") version "1.17.0"
}

group = "com.factset.sdk.eventdriven"
version = "2.0.1"

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
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("org.slf4j:slf4j-api:2.0.9")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.9")

    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")
    testCompileOnly("org.projectlombok:lombok:1.18.40")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.40")
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

val releasesRepoUrl = layout.buildDirectory.dir("repos/releases")
val snapshotsRepoUrl = layout.buildDirectory.dir("repos/snapshots")

// Configure publishing
configure<PublishingExtension> {
    publications {
        register<MavenPublication>("maven") {
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
                url.set("https://github.com/factset/enterprise-sdk-eventdriven-factsettrading-java/")
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
            // change URLs to point to your repos, e.g. http://my.org/repo
            url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)
        }
    }
}

configure<org.jreleaser.gradle.plugin.JReleaserExtension> {
    gitRootSearch.set(true)
    project {
        description = "Event-driven api client for the FactSet Trading API"
        authors = listOf("FactSet")
        license = "APACHE-2.0"
        inceptionYear = "2023"
        vendor = "FactSet"
        copyright = "Copyright (c) ${LocalDate.now().year} FactSet"
    }

    signing {
        active.set(org.jreleaser.model.Active.ALWAYS)
        armored = true
    }

    release {
        github { 
            skipTag = true
            skipRelease = true
        }
    }

    deploy {
        maven {
            mavenCentral {
                register("release-deploy") {
                    active.set(org.jreleaser.model.Active.RELEASE)
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(releasesRepoUrl.get().asFile.path)
                }
            }
            nexus2 {
                register("snapshot-deploy") {
                    active.set(org.jreleaser.model.Active.SNAPSHOT)
                    url = "https://central.sonatype.com/repository/maven-snapshots/"
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    stagingRepository(snapshotsRepoUrl.get().asFile.path)
                }
            }
        }
    }
}
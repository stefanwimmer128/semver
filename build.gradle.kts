plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
    id("com.jfrog.bintray") version "1.8.4"
    id("maven-publish")
}

group = "eu.stefanwimmer128.semver"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.3.10")
    
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:1.3.10")
}

tasks {
    create<Jar>("sourceJar") {
        from(sourceSets["main"].allSource)
        
        classifier = "sources"
    }
}

artifacts {
    add("archives", tasks.getByName("jar"))
    add("archives", tasks.getByName("sourceJar"))
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    pkg.apply {
        repo = "maven"
        name = "semver"
        
        version.apply {
            name = project.version as String
            vcsTag = "v${project.version}"
        }
    }
    setPublications("maven")
    override = true
    publish = true
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            artifacts.artifact(tasks.getByName("sourceJar"))
            
            pom {
                name.set("SemVer")
                url.set("https://github.com/stefanwimmer128/semver#readme")
                licenses {
                    license {
                        name.set("ISC")
                        url.set("https://raw.githubusercontent.com/stefanwimmer128/semver/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("stefanwimmer128")
                        name.set("Stefan Wimmer")
                        email.set("info@stefanwimmer128.eu")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/stefanwimmer128/semver.git")
                    developerConnection.set("scm:git:git@github.com:stefanwimmer128/semver.git")
                    url.set("https://github.com/stefanwimmer128/semver")
                }
            }
        }
    }
}

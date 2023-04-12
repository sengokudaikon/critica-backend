/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build and test") {
    container("amazoncorretto:17-alpine") {
        shellScript {
            content = "./gradlew build"
        }
    }
}

job("Flyway migration") {
    container("amazoncorretto:17-alpine") {
        shellScript {
            content = "./gradlew flywayMigrate"
        }
    }
}

job("Detekt static code analysis") {
    container("amazoncorretto:17-alpine") {
        shellScript {
            content = "./gradlew detekt"
        }
    }
}

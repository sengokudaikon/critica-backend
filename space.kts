import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

version = "2023.1"

project {
    buildType {
        id("Build")
        name = "Build"

        vcs {
            root(DslContext.settingsRoot)
        }

        steps {
            gradle {
                tasks = "build"
                buildFile = "build.gradle.kts"
            }
        }

        triggers {
            vcs {
            }
        }

        features {
            commitStatusPublisher {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
            }
        }
    }

    buildType {
        id("StaticCodeAnalysis")
        name = "Static Code Analysis"

        vcs {
            root(DslContext.settingsRoot)
        }

        steps {
            gradle {
                tasks = "detekt"
                buildFile = "build.gradle.kts"
            }
        }

        triggers {
            vcs {
            }
        }

        features {
            commitStatusPublisher {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
            }
        }
    }

    buildType {
        id("Migrations")
        name = "Migrations"

        vcs {
            root(DslContext.settingsRoot)
        }

        steps {
            gradle {
                tasks = "flywayMigrate"
                buildFile = "build.gradle.kts"
            }
        }

        triggers {
            vcs {
                branchFilter = "+:merge"
            }
        }

        features {
            commitStatusPublisher {
                vcsRootExtId = "${DslContext.settingsRoot.id}"
            }
        }
    }
}

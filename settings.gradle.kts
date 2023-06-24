pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
//        maven("https://maven.aliyun.com/nexus/content/groups/public/")
//        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
//        maven("https://maven.aliyun.com/nexus/content/repositories/google")
    }
}

rootProject.name="UestcBBS-MVP"
include(":app", ":util", ":widget")


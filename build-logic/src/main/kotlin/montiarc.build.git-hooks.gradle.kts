/* (c) https://github.com/MontiCore/monticore */
import com.diffplug.spotless.GitPrePushHookInstaller.GitPreHookLogger
import com.diffplug.spotless.GitPrePushHookInstallerGradle

plugins {
  id("com.diffplug.spotless")
}

// Spotless only installs its git pre-push hook when its `spotlessInstallGitPrePushHook` task is
// explicitly run. To make the hook show up for every developer without a manual setup step, we
// invoke the same installer Spotless uses for that task directly during project configuration, so
// it runs on every `gradle` invocation from the repo root. The installer is idempotent (it detects
// and updates its own marked block) and preserves any other hook content already in the file (e.g.
// the git-lfs pre-push shim), so this is safe to call on every build.
if (System.getenv("CI").isNullOrEmpty()) {
  try {
    val hookLogger = object : GitPreHookLogger {
      override fun info(format: String, vararg arguments: Any) = logger.info(String.format(format, *arguments))
      override fun warn(format: String, vararg arguments: Any) = logger.warn(String.format(format, *arguments))
      override fun error(format: String, vararg arguments: Any) = logger.error(String.format(format, *arguments))
    }
    GitPrePushHookInstallerGradle(hookLogger, rootDir).install()
  } catch (e: Exception) {
    logger.warn("Could not install Spotless git pre-push hook: ${e.message}")
  }
}

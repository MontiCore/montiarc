/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MA2JSimToolCLITest {

  Path input = Paths.get("test/resources");
  Path output = Paths.get("build/generated/montiarc/test/java");

  /**
   * Helper method to check if the system's javac command is version 21 or newer.
   */
  private boolean isJavac21OrGreater() {
    try {
      Process process = new ProcessBuilder("javac", "--version")
        .redirectErrorStream(true)
        .start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line = reader.readLine();
      process.waitFor();

      if (line != null && line.startsWith("javac ")) {
        String versionStr = line.split(" ")[1];
        String[] parts = versionStr.split("\\.");
        int majorVersion = Integer.parseInt(parts[0]);

        if (majorVersion == 1 && parts.length > 1) {
          majorVersion = Integer.parseInt(parts[1]);
        }

        return majorVersion >= 21;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  @Test
  public void testBuildAndRun() {
    Assumptions.assumeTrue(isJavac21OrGreater(),
      "Test skipped: Locally installed javac command is missing or older than Java 21.");

    // Given
    String[] argsBuild = new String[]{
      "-i", input.toAbsolutePath().toString(),
      "-o", output.toAbsolutePath().toString(),
      "-c2mc"
    };
    String[] argsRun = new String[]{
      "run",
      output.toAbsolutePath() + "/HelloWorld.arc"
    };

    // When
    MA2JSimToolCLI.main(argsBuild);
    MA2JSimToolCLI.main(argsRun);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MA2JSimToolCLITest {

  Path input = Paths.get("test/resources");
  Path output = Paths.get("build/generated/montiarc/test/java");

  @Test
  public void testBuildAndRun() {
    // Given
    String[] argsBuild = new String[] {
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

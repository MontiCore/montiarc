/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MA2JSimTest extends MA2JSimTestBase {

  /**
   * We override the type-check and basis symbols initialization as these
   * should be done by the tool call.
   */
  @Override
  protected void init() { }

  Path input = Paths.get("test/resources");
  Path hwcPath = Paths.get("test/resources/java");
  Path output = Paths.get("build/generated/montiarc/test/java");

  @Test
  public void testBuild() {
    // Given
    String[] args = new String[] {
      "-i", input.toAbsolutePath().toString(),
      "-o", output.toAbsolutePath().toString(),
      "-hwc", hwcPath.toAbsolutePath().toString(),
      "-c2mc"
    };

    // When
    MA2JSimTool.main(args);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());
  }

  @Test
  public void testRun() {
    // Given
    String[] args = new String[] {
      "run", "test/resources/MA2JSimToolTestRunFile.java",
      "-t", "test",
    };

    // When
    MA2JSimTool.main(args);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());
  }
}
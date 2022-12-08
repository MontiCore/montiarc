/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class MontiArcToolTest {

  @TempDir
  Path tempDir;

  private final static Path CLI_RESOURCES_DIR = Path.of("test", "resources", "clitool");

  @Test
  public void shouldIgnoreLibModelInGeneration() {
    // Given
    Path modelPath = CLI_RESOURCES_DIR.resolve( Path.of("libModelsAreExcluded", "consumer"));
    Path libraryPath = CLI_RESOURCES_DIR.resolve( Path.of("libModelsAreExcluded", "library"));

    Path genDir = tempDir;
    Path genFile = tempDir.resolve("consumerpack/ReferencingComponent.java");
    Path illegalGenFile = tempDir.resolve("libpack/ReferencedComponent.java");

    String[] args = new String[] {
      "--modelpath", modelPath.toString(),
      "-lib", libraryPath.toString(),
      "-output", genDir.toString()
    };

    // When
    MontiArcTool.main(args);

    // Then
    // Tests may also fail due to the SE Logger exiting early (failQuick), when it encounters errors. These errors may
    // be errors from MontiArc models  In this case it is possible, that the library models were not parsed and
    // therefore are missing, leading to the errors.
    Assertions.assertTrue(genFile.toFile().isFile());
    Assertions.assertFalse(illegalGenFile.toFile().exists());
  }
}

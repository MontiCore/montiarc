/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static montiarc.util.ArcError.RESTRICTED_IDENTIFIER;
import static montiarc.util.ArcError.TYPE_PARAMETER_UPPER_CASE;
import static montiarc.util.ArcError.VARIABILITY_NOT_ANALYZED;
import static org.assertj.core.api.Assertions.assertThat;

public class MA2JSimToolTest extends MA2JSimTestBase {

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
  public void testBuildAcceptsJavaKeywordsAsPrefixedNames(@TempDir Path models,
                                                          @TempDir Path target) throws IOException {
    // Given
    Files.writeString(models.resolve("Comp.arc"), "component Comp { port in int assert; }");
    String[] args = new String[] {
      "-i", models.toAbsolutePath().toString(),
      "-o", target.toAbsolutePath().toString()
    };

    // When
    MA2JSimTool.main(args);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
  }

  @Test
  public void testBuildRejectsJavaKeywordsAsTypeParameters(@TempDir Path models,
                                                           @TempDir Path target) throws IOException {
    // Given
    Files.writeString(models.resolve("Comp.arc"), "component Comp<assert> { port in assert i; }");
    String[] args = new String[] {
      "-i", models.toAbsolutePath().toString(),
      "-o", target.toAbsolutePath().toString()
    };

    // When
    MA2JSimTool.main(args);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(RESTRICTED_IDENTIFIER, TYPE_PARAMETER_UPPER_CASE));
  }

  @Test
  public void testBuildRejectsVariability(@TempDir Path models, @TempDir Path target) throws IOException {
    // Given
    Files.writeString(models.resolve("Comp.arc"), "component Comp { feature f; varif (f) { port in int i; } }");
    String[] args = new String[] {
      "-i", models.toAbsolutePath().toString(),
      "-o", target.toAbsolutePath().toString()
    };

    // When
    MA2JSimTool.main(args);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(VARIABILITY_NOT_ANALYZED, VARIABILITY_NOT_ANALYZED));
  }

  @Test
  public void testRun() throws IOException {
    // Given
    String[] args = new String[] {
      "run", "test/resources/MA2JSimToolTestRunFile",
      "-t", "test",
    };

    // When
    MA2JSimTool.main(args);

    // Then
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());

    // Cleanup
    FileUtils.deleteDirectory(new File("test/resources/build"));
  }
}

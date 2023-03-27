/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import arcbasis.ArcBasisAbstractTest;
import com.google.common.base.Preconditions;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.file.PathUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FDGeneratorTest extends ArcBasisAbstractTest {

  protected static final String FD_FILE_EXTENSION = ".fd";
  protected static final String TEST_RESOURCE_PATH = "test/resources/";
  protected static final String TEST_TARGET_PATH = Path.of(System.getProperty("buildDir"), "generated", "fd-gen").toString();

  @AfterAll
  static void cleanup() throws IOException {
    PathUtils.deleteDirectory(Path.of(TEST_TARGET_PATH));
  }

  @Test
  public void testGeneratorWithValidModels() {
    // Given
    FeatureDiagramTool tool = new FeatureDiagramTool();
    final String i = Path.of(TEST_RESOURCE_PATH, "valid").toString();
    final String o = Path.of(TEST_TARGET_PATH, "valid").toString();

    // When
    FDGenerator.main(new String[]{i, o});

    for (Path fd: getAllFeatureDiagrams(Path.of(o))) {
      tool.createSymbolTable(tool.parse(fd.toString()));
    }

    // Then
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratorWithWarningModels() {
    // Given
    FeatureDiagramTool tool = new FeatureDiagramTool();
    final String i = Path.of(TEST_RESOURCE_PATH, "warning").toString();
    final String o = Path.of(TEST_TARGET_PATH, "warning").toString();

    // When
    FDGenerator.main(new String[]{i, o});

    for (Path fd: getAllFeatureDiagrams(Path.of(o))) {
      tool.createSymbolTable(tool.parse(fd.toString()));
    }

    // Then
    Assertions.assertEquals(0, Log.getErrorCount());
  }

  @Test
  public void testGeneratorWithInvalidModels() {
    // Given
    FeatureDiagramTool tool = new FeatureDiagramTool();
    final String i = Path.of(TEST_RESOURCE_PATH, "invalid").toString();
    final String o = Path.of(TEST_TARGET_PATH, "invalid").toString();

    // When
    FDGenerator.main(new String[]{i, o});

    for (Path fd: getAllFeatureDiagrams(Path.of(o))) {
      tool.createSymbolTable(tool.parse(fd.toString()));
    }

    // Then
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  /**
   * Helper to get a collection of paths to all feature diagrams in the given
   * directory with the given file extension
   *
   * @param directory Directory which should be searched for all files with
   *                  the given file extension
   * @return Collection of Paths of all files which match the directory and
   * file extension
   */
  private Collection<Path> getAllFeatureDiagrams(@NotNull Path directory) {
    Preconditions.checkNotNull(directory);
    Preconditions.checkArgument(directory.toFile().exists());
    Preconditions.checkArgument(directory.toFile().isDirectory());

    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
        .filter(file -> file.getFileName().toString().endsWith(FDGeneratorTest.FD_FILE_EXTENSION))
        .collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format("No Feature Diagram found in directory '%s'", directory));
    }
    return Collections.emptySet();
  }
}

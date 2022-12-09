/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import arcbasis.AbstractTest;
import com.google.common.base.Preconditions;
import de.monticore.featurediagram.FeatureDiagramTool;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.monticore.featurediagram._cocos.FeatureDiagramCoCos;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.file.PathUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class FDGeneratorTest extends AbstractTest {

  protected static final String FD_FILE_EXTENSION = ".fd";
  protected static final String ARC_FILE_EXTENSION = ".arc";
  protected static final String TEST_RESOURCE_PATH = "test/resources/";
  protected static final String VALID_MODEL_FOLDER = TEST_RESOURCE_PATH + "valid";
  protected static final String INVALID_MODEL_FOLDER = TEST_RESOURCE_PATH + "invalid";
  protected static final String WARNING_MODEL_FOLDER = TEST_RESOURCE_PATH + "warning";

  @BeforeAll
  static void setup() {
    Log.clearFindings();
    Log.enableFailQuick(false);
  }

  // Delete the "Converted-Formula-Folder" after each test
  @AfterAll
  static void cleanup() throws IOException {
    PathUtils.deleteDirectory(FDGenerator.getOutputDir(Path.of(VALID_MODEL_FOLDER)));
    PathUtils.deleteDirectory(FDGenerator.getOutputDir(Path.of(INVALID_MODEL_FOLDER)));
    PathUtils.deleteDirectory(FDGenerator.getOutputDir(Path.of(WARNING_MODEL_FOLDER)));
    Log.enableFailQuick(true);
  }

  /**
   * Method under test {@link FDGenerator#main(String[])} (with valid models
   * only)
   */
  @ParameterizedTest
  @ValueSource(strings = {VALID_MODEL_FOLDER})
  void testGeneratorWithValidModels(@NotNull String modelFolder) {
    Assertions.assertNotNull(modelFolder);

    // Process all Files
    Log.println("[Test Arc2FD]: Testing Valid Models");
    FDGenerator.main(new String[]{modelFolder, modelFolder});

    // In the second step, analyze and check that all files are valid...
    Path output = FDGenerator.getOutputDir(Path.of(modelFolder));
    Collection<Path> validFDs = getAllFeatureDiagrams(FD_FILE_EXTENSION,
        output);
    parseAndTestFDs(validFDs, MODE.VALID);
  }

  /**
   * Method under test {@link FDGenerator#main(String[])} (with valid models
   * only)
   */
  @ParameterizedTest
  @ValueSource(strings = {WARNING_MODEL_FOLDER})
  void testGeneratorWithWarningModels(@NotNull String modelFolder) {
    Assertions.assertNotNull(modelFolder);

    // Process all Files
    Log.println("[Test Arc2FD]: Testing Models with Warnings");
    Path path = Path.of(modelFolder);
    Collection<Path> files = getAllFeatureDiagrams(ARC_FILE_EXTENSION, path);
    files.forEach(f -> {
      Log.clearFindings();
      FDGenerator.main(new String[]{f.toString(), f.toString()});
      Assertions.assertNotEquals(0,
          Log.getFindings().stream().filter(Finding::isWarning).count());
    });

    // In the second step, analyze and check that all files are valid...
    Path output = FDGenerator.getOutputDir(path);
    Collection<Path> validFDs = getAllFeatureDiagrams(FD_FILE_EXTENSION,
        output);
    parseAndTestFDs(validFDs, MODE.VALID);
  }

  /**
   * Method under test {@link FDGenerator#main(String[])} (with invalid
   * models only)
   */
  @ParameterizedTest
  @ValueSource(strings = {INVALID_MODEL_FOLDER})
  void testGeneratorWithInvalidModels(@NotNull String modelFolder) {
    Assertions.assertNotNull(modelFolder);

    // Process all Files
    Log.println("[Test Arc2FD]: Testing Invalid Models");
    FDGenerator.main(new String[]{modelFolder, modelFolder});

    // In the second step, analyze and check that all files are valid...
    Path output = FDGenerator.getOutputDir(Path.of(modelFolder));
    Collection<Path> invalidFDs = getAllFeatureDiagrams(FD_FILE_EXTENSION,
        output);
    parseAndTestFDs(invalidFDs, MODE.INVALID);
  }

  /**
   * Parse a Collection of valid Feature Diagrams and asserts that each one
   * is valid & present
   *
   * @param fds  Collection of Paths to feature diagrams
   * @param mode Mode of the parsing (whether the Feature-Diagrams should be
   *             valid, invalid or give warnings)
   */
  private void parseAndTestFDs(@NotNull Collection<Path> fds,
                               @NotNull MODE mode) {
    Assertions.assertNotNull(fds);
    Assertions.assertNotNull(mode);

    FeatureDiagramTool tool = new FeatureDiagramTool();
    fds.forEach(fd -> {
      Log.clearFindings();
      ASTFDCompilationUnit ast = tool.parse(fd.toString());
      tool.createSymbolTable(ast);

      // TODO: Delete Later, but at the moment the checkCocos-Function
      //  directly exits the program so we cannot
      //       introduce a really useful test here...
      if (mode != MODE.INVALID)
        FeatureDiagramCoCos.checkAll(ast);

      switch (mode) {
        case VALID:
          // We should have no errors if we're in Valid-Mode
          Assertions.assertEquals(0,
              Log.getFindings().stream().filter(Finding::isError).count());
          break;
        case INVALID:
          // We should have at least one error if we're in Invalid-Mode
//                    Assertions.assertNotEquals(0, Log.getFindings().stream
//                    ().filter(Finding::isError).count());
          break;
      }
    });
  }

  /**
   * Helper to get a collection of paths to all feature diagrams in the given
   * directory with the given file extension
   *
   * @param fileExt   File Extension of Feature Diagrams
   * @param directory Directory which should be searched for all files with
   *                  the given file extension
   * @return Collection of Paths of all files which match the directory and
   * file extension
   */
  private Collection<Path> getAllFeatureDiagrams(@NotNull String fileExt,
                                                 @NotNull Path directory) {
    Preconditions.checkNotNull(fileExt);
    Preconditions.checkNotNull(directory);
    Preconditions.checkArgument(!fileExt.isEmpty());
    Preconditions.checkArgument(directory.toFile().exists(), "Directory does " +
        "not exist: " + directory);
    Preconditions.checkArgument(directory.toFile().isDirectory(), "Directory " +
        "is file: " + directory);

    try (Stream<Path> paths = Files.walk(directory)) {
      return paths.filter(Files::isRegularFile)
          .filter(file -> file.getFileName().toString().endsWith(fileExt)).collect(Collectors.toSet());
    } catch (IOException e) {
      Log.error(String.format("No Feature Diagram found in directory '%s'",
          directory));
    }
    return Collections.emptySet();
  }


  /**
   * Mode how we want to test the feature diagram parsing (so whether they
   * should give errors or
   * parse without problems)
   */
  enum MODE {
    VALID,
    INVALID,
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.monticore.featurediagram.FeatureDiagramMill;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.MontiArcError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Arc2FDToolTest extends Arc2FDToolTestBase {

  /**
   * Where the actual feature diagrams of {@link #testExpectedFeatureDiagram} are written, for comparison
   * with (or updating) the expected ones.
   */
  protected static final Path ACTUAL_DIR = Path.of("build", "test-actual");

  /* ---------------------------------------------------------------------- */
  /* CLI                                                                    */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest
  @MethodSource("provideValidTestCases")
  void testArc2FDMain(String fileName, @TempDir Path outputDirPath) {
    Path modelPath = getModelPath("valid/" + fileName);

    Arc2FDTool.main(new String[]{
      "--input",
      modelPath.toString(),
      "--output",
      outputDirPath.toString(),
      "--include-subcomponents"
    });

    assertEquals(0, Log.getErrorCount());
  }

  @ParameterizedTest
  @MethodSource("provideValidTestCases")
  void testArc2FDMainWithoutSubcomponents(String fileName, @TempDir Path outputDirPath) {
    Path modelPath = getModelPath("valid/" + fileName);

    Arc2FDTool.main(new String[]{
      "--input",
      modelPath.toString(),
      "--output",
      outputDirPath.toString()
    });

    assertEquals(0, Log.getErrorCount());
  }

  @Test
  void testMainMultipleInputs(@TempDir Path tempDir, @TempDir Path outputDirPath) throws IOException {
    Path dirA = writeModel(tempDir.resolve("a"), "A");
    Path dirB = writeModel(tempDir.resolve("b"), "B");

    Arc2FDTool.main(new String[]{
      "-i", dirA.toString(), dirB.toString(),
      "-o", outputDirPath.toString()
    });

    assertEquals(0, Log.getErrorCount());
    assertTrue(Files.isRegularFile(outputDirPath.resolve("A.fd")));
    assertTrue(Files.isRegularFile(outputDirPath.resolve("B.fd")));
  }

  @Test
  void testMainMultipleInputsAsPath(@TempDir Path tempDir, @TempDir Path outputDirPath) throws IOException {
    Path dirA = writeModel(tempDir.resolve("a"), "A");
    Path dirB = writeModel(tempDir.resolve("b"), "B");

    // the gradle plugin passes multiple source directories as a single path
    Arc2FDTool.main(new String[]{
      "-i", dirA + File.pathSeparator + dirB,
      "-o", outputDirPath.toString()
    });

    assertEquals(0, Log.getErrorCount());
    assertTrue(Files.isRegularFile(outputDirPath.resolve("A.fd")));
    assertTrue(Files.isRegularFile(outputDirPath.resolve("B.fd")));
  }

  @Test
  void testMainMissingOutput() {
    Arc2FDTool.main(new String[]{"-i", getModelPath("valid/SimpleAnd.arc").toString()});

    assertLoggedError(MontiArcError.CLI_OPTION_MISSING.getErrorCode());
  }

  @Test
  void testMainHelp() {
    Arc2FDTool.main(new String[]{"-h"});

    assertEquals(0, Log.getErrorCount());
  }

  /* ---------------------------------------------------------------------- */
  /* Feature diagrams                                                       */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest
  @MethodSource("provideExpectedFeatureDiagrams")
  void testExpectedFeatureDiagram(String model, String component, boolean recursive, String expected)
    throws IOException {
    Set<ASTFDCompilationUnit> fds = tool.arc2fd(getModelPath(model), recursive);
    Optional<ASTFDCompilationUnit> fd = fds.stream()
      .filter(f -> f.getFeatureDiagram().getName().equals(component))
      .findFirst();
    assertTrue(fd.isPresent(), "Missing feature diagram of " + component);

    String actual = FeatureDiagramMill.prettyPrint(fd.get(), false);
    Files.createDirectories(ACTUAL_DIR);
    Files.writeString(ACTUAL_DIR.resolve(expected), actual);

    assertEquals(normalize(Files.readString(getModelPath("expected/" + expected))), normalize(actual),
      "See " + ACTUAL_DIR.resolve(expected).toAbsolutePath() + " for the actual feature diagram");

    // the output must be a valid feature diagram
    assertTrue(FeatureDiagramMill.parser().parse_String(actual).isPresent());
    assertEquals(0, Log.getErrorCount());
  }

  @Test
  void testEmptyComponentHasNoFeatureDiagram() {
    Set<ASTFDCompilationUnit> fds = tool.arc2fd(getModelPath("valid/EmptyComponent.arc"));
    assertTrue(fds.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("provideIncorrectAndErrorCodes")
  void testInvalidTestCases(String fileName, String[] errorCodes) {
    Path modelPath = getModelPath("invalid/" + fileName);

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath, true);
    assertTrue(fds.isEmpty());

    Arrays.asList(errorCodes).forEach(Arc2FDToolTest::assertLoggedError);
  }

  @Test
  void testSeparatorInNameWithoutSubcomponents() {
    // without flattening, the separator cannot cause name collisions
    Set<ASTFDCompilationUnit> fds = tool.arc2fd(getModelPath("invalid/SeparatorInName.arc"), false);

    assertEquals(1, fds.size());
    assertEquals(0, Log.getErrorCount());
  }

  @ParameterizedTest
  @MethodSource("provideWarningAndErrorCodes")
  void testWarningTestCases(String fileName, String[] errorCodes) {
    Path modelPath = getModelPath("warning/" + fileName);

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath, true);
    assertEquals(1, fds.size());

    Arrays.asList(errorCodes).forEach(Arc2FDToolTest::assertLoggedError);
  }

  /* ---------------------------------------------------------------------- */
  /* Pipeline stages                                                        */
  /* ---------------------------------------------------------------------- */

  @ParameterizedTest
  @MethodSource("provideValidTestCases")
  void testCollectedConstraintsAreSatisfiable(String fileName) {
    IVariableArcComponentTypeSymbol component = compile("valid/" + fileName);

    try (Context context = new Context()) {
      Optional<List<BoolExpr>> constraints = new FeatureCollector(true).collectConstraints(component, context);
      assertTrue(constraints.isPresent());

      Solver solver = context.mkSolver();
      solver.add(constraints.get().toArray(BoolExpr[]::new));
      assertEquals(Status.SATISFIABLE, solver.check());
    }
  }

  @Test
  void testFlattenedFeatureNames() {
    IVariableArcComponentTypeSymbol component = compile("valid/UnderscoreNames.arc");

    Optional<Map<String, String>> features = new FeatureCollector(true).collectFeatures(component);

    assertTrue(features.isPresent());
    assertEquals(Map.of("a_b$c", "a_b.c", "a$b_c", "a.b_c"), features.get());
  }

  /* ---------------------------------------------------------------------- */
  /* Output                                                                 */
  /* ---------------------------------------------------------------------- */

  @Test
  void testOutputDirectoryExistsAsFile(@TempDir Path tempDir) throws IOException {
    Path outputDirPath = tempDir.resolve("fd_generated");
    Path modelPath = getModelPath("valid/CarExample.arc");

    Files.createFile(outputDirPath);
    assertTrue(Files.exists(outputDirPath));

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath);
    assertFalse(fds.isEmpty());

    tool.writeFeatureDiagrams(outputDirPath, fds);

    assertEquals(1, Log.getErrorCount());
    assertLoggedError(Arc2FDError.OUTPUT_DIRECTORY.getErrorCode());
  }

  @Test
  void testCreatesOutputDirectoryIfMissing(@TempDir Path tempDir) {
    Path outputDirPath = tempDir.resolve("fd_generated");
    Path modelPath = getModelPath("valid/CarExample.arc");

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath);
    assertFalse(fds.isEmpty());

    tool.writeFeatureDiagrams(outputDirPath, fds);

    assertTrue(Files.isDirectory(outputDirPath));
  }

  @Test
  void testCreatesOutputFile(@TempDir Path outputDirPath) {
    Path outputFile = outputDirPath.resolve("CarExample.fd");
    Path modelPath = getModelPath("valid/CarExample.arc");

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath);
    assertFalse(fds.isEmpty());

    tool.writeFeatureDiagrams(outputDirPath, fds);

    assertTrue(Files.isRegularFile(outputFile));
  }

  @Test
  void testCreatesOutputFileWithPackageStructure(@TempDir Path tempDir, @TempDir Path outputDirPath) throws IOException {
    Path modelDir = tempDir.resolve("foo").resolve("bar");
    Files.createDirectories(modelDir);
    Path modelPath = modelDir.resolve("PackedComp.arc");
    Files.writeString(modelPath, "package foo.bar;\ncomponent PackedComp {\n  feature f;\n  constraint (f);\n}");

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(tempDir);
    assertFalse(fds.isEmpty());

    tool.writeFeatureDiagrams(outputDirPath, fds);

    Path expectedFile = outputDirPath.resolve("foo").resolve("bar").resolve("PackedComp.fd");
    assertTrue(Files.isRegularFile(expectedFile));
    assertFalse(Files.exists(outputDirPath.resolve("prettyprint")));
  }

  @Test
  void testOverwritesExistingOutputFile(@TempDir Path outputDirPath) throws IOException {
    Path outputFile = outputDirPath.resolve("CarExample.fd");
    Path modelPath = getModelPath("valid/CarExample.arc");

    String originalContent = "Original File Content";

    Files.writeString(outputFile, originalContent);

    Set<ASTFDCompilationUnit> fds = tool.arc2fd(modelPath);
    assertFalse(fds.isEmpty());

    tool.writeFeatureDiagrams(outputDirPath, fds);

    assertTrue(Files.exists(outputFile));
    assertNotEquals(originalContent, Files.readString(outputFile));
  }

  /* ---------------------------------------------------------------------- */
  /* Helpers                                                                */
  /* ---------------------------------------------------------------------- */

  /**
   * Compiles a single model and returns its component.
   */
  protected IVariableArcComponentTypeSymbol compile(String model) {
    tool.prepareGlobalScope(new String[0], false);
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{getModelPath(model).toString()},
      null, null, null, false, false);
    return (IVariableArcComponentTypeSymbol) asts.iterator().next().getArcComponentType().getSymbol();
  }

  /**
   * Writes a component with a single mandatory feature into a new directory.
   *
   * @return The directory
   */
  protected static Path writeModel(Path dir, String name) throws IOException {
    Files.createDirectories(dir);
    Files.writeString(dir.resolve(name + ".arc"),
      "component " + name + " {\n  feature f;\n  constraint (f);\n}");
    return dir;
  }

  /**
   * Normalizes line endings and trailing whitespace.
   */
  protected static String normalize(String fd) {
    return fd.lines().map(String::stripTrailing).collect(Collectors.joining("\n")).strip();
  }
}

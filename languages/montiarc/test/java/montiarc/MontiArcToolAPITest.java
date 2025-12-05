/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Contains unit tests for the application programming interface (API)
 * of {@link MontiArcTool}.
 *
 * @see MontiArcToolTest For tests targeting the command-line interface (CLI).
 */
class MontiArcToolAPITest extends MontiArcTestBase {

  /**
   * Verifies that the number of options matches the expected number of options.
   * This test catches changes upstream so new options can be properly
   * integrated into the MontiArc tools and plugins.
   * <p>
   * If the expected number of options changes, please:
   * (1) Implement new options in the MontiArc Gradle tools.
   * (2) Add unit tests for new options in this test class.
   * (3) Add CLI tests for new options in {@link MontiArcToolTest}.
   */
  @Test
  void initOptionsShouldInitializeExpectedNumberOfOptions() {
    // When
    Options options = new MontiArcTool().initOptions();

    // Then
    assertThat(options.getOptions().size())
        .as(() -> options.getOptions().toString())
        .isEqualTo(11);
  }

  @Test
  void initOptionsShouldCreateHelpOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("h");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("help");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateVersionOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("v");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("version");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateInputOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("i");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("input");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isTrue();
    assertThat(option.getArgs()).isEqualTo(Option.UNLIMITED_VALUES);
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("paths");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreatePathOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("path");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("path");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isTrue();
    assertThat(option.getArgs()).isEqualTo(Option.UNLIMITED_VALUES);
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("dir");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateSymboltableOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("s");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("symboltable");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("dir");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreatePrettyPrintOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("pp");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("prettyprint");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isTrue();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("dir");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateReportOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("r");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("report");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("dir");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateC2MCOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("c2mc");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("class2mc");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateNoVarOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("novar");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("no-variability-checks");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateDebugOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("d");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("debug");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateTraceOptionAsExpected() {
    // When
    Option option = new MontiArcTool().initOptions().getOption("t");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("trace");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isFalse();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isFalse();
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void compileNullInputShouldThrow() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    assertThrows(NullPointerException.class, () -> tool.compile(null, "", "", "", false, false));
  }

  @Test
  void compileEmptyInputShouldThrow() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When && Then
    assertThrows(IllegalArgumentException.class, () -> tool.compile(new String[]{}, "", "", "", false, false));
  }

  @TempDir
  Path i1, i2, pp, s, r;

  @Test
  void compileEmptyInputDirShouldSucceed() {
    // Given
    MontiArcTool tool = new MontiArcTool();

    // When
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{i1.toString()}, null, null, null, false, false);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
    assertThat(asts).isEmpty();
  }

  @Test
  void compileFileInputShouldReturnAST() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();

    Path f = i1.resolve("Comp.arc");
    Files.writeString(f, "component Comp { }");

    // When
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{f.toString()}, null, null, null, false, false);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
    assertThat(asts).isNotEmpty();
    assertThat(asts.size()).isEqualTo(1);
    assertThat(asts).anyMatch(ast -> "Comp".equals(ast.getArcComponentType().getName()));
  }

  @Test
  void compileFileInputShouldReturnASTs() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();

    Path f1 = i1.resolve("Comp1.arc");
    Path f2 = i1.resolve("Comp2.arc");
    Files.writeString(f1, "component Comp1 { }");
    Files.writeString(f2, "component Comp2 { }");

    // When
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{f1.toString(), f2.toString()}, null, null, null, false, false);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
    assertThat(asts).isNotEmpty();
    assertThat(asts.size()).isEqualTo(2);
    assertThat(asts).anyMatch(ast -> "Comp1".equals(ast.getArcComponentType().getName()));
    assertThat(asts).anyMatch(ast -> "Comp2".equals(ast.getArcComponentType().getName()));
  }

  @Test
  void compileNonEmptyInputDirShouldReturnAST() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();

    Path f = i1.resolve("Comp.arc");
    Files.writeString(f, "component Comp { }");

    // When
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{i1.toString()}, null, null, null, false, false);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
    assertThat(asts).isNotEmpty();
    assertThat(asts.size()).isEqualTo(1);
    assertThat(asts).anyMatch(ast -> "Comp".equals(ast.getArcComponentType().getName()));
  }

  @Test
  void compileNonEmptyInputDirShouldReturnASTs() throws IOException {
    // Given
    MontiArcTool tool = new MontiArcTool();

    Path f1 = i1.resolve("Comp1.arc");
    Path f2 = i1.resolve("Comp2.arc");
    Files.writeString(f1, "component Comp1 { }");
    Files.writeString(f2, "component Comp2 { }");

    // When
    Set<ASTMACompilationUnit> asts = tool.compile(new String[]{i1.toString()}, null, null, null, false, false);

    // Then
    assertThat(Log.getFindings()).as(() -> Log.getFindings().toString()).isEmpty();
    assertThat(asts).isNotEmpty();
    assertThat(asts.size()).isEqualTo(2);
    assertThat(asts).anyMatch(ast -> "Comp1".equals(ast.getArcComponentType().getName()));
    assertThat(asts).anyMatch(ast -> "Comp2".equals(ast.getArcComponentType().getName()));
  }

  @Test
  public void testCreateEmpty() throws IOException {
    // Given
    File targetDir = new File(System.getProperty("java.io.tmpdir") + "test/montiarc/create/EmptyProject");
    String[] args = new String[] {
      "create", targetDir.getAbsolutePath(),
    };

    // When
    MontiArcTool.main(args);

    // Then
    Assertions.assertTrue(FileUtils.isDirectory(targetDir));
    Assertions.assertFalse(FileUtils.isEmptyDirectory(targetDir));
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());

    // Cleanup
    FileUtils.deleteDirectory(targetDir);
  }

  @Test
  public void testCreateEmptyTemplate() throws IOException {
    // Given
    File targetDir = new File(System.getProperty("java.io.tmpdir") + "test/montiarc/create/EmptyProject");
    String[] args = new String[] {
      "create", targetDir.getAbsolutePath(),
      "-t", "empty",
    };

    // When
    MontiArcTool.main(args);

    // Then
    Assertions.assertTrue(FileUtils.isDirectory(targetDir));
    Assertions.assertFalse(FileUtils.isEmptyDirectory(targetDir));
    Assertions.assertEquals(0, Log.getErrorCount(), () -> Log.getFindings().toString());

    // Cleanup
    FileUtils.deleteDirectory(targetDir);
  }
}

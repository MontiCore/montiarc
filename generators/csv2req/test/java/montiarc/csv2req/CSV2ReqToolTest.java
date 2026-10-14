/* (c) https://github.com/MontiCore/monticore */
package montiarc.csv2req;

import com.google.common.base.Preconditions;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.requirementsymbols.RequirementSymbolsMill;
import de.monticore.symbols.requirementsymbols._symboltable.IRequirementSymbolsArtifactScope;
import de.monticore.symbols.requirementsymbols._symboltable.RequirementSymbol;
import de.monticore.symbols.requirementsymbols._symboltable.RequirementSymbolsSymbols2Json;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the translation of requirements CSV files into requirement symbol
 * tables by the {@link CSV2ReqTool}.
 */
public class CSV2ReqToolTest extends ATestBase {

  protected static final Path REQUIREMENTS = Path.of(TEST_RESOURCE, "requirements");
  protected static final Path EXPORT = Path.of(TEST_RESOURCE, "export");
  protected static final Path VALID = Path.of(TEST_RESOURCE, "valid");
  protected static final Path INCORRECT = Path.of(TEST_RESOURCE, "incorrect");
  protected static final Path COLLISION = Path.of(TEST_RESOURCE, "collision");
  protected static final Path DELIMITER = Path.of(TEST_RESOURCE, "delimiter");

  @TempDir
  Path output;

  @BeforeEach
  public void init() {
    RequirementSymbolsMill.reset();
    RequirementSymbolsMill.init();
  }

  @AfterEach
  public void reset() {
    RequirementSymbolsMill.reset();
  }

  @Test
  public void shouldStoreSymbolTablePerCsvFile() {
    // When
    new CSV2ReqTool().run(new String[]{"-i", REQUIREMENTS.toString(), "-s", output.toString()});

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).containsExactlyInAnyOrder(
      Path.of("Requirements.reqsym"),
      Path.of("automotive", "Braking.reqsym"),
      Path.of("_2026_release", "safety_goals.reqsym")
    );
  }

  @Test
  public void shouldStoreSanitizedIds() {
    // When
    new CSV2ReqTool().run(List.of(REQUIREMENTS), output);

    // Then
    assertThat(Log.getFindings()).isEmpty();
    IRequirementSymbolsArtifactScope scope = this.load(Path.of("automotive", "Braking.reqsym"));
    assertThat(scope.getPackageName()).isEqualTo("automotive");
    assertThat(scope.getName()).isEqualTo("Braking");
    assertThat(scope.getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getFullName)
      .containsExactly("automotive.Braking.REQ_101", "automotive.Braking.REQ_102");
  }

  @Test
  public void shouldSanitizePackageAndArtifactName() {
    // When
    new CSV2ReqTool().run(List.of(REQUIREMENTS), output);

    // Then
    assertThat(Log.getFindings()).isEmpty();
    IRequirementSymbolsArtifactScope scope = this.load(Path.of("_2026_release", "safety_goals.reqsym"));
    assertThat(scope.getPackageName()).isEqualTo("_2026_release");
    assertThat(scope.getName()).isEqualTo("safety_goals");
    assertThat(scope.getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getFullName)
      .containsExactly("_2026_release.safety_goals.SG_1");
  }

  @Test
  public void shouldStoreOnlyIds() {
    // When
    new CSV2ReqTool().run(List.of(REQUIREMENTS), output);

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(FileReaderWriter.readFromFile(output.resolve("automotive").resolve("Braking.reqsym")))
      .contains("REQ_101")
      .doesNotContain("Emergency braking", "Safety Analysis", "Approved");
  }

  @Test
  public void shouldBeResolvable() {
    // Given
    new CSV2ReqTool().run(List.of(REQUIREMENTS), output);

    // When
    RequirementSymbolsMill.reset();
    RequirementSymbolsMill.init();
    RequirementSymbolsMill.globalScope().setSymbolPath(new MCPath(output));

    // Then
    assertThat(RequirementSymbolsMill.globalScope().resolveRequirement("Requirements.REQ_001")).isPresent();
    assertThat(RequirementSymbolsMill.globalScope().resolveRequirement("automotive.Braking.REQ_102")).isPresent();
    assertThat(RequirementSymbolsMill.globalScope().resolveRequirement("_2026_release.safety_goals.SG_1")).isPresent();
    assertThat(RequirementSymbolsMill.globalScope().resolveRequirement("automotive.Braking.REQ_001")).isEmpty();
    assertThat(Log.getFindings()).isEmpty();
  }

  @Test
  public void shouldUseEmptyPackageForFileInput() {
    // When
    new CSV2ReqTool().run(List.of(REQUIREMENTS.resolve("automotive").resolve("Braking.csv")), output);

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).containsExactly(Path.of("Braking.reqsym"));
    assertThat(this.load(Path.of("Braking.reqsym")).getPackageName()).isEmpty();
  }

  @Test
  public void shouldProcessInputsSeparatedByPathSeparator() {
    // Given
    String inputs = String.join(File.pathSeparator,
      REQUIREMENTS.resolve("Requirements.csv").toString(),
      REQUIREMENTS.resolve("automotive").toString());

    // When
    new CSV2ReqTool().run(new String[]{"-i", inputs, "-s", output.toString()});

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).containsExactlyInAnyOrder(
      Path.of("Requirements.reqsym"),
      Path.of("Braking.reqsym")
    );
  }

  @Test
  public void shouldStoreEmptySymbolTableForHeaderOnlyCsv() {
    // When
    new CSV2ReqTool().run(List.of(VALID.resolve("HeaderOnly.csv")), output);

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("HeaderOnly.reqsym")).getLocalRequirementSymbols()).isEmpty();
  }

  @Test
  public void shouldUseConfiguredIdColumn() {
    // When
    new CSV2ReqTool().run(new String[]{
      "-i", EXPORT.resolve("JiraExport.csv").toString(),
      "-s", output.toString(),
      "--id-column", "Key"
    });

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("JiraExport.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("PROJ_1", "PROJ_2");
  }

  @Test
  public void shouldReportMissingDefaultIdColumn() {
    // Given
    Path csv = EXPORT.resolve("JiraExport.csv");

    // When
    new CSV2ReqTool().run(new String[]{"-i", csv.toString(), "-s", output.toString()});

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.MISSING_COLUMN.format(csv, "'ID'"));
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldReportBlankIdColumn() {
    // When
    new CSV2ReqTool().run(new String[]{"-i", REQUIREMENTS.toString(), "-s", output.toString(), "-c", " "});

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(CSV2ReqError.CLI_PARSE.getErrorCode());
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldReportMissingInput() {
    // Given
    Path csv = REQUIREMENTS.resolve("Missing.csv");

    // When
    new CSV2ReqTool().run(List.of(csv), output);

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.INPUT_NOT_FOUND.format(csv));
  }

  @Test
  public void shouldReportMissingOutput() {
    // When
    new CSV2ReqTool().run(new String[]{"-i", REQUIREMENTS.toString()});

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(CSV2ReqError.MISSING_OUTPUT.getErrorCode());
  }

  @Test
  public void shouldPrintHelp() {
    // When
    new CSV2ReqTool().run(new String[]{"-h", "-i", REQUIREMENTS.toString(), "-s", output.toString()});

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldUseFirstPresentIdColumn() {
    // When
    new CSV2ReqTool().run(new String[]{
      "-i", EXPORT.resolve("JiraExport.csv").toString(), REQUIREMENTS.resolve("Requirements.csv").toString(),
      "-s", output.toString(),
      "-c", "ID", "Key"
    });

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("JiraExport.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("PROJ_1", "PROJ_2");
    assertThat(this.load(Path.of("Requirements.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("REQ_001", "REQ_002");
  }

  @Test
  public void shouldUseDelimiter() {
    // When
    new CSV2ReqTool().run(new String[]{
      "-i", DELIMITER.resolve("GermanExcel.csv").toString(),
      "-s", output.toString(),
      "-d", ";"
    });

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("GermanExcel.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("REQ_001", "REQ_002");
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @ValueSource(strings = {"\\t", "tab", "TAB", "\t"})
  public void shouldUseTabDelimiter(@NotNull String delimiter) {
    Preconditions.checkNotNull(delimiter);

    // When
    new CSV2ReqTool().run(new String[]{
      "-i", DELIMITER.resolve("Tabs.csv").toString(),
      "-s", output.toString(),
      "-d", delimiter
    });

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("Tabs.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("REQ_001");
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @ValueSource(strings = {";;", "\"", "\n", ""})
  public void shouldReportInvalidDelimiter(@NotNull String delimiter) {
    Preconditions.checkNotNull(delimiter);

    // When
    new CSV2ReqTool().run(new String[]{
      "-i", DELIMITER.resolve("GermanExcel.csv").toString(),
      "-s", output.toString(),
      "-d", delimiter
    });

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(CSV2ReqError.CLI_PARSE.getErrorCode());
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldPassSpaceDelimiterInArgumentFile() throws IOException {
    // Given
    Path csv = output.resolve("Spaces.csv");
    Files.writeString(csv, "Title ID\n\"Example requirement\" REQ-001\n");
    Path arguments = output.resolve("arguments.txt");
    Files.write(arguments, List.of("-i", csv.toString(), "-s", output.resolve("symbols").toString(), "-d", " "));

    // When
    new CSV2ReqTool().run(new String[]{"@" + arguments});

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.load(Path.of("symbols", "Spaces.reqsym")).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactly("REQ_001");
  }

  @Test
  public void shouldDeterminePackageBySourceRoot() {
    // When
    new CSV2ReqTool().run(new String[]{
      "-i", REQUIREMENTS.resolve("automotive").resolve("Braking.csv").toString(),
      "-r", EXPORT.toString(), REQUIREMENTS.toString(),
      "-s", output.toString()
    });

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).containsExactly(Path.of("automotive", "Braking.reqsym"));
    assertThat(this.load(Path.of("automotive", "Braking.reqsym")).getPackageName()).isEqualTo("automotive");
  }

  @Test
  public void shouldDeterminePackageByInnermostSourceRoot() {
    // When
    new CSV2ReqTool().run(
      List.of(REQUIREMENTS.resolve("automotive").resolve("Braking.csv")),
      List.of(Path.of(TEST_RESOURCE), REQUIREMENTS),
      output,
      new RequirementCsvReader()
    );

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(this.symbolFiles()).containsExactly(Path.of("automotive", "Braking.reqsym"));
  }

  @Test
  public void shouldExpandArgumentFile() throws IOException {
    // Given
    Path arguments = output.resolve("arguments.txt");
    Files.write(arguments, List.of(
      "--input",
      REQUIREMENTS.resolve("automotive").resolve("Braking.csv").toString(),
      "",
      "--source-root",
      REQUIREMENTS.toString(),
      "--symboltable",
      output.resolve("symbols").toString()
    ));

    // When
    new CSV2ReqTool().run(new String[]{"@" + arguments});

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(output.resolve("symbols").resolve("automotive").resolve("Braking.reqsym")).isRegularFile();
  }

  @Test
  public void shouldReportMissingArgumentFile() {
    // Given
    Path arguments = output.resolve("missing.txt");

    // When
    new CSV2ReqTool().run(new String[]{"@" + arguments});

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(CSV2ReqError.READ_ARGUMENT_FILE_FAILED.getErrorCode());
  }

  protected static Stream<Arguments> artifactCollisionProvider() {
    return Stream.of(
      Arguments.of("Same file in two inputs",
        List.of(COLLISION.resolve("first"), COLLISION.resolve("second")),
        COLLISION.resolve("first").resolve("Reqs.csv"), COLLISION.resolve("second").resolve("Reqs.csv"), "Reqs",
        Path.of("Reqs.reqsym"), List.of("REQ_001")),
      Arguments.of("File names differing in case",
        List.of(COLLISION.resolve("first"), COLLISION.resolve("lowercase")),
        COLLISION.resolve("first").resolve("Reqs.csv"), COLLISION.resolve("lowercase").resolve("reqs.csv"), "reqs",
        Path.of("Reqs.reqsym"), List.of("REQ_001")),
      Arguments.of("File names equal after sanitizing",
        List.of(COLLISION.resolve("sanitized")),
        COLLISION.resolve("sanitized").resolve("safety-goals.csv"),
        COLLISION.resolve("sanitized").resolve("safety_goals.csv"),
        "safety_goals", Path.of("safety_goals.reqsym"), List.of("SG_1"))
    );
  }

  /**
   * CSV files translating to the same artifact are reported, the artifact of
   * the first CSV file is stored.
   */
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("artifactCollisionProvider")
  public void shouldReportArtifactCollision(@NotNull String name,
                                            @NotNull List<Path> inputs,
                                            @NotNull Path first,
                                            @NotNull Path second,
                                            @NotNull String artifact,
                                            @NotNull Path expectedFile,
                                            @NotNull List<String> expectedIds) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(inputs);
    Preconditions.checkNotNull(first);
    Preconditions.checkNotNull(second);
    Preconditions.checkNotNull(artifact);
    Preconditions.checkNotNull(expectedFile);
    Preconditions.checkNotNull(expectedIds);

    // When
    new CSV2ReqTool().run(inputs, output);

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.DUPLICATE_ARTIFACT.format(first, second, artifact));
    assertThat(this.symbolFiles()).containsExactly(expectedFile);
    assertThat(this.load(expectedFile).getLocalRequirementSymbols())
      .extracting(RequirementSymbol::getName)
      .containsExactlyElementsOf(expectedIds);
  }

  @Test
  public void shouldReportFileWithoutCsvExtension() {
    // Given
    Path file = REQUIREMENTS.resolve("notes.txt");

    // When
    new CSV2ReqTool().run(List.of(file), output);

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.NOT_A_CSV_FILE.format(file));
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldReportCsvFileWithoutName() {
    // Given
    Path file = Path.of(TEST_RESOURCE, "invalidname", ".csv");

    // When
    new CSV2ReqTool().run(List.of(file.getParent()), output);

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.INVALID_FILE_NAME.format(file));
    assertThat(this.symbolFiles()).isEmpty();
  }

  @Test
  public void shouldReportMissingInputOption() {
    // When
    new CSV2ReqTool().run(new String[]{"-s", output.toString()});

    // Then
    assertThat(getLoggedErrorCodes()).containsExactly(CSV2ReqError.MISSING_INPUT.getErrorCode());
  }

  protected static Stream<Arguments> incorrectCsvProvider() {
    return Stream.of(
      arg("Empty.csv", Optional.empty(),
        CSV2ReqError.EMPTY_FILE.format(incorrect("Empty.csv"))),
      arg("MissingIdColumn.csv", Optional.empty(),
        CSV2ReqError.MISSING_COLUMN.format(incorrect("MissingIdColumn.csv"), "'ID'")),
      arg("UnterminatedQuote.csv", Optional.empty(),
        CSV2ReqError.MALFORMED_CSV.format(incorrect("UnterminatedQuote.csv"), 2, "")),
      arg("WrongColumnCount.csv", Optional.of(List.of("REQ_002")),
        CSV2ReqError.WRONG_COLUMN_COUNT.format(2, incorrect("WrongColumnCount.csv"), 2, 3)),
      arg("MissingId.csv", Optional.of(List.of("REQ_002")),
        CSV2ReqError.MISSING_ID.format(2, incorrect("MissingId.csv"))),
      arg("DuplicateId.csv", Optional.of(List.of("REQ_001")),
        CSV2ReqError.DUPLICATE_ID.format("REQ-001", "REQ_001", 3, incorrect("DuplicateId.csv"), 2)),
      arg("DuplicateSanitizedId.csv", Optional.of(List.of("REQ_001")),
        CSV2ReqError.DUPLICATE_ID.format("REQ.001", "REQ_001", 3, incorrect("DuplicateSanitizedId.csv"), 2))
    );
  }

  /**
   * Malformed rows are reported and skipped, the remaining requirements are
   * stored nonetheless. Malformed files are reported and no symbol table is
   * stored. Errors detailed by Apache Commons CSV are checked by their prefix.
   */
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("incorrectCsvProvider")
  public void shouldReportIncorrectCsv(@NotNull String file,
                                       @NotNull Optional<List<String>> expectedIds,
                                       @NotNull String expectedError) {
    Preconditions.checkNotNull(file);
    Preconditions.checkNotNull(expectedIds);
    Preconditions.checkNotNull(expectedError);

    // When
    new CSV2ReqTool().run(List.of(INCORRECT.resolve(file)), output);

    // Then
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .singleElement(as(InstanceOfAssertFactories.STRING))
      .startsWith(expectedError);
    if (expectedIds.isPresent()) {
      assertThat(this.load(Path.of(file.replace(".csv", ".reqsym"))).getLocalRequirementSymbols())
        .extracting(RequirementSymbol::getName)
        .containsExactlyElementsOf(expectedIds.get());
    } else {
      assertThat(this.symbolFiles()).isEmpty();
    }
  }

  protected static Arguments arg(@NotNull String file,
                                 @NotNull Optional<List<String>> expectedIds,
                                 @NotNull String expectedError) {
    return Arguments.of(file, expectedIds, expectedError);
  }

  protected static Path incorrect(@NotNull String file) {
    return INCORRECT.resolve(file);
  }

  /**
   * @return the stored symbol files relative to the output directory
   */
  protected List<Path> symbolFiles() {
    try (Stream<Path> files = Files.walk(output)) {
      return files.filter(Files::isRegularFile).map(output::relativize).toList();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  protected IRequirementSymbolsArtifactScope load(@NotNull Path symbolFile) {
    return new RequirementSymbolsSymbols2Json().load(output.resolve(symbolFile).toString());
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc.csv2req;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests the parsing of requirements CSV content by the {@link RequirementCsvReader}.
 * Malformed CSV files are tested with file fixtures in {@link CSV2ReqToolTest}.
 */
public class RequirementCsvReaderTest extends ATestBase {

  protected static final String HEADER = "ID,Title,Text,Type,Priority,Source,Status\n";

  protected static Stream<Arguments> csvProvider() {
    return Stream.of(
      Arguments.of("Single requirement",
        HEADER + "REQ-001,Example requirement,The system shall <do something>.,Functional,High,Stakeholder,Draft",
        List.of("REQ_001")),
      Arguments.of("Order of appearance",
        HEADER + "REQ-002,a,b,c,d,e,f\nREQ-001,a,b,c,d,e,f\n",
        List.of("REQ_002", "REQ_001")),
      Arguments.of("Byte order mark",
        "﻿" + HEADER + "REQ-001,a,b,c,d,e,f",
        List.of("REQ_001")),
      Arguments.of("Windows line endings",
        HEADER.replace("\n", "\r\n") + "REQ-001,a,b,c,d,e,f\r\nREQ-002,a,b,c,d,e,f\r\n",
        List.of("REQ_001", "REQ_002")),
      Arguments.of("Quoted values with commas, quotes, and line breaks",
        HEADER + "REQ-001,\"Quotes \"\"and\"\", commas\",\"First line\r\nsecond line\",c,d,,f",
        List.of("REQ_001")),
      Arguments.of("Quoted and padded ID",
        HEADER + "\" REQ-001 \",a,b,c,d,e,f",
        List.of("REQ_001")),
      Arguments.of("Blank lines",
        "\n" + HEADER + "\nREQ-001,a,b,c,d,e,f\n\n,,,,,,\nREQ-002,a,b,c,d,e,f\n\n",
        List.of("REQ_001", "REQ_002")),
      Arguments.of("Header only",
        HEADER,
        List.of()),
      Arguments.of("Only ID column",
        "ID\nREQ-001\nREQ-002",
        List.of("REQ_001", "REQ_002")),
      Arguments.of("Case-insensitive and padded header",
        "Title, id \nExample requirement,REQ-001",
        List.of("REQ_001"))
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("csvProvider")
  public void shouldReadIds(@NotNull String name, @NotNull String csv, @NotNull List<String> expectedIds) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(csv);
    Preconditions.checkNotNull(expectedIds);

    // When
    Optional<List<String>> ids = new RequirementCsvReader().read(csv, "Test.csv");

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ids).hasValueSatisfying(actual -> assertThat(actual).containsExactlyElementsOf(expectedIds));
  }

  @Test
  public void shouldReadConfiguredIdColumn() {
    // Given
    String csv = "ID,Title,Object Identifier\n"
      + "1,Example requirement,SYS-42";

    // When
    Optional<List<String>> ids = new RequirementCsvReader(List.of(" object identifier ")).read(csv, "Test.csv");

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ids).contains(List.of("SYS_42"));
  }

  protected static Stream<Arguments> idColumnsProvider() {
    return Stream.of(
      Arguments.of("First candidate present", "ID,Key\nREQ-1,PROJ-1", List.of("ID", "Key"), List.of("REQ_1")),
      Arguments.of("Precedence by candidate order", "ID,Key\nREQ-1,PROJ-1", List.of("Key", "ID"), List.of("PROJ_1")),
      Arguments.of("Later candidate present", "Key,Summary\nPROJ-1,Login", List.of("ID", "Key"), List.of("PROJ_1"))
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("idColumnsProvider")
  public void shouldUseFirstPresentIdColumn(@NotNull String name,
                                            @NotNull String csv,
                                            @NotNull List<String> idColumns,
                                            @NotNull List<String> expectedIds) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(csv);
    Preconditions.checkNotNull(idColumns);
    Preconditions.checkNotNull(expectedIds);

    // When
    Optional<List<String>> ids = new RequirementCsvReader(idColumns).read(csv, "Test.csv");

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ids).contains(expectedIds);
  }

  @Test
  public void shouldReadQuoteWithinUnquotedValueLiterally() {
    // Given
    String csv = "Title,ID,Text\n"
      + "The 19\" rack,REQ-001,The \"system\" shall do something.\n"
      + "Another title,REQ-002,The system shall do more.";

    // When
    Optional<List<String>> ids = new RequirementCsvReader().read(csv, "Test.csv");

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ids).contains(List.of("REQ_001", "REQ_002"));
  }

  @Test
  public void shouldReportMissingConfiguredIdColumn() {
    // Given
    String csv = HEADER + "REQ-001,a,b,c,d,e,f";

    // When
    Optional<List<String>> ids = new RequirementCsvReader(List.of("Key", "Object Identifier")).read(csv, "Test.csv");

    // Then
    assertThat(ids).isEmpty();
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.MISSING_COLUMN.format("Test.csv", "'Key', 'Object Identifier'"));
  }

  protected static Stream<Arguments> malformedProvider() {
    return Stream.of(
      Arguments.of("Unterminated quote",
        HEADER + "REQ-001,a,b,c,d,e,f\nREQ-002,\"multi\nline,b,c,d,e,f\n", 3),
      Arguments.of("Unterminated quote after multiline value",
        HEADER + "REQ-001,\"multi\nline\",b,c,d,e,f\nREQ-002,\"unterminated,b,c,d,e,f\n", 4),
      Arguments.of("Characters after closing quote",
        HEADER + "REQ-001,\"quoted\"trailing,b,c,d,e,f\n", 2)
    );
  }

  /**
   * Malformed content is reported in the row it starts in. The details are
   * provided by Apache Commons CSV, hence, only the message prefix is checked.
   */
  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("malformedProvider")
  public void shouldReportMalformedCsvInItsRow(@NotNull String name, @NotNull String csv, int expectedLine) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(csv);

    // When
    Optional<List<String>> ids = new RequirementCsvReader().read(csv, "Test.csv");

    // Then
    assertThat(ids).isEmpty();
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .singleElement(as(InstanceOfAssertFactories.STRING))
      .startsWith(CSV2ReqError.MALFORMED_CSV.format("Test.csv", expectedLine, ""));
  }

  protected static Stream<Arguments> delimiterProvider() {
    return Stream.of(
      Arguments.of("Semicolon", ';', "ID;Titel;Text\nREQ-001;Beispiel;\"Das System; soll\"\n"),
      Arguments.of("Tab", '\t', "ID\tTitle\tText\nREQ-001\tExample\t\"The system\tshall\"\n"),
      Arguments.of("Pipe", '|', "Title|ID\n\"a|b\"|REQ-001")
    );
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("delimiterProvider")
  public void shouldUseDelimiter(@NotNull String name, char delimiter, @NotNull String csv) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(csv);

    // When
    Optional<List<String>> ids = new RequirementCsvReader(List.of("ID"), delimiter).read(csv, "Test.csv");

    // Then
    assertThat(Log.getFindings()).isEmpty();
    assertThat(ids).contains(List.of("REQ_001"));
  }

  @Test
  public void shouldNotSplitAtOtherDelimiter() {
    // Given
    String csv = "ID;Titel\nREQ-001;Beispiel";

    // When
    Optional<List<String>> ids = new RequirementCsvReader().read(csv, "Test.csv");

    // Then
    assertThat(ids).isEmpty();
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.MISSING_COLUMN.format("Test.csv", "'ID'"));
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @ValueSource(chars = {'"', '\r', '\n'})
  public void shouldRejectInvalidDelimiter(char delimiter) {
    assertThat(RequirementCsvReader.isValidDelimiter(delimiter)).isFalse();
    assertThatIllegalArgumentException().isThrownBy(() -> new RequirementCsvReader(List.of("ID"), delimiter));
  }

  @Test
  public void shouldReportLineOfRowAfterMultilineValue() {
    // Given
    String csv = HEADER + "REQ-001,a,\"multi\nline\",c,d,e,f\nREQ-001,a,b,c,d,e,f\n";

    // When
    Optional<List<String>> ids = new RequirementCsvReader().read(csv, "Test.csv");

    // Then
    assertThat(ids).contains(List.of("REQ_001"));
    assertThat(Log.getFindings())
      .extracting(Finding::getMsg)
      .containsExactly(CSV2ReqError.DUPLICATE_ID.format("REQ-001", "REQ_001", 4, "Test.csv", 2));
  }

  protected static Stream<Arguments> sanitizeProvider() {
    return Stream.of(
      Arguments.of("REQ_001", "REQ_001"),
      Arguments.of("$req", "$req"),
      Arguments.of("REQ-001", "REQ_001"),
      Arguments.of("Req.1.2", "Req_1_2"),
      Arguments.of("R E Q", "R_E_Q"),
      Arguments.of("001", "_001"),
      Arguments.of("1-a", "_1_a"),
      Arguments.of("Ä-1", "__1"),
      Arguments.of("-", "_")
    );
  }

  @ParameterizedTest(name = "[{index}] {0} -> {1}")
  @MethodSource("sanitizeProvider")
  public void shouldSanitizeToName(@NotNull String id, @NotNull String expected) {
    Preconditions.checkNotNull(id);
    Preconditions.checkNotNull(expected);

    assertThat(RequirementCsvReader.sanitize(id))
      .isEqualTo(expected)
      .matches("[a-zA-Z_$][a-zA-Z_0-9$]*");
  }
}

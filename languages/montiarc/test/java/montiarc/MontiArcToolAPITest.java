/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}

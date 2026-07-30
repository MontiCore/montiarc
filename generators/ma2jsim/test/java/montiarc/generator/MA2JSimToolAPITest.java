/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contains unit tests for the application programming interface (API)
 * of {@link MA2JSimTool}.
 */
public class MA2JSimToolAPITest extends MA2JSimTestBase {

  /**
   * Verifies that the number of options matches the expected number of options.
   * This test catches changes upstream so new options can be properly
   * integrated into the MA2JSim tools and plugins.
   * <p>
   * If the expected number of options changes, please:
   * (1) Implement new options in the MontiArc Gradle tools.
   * (2) Add unit tests for new options in this test class.
   */
  @Test
  void initOptionsShouldInitializeExpectedNumberOfOptions() {
    // When
    Options options = new MA2JSimTool().initOptions();

    // Then
    assertThat(options.getOptions().size())
      .as(() -> options.getOptions().toString())
      .isEqualTo(12);
  }

  @Test
  void initOptionsShouldCreateOutputOptionAsExpected() {
    // When
    Option option = new MA2JSimTool().initOptions().getOption("o");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("output");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isFalse();
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("dir");
    assertThat(option.hasValueSeparator()).isFalse();
  }

  @Test
  void initOptionsShouldCreateHWCOptionAsExpected() {
    // When
    Option option = new MA2JSimTool().initOptions().getOption("hwc");

    // Then
    assertThat(option).isNotNull();
    assertThat(option.hasLongOpt()).isTrue();
    assertThat(option.getLongOpt()).isEqualTo("handwritten-code");
    assertThat(option.isRequired()).isFalse();
    assertThat(option.hasArg()).isTrue();
    assertThat(option.hasArgs()).isTrue();
    assertThat(option.getArgs()).isEqualTo(Option.UNLIMITED_VALUES);
    assertThat(option.hasOptionalArg()).isFalse();
    assertThat(option.hasArgName()).isTrue();
    assertThat(option.getArgName()).isEqualTo("paths");
    assertThat(option.hasValueSeparator()).isFalse();
  }
}

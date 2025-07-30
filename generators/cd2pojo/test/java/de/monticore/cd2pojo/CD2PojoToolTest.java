/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo;

import com.google.common.base.Preconditions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests {@link CD2PojoTool}
 */
class CD2PojoToolTest {

  protected static final String[] EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE = new String[]{
    "--checkcococs",
    "--input", "some_path",
    "--output", "other/path",
    "--symboltable", "foo/bar",
    "--handwrittencode", "bar/foo",
    "-path", "symbols",
  };

  @Test
  void shouldAugmentWithNewDefaultConfigTemplate() {
    // Given
    String[] args = EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE;

    // When
    String[] augmentedArgs = CD2PojoTool.augmentWithNewDefaultConfigTemplate(args);
    CommandLine cmd = parseCliOptions(augmentedArgs);

    // Then
    assertThat(augmentedArgs).hasSize(args.length + 2);
    assertThat(augmentedArgs).startsWith(args);
    assertThat(augmentedArgs).endsWith("-ct", "cd2pojo.init.CD2Java");
    assertThat(cmd.hasOption("ct")).isTrue();

  }

  @Test
  void shouldNotAugmentWithNewDefaultConfigTemplateForExistingShortArg() {
    // Given
    String[] args = Arrays.copyOf(
      EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE,
      EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE.length + 2
    );
    args[args.length - 2] = "-ct";
    args[args.length - 1] = "my-new-template";

    // When
    String[] augmentedArgs = CD2PojoTool.augmentWithNewDefaultConfigTemplate(args);
    CommandLine cmd = parseCliOptions(augmentedArgs);

    // Then
    assertThat(augmentedArgs).containsExactly(args);
    assertThat(cmd.hasOption("ct")).isTrue();
  }

  @Test
  void shouldNotAugmentWithNewDefaultConfigTemplateForExistingLongArg() {
    // Given
    String[] args = Arrays.copyOf(
      EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE,
      EXAMPLE_ARGS_WITHOUT_CONFIG_TEMPLATE.length + 2
    );
    args[args.length - 2] = "--configtemplate";
    args[args.length - 1] = "my-new-template";

    // When
    String[] augmentedArgs = CD2PojoTool.augmentWithNewDefaultConfigTemplate(args);
    CommandLine cmd = parseCliOptions(augmentedArgs);

    // Then
    assertThat(augmentedArgs).containsExactly(args);
    assertThat(cmd.hasOption("ct")).isTrue();
  }

  protected CommandLine parseCliOptions(@NotNull String[] args) {
    Preconditions.checkNotNull(args);

    CD2PojoTool tool4Options = new CD2PojoTool();

    Options options = new Options();
    tool4Options.addStandardOptions(options);
    tool4Options.addAdditionalOptions(options);

    CommandLineParser parser = new DefaultParser();

    try {
      return parser.parse(options, args);
    } catch (ParseException e) {
      fail(e.getMessage());
      return null;
    }
  }
}

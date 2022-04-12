/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds test for {@link GeneratorSetup}
 */
public class GeneratorSetupTest {

  protected static Stream<Arguments> templateAndBlacklistProvider() {
    return Stream.of(
        Arguments.of("B.ftl", Collections.singletonList("A.ftl")),
        Arguments.of("B.ftl", Collections.singletonList("B.ftl")),
        Arguments.of("B.ftl", Arrays.asList("A.ftl", "B.ftl")));
  }

  protected static Stream<Arguments> templateIllegalArgumentProvider() {
    return Stream.of(
        Arguments.of(null, NullPointerException.class),
        Arguments.of("", IllegalArgumentException.class));
  }

  protected static Stream<Arguments> templateAndBlacklistIllegalArgumentProvider() {
    return Stream.of(
        Arguments.of(null, Collections.singletonList(""), NullPointerException.class),
        Arguments.of("A.ftl", null, NullPointerException.class),
        Arguments.of("", null, NullPointerException.class),
        Arguments.of("", Collections.singletonList(""), IllegalArgumentException.class));
  }

  /**
   * Method under test {@link GeneratorSetup#getNewTemplateController(String)}
   */
  @ParameterizedTest
  @ValueSource(strings = {"A", "A.ftl", "a.b.A.ftl"})
  public void shouldProvideExpectedTC(@NotNull String template) {
    Preconditions.checkNotNull(template);
    Preconditions.checkArgument(!template.isEmpty());

    // Given
    GeneratorSetup setup = new GeneratorSetup();

    // When
    TemplateController tc = setup.getNewTemplateController(template);

    // Then
    Assertions.assertEquals(setup, tc.getGeneratorSetup());
    Assertions.assertEquals(template, tc.getTemplatename());
  }

  /**
   * Method under test {@link GeneratorSetup#getNewTemplateController(String, List)}
   */
  @ParameterizedTest
  @MethodSource("templateAndBlacklistProvider")
  public void shouldProvideExpectedTC(@NotNull String template, @NotNull List<String> blacklist) {
    Preconditions.checkNotNull(template);
    Preconditions.checkArgument(!template.isEmpty());

    // Given
    GeneratorSetup setup = new GeneratorSetup();

    // When
    TemplateController tc = setup.getNewTemplateController(template, blacklist);

    // Then
    Assertions.assertEquals(setup, tc.getGeneratorSetup());
    Assertions.assertEquals(template, tc.getTemplatename());
    Assertions.assertEquals(blacklist, tc.getTemplateBlackList());
  }

  /**
   * Method under test {@link GeneratorSetup#getNewTemplateController(String)}
   */
  @ParameterizedTest
  @MethodSource("templateIllegalArgumentProvider")
  public void getTCShouldThrowException(@Nullable String template, @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    GeneratorSetup setup = new GeneratorSetup();

    // When && Then
    Assertions.assertThrows(expected, () -> setup.getNewTemplateController(template));
  }

  /**
   * Method under test {@link GeneratorSetup#getNewTemplateController(String, List)}
   */
  @ParameterizedTest
  @MethodSource("templateAndBlacklistIllegalArgumentProvider")
  public void getTCBlacklistShouldThrowException(@Nullable String template, @Nullable List<String> blacklist,
                                                 @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // Given
    GeneratorSetup setup = new GeneratorSetup();

    // When && Then
    Assertions.assertThrows(expected, () -> setup.getNewTemplateController(template, blacklist));
  }
}
/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Holds test for {@link TemplateController}
 */
public class TemplateControllerTest {

  protected static Stream<Arguments> setupAndTemplateProvider() {
    return Stream.of(Arguments.of(new GeneratorSetup(), "A.ftl"));
  }

  protected static Stream<Arguments> setupTemplateAndBlacklistProvider() {
    return Stream.of(Arguments.of(new GeneratorSetup(), "A.ftl", Collections.singletonList("")));
  }

  protected static Stream<Arguments> setupAndTemplateIllegalArgumentProvider() {
    return Stream.of(
        Arguments.of(null, "A.ftl", NullPointerException.class),
        Arguments.of(Mockito.mock(GeneratorSetup.class), null, NullPointerException.class));
  }

  protected static Stream<Arguments> setupTemplateAndBlacklistIllegalArgumentProvider() {
    return Stream.of(
        Arguments.of(null, "A.ftl", Mockito.mock(List.class), NullPointerException.class),
        Arguments.of(Mockito.mock(GeneratorSetup.class), null, Mockito.mock(List.class), NullPointerException.class),
        Arguments.of(Mockito.mock(GeneratorSetup.class), "A.ftl", null, NullPointerException.class));
  }

  /**
   * Method under test {@link TemplateController#TemplateController(GeneratorSetup, String)}
   */
  @ParameterizedTest
  @MethodSource("setupAndTemplateProvider")
  public void constructorShouldCreateAsExpected(@NotNull GeneratorSetup setup, @NotNull String template) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(template);
    Preconditions.checkArgument(!template.isEmpty());

    // When
    TemplateController tc = new TemplateController(setup, template);

    // Then
    Assertions.assertEquals(setup, tc.getGeneratorSetup());
    Assertions.assertEquals(template, tc.getTemplatename());
  }

  /**
   * Method under test {@link TemplateController#TemplateController(GeneratorSetup, String, List)}
   */
  @ParameterizedTest
  @MethodSource("setupTemplateAndBlacklistProvider")
  public void constructorShouldCreateAsExpected(@NotNull GeneratorSetup setup, @NotNull String template,
                                                @NotNull List<String> blacklist) {
    Preconditions.checkNotNull(setup);
    Preconditions.checkNotNull(template);
    Preconditions.checkNotNull(blacklist);
    Preconditions.checkArgument(!template.isEmpty());

    // When
    TemplateController tc = new TemplateController(setup, template);

    // Then
    Assertions.assertEquals(setup, tc.getGeneratorSetup());
    Assertions.assertEquals(template, tc.getTemplatename());
  }

  /**
   * Method under test {@link TemplateController#TemplateController(GeneratorSetup, String)}
   */
  @ParameterizedTest
  @MethodSource("setupAndTemplateIllegalArgumentProvider")
  public void constructorShouldThrowException(@Nullable GeneratorSetup setup, @Nullable String template,
                                              @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // When && Then
    Assertions.assertThrows(expected, () -> new TemplateController(setup, template));
  }

  /**
   * Method under test {@link TemplateController#TemplateController(GeneratorSetup, String, List)}
   */
  @ParameterizedTest
  @MethodSource("setupTemplateAndBlacklistIllegalArgumentProvider")
  public void constructorShouldThrowException(@Nullable GeneratorSetup setup, @Nullable String template,
                                              @Nullable List<String> blacklist, @NotNull Class<Exception> expected) {
    Preconditions.checkNotNull(expected);

    // When && Then
    Assertions.assertThrows(expected, () -> new TemplateController(setup, template, blacklist));
  }
}
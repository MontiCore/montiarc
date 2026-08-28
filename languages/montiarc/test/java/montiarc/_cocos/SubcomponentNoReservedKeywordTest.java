/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.SubcomponentNoReservedKeyword;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static montiarc.util.ArcError.RESTRICTED_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubcomponentNoReservedKeyword}.
 */
class SubcomponentNoReservedKeywordTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // reserved keyword used as a package name
    "package key; component ValidComp1 { }",
    // regular subcomponent declaration, no reserved keyword involved
    "component ValidComp2 { component I { } I sub; }",
    // reserved keyword used as a parameter name
    "component ValidComp3(int key) {  }",
    // reserved keyword used as a generic type parameter name
    "component ValidComp4<key> { }",
    // reserved keyword used as a field name
    "component ValidComp5 { int key = 1; }",
    // reserved keyword used as a port name
    "component ValidComp6 { port in int key; }",
    // reserved keyword used as an automaton state name
    "component ValidComp7 { automaton { state key; } }",
    // reserved keyword used as a nested component type name
    "component ValidComp8 { component key { } }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentNoReservedKeyword("lang", Arrays.asList("key", "word")));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentNoReservedKeyword("lang", Arrays.asList("key", "word")));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // subcomponent instance named after the first reserved keyword
      arg("component InvalidComp1 { component Inner { } Inner key; }",
        RESTRICTED_IDENTIFIER),
      // subcomponent instance named after the second reserved keyword
      arg("component InvalidComp2 { component Inner { } Inner word; }",
        RESTRICTED_IDENTIFIER),
      // two subcomponent instances declared together, both named after the same reserved keyword
      arg("component InvalidComp3 { component Inner { } Inner key, key; }",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER),
      // two subcomponent instances declared separately, both named after the same reserved keyword
      arg("component InvalidComp4 { component Inner { } Inner key; Inner key; }",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER)
    );
  }
}

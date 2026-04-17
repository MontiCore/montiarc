/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentNoReservedKeyword;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static montiarc.util.ArcError.RESTRICTED_IDENTIFIER;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ComponentNoReservedKeyword}.
 */
class ComponentNoReservedKeywordTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ComponentNoReservedKeyword("lang", Arrays.asList("key", "word")));

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
    checker.addCoCo(new ComponentNoReservedKeyword("lang", Arrays.asList("key", "word")));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // reserved keyword used as a package name
      arg("""
        package key;
          component ValidComp1 { }
        """
      ),
      // reserved keyword used as a subcomponent instance name
      arg("""
        component ValidComp2 {
          component Inner { }
          Inner key;
        }
        """
      ),
      // reserved keyword used as a parameter name
      arg("component ValidComp3(int key) { }"),
      // reserved keyword used as a generic type parameter name
      arg("component ValidComp4<key> { }"),
      // reserved keyword used as a field name
      arg("""
        component ValidComp5 {
          int key = 1;
        }
        """
      ),
      // reserved keyword used as a port name
      arg("""
        component ValidComp6 {
          port in int key;
        }
        """
      ),
      // reserved keyword used as an automaton state name
      arg("""
        component ValidComp7 {
          automaton {
            state key;
          }
        }
        """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // component named after the first reserved keyword
      arg("component key { }",
        RESTRICTED_IDENTIFIER
      ),
      // component named after the second reserved keyword
      arg("component word { }",
        RESTRICTED_IDENTIFIER
      ),
      // component named after a reserved keyword, with a validly named inner component
      arg("""
          component key {
            component Inner { }
          }
          """,
        RESTRICTED_IDENTIFIER
      ),
      // component and its inner component both named after the first reserved keyword
      arg("""
          component key {
            component key { }
          }""",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER
      ),
      // component and its inner component both named after the second reserved keyword
      arg("""
          component word {
            component word { }
          }""",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER
      ),
      // component with two direct inner components, all three named after the same reserved keyword
      arg("""
          component key {
            component key { }
            component key { }
          }""",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER
      ),
      // three levels of nested components, all named after the same reserved keyword
      arg("""
          component key {
            component key {
              component key { }
            }
          }""",
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER,
        RESTRICTED_IDENTIFIER
      )
    );
  }
}

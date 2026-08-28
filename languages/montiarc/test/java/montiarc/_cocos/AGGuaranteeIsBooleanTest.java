/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcags._cocos.AGIsBoolean;
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

import java.util.stream.Stream;

import static arcags.util.AGError.CONDITION_EXPRESSION_WRONG_TYPE;
import static montiarc.util.MCError.EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE;
import static montiarc.util.MCError.QUALIFIED_NAME_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AGIsBoolean}.
 */
class AGGuaranteeIsBooleanTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // assume and guarantee both boolean literals
    """
      component ValidComp1 {
        assume: true;
        guarantee: true;
      }
    """,
    // guarantee only, no assume clause
    """
      component ValidComp2 {
        guarantee: true;
      }
    """,
    // one assume clause followed by two guarantee clauses
    """
      component ValidComp3 {
        port in int a;
        port in int a;
        assume: true;
        guarantee: true;
        guarantee: true;
      }
    """,
    // two separate assume/guarantee clause pairs
    """
      component ValidComp4 {
        port in int a;
        port in int a;
        assume: true;
        guarantee: true;
        assume: true;
        guarantee: true;
      }
    """,
    // assume and guarantee referencing a port's stream length
    """
      component ValidComp5 {
        port in int a;
        assume: a.len() > 0;
        guarantee: a.len() > 0;
      }
    """,
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AGIsBoolean());

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
    checker.addCoCo(new AGIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // guarantee with a non-boolean (String) literal
      arg("""
        component InvalidComp1 {
          guarantee: "true";
        }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE
      ),
      // guarantee comparing a port directly instead of via stream operations
      arg("""
        component InvalidComp2 {
          port in int a;
          guarantee: a > 0;
        }
        """,
        EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE
      ),
      // guarantee referencing an undeclared port
      arg("""
        component InvalidComp3 {
          guarantee: p.len() > 0;
        }
        """,
        QUALIFIED_NAME_NOT_FOUND
      )
    );
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcags._cocos.NoFieldInAGExpression;
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

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link NoFieldInAGExpression}.
 */
class NoFieldInAGExpressionTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // assume and guarantee without any port or field reference
    """
      component ValidComp1 {
        assume: true;
        guarantee: true;
      }
    """,
    // assume and guarantee referencing a port's stream length
    """
      component ValidComp2 {
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
    checker.addCoCo(new NoFieldInAGExpression());

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
    checker.addCoCo(new NoFieldInAGExpression());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // field referenced in the guarantee clause
      arg("""
        component InvalidComp1 {
          int a = 0;
          guarantee: a > 0;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // field referenced in the assume clause
      arg("""
        component InvalidComp2 {
          int a = 0;
          assume: a > 0;
          guarantee: true;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT
      ),
      // field referenced in both the assume and guarantee clauses, alongside a valid port reference
      arg("""
        component InvalidComp3 {
          port in int p;
          int a = 0;
          assume: a > 0 && p.len() > 0;
          guarantee: a > 0 && p.len() > 0;
        }
        """,
        FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT
      )
    );
  }
}

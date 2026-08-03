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

import java.io.IOException;
import java.util.stream.Stream;

import static arcags.util.AGError.CONDITION_EXPRESSION_WRONG_TYPE;
import static montiarc.util.MCError.EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE;
import static montiarc.util.MCError.QUALIFIED_NAME_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class AGGuaranteeIsBooleanTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    """
      component Default {
        assume: true;
        guarantee: true;
      }
    """,
    """
      component OnlyGuarantee {
        guarantee: true;
      }
    """,
    """
      component MultipleGuarantees1 {
        port in int a;
        port in int a;
        assume: true;
        guarantee: true;
        guarantee: true;
      }
    """,
    """
      component MultipleGuarantees2 {
        port in int a;
        port in int a;
        assume: true;
        guarantee: true;
        assume: true;
        guarantee: true;
      }
    """,
    """
      component UsePort {
        port in int a;
        assume: a.len() > 0;
        guarantee: a.len() > 0;
      }
    """,
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);
    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AGIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AGIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(
        """
          component NotBoolean {
            guarantee: "true";
          }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE),
      arg(
      """
        component PortsUsedWithoutStream {
          port in int a;
          guarantee: a > 0;
        }
      """,
        EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE),
      arg(
      """
        component PortsUsedWithoutStream {
          port in int a;
          guarantee: a > 0;
        }
      """,
        EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE),
      arg(
      """
        component PortDoesNotExist {
          guarantee: p.len() > 0;
        }
      """,
        QUALIFIED_NAME_NOT_FOUND)

    );
  }
}

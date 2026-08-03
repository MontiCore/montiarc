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

import java.io.IOException;
import java.util.stream.Stream;

import static montiarc.util.ArcError.FIELD_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

public class NoFieldInAGExpressionTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    """
      component NoPort {
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
    checker.addCoCo(new NoFieldInAGExpression());

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
    checker.addCoCo(new NoFieldInAGExpression());

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
          component FieldInGuarantee {
            int a = 0;
            guarantee: a > 0;
          }
        """,
        FIELD_REF_IN_STATIC_CONTEXT),
      arg(
        """
          component FieldInAssume {
            int a = 0;
            assume: a > 0;
            guarantee: true;
          }
        """,
        FIELD_REF_IN_STATIC_CONTEXT),
      arg(
        """
          component FieldInBoth {
            port in int p;
            int a = 0;
            assume: a > 0 && p.len() > 0;
            guarantee: a > 0 && p.len() > 0;
          }
        """,
        FIELD_REF_IN_STATIC_CONTEXT,
        FIELD_REF_IN_STATIC_CONTEXT)
    );
  }
}

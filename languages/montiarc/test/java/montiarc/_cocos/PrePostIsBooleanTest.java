/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcprepost._cocos.PrePostIsBoolean;
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

import static montiarc.util.PrePostError.CONDITION_EXPRESSION_WRONG_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class PrePostIsBooleanTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // only post condition
    """
      component OnlyPost {
        post: true;
      }
    """,
    // Boolean constant
    """
      component OnlyPost {
        pre: true;
        post: true;
      }
    """,
    // expression
    """
      component OnlyPost {
        pre: true && false;
        post: true && false;
      }
    """,
    // expression with symbols
    """
      component OnlyPost {
        port in int a;
        port in int b;
        pre: a == b;
        post: a == b;
      }
   """,
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PrePostIsBoolean());

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
    checker.addCoCo(new PrePostIsBoolean());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // post wrong type
      arg(
        """
          component OnlyPost {
            post: "Error";
          }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE),
      // pre wrong, post correct
      arg(
        """
          component OnlyPost {
            pre: "Error";
            post: true;
          }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE),
      // pre correct, post wrong
      arg(
        """
          component OnlyPost {
            pre: true;
            post: "Error";
          }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE),
      // both wrong
      arg(
        """
          component OnlyPost {
            pre: "Error";
            post: "Error";
          }
        """,
        CONDITION_EXPRESSION_WRONG_TYPE,
        CONDITION_EXPRESSION_WRONG_TYPE),
      // With symbols
      arg(
        """
          component OnlyPost {
            port in String a;
            port in String b;
            pre: a + b;
            post: a + b;
          }
       """,
        CONDITION_EXPRESSION_WRONG_TYPE,
        CONDITION_EXPRESSION_WRONG_TYPE)
    );
  }
}

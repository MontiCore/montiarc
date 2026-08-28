/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OnlyAssignmentOrCallExpressionStatement;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.INVALID_STATEMENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link OnlyAssignmentOrCallExpressionStatement}.
 */
class OnlyAssignmentOrCallExpressionStatementTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OnlyAssignmentOrCallExpressionStatement());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // component without any statements
      arg("component ValidComp1 { }"),
      // increment expression statement
      arg("component ValidComp2 { int x = 0; compute { x++; } }"),
      // assignment expression statement
      arg("component ValidComp3 { int x = 0; compute { x = 1 + 2; } }"),
      // assignment expression statement in an init block
      arg("component ValidComp4 { int x = 0; compute { } init { x = 42; } }"),
      // call expression statement
      arg("component ValidComp5 { compute { System.out.println(\"Test\"); } }")
    );
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
    checker.addCoCo(new OnlyAssignmentOrCallExpressionStatement());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // literal expression statement
      arg(
        "component InvalidComp1 { compute { 1; } }",
        INVALID_STATEMENT
      ),
      // bare variable reference expression statement
      arg(
        "component InvalidComp2 { int x = 0; compute { x; } }",
        INVALID_STATEMENT
      ),
      // arithmetic expression statement
      arg(
        "component InvalidComp3 { int x = 0; compute { x + 1; } }",
        INVALID_STATEMENT
      ),
      // boolean literal, logical, and relational expression statements
      arg(
        """
        component InvalidComp4 {
          int x = 0;
          compute {
            true;
            b && true;
            b || true;
            1 == 2;
            1 > 2;
          }
        }
        """,
        INVALID_STATEMENT, INVALID_STATEMENT, INVALID_STATEMENT, INVALID_STATEMENT, INVALID_STATEMENT
      ),
      // parenthesized assignment expression statement (not a direct assignment)
      arg(
        """
        component InvalidComp5 {
          compute {
            int a = 0;
            (a = 0);
          }
        }
        """,
        INVALID_STATEMENT
      ),
      // parenthesized assignment expression statements alongside an if-statement and empty blocks
      arg(
        """
        component InvalidComp6 {
          compute {
            int i = 1;
            if (false); (i = 0);{};
            (i = 0);{};
          }
        }
        """,
        INVALID_STATEMENT, INVALID_STATEMENT
      )
    );
  }
}

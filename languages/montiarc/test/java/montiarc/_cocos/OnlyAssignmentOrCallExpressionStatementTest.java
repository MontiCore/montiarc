/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.OnlyAssignmentOrCallExpressionStatement;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.ATestBase;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class OnlyAssignmentOrCallExpressionStatementTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  public void shouldNotReportError(@NotNull String model) throws IOException {
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
      Arguments.of(
        "component Comp1 { }"
      ),
      Arguments.of(
        "component Comp2 { int x = 0; compute { x++; } }"
      ),
      Arguments.of(
        "component Comp3 { int x = 0; compute { x = 1 + 2; } }"
      ),
      Arguments.of(
        "component Comp4 { int x = 0; compute { } init { x = 42; } }"
      ),
      Arguments.of(
        "component Comp5 { compute { System.out.println(\"Test\"); } }"
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new OnlyAssignmentOrCallExpressionStatement());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(ATestBase.getLoggedErrorCodes()).containsExactlyInAnyOrder(ATestBase.getErrorCodes(errors));
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(
        "component Comp1 { compute { 1; } }",
        ArcError.INVALID_STATEMENT
      ),
      arg(
        "component Comp2 { int x = 0; compute { x; } }",
        ArcError.INVALID_STATEMENT
      ),
      arg(
        "component Comp3 { int x = 0; compute { x + 1; } }",
        ArcError.INVALID_STATEMENT
      ),
      arg(
        "component Comp4 { int x = 0;" +
          "compute { " +
          "true;" +
          "b && true;" +
          "b || true;" +
          "1 == 2;" +
          "1 > 2;" +
          "}" +
          "}",
        ArcError.INVALID_STATEMENT, ArcError.INVALID_STATEMENT, ArcError.INVALID_STATEMENT, ArcError.INVALID_STATEMENT, ArcError.INVALID_STATEMENT
      ),
      arg(
        "component Comp5 { " +
          "compute {" +
          "int a = 0;" +
          "(a = 0);" +
          "} " +
          "}",
        ArcError.INVALID_STATEMENT
      ),
      arg(
        "component Comp6 { " +
          "compute {" +
          "int i = 1;" +
          "if (false); (i = 0);{};" +
          "(i = 0);{}; " +
          "} " +
          "}",
        ArcError.INVALID_STATEMENT, ArcError.INVALID_STATEMENT
      )
    );
  }
}

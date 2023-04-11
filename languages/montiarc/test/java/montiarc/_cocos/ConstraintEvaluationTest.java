/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.VariableArcError;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.ConstraintSatisfied4Comp;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * The class under test is {@link ConstraintSatisfied4Comp}.
 */
public class ConstraintEvaluationTest extends MontiArcAbstractTest {

  @ParameterizedTest
  @ValueSource(strings = {
    // no constraint
    "component Comp1 { }",
    // tautology constraint
    "component Comp2 { constraint(true); }",
    // feature constraint satisfiable
    "component Comp3 { feature f; constraint(f); } ",
    // parameter constraint satisfiable
    "component Comp4(boolean p) { constraint(p); } ",
    // feature constraint of instance satisfied
    "component Comp5 { " +
      "component Inner { " +
      "feature f; " +
      "constraint(f); " +
      "} " +
      "Inner sub; " +
      "constraint(sub.f); " +
      "}",
    // parameter constraint of instance satisfied
    "component Comp6 { " +
      "component Inner(boolean p) { " +
      "constraint(p); " +
      "} " +
      "Inner sub(true); " +
      "}",
    // parameter constraint of instance depth 2 satisfied
    "component Comp7 { " +
      "component Inner1(boolean p) { " +
      "component Inner2(boolean p) { " +
      "constraint(p); " +
      "} " +
      "Inner2 sub(p); " +
      "} " +
      "Inner1 sub(true); " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintSatisfied4Comp());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintSatisfied4Comp());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // unsatisfiable constraint
      arg("component Comp1 { constraint(false); }",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // feature constraint unsatisfiable
      arg("component Comp2 { feature f; constraint(f && !f); } ",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // parameter constraint unsatisfiable
      arg("component Comp3(boolean p) { constraint(p && !p); } ",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // feature constraint of instance unsatisfied
      arg("component Comp4 { " +
        "component Inner { " +
        "feature f; " +
        "constraint(f); " +
        "} " +
        "Inner sub; " +
        "constraint(!sub.f); " +
        "}",
      VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // parameter constraint of instance unsatisfied
      arg("component Comp5 { " +
        "component Inner(boolean p) { " +
        "constraint(p); " +
        "} " +
        "Inner sub(false); " +
        "}",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // parameter constraint of instance unsatisfied
      arg("component Comp6 { " +
          "component Inner1(boolean p) { " +
          "component Inner2(boolean p) { " +
          "constraint(p); " +
          "} " +
          "Inner2 sub(p); " +
          "} " +
          "Inner1 sub(false); " +
          "}",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // two unsatisfiable constraints
      arg("component Comp7 { constraint(false); constraint (false); }",
        VariableArcError.CONSTRAINT_NOT_SATISFIED),
      // tautology and unsatisfiable constraint
      arg("component Comp8 { constraint(true); constraint (false); }",
        VariableArcError.CONSTRAINT_NOT_SATISFIED)
    );
  }
}

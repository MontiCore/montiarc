/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import montiarc.util.MCError;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.SubcomponentsConstraint;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubcomponentsConstraint}.
 */
public class SubcomponentsConstraintTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { feature f; constraint(f); }");
    compile("package a.b; component B { feature f; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no constraint
    "component Comp1 { }",
    // tautology constraint
    "component Comp2 { constraint(true); }",
    // unsatisfiable constraint
    "component Comp3 { constraint(false); }",
    // feature constraint of instance satisfied
    "component Comp4 { " +
      "a.b.A a;" +
      "}",
    // feature constraint of instance satisfied
    "component Comp5 { " +
      "a.b.B b;" +
      "constraint(b.f);" +
      "}",
    // feature constraint of instance indirectly bound
    "component Comp6 { " +
      "a.b.A a;" +
      "a.b.B b;" +
      "constraint(b.f == a.f);" +
      "}",
    // feature constraint of instance indirectly bound
    "component Comp7 { " +
      "feature f;" +
      "a.b.B b1;" +
      "a.b.B b2;" +
      "constraint(b1.f == b2.f && b2.f == f);" +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentsConstraint());

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
    checker.addCoCo(new SubcomponentsConstraint());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // unused subcomponent feature
      arg("component Comp1 { a.b.B b; }",
        VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT),
      // underspecified constraint
      arg("component Comp2 { a.b.B b1; a.b.B b2; constraint(b1.f != b2.f); }",
        VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT),
      // underspecified constraint dependent on implication
      arg("component Comp3 { feature f; a.b.B b; constraint(!f || b.f); }",
        VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT),
      // Type not found (should not result in an error other than Missing Component -> Robustness)
      arg("component Comp4 { a.b.X x; }",
        MCError.MISSING_COMPONENT)
    );
  }
}

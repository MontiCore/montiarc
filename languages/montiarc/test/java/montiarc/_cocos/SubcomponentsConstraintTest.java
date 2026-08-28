/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.SubcomponentsConstraint;

import java.util.stream.Stream;

import static montiarc.util.MCError.MISSING_COMPONENT;
import static montiarc.util.VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubcomponentsConstraint}.
 */
class SubcomponentsConstraintTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { feature f; constraint(f); }");
    compile("package a.b; component B { feature f; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no constraint, no subcomponents
    "component ValidComp1 { }",
    // tautology constraint, no subcomponents
    "component ValidComp2 { constraint(true); }",
    // unsatisfiable constraint, no subcomponents
    "component ValidComp3 { constraint(false); }",
    // subcomponent whose own internal constraint already binds its feature
    "component ValidComp4 { a.b.A a; }",
    // subcomponent feature bound by an explicit outer constraint
    "component ValidComp5 { a.b.B b; constraint(b.f); }",
    // subcomponent feature indirectly bound via another subcomponent's already-constrained feature
    "component ValidComp6 { a.b.A a; a.b.B b; constraint(b.f == a.f); }",
    // two subcomponents' features chained together and bound to the enclosing component's own feature
    "component ValidComp7 { feature f; a.b.B b1; a.b.B b2; constraint(b1.f == b2.f && b2.f == f); }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentsConstraint());

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
    checker.addCoCo(new SubcomponentsConstraint());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // subcomponent feature left unconstrained
      arg("component InvalidComp1 { a.b.B b; }",
        SUBCOMPONENTS_NOT_CONSTRAINT),
      // constraint only excludes one combination, leaving the subcomponents' features underspecified
      arg("component InvalidComp2 { a.b.B b1; a.b.B b2; constraint(b1.f != b2.f); }",
        SUBCOMPONENTS_NOT_CONSTRAINT),
      // constraint conditionally binds the subcomponent feature only via an implication
      arg("component InvalidComp3 { feature f; a.b.B b; constraint(!f || b.f); }",
        SUBCOMPONENTS_NOT_CONSTRAINT),
      // reference to an undeclared component type (robustness: no cascading error beyond the missing type)
      arg("component InvalidComp4 { a.b.X x; }",
        MISSING_COMPONENT)
    );
  }
}

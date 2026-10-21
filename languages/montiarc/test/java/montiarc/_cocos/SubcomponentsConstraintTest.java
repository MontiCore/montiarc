/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
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
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.MCError.MISSING_COMPONENT;
import static montiarc.util.VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubcomponentsConstraint}.
 */
class SubcomponentsConstraintTest extends MontiArcTestBase {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentsConstraint(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

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
    // subcomponent feature bound by an explicit outer constraint that implies the subcomponent's constraint
    "component ValidComp4 { a.b.A a; constraint(a.f); }",
    // subcomponent feature bound by an explicit outer constraint
    "component ValidComp5 { a.b.B b; constraint(b.f); }",
    // subcomponent feature indirectly bound via another subcomponent's feature
    "component ValidComp6 { a.b.A a; a.b.B b; constraint(a.f && b.f == a.f); }",
    // two subcomponents' features chained together and bound to the enclosing component's own feature
    "component ValidComp7 { feature f; a.b.B b1; a.b.B b2; constraint(b1.f == b2.f && b2.f == f); }",
    // subcomponent that is never present
    "component ValidComp8 { feature g; constraint(!g); varif (g) { a.b.B b; } }",
    // subcomponent feature bound by a constraint in the same variation point
    "component ValidComp9 { feature g; varif (g) { a.b.B b; constraint(b.f); } }",
    // inherited subcomponent feature bound by an explicit outer constraint
    "component ValidComp10 { component C extends a.b.A { } C c; constraint(c.f); }",
    // subcomponent feature only bound by the subcomponent's own constraint, reported by SubcomponentConstraintsImplied
    "component ValidComp11 { a.b.A a; }",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = checker();

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

    MontiArcCoCoChecker checker = checker();

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
      // subcomponent feature left unconstrained in the configurations the subcomponent is present in
      arg("component InvalidComp5 { feature g; varif (g) { a.b.B b; } else { constraint(true); } }",
        SUBCOMPONENTS_NOT_CONSTRAINT),
      // feature of the first of two same-named alternative subcomponents left unconstrained
      arg("component InvalidComp6 { feature g; component Other { feature f; } varif (g) { a.b.B s; } else { Other s; } "
          + "constraint(g || s.f); }",
        SUBCOMPONENTS_NOT_CONSTRAINT),
      // reference to an undeclared component type (robustness: no cascading error beyond the missing type)
      arg("component InvalidComp4 { a.b.X x; }",
        MISSING_COMPONENT)
    );
  }
}

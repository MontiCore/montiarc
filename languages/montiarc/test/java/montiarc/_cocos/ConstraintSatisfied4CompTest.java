/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.ConstraintSatisfied4Comp;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.CONSTRAINT_NOT_SATISFIED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConstraintSatisfied4Comp}.
 */
class ConstraintSatisfied4CompTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
    compile("package a.b; component A { feature ff; constraint(ff); }");
    compile("package a.b; component B extends a.b.A { }");
    compile("package a.b; component C { feature ff; a.b.B b; constraint(ff == b.ff); }");
    compile("package a.b; component D extends a.b.C { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no constraint
    "component ValidComp1 { }",
    // tautology constraint
    "component ValidComp2 { constraint(true); }",
    // tautology constraint with implicit cast
    "component ValidComp3 { constraint(1==1.0); }",
    // feature constraint satisfiable
    "component ValidComp4 { feature f; constraint(f); } ",
    // parameter constraint satisfiable
    "component ValidComp5(boolean p) { constraint(p); } ",
    // feature constraint of instance satisfied
    "component ValidComp6 { component Inner { feature f; constraint(f); } Inner sub; constraint(sub.f); }",
    // parameter constraint of instance satisfied
    "component ValidComp7 { component Inner(boolean p) { constraint(p); } Inner sub(true); }",
    // parameter constraint of instance depth 2 satisfied
    "component ValidComp8 { component Inner1(boolean p) { component Inner2(boolean p) { constraint(p); } Inner2 sub(p); } Inner1 sub(true); }",
    // inherited constraint satisfiable
    "component ValidComp9 extends a.b.A { }",
    // implicit inherited constraint in instance satisfiable
    "component ValidComp10 extends a.b.A { a.b.D d; } "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintSatisfied4Comp());

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
    checker.addCoCo(new ConstraintSatisfied4Comp());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // unsatisfiable constraint
      arg("component InvalidComp1 { constraint(false); }",
        CONSTRAINT_NOT_SATISFIED),
      // unsatisfiable constraint with implicit cast
      arg("component InvalidComp2 { constraint(1==1.5); }",
        CONSTRAINT_NOT_SATISFIED),
      // feature constraint unsatisfiable
      arg("component InvalidComp3 { feature f; constraint(f && !f); } ",
        CONSTRAINT_NOT_SATISFIED),
      // parameter constraint unsatisfiable
      arg("component InvalidComp4(boolean p) { constraint(p && !p); } ",
        CONSTRAINT_NOT_SATISFIED),
      // feature constraint of instance unsatisfied
      arg("component InvalidComp5 { component Inner { feature f; constraint(f); } Inner sub; constraint(!sub.f); }",
        CONSTRAINT_NOT_SATISFIED),
      // parameter constraint of instance unsatisfied
      arg("component InvalidComp6 { component Inner(boolean p) { constraint(p); } Inner sub(false); }",
        CONSTRAINT_NOT_SATISFIED),
      // parameter constraint of instance (depth 2) unsatisfied
      arg("component InvalidComp7 { component Inner1(boolean p) { component Inner2(boolean p) { constraint(p); } Inner2 sub(p); } Inner1 sub(false); }",
        CONSTRAINT_NOT_SATISFIED),
      // two unsatisfiable constraints
      arg("component InvalidComp8 { constraint(false); constraint (false); }",
        CONSTRAINT_NOT_SATISFIED),
      // tautology and unsatisfiable constraint
      arg("component InvalidComp9 { constraint(true); constraint (false); }",
        CONSTRAINT_NOT_SATISFIED),
      // inherited constraint contradiction
      arg("component InvalidComp10 extends a.b.A { constraint(!ff); }",
        CONSTRAINT_NOT_SATISFIED),
      // inherited constraint of instance contradiction
      arg("component InvalidComp11 { a.b.B b; constraint(!b.ff); }",
        CONSTRAINT_NOT_SATISFIED),
      // implicit inherited constraint in instance contradiction
      arg("component InvalidComp12 { a.b.D d; constraint(!d.ff); }",
        CONSTRAINT_NOT_SATISFIED)
    );
  }

  /**
   * Constraints containing values that cannot be determined statically (here: method calls and
   * a variable of a type without SMT representation) are reported as error if they are
   * unsatisfiable for all of these values, as warning if they are unsatisfiable for some of
   * them, and not at all if they are satisfiable for all of them.
   */
  @ParameterizedTest
  @MethodSource("modelsWithUnknownValues")
  void shouldClassifyConstraintsWithUnknownValues(@NotNull String model, boolean error, boolean warning) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);
    Log.clearFindings();

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintSatisfied4Comp());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).filteredOn(Finding::isError)
      .extracting(Finding::getMsg)
      .allMatch(msg -> msg.startsWith(CONSTRAINT_NOT_SATISFIED.getErrorCode()))
      .hasSize(error ? 1 : 0);
    assertThat(Log.getFindings()).filteredOn(Finding::isWarning)
      .extracting(Finding::getMsg)
      .allMatch(msg -> msg.startsWith(CONSTRAINT_NOT_SATISFIED.getErrorCode()))
      .hasSize(warning ? 1 : 0);
  }

  protected static Stream<Arguments> modelsWithUnknownValues() {
    return Stream.of(
      // unsatisfiable whatever the method returns
      Arguments.of("component UnknownComp1 { constraint((java.lang.Math.abs(1) > 0) && false); }", true, false),
      // satisfiable whatever the method returns
      Arguments.of("component UnknownComp2 { constraint((java.lang.Math.abs(1) > 0) || true); }", false, false),
      // satisfiable only if the method returns true
      Arguments.of("component UnknownComp3 { constraint((java.lang.Math.abs(1) > 0)); }", false, true),
      // the feature can adapt to whatever the method returns
      Arguments.of("component UnknownComp4 { feature f; constraint(f == (java.lang.Math.abs(1) > 0)); }", false, false),
      // parameter bound to a method call, satisfiable only for positive results
      Arguments.of("component UnknownComp5 { component Inner(int p) { constraint(p > 0); } Inner sub(java.lang.Math.abs(-1)); }", false, true),
      // the parameter's type has no SMT representation, hence, the comparison is unknown
      Arguments.of("component UnknownComp6(String s) { constraint(s == \"x\"); }", false, true)
    );
  }
}

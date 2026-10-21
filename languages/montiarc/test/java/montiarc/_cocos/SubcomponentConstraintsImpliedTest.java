/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.SubcomponentConstraintsImplied;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.VariableArcError.SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link SubcomponentConstraintsImplied}.
 */
class SubcomponentConstraintsImpliedTest extends MontiArcTestBase {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubcomponentConstraintsImplied(withinComponentResolver,
      new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @BeforeEach
  protected void setUpComponents() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    compile("package a.b; component A { feature ff; constraint(ff); }");
    compile("package a.b; component B extends a.b.A { }");
    compile("package a.b; component C { feature ff; a.b.B b; constraint(ff == b.ff && b.ff); }");
    compile("package a.b; component D extends a.b.C { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no subcomponents
    "component ValidComp1 { }",
    // feature constraint of instance implied
    "component ValidComp2 { component Inner { feature f; constraint(f); } Inner sub; constraint(sub.f); }",
    // parameter constraint of instance implied by the argument
    "component ValidComp3 { component Inner(boolean p) { constraint(p); } Inner sub(true); }",
    // parameter constraint of instance at depth 2 implied by each level
    "component ValidComp4 { component Inner1(boolean p) { component Inner2(boolean p) { constraint(p); } Inner2 sub(p); constraint(p); } Inner1 sub(true); }",
    // inherited constraint of instance implied
    "component ValidComp5 { a.b.B b; constraint(b.ff); }",
    // constraint of instance on its own subcomponent's feature is satisfiable
    "component ValidComp6 { a.b.D d; constraint(d.ff); }",
    // instance whose constraint is violated is never present
    "component ValidComp7 { feature f; constraint(!f); component Inner(boolean p) { constraint(p); } varif (f) { Inner sub(false); } }",
    // constraint of instance implied by a constraint in the same variation point
    "component ValidComp8 { feature f; component Inner { feature g; constraint(g); } varif (f) { Inner sub; constraint(sub.g); } }",
    // parameter constraint of instance implied by an inherited constraint
    "component ValidComp9 extends a.b.A { component Inner(boolean p) { constraint(p); } Inner sub(ff); }",
    // inline instance whose constraint is violated is never present
    "component ValidComp10 { feature f; constraint(!f); varif (f) { component Inner sub { feature g; constraint(g); } } }",
    // type without valid configurations is left to ConstraintSatisfied4Comp
    "component ValidComp11 { component Inner { constraint(false); } Inner s1; Inner s2; }",
    // parameter constraint of instance implied by an argument that is converted to the type of the parameter
    "component ValidComp12 { component Inner(double d) { constraint(d > 0.5); } Inner sub(1); }",
    // parameter constraint of instance implied by a default value that is converted to the type of the parameter
    "component ValidComp13 { component Inner(double d = 1) { constraint(d > 0.5); } Inner sub; }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

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

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // feature constraint of instance contradicted
      arg("component InvalidComp1 { component Inner { feature f; constraint(f); } Inner sub; constraint(!sub.f); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // feature constraint of instance not implied
      arg("component InvalidComp2 { component Inner { feature f; constraint(f); } Inner sub; }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // parameter constraint of instance contradicted by the argument
      arg("component InvalidComp3 { component Inner(boolean p) { constraint(p); } Inner sub(false); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // parameter constraint of instance only satisfied in some configurations
      arg("component InvalidComp4 { feature f; component Inner(boolean p) { constraint(p); } Inner sub(f); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // parameter constraint of instance at depth 2 not implied by the intermediate level
      arg("component InvalidComp5 { component Inner1(boolean p) { component Inner2(boolean p) { constraint(p); } Inner2 sub(p); } Inner1 sub(true); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // inherited constraint of instance contradicted
      arg("component InvalidComp6 { a.b.B b; constraint(!b.ff); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // constraint of instance on its own subcomponent's feature unsatisfiable
      arg("component InvalidComp7 { a.b.D d; constraint(!d.ff); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // instance whose constraint is violated is always present
      arg("component InvalidComp8 { feature f; constraint(f); component Inner(boolean p) { constraint(p); } varif (f) { Inner sub(false); } }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED),
      // only one of two instances constrained
      arg("component InvalidComp9 { component Inner { feature f; constraint(f); } Inner s1; Inner s2; constraint(s1.f); }",
        SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED)
    );
  }

  @Test
  void shouldWarnIfImplicationDependsOnUnknownValues() {
    // Given
    ASTMACompilationUnit ast = compile(
      "component UnknownComp1 { component Inner(int p) { constraint(p > 0); } Inner sub(java.lang.Math.abs(-1)); }");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).noneMatch(Finding::isError);
    assertThat(Log.getFindings()).filteredOn(Finding::isWarning)
      .extracting(Finding::getMsg)
      .singleElement()
      .matches(msg -> msg.startsWith(SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED.getErrorCode()));
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component CyclicComp1 { component Inner(int p = p) { constraint(p > 0); } Inner sub; }",
    "component CyclicComp2 { component Inner(int p = q, int q = p) { constraint(p > 0); } Inner sub; }"
  })
  void shouldWarnIfDefaultValuesAreCyclic(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).noneMatch(Finding::isError);
    assertThat(Log.getFindings()).filteredOn(Finding::isWarning)
      .extracting(Finding::getMsg)
      .singleElement()
      .matches(msg -> msg.startsWith(SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED.getErrorCode()));
  }

  @Test
  void shouldWarnIfConstraintsDependOnFieldsOfArguments() {
    // Given
    ASTMACompilationUnit ast = compile(
      "component FieldComp1(java.awt.Point q) { component Inner(java.awt.Point p) { constraint(p.x > 0); } Inner sub(q); }");
    assertThat(Log.getFindings()).as("findings of the compilation").isEmpty();

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).noneMatch(Finding::isError);
    assertThat(Log.getFindings()).filteredOn(Finding::isWarning)
      .extracting(Finding::getMsg)
      .singleElement()
      .matches(msg -> msg.startsWith(SUBCOMPONENT_CONSTRAINTS_NOT_IMPLIED.getErrorCode()));
  }
}

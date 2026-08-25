/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTComponentInstanceCoCo;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._cocos.ConfigurationParameterAssignment;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConfigurationParameterAssignment}.
 */
public class ConfigurationParameterAssignmentTest extends MontiArcTestBase {

  @BeforeEach
  public void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected static void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B(int p) { }");
    compile("package a.b; component C(java.lang.String p) { }");
    compile("package a.b; component D(int p1, int p2) { }");
    compile("package a.b; component E(int p = 1) { }");
    compile("package a.b; component F(java.lang.String p = java.lang.String.String()) { }");
    compile("package a.b; component G(java.lang.String p1 = java.lang.String.String(), int p2 = 1) { }");
    compile("package a.b; component H(int p1, int p2, int p3) { }");
    compile("package a.b; component I(int p1, int p2, int p3, int p4 = 1, int p5 = 1, int p6 = 1) { }");
    compile("package a.b; component J(int p1 = 1, int p2 = 1, int p3 = 1) { }");
    compile("package a.b; component K(java.util.List<java.lang.Integer> p) { }");
    compile("package a.b; component L(java.util.List<java.lang.Integer> p = java.util.Collections.emptyList()) { }");
    compile("package a.b; component M<T>(T p) { }");
    compile("package a.b; component N<T>(java.util.List<T> p) { }");
    compile("package a.b; component O<U, V>(U p1, V p2) { }");
    compile("package a.b; component P(int p1, boolean p2) { }");
    compile("package a.b; component Q(int p1 = 1, boolean p2 = false) { }");
    compile("package a.b; component R(int p1, boolean p2 = false) { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 {  }",
    "component Comp2 { a.b.A a; }",
    "component Comp3 { a.b.A a1, a2; }",
    "component Comp4 { a.b.B b(1); }",
    "component Comp5 { a.b.B b1(1), b2(1); }",
    "component Comp6 { a.b.B b(java.lang.Integer.Integer(1)); }",
    "component Comp7 { a.b.C c(java.lang.String.String()); }",
    "component Comp8 { a.b.D d(1, 2); }",
    "component Comp9 { a.b.E e; }",
    "component Comp10 { a.b.E e(1); }",
    "component Comp11 { a.b.E e(java.lang.Integer.Integer(1)); }",
    "component Comp12 { a.b.E e(p = 1); }",
    "component Comp13 { a.b.E e(p = java.lang.Integer.Integer(1)); }",
    "component Comp14 { a.b.F f; }",
    "component Comp15 { a.b.F f(java.lang.String.String()); }",
    "component Comp16 { a.b.F f(p = java.lang.String.String()); }",
    "component Comp17 { a.b.G g; }",
    "component Comp18 { a.b.G g(java.lang.String.String()); }",
    "component Comp19 { a.b.G g(java.lang.String.String(), 1); }",
    "component Comp20 { a.b.G g(java.lang.String.String(), java.lang.Integer.Integer(1)); }",
    "component Comp21 { a.b.G g(p1 = java.lang.String.String(), p2 = 1); }",
    "component Comp22 { a.b.G g(p1 = java.lang.String.String(), p2 = java.lang.Integer.Integer(1)); }",
    "component Comp23 { a.b.G g(p2 = java.lang.Integer.Integer(1), p1 = java.lang.String.String()); }",
    "component Comp24 { a.b.G g(java.lang.String.String(), p2 = 1); }",
    "component Comp25 { a.b.G g(java.lang.String.String(), p2 = java.lang.Integer.Integer(1)); }",
    "component Comp26 { a.b.G g(p1 = java.lang.String.String()); }",
    "component Comp27 { a.b.G g(p2 = 1); }",
    "component Comp28 { a.b.G g(p2 = java.lang.Integer.Integer(1)); }",
    "component Comp29 { a.b.H h(1, 2, 3); }",
    "component Comp30 { a.b.I i(1, 2, 3); }",
    "component Comp31 { a.b.I i(1, 2, 3, 4); }",
    "component Comp32 { a.b.I i(1, 2, 3, 4, 5); }",
    "component Comp33 { a.b.I i(1, 2, 3, 4, 5, 6); }",
    "component Comp34 { a.b.I i(1, 2, 3, 4, 5, p6 = 6); }",
    "component Comp35 { a.b.I i(1, 2, 3, 4, p5 = 5, p6 = 6); }",
    "component Comp36 { a.b.I i(p1 = 1, p2 = 2, p3 = 3, p4 = 4, p5 = 5, p6 = 6); }",
    "component Comp37 { a.b.I i(p6 = 1, p5 = 2, p4 = 3, p3 = 4, p2 = 5, p1 = 6); }",
    "component Comp38 { a.b.J j; }",
    "component Comp39 { a.b.J j(1); }",
    "component Comp40 { a.b.J j(1, 2); }",
    "component Comp41 { a.b.J j(1, 2, 3); }",
    "component Comp42 { a.b.J j(1, 2, p3 = 3); }",
    "component Comp43 { a.b.J j(1, p2 = 2, p3 = 3); }",
    "component Comp44 { a.b.J j(p1 = 1, p2 = 2, p3 = 3); }",
    "component Comp45 { a.b.J j(p3 = 1, p2 = 2, p1 = 3); }",
    "component Comp46 { a.b.K k(java.util.Arrays.asList(1)); }",
    "component Comp47 { a.b.L l; }",
    "component Comp48 { a.b.L l(java.util.Arrays.asList(1)); }",
    "component Comp49 { a.b.L l(p = java.util.Arrays.asList(1)); }",
    "component Comp50 { a.b.M<java.lang.Integer> m(1); }",
    "component Comp51 { a.b.M<java.lang.Integer> m(java.lang.Integer.Integer(1)); }",
    "component Comp52 { a.b.N<java.lang.Integer> m(java.util.Arrays.asList(1)); }",
    "component Comp53 { a.b.N<java.lang.Integer> m(java.util.Collections.emptyList()); }"
  })
  public void shouldNotReportErrorSubComponent(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no heritage
    "component Comp1 { }",
    // heritage no parameter
    "component Comp2 extends a.b.A { }",
    // heritage add parameter
    "component Comp3(int p) extends a.b.A { }",
    // heritage with one mandatory parameter (assign value)
    "component Comp4 extends a.b.B(1) { }",
    // heritage with one mandatory parameter (pass parameter, different name)
    "component Comp5(int q) extends a.b.B(q) { }",
    // heritage with one mandatory parameter (pass parameter, same name)
    "component Comp6(int p) extends a.b.B(p) { }",
    // heritage with one mandatory parameter (assign value via key)
    "component Comp7 extends a.b.B(p = 1) { }",
    // heritage with one mandatory parameter (pass parameter via key, different name)
    "component Comp8(int q) extends a.b.B(p = q) { }",
    // heritage with one mandatory parameter (pass parameter via key, same name)
    "component Comp9(int p) extends a.b.B(p = p) { }",
    // heritage with one optional parameter (default value)
    "component Comp10 extends a.b.E { }",
    // heritage with one optional parameter (assign value)
    "component Comp11 extends a.b.E(1) { }",
    // heritage with one optional parameter (pass parameter, different name)
    "component Comp12(int q) extends a.b.E(q) { }",
    // heritage with one optional parameter (pass parameter, same nape)
    "component Comp13(int p) extends a.b.E(p) { }",
    // heritage with one optional parameter (assign value via key)
    "component Comp14 extends a.b.E(p = 1) { }",
    // heritage with one optional parameter (pass parameter via key, different name)
    "component Comp15(int q) extends a.b.E(p = q) { }",
    // heritage with one optional parameter (pass parameter via key, same name)
    "component Comp16(int p) extends a.b.E(p = p) { }",
    // heritage with one mandatory and one optional parameter (default value)
    "component Comp17 extends a.b.R(1) { }",
    // heritage with one mandatory and one optional parameter (assign values)
    "component Comp18 extends a.b.R(1, false) { }",
    // heritage with one mandatory and one optional parameter (pass parameter, default value)
    "component Comp19(int p) extends a.b.R(p) { }",
    // heritage with one mandatory and one optional parameter (pass parameters)
    "component Comp20(int p1, boolean p2) extends a.b.R(p1, p2) { }",
    // heritage with one mandatory and one optional parameter (assign values via key in order)
    "component Comp21 extends a.b.R(p1 = 1, p2 = false) { }",
    // heritage with one mandatory and one optional parameter (assign values via key reverse order)
    "component Comp22 extends a.b.R(p2 = false, p1 = 1) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key, default value)
    "component Comp23(int p) extends a.b.R(p1 = p) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key in order)
    "component Comp24(int p1, boolean p2) extends a.b.R(p1 = p1, p2 = p2) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key reverse order)
    "component Comp25(int p1, boolean p2) extends a.b.R(p2 = p2, p1 = p1) { }",
    // heritage with two optional parameters (default values)
    "component Comp26 extends a.b.Q { }",
    // heritage with two optional parameters (assign single value)
    "component Comp27 extends a.b.Q(1) { }",
    // heritage with two optional parameters (assign values)
    "component Comp28 extends a.b.Q(1, false) { }",
    // heritage with two optional parameters (pass single parameter)
    "component Comp29(int p) extends a.b.Q(p) { }",
    // heritage with two optional parameters (pass parameters)
    "component Comp30(int p1, boolean p2) extends a.b.Q(p1, p2) { }",
    // heritage with two optional parameters (assign first value via key)
    "component Comp31 extends a.b.Q(p1 = 1) { }",
    // heritage with two optional parameters (assign second value via key)
    "component Comp32 extends a.b.Q(p2 = false) { }",
    // heritage with two optional parameters (assign values via key in order)
    "component Comp33 extends a.b.Q(p1 = 1, p2 = false) { }",
    // heritage with two optional parameters (assign values via key reverse order)
    "component Comp34 extends a.b.Q(p2 = false, p1 = 1) { }",
    // heritage with two optional parameters (pass first parameter via key)
    "component Comp35(int p) extends a.b.Q(p1 = p) { }",
    // heritage with two optional parameters (pass second parameter via key)
    "component Comp36(boolean p) extends a.b.Q(p2 = p) { }",
    // heritage with two optional parameters (pass parameters via key in order)
    "component Comp37(int p1, boolean p2) extends a.b.Q(p1 = p1, p2 = p2) { }",
    // heritage with two optional parameters (pass parameters via key reverse order)
    "component Comp38(int p1, boolean p2) extends a.b.Q(p2 = p2, p1 = p1) { }",
    // heritage with two mandatory parameters (assign values)
    "component Comp39 extends a.b.P(1, false) { }",
    // heritage with two mandatory parameters (pass parameters)
    "component Comp40(int p1, boolean p2) extends a.b.P(p1, p2) { }",
    // heritage with two mandatory parameters (assign values via key in order)
    "component Comp41 extends a.b.P(p1 = 1, p2 = false) { }",
    // heritage with two mandatory parameters (assign values via key reverse order)
    "component Comp42 extends a.b.P(p2 = false, p1 = 1) { }",
    // heritage with two mandatory parameters (pass parameters via key in order)
    "component Comp43(int p1, boolean p2) extends a.b.P(p1 = p1, p2 = p2) { }",
    // heritage with two mandatory parameters (pass parameters via key reverse order)
    "component Comp44(int p1, boolean p2) extends a.b.P(p2 = p2, p1 = p1) { }",
    // heritage with one mandatory generic parameter (bind type, assign value)
    "component Comp45 extends a.b.M<int>(1) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter)
    "component Comp46(int p) extends a.b.M<int>(p) { }",
    // heritage with one mandatory generic parameter (pass type, pass parameter)
    "component Comp47<T>(T p) extends a.b.M<T>(p) { }",
    // heritage with one mandatory generic parameter (bind type, assign value via key)
    "component Comp48 extends a.b.M<int>(p = 1) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter via key, different name)
    "component Comp49(int q) extends a.b.M<int>(p = q) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter via key, same name)
    "component Comp50(int p) extends a.b.M<int>(p = p) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, different name, same type name)
    "component Comp51<T>(T q) extends a.b.M<T>(p = q) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, same name, same type name)
    "component Comp52<T>(T p) extends a.b.M<T>(p = p) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, different name, different type name)
    "component Comp53<S>(S q) extends a.b.M<S>(p = q) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, same name, different type name)
    "component Comp54<S>(S p) extends a.b.M<S>(p = p) { }",
    // heritage with two mandatory generic parameters (bind type, assign values)
    "component Comp55 extends a.b.O<int, boolean>(1, false) { }",
    // heritage with two mandatory generic parameters (bind type, pass parameters)
    "component Comp56(int p1, boolean p2) extends a.b.O<int, boolean>(p1, p2) { }",
    // heritage with two mandatory generic parameters (pass type, pass parameters)
    "component Comp57<U, V>(U p1, V p2) extends a.b.O<U, V>(p1, p2) { }",
    // heritage with one mandatory generic parameter (bind type, assign value via key) and with two mandatory generic parameters (pass type, pass parameters)
    "component Comp58<U, V>(U p1, V p2) extends a.b.M<int>(p = 1), a.b.O<U, V>(p1, p2) { }"
  })
  public void shouldNotReportErrorHead(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModelsSubComponent")
  public void shouldReportErrorSubComponents(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsSubComponent() {
    return Stream.of(
      arg("component Comp1 { a.b.A a(1); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp2 { a.b.A a(1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp3 { a.b.A a(p = 1); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp4 { a.b.B b;} ",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp5 { a.b.B b(1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp6 { a.b.B b(np = 1); }",
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp7 { a.b.B b(1, p = 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      arg("component Comp8 { a.b.B b(p = 1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      arg("component Comp9 { a.b.B b(1, np = 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp10 { a.b.B b(np = 1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      arg("component Comp11 { a.b.B b(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp12 { a.b.B b(true, 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp13 { a.b.C c(1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      /*arg("component Comp14 { a.b.C c(java.lang.Integer.Integer(1)); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),*/
      arg("component Comp15 { a.b.D d(true, 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp16 { a.b.D d(1, false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp17 { a.b.D d(true, false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp18 { a.b.D d(p2 = 1, false); }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      arg("component Comp19 { a.b.D d(1, p1=2); }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      arg("component Comp20 { a.b.D d(p2 = 1, p2 = 2); }",
        ArcError.KEY_NOT_UNIQUE),
      arg("component Comp21 { a.b.D d(true, p2 = 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp22 { a.b.D d(1, p2 = false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp23 { a.b.E e(1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp24 { a.b.E e(p = 1, p = 2); }",
        ArcError.TOO_MANY_ARGUMENTS, ArcError.KEY_NOT_UNIQUE),
      arg("component Comp25 { a.b.E e(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp26 { a.b.E e(p = true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp27 { a.b.E e(np = 1); }",
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp28 { a.b.F f(1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      /*arg("component Comp29 { a.b.G g(p2 = java.lang.String.String(), p1 = 1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),*/
      arg("component Comp30 { a.b.H h; }",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp31 { a.b.H h(1); }",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp32 { a.b.H h(1, 2); }",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp33 { a.b.I i(1, 2, p4 = 3, p4 = 4, p5 = 5, p6 = 6); }",
        ArcError.TOO_FEW_ARGUMENTS, ArcError.KEY_NOT_UNIQUE),
      arg("component Comp34 { a.b.I i(1, p2 = 2, 3, 4, 5, 6); }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      arg("component Comp35 { a.b.M<java.lang.Integer> m(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp36 { a.b.M<java.lang.String> m(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp37 { a.b.M<java.lang.Integer> m(p = true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH)
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelsHead")
  public void shouldReportErrorHead(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsHead() {
    return Stream.of(
      // one argument too many, no super parameter
      arg("component Comp1 extends a.b.A(1) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // missing single mandatory argument
      arg("component Comp2 extends a.b.B { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch
      arg("component Comp3 extends a.b.B(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch (key assign)
      arg("component Comp4 extends a.b.B(p = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one argument too many (one mandatory parameter)
      arg("component Comp5 extends a.b.B(1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // one argument too many (one mandatory parameter, key assign first)
      arg("component Comp6 extends a.b.B(p = 1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one argument too many (one mandatory parameter, key assign second)
      arg("component Comp7 extends a.b.B(1, p = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.TOO_MANY_ARGUMENTS),
      // one argument too many (one mandatory parameter, repeated key)
      arg("component Comp8 extends a.b.B(p = 1, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter)
      arg("component Comp9 extends a.b.B(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign first)
      arg("component Comp10 extends a.b.B(p = true, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign second)
      arg("component Comp11 extends a.b.B(true, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, repeated key)
      arg("component Comp12 extends a.b.B(p = true, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch
      arg("component Comp13 extends a.b.E(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch (key assign)
      arg("component Comp14 extends a.b.E(p = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one argument too many (one optional parameter)
      arg("component Comp15 extends a.b.E(1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // one argument too many (one optional parameter, key assign first)
      arg("component Comp16 extends a.b.E(p = 1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one argument too many (one optional parameter, key assign second)
      arg("component Comp17 extends a.b.E(1, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // one argument too many (one optional parameter, repeated key)
      arg("component Comp18 extends a.b.E(p = 1, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.KEY_NOT_UNIQUE),
      // one optional argument type mismatch and one argument too many
      arg("component Comp19 extends a.b.E(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign first)
      arg("component Comp20 extends a.b.E(p = true, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign second)
      arg("component Comp21 extends a.b.E(true, p = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one argument too many (one optional parameter, repeated key)
      arg("component Comp22 extends a.b.E(p = true, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // two arguments too many (one optional parameter)
      arg("component Comp23 extends a.b.E(1, 2, 3) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // missing one mandatory argument (one mandatory and one optional parameter)
      arg("component Comp24 extends a.b.R { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (one mandatory and one optional parameter)
      arg("component Comp25 extends a.b.R(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch (one mandatory and one optional parameter, key assign first)
      arg("component Comp26 extends a.b.R(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument missing (one mandatory and one optional parameter, key assign second)
      arg("component Comp27 extends a.b.R(p2 = true) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument missing and one optional argument type mismatch
      // (one mandatory and one optional parameter, key assign second)
      arg("component Comp28 extends a.b.R(p2 = 1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch
      arg("component Comp29 extends a.b.R(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one optional argument type mismatch
      arg("component Comp30 extends a.b.R(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory and one optional argument type mismatch
      arg("component Comp31 extends a.b.R(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign first)
      arg("component Comp32 extends a.b.R(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one optional argument (key assign second)
      arg("component Comp33 extends a.b.R(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign both)
      arg("component Comp34 extends a.b.R(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp35 extends a.b.R(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp36 extends a.b.R(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp37 extends a.b.R(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp38 extends a.b.R(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one optional argument (repeated key)
      arg("component Comp39 extends a.b.R(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // one mandatory argument and one optional argument (arg assigned twice, key assign first twice)
      arg("component Comp40 extends a.b.R(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // one mandatory argument and one optional argument (arg assigned twice, key assign second twice)
      arg("component Comp41 extends a.b.R(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument and one optional argument (arg assigned twice, repeated first key)
      arg("component Comp42 extends a.b.R(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // one mandatory argument and one optional argument (arg assigned twice, repeated second key)
      arg("component Comp43 extends a.b.R(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_FEW_ARGUMENTS),
      // one optional argument type mismatch (two optional parameters)
      arg("component Comp44 extends a.b.Q(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch (two optional parameters, key assign first)
      arg("component Comp45 extends a.b.Q(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument
      arg("component Comp46 extends a.b.Q(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument and one optional argument type mismatch
      arg("component Comp47 extends a.b.Q(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two optional argument type mismatch
      arg("component Comp48 extends a.b.Q(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign first)
      arg("component Comp49 extends a.b.Q(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one optional argument type mismatch and one optional argument (key assign second)
      arg("component Comp50 extends a.b.Q(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign both)
      arg("component Comp51 extends a.b.Q(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp52 extends a.b.Q(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp53 extends a.b.Q(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one optional argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp54 extends a.b.Q(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp55 extends a.b.Q(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two optional arguments (arg assigned twice, key assign first)
      arg("component Comp56 extends a.b.Q(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // two optional arguments (arg assigned twice, key assign second)
      arg("component Comp57 extends a.b.Q(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // two optional arguments (repeated first key)
      arg("component Comp58 extends a.b.Q(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // two optional arguments (repeated second key)
      arg("component Comp59 extends a.b.Q(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // missing two mandatory arguments (two mandatory parameters)
      arg("component Comp60 extends a.b.P { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters)
      arg("component Comp61 extends a.b.P(1) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters, key assign first)
      arg("component Comp62 extends a.b.P(p1 = 1) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters, key assign second)
      arg("component Comp63 extends a.b.P(p2 = true) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters)
      arg("component Comp64 extends a.b.P(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters, key assign first)
      arg("component Comp65 extends a.b.P(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters, key assign second)
      arg("component Comp66 extends a.b.P(p2 = 1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch and one mandatory argument
      arg("component Comp67 extends a.b.P(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one mandatory argument type mismatch
      arg("component Comp68 extends a.b.P(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two mandatory argument type mismatch
      arg("component Comp69 extends a.b.P(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign first)
      arg("component Comp70 extends a.b.P(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one mandatory argument (key assign second)
      arg("component Comp71 extends a.b.P(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign both)
      arg("component Comp72 extends a.b.P(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign first twice)
      arg("component Comp73 extends a.b.P(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign second twice)
      arg("component Comp74 extends a.b.P(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one mandatory argument (key assign both in order)
      arg("component Comp75 extends a.b.P(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign both reverse order)
      arg("component Comp76 extends a.b.P(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments (arg assigned twice, key assign first)
      arg("component Comp77 extends a.b.P(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // two mandatory arguments (arg assigned twice, key assign second)
      arg("component Comp78 extends a.b.P(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // two mandatory arguments (repeated first key)
      arg("component Comp79 extends a.b.P(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // two mandatory arguments (repeated second key)
      arg("component Comp80 extends a.b.P(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // one mandatory argument generic type mismatch
      arg("component Comp81 extends a.b.M<int>(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter)
      arg("component Comp82(boolean q) extends a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter)
      arg("component Comp83<S>(S q) extends a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, same name)
      arg("component Comp84(boolean p) extends a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name)
      arg("component Comp85<S>(S p) extends a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same type name)
      arg("component Comp86<T>(T q) extends a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name)
      arg("component Comp87<T>(T p) extends a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, key assign)
      arg("component Comp88(boolean q) extends a.b.M<int>(p = q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, key assign)
      arg("component Comp89<S>(S q) extends a.b.M<int>(p = q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, same name, key assign)
      arg("component Comp90(boolean p) extends a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, key assign)
      arg("component Comp91<S>(S p) extends a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same type name, key assign)
      arg("component Comp92<T>(T q) extends a.b.M<int>(q = p) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name, key assign)
      arg("component Comp93<T>(T p) extends a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch
      arg("component Comp94 extends a.b.O<int, boolean>(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch
      arg("component Comp95 extends a.b.O<int, boolean>(1 ,2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch
      arg("component Comp96 extends a.b.O<int, boolean>(true ,2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp97(boolean q1, boolean q2) extends a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp98(int q1, int q2) extends a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp99(boolean q1, int q2) extends a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp100<S, T>(S q1, T q2) extends a.b.O<T, T>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp101<S, T>(S q1, T q2) extends a.b.O<S, S>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp102<S, T>(S q1, T q2) extends a.b.O<T, S>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // heritage with two mandatory parameters (assign first key to first argument, second key invalid)
      arg("component Comp103 extends a.b.P(p1 = 4, p4 = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // heritage with two mandatory parameters (assign first key to second argument, second key invalid)
      arg("component Comp104 extends a.b.P(p2 = false, p4 = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // two mandatory arguments (arg assigned twice, key assign second) and two mandatory arguments generic type mismatch
      arg("component Comp105 extends a.b.P(p2 = 1, 2), a.b.O<int, boolean>(true ,2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH)
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // No refinement
    "component Comp1 {  }",
    // Refinement no parameter
    "component Comp2 refines a.b.A  { }",
    // MultipleRefinements, no parameter
    "component Comp3 refines a.b.A, a.b.A { }",
    // Refinement with one mandatory parameter (assign value)
    "component Comp4 refines a.b.B (1) { }",
    // Refinement with a boxed version of the parameter's primitive type
    "component Comp5 refines a.b.B (java.lang.Integer.Integer(1)) { }",
    // Refinement with one mandatory parameter (pass parameter, different name)
    "component Comp6(int q) refines a.b.B(q) { }",
    // Refinement with one mandatory parameter (pass parameter, same name)
    "component Comp7(int p) refines a.b.B(p) { }",
    // Refinement with one mandatory parameter (assign value via key)
    "component Comp8 refines a.b.B(p = 1) { }",
    // Refinement with one mandatory parameter (pass parameter via key, different name)
    "component Comp9(int q) refines a.b.B(p = q) { }",
    // Refinement with one mandatory parameter (pass parameter via key, same name)
    "component Comp10(int p) refines a.b.B(p = p) { }",
    // Refinement with a constructor call as the argument
    "component Comp11 refines a.b.C (java.lang.String.String()) { }",
    // Assigning both mandatory params
    "component Comp12 refines a.b.D (1, 2) { }",
    // Refinement with one optional parameter (default value)
    "component Comp13 refines a.b.E { }",
    // Refinement with one optional parameter (assign value)
    "component Comp14 refines a.b.E (1) { }",
    // Refinement with a boxed version of the parameter's primitive type
    "component Comp15 refines a.b.E (java.lang.Integer.Integer(1)) { }",
    // Refinement with one optional parameter (assign value via key)
    "component Comp16 refines a.b.E (p = 1) { }",
    // Refinement with a boxed version of the parameter's primitive type, assigned by key
    "component Comp17 refines a.b.E (p = java.lang.Integer.Integer(1)) { }",
    // Refinement with one optional parameter (pass parameter, different name)
    "component Comp18(int q) refines a.b.E(q) { }",
    // Refinement with one optional parameter (pass parameter, same nape)
    "component Comp19(int p) refines a.b.E(p) { }",
    // Refinement with one optional parameter (pass parameter via key, different name)
    "component Comp20(int q) refines a.b.E(p = q) { }",
    // Refinement with one optional parameter (pass parameter via key, same name)
    "component Comp21(int p) refines a.b.E(p = p) { }",
    // Refinement with one optional parameter (default value)
    "component Comp22 refines a.b.F { }",
    // Refinement with one optional parameter (assign value with constructor call)
    "component Comp23 refines a.b.F (java.lang.String.String()) { }",
    // Refinement with one optional parameter (assign value by key with constructor call)
    "component Comp24 refines a.b.F (p = java.lang.String.String()) { }",
    // Refinement with two optional parameters (default values)
    "component Comp25 refines a.b.G { }",
    // Refinement with two optional parameters (1. assign value, 2. default value)
    "component Comp26 refines a.b.G (java.lang.String.String()) { }",
    // Refinement with two optional parameters (assigned values)
    "component Comp27 refines a.b.G (java.lang.String.String(), 1) { }",
    // Refinement with two optional parameters (assigned values, with boxed value for primitive parameter type)
    "component Comp28 refines a.b.G (java.lang.String.String(), java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (assign by key)
    "component Comp29 refines a.b.G (p1 = java.lang.String.String(), p2 = 1) { }",
    // Refinement with two optional parameters (assign by key, with boxed value for primitive parameter type)
    "component Comp30 refines a.b.G (p1 = java.lang.String.String(), p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (assign by key, in changed order, with boxed value for primitive parameter type)
    "component Comp31 refines a.b.G (p2 = java.lang.Integer.Integer(1), p1 = java.lang.String.String()) { }",
    // Refinement with two optional parameters (1. assign value, 2. assign by key)
    "component Comp32 refines a.b.G (java.lang.String.String(), p2 = 1) { }",
    // Refinement with two optional parameters (1. assign value, 2. assign by key and with boxed value)
    "component Comp33 refines a.b.G (java.lang.String.String(), p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (1. assign by key, omit 2nd)
    "component Comp34 refines a.b.G (p1 = java.lang.String.String()) { }",
    // Refinement with two optional parameters (omit first, assign 2nd)
    "component Comp35 refines a.b.G (p2 = 1) { }",
    // Refinement with two optional parameters (omit first, assign 2nd with boxed value)
    "component Comp36 refines a.b.G (p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with three mandatory parameters
    "component Comp37 refines a.b.H (1, 2, 3) { }",
    // Refinement with three mandatory and three optional parameters. Assign first three params by position
    "component Comp38 refines a.b.I (1, 2, 3) { }",
    // Refinement with three mandatory and three optional parameters. Assign first four params by position
    "component Comp39 refines a.b.I (1, 2, 3, 4) { }",
    // Refinement with three mandatory and three optional parameters. Assign first five params by position
    "component Comp40 refines a.b.I (1, 2, 3, 4, 5) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by position
    "component Comp41 refines a.b.I (1, 2, 3, 4, 5, 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign first five params by position, last by key
    "component Comp42 refines a.b.I (1, 2, 3, 4, 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign first 4 params by position, last two by key
    "component Comp43 refines a.b.I (1, 2, 3, 4, p5 = 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by key
    "component Comp44 refines a.b.I (p1 = 1, p2 = 2, p3 = 3, p4 = 4, p5 = 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by key in changed order
    "component Comp45 refines a.b.I (p6 = 1, p5 = 2, p4 = 3, p3 = 4, p2 = 5, p1 = 6) { }",
    // Refinement with three optional parameters (omit args)
    "component Comp46 refines a.b.J { }",
    // Refinement with three optional parameters. Assign first arg
    "component Comp47 refines a.b.J (1) { }",
    // Refinement with three optional parameters. Assign first two args
    "component Comp48 refines a.b.J (1, 2) { }",
    // Refinement with three optional parameters. Assign all args
    "component Comp49 refines a.b.J (1, 2, 3) { }",
    // Refinement with three optional parameters. Assign first two args by value, last by key
    "component Comp50 refines a.b.J (1, 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign first arg by value, last two by key
    "component Comp51 refines a.b.J (1, p2 = 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign all args by key
    "component Comp52 refines a.b.J (p1 = 1, p2 = 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign all args by key in changed order
    "component Comp53 refines a.b.J (p3 = 1, p2 = 2, p1 = 3) { }",
    // Refinement with optional list parameter (omit arg)
    "component Comp54 refines a.b.L { }",
    // Refinement with one mandatory generic parameter (bind type, assign primitive value)
    "component Comp55 refines a.b.M<java.lang.Integer>(1) { }",
    // Refinement with one mandatory generic parameter (bind type, assign boxed value)
    "component Comp56 refines a.b.M<java.lang.Integer>(java.lang.Integer.Integer(1)) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value)
    "component Comp57 refines a.b.M<int>(1) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter)
    "component Comp58(int p) refines a.b.M<int>(p) { }",
    // Refinement with one mandatory generic parameter (pass type, pass parameter)
    "component Comp59<T>(T p) refines a.b.M<T>(p) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value via key)
    "component Comp60 refines a.b.M<int>(p = 1) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter via key, different name)
    "component Comp61(int q) refines a.b.M<int>(p = q) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter via key, same name)
    "component Comp62(int p) refines a.b.M<int>(p = p) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, different name, same type name)
    "component Comp63<T>(T q) refines a.b.M<T>(p = q) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, same name, same type name)
    "component Comp64<T>(T p) refines a.b.M<T>(p = p) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, different name, different type name)
    "component Comp65<S>(S q) refines a.b.M<S>(p = q) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, same name, different type name)
    "component Comp66<S>(S p) refines a.b.M<S>(p = p) { }",
    // Refinement with two mandatory generic parameters (bind type, assign values)
    "component Comp67 refines a.b.O<int, boolean>(1, false) { }",
    // Refinement with two mandatory generic parameters (bind type, pass parameters)
    "component Comp68(int p1, boolean p2) refines a.b.O<int, boolean>(p1, p2) { }",
    // Refinement with two mandatory generic parameters (pass type, pass parameters)
    "component Comp69<U, V>(U p1, V p2) refines a.b.O<U, V>(p1, p2) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value via key) and with two mandatory generic parameters (pass type, pass parameters)
    "component Comp70<U, V>(U p1, V p2) refines a.b.M<int>(p = 1), a.b.O<U, V>(p1, p2) { }",
    // Refinement with one mandatory and one optional parameter (default value)
    "component Comp71 refines a.b.R(1) { }",
    // Refinement with one mandatory and one optional parameter (assign values)
    "component Comp72 refines a.b.R(1, false) { }",
    // Refinement with one mandatory and one optional parameter (pass parameter, default value)
    "component Comp73(int p) refines a.b.R(p) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters)
    "component Comp74(int p1, boolean p2) refines a.b.R(p1, p2) { }",
    // Refinement with one mandatory and one optional parameter (assign values via key in order)
    "component Comp75 refines a.b.R(p1 = 1, p2 = false) { }",
    // Refinement with one mandatory and one optional parameter (assign values via key reverse order)
    "component Comp76 refines a.b.R(p2 = false, p1 = 1) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key, default value)
    "component Comp77(int p) refines a.b.R(p1 = p) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key in order)
    "component Comp78(int p1, boolean p2) refines a.b.R(p1 = p1, p2 = p2) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key reverse order)
    "component Comp79(int p1, boolean p2) refines a.b.R(p2 = p2, p1 = p1) { }",
    // Refinement with two optional parameters (default values)
    "component Comp80 refines a.b.Q { }",
    // Refinement with two optional parameters (assign single value)
    "component Comp81 refines a.b.Q(1) { }",
    // Refinement with two optional parameters (assign values)
    "component Comp82 refines a.b.Q(1, false) { }",
    // Refinement with two optional parameters (pass single parameter)
    "component Comp83(int p) refines a.b.Q(p) { }",
    // Refinement with two optional parameters (pass parameters)
    "component Comp84(int p1, boolean p2) refines a.b.Q(p1, p2) { }",
    // Refinement with two optional parameters (assign first value via key)
    "component Comp85 refines a.b.Q(p1 = 1) { }",
    // Refinement with two optional parameters (assign second value via key)
    "component Comp86 refines a.b.Q(p2 = false) { }",
    // Refinement with two optional parameters (assign values via key in order)
    "component Comp87 refines a.b.Q(p1 = 1, p2 = false) { }",
    // Refinement with two optional parameters (assign values via key reverse order)
    "component Comp88 refines a.b.Q(p2 = false, p1 = 1) { }",
    // Refinement with two optional parameters (pass first parameter via key)
    "component Comp89(int p) refines a.b.Q(p1 = p) { }",
    // Refinement with two optional parameters (pass second parameter via key)
    "component Comp90(boolean p) refines a.b.Q(p2 = p) { }",
    // Refinement with two optional parameters (pass parameters via key in order)
    "component Comp91(int p1, boolean p2) refines a.b.Q(p1 = p1, p2 = p2) { }",
    // Refinement with two optional parameters (pass parameters via key reverse order)
    "component Comp92(int p1, boolean p2) refines a.b.Q(p2 = p2, p1 = p1) { }",
    // Refinement with two mandatory parameters (assign values)
    "component Comp93 refines a.b.P(1, false) { }",
    // Refinement with two mandatory parameters (pass parameters)
    "component Comp94(int p1, boolean p2) refines a.b.P(p1, p2) { }",
    // Refinement with two mandatory parameters (assign values via key in order)
    "component Comp95 refines a.b.P(p1 = 1, p2 = false) { }",
    // Refinement with two mandatory parameters (assign values via key reverse order)
    "component Comp96 refines a.b.P(p2 = false, p1 = 1) { }",
    // Refinement with two mandatory parameters (pass parameters via key in order)
    "component Comp97(int p1, boolean p2) refines a.b.P(p1 = p1, p2 = p2) { }",
    // Refinement with two mandatory parameters (pass parameters via key reverse order)
    "component Comp98(int p1, boolean p2) refines a.b.P(p2 = p2, p1 = p1) { }",

  })
  void shouldNotReportRefinementError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidRefinementModels")
  void shouldReportRefinementError(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTArcComponentTypeCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidRefinementModels() {
    return Stream.of(
      // One argument too many, no abstraction parameter
      arg("component Comp1 refines a.b.A(1) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // Two arguments too many
      arg("component Comp2 refines a.b.A(1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // One argument too many, with non-existing key
      arg("component Comp3 refines a.b.A(p = 1) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID),
      // Missing single mandatory argument
      arg("component Comp4 refines a.b.B { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch
      arg("component Comp5 refines a.b.B(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch (key assign)
      arg("component Comp6 refines a.b.B(p = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One argument too many (one mandatory parameter)
      arg("component Comp7 refines a.b.B(1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // One argument too many (one mandatory parameter, key assign first)
      arg("component Comp8 refines a.b.B(p = 1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One argument too many (one mandatory parameter, key assign second)
      arg("component Comp9 refines a.b.B(1, p = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.TOO_MANY_ARGUMENTS),
      // One argument too many (one mandatory parameter, repeated key)
      arg("component Comp10 refines a.b.B(p = 1, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter)
      arg("component Comp11 refines a.b.B(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign first)
      arg("component Comp12 refines a.b.B(p = true, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.TOO_MANY_ARGUMENTS),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign second)
      arg("component Comp13 refines a.b.B(true, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, repeated key)
      arg("component Comp14 refines a.b.B(p = true, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // Parameter assignment with a wrong key
      arg("component Comp15 refines a.b.B(np = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // One argument too many, wrong key
      arg("component Comp16 refines a.b.B(1, np = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID),
      // One argument too many, wrong key, key-first assign
      arg("component Comp17 refines a.b.B(np = 1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // Wrong type
      arg("component Comp18 refines a.b.C(1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // First arg has wrong type
      arg("component Comp19 refines a.b.D(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Second arg has wrong type
      arg("component Comp20 refines a.b.D(1, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Both args have wrong type
      arg("component Comp21 refines a.b.D(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Second arg has wrong type, key-first assign
      arg("component Comp22 refines a.b.D(p2 = 1, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // First arg is assigned via position, then again via key
      arg("component Comp23 refines a.b.D(1, p1=2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // Second arg is assigned twice via key
      arg("component Comp24 refines a.b.D(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // First arg has wrong type, second arg is correctly assigned by key
      arg("component Comp25 refines a.b.D(true, p2 = 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // First arg is correctly assigned, second arg has wrong type
      arg("component Comp26 refines a.b.D(1, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Arg key does not exist
      arg("component Comp27 refines a.b.E(np = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // One optional argument type mismatch
      arg("component Comp28 refines a.b.E(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch (key assign)
      arg("component Comp29 refines a.b.E(p = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One argument too many (one optional parameter)
      arg("component Comp30 refines a.b.E(1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // One argument too many (one optional parameter, key assign first)
      arg("component Comp31 refines a.b.E(p = 1, 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One argument too many (one optional parameter, key assign second)
      arg("component Comp32 refines a.b.E(1, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // One argument too many (one optional parameter, repeated key)
      arg("component Comp33 refines a.b.E(p = 1, p = 2) { }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.KEY_NOT_UNIQUE),
      // One optional argument type mismatch and one argument too many
      arg("component Comp34 refines a.b.E(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      // One optional argument type mismatch and one argument too many (one optional parameter, key assign first)
      arg("component Comp35 refines a.b.E(p = true, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.TOO_MANY_ARGUMENTS),
      // One optional argument type mismatch and one argument too many (one optional parameter, key assign second)
      arg("component Comp36 refines a.b.E(true, p = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one argument too many (one optional parameter, repeated key)
      arg("component Comp37 refines a.b.E(p = true, p = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_MANY_ARGUMENTS),
      // Two arguments too many (one optional parameter)
      arg("component Comp38 refines a.b.E(1, 2, 3) { }",
        ArcError.TOO_MANY_ARGUMENTS),
      // Wrong arg type for optional parameter
      arg("component Comp39 refines a.b.F(1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Not even parantheses, although three params are mandatory
      arg("component Comp40 refines a.b.H h { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // One argument for three mandatory params
      arg("component Comp41 refines a.b.H(1) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // Two arguments for three mandatory params
      arg("component Comp42 refines a.b.H(1, 2) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // Six args for six parameters, but two args are for the same parameter (assignment by key)
      arg("component Comp43 refines a.b.I(1, 2, p4 = 3, p4 = 4, p5 = 5, p6 = 6) { }",
        ArcError.TOO_FEW_ARGUMENTS, ArcError.KEY_NOT_UNIQUE),
      // Position-based argument list is interrupted by key assignment
      arg("component Comp44 refines a.b.I(1, p2 = 2, 3, 4, 5, 6) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // Generic parameter type is bound to int, but argument is boolean
      arg("component Comp45 refines a.b.M<java.lang.Integer>(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Generic parameter type is bound to String, but argument is boolean
      arg("component Comp46 refines a.b.M<java.lang.String>(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Generic parameter type is bound to int, but argument is boolean. Assignment by key.
      arg("component Comp47 refines a.b.M<java.lang.Integer>(p = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch
      arg("component Comp48 refines a.b.M<int>(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass parameter)
      arg("component Comp49(boolean q) refines a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter)
      arg("component Comp50<S>(S q) refines a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass parameter, same name)
      arg("component Comp51(boolean p) refines a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, same name)
      arg("component Comp52<S>(S p) refines a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, same type name)
      arg("component Comp53<T>(T q) refines a.b.M<int>(q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, same type name)
      arg("component Comp54<T>(T p) refines a.b.M<int>(p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass parameter, key assign)
      arg("component Comp55(boolean q) refines a.b.M<int>(p = q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, key assign)
      arg("component Comp56<S>(S q) refines a.b.M<int>(p = q) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass parameter, same name, key assign)
      arg("component Comp57(boolean p) refines a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, key assign)
      arg("component Comp58<S>(S p) refines a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument generic type mismatch (pass generic parameter, same type name, key assign)
      arg("component Comp59<T>(T q) refines a.b.M<int>(q = p) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, same type name, key assign)
      arg("component Comp60<T>(T p) refines a.b.M<int>(p = p) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),

      // Missing one mandatory argument (one mandatory and one optional parameter)
      arg("component Comp61 refines a.b.R { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch (one mandatory and one optional parameter)
      arg("component Comp62 refines a.b.R(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch (one mandatory and one optional parameter, key assign first)
      arg("component Comp63 refines a.b.R(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument missing (one mandatory and one optional parameter, key assign second)
      arg("component Comp64 refines a.b.R(p2 = true) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument missing and one optional argument type mismatch
      // (one mandatory and one optional parameter, key assign second)
      arg("component Comp65 refines a.b.R(p2 = 1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch
      arg("component Comp66 refines a.b.R(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument and one optional argument type mismatch
      arg("component Comp67 refines a.b.R(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory and one optional argument type mismatch
      arg("component Comp68 refines a.b.R(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one optional argument (key assign first)
      arg("component Comp69 refines a.b.R(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One mandatory argument type mismatch and one optional argument (key assign second)
      arg("component Comp70 refines a.b.R(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one optional argument (key assign both)
      arg("component Comp71 refines a.b.R(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp72 refines a.b.R(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp73 refines a.b.R(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One mandatory argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp74 refines a.b.R(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp75 refines a.b.R(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument and one optional argument (repeated key)
      arg("component Comp76 refines a.b.R(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // One mandatory argument and one optional argument (arg assigned twice, key assign first twice)
      arg("component Comp77 refines a.b.R(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // One mandatory argument and one optional argument (arg assigned twice, key assign second twice)
      arg("component Comp78 refines a.b.R(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One mandatory argument and one optional argument (arg assigned twice, repeated first key)
      arg("component Comp79 refines a.b.R(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // One mandatory argument and one optional argument (arg assigned twice, repeated second key)
      arg("component Comp80 refines a.b.R(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE,
        ArcError.TOO_FEW_ARGUMENTS),
      // One optional argument type mismatch (two optional parameters)
      arg("component Comp81 refines a.b.Q(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch (two optional parameters, key assign first)
      arg("component Comp82 refines a.b.Q(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument
      arg("component Comp83 refines a.b.Q(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument and one optional argument type mismatch
      arg("component Comp84 refines a.b.Q(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two optional argument type mismatch
      arg("component Comp85 refines a.b.Q(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument (key assign first)
      arg("component Comp86 refines a.b.Q(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One optional argument type mismatch and one optional argument (key assign second)
      arg("component Comp87 refines a.b.Q(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument (key assign both)
      arg("component Comp88 refines a.b.Q(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp89 refines a.b.Q(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp90 refines a.b.Q(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One optional argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp91 refines a.b.Q(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One optional argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp92 refines a.b.Q(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two optional arguments (arg assigned twice, key assign first)
      arg("component Comp93 refines a.b.Q(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // Two optional arguments (arg assigned twice, key assign second)
      arg("component Comp94 refines a.b.Q(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // Two optional arguments (repeated first key)
      arg("component Comp95 refines a.b.Q(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // Two optional arguments (repeated second key)
      arg("component Comp96 refines a.b.Q(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // Missing two mandatory arguments (two mandatory parameters)
      arg("component Comp97 refines a.b.P { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // Missing one mandatory argument (two mandatory parameters)
      arg("component Comp98 refines a.b.P(1) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // Missing one mandatory argument (two mandatory parameters, key assign first)
      arg("component Comp99 refines a.b.P(p1 = 1) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // Missing one mandatory argument (two mandatory parameters, key assign second)
      arg("component Comp100 refines a.b.P(p2 = true) { }",
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch (two mandatory parameters)
      arg("component Comp101 refines a.b.P(true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch (two mandatory parameters, key assign first)
      arg("component Comp102 refines a.b.P(p1 = true) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch (two mandatory parameters, key assign second)
      arg("component Comp103 refines a.b.P(p2 = 1) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_FEW_ARGUMENTS),
      // One mandatory argument type mismatch and one mandatory argument
      arg("component Comp104 refines a.b.P(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument and one mandatory argument type mismatch
      arg("component Comp105 refines a.b.P(1, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two mandatory argument type mismatch
      arg("component Comp106 refines a.b.P(true, 2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one mandatory argument (key assign first)
      arg("component Comp107 refines a.b.P(p1 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One mandatory argument type mismatch and one mandatory argument (key assign second)
      arg("component Comp108 refines a.b.P(true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one mandatory argument (key assign both)
      arg("component Comp109 refines a.b.P(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one mandatory argument (key assign first twice)
      arg("component Comp110 refines a.b.P(true, p1 = false) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one mandatory argument (key assign second twice)
      arg("component Comp111 refines a.b.P(p2 = true, false) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // One mandatory argument type mismatch and one mandatory argument (key assign both in order)
      arg("component Comp112 refines a.b.P(p1 = true, p2 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // One mandatory argument type mismatch and one mandatory argument (key assign both reverse order)
      arg("component Comp113 refines a.b.P(p2 = true, p1 = false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two mandatory arguments (arg assigned twice, key assign first)
      arg("component Comp114 refines a.b.P(1, p1 = 2) { }",
        ArcError.COMP_ARG_MULTIPLE_VALUES),
      // Two mandatory arguments (arg assigned twice, key assign second)
      arg("component Comp115 refines a.b.P(p2 = 1, 2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY),
      // Two mandatory arguments (repeated first key)
      arg("component Comp116 refines a.b.P(p1 = 1, p1 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // Two mandatory arguments (repeated second key)
      arg("component Comp117 refines a.b.P(p2 = 1, p2 = 2) { }",
        ArcError.KEY_NOT_UNIQUE),
      // First of two mandatory arguments generic type mismatch
      arg("component Comp118 refines a.b.O<int, boolean>(true, false) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Second of two mandatory arguments generic type mismatch
      arg("component Comp119 refines a.b.O<int, boolean>(1 ,2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two mandatory arguments generic type mismatch
      arg("component Comp120 refines a.b.O<int, boolean>(true ,2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // First of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp121(boolean q1, boolean q2) refines a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Second of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp122(int q1, int q2) refines a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp123(boolean q1, int q2) refines a.b.O<int, boolean>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // First of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp124<S, T>(S q1, T q2) refines a.b.O<T, T>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Second of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp125<S, T>(S q1, T q2) refines a.b.O<S, S>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp126<S, T>(S q1, T q2) refines a.b.O<T, S>(q1, q2) { }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      // Refinement with two mandatory parameters (assign first key to first argument, second key invalid)
      arg("component Comp127 refines a.b.P(p1 = 4, p4 = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // Refinement with two mandatory parameters (assign first key to second argument, second key invalid)
      arg("component Comp128 refines a.b.P(p2 = false, p4 = 1) { }",
        ArcError.COMP_ARG_KEY_INVALID),
      // Two mandatory arguments (arg assigned twice, key assign second) and two mandatory arguments generic type mismatch
      arg("component Comp129 refines a.b.P(p2 = 1, 2), a.b.O<int, boolean>(true ,2) { }",
        ArcError.COMP_ARG_VALUE_AFTER_KEY,
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH)
    );
  }
}

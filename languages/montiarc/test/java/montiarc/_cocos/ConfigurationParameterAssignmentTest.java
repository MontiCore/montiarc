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
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.ArcError.COMP_ARG_KEY_INVALID;
import static montiarc.util.ArcError.COMP_ARG_MULTIPLE_VALUES;
import static montiarc.util.ArcError.COMP_ARG_VALUE_AFTER_KEY;
import static montiarc.util.ArcError.KEY_NOT_UNIQUE;
import static montiarc.util.ArcError.TOO_FEW_ARGUMENTS;
import static montiarc.util.ArcError.TOO_MANY_ARGUMENTS;
import static montiarc.util.ArcError.COMP_ARG_TYPE_MISMATCH;
import static montiarc.util.MCError.TARGET_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConfigurationParameterAssignment}.
 */
class ConfigurationParameterAssignmentTest extends MontiArcTestBase {

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
    // Component without subcomponents, inheritance, or refinement
    "component ValidCompS01 {  }",
    // Subcomponent without arguments
    "component ValidCompS02 { a.b.A a; }",
    // Multiple subcomponents without arguments
    "component ValidCompS03 { a.b.A a1, a2; }",
    // Subcomponent with one mandatory parameter (assign value)
    "component ValidCompS04 { a.b.B b(1); }",
    // Multiple subcomponents with one mandatory parameter each (assign value)
    "component ValidCompS05 { a.b.B b1(1), b2(1); }",
    // Subcomponent with one mandatory parameter (assign boxed value)
    "component ValidCompS06 { a.b.B b(java.lang.Integer.Integer(1)); }",
    // Subcomponent with one mandatory String parameter (assign constructor-call value)
    "component ValidCompS07 { a.b.C c(java.lang.String.String()); }",
    // Subcomponent with two mandatory parameters (assign values)
    "component ValidCompS08 { a.b.D d(1, 2); }",
    // Subcomponent with one optional parameter (default value)
    "component ValidCompS09 { a.b.E e; }",
    // Subcomponent with one optional parameter (assign value)
    "component ValidCompS10 { a.b.E e(1); }",
    // Subcomponent with one optional parameter (assign boxed value)
    "component ValidCompS11 { a.b.E e(java.lang.Integer.Integer(1)); }",
    // Subcomponent with one optional parameter (assign value via key)
    "component ValidCompS12 { a.b.E e(p = 1); }",
    // Subcomponent with one optional parameter (assign boxed value via key)
    "component ValidCompS13 { a.b.E e(p = java.lang.Integer.Integer(1)); }",
    // Subcomponent with one optional String parameter (default value)
    "component ValidCompS14 { a.b.F f; }",
    // Subcomponent with one optional String parameter (assign value)
    "component ValidCompS15 { a.b.F f(java.lang.String.String()); }",
    // Subcomponent with one optional String parameter (assign value via key)
    "component ValidCompS16 { a.b.F f(p = java.lang.String.String()); }",
    // Subcomponent with two optional parameters (default values)
    "component ValidCompS17 { a.b.G g; }",
    // Subcomponent with two optional parameters (assign first value, second default)
    "component ValidCompS18 { a.b.G g(java.lang.String.String()); }",
    // Subcomponent with two optional parameters (assign values)
    "component ValidCompS19 { a.b.G g(java.lang.String.String(), 1); }",
    // Subcomponent with two optional parameters (assign values, second boxed)
    "component ValidCompS20 { a.b.G g(java.lang.String.String(), java.lang.Integer.Integer(1)); }",
    // Subcomponent with two optional parameters (assign values via key in order)
    "component ValidCompS21 { a.b.G g(p1 = java.lang.String.String(), p2 = 1); }",
    // Subcomponent with two optional parameters (assign values via key in order, second boxed)
    "component ValidCompS22 { a.b.G g(p1 = java.lang.String.String(), p2 = java.lang.Integer.Integer(1)); }",
    // Subcomponent with two optional parameters (assign values via key in reverse order, second boxed)
    "component ValidCompS23 { a.b.G g(p2 = java.lang.Integer.Integer(1), p1 = java.lang.String.String()); }",
    // Subcomponent with two optional parameters (assign first by value, second by key)
    "component ValidCompS24 { a.b.G g(java.lang.String.String(), p2 = 1); }",
    // Subcomponent with two optional parameters (assign first by value, second by key, boxed)
    "component ValidCompS25 { a.b.G g(java.lang.String.String(), p2 = java.lang.Integer.Integer(1)); }",
    // Subcomponent with two optional parameters (assign first value via key, omit second)
    "component ValidCompS26 { a.b.G g(p1 = java.lang.String.String()); }",
    // Subcomponent with two optional parameters (omit first, assign second value via key)
    "component ValidCompS27 { a.b.G g(p2 = 1); }",
    // Subcomponent with two optional parameters (omit first, assign second boxed value via key)
    "component ValidCompS28 { a.b.G g(p2 = java.lang.Integer.Integer(1)); }",
    // Subcomponent with three mandatory parameters (assign values)
    "component ValidCompS29 { a.b.H h(1, 2, 3); }",
    // Subcomponent with three mandatory and three optional parameters (assign first three by position)
    "component ValidCompS30 { a.b.I i(1, 2, 3); }",
    // Subcomponent with three mandatory and three optional parameters (assign first four by position)
    "component ValidCompS31 { a.b.I i(1, 2, 3, 4); }",
    // Subcomponent with three mandatory and three optional parameters (assign first five by position)
    "component ValidCompS32 { a.b.I i(1, 2, 3, 4, 5); }",
    // Subcomponent with three mandatory and three optional parameters (assign all six by position)
    "component ValidCompS33 { a.b.I i(1, 2, 3, 4, 5, 6); }",
    // Subcomponent with three mandatory and three optional parameters (assign first five by position, last by key)
    "component ValidCompS34 { a.b.I i(1, 2, 3, 4, 5, p6 = 6); }",
    // Subcomponent with three mandatory and three optional parameters (assign first four by position, last two by key)
    "component ValidCompS35 { a.b.I i(1, 2, 3, 4, p5 = 5, p6 = 6); }",
    // Subcomponent with three mandatory and three optional parameters (assign all by key in order)
    "component ValidCompS36 { a.b.I i(p1 = 1, p2 = 2, p3 = 3, p4 = 4, p5 = 5, p6 = 6); }",
    // Subcomponent with three mandatory and three optional parameters (assign all by key in reverse order)
    "component ValidCompS37 { a.b.I i(p6 = 1, p5 = 2, p4 = 3, p3 = 4, p2 = 5, p1 = 6); }",
    // Subcomponent with three optional parameters (default values)
    "component ValidCompS38 { a.b.J j; }",
    // Subcomponent with three optional parameters (assign first)
    "component ValidCompS39 { a.b.J j(1); }",
    // Subcomponent with three optional parameters (assign first two)
    "component ValidCompS40 { a.b.J j(1, 2); }",
    // Subcomponent with three optional parameters (assign all by position)
    "component ValidCompS41 { a.b.J j(1, 2, 3); }",
    // Subcomponent with three optional parameters (assign first two by position, last by key)
    "component ValidCompS42 { a.b.J j(1, 2, p3 = 3); }",
    // Subcomponent with three optional parameters (assign first by position, last two by key)
    "component ValidCompS43 { a.b.J j(1, p2 = 2, p3 = 3); }",
    // Subcomponent with three optional parameters (assign all by key in order)
    "component ValidCompS44 { a.b.J j(p1 = 1, p2 = 2, p3 = 3); }",
    // Subcomponent with three optional parameters (assign all by key in reverse order)
    "component ValidCompS45 { a.b.J j(p3 = 1, p2 = 2, p1 = 3); }",
    // Subcomponent with one mandatory generic-collection parameter (assign value)
    "component ValidCompS46 { a.b.K k(java.util.Arrays.asList(1)); }",
    // Subcomponent with one optional generic-collection parameter (default value)
    "component ValidCompS47 { a.b.L l; }",
    // Subcomponent with one optional generic-collection parameter (assign value)
    "component ValidCompS48 { a.b.L l(java.util.Arrays.asList(1)); }",
    // Subcomponent with one optional generic-collection parameter (assign value via key)
    "component ValidCompS49 { a.b.L l(p = java.util.Arrays.asList(1)); }",
    // Subcomponent with one mandatory generic parameter (bind type, assign value)
    "component ValidCompS50 { a.b.M<java.lang.Integer> m(1); }",
    // Subcomponent with one mandatory generic parameter (bind type, assign boxed value)
    "component ValidCompS51 { a.b.M<java.lang.Integer> m(java.lang.Integer.Integer(1)); }",
    // Subcomponent with one mandatory generic-list parameter (bind type, assign value)
    "component ValidCompS52 { a.b.N<java.lang.Integer> m(java.util.Arrays.asList(1)); }",
    // Subcomponent with one mandatory generic-list parameter (bind type, assign empty list)
    "component ValidCompS53 { a.b.N<java.lang.Integer> m(java.util.Collections.emptyList()); }",
    // Inheritance without parameters
    "component ValidCompI01 extends a.b.A { }",
    // Inheritance adding a parameter
    "component ValidCompI02(int p) extends a.b.A { }",
    // Inheritance with one mandatory parameter (assign value)
    "component ValidCompI03 extends a.b.B(1) { }",
    // Inheritance with one mandatory parameter (pass parameter, different names)
    "component ValidCompI04(int q) extends a.b.B(q) { }",
    // Inheritance with one mandatory parameter (pass parameter, same name)
    "component ValidCompI05(int p) extends a.b.B(p) { }",
    // Inheritance with one mandatory parameter (assign value via key)
    "component ValidCompI06 extends a.b.B(p = 1) { }",
    // Inheritance with one mandatory parameter (pass parameter via key, different name)
    "component ValidCompI07(int q) extends a.b.B(p = q) { }",
    // Inheritance with one mandatory parameter (pass parameter via key, same name)
    "component ValidCompI08(int p) extends a.b.B(p = p) { }",
    // Inheritance with one optional parameter (default value)
    "component ValidCompI09 extends a.b.E { }",
    // Inheritance with one optional parameter (assign value)
    "component ValidCompI10 extends a.b.E(1) { }",
    // Inheritance with one optional parameter (pass parameter, different name)
    "component ValidCompI11(int q) extends a.b.E(q) { }",
    // Inheritance with one optional parameter (pass parameter, same name)
    "component ValidCompI12(int p) extends a.b.E(p) { }",
    // Inheritance with one optional parameter (assign value via key)
    "component ValidCompI13 extends a.b.E(p = 1) { }",
    // Inheritance with one optional parameter (pass parameter via key, different name)
    "component ValidCompI14(int q) extends a.b.E(p = q) { }",
    // Inheritance with one optional parameter (pass parameter via key, same name)
    "component ValidCompI15(int p) extends a.b.E(p = p) { }",
    // Inheritance with one mandatory and one optional parameter (default value)
    "component ValidCompI16 extends a.b.R(1) { }",
    // Inheritance with one mandatory and one optional parameter (assign values)
    "component ValidCompI17 extends a.b.R(1, false) { }",
    // Inheritance with one mandatory and one optional parameter (pass parameter, default value)
    "component ValidCompI18(int p) extends a.b.R(p) { }",
    // Inheritance with one mandatory and one optional parameter (pass parameters)
    "component ValidCompI19(int p1, boolean p2) extends a.b.R(p1, p2) { }",
    // Inheritance with one mandatory and one optional parameter (assign values via key in order)
    "component ValidCompI20 extends a.b.R(p1 = 1, p2 = false) { }",
    // Inheritance with one mandatory and one optional parameter (assign values via key reverse order)
    "component ValidCompI21 extends a.b.R(p2 = false, p1 = 1) { }",
    // Inheritance with one mandatory and one optional parameter (pass parameters via key, default value)
    "component ValidCompI22(int p) extends a.b.R(p1 = p) { }",
    // Inheritance with one mandatory and one optional parameter (pass parameters via key in order)
    "component ValidCompI23(int p1, boolean p2) extends a.b.R(p1 = p1, p2 = p2) { }",
    // Inheritance with one mandatory and one optional parameter (pass parameters via key reverse order)
    "component ValidCompI24(int p1, boolean p2) extends a.b.R(p2 = p2, p1 = p1) { }",
    // Inheritance with two optional parameters (default values)
    "component ValidCompI25 extends a.b.Q { }",
    // Inheritance with two optional parameters (assign single value)
    "component ValidCompI26 extends a.b.Q(1) { }",
    // Inheritance with two optional parameters (assign values)
    "component ValidCompI27 extends a.b.Q(1, false) { }",
    // Inheritance with two optional parameters (pass single parameter)
    "component ValidCompI28(int p) extends a.b.Q(p) { }",
    // Inheritance with two optional parameters (pass parameters)
    "component ValidCompI29(int p1, boolean p2) extends a.b.Q(p1, p2) { }",
    // Inheritance with two optional parameters (assign first value via key)
    "component ValidCompI30 extends a.b.Q(p1 = 1) { }",
    // Inheritance with two optional parameters (assign second value via key)
    "component ValidCompI31 extends a.b.Q(p2 = false) { }",
    // Inheritance with two optional parameters (assign values via key in order)
    "component ValidCompI32 extends a.b.Q(p1 = 1, p2 = false) { }",
    // Inheritance with two optional parameters (assign values via key reverse order)
    "component ValidCompI33 extends a.b.Q(p2 = false, p1 = 1) { }",
    // Inheritance with two optional parameters (pass first parameter via key)
    "component ValidCompI34(int p) extends a.b.Q(p1 = p) { }",
    // Inheritance with two optional parameters (pass second parameter via key)
    "component ValidCompI35(boolean p) extends a.b.Q(p2 = p) { }",
    // Inheritance with two optional parameters (pass parameters via key in order)
    "component ValidCompI36(int p1, boolean p2) extends a.b.Q(p1 = p1, p2 = p2) { }",
    // Inheritance with two optional parameters (pass parameters via key reverse order)
    "component ValidCompI37(int p1, boolean p2) extends a.b.Q(p2 = p2, p1 = p1) { }",
    // Inheritance with two mandatory parameters (assign values)
    "component ValidCompI38 extends a.b.P(1, false) { }",
    // Inheritance with two mandatory parameters (pass parameters)
    "component ValidCompI39(int p1, boolean p2) extends a.b.P(p1, p2) { }",
    // Inheritance with two mandatory parameters (assign values via key in order)
    "component ValidCompI40 extends a.b.P(p1 = 1, p2 = false) { }",
    // Inheritance with two mandatory parameters (assign values via key reverse order)
    "component ValidCompI41 extends a.b.P(p2 = false, p1 = 1) { }",
    // Inheritance with two mandatory parameters (pass parameters via key in order)
    "component ValidCompI42(int p1, boolean p2) extends a.b.P(p1 = p1, p2 = p2) { }",
    // Inheritance with two mandatory parameters (pass parameters via key reverse order)
    "component ValidCompI43(int p1, boolean p2) extends a.b.P(p2 = p2, p1 = p1) { }",
    // Inheritance with one mandatory generic parameter (bind type, assign value)
    "component ValidCompI44 extends a.b.M<int>(1) { }",
    // Inheritance with one mandatory generic parameter (bind type, pass parameter)
    "component ValidCompI45(int p) extends a.b.M<int>(p) { }",
    // Inheritance with one mandatory generic parameter (pass type, pass parameter)
    "component ValidCompI46<T>(T p) extends a.b.M<T>(p) { }",
    // Inheritance with one mandatory generic parameter (bind type, assign value via key)
    "component ValidCompI47 extends a.b.M<int>(p = 1) { }",
    // Inheritance with one mandatory generic parameter (bind type, pass parameter via key, different name)
    "component ValidCompI48(int q) extends a.b.M<int>(p = q) { }",
    // Inheritance with one mandatory generic parameter (bind type, pass parameter via key, same name)
    "component ValidCompI49(int p) extends a.b.M<int>(p = p) { }",
    // Inheritance with one mandatory generic parameter (pass parameter via key, different name, same type name)
    "component ValidCompI50<T>(T q) extends a.b.M<T>(p = q) { }",
    // Inheritance with one mandatory generic parameter (pass parameter via key, same name, same type name)
    "component ValidCompI51<T>(T p) extends a.b.M<T>(p = p) { }",
    // Inheritance with one mandatory generic parameter (pass parameter via key, different name, different type name)
    "component ValidCompI52<S>(S q) extends a.b.M<S>(p = q) { }",
    // Inheritance with one mandatory generic parameter (pass parameter via key, same name, different type name)
    "component ValidCompI53<S>(S p) extends a.b.M<S>(p = p) { }",
    // Inheritance with two mandatory generic parameters (bind type, assign values)
    "component ValidCompI54 extends a.b.O<int, boolean>(1, false) { }",
    // Inheritance with two mandatory generic parameters (bind type, pass parameters)
    "component ValidCompI55(int p1, boolean p2) extends a.b.O<int, boolean>(p1, p2) { }",
    // Inheritance with two mandatory generic parameters (pass type, pass parameters)
    "component ValidCompI56<U, V>(U p1, V p2) extends a.b.O<U, V>(p1, p2) { }",
    // Inheritance with one mandatory generic parameter (bind type, assign value via key) and with two mandatory generic parameters (pass type, pass parameters)
    "component ValidCompI57<U, V>(U p1, V p2) extends a.b.M<int>(p = 1), a.b.O<U, V>(p1, p2) { }",
    // Refinement without parameters
    "component ValidCompR01 refines a.b.A  { }",
    // Multi-Refinement without parameters
    "component ValidCompR02 refines a.b.A, a.b.A { }",
    // Refinement with one mandatory parameter (assign value)
    "component ValidCompR03 refines a.b.B (1) { }",
    // Refinement with a boxed version of the parameter's primitive type
    "component ValidCompR04 refines a.b.B (java.lang.Integer.Integer(1)) { }",
    // Refinement with one mandatory parameter (pass parameter, different name)
    "component ValidCompR05(int q) refines a.b.B(q) { }",
    // Refinement with one mandatory parameter (pass parameter, same name)
    "component ValidCompR06(int p) refines a.b.B(p) { }",
    // Refinement with one mandatory parameter (assign value via key)
    "component ValidCompR07 refines a.b.B(p = 1) { }",
    // Refinement with one mandatory parameter (pass parameter via key, different name)
    "component ValidCompR08(int q) refines a.b.B(p = q) { }",
    // Refinement with one mandatory parameter (pass parameter via key, same name)
    "component ValidCompR09(int p) refines a.b.B(p = p) { }",
    // Refinement with a constructor call as the argument
    "component ValidCompR10 refines a.b.C (java.lang.String.String()) { }",
    // Assigning both mandatory params
    "component ValidCompR11 refines a.b.D (1, 2) { }",
    // Refinement with one optional parameter (default value)
    "component ValidCompR12 refines a.b.E { }",
    // Refinement with one optional parameter (assign value)
    "component ValidCompR13 refines a.b.E (1) { }",
    // Refinement with a boxed version of the parameter's primitive type
    "component ValidCompR14 refines a.b.E (java.lang.Integer.Integer(1)) { }",
    // Refinement with one optional parameter (assign value via key)
    "component ValidCompR15 refines a.b.E (p = 1) { }",
    // Refinement with a boxed version of the parameter's primitive type, assigned by key
    "component ValidCompR16 refines a.b.E (p = java.lang.Integer.Integer(1)) { }",
    // Refinement with one optional parameter (pass parameter, different name)
    "component ValidCompR17(int q) refines a.b.E(q) { }",
    // Refinement with one optional parameter (pass parameter, same name)
    "component ValidCompR18(int p) refines a.b.E(p) { }",
    // Refinement with one optional parameter (pass parameter via key, different name)
    "component ValidCompR19(int q) refines a.b.E(p = q) { }",
    // Refinement with one optional parameter (pass parameter via key, same name)
    "component ValidCompR20(int p) refines a.b.E(p = p) { }",
    // Refinement with one optional parameter (default value)
    "component ValidCompR21 refines a.b.F { }",
    // Refinement with one optional parameter (assign value with constructor call)
    "component ValidCompR22 refines a.b.F (java.lang.String.String()) { }",
    // Refinement with one optional parameter (assign value by key with constructor call)
    "component ValidCompR23 refines a.b.F (p = java.lang.String.String()) { }",
    // Refinement with two optional parameters (default values)
    "component ValidCompR24 refines a.b.G { }",
    // Refinement with two optional parameters (1. assign value, 2. default value)
    "component ValidCompR25 refines a.b.G (java.lang.String.String()) { }",
    // Refinement with two optional parameters (assigned values)
    "component ValidCompR26 refines a.b.G (java.lang.String.String(), 1) { }",
    // Refinement with two optional parameters (assigned values, with boxed value for primitive parameter type)
    "component ValidCompR27 refines a.b.G (java.lang.String.String(), java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (assign by key)
    "component ValidCompR28 refines a.b.G (p1 = java.lang.String.String(), p2 = 1) { }",
    // Refinement with two optional parameters (assign by key, with boxed value for primitive parameter type)
    "component ValidCompR29 refines a.b.G (p1 = java.lang.String.String(), p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (assign by key, in changed order, with boxed value for primitive parameter type)
    "component ValidCompR30 refines a.b.G (p2 = java.lang.Integer.Integer(1), p1 = java.lang.String.String()) { }",
    // Refinement with two optional parameters (1. assign value, 2. assign by key)
    "component ValidCompR31 refines a.b.G (java.lang.String.String(), p2 = 1) { }",
    // Refinement with two optional parameters (1. assign value, 2. assign by key and with boxed value)
    "component ValidCompR32 refines a.b.G (java.lang.String.String(), p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with two optional parameters (1. assign by key, omit 2nd)
    "component ValidCompR33 refines a.b.G (p1 = java.lang.String.String()) { }",
    // Refinement with two optional parameters (omit first, assign 2nd)
    "component ValidCompR34 refines a.b.G (p2 = 1) { }",
    // Refinement with two optional parameters (omit first, assign 2nd with boxed value)
    "component ValidCompR35 refines a.b.G (p2 = java.lang.Integer.Integer(1)) { }",
    // Refinement with three mandatory parameters
    "component ValidCompR36 refines a.b.H (1, 2, 3) { }",
    // Refinement with three mandatory and three optional parameters. Assign first three params by position
    "component ValidCompR37 refines a.b.I (1, 2, 3) { }",
    // Refinement with three mandatory and three optional parameters. Assign first four params by position
    "component ValidCompR38 refines a.b.I (1, 2, 3, 4) { }",
    // Refinement with three mandatory and three optional parameters. Assign first five params by position
    "component ValidCompR39 refines a.b.I (1, 2, 3, 4, 5) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by position
    "component ValidCompR40 refines a.b.I (1, 2, 3, 4, 5, 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign first five params by position, last by key
    "component ValidCompR41 refines a.b.I (1, 2, 3, 4, 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign first 4 params by position, last two by key
    "component ValidCompR42 refines a.b.I (1, 2, 3, 4, p5 = 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by key
    "component ValidCompR43 refines a.b.I (p1 = 1, p2 = 2, p3 = 3, p4 = 4, p5 = 5, p6 = 6) { }",
    // Refinement with three mandatory and three optional parameters. Assign all params by key in changed order
    "component ValidCompR44 refines a.b.I (p6 = 1, p5 = 2, p4 = 3, p3 = 4, p2 = 5, p1 = 6) { }",
    // Refinement with three optional parameters (omit args)
    "component ValidCompR45 refines a.b.J { }",
    // Refinement with three optional parameters. Assign first arg
    "component ValidCompR46 refines a.b.J (1) { }",
    // Refinement with three optional parameters. Assign first two args
    "component ValidCompR47 refines a.b.J (1, 2) { }",
    // Refinement with three optional parameters. Assign all args
    "component ValidCompR48 refines a.b.J (1, 2, 3) { }",
    // Refinement with three optional parameters. Assign first two args by value, last by key
    "component ValidCompR49 refines a.b.J (1, 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign first arg by value, last two by key
    "component ValidCompR50 refines a.b.J (1, p2 = 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign all args by key
    "component ValidCompR51 refines a.b.J (p1 = 1, p2 = 2, p3 = 3) { }",
    // Refinement with three optional parameters. Assign all args by key in changed order
    "component ValidCompR52 refines a.b.J (p3 = 1, p2 = 2, p1 = 3) { }",
    // Refinement with optional list parameter (omit arg)
    "component ValidCompR53 refines a.b.L { }",
    // Refinement with one mandatory generic parameter (bind type, assign primitive value)
    "component ValidCompR54 refines a.b.M<java.lang.Integer>(1) { }",
    // Refinement with one mandatory generic parameter (bind type, assign boxed value)
    "component ValidCompR55 refines a.b.M<java.lang.Integer>(java.lang.Integer.Integer(1)) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value)
    "component ValidCompR56 refines a.b.M<int>(1) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter)
    "component ValidCompR57(int p) refines a.b.M<int>(p) { }",
    // Refinement with one mandatory generic parameter (pass type, pass parameter)
    "component ValidCompR58<T>(T p) refines a.b.M<T>(p) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value via key)
    "component ValidCompR59 refines a.b.M<int>(p = 1) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter via key, different name)
    "component ValidCompR60(int q) refines a.b.M<int>(p = q) { }",
    // Refinement with one mandatory generic parameter (bind type, pass parameter via key, same name)
    "component ValidCompR61(int p) refines a.b.M<int>(p = p) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, different name, same type name)
    "component ValidCompR62<T>(T q) refines a.b.M<T>(p = q) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, same name, same type name)
    "component ValidCompR63<T>(T p) refines a.b.M<T>(p = p) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, different name, different type name)
    "component ValidCompR64<S>(S q) refines a.b.M<S>(p = q) { }",
    // Refinement with one mandatory generic parameter (pass parameter via key, same name, different type name)
    "component ValidCompR65<S>(S p) refines a.b.M<S>(p = p) { }",
    // Refinement with two mandatory generic parameters (bind type, assign values)
    "component ValidCompR66 refines a.b.O<int, boolean>(1, false) { }",
    // Refinement with two mandatory generic parameters (bind type, pass parameters)
    "component ValidCompR67(int p1, boolean p2) refines a.b.O<int, boolean>(p1, p2) { }",
    // Refinement with two mandatory generic parameters (pass type, pass parameters)
    "component ValidCompR68<U, V>(U p1, V p2) refines a.b.O<U, V>(p1, p2) { }",
    // Refinement with one mandatory generic parameter (bind type, assign value via key) and with two mandatory generic parameters (pass type, pass parameters)
    "component ValidCompR69<U, V>(U p1, V p2) refines a.b.M<int>(p = 1), a.b.O<U, V>(p1, p2) { }",
    // Refinement with one mandatory and one optional parameter (default value)
    "component ValidCompR70 refines a.b.R(1) { }",
    // Refinement with one mandatory and one optional parameter (assign values)
    "component ValidCompR71 refines a.b.R(1, false) { }",
    // Refinement with one mandatory and one optional parameter (pass parameter, default value)
    "component ValidCompR72(int p) refines a.b.R(p) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters)
    "component ValidCompR73(int p1, boolean p2) refines a.b.R(p1, p2) { }",
    // Refinement with one mandatory and one optional parameter (assign values via key in order)
    "component ValidCompR74 refines a.b.R(p1 = 1, p2 = false) { }",
    // Refinement with one mandatory and one optional parameter (assign values via key reverse order)
    "component ValidCompR75 refines a.b.R(p2 = false, p1 = 1) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key, default value)
    "component ValidCompR76(int p) refines a.b.R(p1 = p) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key in order)
    "component ValidCompR77(int p1, boolean p2) refines a.b.R(p1 = p1, p2 = p2) { }",
    // Refinement with one mandatory and one optional parameter (pass parameters via key reverse order)
    "component ValidCompR78(int p1, boolean p2) refines a.b.R(p2 = p2, p1 = p1) { }",
    // Refinement with two optional parameters (default values)
    "component ValidCompR79 refines a.b.Q { }",
    // Refinement with two optional parameters (assign single value)
    "component ValidCompR80 refines a.b.Q(1) { }",
    // Refinement with two optional parameters (assign values)
    "component ValidCompR81 refines a.b.Q(1, false) { }",
    // Refinement with two optional parameters (pass single parameter)
    "component ValidCompR82(int p) refines a.b.Q(p) { }",
    // Refinement with two optional parameters (pass parameters)
    "component ValidCompR83(int p1, boolean p2) refines a.b.Q(p1, p2) { }",
    // Refinement with two optional parameters (assign first value via key)
    "component ValidCompR84 refines a.b.Q(p1 = 1) { }",
    // Refinement with two optional parameters (assign second value via key)
    "component ValidCompR85 refines a.b.Q(p2 = false) { }",
    // Refinement with two optional parameters (assign values via key in order)
    "component ValidCompR86 refines a.b.Q(p1 = 1, p2 = false) { }",
    // Refinement with two optional parameters (assign values via key reverse order)
    "component ValidCompR87 refines a.b.Q(p2 = false, p1 = 1) { }",
    // Refinement with two optional parameters (pass first parameter via key)
    "component ValidCompR88(int p) refines a.b.Q(p1 = p) { }",
    // Refinement with two optional parameters (pass second parameter via key)
    "component ValidCompR89(boolean p) refines a.b.Q(p2 = p) { }",
    // Refinement with two optional parameters (pass parameters via key in order)
    "component ValidCompR90(int p1, boolean p2) refines a.b.Q(p1 = p1, p2 = p2) { }",
    // Refinement with two optional parameters (pass parameters via key reverse order)
    "component ValidCompR91(int p1, boolean p2) refines a.b.Q(p2 = p2, p1 = p1) { }",
    // Refinement with two mandatory parameters (assign values)
    "component ValidCompR92 refines a.b.P(1, false) { }",
    // Refinement with two mandatory parameters (pass parameters)
    "component ValidCompR93(int p1, boolean p2) refines a.b.P(p1, p2) { }",
    // Refinement with two mandatory parameters (assign values via key in order)
    "component ValidCompR94 refines a.b.P(p1 = 1, p2 = false) { }",
    // Refinement with two mandatory parameters (assign values via key reverse order)
    "component ValidCompR95 refines a.b.P(p2 = false, p1 = 1) { }",
    // Refinement with two mandatory parameters (pass parameters via key in order)
    "component ValidCompR96(int p1, boolean p2) refines a.b.P(p1 = p1, p2 = p2) { }",
    // Refinement with two mandatory parameters (pass parameters via key reverse order)
    "component ValidCompR97(int p1, boolean p2) refines a.b.P(p2 = p2, p1 = p1) { }",
  })
  @DisableIfDisplayName(contains = {
    "ValidCompS53"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModelsSubComponent")
  void shouldReportErrorSubComponents(@NotNull String model,
                                      @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo((ArcBasisASTComponentInstanceCoCo) new ConfigurationParameterAssignment());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsSubComponent() {
    return Stream.of(
      // one argument too many, no parameters
      arg("""
          component InvalidComp1 {
            a.b.A a(1);
          }""",
        TOO_MANY_ARGUMENTS
      ),
      // two arguments too many, no parameters
      arg("""
          component InvalidComp2 {
            a.b.A a(1, 2);
          }""",
        TOO_MANY_ARGUMENTS
      ),
      // one argument too many, no parameters, invalid key
      arg("""
          component InvalidComp3 {
            a.b.A a(p = 1);
          }""",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID
      ),
      // missing single mandatory argument
      arg("""
          component InvalidComp4 {
            a.b.B b;
          }""",
        TOO_FEW_ARGUMENTS
      ),
      // one argument too many (one mandatory parameter)
      arg("""
          component InvalidComp5 {
            a.b.B b (1, 2);
          }""",
        TOO_MANY_ARGUMENTS
      ),
      // invalid key for the mandatory parameter
      arg("""
          component InvalidComp6 {
            a.b.B b (np = 1);
          }""",
        COMP_ARG_KEY_INVALID
      ),
      // one argument too many (one mandatory parameter, key assign second)
      arg("""
          component InvalidComp7 {
            a.b.B b (1, p = 2);
          }""",
        TOO_MANY_ARGUMENTS, COMP_ARG_MULTIPLE_VALUES
      ),
      // one argument too many (one mandatory parameter, key assign first)
      arg("""
          component InvalidComp8 {
            a.b.B b (p = 1, 2);
          }""",
        TOO_MANY_ARGUMENTS, COMP_ARG_VALUE_AFTER_KEY
      ),
      // one argument too many, invalid key second
      arg("""
          component InvalidComp9 {
            a.b.B b (1, np = 2);
          }""",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID
      ),
      // one argument too many, invalid key first
      arg("""
          component InvalidComp10 {
            a.b.B b (np = 1, 2);
          }""",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID, COMP_ARG_VALUE_AFTER_KEY
      ),
      // one mandatory argument type mismatch
      arg("""
          component InvalidComp11 {
            a.b.B b (true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one argument too many
      arg("""
          component InvalidComp12 {
            a.b.B b (true, 2);
          }""",
        COMP_ARG_TYPE_MISMATCH, TOO_MANY_ARGUMENTS
      ),
      // mandatory String parameter, int argument type mismatch
      arg("""
          component InvalidComp13 {
            a.b.C c (1);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // mandatory String parameter, boxed Integer argument (target type mismatch)
      arg("""
          component InvalidComp14 {
            a.b.C c (java.lang.Integer.Integer(1));
          }""",
        TARGET_TYPE_MISMATCH
      ),
      // first of two mandatory arguments type mismatch
      arg("""
          component InvalidComp15 {
            a.b.D d (true, 2);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // second of two mandatory arguments type mismatch
      arg("""
          component InvalidComp16 {
            a.b.D d (1, false);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // both mandatory arguments type mismatch
      arg("""
          component InvalidComp17 {
            a.b.D d (true, false);
          }""",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // key assign first, positional value after key
      arg("""
          component InvalidComp18 {
            a.b.D d (p2 = 1, false);
          }""",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // first argument assigned twice (positional, then key)
      arg("""
          component InvalidComp19 {
            a.b.D d (1, p1 = 2);
          }""",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // second parameter's key repeated
      arg("""
          component InvalidComp20 {
            a.b.D d (p2 = 1, p2 = 2);
          }""",
        KEY_NOT_UNIQUE
      ),
      // first argument type mismatch, second correctly assigned by key
      arg("""
          component InvalidComp21 {
            a.b.D d (true, p2 = 2);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // first argument correct, second argument type mismatch (key assign)
      arg("""
          component InvalidComp22 {
            a.b.D d (1, p2 = false);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one argument too many (one optional parameter)
      arg("""
          component InvalidComp23 {
            a.b.E e (1, 2);
          }""",
        TOO_MANY_ARGUMENTS
      ),
      // one argument too many (one optional parameter, repeated key)
      arg("""
          component InvalidComp24 {
            a.b.E e (p = 1, p = 2);
          }""",
        TOO_MANY_ARGUMENTS, KEY_NOT_UNIQUE
      ),
      // one optional argument type mismatch
      arg("""
          component InvalidComp25 {
            a.b.E e (true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch (key assign)
      arg("""
          component InvalidComp26 {
            a.b.E e (p = true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // invalid key for the optional parameter
      arg("""
          component InvalidComp27 {
            a.b.E e (np = 1);
          }""",
        COMP_ARG_KEY_INVALID
      ),
      // optional String parameter, int argument type mismatch
      arg("""
          component InvalidComp28 {
            a.b.F f (1);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // both keyed arguments assigned to the swapped parameter's type
      arg("""
          component InvalidComp29 {
            a.b.G g (p2 = java.lang.String.String(), p1 = 1);
          }""",
        TARGET_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // missing all three mandatory arguments
      arg("""
          component InvalidComp30 {
            a.b.H h;
          }""",
        TOO_FEW_ARGUMENTS
      ),
      // missing two of three mandatory arguments
      arg("""
          component InvalidComp31 {
            a.b.H h (1);
          }""",
        TOO_FEW_ARGUMENTS
      ),
      // missing one of three mandatory arguments
      arg("""
          component InvalidComp32 {
            a.b.H h (1, 2);
          }""",
        TOO_FEW_ARGUMENTS
      ),
      // repeated key leaves one mandatory parameter unassigned
      arg("""
          component InvalidComp33 {
            a.b.I i (1, 2, p4 = 3, p4 = 4, p5 = 5, p6 = 6);
          }""",
        TOO_FEW_ARGUMENTS, KEY_NOT_UNIQUE
      ),
      // positional arguments after a key assignment
      arg("""
          component InvalidComp34 {
            a.b.I i (1, 2, p3 = 3, 4, 5, 6);
          }""",
        COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_VALUE_AFTER_KEY
      ),
      // generic parameter bound to Integer, boolean argument type mismatch
      arg("""
          component InvalidComp35 {
            a.b.M<java.lang.Integer> m (true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // generic parameter bound to String, boolean argument type mismatch
      arg("""
          component InvalidComp36 {
            a.b.M<java.lang.String> m (true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // generic parameter bound to Integer, boolean argument type mismatch (key assign)
      arg("""
          component InvalidComp37 {
            a.b.M<java.lang.Integer> m (p = true);
          }""",
        COMP_ARG_TYPE_MISMATCH
      )
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
      arg("component InvalidComp1 extends a.b.A(1) { }",
        TOO_MANY_ARGUMENTS
      ),
      // missing single mandatory argument
      arg("component InvalidComp2 extends a.b.B { }",
        TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch
      arg("component InvalidComp3 extends a.b.B(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch (key assign)
      arg("component InvalidComp4 extends a.b.B(p = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one argument too many (one mandatory parameter)
      arg("component InvalidComp5 extends a.b.B(1, 2) { }",
        TOO_MANY_ARGUMENTS),
      // one argument too many (one mandatory parameter, key assign first)
      arg("component InvalidComp6 extends a.b.B(p = 1, 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_VALUE_AFTER_KEY
      ),
      // one argument too many (one mandatory parameter, key assign second)
      arg("component InvalidComp7 extends a.b.B(1, p = 2) { }",
        COMP_ARG_MULTIPLE_VALUES, TOO_MANY_ARGUMENTS
      ),
      // one argument too many (one mandatory parameter, repeated key)
      arg("component InvalidComp8 extends a.b.B(p = 1, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter)
      arg("component InvalidComp9 extends a.b.B(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_MANY_ARGUMENTS
      ),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign first)
      arg("component InvalidComp10 extends a.b.B(p = true, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY, TOO_MANY_ARGUMENTS
      ),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign second)
      arg("component InvalidComp11 extends a.b.B(true, p = 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, repeated key)
      arg("component InvalidComp12 extends a.b.B(p = true, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // one optional argument type mismatch
      arg("component InvalidComp13 extends a.b.E(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch (key assign)
      arg("component InvalidComp14 extends a.b.E(p = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one argument too many (one optional parameter)
      arg("component InvalidComp15 extends a.b.E(1, 2) { }",
        TOO_MANY_ARGUMENTS
      ),
      // one argument too many (one optional parameter, key assign first)
      arg("component InvalidComp16 extends a.b.E(p = 1, 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_VALUE_AFTER_KEY
      ),
      // one argument too many (one optional parameter, key assign second)
      arg("component InvalidComp17 extends a.b.E(1, p = 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_MULTIPLE_VALUES
      ),
      // one argument too many (one optional parameter, repeated key)
      arg("component InvalidComp18 extends a.b.E(p = 1, p = 2) { }",
        TOO_MANY_ARGUMENTS, KEY_NOT_UNIQUE
      ),
      // one optional argument type mismatch and one argument too many
      arg("component InvalidComp19 extends a.b.E(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_MANY_ARGUMENTS
      ),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign first)
      arg("component InvalidComp20 extends a.b.E(p = true, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY, TOO_MANY_ARGUMENTS
      ),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign second)
      arg("component InvalidComp21 extends a.b.E(true, p = 2) { }",
        COMP_ARG_MULTIPLE_VALUES, TOO_MANY_ARGUMENTS, COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one argument too many (one optional parameter, repeated key)
      arg("component InvalidComp22 extends a.b.E(p = true, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // two arguments too many (one optional parameter)
      arg("component InvalidComp23 extends a.b.E(1, 2, 3) { }",
        TOO_MANY_ARGUMENTS
      ),
      // missing one mandatory argument (one mandatory and one optional parameter)
      arg("component InvalidComp24 extends a.b.R { }",
        TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch (one mandatory and one optional parameter)
      arg("component InvalidComp25 extends a.b.R(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch (one mandatory and one optional parameter, key assign first)
      arg("component InvalidComp26 extends a.b.R(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument missing (one mandatory and one optional parameter, key assign second)
      arg("component InvalidComp27 extends a.b.R(p2 = true) { }",
        TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument missing and one optional argument type mismatch
      // (one mandatory and one optional parameter, key assign second)
      arg("component InvalidComp28 extends a.b.R(p2 = 1) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch
      arg("component InvalidComp29 extends a.b.R(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument and one optional argument type mismatch
      arg("component InvalidComp30 extends a.b.R(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory and one optional argument type mismatch
      arg("component InvalidComp31 extends a.b.R(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one optional argument (key assign first)
      arg("component InvalidComp32 extends a.b.R(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one mandatory argument type mismatch and one optional argument (key assign second)
      arg("component InvalidComp33 extends a.b.R(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one optional argument (key assign both)
      arg("component InvalidComp34 extends a.b.R(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one optional argument (key assign first twice)
      arg("component InvalidComp35 extends a.b.R(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one optional argument (key assign second twice)
      arg("component InvalidComp36 extends a.b.R(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one mandatory argument type mismatch and one optional argument (key assign both reverse order)
      arg("component InvalidComp38 extends a.b.R(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument and one optional argument (arg assigned twice, key assign first twice)
      arg("component InvalidComp40 extends a.b.R(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // one mandatory argument and one optional argument (arg assigned twice, key assign second twice)
      arg("component InvalidComp41 extends a.b.R(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one mandatory argument and one optional argument (repeated first key)
      arg("component InvalidComp42 extends a.b.R(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // one mandatory argument and one optional argument (arg assigned twice, repeated second key)
      arg("component InvalidComp43 extends a.b.R(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE, TOO_FEW_ARGUMENTS
      ),
      // one optional argument type mismatch (two optional parameters)
      arg("component InvalidComp44 extends a.b.Q(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch (two optional parameters, key assign first)
      arg("component InvalidComp45 extends a.b.Q(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one optional argument
      arg("component InvalidComp46 extends a.b.Q(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument and one optional argument type mismatch
      arg("component InvalidComp47 extends a.b.Q(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two optional argument type mismatch
      arg("component InvalidComp48 extends a.b.Q(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one optional argument (key assign first)
      arg("component InvalidComp49 extends a.b.Q(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one optional argument type mismatch and one optional argument (key assign second)
      arg("component InvalidComp50 extends a.b.Q(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one optional argument (key assign both)
      arg("component InvalidComp51 extends a.b.Q(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one optional argument (key assign first twice)
      arg("component InvalidComp52 extends a.b.Q(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one optional argument type mismatch and one optional argument (key assign second twice)
      arg("component InvalidComp53 extends a.b.Q(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one optional argument type mismatch and one optional argument (key assign both reverse order)
      arg("component InvalidComp55 extends a.b.Q(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two optional arguments (arg assigned twice, key assign first)
      arg("component InvalidComp56 extends a.b.Q(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // two optional arguments (arg assigned twice, key assign second)
      arg("component InvalidComp57 extends a.b.Q(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // two optional arguments (repeated first key)
      arg("component InvalidComp58 extends a.b.Q(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // two optional arguments (repeated second key)
      arg("component InvalidComp59 extends a.b.Q(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // missing two mandatory arguments (two mandatory parameters)
      arg("component InvalidComp60 extends a.b.P { }",
        TOO_FEW_ARGUMENTS
      ),
      // missing one mandatory argument (two mandatory parameters)
      arg("component InvalidComp61 extends a.b.P(1) { }",
        TOO_FEW_ARGUMENTS
      ),
      // missing one mandatory argument (two mandatory parameters, key assign first)
      arg("component InvalidComp62 extends a.b.P(p1 = 1) { }",
        TOO_FEW_ARGUMENTS
      ),
      // missing one mandatory argument (two mandatory parameters, key assign second)
      arg("component InvalidComp63 extends a.b.P(p2 = true) { }",
        TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch (two mandatory parameters)
      arg("component InvalidComp64 extends a.b.P(true) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch (two mandatory parameters, key assign first)
      arg("component InvalidComp65 extends a.b.P(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch (two mandatory parameters, key assign second)
      arg("component InvalidComp66 extends a.b.P(p2 = 1) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // one mandatory argument type mismatch and one mandatory argument
      arg("component InvalidComp67 extends a.b.P(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument and one mandatory argument type mismatch
      arg("component InvalidComp68 extends a.b.P(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two mandatory argument type mismatch
      arg("component InvalidComp69 extends a.b.P(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one mandatory argument (key assign first)
      arg("component InvalidComp70 extends a.b.P(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // one mandatory argument type mismatch and one mandatory argument (key assign second)
      arg("component InvalidComp71 extends a.b.P(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one mandatory argument (key assign both)
      arg("component InvalidComp72 extends a.b.P(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one mandatory argument (key assign first twice)
      arg("component InvalidComp73 extends a.b.P(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument type mismatch and one mandatory argument (key assign second twice)
      arg("component InvalidComp74 extends a.b.P(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one mandatory argument (key assign both reverse order)
      arg("component InvalidComp76 extends a.b.P(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two mandatory arguments (arg assigned twice, key assign first)
      arg("component InvalidComp77 extends a.b.P(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // two mandatory arguments (arg assigned twice, key assign second)
      arg("component InvalidComp78 extends a.b.P(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // two mandatory arguments (repeated first key)
      arg("component InvalidComp79 extends a.b.P(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // two mandatory arguments (repeated second key)
      arg("component InvalidComp80 extends a.b.P(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // one mandatory argument generic type mismatch
      arg("component InvalidComp81 extends a.b.M<int> (true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass parameter)
      arg("""
          component InvalidComp82 ( boolean q) extends a.b.M<int> (q) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter)
      arg("""
          component InvalidComp83<S> (S q) extends a.b.M<int> (q) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass parameter, same name)
      arg("""
          component InvalidComp84 ( boolean p) extends a.b.M<int> (p) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, same name)
      arg("""
          component InvalidComp85<S> (S p) extends a.b.M<int> (p) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, same type name)
      arg("""
          component InvalidComp86<T> (T q) extends a.b.M<int> (q) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name)
      arg("""
          component InvalidComp87<T> (T p) extends a.b.M<int> (p) {}""",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass parameter, key assign)
      arg("component InvalidComp88(boolean q) extends a.b.M<int> (p = q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, key assign)
      arg("component InvalidComp89<S> (S q) extends a.b.M<int> (p = q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass parameter, same name, key assign)
      arg("component InvalidComp90 ( boolean p) extends a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, key assign)
      arg("component InvalidComp91<S> (S p) extends a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // invalid key (key/value swapped: 'q' is not M's parameter name 'p'; looks like a typo for the
      // "pass generic parameter, same type name, key assign" type-mismatch case, i.e. "p = q")
      arg("component InvalidComp92<T> (T q) extends a.b.M<int> (q = p) { }",
        COMP_ARG_KEY_INVALID
      ),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name, key assign)
      arg("component InvalidComp93<T> (T p) extends a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // first of two mandatory arguments generic type mismatch
      arg("component InvalidComp94 extends a.b.O<int, boolean> (true, false){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // second of two mandatory arguments generic type mismatch
      arg("component InvalidComp95 extends a.b.O<int, boolean> (1, 2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two mandatory arguments generic type mismatch
      arg("component InvalidComp96 extends a.b.O<int, boolean> (true, 2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // first of two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp97 ( boolean q1, boolean q2) extends a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // second of two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp98 ( int q1, int q2) extends a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp99 ( boolean q1, int q2) extends a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // first of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component InvalidComp100<S, T> (S q1, T q2) extends a.b.O<T, T> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // second of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component InvalidComp101<S, T> (S q1, T q2) extends a.b.O<S, S> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Inheritance with two mandatory arguments, generic type mismatch (pass generic parameter)
      arg("component InvalidComp102<S, T> (S q1, T q2) extends a.b.O<T, S> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // Inheritance with two mandatory parameters (assign first key to first argument, second key invalid)
      arg("component InvalidComp103 extends a.b.P(p1 = 4, p4 = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // Inheritance with two mandatory parameters (assign first key to second argument, second key invalid)
      arg("component InvalidComp104 extends a.b.P(p2 = false, p4 = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // two mandatory arguments (arg assigned twice, key assign second) and two mandatory arguments generic type mismatch
      arg("component InvalidComp105 extends a.b.P(p2 = 1, 2), a.b.O <int, boolean>(true, 2){ }",
        COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModels4Refinement")
  void shouldReportError4Refinement(@NotNull String model,
                                    @NotNull Error... errors) {
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

  protected static Stream<Arguments> invalidModels4Refinement() {
    return Stream.of(
      // One argument too many, no abstraction parameter
      arg("component InvalidComp1 refines a.b.A(1) { }",
        TOO_MANY_ARGUMENTS
      ),
      // Two arguments too many
      arg("component InvalidComp2 refines a.b.A(1, 2) { }",
        TOO_MANY_ARGUMENTS
      ),
      // One argument too many, with non-existing key
      arg("component InvalidComp3 refines a.b.A(p = 1) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID
      ),
      // Missing single mandatory argument
      arg("component InvalidComp4 refines a.b.B { }",
        TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch
      arg("component InvalidComp5 refines a.b.B(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch (key assign)
      arg("component InvalidComp6 refines a.b.B(p = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One argument too many (one mandatory parameter)
      arg("component InvalidComp7 refines a.b.B(1, 2) { }",
        TOO_MANY_ARGUMENTS
      ),
      // One argument too many (one mandatory parameter, key assign first)
      arg("component InvalidComp8 refines a.b.B(p = 1, 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_VALUE_AFTER_KEY
      ),
      // One argument too many (one mandatory parameter, key assign second)
      arg("component InvalidComp9 refines a.b.B(1, p = 2) { }",
        COMP_ARG_MULTIPLE_VALUES, TOO_MANY_ARGUMENTS
      ),
      // One argument too many (one mandatory parameter, repeated key)
      arg("component InvalidComp10 refines a.b.B(p = 1, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter)
      arg("component InvalidComp11 refines a.b.B(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_MANY_ARGUMENTS
      ),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign first)
      arg("component InvalidComp12 refines a.b.B(p = true, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY, TOO_MANY_ARGUMENTS
      ),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign second)
      arg("component InvalidComp13 refines a.b.B(true, p = 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one argument too many (one mandatory parameter, repeated key)
      arg("component InvalidComp14 refines a.b.B(p = true, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // Parameter assignment with a wrong key
      arg("component InvalidComp15 refines a.b.B(np = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // One argument too many, wrong key
      arg("component InvalidComp16 refines a.b.B(1, np = 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID
      ),
      // One argument too many, wrong key, key-first assign
      arg("component InvalidComp17 refines a.b.B(np = 1, 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_KEY_INVALID, COMP_ARG_VALUE_AFTER_KEY
      ),
      // Wrong type
      arg("component InvalidComp18 refines a.b.C(1) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // First arg has wrong type
      arg("component InvalidComp19 refines a.b.D(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Second arg has wrong type
      arg("component InvalidComp20 refines a.b.D(1, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Both args have wrong type
      arg("component InvalidComp21 refines a.b.D(true, false) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // Second arg has wrong type, key-first assign
      arg("component InvalidComp22 refines a.b.D(p2 = 1, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // First arg is assigned via position, then again via key
      arg("component InvalidComp23 refines a.b.D(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // Second arg is assigned twice via key
      arg("component InvalidComp24 refines a.b.D(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // First arg has wrong type, second arg is correctly assigned by key
      arg("component InvalidComp25 refines a.b.D(true, p2 = 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // First arg is correctly assigned, second arg has wrong type
      arg("component InvalidComp26 refines a.b.D(1, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Arg key does not exist
      arg("component InvalidComp27 refines a.b.E(np = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // One optional argument type mismatch
      arg("component InvalidComp28 refines a.b.E(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch (key assign)
      arg("component InvalidComp29 refines a.b.E(p = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One argument too many (one optional parameter)
      arg("component InvalidComp30 refines a.b.E(1, 2) { }",
        TOO_MANY_ARGUMENTS
      ),
      // One argument too many (one optional parameter, key assign first)
      arg("component InvalidComp31 refines a.b.E(p = 1, 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_VALUE_AFTER_KEY
      ),
      // One argument too many (one optional parameter, key assign second)
      arg("component InvalidComp32 refines a.b.E(1, p = 2) { }",
        TOO_MANY_ARGUMENTS, COMP_ARG_MULTIPLE_VALUES
      ),
      // One argument too many (one optional parameter, repeated key)
      arg("component InvalidComp33 refines a.b.E(p = 1, p = 2) { }",
        TOO_MANY_ARGUMENTS, KEY_NOT_UNIQUE
      ),
      // One optional argument type mismatch and one argument too many
      arg("component InvalidComp34 refines a.b.E(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_MANY_ARGUMENTS
      ),
      // One optional argument type mismatch and one argument too many (one optional parameter, key assign first)
      arg("component InvalidComp35 refines a.b.E(p = true, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY, TOO_MANY_ARGUMENTS
      ),
      // One optional argument type mismatch and one argument too many (one optional parameter, key assign second)
      arg("component InvalidComp36 refines a.b.E(true, p = 2) { }",
        COMP_ARG_MULTIPLE_VALUES, TOO_MANY_ARGUMENTS, COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one argument too many (one optional parameter, repeated key)
      arg("component InvalidComp37 refines a.b.E(p = true, p = 2) { }",
        KEY_NOT_UNIQUE, TOO_MANY_ARGUMENTS
      ),
      // Two arguments too many (one optional parameter)
      arg("component InvalidComp38 refines a.b.E(1, 2, 3) { }",
        TOO_MANY_ARGUMENTS
      ),
      // Wrong arg type for optional parameter
      arg("component InvalidComp39 refines a.b.F(1) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Not even parantheses, although three params are mandatory
      arg("component InvalidComp40 refines a.b.H h { }",
        TOO_FEW_ARGUMENTS
      ),
      // One argument for three mandatory params
      arg("component InvalidComp41 refines a.b.H(1) { }",
        TOO_FEW_ARGUMENTS
      ),
      // Two arguments for three mandatory params
      arg("component InvalidComp42 refines a.b.H(1, 2) { }",
        TOO_FEW_ARGUMENTS
      ),
      // Six args for six parameters, but two args are for the same parameter (assignment by key)
      arg("component InvalidComp43 refines a.b.I(1, 2, p4 = 3, p4 = 4, p5 = 5, p6 = 6) { }",
        TOO_FEW_ARGUMENTS, KEY_NOT_UNIQUE
      ),
      // Position-based argument list is interrupted by key assignment
      arg("component InvalidComp44 refines a.b.I(1, p2 = 2, 3, 4, 5, 6) { }",
        COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_VALUE_AFTER_KEY,
        COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_VALUE_AFTER_KEY
      ),
      // Generic parameter type is bound to int, but argument is boolean
      arg("component InvalidComp45 refines a.b.M<java.lang.Integer> (true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Generic parameter type is bound to String, but argument is boolean
      arg("component InvalidComp46 refines a.b.M<java.lang.String> (true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Generic parameter type is bound to int, but argument is boolean. Assignment by key.
      arg("component InvalidComp47 refines a.b.M<java.lang.Integer> (p = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch
      arg("component InvalidComp48 refines a.b.M<int> (true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass parameter)
      arg("component InvalidComp49 ( boolean q)refines a.b.M<int> (q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter)
      arg("component InvalidComp50<S> (S q)refines a.b.M<int> (q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass parameter, same name)
      arg("component InvalidComp51 ( boolean p)refines a.b.M<int> (p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, same name)
      arg("component InvalidComp52<S> (S p)refines a.b.M<int> (p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, same type name)
      arg("component InvalidComp53<T> (T q)refines a.b.M<int> (q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, same type name)
      arg("component InvalidComp54<T> (T p)refines a.b.M<int> (p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass parameter, key assign)
      arg("component InvalidComp55 ( boolean q)refines a.b.M<int> (p = q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, key assign)
      arg("component InvalidComp56<S> (S q)refines a.b.M<int> (p = q) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass parameter, same name, key assign)
      arg("component InvalidComp57 ( boolean p)refines a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, key assign)
      arg("component InvalidComp58<S> (S p)refines a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Invalid key (key/value swapped: 'q' is not M's parameter name 'p'; looks like a typo for the
      // "pass generic parameter, same type name, key assign" type-mismatch case, i.e. "p = q")
      arg("component InvalidComp59<T> (T q)refines a.b.M<int> (q = p) { }",
        COMP_ARG_KEY_INVALID
      ),
      // One mandatory argument generic type mismatch (pass generic parameter, same name, same type name, key assign)
      arg("component InvalidComp60<T> (T p)refines a.b.M<int> (p = p) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Missing one mandatory argument (one mandatory and one optional parameter)
      arg("component InvalidComp61 refines a.b.R { }",
        TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch (one mandatory and one optional parameter)
      arg("component InvalidComp62 refines a.b.R(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch (one mandatory and one optional parameter, key assign first)
      arg("component InvalidComp63 refines a.b.R(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument missing (one mandatory and one optional parameter, key assign second)
      arg("component InvalidComp64 refines a.b.R(p2 = true) { }",
        TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument missing and one optional argument type mismatch
      // (one mandatory and one optional parameter, key assign second)
      arg("component InvalidComp65 refines a.b.R(p2 = 1) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch
      arg("component InvalidComp66 refines a.b.R(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument and one optional argument type mismatch
      arg("component InvalidComp67 refines a.b.R(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory and one optional argument type mismatch
      arg("component InvalidComp68 refines a.b.R(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one optional argument (key assign first)
      arg("component InvalidComp69 refines a.b.R(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One mandatory argument type mismatch and one optional argument (key assign second)
      arg("component InvalidComp70 refines a.b.R(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one optional argument (key assign both)
      arg("component InvalidComp71 refines a.b.R(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one optional argument (key assign first twice)
      arg("component InvalidComp72 refines a.b.R(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one optional argument (key assign second twice)
      arg("component InvalidComp73 refines a.b.R(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One mandatory argument type mismatch and one optional argument (key assign both reverse order)
      arg("component InvalidComp75 refines a.b.R(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument and one optional argument (arg assigned twice, key assign first twice)
      arg("component InvalidComp77 refines a.b.R(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // One mandatory argument and one optional argument (arg assigned twice, key assign second twice)
      arg("component InvalidComp78 refines a.b.R(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One mandatory argument and one optional argument (repeated first key)
      arg("component InvalidComp79 refines a.b.R(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // One mandatory argument and one optional argument (arg assigned twice, repeated second key)
      arg("component InvalidComp80 refines a.b.R(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE, TOO_FEW_ARGUMENTS
      ),
      // One optional argument type mismatch (two optional parameters)
      arg("component InvalidComp81 refines a.b.Q(true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch (two optional parameters, key assign first)
      arg("component InvalidComp82 refines a.b.Q(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one optional argument
      arg("component InvalidComp83 refines a.b.Q(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument and one optional argument type mismatch
      arg("component InvalidComp84 refines a.b.Q(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two optional argument type mismatch
      arg("component InvalidComp85 refines a.b.Q(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one optional argument (key assign first)
      arg("component InvalidComp86 refines a.b.Q(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One optional argument type mismatch and one optional argument (key assign second)
      arg("component InvalidComp87 refines a.b.Q(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one optional argument (key assign both)
      arg("component InvalidComp88 refines a.b.Q(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one optional argument (key assign first twice)
      arg("component InvalidComp89 refines a.b.Q(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One optional argument type mismatch and one optional argument (key assign second twice)
      arg("component InvalidComp90 refines a.b.Q(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One optional argument type mismatch and one optional argument (key assign both reverse order)
      arg("component InvalidComp92 refines a.b.Q(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two optional arguments (arg assigned twice, key assign first)
      arg("component InvalidComp93 refines a.b.Q(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // Two optional arguments (arg assigned twice, key assign second)
      arg("component InvalidComp94 refines a.b.Q(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // Two optional arguments (repeated first key)
      arg("component InvalidComp95 refines a.b.Q(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // Two optional arguments (repeated second key)
      arg("component InvalidComp96 refines a.b.Q(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // Missing two mandatory arguments (two mandatory parameters)
      arg("component InvalidComp97 refines a.b.P { }",
        TOO_FEW_ARGUMENTS
      ),
      // Missing one mandatory argument (two mandatory parameters)
      arg("component InvalidComp98 refines a.b.P(1) { }",
        TOO_FEW_ARGUMENTS
      ),
      // Missing one mandatory argument (two mandatory parameters, key assign first)
      arg("component InvalidComp99 refines a.b.P(p1 = 1) { }",
        TOO_FEW_ARGUMENTS
      ),
      // Missing one mandatory argument (two mandatory parameters, key assign second)
      arg("component InvalidComp100 refines a.b.P(p2 = true) { }",
        TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch (two mandatory parameters)
      arg("component InvalidComp101 refines a.b.P(true) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch (two mandatory parameters, key assign first)
      arg("component InvalidComp102 refines a.b.P(p1 = true) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch (two mandatory parameters, key assign second)
      arg("component InvalidComp103 refines a.b.P(p2 = 1) { }",
        COMP_ARG_TYPE_MISMATCH, TOO_FEW_ARGUMENTS
      ),
      // One mandatory argument type mismatch and one mandatory argument
      arg("component InvalidComp104 refines a.b.P(true, false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument and one mandatory argument type mismatch
      arg("component InvalidComp105 refines a.b.P(1, 2) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two mandatory argument type mismatch
      arg("component InvalidComp106 refines a.b.P(true, 2) { }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign first)
      arg("component InvalidComp107 refines a.b.P(p1 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign second)
      arg("component InvalidComp108 refines a.b.P(true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign both)
      arg("component InvalidComp109 refines a.b.P(p1 = true, p2 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign first twice)
      arg("component InvalidComp110 refines a.b.P(true, p1 = false) { }",
        COMP_ARG_MULTIPLE_VALUES, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign second twice)
      arg("component InvalidComp111 refines a.b.P(p2 = true, false) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // One mandatory argument type mismatch and one mandatory argument (key assign both reverse order)
      arg("component InvalidComp113 refines a.b.P(p2 = true, p1 = false) { }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two mandatory arguments (arg assigned twice, key assign first)
      arg("component InvalidComp114 refines a.b.P(1, p1 = 2) { }",
        COMP_ARG_MULTIPLE_VALUES
      ),
      // Two mandatory arguments (arg assigned twice, key assign second)
      arg("component InvalidComp115 refines a.b.P(p2 = 1, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY
      ),
      // Two mandatory arguments (repeated first key)
      arg("component InvalidComp116 refines a.b.P(p1 = 1, p1 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // Two mandatory arguments (repeated second key)
      arg("component InvalidComp117 refines a.b.P(p2 = 1, p2 = 2) { }",
        KEY_NOT_UNIQUE
      ),
      // First of two mandatory arguments generic type mismatch
      arg("component InvalidComp118 refines a.b.O<int, boolean> (true, false){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Second of two mandatory arguments generic type mismatch
      arg("component InvalidComp119 refines a.b.O<int, boolean> (1, 2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two mandatory arguments generic type mismatch
      arg("component InvalidComp120 refines a.b.O<int, boolean> (true, 2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // First of two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp121 ( boolean q1, boolean q2)refines a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Second of two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp122 ( int q1, int q2)refines a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two mandatory arguments generic type mismatch (pass parameter)
      arg("component InvalidComp123 ( boolean q1, int q2)refines a.b.O<int, boolean> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // First of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component InvalidComp124<S, T> (S q1, T q2)refines a.b.O<T, T> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Second of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component InvalidComp125<S, T> (S q1, T q2)refines a.b.O<S, S> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH
      ),
      // Two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component InvalidComp126<S, T> (S q1, T q2)refines a.b.O<T, S> (q1, q2){ }",
        COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      ),
      // Refinement with two mandatory parameters (assign first key to first argument, second key invalid)
      arg("component InvalidComp127 refines a.b.P(p1 = 4, p4 = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // Refinement with two mandatory parameters (assign first key to second argument, second key invalid)
      arg("component InvalidComp128 refines a.b.P(p2 = false, p4 = 1) { }",
        COMP_ARG_KEY_INVALID
      ),
      // Two mandatory arguments (arg assigned twice, key assign second) and two mandatory arguments generic type mismatch
      arg("component InvalidComp129 refines a.b.P(p2 = 1, 2), a.b.O <int, boolean>(true, 2) { }",
        COMP_ARG_VALUE_AFTER_KEY, COMP_ARG_TYPE_MISMATCH, COMP_ARG_TYPE_MISMATCH
      )
    );
  }
}

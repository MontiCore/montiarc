/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterHeritage;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.check.MontiArcTypeCalculator;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

public class ParameterHeritageTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
    setUpComponents();
  }

  @Override
  public void setUp() {
  }

  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }

  protected static void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B(int p) { }");
    compile("package a.b; component C(int p = 1) { }");
    compile("package a.b; component D(int p1, boolean p2 = false) { }");
    compile("package a.b; component E(int p1 = 1, boolean p2 = false) { }");
    compile("package a.b; component F(int p1, boolean p2) { }");
    compile("package a.b; component G<T>(T p) { }");
    compile("package a.b; component H<U, V>(U p1, V p2) { }");
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
    "component Comp10 extends a.b.C { }",
    // heritage with one optional parameter (assign value)
    "component Comp11 extends a.b.C(1) { }",
    // heritage with one optional parameter (pass parameter, different name)
    "component Comp12(int q) extends a.b.C(q) { }",
    // heritage with one optional parameter (pass parameter, same nape)
    "component Comp13(int p) extends a.b.C(p) { }",
    // heritage with one optional parameter (assign value via key)
    "component Comp14 extends a.b.C(p = 1) { }",
    // heritage with one optional parameter (pass parameter via key, different name)
    "component Comp15(int q) extends a.b.C(p = q) { }",
    // heritage with one optional parameter (pass parameter via key, same name)
    "component Comp16(int p) extends a.b.C(p = p) { }",
    // heritage with one mandatory and one optional parameter (default value)
    "component Comp17 extends a.b.D(1) { }",
    // heritage with one mandatory and one optional parameter (assign values)
    "component Comp18 extends a.b.D(1, false) { }",
    // heritage with one mandatory and one optional parameter (pass parameter, default value)
    "component Comp19(int p) extends a.b.D(p) { }",
    // heritage with one mandatory and one optional parameter (pass parameters)
    "component Comp20(int p1, boolean p2) extends a.b.D(p1, p2) { }",
    // heritage with one mandatory and one optional parameter (assign values via key in order)
    "component Comp21 extends a.b.D(p1 = 1, p2 = false) { }",
    // heritage with one mandatory and one optional parameter (assign values via key reverse order)
    "component Comp22 extends a.b.D(p2 = false, p1 = 1) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key, default value)
    "component Comp23(int p) extends a.b.D(p1 = p) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key in order)
     "component Comp24(int p1, boolean p2) extends a.b.D(p1 = p1, p2 = p2) { }",
    // heritage with one mandatory and one optional parameter (pass parameters via key reverse order)
    "component Comp25(int p1, boolean p2) extends a.b.D(p2 = p2, p1 = p1) { }",
    // heritage with two optional parameters (default values)
    "component Comp26 extends a.b.E { }",
    // heritage with two optional parameters (assign single value)
    "component Comp27 extends a.b.E(1) { }",
    // heritage with two optional parameters (assign values)
    "component Comp28 extends a.b.E(1, false) { }",
    // heritage with two optional parameters (pass single parameter)
    "component Comp29(int p) extends a.b.E(p) { }",
    // heritage with two optional parameters (pass parameters)
    "component Comp30(int p1, boolean p2) extends a.b.E(p1, p2) { }",
    // heritage with two optional parameters (assign first value via key)
    "component Comp31 extends a.b.E(p1 = 1) { }",
    // heritage with two optional parameters (assign second value via key)
    "component Comp32 extends a.b.E(p2 = false) { }",
    // heritage with two optional parameters (assign values via key in order)
    "component Comp33 extends a.b.E(p1 = 1, p2 = false) { }",
    // heritage with two optional parameters (assign values via key reverse order)
    "component Comp34 extends a.b.E(p2 = false, p1 = 1) { }",
    // heritage with two optional parameters (pass first parameter via key)
    "component Comp35(int p) extends a.b.E(p1 = p) { }",
    // heritage with two optional parameters (pass second parameter via key)
    "component Comp36(boolean p) extends a.b.E(p2 = p) { }",
    // heritage with two optional parameters (pass parameters via key in order)
    "component Comp37(int p1, boolean p2) extends a.b.E(p1 = p1, p2 = p2) { }",
    // heritage with two optional parameters (pass parameters via key reverse order)
    "component Comp38(int p1, boolean p2) extends a.b.E(p2 = p2, p1 = p1) { }",
    // heritage with two mandatory parameters (assign values)
    "component Comp39 extends a.b.F(1, false) { }",
    // heritage with two mandatory parameters (pass parameters)
    "component Comp40(int p1, boolean p2) extends a.b.F(p1, p2) { }",
    // heritage with two mandatory parameters (assign values via key in order)
    "component Comp41 extends a.b.F(p1 = 1, p2 = false) { }",
    // heritage with two mandatory parameters (assign values via key reverse order)
    "component Comp42 extends a.b.F(p2 = false, p1 = 1) { }",
    // heritage with two mandatory parameters (pass parameters via key in order)
    "component Comp43(int p1, boolean p2) extends a.b.F(p1 = p1, p2 = p2) { }",
    // heritage with two mandatory parameters (pass parameters via key reverse order)
    "component Comp44(int p1, boolean p2) extends a.b.F(p2 = p2, p1 = p1) { }",
    // heritage with one mandatory generic parameter (bind type, assign value)
    "component Comp45 extends a.b.G<int>(1) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter)
    "component Comp46(int p) extends a.b.G<int>(p) { }",
    // heritage with one mandatory generic parameter (pass type, pass parameter)
    "component Comp47<T>(T p) extends a.b.G<T>(p) { }",
    // heritage with one mandatory generic parameter (bind type, assign value via key)
    "component Comp48 extends a.b.G<int>(p = 1) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter via key, different name)
    "component Comp49(int q) extends a.b.G<int>(p = q) { }",
    // heritage with one mandatory generic parameter (bind type, pass parameter via key, same name)
    "component Comp50(int p) extends a.b.G<int>(p = p) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, different name, same type name)
    "component Comp51<T>(T q) extends a.b.G<T>(p = q) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, same name, same type name)
    "component Comp52<T>(T p) extends a.b.G<T>(p = p) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, different name, different type name)
    "component Comp53<S>(S q) extends a.b.G<S>(p = q) { }",
    // heritage with one mandatory generic parameter (pass parameter via key, same name, different type name)
    "component Comp54<S>(S p) extends a.b.G<S>(p = p) { }",
    // heritage with two mandatory generic parameters (bind type, assign values)
    "component Comp55 extends a.b.H<int, boolean>(1, false) { }",
    // heritage with two mandatory generic parameters (bind type, pass parameters)
    "component Comp56(int p1, boolean p2) extends a.b.H<int, boolean>(p1, p2) { }",
    // heritage with two mandatory generic parameters (pass type, pass parameters)
    "component Comp57<U, V>(U p1, V p2) extends a.b.H<U, V>(p1, p2) { }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterHeritage(new MontiArcTypeCalculator()));

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
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterHeritage(new MontiArcTypeCalculator()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // one argument too many, no super parameter
      arg("component Comp1 extends a.b.A(1) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // missing single mandatory argument
      arg("component Comp2 extends a.b.B { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch
      arg("component Comp3 extends a.b.B(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch (key assign)
      arg("component Comp4 extends a.b.B(p = true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one argument too many (one mandatory parameter)
      arg("component Comp5 extends a.b.B(1, 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one argument too many (one mandatory parameter, key assign first)
      arg("component Comp6 extends a.b.B(p = 1, 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS,
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one argument too many (one mandatory parameter, key assign second)
      arg("component Comp7 extends a.b.B(1, p = 2) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one argument too many (one mandatory parameter, repeated key)
      arg("component Comp8 extends a.b.B(p = 1, p = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter)
      arg("component Comp9 extends a.b.B(true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign first)
      arg("component Comp10 extends a.b.B(p = true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, key assign second)
      arg("component Comp11 extends a.b.B(true, p = 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS,
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one mandatory argument type mismatch and one argument too many (one mandatory parameter, repeated key)
      arg("component Comp12 extends a.b.B(p = true, p = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
        // one optional argument type mismatch
      arg("component Comp13 extends a.b.C(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch (key assign)
      arg("component Comp14 extends a.b.C(p = true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one argument too many (one optional parameter)
      arg("component Comp15 extends a.b.C(1, 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one argument too many (one optional parameter, key assign first)
      arg("component Comp16 extends a.b.C(p = 1, 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS,
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one argument too many (one optional parameter, key assign second)
      arg("component Comp17 extends a.b.C(1, p = 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS,
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one argument too many (one optional parameter, repeated key)
      arg("component Comp18 extends a.b.C(p = 1, p = 2) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS,
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // one optional argument type mismatch and one argument too many
      arg("component Comp19 extends a.b.C(true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign first)
      arg("component Comp20 extends a.b.C(p = true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch and one argument too many (one optional parameter, key assign second)
      arg("component Comp21 extends a.b.C(true, p = 2) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // one optional argument type mismatch and one argument too many (one optional parameter, repeated key)
      arg("component Comp22 extends a.b.C(p = true, p = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE,
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // two arguments too many (one optional parameter)
      arg("component Comp23 extends a.b.C(1, 2, 3) { }",
        ArcError.HERITAGE_TOO_MANY_ARGUMENTS),
      // missing one mandatory argument (one mandatory and one optional parameter)
      arg("component Comp24 extends a.b.D { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (one mandatory and one optional parameter)
      arg("component Comp25 extends a.b.D(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch (one mandatory and one optional parameter, key assign first)
      arg("component Comp26 extends a.b.D(p1 = true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument missing (one mandatory and one optional parameter, key assign second)
      arg("component Comp27 extends a.b.D(p2 = true) { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument missing and one optional argument type mismatch
      // (one mandatory and one optional parameter, key assign second)
      arg("component Comp28 extends a.b.D(p2 = 1) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch
      arg("component Comp29 extends a.b.D(true, false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one optional argument type mismatch
      arg("component Comp30 extends a.b.D(1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory and one optional argument type mismatch
      arg("component Comp31 extends a.b.D(true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign first)
      arg("component Comp32 extends a.b.D(p1 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one optional argument (key assign second)
      arg("component Comp33 extends a.b.D(true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign both)
      arg("component Comp34 extends a.b.D(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp35 extends a.b.D(true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one mandatory argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp36 extends a.b.D(p2 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp37 extends a.b.D(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp38 extends a.b.D(p2 = true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one optional argument (repeated key)
      arg("component Comp39 extends a.b.D(p1 = 1, p1 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // one mandatory argument and one optional argument (arg assigned twice, key assign first twice)
      arg("component Comp40 extends a.b.D(1, p1 = 2) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one mandatory argument and one optional argument (arg assigned twice, key assign second twice)
      arg("component Comp41 extends a.b.D(p2 = 1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument and one optional argument (arg assigned twice, repeated first key)
      arg("component Comp42 extends a.b.D(p1 = 1, p1 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // one mandatory argument and one optional argument (arg assigned twice, repeated second key)
      arg("component Comp43 extends a.b.D(p2 = 1, p2 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE,
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one optional argument type mismatch (two optional parameters)
      arg("component Comp44 extends a.b.E(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch (two optional parameters, key assign first)
      arg("component Comp45 extends a.b.E(p1 = true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument
      arg("component Comp46 extends a.b.E(true, false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument and one optional argument type mismatch
      arg("component Comp47 extends a.b.E(1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two optional argument type mismatch
      arg("component Comp48 extends a.b.E(true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign first)
      arg("component Comp49 extends a.b.E(p1 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one optional argument type mismatch and one optional argument (key assign second)
      arg("component Comp50 extends a.b.E(true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign both)
      arg("component Comp51 extends a.b.E(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign first twice)
      arg("component Comp52 extends a.b.E(true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one optional argument type mismatch and one optional argument (key assign second twice)
      arg("component Comp53 extends a.b.E(p2 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one optional argument type mismatch and one optional argument (key assign both in order)
      arg("component Comp54 extends a.b.E(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one optional argument type mismatch and one optional argument (key assign both reverse order)
      arg("component Comp55 extends a.b.E(p2 = true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two optional arguments (arg assigned twice, key assign first)
      arg("component Comp56 extends a.b.E(1, p1 = 2) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // two optional arguments (arg assigned twice, key assign second)
      arg("component Comp57 extends a.b.E(p2 = 1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // two optional arguments (repeated first key)
      arg("component Comp58 extends a.b.E(p1 = 1, p1 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // two optional arguments (repeated second key)
      arg("component Comp59 extends a.b.E(p2 = 1, p2 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // missing two mandatory arguments (two mandatory parameters)
      arg("component Comp60 extends a.b.F { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters)
      arg("component Comp61 extends a.b.F(1) { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters, key assign first)
      arg("component Comp62 extends a.b.F(p1 = 1) { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // missing one mandatory argument (two mandatory parameters, key assign second)
      arg("component Comp63 extends a.b.F(p2 = true) { }",
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters)
      arg("component Comp64 extends a.b.F(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters, key assign first)
      arg("component Comp65 extends a.b.F(p1 = true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch (two mandatory parameters, key assign second)
      arg("component Comp66 extends a.b.F(p2 = 1) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_TOO_FEW_ARGUMENTS),
      // one mandatory argument type mismatch and one mandatory argument
      arg("component Comp67 extends a.b.F(true, false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument and one mandatory argument type mismatch
      arg("component Comp68 extends a.b.F(1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two mandatory argument type mismatch
      arg("component Comp69 extends a.b.F(true, 2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign first)
      arg("component Comp70 extends a.b.F(p1 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one mandatory argument (key assign second)
      arg("component Comp71 extends a.b.F(true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign both)
      arg("component Comp72 extends a.b.F(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign first twice)
      arg("component Comp73 extends a.b.F(true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // one mandatory argument type mismatch and one mandatory argument (key assign second twice)
      arg("component Comp74 extends a.b.F(p2 = true, false) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // one mandatory argument type mismatch and one mandatory argument (key assign both in order)
      arg("component Comp75 extends a.b.F(p1 = true, p2 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument type mismatch and one mandatory argument (key assign both reverse order)
      arg("component Comp76 extends a.b.F(p2 = true, p1 = false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments (arg assigned twice, key assign first)
      arg("component Comp77 extends a.b.F(1, p1 = 2) { }",
        ArcError.HERITAGE_COMP_ARG_MULTIPLE_VALUES),
      // two mandatory arguments (arg assigned twice, key assign second)
      arg("component Comp78 extends a.b.F(p2 = 1, 2) { }",
        ArcError.HERITAGE_COMP_ARG_VALUE_AFTER_KEY),
      // two mandatory arguments (repeated first key)
      arg("component Comp79 extends a.b.F(p1 = 1, p1 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // two mandatory arguments (repeated second key)
      arg("component Comp80 extends a.b.F(p2 = 1, p2 = 2) { }",
        ArcError.HERITAGE_KEY_NOT_UNIQUE),
      // one mandatory argument generic type mismatch
      arg("component Comp81 extends a.b.G<int>(true) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter)
      arg("component Comp82(boolean q) extends a.b.G<int>(q) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter)
      arg("component Comp83<S>(S q) extends a.b.G<int>(q) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, same name)
      arg("component Comp84(boolean p) extends a.b.G<int>(p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name)
      arg("component Comp85<S>(S p) extends a.b.G<int>(p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same type name)
      arg("component Comp86<T>(T q) extends a.b.G<int>(q) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name)
      arg("component Comp87<T>(T p) extends a.b.G<int>(p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, key assign)
      arg("component Comp88(boolean q) extends a.b.G<int>(p = q) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, key assign)
      arg("component Comp89<S>(S q) extends a.b.G<int>(p = q) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass parameter, same name, key assign)
      arg("component Comp90(boolean p) extends a.b.G<int>(p = p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, key assign)
      arg("component Comp91<S>(S p) extends a.b.G<int>(p = p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // one mandatory argument generic type mismatch (pass generic parameter, same type name, key assign)
      arg("component Comp92<T>(T q) extends a.b.G<int>(q = p) { }",
        ArcError.HERITAGE_COMP_ARG_KEY_INVALID),
      // one mandatory argument generic type mismatch (pass generic parameter, same name, same type name, key assign)
      arg("component Comp93<T>(T p) extends a.b.G<int>(p = p) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch
      arg("component Comp94 extends a.b.H<int, boolean>(true, false) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch
      arg("component Comp95 extends a.b.H<int, boolean>(1 ,2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch
      arg("component Comp96 extends a.b.H<int, boolean>(true ,2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp97(boolean q1, boolean q2) extends a.b.H<int, boolean>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp98(int q1, int q2) extends a.b.H<int, boolean>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch (pass parameter)
      arg("component Comp99(boolean q1, int q2) extends a.b.H<int, boolean>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // first of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp100<S, T>(S q1, T q2) extends a.b.H<T, T>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // second of two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp101<S, T>(S q1, T q2) extends a.b.H<S, S>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // two mandatory arguments generic type mismatch (pass generic parameter)
      arg("component Comp102<S, T>(S q1, T q2) extends a.b.H<T, S>(q1, q2) { }",
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH,
        ArcError.HERITAGE_COMP_ARG_TYPE_MISMATCH),
      // heritage with two mandatory parameters (assign first key to first argument, second key invalid)
      arg("component Comp42 extends a.b.F(p1 = 4, p4 = 1) { }",
        ArcError.HERITAGE_COMP_ARG_KEY_INVALID),
        // heritage with two mandatory parameters (assign first key to second argument, second key invalid)
      arg("component Comp42 extends a.b.F(p2 = false, p4 = 1) { }",
        ArcError.HERITAGE_COMP_ARG_KEY_INVALID)

    );
  }
}

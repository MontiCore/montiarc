/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConfigurationParameterAssignment;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.TypeRelations;
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

/**
 * The class under test is {@link ConfigurationParameterAssignment}.
 */
public class ConfigurationParameterAssignmentTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
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
    //"component Comp46 { a.b.K k(java.util.Arrays.asList(1)); }",
    "component Comp47 { a.b.L l; }",
    //"component Comp48 { a.b.L l(java.util.Arrays.asList(1)); }",
    //"component Comp49 { a.b.L l(p = java.util.Arrays.asList(1)); }",
    "component Comp50 { a.b.M<java.lang.Integer> m(1); }",
    "component Comp51 { a.b.M<java.lang.Integer> m(java.lang.Integer.Integer(1)); }",
    //"component Comp52 { a.b.N<java.lang.Integer> m(java.util.Arrays.asList(1)); }",
    //"component Comp53 { a.b.N<java.lang.Integer> m(java.util.Collections.emptyList()); }"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcTypeCalculator(), new TypeRelations()));

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
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConfigurationParameterAssignment(new MontiArcTypeCalculator(), new TypeRelations()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
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
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp8 { a.b.B b(p = 1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY),
      arg("component Comp9 { a.b.B b(1, np = 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp10 { a.b.B b(np = 1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS,
        ArcError.COMP_ARG_KEY_INVALID,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY),
      arg("component Comp11 { a.b.B b(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp12 { a.b.B b(true, 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp13 { a.b.C c(1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp14 { a.b.C c(java.lang.Integer.Integer(1)); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp15 { a.b.D d(true, 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp16 { a.b.D d(1, false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp17 { a.b.D d(true, false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      //arg("component Comp18 { a.b.D d(p2 = 1, false); }",
      //  ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY),
      //arg("component Comp19 { a.b.D d(p2 = 1, p2 = 2); }",
      //  ArcError....),
      arg("component Comp20 { a.b.D d(true, p2 = 2); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp21 { a.b.D d(1, p2 = false); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp22 { a.b.E e(1, 2); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp23 { a.b.E e(p = 1, p = 2); }",
        ArcError.TOO_MANY_ARGUMENTS),
      arg("component Comp24{ a.b.E e(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp25 { a.b.E e(p = true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp26 { a.b.E e(np = 1); }",
        ArcError.COMP_ARG_KEY_INVALID),
      arg("component Comp27 { a.b.F f(1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp28 { a.b.G g(p2 = java.lang.String.String(), p1 = 1); }",
        ArcError.COMP_ARG_TYPE_MISMATCH,
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp29 { a.b.H h; }",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp30 { a.b.H h(1); }",
        ArcError.TOO_FEW_ARGUMENTS),
      arg("component Comp31 { a.b.H h(1, 2); }",
        ArcError.TOO_FEW_ARGUMENTS),
      //arg("component Comp32 { a.b.I i(1, 2, p4 = 3, p4 = 4, p5 = 5, p6 = 6); }",
      //  ArcError.COMP_ARG_KEY_INVALID)
      arg("component Comp33 { a.b.I i(1, p2 = 2, 3, 4, 5, 6); }",
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY,
        ArcError.POSITIONAL_ASSIGNMENT_AFTER_KEY),
      arg("component Comp34 { a.b.M<java.lang.Integer> m(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp35 { a.b.M<java.lang.String> m(true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH),
      arg("component Comp36 { a.b.M<java.lang.Integer> m(p = true); }",
        ArcError.COMP_ARG_TYPE_MISMATCH)
    );
  }
}
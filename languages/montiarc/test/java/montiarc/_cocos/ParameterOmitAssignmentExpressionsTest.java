/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ParameterOmitAssignmentExpressions;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;
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
 * The class under test is {@link ParameterOmitAssignmentExpressions}.
 */
public class ParameterOmitAssignmentExpressionsTest extends MontiArcAbstractTest {

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
  public void setUp() { }

  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }

  protected static void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B(int p) { }");
    compile("package a.b; component C(int p1, int p2) { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { a.b.A a(); }",
    "component Comp2 { a.b.B b(1); }",
    "component Comp3 { a.b.B b(p = 1); }",
    "component Comp4 { a.b.C c(1, 2); }",
    "component Comp5 { a.b.C c(1, p2 = 2); }",
    "component Comp6 { a.b.C c(p1 = 1, p2 = 2); }",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ParameterOmitAssignmentExpressions());

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
    checker.addCoCo(new ParameterOmitAssignmentExpressions());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { a.b.B b(p = p = 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp2 { a.b.B b(p = p += 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp3 { a.b.B b(p = p -= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp4 { a.b.B b(p = p *= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp5 { a.b.B b(p = p /= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp6 { a.b.B b(p = p /= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp7 { a.b.B b(p = p &= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp8 { a.b.B b(p = p |= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp9 { a.b.B b(p = p ^= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp10 { a.b.B b(p = p >>= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp11 { a.b.B b(p = p <<= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp12 { a.b.B b(p = p %= 1); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp13 { a.b.C c(p1 = 1, p2 = p2 = 2); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp14 { a.b.C c(p1 = p1 = 1, p2 = 2); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp15 { a.b.C c(p1 = p1 = 1, p2 = p2 = 2); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT,
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp16 { a.b.C c(p1 = p1 = 1, p2 = p2 = 2); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT,
        ArcError.COMP_ARG_MULTI_ASSIGNMENT),
      arg("component Comp17 { a.b.C c(p1 = p2 = 1, p1 = p2 = 2); }",
        ArcError.COMP_ARG_MULTI_ASSIGNMENT,
        ArcError.COMP_ARG_MULTI_ASSIGNMENT)
    );
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.SubPortsConnected;
import com.google.common.base.Preconditions;
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
 * The class under test is {@link SubPortsConnected}.
 */
public class SubPortsConnectedTest extends MontiArcAbstractTest {

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
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { port in int i; port out int o; }");
    compile("package a.b; component E { port in int i1, i2; }");
    compile("package a.b; component F { port out int o1, o2; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { }",
    "component Comp2 { " +
      "a.b.A a; " +
      "}",
    "component Comp3 { " +
      "port in int i; " +
      "a.b.B b; " +
      "i -> b.i; " +
      "}",
    "component Comp4 { " +
      "port out int o; " +
      "a.b.C c; " +
      "c.o -> o; " +
      "}",
    "component Comp5 { " +
      "port out int o1, o2; " +
      "a.b.C c; " +
      "c.o -> o1, o2; " +
      "}",
    "component Comp6 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.D d; " +
      "i -> d.i; " +
      "d.o -> o; " +
      "}",
    "component Comp7 { " +
      "port in int i; " +
      "a.b.E e; " +
      "i -> e.i1; " +
      "i -> e.i2; " +
      "}",
    "component Comp8 { " +
      "port in int i; " +
      "a.b.E e; " +
      "i -> e.i1, e.i2; " +
      "}",
    "component Comp9 { " +
      "port out int o1, o2; " +
      "a.b.F f; " +
      "f.o1 -> o1; " +
      "f.o2 -> o2; " +
      "}",
    "component Comp10 { " +
      "port in int i; " +
      "port out int o; " +
      "component Inner {" +
      "port in int i; " +
      "port out int o; " +
      "}" +
      "Inner sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new SubPortsConnected());

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
    checker.addCoCo(new SubPortsConnected());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { " +
          "a.b.B b; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp2 { " +
          "a.b.C c; " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp3 { " +
          "a.b.D d; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp4 { " +
          "a.b.B b; " +
          "a.b.C c; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp5 { " +
          "a.b.E e; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp6 { " +
          "a.b.F f; " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      arg("component Comp7 { " +
          "a.b.B b1, b2; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED),
      arg("component Comp8 { " +
          "a.b.C c1, c2; " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED)
    );
  }
}

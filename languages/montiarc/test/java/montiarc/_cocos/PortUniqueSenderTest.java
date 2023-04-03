/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortUniqueSender;
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
 * The class under test is {@link PortUniqueSender}.
 */
public class PortUniqueSenderTest extends MontiArcAbstractTest {

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
    compile("package a.b; component A { port in int i; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port in int i; port out int o; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no ports or connectors
    "component Comp1 { }",
    // single in port forward
    "component Comp2 { " +
      "port in int i; " +
      "a.b.A a; " +
      "i -> a.i; " +
      "}",
    // multiple targets in port forward
    "component Comp3 { " +
      "port in int i; " +
      "a.b.A a1, a2; " +
      "i -> a1.i, a2.i; " +
      "}",
    // multiple in port forwards (different targets)
    "component Comp4 { " +
      "port in int i; " +
      "a.b.A a1, a2; " +
      "i -> a1.i;" +
      "i -> a2.i; " +
      "}",
    // single out port forward
    "component Comp5 { " +
      "port out int o; " +
      "a.b.B b; " +
      "b.o -> o; " +
      "}",
    // multiple targets out port forward
    "component Comp6 { " +
      "port out int o1, o2; " +
      "a.b.B b; " +
      "b.o -> o1, o2; " +
      "}",
    // multiple out port forwards (different targets)
    "component Comp7 { " +
      "port out int o1, o2; " +
      "a.b.B b1, b2; " +
      "b1.o -> o1;" +
      "b2.o -> o2; " +
      "}",
    // single hidden connector
    "component Comp8 { " +
      "a.b.A a;" +
      "a.b.B b; " +
      "b.o -> a.i; " +
      "}",
    // multiple targets hidden connector
    "component Comp9 { " +
      "a.b.A a1, a2;" +
      "a.b.B b; " +
      "b.o -> a1.i, a2.i; " +
      "}",
    // multiple hidden connectors (different targets)
    "component Comp10 { " +
      "a.b.A a1, a2;" +
      "a.b.B b1, b2; " +
      "b1.o -> a1.i; " +
      "b2.o -> a2.i; " +
      "}",
    // multiple connectors (different targets)
    "component Comp11 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C c; " +
      "i -> c.i; " +
      "c.o -> o; " +
      "}"
  })
  public void shouldReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender());

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
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortUniqueSender());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // multiple in port forward (same target)
      arg("component Comp1 {" +
          "port in int i1, i2; " +
          "a.b.A a; " +
          "i1 -> a.i; " +
          "i2 -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp2 {" +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "b1.o -> o; " +
          "b2.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant in port forward
      arg("component Comp3 { " +
          "port in int i; " +
          "a.b.A a; " +
          "i -> a.i; " +
          "i -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // redundant out port forward
      arg("component Comp4 { " +
          "port out int o; " +
          "a.b.B b; " +
          "b.o -> o; " +
          "b.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple targets hidden connector (same target)
      arg("component Comp5 {" +
          "a.b.B b; " +
          "a.b.A a; " +
          "b.o -> a.i, a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple hidden connector (same target)
      arg("component Comp6 {" +
          "a.b.B b1, b2; " +
          "a.b.A a; " +
          "b1.o -> a.i; " +
          "b2.o -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER),
      // three in port forward (same target)
      arg("component Comp7 {" +
          "port in int i1, i2, i3; " +
          "a.b.A a; " +
          "i1 -> a.i; " +
          "i2 -> a.i; " +
          "i3 -> a.i; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER),
      // multiple out port forward (same target)
      arg("component Comp8 {" +
          "port out int o; " +
          "a.b.B b1, b2, b3; " +
          "b1.o -> o; " +
          "b2.o -> o; " +
          "b3.o -> o; " +
          "}",
        ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER)
    );
  }
}
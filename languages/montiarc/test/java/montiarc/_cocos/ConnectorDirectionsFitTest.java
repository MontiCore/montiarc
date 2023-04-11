/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorDirectionsFit;
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
 * The class under test is {@link ConnectorDirectionsFit}.
 */
public class ConnectorDirectionsFitTest extends MontiArcAbstractTest {

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
    compile("package a.b; component B { port in int i; port out int o; }");
    compile("package a.b; component C { port in int i; port <<delayed>> out int o; }");
    compile("package a.b; component D { port in int i1, i2; port out int o; }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { " +
      "a.b.A a(); " +
      "}",
    "component Comp2 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.B b; " +
      "i -> b.i; " +
      "b.o -> o; " +
      "}",
    "component Comp3 { " +
      "a.b.B b; " +
      "a.b.C c; " +
      "b.o -> c.i; " +
      "c.o -> b.i; " +
      "}",
    "component Comp4 { " +
      "port in int i; " +
      "port out int o; " +
      "component Inner { " +
      "port in int i; " +
      "port out int o; " +
      "} " +
      "Inner sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "}",
    "component Comp5 { " +
      "port in int i; " +
      "port out int o1, o2; " +
      "a.b.D d; " +
      "i -> d.i1; " +
      "i -> d.i2; " +
      "d.o -> o1, o2; " +
      "}",
    "component Comp6 { " +
      "port in int i; " +
      "port out int o; " +
      "i -> o; " +
      "}",
    "component Comp7 { " +
      "port in int i; " +
      "port out int o1, o2; " +
      "i -> o1, o2; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorDirectionsFit());

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
    checker.addCoCo(new ConnectorDirectionsFit());

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
          "port in int i1; " +
          "port in int i2; " +
          "a.b.B b; " +
          "i1 -> b.i; " +
          "i2 -> b.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp2 { " +
          "port out int o1; " +
          "port out int o2; " +
          "a.b.B b; " +
          "b.i -> o1; " +
          "b.o -> o2; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp3 { " +
          "port in int i1, i2; " +
          "i1 -> i2; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp4 { " +
          "port out int o1, o2; " +
          "o1 -> o2; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp5 { " +
          "port in int i1, i2; " +
          "port out int o; " +
          "i1 -> i2, o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp6 { " +
          "port in int i1, i2; " +
          "port out int o; " +
          "i1 -> o, i2; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp7 { " +
          "port in int i1, i2, i3; " +
          "i1 -> i2, i3; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp8 { " +
          "port in int i1, i2, i3, i4; " +
          "i1 -> i3; " +
          "i2 -> i4; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp9 { " +
          "port in int i1, i2; " +
          "a.b.B b1, b2; " +
          "i1 -> b1.i; " +
          "i2 -> b2.i; " +
          "b1.o -> b2.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp10 { " +
          "port out int o1, o2; " +
          "a.b.B b1, b2; " +
          "b1.o -> o1; " +
          "b2.o -> o2; " +
          "b1.i -> b2.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp11 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "i -> b1.i; " +
          "b2.i -> b1.o; " +
          "b2.o -> p; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp12 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "o -> b1.o; " +
          "b1.i -> b2.o; " +
          "b1.i -> i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp13 { " +
          "port in int i; " +
          "port out int o; " +
          "component Inner {" +
          "port in int i; " +
          "port out int o; " +
          "} " +
          "Inner sub; " +
          "i -> sub.o; " +
          "o -> sub.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH)
    );
  }
}

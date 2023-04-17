/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTypeFits;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.TypeRelations;
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

public class PortHeritageTypeFitsTest extends MontiArcAbstractTest {

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
    compile("package a.b; component C<T> { port in T i; port out T o; } ");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // no heritage
    "component Comp1 { }",
    // heritage without ports
    "component Comp2 extends a.b.A { }",
    // heritage adding ports, without super ports
    "component Comp3 extends a.b.A { " +
      "port in int i; " +
      "port out int o; " +
      "}",
    // heritage with adding ports, with super ports
    "component Comp4 extends a.b.B { }",
    // heritage with adding ports, with super ports
    "component Comp5 extends a.b.B { " +
      "port in int i2; " +
      "port out int o2; " +
      "}",
    // heritage with overriding super ports (same type)
    "component Comp6 extends a.b.B { " +
      "port in int i; " +
      "port out int o; " +
      "}",
    // heritage with overriding super ports (incoming subtype)
    "component Comp7 extends a.b.B { " +
      "port in byte i; " +
      "port out int o; " +
      "}",
    // heritage with overriding super ports (outgoing supertype)
    "component Comp8 extends a.b.B { " +
      "port in int i; " +
      "port out long o; " +
      "}",
    // heritage with overriding super ports (incoming and outgoing)
    "component Comp9 extends a.b.B { " +
      "port in byte i; " +
      "port out long o; " +
      "}",
    // heritage with overriding generic typed ports (matching type)
    "component Comp10 extends a.b.C<int> { " +
      "port in int i;" +
      "port out int o; " +
      "}",
    // heritage with overriding generic typed ports (matching generic type)
    "component Comp11<T> extends a.b.C<T> { " +
      "port in T i;" +
      "port out T o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTypeFits(new TypeRelations()));

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
    checker.addCoCo(new PortHeritageTypeFits(new TypeRelations()));

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // heritage with overriding super ports (incoming supertype)
      arg("component Comp1 extends a.b.B { " +
          "port in double i; " +
          "port out int o; " +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage with overriding super ports (outgoing subtype)
      arg("component Comp2 extends a.b.B { " +
          "port in int i; " +
          "port out byte o; " +
          "}",
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage with incoming and outgoing port type mismatch
      arg("component Comp3 extends a.b.B { " +
          "port in double i; " +
          "port out byte o; " +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH,
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage with overriding generic typed ports (incoming supertype)
      arg("component Comp4 extends a.b.C<int> { " +
          "port in double i; " +
          "port out int o; " +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage with overriding super ports (outgoing subtype)
      arg("component Comp5 extends a.b.C<int> { " +
          "port in int i; " +
          "port out byte o; " +
          "}",
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH)
    );
  }
}

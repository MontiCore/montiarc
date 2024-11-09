/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortsConnected;
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

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortsConnected}.
 */
public class PortsConnectedTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  protected void setUpComponents() {
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
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortsConnected());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("component Comp1 { " +
          "port in int i; " +
          "a.b.A a; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      arg("component Comp2 { " +
          "port out int o; " +
          "a.b.A a; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      arg("component Comp3 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.A a; " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED),
      arg("component Comp4 { " +
          "port in int i1, i2; " +
          "a.b.A a; " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.IN_PORT_UNUSED),
      arg("component Comp5 { " +
          "port out int o1, o2; " +
          "a.b.A a; " +
          "}",
        ArcError.OUT_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED),
      arg("component Comp6 { " +
          "port in int i; " +
          "port out int o; " +
          "component Inner { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.A a; " +
          "} " +
          "Inner sub; " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.IN_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED)
    );
  }
}

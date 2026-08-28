/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTimingFits;
import com.google.common.base.Preconditions;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.INVALID_PORT_TIMING_OVERRIDE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link PortHeritageTimingFits}.
 */
class PortHeritageTimingFitsTest extends MontiArcTestBase {

  @BeforeEach
  protected void initSymbols() {
    setUpComponents();
  }

  protected void setUpComponents() {
    // Parent components with outgoing ports
    compile("package a.b; component A { port sync out int os; }");
    compile("package a.b; component B { port out int o; }");
    compile("package a.b; component C { port out int o1, o2; }");
    compile("package a.b; component D { port sync out int os1, os2; }");
    compile("package a.b; component E { port sync out int os1, out int o2; }");
    compile("package a.b; component F extends A { port sync out int os; }");
    compile("package a.b; component G extends B { port out int o; }");

    // Parent components with incoming ports
    compile("package a.b; component H { port sync in int i; }");
    compile("package a.b; component I { port in int i; }");
    compile("package a.b; component J { port in int i1, i2; }");
    compile("package a.b; component K { port sync in int is1, is2; }");
    compile("package a.b; component L { port sync in int is1, in int i2; }");
    compile("package a.b; component M extends H { port sync in int i; }");
    compile("package a.b; component N extends I { port in int i; }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTimingFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTimingFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // Outgoing ports
      // no overriden port
      arg("component ValidComp1 extends a.b.A { port out int o; }"),
      // sync -> sync
      arg("component ValidComp2 extends a.b.A { port sync out int os; }"),
      // non-sync -> non-sync
      arg("component ValidComp3 extends a.b.B { port out int o; }"),
      // non-sync -> sync
      arg("component ValidComp4 extends a.b.B { port sync out int o; }"),
      // Outgoing ports multiple ports
      // non-sync -> non-sync, non-sync -> non-sync
      arg("component ValidComp5 extends a.b.C { port out int o1; port out int o2; }"),
      // sync -> sync, sync -> sync
      arg("component ValidComp6 extends a.b.C { port sync out int o1; port sync out int o2; }"),
      // non-sync -> sync, non-sync -> non-sync
      arg("component ValidComp7 extends a.b.C { port sync out int o1; port out int o2; }"),
      // non-sync -> non-sync, non-sync -> sync
      arg("component ValidComp8 extends a.b.C { port out int o1; port sync out int o2; }"),
      // sync -> sync, sync -> sync
      arg("component ValidComp9 extends a.b.D { port sync out int os1; port sync out int os2; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp10 extends a.b.E { port sync out int os1; port out int o2; }"),
      // sync -> sync, non-sync -> sync
      arg("component ValidComp11 extends a.b.E { port sync out int os1; port sync out int o2; }"),
      // Outgoing ports multiple inheritance
      // sync -> sync
      arg("component ValidComp12 extends a.b.A, a.b.B { port sync out int os; }"),
      // non-sync -> non-sync
      arg("component ValidComp13 extends a.b.A, a.b.B { port out int o; }"),
      // non-sync -> sync
      arg("component ValidComp14 extends a.b.A, a.b.B { port sync out int o; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp15 extends a.b.A, a.b.B { port sync out int os; port out int o; }"),
      // sync -> sync, sync -> sync
      arg("component ValidComp16 extends a.b.A, a.b.B { port sync out int os; port sync out int o; }"),
      // non-sync -> non-sync, sync -> sync
      arg("component ValidComp17 extends a.b.A, a.b.B { port out int o; port sync out int os; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp18 extends a.b.A, a.b.C { port sync out int os; port out int o1; }"),
      // sync -> sync, non-sync -> sync
      arg("component ValidComp19 extends a.b.A, a.b.C { port sync out int os; port sync out int o2; }"),
      // no overriden port, sync -> sync
      arg("component ValidComp20 extends a.b.A, a.b.C { port out int o; port sync out int os; }"),
      // no overriden port, non-sync -> non-sync
      arg("component ValidComp21 extends a.b.A, a.b.C { port out int o; port out int o2; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp22 extends a.b.A, a.b.C { port sync out int os; port out int o2; }"),
      // Outgoing ports transitive inheritance
      // sync -> sync
      arg("component ValidComp23 extends a.b.F { port sync out int os; }"),
      // non-sync -> non-sync
      arg("component ValidComp24 extends a.b.G { port out int o; }"),
      // non-sync -> sync
      arg("component ValidComp25 extends a.b.G { port sync out int o; }"),
      // Incoming ports
      // sync -> sync
      arg("component ValidComp26 extends a.b.H { port sync in int i; }"),
      // non-sync -> non-sync
      arg("component ValidComp27 extends a.b.I { port in int i; }"),
      // sync -> non-sync
      arg("component ValidComp28 extends a.b.H { port in int i; }"),
      // Incoming ports - multiple ports
      // non-sync -> non-sync, non-sync -> non-sync
      arg("component ValidComp29 extends a.b.J { port in int i1; port in int i2; }"),
      // sync -> sync, sync -> sync
      arg("component ValidComp30 extends a.b.K { port sync in int is1; port sync in int is2; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp31 extends a.b.K { port sync in int is1; port in int i2; }"),
      // non-sync -> non-sync, sync -> sync
      arg("component ValidComp32 extends a.b.K { port in int i1; port sync in int is2; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp33 extends a.b.L { port sync in int is1; port in int i2; }"),
      // sync -> non-sync, non-sync -> non-sync
      arg("component ValidComp34 extends a.b.L { port in int is1; port in int i2; }"),
      // Incoming ports - multiple inheritance
      // sync -> sync
      arg("component ValidComp35 extends a.b.H, a.b.I { port sync in int i; }"),
      // non-sync -> non-sync
      arg("component ValidComp36 extends a.b.H, a.b.I { port in int i; }"),
      // sync -> non-sync
      arg("component ValidComp37 extends a.b.H, a.b.I { port sync in int i; }"),
      // sync -> sync, no overriden port
      arg("component ValidComp38 extends a.b.H, a.b.I { port sync in int i; port in int i2; }"),
      // sync -> sync, no overriden port
      arg("component ValidComp39 extends a.b.H, a.b.I { port sync in int i; port sync in int i2; }"),
      // non-sync -> non-sync, no overriden port
      arg("component ValidComp40 extends a.b.H, a.b.I { port in int i; port sync in int i2; }"),
      // non-sync -> non-sync, no overriden port
      arg("component ValidComp41 extends a.b.H, a.b.I { port in int i; port in int i2; }"),
      // sync -> sync, non-sync -> non-sync
      arg("component ValidComp42 extends a.b.H, a.b.J { port sync in int i; port in int i1; }"),
      // sync -> non-sync, non-sync -> non-sync
      arg("component ValidComp43 extends a.b.H, a.b.J { port in int i; port in int i2; }"),
      // sync -> non-sync, no overriden port
      arg("component ValidComp44 extends a.b.H, a.b.J { port in int i; port sync in int is1; }"),
      // no overriden port, non-sync -> non-sync
      arg("component ValidComp45 extends a.b.H, a.b.J { port sync in int is1; port in int i2; }"),
      // Incoming ports - transitive inheritance
      // sync -> sync
      arg("component ValidComp46 extends a.b.M { port sync in int i; }"),
      // non-sync -> sync
      arg("component ValidComp47 extends a.b.M { port in int i; }"),
      // non-sync -> non-sync
      arg("component ValidComp48 extends a.b.N { port in int i; }")
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // Outgoing ports
      // sync -> non-sync
      arg("component InvalidComp1 extends a.b.A { port out int os; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // sync -> non-sync
      arg("component InvalidComp2 extends a.b.A, a.b.B { port out int os; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // sync -> non-sync
      arg("component InvalidComp3 extends a.b.D { port out int os1; port out int os2; }",
        INVALID_PORT_TIMING_OVERRIDE, INVALID_PORT_TIMING_OVERRIDE),
      // sync -> non-sync
      arg("component InvalidComp4 extends a.b.E { port out int os1; port out int os2; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // sync -> non-sync
      arg("component InvalidComp5 extends a.b.F { port out int os; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // Incoming ports
      // non-sync -> sync
      arg("component InvalidComp6 extends a.b.I { port sync in int i; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // non-sync -> sync
      arg("component InvalidComp7 extends a.b.I, a.b.J { port in int i; port sync in int i1; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // non-sync -> sync
      arg("component InvalidComp8 extends a.b.I, a.b.K { port sync in int i; port sync in int is1; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // non-sync -> sync
      arg("component InvalidComp9 extends a.b.J { port in int i1; port sync in int i2; }",
        INVALID_PORT_TIMING_OVERRIDE),
      // non-sync -> sync
      arg("component InvalidComp10 extends a.b.N { port sync in int i; }",
        INVALID_PORT_TIMING_OVERRIDE)
    );
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTimingFits;
import com.google.common.base.Preconditions;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PortHeritageTimingFitsTest extends MontiArcTestBase {

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
  public void shouldNotReportError(@NotNull String model) throws IOException {
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
  public void shouldReportError(@NotNull String model, @NotNull ArcError... expectedErrorCode) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrorCode);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortHeritageTimingFits());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(expectedErrorCode));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // Outgoing ports
      Arguments.of("component Vaild0 extends a.b.A { port out int o; }"), // no overriden port
      Arguments.of("component Valid1 extends a.b.A { port sync out int os; }"), // sync -> sync
      Arguments.of("component Valid2 extends a.b.B { port out int o; }"), // non-sync -> non-sync
      Arguments.of("component Valid3 extends a.b.B { port sync out int o; }"), // non-sync -> sync
      // Outgoing ports multiple ports
      Arguments.of("component Valid4 extends a.b.C { port out int o1; port out int o2; }"), // non-sync -> non-sync, non-sync -> non-sync
      Arguments.of("component Valid5 extends a.b.C { port sync out int o1; port sync out int o2; }"), // sync -> sync, sync -> sync
      Arguments.of("component Valid6 extends a.b.C { port sync out int o1; port out int o2; }"), // non-sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid7 extends a.b.C { port out int o1; port sync out int o2; }"), // non-sync -> non-sync, non-sync -> sync

      Arguments.of("component Valid8 extends a.b.D { port sync out int os1; port sync out int os2; }"), // sync -> sync, sync -> sync

      Arguments.of("component Valid9 extends a.b.E { port sync out int os1; port out int o2; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid10 extends a.b.E { port sync out int os1; port sync out int o2; }"), // sync -> sync, non-sync -> sync
      // Outgoing ports multiple inheritance
      Arguments.of("component Valid11 extends a.b.A, a.b.B { port sync out int os; }"), // sync -> sync
      Arguments.of("component Valid12 extends a.b.A, a.b.B { port out int o; }"), // non-sync -> non-sync
      Arguments.of("component Valid13 extends a.b.A, a.b.B { port sync out int o; }"), // non-sync -> sync
      Arguments.of("component Valid14 extends a.b.A, a.b.B { port sync out int os; port out int o; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid15 extends a.b.A, a.b.B { port sync out int os; port sync out int o; }"), // sync -> sync, sync -> sync
      Arguments.of("component Valid16 extends a.b.A, a.b.B { port out int o; port sync out int os; }"), // non-sync -> non-sync, sync -> sync

      Arguments.of("component Valid17 extends a.b.A, a.b.C { port sync out int os; port out int o1; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid18 extends a.b.A, a.b.C { port sync out int os; port sync out int o2; }"), // sync -> sync, non-sync -> sync
      Arguments.of("component Valid19 extends a.b.A, a.b.C { port out int o; port sync out int os; }"), // no overriden port, sync -> sync
      Arguments.of("component Valid20 extends a.b.A, a.b.C { port out int o; port out int o2; }"), // no overriden port, non-sync -> non-sync
      Arguments.of("component Valid21 extends a.b.A, a.b.C { port sync out int os; port out int o2; }"), // sync -> sync, non-sync -> non-sync
      // Outgoing ports transitive inheritance
      Arguments.of("component Valid22 extends a.b.F { port sync out int os; }"), // sync -> sync
      Arguments.of("component Valid23 extends a.b.G { port out int o; }"), // non-sync -> non-sync
      Arguments.of("component Valid24 extends a.b.G { port sync out int o; }"), // non-sync -> sync

      // Incoming ports
      Arguments.of("component Valid25 extends a.b.H { port sync in int i; }"), // sync -> sync
      Arguments.of("component Valid26 extends a.b.I { port in int i; }"), // non-sync -> non-sync
      Arguments.of("component Valid27 extends a.b.H { port in int i; }"), // sync -> non-sync
      // Incoming ports - multiple ports
      Arguments.of("component Valid28 extends a.b.J { port in int i1; port in int i2; }"), // non-sync -> non-sync, non-sync -> non-sync
      Arguments.of("component Valid29 extends a.b.K { port sync in int is1; port sync in int is2; }"), // sync -> sync, sync -> sync
      Arguments.of("component Valid30 extends a.b.K { port sync in int is1; port in int i2; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid31 extends a.b.K { port in int i1; port sync in int is2; }"), // non-sync -> non-sync, sync -> sync
      Arguments.of("component Valid32 extends a.b.L { port sync in int is1; port in int i2; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid33 extends a.b.L { port in int is1; port in int i2; }"), // sync -> non-sync, non-sync -> non-sync
      // Incoming ports - multiple inheritance
      Arguments.of("component Valid34 extends a.b.H, a.b.I { port sync in int i; }"), // sync -> sync
      Arguments.of("component Valid35 extends a.b.H, a.b.I { port in int i; }"), // non-sync -> non-sync
      Arguments.of("component Valid36 extends a.b.H, a.b.I { port sync in int i; }"), // sync -> non-sync

      Arguments.of("component Valid37 extends a.b.H, a.b.I { port sync in int i; port in int i2; }"), // sync -> sync, no overriden port
      Arguments.of("component Valid38 extends a.b.H, a.b.I { port sync in int i; port sync in int i2; }"), // sync -> sync, no overriden port
      Arguments.of("component Valid39 extends a.b.H, a.b.I { port in int i; port sync in int i2; }"), // non-sync -> non-sync, no overriden port
      Arguments.of("component Valid40 extends a.b.H, a.b.I { port in int i; port in int i2; }"), // non-sync -> non-sync, no overriden port

      Arguments.of("component Valid41 extends a.b.H, a.b.J { port sync in int i; port in int i1; }"), // sync -> sync, non-sync -> non-sync
      Arguments.of("component Valid42 extends a.b.H, a.b.J { port in int i; port in int i2; }"), //  sync -> non-sync, non-sync -> non-sync
      Arguments.of("component Valid43 extends a.b.H, a.b.J { port in int i; port sync in int is1; }"), // sync -> non-sync, no overriden port
      Arguments.of("component Valid44 extends a.b.H, a.b.J { port sync in int is1; port in int i2; }"), // no overriden port, non-sync -> non-sync

      // Incoming ports - transitive inheritance
      Arguments.of("component Valid45 extends a.b.M { port sync in int i; }"), // sync -> sync
      Arguments.of("component Valid46 extends a.b.M { port in int i; }"), // non-sync -> sync
      Arguments.of("component Valid47 extends a.b.N { port in int i; }") // non-sync -> non-sync
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      // Outgoing ports
      Arguments.of("component Invalid1 extends a.b.A { port out int os; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // sync -> non-sync
      Arguments.of("component Invalid2 extends a.b.A, a.b.B { port out int os; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // sync -> non-sync
      Arguments.of("component Invalid3 extends a.b.D { port out int os1; port out int os2; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE, ArcError.INVALID_PORT_TIMING_OVERRIDE}), // sync -> non-sync
      Arguments.of("component Invalid4 extends a.b.E { port out int os1; port out int os2; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // sync -> non-sync
      Arguments.of("component Invalid5 extends a.b.F { port out int os; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // sync -> non-sync

      // Incoming ports
      Arguments.of("component Invalid5 extends a.b.I { port sync in int i; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // non-sync -> sync
      Arguments.of("component Invalid6 extends a.b.I, a.b.J { port in int i; port sync in int i1; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // non-sync -> sync
      Arguments.of("component Invalid6 extends a.b.I, a.b.K { port sync in int i; port sync in int is1; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // non-sync -> sync
      Arguments.of("component Invalid7 extends a.b.J { port in int i1; port sync in int i2; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}), // non-sync -> sync
      Arguments.of("component Invalid10 extends a.b.N { port sync in int i; }", new ArcError[]{ArcError.INVALID_PORT_TIMING_OVERRIDE}) // non-sync -> sync
    );
  }
}

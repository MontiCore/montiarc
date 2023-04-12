/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

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
import variablearc._cocos.VariantCoCos;

import java.io.IOException;
import java.util.stream.Stream;

public class VariantCoCosTest extends MontiArcAbstractTest {

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
    compile("package a.b; component C { port in int i1, i2; port out int o; }");
    compile("package a.b; component D { feature ff; varif (ff) { port in int io; } else { port out int io; } }");
    compile("package a.b; component E { port in boolean i; }");
    compile("package a.b; component F { port out boolean o; }");
    compile("package a.b; component G { feature ff; varif (ff) { port in boolean i, out boolean o; } else { port in int i, out int o; } }");
    compile("package a.b; component H { feature ff; varif (ff) { port in int io; } else { port out boolean io; } }");
    compile("package a.b; component I { feature ff; varif (ff) { port <<sync>> in int i; } else { port <<timed>> in int i; } }");
    compile("package a.b; component J { feature ff; varif (ff) { port <<sync>> out int o; } else { port <<timed>> out int o; } }");
    compile("package a.b; component K { port in int i; port out int o1, <<delayed>> out int o2; } ");
    compile("package a.b; component L { port in int i; feature ff; varif (ff) { port out int o; } else { port <<delayed>> out int o; } }");
    compile("package a.b; component M { feature ff; varif (ff) { port in int i; } }");
    compile("package a.b; component N { feature ff; varif (ff) { port out int o; } }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no variability
    "component Comp1 { }",
    // in port forward
    "component Comp2 { " +
      "port in int i; " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "}",
    // out port forward
    "component Comp3 { " +
      "port out int o; " +
      "a.b.B sub; " +
      "sub.o -> o; " +
      "}",
    // hidden channel
    "component Comp4 { " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // in port forward, single variation point
    "component Comp5 { " +
      "port in int i; " +
      "feature f; " +
      "varif (f) { " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, single variation point
    "component Comp6 { " +
      "port out int o; " +
      "feature f; " +
      "varif (f) { " +
      "a.b.B sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, single variation point
    "component Comp7 { " +
      "feature f; " +
      "varif (f) { " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, excluded variation point
    "component Comp8 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, excluded variation point
    "component Comp9 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, excluded variation point
    "component Comp10 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, excluded variation point
    "component Comp11 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, excluded variation point
    "component Comp12 { " +
      "varif (false) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, excluded variation point
    "component Comp13 { " +
      "varif (false) { " +
      "a.b.B sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, constrained feature
    "component Comp14 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, constrained feature
    "component Comp15 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, constrained feature
    "component Comp16 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, constrained feature
    "component Comp17 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, constrained feature
    "component Comp18 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, constrained feature
    "component Comp19 { " +
      "feature f;" +
      "constraint(!f);" +
      "varif (f) { " +
      "a.b.B sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, subcomponent with variable interface direction
    "component Comp20 { " +
      "port in int i; " +
      "a.b.D sub; " +
      "i -> sub.io; " +
      "constraint (sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface direction
    "component Comp21 { " +
      "port out int o; " +
      "a.b.D sub; " +
      "sub.io -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // hidden channel, subcomponent with variable interface direction
    "component Comp22 { " +
      "a.b.D sub1; " +
      "a.b.D sub2; " +
      "sub2.io -> sub1.io; " +
      "constraint (sub1.ff && !sub2.ff); " +
      "}",
    // port forward, component & subcomponent with variable interface direction
    "component Comp23 { " +
      "feature f; " +
      "varif (f) { " +
      "port in int io; " +
      "io -> sub.io; " +
      "} " +
      "varif (!f) { " +
      "port out int io; " +
      "sub.io -> io; " +
      "} " +
      "a.b.D sub; " +
      "constraint (sub.ff == f); " +
      "}",
    // in port forward, connector type mismatch, excluded variation point
    "component Comp24 { " +
      "varif (false) { " +
      "port in int i; " +
      "a.b.E sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, connector type mismatch, excluded variation point
    "component Comp25 { " +
      "varif (false) { " +
      "port out int o; " +
      "a.b.F sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, connector type mismatch, excluded variation point
    "component Comp26 { " +
      "varif (false) { " +
      "a.b.E sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "a.b.A sub3; " +
      "a.b.F sub4; " +
      "sub4.o -> sub3.i; " +
      "} " +
      "}",
    // port forward, subcomponent with variable interface types (deselect feature)
    "component Comp27 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // port forward, subcomponent with variable interface types (select feature)
    "component Comp28 { " +
      "port in boolean i; " +
      "port out boolean o; " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (sub.ff); " +
      "}",
    // port forward, component and subcomponent with variable interface types
    "component Comp29 { " +
      "feature f; " +
      "varif (f) { " +
      "port in boolean i; " +
      "port out boolean o; " +
      "} else { " +
      "port in int i; " +
      "port out int o; " +
      "} " +
      "a.b.G sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint (sub.ff == f); " +
      "}",
    // in port forward, subcomponent with variable interface timing (deselect feature)
    "component Comp30 { " +
      "port <<timed>> in int i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (!sub.ff); " +
      "}",
    // in port forward, subcomponent with variable interface timing (select feature)
    "component Comp31 { " +
      "port <<sync>> in int i; " +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface timing (deselect feature)
    "component Comp32 { " +
      "port <<timed>> out int o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface timing (select feature)
    "component Comp33 { " +
      "port <<sync>> out int o; " +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff); " +
      "}",
    // in port forward, component and subcomponent with variable interface timing
    "component Comp34 { " +
      "feature f; " +
      "varif (f) { " +
      "port <<sync>> in int i; " +
      "} else {" +
      "port <<timed>> in int i; " +
      "}" +
      "a.b.I sub; " +
      "i -> sub.i; " +
      "constraint (sub.ff == f); " +
      "}",
    // out port forward, component and subcomponent with variable interface timing
    "component Comp35 { " +
      "feature f; " +
      "varif (f) { " +
      "port <<sync>> out int o; " +
      "} else {" +
      "port <<timed>> out int o; " +
      "}" +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff == f); " +
      "}",
    // feedback loop
    "component Comp36 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C sub1; " +
      "a.b.K sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o1 -> o; " +
      "sub2.o2 -> sub1.i2; " +
      "}",
    // feedback loop, subcomponent with variable interface delay (deselect feature)
    "component Comp37 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.C sub1; " +
      "a.b.L sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o -> o; " +
      "sub2.o -> sub1.i2; " +
      "constraint (!sub2.ff); " +
      "}",
    // feedback loop, component with variable configuration and subcomponent with variable interface delay
    "component Comp38 { " +
      "port in int i; " +
      "port out int o; " +
      "feature f; " +
      "a.b.C sub1; " +
      "a.b.L sub2; " +
      "i -> sub1.i1; " +
      "sub1.o -> sub2.i; " +
      "sub2.o -> o; " +
      "varif (!f) { " +
      "sub2.o -> sub1.i2; " +
      "} else { " +
      "i -> sub1.i2; " +
      "}" +
      "constraint (sub2.ff = f); " +
      "}",
    // in port unused, component with variable configuration, excluded variation point
    "component Comp39 { " +
      "varif (false) { " +
      "port in int i; " +
      "} " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // out port unused, component with variable configuration, excluded variation point
    "component Comp40 { " +
      "varif (false) { " +
      "port out int o; " +
      "} " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // in port not connected, component with variable configuration, excluded variation point
    "component Comp41 { " +
      "varif (false) { " +
      "a.b.A sub; " +
      "} " +
      "}",
    // out port not connected, component with variable configuration, excluded variation point
    "component Comp42 { " +
      "varif (false) { " +
      "a.b.B sub; " +
      "} " +
      "}",
    // in port not connected, subcomponent with variable configuration (deselected feature)
    "component Comp43 { " +
      "a.b.M sub; " +
      "constraint (!sub.ff); " +
      "}",
    // out port not connected, subcomponent with variable configuration (deselected feature)
    "component Comp44 { " +
      "a.b.N sub; " +
      "constraint (!sub.ff); " +
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
    checker.addCoCo(new VariantCoCos());

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
    checker.addCoCo(new VariantCoCos());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // in port forward, source direction mismatch
      arg("component Comp1 { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch
      arg("component Comp2 { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch
      arg("component Comp3 { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch
      arg("component Comp4 { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch
      arg("component Comp5 { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch
      arg("component Comp6 { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, single variation point
      arg("component Comp7 { " +
          "port out int o; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, single variation point
      arg("component Comp8 { " +
          "port in int i; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, single variation point
      arg("component Comp9 { " +
          "port out int o; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, single variation point
      arg("component Comp10 { " +
          "port in int i; " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, single variation point, source direction mismatch
      arg("component Comp11 { " +
          "feature f; " +
          "varif (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, single variation point, target direction mismatch
      arg("component Comp12 { " +
          "feature f; " +
          "varif (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, included variation point
      arg("component Comp13 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, included variation point
      arg("component Comp14 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, included variation point
      arg("component Comp15 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, included variation point
      arg("component Comp16 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, included variation point
      arg("component Comp17 { " +
          "varif (true) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, included variation point
      arg("component Comp18 { " +
          "varif (true) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, unconstrained feature
      arg("component Comp19 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, unconstrained feature
      arg("component Comp20 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, unconstrained feature
      arg("component Comp21 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, unconstrained feature
      arg("component Comp22 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, unconstrained feature
      arg("component Comp23 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, unconstrained feature
      arg("component Comp24 { " +
          "feature f;" +
          "constraint(f);" +
          "varif (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, subcomponent with variable interface direction
      arg("component Comp25 { " +
          "port in int i; " +
          "a.b.D sub; " +
          "i -> sub.io; " +
          "constraint (!sub.ff); " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, subcomponent with variable interface direction
      arg("component Comp26 { " +
          "port out int o; " +
          "a.b.D sub; " +
          "sub.io -> o; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, subcomponent with variable interface direction
      arg("component Comp27 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (sub1.ff && sub2.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, subcomponent with variable interface direction
      arg("component Comp28 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (!sub1.ff && !sub2.ff); " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source and target direction mismatch, subcomponent with variable interface direction
      arg("component Comp29 { " +
          "a.b.D sub1; " +
          "a.b.D sub2; " +
          "sub2.io -> sub1.io; " +
          "constraint (!sub1.ff && sub2.ff); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, connector type mismatch, included variation point
      arg("component Comp30 { " +
          "varif (true) { " +
          "port in int i; " +
          "a.b.E sub; " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // out port forward, connector type mismatch, included variation point
      arg("component Comp31 { " +
          "varif (true) { " +
          "port out int o; " +
          "a.b.F sub; " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // hidden channel, connector type mismatch, included variation point
      arg("component Comp32 { " +
          "varif (true) { " +
          "a.b.E sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "a.b.A sub3; " +
          "a.b.F sub4; " +
          "sub4.o -> sub3.i; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // port forward, connector type mismatch, component and subcomponent with variable interface types
      arg("component Comp33 { " +
          "feature f; " +
          "varif (f) { " +
          "port in boolean i; " +
          "port out boolean o; " +
          "} else { " +
          "port in int i; " +
          "port out int o; " +
          "} " +
          "a.b.G sub; " +
          "i -> sub.i; " +
          "sub.o -> o; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // port forward, connector direction and type mismatch, component and subcomponent with variable interface types
      arg("component Comp34 { " +
          "feature f; " +
          "a.b.H sub; " +
          "varif (f) { " +
          "port out boolean o; " +
          "sub.io -> o; " +
          "} else { " +
          "port in int i; " +
          "i -> sub.io; " +
          "} " +
          "constraint (sub.ff == f); " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // in port forward, timing mismatch, subcomponent with variable interface timing (deselect feature)
      arg("component Comp35 { " +
          "port <<sync>> in int i; " +
          "a.b.I sub; " +
          "i -> sub.i; " +
          "constraint (!sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // in port forward, timing mismatch, subcomponent with variable interface timing (select feature)
      arg("component Comp36 { " +
          "port <<timed>> in int i; " +
          "a.b.I sub; " +
          "i -> sub.i; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, timing mismatch, subcomponent with variable interface timing (deselect feature)
      arg("component Comp37 { " +
          "port <<sync>> out int o; " +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (!sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, timing mismatch, subcomponent with variable interface timing (select feature)
      arg("component Comp38 { " +
          "port <<timed>> out int o; " +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // in port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("component Comp39 { " +
          "feature f; " +
          "varif (f) { " +
          "port <<sync>> in int i; " +
          "} else {" +
          "port <<timed>> in int i; " +
          "}" +
          "a.b.I sub; " +
          "i -> sub.i; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // out port forward, timing mismatch, component and subcomponent with variable interface timing
      arg("component Comp40 { " +
          "feature f; " +
          "varif (f) { " +
          "port <<sync>> out int o; " +
          "} else {" +
          "port <<timed>> out int o; " +
          "}" +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH),
      // feedback loop, weakly-causal feedback
      /*arg("component Comp41 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.C sub1; " +
          "a.b.K sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o1 -> sub1.i2; " +
          "sub2.o2 -> o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // feedback loop, weakly-causal feedback, subcomponent with variable interface delay (select feature)
      arg("component Comp42 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.C sub1; " +
          "a.b.L sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o -> o; " +
          "sub2.o -> sub1.i2; " +
          "constraint (sub2.ff); " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // feedback loop, weakly-causal feedback, component with variable configuration and subcomponent with variable interface delay
      arg("component Comp43 { " +
          "port in int i; " +
          "port out int o; " +
          "feature f; " +
          "a.b.C sub1; " +
          "a.b.L sub2; " +
          "i -> sub1.i1; " +
          "sub1.o -> sub2.i; " +
          "sub2.o -> o; " +
          "varif (f) { " +
          "sub2.o -> sub1.i2; " +
          "} else { " +
          "i -> sub1.i2; " +
          "}" +
          "constraint (sub2.ff = f); " +
          "}",
        ArcError.FEEDBACK_CAUSALITY)*/
      // in port unused
      arg("component Comp44 { " +
          "port in int i; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused
      arg("component Comp45 { " +
          "port out int o; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // ports unused
      arg("component Comp46 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.OUT_PORT_UNUSED),
      // in port unused, component with variable configuration, included variation point
      arg("component Comp47 { " +
          "varif (true) { " +
          "port in int i; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused, component with variable configuration, included variation point
      arg("component Comp48 { " +
          "varif (true) { " +
          "port out int o; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // in port unused, component with variable configuration
      arg("component Comp49 { " +
          "feature f; " +
          "port in int i; " +
          "varif (f) { " +
          "a.b.A sub; " +
          "i -> sub.i; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.IN_PORT_UNUSED),
      // out port unused, component with variable configuration
      arg("component Comp50 { " +
          "feature f; " +
          "port out int o; " +
          "varif (f) { " +
          "a.b.B sub; " +
          "sub.o -> o; " +
          "} " +
          "a.b.A sub1; " +
          "a.b.B sub2; " +
          "sub2.o -> sub1.i; " +
          "}",
        ArcError.OUT_PORT_UNUSED),
      // in port not connected
      arg("component Comp51 { " +
          "a.b.A sub; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected
      arg("component Comp52 { " +
          "a.b.B sub; " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // ports not connected
      arg("component Comp53 { " +
          "a.b.C sub; " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, component with variable configuration, included variation point
      arg("component Comp54 { " +
          "varif (true) { " +
          "a.b.A sub; " +
          "} " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, component with variable configuration, included variation point
      arg("component Comp55 { " +
          "varif (true) { " +
          "a.b.B sub; " +
          "} " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, component with variable configuration
      arg("component Comp56 { " +
          "feature f; " +
          "a.b.A sub; " +
          "varif (f) { " +
          "port in int i; " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, component with variable configuration
      arg("component Comp57 { " +
          "feature f; " +
          "a.b.B sub; " +
          "varif (f) { " +
          "port out int o; " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port unused, in port not connected, component with variable configuration
      arg("component Comp58 { " +
          "feature f; " +
          "a.b.A sub; " +
          "port in int i; " +
          "varif (f) { " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.IN_PORT_UNUSED,
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port unused, out port not connected, component with variable configuration
      arg("component Comp59 { " +
          "feature f; " +
          "a.b.B sub; " +
          "port out int o; " +
          "varif (f) { " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.OUT_PORT_UNUSED,
        ArcError.OUT_PORT_NOT_CONNECTED),
      // in port not connected, subcomponent with variable configuration (selected feature)
      arg("component Comp60 { " +
          "a.b.M sub; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.IN_PORT_NOT_CONNECTED),
      // out port not connected, subcomponent with variable configuration (selected feature)
      arg("component Comp61 { " +
          "a.b.N sub; " +
          "constraint (sub.ff); " +
          "}",
        ArcError.OUT_PORT_NOT_CONNECTED)
    );
  }
}

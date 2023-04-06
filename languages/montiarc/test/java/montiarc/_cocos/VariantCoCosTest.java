/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
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
import variablearc._cocos.VariableArcASTArcBlockCoCo;
import variablearc._cocos.VariableArcASTArcIfStatementCoCo;
import variablearc._cocos.VariableElementsUsage;
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
    compile("package a.b; component C { port in int i; port out int o; }");
    compile("package a.b; component D { feature ff; if (ff) { port in int io; } else { port out int io; } }");
    compile("package a.b; component E { port in boolean i; }");
    compile("package a.b; component F { port out boolean o; }");
    compile("package a.b; component G { feature ff; if (ff) { port in boolean i, out boolean o; } else { port in int i, out int o; } }");
    compile("package a.b; component H { feature ff; if (ff) { port in int io; } else { port out boolean io; } }");
    compile("package a.b; component I { feature ff; if (ff) { port <<sync>> in int i; } else { port <<timed>> in int i; } }");
    compile("package a.b; component J { feature ff; if (ff) { port <<sync>> out int o; } else { port <<timed>> out int o; } }");
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
      "if (f) { " +
      "a.b.A sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, single variation point
    "component Comp6 { " +
      "port out int o; " +
      "feature f; " +
      "if (f) { " +
      "a.b.B sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, single variation point
    "component Comp7 { " +
      "feature f; " +
      "if (f) { " +
      "a.b.A sub1; " +
      "a.b.B sub2; " +
      "sub2.o -> sub1.i; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, excluded variation point
    "component Comp8 { " +
      "if (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, excluded variation point
    "component Comp9 { " +
      "if (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, excluded variation point
    "component Comp10 { " +
      "if (false) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, excluded variation point
    "component Comp11 { " +
      "if (false) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, excluded variation point
    "component Comp12 { " +
      "if (false) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, excluded variation point
    "component Comp13 { " +
      "if (false) { " +
      "a.b.B sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, constrained feature
    "component Comp14 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, constrained feature
    "component Comp15 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, constrained feature
    "component Comp16 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port out int o; " +
      "a.b.A sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, constrained feature
    "component Comp17 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port in int i; " +
      "a.b.B sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, constrained feature
    "component Comp18 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "a.b.A sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, constrained feature
    "component Comp19 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
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
      "if (f) { " +
      "port in int io; " +
      "io -> sub.io; " +
      "} " +
      "if (!f) { " +
      "port out int io; " +
      "sub.io -> io; " +
      "} " +
      "a.b.D sub; " +
      "constraint (sub.ff == f); " +
      "}",
    // in port forward, connector type mismatch, excluded variation point
    "component Comp24 { " +
      "if (false) { " +
      "port in int i; " +
      "a.b.E sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, connector type mismatch, excluded variation point
    "component Comp25 { " +
      "if (false) { " +
      "port out int o; " +
      "a.b.F sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, connector type mismatch, excluded variation point
    "component Comp26 { " +
      "if (false) { " +
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
      "if (f) { " +
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
      "if (f) { " +
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
      "if (f) { " +
      "port <<sync>> out int o; " +
      "} else {" +
      "port <<timed>> out int o; " +
      "}" +
      "a.b.J sub; " +
      "sub.o -> o; " +
      "constraint (sub.ff == f); " +
      "}",
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser().parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.symbolTableCompleterDelegator().createFromAST(ast);
    MontiArcMill.symbolTablePass3Delegator().createFromAST(ast);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VariantCoCos());
    checker.addCoCo((VariableArcASTArcIfStatementCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcBlockCoCo) new VariableElementsUsage());
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new VariableElementsUsage());

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
    checker.addCoCo(new VariantCoCos());
    checker.addCoCo((VariableArcASTArcIfStatementCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcBlockCoCo) new VariableElementsUsage());
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new VariableElementsUsage());

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
          "if (f) { " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, single variation point
      arg("component Comp8 { " +
          "port in int i; " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, single variation point
      arg("component Comp9 { " +
          "port out int o; " +
          "feature f; " +
          "if (f) { " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, single variation point
      arg("component Comp10 { " +
          "port in int i; " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, single variation point, source direction mismatch
      arg("component Comp11 { " +
          "feature f; " +
          "if (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, single variation point, target direction mismatch
      arg("component Comp12 { " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, included variation point
      arg("component Comp13 { " +
          "if (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, included variation point
      arg("component Comp14 { " +
          "if (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, included variation point
      arg("component Comp15 { " +
          "if (true) { " +
          "port out int o; " +
          "a.b.A sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, included variation point
      arg("component Comp16 { " +
          "if (true) { " +
          "port in int i; " +
          "a.b.B sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, included variation point
      arg("component Comp17 { " +
          "if (true) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, included variation point
      arg("component Comp18 { " +
          "if (true) { " +
          "a.b.B sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, unconstrained feature
      arg("component Comp19 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
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
          "if (f) { " +
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
          "if (f) { " +
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
          "if (f) { " +
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
          "if (f) { " +
          "a.b.A sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, unconstrained feature
      arg("component Comp24 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
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
          "if (true) { " +
          "port in int i; " +
          "a.b.E sub; " +
          "i -> sub.i; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // out port forward, connector type mismatch, included variation point
      arg("component Comp31 { " +
          "if (true) { " +
          "port out int o; " +
          "a.b.F sub; " +
          "sub.o -> o; " +
          "} " +
          "}",
        ArcError.CONNECTOR_TYPE_MISMATCH),
      // hidden channel, connector type mismatch, included variation point
      arg("component Comp32 { " +
          "if (true) { " +
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
          "if (f) { " +
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
          "if (f) { " +
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
          "if (f) { " +
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
          "if (f) { " +
          "port <<sync>> out int o; " +
          "} else {" +
          "port <<timed>> out int o; " +
          "}" +
          "a.b.J sub; " +
          "sub.o -> o; " +
          "constraint (sub.ff == !f); " +
          "}",
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH)
    );
  }
}

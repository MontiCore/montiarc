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
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; }");
    compile("package a.b; component C { port out int o; }");
    compile("package a.b; component D { feature ff; if (ff) { port in int io; } if (!ff) { port out int io; } }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no variability
    "component Comp1 { }",
    // in port forward
    "component Comp2 { " +
      "port in int i; " +
      "a.b.B sub; " +
      "i -> sub.i; " +
      "}",
    // out port forward
    "component Comp3 { " +
      "port out int o; " +
      "a.b.C sub; " +
      "sub.o -> o; " +
      "}",
    // hidden channel
    "component Comp4 { " +
      "a.b.B sub1; " +
      "a.b.C sub2; " +
      "sub2.o -> sub1.i; " +
      "}",
    // in port forward, single variation point
    "component Comp5 { " +
      "port in int i; " +
      "feature f; " +
      "if (f) { " +
      "a.b.B sub; " +
      "i -> sub.i; " +
      "} " +
      "}",
    // out port forward, single variation point
    "component Comp6 { " +
      "port out int o; " +
      "feature f; " +
      "if (f) { " +
      "a.b.C sub; " +
      "sub.o -> o; " +
      "} " +
      "}",
    // hidden channel, single variation point
    "component Comp7 { " +
      "feature f; " +
      "if (f) { " +
      "a.b.B sub1; " +
      "a.b.C sub2; " +
      "sub2.o -> sub1.i; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, excluded variation point
    "component Comp8 { " +
      "if (false) { " +
      "port out int o; " +
      "a.b.B sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, excluded variation point
    "component Comp9 { " +
      "if (false) { " +
      "port in int i; " +
      "a.b.C sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, excluded variation point
    "component Comp10 { " +
      "if (false) { " +
      "port out int o; " +
      "a.b.B sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, excluded variation point
    "component Comp11 { " +
      "if (false) { " +
      "port in int i; " +
      "a.b.C sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, excluded variation point
    "component Comp12 { " +
      "if (false) { " +
      "a.b.B sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, excluded variation point
    "component Comp13 { " +
      "if (false) { " +
      "a.b.C sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // out port forward, source direction mismatch, constrained feature
    "component Comp14 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port out int o; " +
      "a.b.B sub; " +
      "sub.i -> o; " +
      "} " +
      "}",
    // out port forward, target direction mismatch, constrained feature
    "component Comp15 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port in int i; " +
      "a.b.C sub; " +
      "sub.o -> i; " +
      "} " +
      "}",
    // in port forward, source direction mismatch, constrained feature
    "component Comp16 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port out int o; " +
      "a.b.B sub; " +
      "o -> sub.i; " +
      "} " +
      "}",
    // in port forward, target direction mismatch, constrained feature
    "component Comp17 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "port in int i; " +
      "a.b.C sub; " +
      "i -> sub.o; " +
      "} " +
      "}",
    // hidden channel, source direction mismatch, constrained feature
    "component Comp18 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "a.b.B sub1, sub2; " +
      "sub2.i -> sub1.i; " +
      "} " +
      "}",
    // hidden channel, target direction mismatch, constrained feature
    "component Comp19 { " +
      "feature f;" +
      "constraint(!f);" +
      "if (f) { " +
      "a.b.C sub1, sub2; " +
      "sub2.o -> sub1.o; " +
      "} " +
      "}",
    // in port forward, subcomponent with variable interface
    "component Comp20 { " +
      "port in int i; " +
      "a.b.D sub; " +
      "i -> sub.io; " +
      "constraint (sub.ff); " +
      "}",
    // out port forward, subcomponent with variable interface
    "component Comp21 { " +
      "port out int o; " +
      "a.b.D sub; " +
      "sub.io -> o; " +
      "constraint (!sub.ff); " +
      "}",
    // hidden channel, subcomponent with variable interface
    "component Comp22 { " +
      "a.b.D sub1; " +
      "a.b.D sub2; " +
      "sub2.io -> sub1.io; " +
      "constraint (sub1.ff && !sub2.ff); " +
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
      // out port forward, source direction mismatch
      arg("component Comp1 { " +
          "port out int o; " +
          "a.b.B sub; " +
          "sub.i -> o; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch
      arg("component Comp2 { " +
          "port in int i; " +
          "a.b.C sub; " +
          "sub.o -> i; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch
      arg("component Comp3 { " +
          "port out int o; " +
          "a.b.B sub; " +
          "o -> sub.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch
      arg("component Comp4 { " +
          "port in int i; " +
          "a.b.C sub; " +
          "i -> sub.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch
      arg("component Comp5 { " +
          "a.b.B sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch
      arg("component Comp6 { " +
          "a.b.C sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, single variation point
      arg("component Comp7 { " +
          "port out int o; " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, single variation point
      arg("component Comp8 { " +
          "port in int i; " +
          "feature f; " +
          "if (f) { " +
          "a.b.C sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, single variation point
      arg("component Comp9 { " +
          "port out int o; " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, single variation point
      arg("component Comp10 { " +
          "port in int i; " +
          "feature f; " +
          "if (f) { " +
          "a.b.C sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, single variation point, source direction mismatch
      arg("component Comp11 { " +
          "feature f; " +
          "if (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, single variation point, target direction mismatch
      arg("component Comp12 { " +
          "feature f; " +
          "if (f) { " +
          "a.b.C sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, included variation point
      arg("component Comp13 { " +
          "if (true) { " +
          "port out int o; " +
          "a.b.B sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, included variation point
      arg("component Comp14 { " +
          "if (true) { " +
          "port in int i; " +
          "a.b.C sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, included variation point
      arg("component Comp15 { " +
          "if (true) { " +
          "port out int o; " +
          "a.b.B sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, included variation point
      arg("component Comp16 { " +
          "if (true) { " +
          "port in int i; " +
          "a.b.C sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, included variation point
      arg("component Comp17 { " +
          "if (true) { " +
          "a.b.B sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, included variation point
      arg("component Comp18 { " +
          "if (true) { " +
          "a.b.C sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // out port forward, source direction mismatch, unconstrained feature
      arg("component Comp19 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "port out int o; " +
          "a.b.B sub; " +
          "sub.i -> o; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // out port forward, target direction mismatch, unconstrained feature
      arg("component Comp20 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "port in int i; " +
          "a.b.C sub; " +
          "sub.o -> i; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // in port forward, source direction mismatch, unconstrained feature
      arg("component Comp21 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "port out int o; " +
          "a.b.B sub; " +
          "o -> sub.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // in port forward, target direction mismatch, unconstrained feature
      arg("component Comp22 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "port in int i; " +
          "a.b.C sub; " +
          "i -> sub.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      // hidden channel, source direction mismatch, unconstrained feature
      arg("component Comp23 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "a.b.B sub1, sub2; " +
          "sub2.i -> sub1.i; " +
          "} " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      // hidden channel, target direction mismatch, unconstrained feature
      arg("component Comp24 { " +
          "feature f;" +
          "constraint(f);" +
          "if (f) { " +
          "a.b.C sub1, sub2; " +
          "sub2.o -> sub1.o; " +
          "} " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH)
    );
  }
}

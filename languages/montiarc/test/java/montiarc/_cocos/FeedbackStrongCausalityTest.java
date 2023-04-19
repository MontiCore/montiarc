/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.FeedbackStrongCausality;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
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

import static org.assertj.core.api.Assertions.assertThat;

public class FeedbackStrongCausalityTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MontiArcMill.reset();
    MontiArcMill.init();
    BasicSymbolsMill.initializePrimitives();
    MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
    MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
    setUpComponents();
  }

  @Override
  public void setUp() {}

  @AfterEach
  public void tearDown() {
    Log.clearFindings();
  }

  protected static void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B { port in int i; } ");
    compile("package a.b; component C { port out int o; } ");
    compile("package a.b; component D { port in int i; port <<delayed>> out int o; }");
    compile("package a.b; component E { port in int i; port out int o; }");
    compile("package a.b; component F { port in int i1, i2; port <<delayed>> out int o; }");
    compile("package a.b; component G { port in int i1, i2; port out int o; }");
    compile("package a.b; component H { port in int i; port out int o; D sub; i -> sub.i; sub.o -> o; }");
    compile("package a.b; component I { port in int i; port out int o; E sub; i -> sub.i; sub.o -> o; }");
    compile("package a.b; component J { port in int i; port out int o; D sub1; E sub2; i -> sub1.i; sub1.o -> sub2.i; sub2.o -> o; } ");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // component without subcomponents
    "component Comp1 { }",
    // subcomponent without ports
    "component Comp2 { " +
      "  a.b.A sub; " +
      "}",
    // in port forward to sink
    "component Comp3 { " +
      "  port in int i; " +
      "  a.b.B sub; " +
      "  i -> sub.i; " +
      "}",
    // out port forward from source
    "component Comp4 { " +
      "  port out int o; " +
      "  a.b.E sub; " +
      "  sub.o -> o; " +
      "}",
    // direct strongly causal feedback loop
    "component Comp5 { " +
      "  a.b.D sub; " +
      "  sub.o -> sub.i; " +
      "}",
    // direct strongly causal feedback loop & port forward
    "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.F sub; " +
      "  i -> sub.i1; " +
      "  sub.o -> sub.i2; " +
      "  sub.o -> o; " +
      "}",
    // strongly causal feedback loop
    "component Comp7 { " +
      "  a.b.D sub1; " +
      "  a.b.E sub2; " +
      "  sub1.o -> sub2.i; " +
      "  sub2.o -> sub1.i; " +
      "}",
    // strongly causal feedback loop
    "component Comp8 { " +
      "  a.b.E sub1; " +
      "  a.b.F sub2; " +
      "  sub1.o -> sub2.i; " +
      "  sub2.o -> sub1.i; " +
      "}",
    // strongly causal feedback loop  & port forward
    "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.G sub1; " +
      "  a.b.D sub2; " +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i; " +
      "  sub2.o -> sub1.i2; " +
      "  sub2.o -> o; " +
      "}",
    // direct strongly causal feedback loop & nested subcomponent
    "component Comp10 { " +
      "  a.b.H sub; " +
      "  sub.o -> sub.i; " +
      "}",
    // indirect strongly causal feedback loop & nested subcomponent
    "component Comp11 { " +
      "  a.b.J sub; " +
      "  sub.o -> sub.i; " +
      "}",
    // multiple strongly causal feedback loops & port forward
    "component Comp12 { " +
        "  port in int i; " +
        "  port out int o; " +
        "  a.b.G sub1; " +
        "  a.b.F sub2; " +
        "  i -> sub1.i1; " +
        "  sub1.o -> sub2.i1; " +
        "  sub1.o -> sub2.i2; " +
        "  sub2.o -> sub1.i2; " +
        "  sub2.o -> o; " +
        "}",
    // multiple strongly causal feedback loops & port forward
    "component Comp13 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.F sub1; " +
      "  a.b.G sub2; " +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i1; " +
      "  sub1.o -> sub2.i2; " +
      "  sub2.o -> sub1.i2; " +
      "  sub2.o -> o; " +
      "}",
    // multiple strongly causal feedback loops & port forward (connector with multiple targets)
    "component Comp14 { " +
        "  port in int i; " +
        "  port out int o; " +
        "  a.b.G sub1; " +
        "  a.b.F sub2; " +
        "  i -> sub1.i1; " +
        "  sub1.o -> sub2.i1, sub2.i2; " +
        "  sub2.o -> sub1.i2, o; " +
        "}",
    // multiple strongly causal feedback loops & port forward (connector with multiple targets)
    "component Comp15 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.F sub1; " +
      "  a.b.G sub2; " +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i1, sub2.i2; " +
      "  sub2.o -> sub1.i2, o; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality());

    // When
    checker.checkAll(ast);

    // Then
    Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    Assertions.assertThat(this.collectErrorCodes(Log.getFindings())).as(Log.getFindings().toString())
      .containsExactlyElementsOf(this.collectErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // direct non strongly causal feedback loop
      arg("component Comp1 { " +
          "  a.b.E sub; " +
          "  sub.o -> sub.i; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // direct non strongly causal feedback loop & port forward
      arg("component Comp2 { " +
          "  port in int i; " +
          "  port out int o; " +
          "  a.b.G sub; " +
          "  i -> sub.i1; " +
          "  sub.o -> sub.i2; " +
          "  sub.o -> o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // non strongly causal feedback loop
      arg("component Comp3 { " +
          "  a.b.E sub1; " +
          "  a.b.E sub2; " +
          "  sub1.o -> sub2.i; " +
          "  sub2.o -> sub1.i; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY),
      // non strongly causal feedback loop & port forward
      arg("component Comp4 { " +
          "  port in int i; " +
          "  port out int o; " +
          "  a.b.G sub1; " +
          "  a.b.E sub2; " +
          "  i -> sub1.i1; " +
          "  sub1.o -> sub2.i; " +
          "  sub2.o -> sub1.i2; " +
          "  sub2.o -> o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY),
      // direct non strongly causal feedback loop & nested subcomponent
      arg("component Comp5 { " +
          "  a.b.I sub; " +
          "  sub.o -> sub.i; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY),
      // multiple direct non strongly causal feedback loops
      arg("component Comp6 { " +
          "  a.b.G sub; " +
          "  sub.o -> sub.i1; " +
          "  sub.o -> sub.i2; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY),
      // multiple non strongly causal feedback loops
      arg("component Comp7 { " +
          "  a.b.G sub1; " +
          "  a.b.E sub2; " +
          "  a.b.E sub3; " +
          "  sub1.o -> sub2.i; " +
          "  sub1.o -> sub3.i; " +
          "  sub2.o -> sub1.i1; " +
          "  sub3.o -> sub2.i2; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY),
      // multiple non strongly causal feedback loops & port forward
      arg("component Comp8 { " +
          "  port in int i; " +
          "  port out int o; " +
          "  a.b.G sub1; " +
          "  a.b.G sub2; " +
          "  i -> sub1.i1; " +
          "  sub1.o -> sub2.i1; " +
          "  sub1.o -> sub2.i2; " +
          "  sub2.o -> sub1.i2; " +
          "  sub2.o -> o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY),
      // multiple non strongly causal feedback loops & port forward (connector with multiple targets)
      arg("component Comp8 { " +
          "  port in int i; " +
          "  port out int o; " +
          "  a.b.G sub1; " +
          "  a.b.G sub2; " +
          "  i -> sub1.i1; " +
          "  sub1.o -> sub2.i1, sub2.i2; " +
          "  sub2.o -> sub1.i2, o; " +
          "}",
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY,
        ArcError.FEEDBACK_CAUSALITY)
    );
  }
}

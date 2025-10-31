/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.assertj.core.api.Assertions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.FeedbackStrongCausality4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FeedbackStrongCausality4FamilyTest extends MontiArcTestBase {
    @BeforeEach
    protected void initSymbols() {
        MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
        MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
        setUpComponents();
    }

    protected void setUpComponents() {
      compile("package a.b; component A { }");
      compile("package a.b; component B { port in int i; } ");
      compile("package a.b; component C { port out int o; } ");
      compile("package a.b; component D { port in int i; port out int o; <<delayed>> compute {}}");
      compile("package a.b; component E { port in int i; port out int o; }");
      compile("package a.b; component F { port in int i1, i2; port out int o; <<delayed>> compute {}}");
      compile("package a.b; component G { port in int i1, i2; port out int o; }");
      compile("package a.b; component H { port in int i; port out int o; D sub; i -> sub.i; sub.o -> o; }");
      compile("package a.b; component I { port in int i; port out int o; E sub; i -> sub.i; sub.o -> o; }");
      compile("package a.b; component J { port in int i; port out int o; D sub1; E sub2; i -> sub1.i; sub1.o -> sub2.i; sub2.o -> o; } ");
      compile("package a.b; component K { port in int i; port out int o; B sub1; C sub2; i -> sub1.i; sub2.o -> o; } ");
    }

    private static Stream<Arguments> provideUniqueSenderModel()
    {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel = arg("component Comp1 { " +
                "feature f1;" +
                "  a.b.E sub; " +
                " varif(f1){ sub.o -> sub.i; }" +
                "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new FeedbackStrongCausality4Family());

        ASTMACompilationUnit mainAST = compile(model);
        checker.checkAll(mainAST);

        String[] test = getLoggedErrorCodes();

        // Then
        assertThat(Log.getErrorCount() == 0);
    }

    private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
        consumer.accept(coco);
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
      "}",
    // Strongly causal feedback loops with nested sink and source
    "component Comp16 { " +
      "  a.b.K sub; " +
      "  sub.o -> sub.i; " +
      "}",
    // directly strongly causal with behavior declaring the delay
    "component Comp17 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner inner {" +
      "    port in int i;" +
      "    port out int o;" +
      "    <<delayed>> automaton {}" +
      "  }" +
      "  i -> inner.i; inner.o -> o; " +
      "}",
  })

    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new FeedbackStrongCausality4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).isEmpty();
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // component without subcomponents
    "component Comp1 {" +
      "feature f1; }",
    // subcomponent without ports
    "component Comp2 { " +
      "feature f1;" +
      " varif(f1){ a.b.A sub; } " +
      "}",
    // in port forward to sink
    "component Comp3 { " +
      "feature f1;" +
      "  port in int i; " +
      "  a.b.B sub; " +
      "  varif(f1){i -> sub.i; }" +
      "}",
    // out port forward from source
    "component Comp4 {" +
      "feature f1; " +
      "  port out int o; " +
      "  a.b.E sub; " +
      "  varif(f1){sub.o -> o; }" +
      "  constraint(f1);" +
      "}",
    // direct strongly causal feedback loop
    "component Comp5 { " +
      "feature f1,f2;" +
      "  varif(f1){a.b.D sub; " +
      "  varif(f2){" +
      "  sub.o -> sub.i; }}" +
      "}",
    // direct strongly causal feedback loop & port forward
    "component Comp6 { " +
      "  feature f1;" +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.F sub; " +
      "varif(f1){}else{" +
      "  i -> sub.i1; " +
      "  sub.o -> sub.i2; " +
      "  sub.o -> o; }" +
      "constraint(!f1);" +
      "}",
    // strongly causal feedback loop
    "component Comp7 { " +
      "feature f1,f2;" +
      "  a.b.D sub1; " +
      "  a.b.E sub2; " +
      "  varif(f1){sub1.o -> sub2.i; }" +
      "  varif(f2){sub2.o -> sub1.i; }" +
      "constraint(f1 && f2);" +
      "}",
    // strongly causal feedback loop
    "component Comp8 { " +
      "feature f1,f2;" +
      "  varif(f1){a.b.E sub1; " +
      "  a.b.F sub2; }" +
      "varif(f2){" +
      "  sub1.o -> sub2.i; " +
      "  sub2.o -> sub1.i; }" +
      "}",
    // strongly causal feedback loop  & port forward
    "component Comp9 { " +
      "feature f1,f2,f3;" +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.G sub1; " +
      "  a.b.D sub2; " +
      "  varif(f1){i -> sub1.i1; }" +
      "  varif(f2){sub1.o -> sub2.i; }" +
      "  varif(f3){sub2.o -> sub1.i2; " +
      "  sub2.o -> o; }" +
      "constraint(f1 && f2 && f3);" +
      "}",
    // direct strongly causal feedback loop & nested subcomponent
    "component Comp10 {" +
      "feature f1; " +
      " varif(f1){ a.b.H sub; }" +
      "  sub.o -> sub.i; " +
      "constraint(f1);" +
      "}",
    // indirect strongly causal feedback loop & nested subcomponent
    "component Comp11 { " +
      "feature f1;" +
      " varif(f1){}else{ a.b.J sub; " +
      "  sub.o -> sub.i; }" +
      "constraint(!f1);" +
      "}",
    // multiple strongly causal feedback loops & port forward
    "component Comp12 {" +
      "feature f1; " +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.G sub1; " +
      "  a.b.F sub2; " +
      "varif(f1){" +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i1; " +
      "  sub1.o -> sub2.i2; " +
      "  sub2.o -> sub1.i2; " +
      "  sub2.o -> o; }" +
      "}",
    // multiple strongly causal feedback loops & port forward
    "component Comp13 {" +
      "feature f1,f2,f3; " +
      "  varif(f1){port in int i; " +
      "  port out int o; }" +
      "  varif(f2){a.b.F sub1; " +
      "  a.b.G sub2; }" +
      "varif(f3){" +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i1; " +
      "  sub1.o -> sub2.i2; " +
      "  sub2.o -> sub1.i2; " +
      "  sub2.o -> o;}" +
      "constraint(f1 && f2); " +
      "}",
    // multiple strongly causal feedback loops & port forward (connector with multiple targets)
    "component Comp14 { " +
      "feature f1;" +
      "varif(f1){" +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.G sub1; " +
      "  a.b.F sub2; " +
      "  i -> sub1.i1; " +
      "  sub1.o -> sub2.i1, sub2.i2; " +
      "  sub2.o -> sub1.i2, o; }" +
      "}",
    // multiple strongly causal feedback loops & port forward (connector with multiple targets)
    "component Comp15 { " +
      "feature f1,f2;" +
      "  port in int i; " +
      "  port out int o; " +
      "  a.b.F sub1; " +
      "  a.b.G sub2; " +
      "  varif(f1){i -> sub1.i1; " +
      "  varif(f2){sub1.o -> sub2.i1, sub2.i2; " +
      "  sub2.o -> sub1.i2, o; }}" +
      "constraint(f1 && f2);" +
      "}",
    // Strongly causal feedback loops with nested sink and source
    "component Comp16 { " +
      "feature f1;" +
      " varif(f1){ a.b.K sub; " +
      "  sub.o -> sub.i; }" +
      "}",
    // directly strongly causal with behavior declaring the delay
    "component Comp17 { " +
      "feature f1;" +
      "  port in int i; " +
      "  port out int o; " +
      "  component Inner inner {" +
      "feature f1;" +
      "varif(f1){" +
      "    port in int i;" +
      "    port out int o;" +
      "    <<delayed>> automaton {}}" +
      "  }" +
      "  i -> inner.i; inner.o -> o; " +
      "constraint(f1 == inner.f1);" +
      "}",
  })

  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new FeedbackStrongCausality4Family());

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
        checker.addCoCo(new FeedbackStrongCausality4Family());

        // When
        checker.checkAll(ast);

        // Then
        Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
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
            "  sub3.o -> sub1.i2; " +
            "}",
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
          ArcError.FEEDBACK_CAUSALITY),
        // multiple non strongly causal feedback loops & port forward (connector with multiple targets)
        arg("component Comp9 { " +
            "  port in int i; " +
            "  port out int o; " +
            "  a.b.G sub1; " +
            "  a.b.G sub2; " +
            "  i -> sub1.i1; " +
            "  sub1.o -> sub2.i1, sub2.i2; " +
            "  sub2.o -> sub1.i2, o; " +
            "}",
          ArcError.FEEDBACK_CAUSALITY,
          ArcError.FEEDBACK_CAUSALITY)
      );
    }

      @ParameterizedTest
      @MethodSource("invalidModelsWithVariability")
      public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
        Preconditions.checkNotNull(model);
        Preconditions.checkNotNull(errors);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new FeedbackStrongCausality4Family());

        // When
        checker.checkAll(ast);

        // Then
        Assertions.assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
          .containsExactlyInAnyOrder(getErrorCodes(errors));
      }

      protected static Stream<Arguments> invalidModelsWithVariability() {
        return Stream.of(
          // direct non strongly causal feedback loop
          arg("component Comp1 { " +
              "feature f1;" +
              "  a.b.E sub; " +
              "  varif(f1){sub.o -> sub.i; }" +
              "}",
            ArcError.FEEDBACK_CAUSALITY),
          // direct non strongly causal feedback loop & port forward
          arg("component Comp2 {" +
              "  feature f1,f2; " +
              "  port in int i; " +
              "  port out int o; " +
              "  a.b.G sub; " +
              "  varif(f1){i -> sub.i1; " +
              "  varif(f2){sub.o -> sub.i2; " +
              "  sub.o -> o; }}" +
              "constraint(f1 && f2);" +
              "}",
            ArcError.FEEDBACK_CAUSALITY),
          // non strongly causal feedback loop
          arg("component Comp3 { " +
              "feature f1,f2;" +
              "  a.b.E sub1; " +
              "  a.b.E sub2; " +
              "  varif(f1){sub1.o -> sub2.i;} " +
              "  varif(f2){sub2.o -> sub1.i; }" +
              "}",
            ArcError.FEEDBACK_CAUSALITY),
          // non strongly causal feedback loop & port forward
          arg("component Comp4 { " +
              "feature f1,f2;" +
              "  port in int i; " +
              "  port out int o; " +
              "  a.b.G sub1; " +
              "  a.b.E sub2; " +
              "varif(f1){" +
              "  i -> sub1.i1; " +
              "varif(f2){}else{" +
              "  sub1.o -> sub2.i; " +
              "  sub2.o -> sub1.i2; " +
              "  sub2.o -> o; }}" +
              "constraint(f1 && !f2);" +
              "}",
            ArcError.FEEDBACK_CAUSALITY),
          // direct non strongly causal feedback loop & nested subcomponent
          arg("component Comp5 { " +
              "feature f1;" +
              "varif(f1){" +
              "  a.b.I sub; " +
              "  sub.o -> sub.i; }" +
              "}",
            ArcError.FEEDBACK_CAUSALITY),
          // multiple direct non strongly causal feedback loops
          arg("component Comp6 { " +
              "feature f1,f2;" +
              "  a.b.G sub; " +
              " varif(f1){ sub.o -> sub.i1;} " +
              "  varif(f2){sub.o -> sub.i2; }" +
              "constraint(f1 && f2);" +
              "}",
            ArcError.FEEDBACK_CAUSALITY,
            ArcError.FEEDBACK_CAUSALITY),
          // multiple non strongly causal feedback loops
          arg("component Comp7 { " +
              "feature f1,f2,f3;" +
              "  a.b.G sub1; " +
              "  a.b.E sub2; " +
              "  a.b.E sub3; " +
              " varif(f1){ sub1.o -> sub2.i; }" +
              " varif(f2){ sub1.o -> sub3.i; }" +
              " varif(f3){ sub2.o -> sub1.i1; " +
              "  sub3.o -> sub1.i2; }" +
              "constraint(f1 && f2 && f3);" +
              "}",
            ArcError.FEEDBACK_CAUSALITY,
            ArcError.FEEDBACK_CAUSALITY),
          // multiple non strongly causal feedback loops & port forward
          arg("component Comp8 {" +
              "feature f1; " +
              "  port in int i; " +
              "  port out int o; " +
              "  a.b.G sub1; " +
              "  a.b.G sub2; " +
              "  i -> sub1.i1; " +
              " varif(f1){ sub1.o -> sub2.i1; " +
              "  sub1.o -> sub2.i2; " +
              "  sub2.o -> sub1.i2; " +
              "  sub2.o -> o; }" +
              "}",
            ArcError.FEEDBACK_CAUSALITY,
            ArcError.FEEDBACK_CAUSALITY),
          // multiple non strongly causal feedback loops & port forward (connector with multiple targets)
          arg("component Comp9 {" +
              "feature f1,f2; " +
              "  port in int i; " +
              "  port out int o; " +
              "  a.b.G sub1; " +
              "  a.b.G sub2; " +
              "  varif(f1){i -> sub1.i1; }" +
              "  varif(f2){sub1.o -> sub2.i1, sub2.i2; " +
              "  sub2.o -> sub1.i2, o; }" +
              "constraint(f1 && f2);" +
              "}",
            ArcError.FEEDBACK_CAUSALITY,
            ArcError.FEEDBACK_CAUSALITY)
        );
      }
}

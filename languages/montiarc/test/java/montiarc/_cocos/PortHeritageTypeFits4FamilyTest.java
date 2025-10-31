/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortHeritageTypeFits;
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
import variablearc._cocos.FeedbackStrongCausality4Family;
import variablearc._cocos.PortHeritageTypeFits4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PortHeritageTypeFits4FamilyTest extends MontiArcTestBase {

    @BeforeEach
    protected void setUpComponents() {
        compile("package a.b; component A { }");
        compile("package a.b; component B { port in int i; port out int o; }");
        compile("package a.b; component C<T> { port in T i; port out T o; } ");
        compile("package a.b; component D { port in int i; }");
        compile("package a.b; component E { port out int o; }");
    }

    private static Stream<Arguments> provideUniqueSenderModel()
    {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel = arg("component Comp6 extends a.b.D, a.b.E { " +
                "feature f1,f2;" +
                "varif(f1){port in double i; }" +
                "varif(f2){port out byte o; }" +
                "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new PortHeritageTypeFits4Family());

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
                    "}",
            // multi heritage with overriding super ports (outgoing supertype)
            "component Comp12 extends a.b.D, a.b.E { " +
                    "port in int i; " +
                    "port out long o; " +
                    "}",
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new PortHeritageTypeFits4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // no heritage
    "component Comp1 { feature f1; }",
    // heritage without ports
    "component Comp2 extends a.b.A { feature f1; }",
    // heritage adding ports, without super ports
    "component Comp3 extends a.b.A {" +
      "feature f1,f2; " +
      "varif(f1){ port in int i; }" +
      "varif(f2){ port out int o; }" +
      "}",
    // heritage with adding ports, with super ports
    "component Comp4 extends a.b.B { feature f1; }",
    // heritage with adding ports, with super ports
    "component Comp5 extends a.b.B {" +
      "feature f1,f2; " +
      "varif(f1){ port in int i2; } " +
      "varif(f2){ port out int o2; }" +
      "constraint(f1 && f2);" +
      "}",
    // heritage with overriding super ports (same type)
    "component Comp6 extends a.b.B { " +
      "feature f1;" +
      "varif(f1){}else{port in int i; " +
      "port out int o; }" +
      "constraint(!f1);" +
      "}",
    // heritage with overriding super ports (incoming subtype)
    "component Comp7 extends a.b.B { " +
      "feature f1,f2;" +
      "varif(f1){" +
      "port in byte i;" +
      "varif(f2){ " +
      "port out int o; }}" +
      "constraint(f1 && f2);" +
      "}",
    // heritage with overriding super ports (outgoing supertype)
    "component Comp8 extends a.b.B { " +
      "feature f1;" +
      "varif(f1){" +
      "port in int i; " +
      "port out long o; }" +
      "}",
    // heritage with overriding super ports (incoming and outgoing)
    "component Comp9 extends a.b.B { " +
      "feature f1;" +
      "varif(f1){port in byte i; }" +
      "port out long o; " +
      "}",
    // heritage with overriding generic typed ports (matching type)
    "component Comp10 extends a.b.C<int> { " +
      "feature f1,f2;" +
      "varif(f1){ port in int i;}" +
      "varif(f2){ port out int o; }" +
      "}",
    // heritage with overriding generic typed ports (matching generic type)
    "component Comp11<T> extends a.b.C<T> { " +
      "feature f1,f2;" +
      "varif(f1){ port in T i; }" +
      "varif(f2){ port out T o; }" +
      "constraint(f1 || f2);" +
      "}",
    // multi heritage with overriding super ports (outgoing supertype)
    "component Comp12 extends a.b.D, a.b.E {" +
      "feature f1;" +
      "varif(f1){ " +
      "port in int i; " +
      "port out long o; }" +
      "}",
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortHeritageTypeFits4Family());

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
        checker.addCoCo(new PortHeritageTypeFits4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
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
                        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
                // multi heritage with incoming and outgoing port type mismatch
                arg("component Comp6 extends a.b.D, a.b.E { " +
                                "port in double i; " +
                                "port out byte o; " +
                                "}",
                        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH,
                        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH)
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
    checker.get4FullVariant().addCoCo(new PortHeritageTypeFits4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // heritage with overriding super ports (incoming supertype)
      arg("component Comp1 extends a.b.B { " +
          "feature f1;" +
          "varif(f1){ port in double i; " +
          "port out int o;} " +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage with overriding super ports (outgoing subtype)
      arg("component Comp2 extends a.b.B { " +
          "feature f1;" +
          "varif(f1){" +
          "port in int i; " +
          "port out byte o; }" +
          "constraint(f1);" +
          "}",
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage with incoming and outgoing port type mismatch
      arg("component Comp3 extends a.b.B { " +
          "feature f1,f2;" +
          "varif(f1){ port in double i; }" +
          "varif(f2){ port out byte o; }" +
          "constraint(f1 && f2); " +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH,
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // heritage with overriding generic typed ports (incoming supertype)
      arg("component Comp4 extends a.b.C<int> { " +
          "feature f1,f2;" +
          "varif(f1){port in double i; " +
          "varif(f2){" +
          "port out int o; }}" +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH),
      // heritage with overriding super ports (outgoing subtype)
      arg("component Comp5 extends a.b.C<int> { " +
          "feature f1,f2;" +
          "varif(f1){}else{port in int i; }" +
          "varif(f2){}else{port out byte o; }" +
          "constraint(!f1 && !f2);" +
          "}",
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH),
      // multi heritage with incoming and outgoing port type mismatch
      arg("component Comp6 extends a.b.D, a.b.E { " +
          "feature f1,f2;" +
          "varif(f1){port in double i; " +
          "varif(f2){port out byte o;}}" +
          "}",
        ArcError.HERITAGE_IN_PORT_TYPE_MISMATCH,
        ArcError.HERITAGE_OUT_PORT_TYPE_MISMATCH)
    );
  }
}

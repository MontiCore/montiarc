/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicMaxOneBehavior;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
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
import variablearc._cocos.AtomicMaxOneBehavior4Family;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AtomicMaxOneBehavior4FamilyTest extends MontiArcTestBase {

    protected void initSymbols() {
        MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
        MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
        setupEnums();
        setupComponents();
    }

    protected void setupEnums()
    {
        OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        MontiArcMill.globalScope().add(onOffEnumType);
    }

    protected void setupComponents()
    {
        compile("package a.b; component A { }");
        compile("package a.b; component B { port in int i; port out int o; }");
        compile("package a.b; component C { port in int i; port <<delayed>> out int o; }");
        compile("package a.b; component D { port in int i1, i2; port out int o; }");
        compile("package a.b; component Z { a.b.A a1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> a1.i;} }");
    }

    private static Stream<Arguments> provideUniqueSenderModel()
    {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel = arg( "component Comp1 { " +
                "feature f1,f2;" +
                "varif(f1){automaton { } }" +
                "varif(f2){automaton {}}" +
                "automaton { } " +
                "constraint((f1&&!f2)||(!f1&&f2));" +
                "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new AtomicMaxOneBehavior4Family());

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
            // atomic no behavior
            "component Comp1 { }",
            // atomic with automaton
            "component Comp2 { " +
                    "automaton { } " +
                    "}",
            // inner with automaton
            "component Comp3 { " +
                    "component Inner { " +
                    "automaton { } " +
                    "} " +
                    "}",
            // two inner with automata
            "component Comp4 { " +
                    "component Inner1 { " +
                    "automaton { } " +
                    "} " +
                    "component Inner2 { " +
                    "automaton { } " +
                    "} " +
                    "}",
            // atomic with ajava
            "component Comp5 { " +
                    "compute { } " +
                    "}",
            // composed with behavior and automaton
            "component Comp6 { " +
                    "component Inner1 i1 { } " +
                    "compute { } " +
                    "automaton { } " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new AtomicMaxOneBehavior4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic no behavior
    "component Comp1 { feature f1; }",
    // atomic with automaton
    "component Comp2 {" +
      "feature f1; " +
      "varif(f1){automaton { } }" +
      "}",
    // inner with automaton
    "component Comp3 {" +
      "feature f1;" +
      "varif(f1){ " +
      "component Inner { " +
      "automaton { } " +
      "} }" +
      "constraint(f1);" +
      "}",
    // two inner with automata
    "component Comp4 {" +
      "feature f1,f2;" +
      "varif(f1){ " +
      "component Inner1 { " +
      "automaton { } " +
      "}}" +
      "varif(f2){ " +
      "component Inner2 { " +
      "automaton { } " +
      "} }" +
      "}",
    // atomic with ajava
    "component Comp5 {" +
      "feature f1;" +
      "varif(f1){ }else{ " +
      "compute { }}" +
      "constraint(!f1); " +
      "}",
    // composed with behavior and automaton
    "component Comp6 { " +
      "feature f1,f2;" +
      "component Inner1 i1 { } " +
      "varif(f1){compute { }" +
      "varif(f2){ " +
      "automaton { } }}" +
      "}"
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new AtomicMaxOneBehavior4Family());

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
        checker.addCoCo(new AtomicMaxOneBehavior4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                // atomic two automata
                arg("component Comp1 { " +
                                "automaton { } " +
                                "automaton { } " +
                                "}",
                        ArcError.MULTIPLE_BEHAVIOR),
                // atomic two ajava blocks
                arg("component Comp2 { " +
                                "compute { } " +
                                "compute { } " +
                                "}",
                        ArcError.MULTIPLE_BEHAVIOR),
                // atomic with automaton and ajava
                arg("component Comp3 { " +
                                "automaton { } " +
                                "compute { } " +
                                "}",
                        ArcError.MULTIPLE_BEHAVIOR),
                // inner with two automata
                arg("component Comp4 { " +
                                "component Inner { " +
                                "automaton { } " +
                                "automaton { } " +
                                "} " +
                                "}",
                        ArcError.MULTIPLE_BEHAVIOR)
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
    checker.get4FullVariant().addCoCo(new AtomicMaxOneBehavior4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // atomic two automata
      arg("component Comp1 { " +
          "feature f1,f2;" +
          "varif(f1){automaton { } }" +
          "varif(f2){automaton { }} " +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // atomic two ajava blocks
      arg("component Comp2 {" +
          "feature f1,f2; " +
          "varif(f1){ compute { } }" +
          "varif(f2){compute { } }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // atomic with automaton and ajava
      arg("component Comp3 { " +
          "feature f1,f2;" +
          "varif(f1){automaton { }" +
          "varif(f2){ " +
          "compute { } }}" +
          "}",
        ArcError.MULTIPLE_BEHAVIOR),
      // inner with two automata
      arg("component Comp4 {" +
          "feature f1; " +
          "varif(f1){" +
          "component Inner { " +
          "feature f1;" +
          "varif(f1){" +
          "automaton { } " +
          "automaton { } }" +
          "constraint(f1);" +
          "} }" +
          "}",
        ArcError.MULTIPLE_BEHAVIOR)
    );
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.SCError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TransitionPreconditionsAreBoolean4FamilyTest extends MontiArcTestBase {
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
        Arguments simpleModel = arg(     "component Comp2 { " +
          "automaton { " +
          "initial state s; " +
          "s -> s [true]; " +
          "s -> s [1]; " +
          "} " +
          "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());

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
            // no automaton
            "component Comp1 { }",
            // no transition
            ("component Comp2 { " +
                    "automaton { } " +
                    "}"),
            // no precondition
            "component Comp3 { " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s; " +
                    "} " +
                    "}",
            // single boolean precondition
            "component Comp4 { " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s [true]; " +
                    "} " +
                    "}",
            // two boolean preconditions
            "component Comp5 { " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s [true]; " +
                    "s -> s [false]; " +
                    "} " +
                    "}",
            // boolean precondition (access port)
            "component Comp6 { " +
                    "port in boolean i; " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s [i]; " +
                    "} " +
                    "}",
            // boolean precondition (access parameter)
            "component Comp7(boolean p) { " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s[p]; " +
                    "} " +
                    "}",
            // boolean precondition (access variable)
            "component Comp8 { " +
                    "boolean v = true; " +
                    "automaton { " +
                    "initial state s; " +
                    "s -> s [v]; " +
                    "} " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new TransitionPreconditionsAreBoolean4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // no automaton
    "component Comp1 { feature f1; }",
    // no transition
    ("component Comp2 {" +
      "feature f1; " +
      "automaton { } " +
      "}"),
    // no precondition
    "component Comp3 { " +
      "feature f1;" +
      "varif(f1){" +
      "automaton { " +
      "initial state s; " +
      "s -> s; " +
      "} }" +
      "}",
    // single boolean precondition
    "component Comp4 {" +
      "feature f1; " +
      "varif(f1){automaton { " +
      "initial state s; " +
      "s -> s [true]; " +
      "}}" +
      "constraint(f1); " +
      "}",
    // two boolean preconditions
    "component Comp5 {" +
      "feature f1; " +
      "varif(f1){}else{automaton { " +
      "initial state s; " +
      "s -> s [true]; " +
      "s -> s [false]; " +
      "} }" +
      "}",
    // boolean precondition (access port)
    "component Comp6 {" +
      "feature f1,f2; " +
      "varif(f1){port in boolean i; }" +
      "varif(f2){automaton { " +
      "initial state s; " +
      "s -> s [i]; " +
      "} }" +
      "}",
    // boolean precondition (access parameter)
    "component Comp7(boolean p) { " +
      "feature f1;" +
      "automaton { " +
      "initial state s; " +
      "s -> s[p]; " +
      "} " +
      "}",
    // boolean precondition (access variable)
    "component Comp8 { " +
      "feature f1;" +
      "varif(f1){}else{boolean v = true; }" +
      "automaton { " +
      "initial state s; " +
      "s -> s [v]; " +
      "} " +
      "constraint(!f1);" +
      "}"
  })

  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());

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
        checker.addCoCo(new TransitionPreconditionsAreBoolean4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                // single non-boolean precondition
                arg("component Comp1 { " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [1]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN),
                // one boolean and one non-boolean precondition
                arg("component Comp2 { " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [true]; " +
                                "s -> s [1]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN),
                // two non-boolean preconditions
                arg("component Comp3 { " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [1]; " +
                                "s -> s [1]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN,
                        SCError.PRECONDITION_NOT_BOOLEAN),
                // single non-boolean precondition (port access)
                arg("component Comp4 { " +
                                "port in int i; " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [i]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN),
                // single non-boolean precondition (parameter access)
                arg("component Comp5(int p) { " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [p]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN),
                // single non-boolean precondition (variable access)
                arg("component Comp6 { " +
                                "int v = 1; " +
                                "automaton { " +
                                "initial state s; " +
                                "s -> s [v]; " +
                                "} " +
                                "}",
                        SCError.PRECONDITION_NOT_BOOLEAN)
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
    checker.get4FullVariant().addCoCo(new TransitionPreconditionsAreBoolean4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // single non-boolean precondition
      arg("component Comp1 { " +
          "feature f1;" +
          "varif(f1){automaton { " +
          "initial state s; " +
          "s -> s [1]; " +
          "} }" +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // one boolean and one non-boolean precondition
      arg("component Comp2 {" +
          "feature f1;" +
          "varif(f1){ " +
          "automaton { " +
          "initial state s; " +
          "s -> s [true]; " +
          "s -> s [1]; " +
          "} }" +
          "constraint(f1);" +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // two non-boolean preconditions
      arg("component Comp3 {" +
          "feature f1,f2;" +
          "varif(f1){" +
          "varif(f2){ " +
          "automaton { " +
          "initial state s; " +
          "s -> s [1]; " +
          "s -> s [1]; " +
          "} }}" +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN,
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (port access)
      arg("component Comp4 {" +
          "feature f1,f2; " +
          "varif(f1){port in int i; }" +
          "varif(f2){automaton { " +
          "initial state s; " +
          "s -> s [i]; }" +
          "} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (parameter access)
      arg("component Comp5(int p) { " +
          "feature f1;" +
          "varif(f1){}else{" +
          "automaton { " +
          "initial state s; " +
          "s -> s [p]; " +
          "} }" +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN),
      // single non-boolean precondition (variable access)
      arg("component Comp6 { " +
          "feature f1,f2;" +
          "varif(f1){int v = 1; " +
          "varif(f2){automaton { " +
          "initial state s; " +
          "s -> s [v]; " +
          "}}} " +
          "}",
        SCError.PRECONDITION_NOT_BOOLEAN)
    );
  }
}

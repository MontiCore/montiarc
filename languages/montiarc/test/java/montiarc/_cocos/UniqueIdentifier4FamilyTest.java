/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.UniqueIdentifier;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.UniqueIdentifier4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UniqueIdentifier4FamilyTest extends MontiArcTestBase {

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
        Arguments simpleModel = arg(             "component Comp12(int p) { " +
          "component Inner(int p) { } " +
          "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new UniqueIdentifier4Family());

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
            // single unique component
            "component Comp1 { }",
            // single unique parameter
            "component Comp2(int p) { } ",
            // single unique port
            "component Comp3 { " +
                    "port in int i; " +
                    "}",
            // single unique variable
            "component Comp4 { " +
                    "int v = 0; " +
                    "}",
            // single unique inner component
            "component Comp5 { " +
                    "component Inner { } " +
                    "}",
            // single unique subcomponent
            "component Comp6 { " +
                    "component Inner { } " +
                    "Inner sub; " +
                    "}",
            // single unique type-parameter
            "component Comp7<T> { }",
            // single unique feature
            "component Comp8 { " +
                    "feature f; " +
                    "}",
            // one of each unique identifier
            "component Comp10<T>(int p) { " +
                    "port in int i; " +
                    "int v = 0; " +
                    "component Inner { } " +
                    "Inner sub; " +
                    "}",
            // unique parameter (inner component)
            "component Comp12(int p) { " +
                    "component Inner(int p) { } " +
                    "}",
            // unique port (inner component)
            "component Comp13 { " +
                    "port in int i; " +
                    "component Inner { " +
                    "port in int i; " +
                    "} " +
                    "}",
            // unique variable (inner component)
            "component Comp14 { " +
                    "int v = 0; " +
                    "component Inner { " +
                    "int v = 0; " +
                    "} " +
                    "}",
            // unique inner component (inner component)
            "component Comp15 { " +
                    "component Inner { " +
                    "component Inner { } " +
                    "} " +
                    "}",
            // unique subcomponent (inner component)
            "component Comp16 { " +
                    "component Inner { " +
                    "component Inner { } " +
                    "Inner sub; " +
                    "} " +
                    "Inner sub; " +
                    "}",
            // unique type-parameter (inner component)
            "component Comp17<T> { " +
                    "component Inner<T> { } " +
                    "}",
            // unique feature (inner component)
            "component Comp18 { " +
                    "feature f; " +
                    "component Inner { " +
                    "feature f; " +
                    "} " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new UniqueIdentifier4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // single unique component
    "component Comp1 { feature f1; }",
    // single unique parameter
    "component Comp2(int p) { feature f1; } ",
    // single unique port
    "component Comp3 {" +
      "feature f1; " +
      "varif(f1){ port in int i; }" +
      "}",
    // single unique variable
    "component Comp4 { " +
      "feature f1;" +
      "varif(f1){ int v = 0; }" +
      "constraint(f1);" +
      "}",
    // single unique inner component
    "component Comp5 { " +
      "feature f1;" +
      "component Inner { feature f2; } " +
      "}",
    // single unique subcomponent
    "component Comp6 {" +
      "feature f1; " +
      "component Inner { feature f1; constraint(f1);} " +
      "varif(f1){Inner sub; }" +
      "constraint(f1 == sub.f1);" +
      "}",
    // single unique type-parameter
    "component Comp7<T> {  feature f1; }",
    // single unique feature
    "component Comp8 { " +
      "feature f; " +
      "}",
    // one of each unique identifier
    "component Comp10<T>(int p) { " +
      "feature f1,f2;" +
      "varif(f1){}else{port in int i;} " +
      "varif(f2){ int v = 0; }" +
      "component Inner { } " +
      "Inner sub; " +
      "}",
    // unique parameter (inner component)
    "component Comp12(int p) { " +
      "feature f1;" +
      "component Inner(int p) { feature f1; } " +
      "}",
    // unique port (inner component)
    "component Comp13 { " +
      "feature f1;" +
      "varif(f1){ port in int i; }" +
      "component Inner { " +
      "feature f1;" +
      "varif(f1){port in int i; }" +
      "} " +
      "}",
    // unique variable (inner component)
    "component Comp14 {" +
      "feature f1,f2; " +
      "varif(f1){varif(f2){int v = 0; }}" +
      "component Inner { " +
      "int v = 0; " +
      "} " +
      "}",
    // unique inner component (inner component)
    "component Comp15 { " +
      "feature f1;" +
      "component Inner {" +
      "feature f1; " +
      "component Inner {" +
      "feature f1; } " +
      "} " +
      "}",
    // unique subcomponent (inner component)
    "component Comp16 {" +
      "feature f1;" +
      "varif(f1){ " +
      "component Inner { " +
      "component Inner { } " +
      "Inner sub; " +
      "} " +
      "Inner sub; }" +
      "}",
    // unique type-parameter (inner component)
    "component Comp17<T> {" +
      "feature f1; " +
      "component Inner<T> { feature f1; } " +
      "}",
    // unique feature (inner component)
    "component Comp18 { " +
      "feature f; " +
      "component Inner { " +
      "feature f; " +
      "} " +
      "}"
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new UniqueIdentifier4Family());

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
        checker.addCoCo(new UniqueIdentifier4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                // duplicate component
                //arg("component Comp1 { " +
                //    "component Comp1 { } " +
                //    "}",
                //  ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate parameter
                arg("component Comp2(int p, int p) { }",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate port
                arg("component Comp3 { " +
                                "port in int i; " +
                                "port in int i; " +
                                "}",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate variable
                arg("component Comp4 { " +
                                "int v = 0; " +
                                "int v = 0; " +
                                "}",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate inner component
                arg("component Comp5 { " +
                                "component Inner { } " +
                                "component Inner { } " +
                                "}",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate subcomponent
                arg("component Comp6 { " +
                                "component Inner { } " +
                                "Inner sub; " +
                                "Inner sub; " +
                                "}",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate type-parameter
                arg("component Comp7<T, T> { }",
                        ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate feature
                //arg("component Comp8 { " +
                //   "feature f; " +
                //    "feature f; " +
                //    "}",
                //  ArcError.UNIQUE_IDENTIFIER_NAMES),
                // duplicate parameter in inner component
                arg("component Comp8() { component Inner(int i, double i, boolean i){ } }",
                        ArcError.UNIQUE_IDENTIFIER_NAMES)
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
    checker.get4FullVariant().addCoCo(new UniqueIdentifier4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // duplicate component
      //arg("component Comp1 { " +
      //    "component Comp1 { } " +
      //    "}",
      //  ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate parameter
      arg("component Comp2(int p, int p) { feature f1; }",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate port
      arg("component Comp3 {" +
          "feature f1,f2; " +
          "varif(f1){ port in int i; }" +
          "varif(f2){ port in int i; }" +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate variable
      arg("component Comp4 { " +
          "feature f1,f2;" +
          "varif(f1){}else{int v = 0; }" +
          "varif(f2){ int v = 0; }" +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate inner component
      arg("component Comp5 { " +
          "feature f1;" +
          "component Inner { feature f1; } " +
          "component Inner { feature f1; } " +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate subcomponent
      arg("component Comp6 { " +
          "feature f1,f2;" +
          "component Inner { } " +
          "varif(f1){Inner sub; " +
          "varif(f2){ Inner sub; }}" +
          "}",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate type-parameter
      arg("component Comp7<T, T> { feature f1; }",
        ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate feature
      //arg("component Comp8 { " +
      //   "feature f; " +
      //    "feature f; " +
      //    "}",
      //  ArcError.UNIQUE_IDENTIFIER_NAMES),
      // duplicate parameter in inner component
      arg("component Comp8() { feature f1; component Inner(int i, double i, boolean i){ feature f1; } }",
        ArcError.UNIQUE_IDENTIFIER_NAMES)
    );
  }

}

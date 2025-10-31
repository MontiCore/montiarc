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
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.AtomicNoConnector4Family;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AtomicNoConnector4FamilyTest extends MontiArcTestBase {
    @BeforeEach
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
        Arguments simpleModel = arg("component Comp2 {" +
          "feature f1; " +
          "port in int i;" +
          "port out int o;" +
          "varif(f1){a.b.A a;}else{i -> o;}" +
          "constraint(!f1); " +
                "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new AtomicNoConnector4Family());

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
            // atomic no connector
            "component Comp1 { }",
            // composed with connector
            "component Comp2 { " +
                    "component Inner1 i1 { } " +
                    "port in int i;" +
                    "port out int o;" +
                    "i -> o; " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new AtomicNoConnector4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).isEmpty();
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic no connector
    "component Comp1 { feature f1; }",
    // composed with connector
    "component Comp2 { " +
      "feature f1,f2;" +
      "varif(f1){component Inner1 i1 { } }" +
      "port in int i;" +
      "port out int o;" +
      "varif(f2){i -> o; }" +
      "constraint(f1 && f2);" +
      "}"
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new AtomicNoConnector4Family());

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
        checker.addCoCo(new AtomicNoConnector4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                // atomic one connector
                arg("component Comp1 { " +
                                "port in int i;" +
                                "port out int o;" +
                                "i -> o; " +
                                "}",
                        ArcError.CONNECTORS_IN_ATOMIC),
                // atomic two connectors
                arg("component Comp2 { " +
                                "port in int i;" +
                                "port out int o;" +
                                "i -> o; " +
                                "i -> o; " +
                                "}",
                        ArcError.CONNECTORS_IN_ATOMIC,
                        ArcError.CONNECTORS_IN_ATOMIC)
        );
    }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new AtomicNoConnector4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // atomic one connector
      arg("component Comp1 { " +
          "feature f1;" +
          "port in int i;" +
          "port out int o;" +
          "varif(f1){i -> o; }" +
          "}",
        ArcError.CONNECTORS_IN_ATOMIC),
      // atomic two connectors
      arg("component Comp2 {" +
          "feature f1,f2; " +
          "port in int i;" +
          "port out int o;" +
          "varif(f1){i -> o; }" +
          "varif(f2){i -> o; }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.CONNECTORS_IN_ATOMIC,
        ArcError.CONNECTORS_IN_ATOMIC)
    );
  }
}

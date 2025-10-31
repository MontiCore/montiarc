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
import variablearc._cocos.ConnectorPortsExist4Family;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectorPortsExist4FamilyTest extends MontiArcTestBase {

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
        compile("package a.b; component B { port in int i; }");
        compile("package a.b; component C { port out int o; }");
        compile("package a.b; component D { port in int i; port out int o; }");
        compile("package a.b; component E { port in int i1, i2; port out int o; }");
        compile("package a.b; component Z { a.b.A a1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> a1.i;} }");
    }

    private static Stream<Arguments> provideUniqueSenderModel()
    {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel =  arg("component Comp1 { " +
          "feature f1,f2;" +
          "port in int i; " +
          "port out int o; " +
          "a.b.A a; " +
          "varif(f1){ port in int k;  }" +
          "else{ i -> a.i; }" +
          "varif(f2){ a.o -> o; }" +
          "}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new ConnectorPortsExist4Family());

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
            "component Comp1 { }",
            "component Comp2 {" +
                    "port in int i; " +
                    "port out int o; " +
                    "i -> o; " +
                    "}",
            "component Comp3 { " +
                    "port in int i; " +
                    "port in int o; " +
                    "a.b.D d; " +
                    "i -> d.i; " +
                    "d.o -> o; " +
                    "}",
            "component Comp4 { " +
                    "port in int i; " +
                    "port out int o1, o2; " +
                    "a.b.E e; " +
                    "i -> e.i1, e.i2; " +
                    "e.o -> o1, o2; " +
                    "}",
            "component Comp5 { " +
                    "port in int i; " +
                    "port out int o; " +
                    "component Inner {" +
                    "port in int i; " +
                    "port out int o; " +
                    "} " +
                    "Inner sub; " +
                    "i -> sub.i; " +
                    "sub.o -> o; " +
                    "}"
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = compile(model);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new ConnectorPortsExist4Family());

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
        checker.addCoCo(new ConnectorPortsExist4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModels() {
        return Stream.of(
                arg("component Comp1 { " +
                                "port in int i; " +
                                "port out int o; " +
                                "a.b.A a; " +
                                "i -> a.i; " +
                                "a.o -> o; " +
                                "}",
                        ArcError.MISSING_PORT,
                        ArcError.MISSING_PORT),
                arg("component Comp2 { " +
                                "port in int i; " +
                                "port in int o; " +
                                "a.b.B b; " +
                                "i -> b.i; " +
                                "b.o -> o; " +
                                "}",
                        ArcError.MISSING_PORT),
                arg("component Comp3 { " +
                                "port in int i; " +
                                "port in int o; " +
                                "a.b.C c; " +
                                "i -> c.i; " +
                                "c.o -> o; " +
                                "}",
                        ArcError.MISSING_PORT),
                arg("component Comp4 { i -> o; }",
                        ArcError.MISSING_PORT,
                        ArcError.MISSING_PORT),
                arg("component Comp5 { " +
                                "port in int i; " +
                                "i -> o; " +
                                "}",
                        ArcError.MISSING_PORT),
                arg("component Comp6 { " +
                                "port out int o; " +
                                "i -> o; " +
                                "}",
                        ArcError.MISSING_PORT),
                arg("component Comp7 { " +
                                "port in int i; " +
                                "i -> b.i; " +
                                "}",
                        ArcError.MISSING_SUBCOMPONENT),
                arg("component Comp8 { " +
                                "port out int o; " +
                                "c.o -> o; " +
                                "}",
                        ArcError.MISSING_SUBCOMPONENT),
                arg("component Comp9 { " +
                                "c.o -> b.i; " +
                                "}",
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_SUBCOMPONENT),
                arg("component Comp10 { " +
                                "port in int i; " +
                                "i -> b1.i, b2.i; " +
                                "}",
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_SUBCOMPONENT),
                arg("component Comp10 { " +
                                "i -> d1.i, d2.i; " +
                                "d1.o -> o1; " +
                                "d2.o -> o2; " +
                                "}",
                        ArcError.MISSING_PORT,
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_PORT,
                        ArcError.MISSING_SUBCOMPONENT,
                        ArcError.MISSING_PORT)
        );
    }
}

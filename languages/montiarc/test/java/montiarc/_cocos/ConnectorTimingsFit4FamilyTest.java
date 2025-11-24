/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ConnectorTimingsFit;
import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.ConnectorTimingsFit4Family;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectorTimingsFit4FamilyTest extends MontiArcTestBase {

    @BeforeEach
    protected void initSymbols() {
        MontiArcMill.globalScope().addAdaptedTypeSymbolResolver(new OOClass2MCResolver());
        MontiArcMill.globalScope().addAdaptedOOTypeSymbolResolver(new OOClass2MCResolver());
        setupEnums();
        setupComponents();
    }

    protected void setupEnums() {
        OOTypeSymbol onOffEnumType = MontiArcMill.oOTypeSymbolBuilder().setIsEnum(true).setName("OnOff").setIsPublic(true).setSpannedScope(MontiArcMill.scope()).build();
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("ON").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        onOffEnumType.getSpannedScope().add(MontiArcMill.fieldSymbolBuilder().setName("OFF").setIsStatic(true).setIsFinal(true).setIsPublic(true).setIsReadOnly(true).setType(SymTypeExpressionFactory.createTypeObject(onOffEnumType)).build());
        MontiArcMill.globalScope().add(onOffEnumType);
    }

    protected void setupComponents() {
        compile("package a.b; component A { }");
        compile("package a.b; component B { port in int i; }");
        compile("package a.b; component C { port out int o; }");
        compile("package a.b; component D { port in int i; port out int o; }");
        compile("package a.b; component E { port in int i1, i2; port out int o; }");
        compile("package a.b; component Z { a.b.A a1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> a1.i;} }");
    }

    private static Stream<Arguments> provideUniqueSenderModel() {
        List<Arguments> componentList = new ArrayList<>();
        Arguments simpleModel = arg(   "component c1 {" +
                "feature f1;"
                + "port sync out int o;"
                + "component Inner {"
                + "port out int o;"
                + "}"
                + "Inner inner;"
                + "varif(f1){inner.o -> o;}"
                + "}");
                //arg("package a.b; component C1 { feature f1; port in int i; z1.i -> i; varif(f1){a.b.Z z1;}}");
        componentList.add(simpleModel);
        return componentList.stream();
    }

    @ParameterizedTest
    @MethodSource("provideUniqueSenderModel")
    public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

        Preconditions.checkNotNull(model);
        MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
        checker.get4FullVariant().addCoCo(new ConnectorTimingsFit4Family());

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
            // all timing match (sync)
            "component c1 {"
                    + "port sync in int i;"
                    + "port sync out int o;"
                    + "component Inner {"
                    + "port sync in int i;"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner1, inner2;"
                    + "i -> inner1.i;"
                    + "inner1.o -> inner2.i;"
                    + "inner2.o -> o;"
                    + "}",
            // all timing match (timed)
            "component c2 {"
                    + "port in int i;"
                    + "port out int o;"
                    + "component Inner {"
                    + "port in int i;"
                    + "port out int o;"
                    + "}"
                    + "Inner inner1, inner2;"
                    + "i -> inner1.i;"
                    + "inner1.o -> inner2.i;"
                    + "inner2.o -> o;"
                    + "}",
            // all timing match (timed)
            "component c3 {"
                    + "port in int i;"
                    + "port out int o;"
                    + "component Inner {"
                    + "port in int i;"
                    + "port out int o;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i;"
                    + "inner.o -> o;"
                    + "}",
            // all timing match (sync)
            "component c4 {"
                    + "port sync in int i;"
                    + "port sync out int o;"
                    + "component Inner {"
                    + "port sync in int i;"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i;"
                    + "inner.o -> o;"
                    + "}",
            // all timing match (sync) - multiple targets
            "component c5 {"
                    + "port sync in int i;"
                    + "port sync out int o1;"
                    + "port sync out int o2;"
                    + "component Inner {"
                    + "port sync in int i1;"
                    + "port sync in int i2;"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i1;"
                    + "i -> inner.i2;"
                    + "inner.o -> o1;"
                    + "inner.o -> o2;"
                    + "}",
            // all timings match - pass through connector
            "component c6 {"
                    + "port in int i;"
                    + "port out int o;"
                    + "i -> o;"
                    + "component Inner { }"
                    + "Inner inner; "
                    + "}",
            // all timings match for an input port forward (sync -> timed)
            "component c7 {"
                    + "port sync in int i;"
                    + "component Inner {"
                    + "port in int i;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i;"
                    + "}",
            // all timings match for an output port forward (sync -> timed)
            "component c8 {"
                    + "port out int o;"
                    + "component Inner {"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner;"
                    + "inner.o -> o;"
                    + "}",
            // all timings match for a pass through connector (sync -> timed)
            "component c9 {"
                    + "port sync in int i;"
                    + "port out int o;"
                    + "i -> o;"
                    + "}",
            // all timings match for a hidden connector (sync -> timed)
            "component c10 {"
                    + "component Inner {"
                    + "port in int i;"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner1, inner2;"
                    + "inner1.o -> inner2.i;" // sync -> timed
                    + "}",
            // all timings match for an input port forward (sync -> timed) - multiple targets
            "component c11 {"
                    + "port sync in int i;"
                    + "component Inner {"
                    + "port sync in int i1;"
                    + "port in int i2;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i1;"
                    + "i -> inner.i2;"
                    + "}",
            // all timings match for an output port forward (sync -> timed) - multiple targets
            "component c12 {"
                    + "port sync out int o1;"
                    + "port out int o2;"
                    + "component Inner {"
                    + "port sync out int o;"
                    + "}"
                    + "Inner inner;"
                    + "inner.o -> o1;"
                    + "inner.o -> o2;"
                    + "}",
            // all timings match for a hidden connector (sync -> timed) - multiple targets
            "component c13 {"
                    + "component Source {"
                    + "port sync out int o;"
                    + "}"
                    + "component Sink {"
                    + "port sync in int i1;"
                    + "port in int i2;"
                    + "}"
                    + "Source source;"
                    + "Sink sink;"
                    + "source.o -> sink.i1;"
                    + "source.o -> sink.i2;"
                    + "}",
            // mismatched timing for a hidden connector (sync -> untimed)
            // the automaton defines the timing for the outgoing port
            "component c14 {"
                    + "component Source {"
                    + "port sync out int o;"
                    + "}"
                    + "component Sink {"
                    + "port in int i;"
                    + "automaton { }"
                    + "}"
                    + "Source source;"
                    + "Sink sink;"
                    + "source.o -> sink.i;"
                    + "}",
            // mismatched timing for an output port forward (sync -> untimed)
            // automaton override for incoming port
            "component c15 {"
                    + "port out int o;"
                    + "component Inner {"
                    + "port in int i;"
                    + "port sync out int o;"
                    + "automaton { }"
                    + "}"
                    + "Inner inner;"
                    + "inner.o -> o;"
                    + "}",
    })
    public void shouldNotReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = MontiArcMill.parser()
                .parse_StringMACompilationUnit(model).orElseThrow();
        MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
        MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
        MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new ConnectorTimingsFit4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(Log.getFindings()).isEmpty();
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // all timing match (sync)
    "component c1 {" +
      "feature f1;"
      + "port sync in int i;"
      + "port sync out int o;"
      + "component Inner {"
      + "feature f1;"
      + "varif(f1){port sync in int i;"
      + "port sync out int o;}" +
      "constraint(f1);"
      + "}"
      + "Inner inner1, inner2;"
      + "varif(f1){i -> inner1.i;}"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;"
      + "constraint(f1 = inner1.f1);"
      + "}",
    // all timing match (timed)
    "component c2 {" +
      "feature f1;"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner1, inner2;" +
      "varif(f1){"
      + "i -> inner1.i;"
      + "inner1.o -> inner2.i;"
      + "inner2.o -> o;}"
      + "}",
    // all timing match (timed)
    "component c3 {" +
      "feature f1,f2;"
      + "port in int i;"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port out int o;"
      + "}"
      + "Inner inner;"
      + "varif(f1){ i -> inner.i;}"
      + "varif(f2){ inner.o -> o;}" +
      "constraint(f1 && f2);"
      + "}",
    // all timing match (sync)
    "component c4 {" +
      "feature f1,f2;"
      + "port sync in int i;"
      + "port sync out int o;"
      + "component Inner {"
      + "port sync in int i;"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "varif(f1){ i -> inner.i; }"
      + "varif(f2){ inner.o -> o; }" +
      "constraint(f1 || f2);"
      + "}",
    // all timing match (sync) - multiple targets
    "component c5 {" +
      "feature f1;"
      + "port sync in int i;"
      + "port sync out int o1;"
      + "port sync out int o2;"
      + "component Inner {"
      + "port sync in int i1;"
      + "port sync in int i2;"
      + "port sync out int o;"
      + "}"
      + "Inner inner;" +
      "varif(f1){"
      + "i -> inner.i1;"
      + "i -> inner.i2;"
      + "inner.o -> o1;"
      + "inner.o -> o2;}"
      + "}",
    // all timings match - pass through connector
    "component c6 {" +
      "feature f1;"
      + "port in int i;"
      + "port out int o;"
      + "varif(f1){a.b.A a;}else{i -> o;}"
      + "component Inner { }"
      + "Inner inner; " +
      "constraint(!f1);"
      + "}",
    // all timings match for an input port forward (sync -> timed)
    "component c7 {" +
      "feature f1,f2;"
      + "port sync in int i;"
      + "component Inner {"
      + "port in int i;"
      + "}"
      + "Inner inner;"
      + "varif(f1){" +
      "a.b.A a;" +
      "varif(f2){i -> inner.i;}}"
      + "}",
    // all timings match for an output port forward (sync -> timed)
    "component c8 {" +
      "feature f1,f2;"
      + "port out int o;"
      + "component Inner {"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "varif(f1){" +
      "varif(f2){a.b.A a;}else{inner.o -> o;}}" +
      "constraint(f1 && !f2);"
      + "}",
    // all timings match for a pass through connector (sync -> timed)
    "component c9 {" +
      "feature f1;"
      + "port sync in int i;"
      + "port out int o;"
      + "varif(f1){ i -> o; }"
      + "}",
    // all timings match for a hidden connector (sync -> timed)
    "component c10 {" +
      "feature f1,f2,f3;"
      + "component Inner {"
      + "port in int i;"
      + "port sync out int o;"
      + "}"
      + "Inner inner1, inner2;"
      + "varif(f1){" +
      "varif(f2){" +
      "varif(f3){inner1.o -> inner2.i;}}}" +
      "constraint(f1 && f2 && f3);" // sync -> timed
      + "}",
    // all timings match for an input port forward (sync -> timed) - multiple targets
    "component c11 {" +
      "feature f1,f2;"
      + "port sync in int i;"
      + "component Inner {"
      + "port sync in int i1;"
      + "port in int i2;"
      + "}"
      + "Inner inner;"
      + "varif(f1){ i -> inner.i1; }"
      + "varif(f2){ i -> inner.i2; }"
      + "}",
    // all timings match for an output port forward (sync -> timed) - multiple targets
    "component c12 {" +
      "feature f1;"
      + "port sync out int o1;"
      + "port out int o2;"
      + "component Inner {"
      + "port sync out int o;"
      + "}"
      + "Inner inner;"
      + "varif(f1){inner.o -> o1;}"
      + "inner.o -> o2;" +
      "constraint(f1);"
      + "}",
    // all timings match for a hidden connector (sync -> timed) - multiple targets
    "component c13 {" +
      "feature f1,f2;"
      + "component Source {"
      + "port sync out int o;"
      + "}"
      + "component Sink {"
      + "port sync in int i1;"
      + "port in int i2;"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "varif(f1){ source.o -> sink.i1;}"
      + "varif(f2){ source.o -> sink.i2;}" +
      "constraint(f1 && f2);"
      + "}",
    // mismatched timing for a hidden connector (sync -> untimed)
    // the automaton defines the timing for the outgoing port
    "component c14 {" +
      "feature f1;"
      + "component Source {"
      + "port sync out int o;"
      + "}"
      + "component Sink {"
      + "port in int i;"
      + "automaton { }"
      + "}"
      + "varif(f1){Source source;"
      + "Sink sink;}"
      + "source.o -> sink.i;" +
      "constraint(f1);"
      + "}",
    // mismatched timing for an output port forward (sync -> untimed)
    // automaton override for incoming port
    "component c15 {" +
      "feature f1,f2;"
      + "port out int o;"
      + "component Inner {"
      + "port in int i;"
      + "port sync out int o;"
      + "automaton { }"
      + "}"
      + "varif(f1){ Inner inner; }"
      + "varif(f2){ inner.o -> o; }" +
      "constraint(f1 && f2);"
      + "}",
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElseThrow();
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTimingsFit());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

    @ParameterizedTest
    @ValueSource(strings = {
            // mismatched timing for an output port forward (timed -> sync)
            "component c1 {"
                    + "port sync out int o;"
                    + "component Inner {"
                    + "port out int o;"
                    + "}"
                    + "Inner inner;"
                    + "inner.o -> o;"
                    + "}",
            // mismatched timing for a hidden connector (timed -> sync)
            "component c2 {"
                    + "component Inner {"
                    + "port sync in int i;"
                    + "port out int o;"
                    + "}"
                    + "Inner inner1, inner2;"
                    + "inner1.o -> inner2.i;" // timed -> sync
                    + "}",
            // mismatched timing for a hidden connector (untimed -> sync)
            "component c3 {"
                    + "component Source {"
                    + "port out int o;"
                    + "}"
                    + "component Sink {"
                    + "port sync in int i;"
                    + "}"
                    + "Source source;"
                    + "Sink sink;"
                    + "source.o -> sink.i;"
                    + "}",
            // mismatched timing for an input port forward (untimed -> sync)
            "component c4 {"
                    + "port in int i;"
                    + "component Inner {"
                    + "port sync in int i;"
                    + "port out int o;"
                    + "}"
                    + "Inner inner;"
                    + "i -> inner.i;"
                    + "}",
            // mismatched timing for a pass through connector with default source timing (timed -> sync)
            "component c5 {"
                    + "port in int i;"
                    + "port sync out int o;"
                    + "i -> o;"
                    + "component Inner { }"
                    + "Inner inner; "
                    + "}"
    })
    public void shouldReportError(@NotNull String model) throws IOException {
        Preconditions.checkNotNull(model);

        // Given
        ASTMACompilationUnit ast = MontiArcMill.parser()
                .parse_StringMACompilationUnit(model).orElse(null);
        Preconditions.checkNotNull(ast);
        MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
        MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
        MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

        MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
        checker.addCoCo(new ConnectorTimingsFit4Family());

        // When
        checker.checkAll(ast);

        // Then
        assertThat(getLoggedErrorCodes())
                .containsExactlyInAnyOrder(getErrorCodes(ArcError.CONNECTOR_TIMING_MISMATCH));
    }

  @ParameterizedTest
  @ValueSource(strings = {
    // mismatched timing for an output port forward (timed -> sync)
    "component c1 {" +
      "feature f1,f2;"
      + "port sync out int o;"
      + "varif(f2){component Inner {"
      + "port out int o;"
      + "}"
      + "Inner inner;}"
      + "varif(f1){inner.o -> o;}"
      + "}",
    // mismatched timing for a hidden connector (timed -> sync)
    "component c2 {" +
      "feature f1,f2;"
      + "component Inner {"
      + "port sync in int i;"
      + "port out int o;"
      + "}"
      + "varif(f1){Inner inner1, inner2;}"
      + "varif(f2){inner1.o -> inner2.i;}" +
      "constraint(f1);" // timed -> sync
      + "}",
    // mismatched timing for a hidden connector (untimed -> sync)
    "component c3 {" +
      "feature f1;"
      + "component Source {"
      + "port out int o;"
      + "}"
      + "component Sink {"
      + "port sync in int i;"
      + "}"
      + "Source source;"
      + "Sink sink;"
      + "varif(f1){a.b.A a;}else{source.o -> sink.i;}"
      + "}",
    // mismatched timing for an input port forward (untimed -> sync)
    "component c4 {" +
      "feature f1,f2;"
      + "port in int i;"
      + "component Inner {"
      + "port sync in int i;"
      + "port out int o;"
      + "}"
      + "varif(f1){Inner inner;"
      + "varif(f2){i -> inner.i;}}" +
      "constraint(f1 && f2);"
      + "}",
    // mismatched timing for a pass through connector with default source timing (timed -> sync)
    "component c5 {" +
      "feature f1;"
      + "port in int i;"
      + "port sync out int o;"
      + "varif(f1){i -> o;}"
      + "component Inner { }"
      + "Inner inner; " +
      "constraint(f1);"
      + "}"
  })
  public void shouldReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcMill.parser()
      .parse_StringMACompilationUnit(model).orElse(null);
    Preconditions.checkNotNull(ast);
    MontiArcMill.scopesGenitorDelegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ast);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ast);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorTimingsFit4Family());


    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(ArcError.CONNECTOR_TIMING_MISMATCH));
  }

}

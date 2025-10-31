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
import variablearc._cocos.ConnectorDirectionsFit4Family;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConnectorDirectionsFit4FamilyTest extends MontiArcTestBase {

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
    compile("package a.b; component B { port in int i; port out int o; }");
    compile("package a.b; component C { port in int i; port <<delayed>> out int o; }");
    compile("package a.b; component D { port in int i1, i2; port out int o; }");
    compile("package a.b; component Z { a.b.A a1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> a1.i;} }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg(                   "component Comp7 { " +
      "feature f1,f2;" +
      "varif(f1){port in int i1, i2, i3; " +
      "varif(f2){a.b.A a;} else{i1 -> i2, i3; }}" +
      "constraint(f1 && !f2);" +
      "}");
    //arg("package a.b; component C1 { feature f1,f2; port out int i1; port in int i2; a.b.Z z5; i1 -> z5.i; varif(f1){a.b.Z z1; i2 -> z5.i;  } constraint(!f1); }");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());

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
    "component Comp1 { " +
      "a.b.A a(); " +
      "}",
    "component Comp2 { " +
      "port in int i; " +
      "port out int o; " +
      "a.b.B b; " +
      "i -> b.i; " +
      "b.o -> o; " +
      "}",
    "component Comp3 { " +
      "a.b.B b; " +
      "a.b.C c; " +
      "b.o -> c.i; " +
      "c.o -> b.i; " +
      "}",
    "component Comp4 { " +
      "port in int i; " +
      "port out int o; " +
      "component Inner { " +
      "port in int i; " +
      "port out int o; " +
      "} " +
      "Inner sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "}",
    "component Comp5 { " +
      "port in int i; " +
      "port out int o1, o2; " +
      "a.b.D d; " +
      "i -> d.i1; " +
      "i -> d.i2; " +
      "d.o -> o1, o2; " +
      "}",
    "component Comp6 { " +
      "port in int i; " +
      "port out int o; " +
      "i -> o; " +
      "}",
    "component Comp7 { " +
      "port in int i; " +
      "port out int o1, o2; " +
      "i -> o1, o2; " +
      "}"
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConnectorDirectionsFit4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "component Comp1 { " +
      "feature f1;" +
      "varif(f1){a.b.A a(); }" +
      "}",
    "component Comp2 { " +
      "feature f1,f2,f3;" +
      "varif(f1){ port in int i; }" +
      "varif(f2){ port out int o; }" +
      "a.b.B b; " +
      "varif(f3){ i -> b.i; " +
      "b.o -> o; }" +
      "constraint(f1 && f2 && f3);" +
      "}",
    "component Comp3 {" +
      "feature f1,f2; " +
      "varif(f1){a.b.A a;}else{a.b.B b; " +
      "a.b.C c;}" +
      "varif(f2){b.o -> c.i; " +
      "c.o -> b.i; }" +
      "constraint(!f1 && f2);" +
      "}",
    "component Comp4 { " +
      "feature f1;" +
      "varif(f1){port in int i; " +
      "port out int o; }" +
      "component Inner {" +
      "feature f1; " +
      "varif(f1){port in int i; " +
      "port out int o; }" +
      "constraint(f1);" +
      "} " +
      "Inner sub; " +
      "i -> sub.i; " +
      "sub.o -> o; " +
      "constraint(f1 == sub.f1);" +
      "}",
    "component Comp5 { " +
      "feature f1,f2;" +
      "port in int i; " +
      "port out int o1, o2; " +
      "a.b.D d; " +
      "varif(f1){ i -> d.i1; }" +
      "varif(f2){ i -> d.i2; }" +
      "d.o -> o1, o2; " +
      "constraint(f1 && f2);" +
      "}",
    "component Comp6 { " +
      "feature f1;" +
      "port in int i; " +
      "port out int o; " +
      "varif(f1){i -> o; }" +
      "}",
    "component Comp7 {" +
      "feature f1; " +
      "port in int i; " +
      "port out int o1, o2; " +
      "varif(f1){a.b.A a;}else{i -> o1, o2; }" +
      "constraint(!f1);" +
      "}"
  })
  public void shouldNotReportErrorWithVariability(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());

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
    checker.addCoCo(new ConnectorDirectionsFit4Family());

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
          "port in int i1; " +
          "port in int i2; " +
          "a.b.B b; " +
          "i1 -> b.i; " +
          "i2 -> b.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp2 { " +
          "port out int o1; " +
          "port out int o2; " +
          "a.b.B b; " +
          "b.i -> o1; " +
          "b.o -> o2; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp3 { " +
          "port in int i1, i2; " +
          "i1 -> i2; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp4 { " +
          "port out int o1, o2; " +
          "o1 -> o2; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp5 { " +
          "port in int i1, i2; " +
          "port out int o; " +
          "i1 -> i2, o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp6 { " +
          "port in int i1, i2; " +
          "port out int o; " +
          "i1 -> o, i2; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp7 { " +
          "port in int i1, i2, i3; " +
          "i1 -> i2, i3; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp8 { " +
          "port in int i1, i2, i3, i4; " +
          "i1 -> i3; " +
          "i2 -> i4; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp9 { " +
          "port in int i1, i2; " +
          "a.b.B b1, b2; " +
          "i1 -> b1.i; " +
          "i2 -> b2.i; " +
          "b1.o -> b2.o; " +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp10 { " +
          "port out int o1, o2; " +
          "a.b.B b1, b2; " +
          "b1.o -> o1; " +
          "b2.o -> o2; " +
          "b1.i -> b2.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp11 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "i -> b1.i; " +
          "b2.i -> b1.o; " +
          "b2.o -> p; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp12 { " +
          "port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "o -> b1.o; " +
          "b1.i -> b2.o; " +
          "b1.i -> i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp13 { " +
          "port in int i; " +
          "port out int o; " +
          "component Inner {" +
          "port in int i; " +
          "port out int o; " +
          "} " +
          "Inner sub; " +
          "i -> sub.o; " +
          "o -> sub.i; " +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH)
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
    checker.get4FullVariant().addCoCo(new ConnectorDirectionsFit4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      arg("component Comp1 { " +
          "feature f1,f2;" +
          "varif(f1){port in int i1; " +
          "port in int i2; }" +
          "a.b.B b; " +
          "varif(f2){i1 -> b.i; " +
          "i2 -> b.o; }" +
          "constraint(f1 && f2);" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp2 { " +
          "feature f1,f2;" +
          "varif(f1){a.b.A a;}else{port out int o1; }" +
          "port out int o2; " +
          "varif(f2){a.b.B b; " +
          "b.i -> o1; " +
          "b.o -> o2; }" +
          "constraint(!f1 && f2);" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp3 { " +
          "feature f1;" +
          "port in int i1, i2; " +
          "varif(f1){ i1 -> i2; }" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp4 { " +
          "feature f1,f2;" +
          "varif(f1){" +
          "port out int o1, o2;" +
          "varif(f2){" +
          "o1 -> o2; }}" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp5 { " +
          "feature f1,f2;" +
          "varif(f1){port in int i1, i2; " +
          "port out int o;" +
          "varif(f2){ " +
          "i1 -> i2, o; }}" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp6 { " +
          "feature f1;" +
          "port in int i1, i2; " +
          "port out int o; " +
          "varif(f1){i1 -> o, i2; }" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp7 { " +
          "feature f1,f2;" +
          "varif(f1){port in int i1, i2, i3; " +
          "varif(f2){a.b.A a;} else{i1 -> i2, i3; }}" +
          "constraint(f1 && !f2);" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp8 {" +
          "feature f1,f2;" +
          "port in int i1, i2, i3, i4; " +
          "varif(f1){i1 -> i3; }" +
          "varif(f2){i2 -> i4; }" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp9 { " +
          "port in int i1, i2; " +
          "feature f1,f2;" +
          "a.b.B b1, b2; " +
          "varif(f1){" +
          "i1 -> b1.i; " +
          "i2 -> b2.i; }" +
          "varif(f2){" +
          "b1.o -> b2.o; }" +
          "}",
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp10 { " +
          "feature f1,f2;" +
          "varif(f1){port out int o1, o2; }" +
          "a.b.B b1, b2; " +
          "varif(f2){b1.o -> o1; " +
          "b2.o -> o2; " +
          "b1.i -> b2.i; }" +
          "constraint(f1);" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH),
      arg("component Comp11 {" +
          "feature f1; " +
          "varif(f1){port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "i -> b1.i; " +
          "b2.i -> b1.o; " +
          "b2.o -> p; }" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp12 { " +
          "feature f1,f2,f3;" +
          "port in int i; " +
          "port out int o; " +
          "a.b.B b1, b2; " +
          "varif(f1){o -> b1.o; }" +
          "varif(f2){b1.i -> b2.o;} " +
          "varif(f3){b1.i -> i; }" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH),
      arg("component Comp13 {" +
          "feature f1; " +
          "port in int i; " +
          "port out int o; " +
          "component Inner {" +
          "feature f1;" +
          "port in int i; " +
          "port out int o; " +
          "constraint(f1);" +
          "} " +
          "Inner sub; " +
          "varif(f1){i -> sub.o; " +
          "o -> sub.i; }" +
          "constraint(f1 == sub.f1);" +
          "}",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH)
    );
  }
}

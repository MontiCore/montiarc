/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

public class NoPortInSubcomponentArgument4FamilyTest extends MontiArcTestBase {

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
    compile("package a.b; component A {feature f1,f2; constraint(f1); }");
    compile("package a.b; component B { port in int i; port out int o; }");
    compile("package a.b; component C { port in int i; port <<delayed>> out int o; }");
    compile("package a.b; component D { port in int i1, i2; port out int o; }");
    compile("package a.b; component Z { a.b.A a1; feature f1;  port in int i; port out int o; varif(f1){port out int k; o -> a1.i;} }");
  }

  private static Stream<Arguments> provideUniqueSenderModel() {
    List<Arguments> componentList = new ArrayList<>();
    Arguments simpleModel = arg(          "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6(OOTypeWithFieldIO p) { " +
      "feature f1,f2;" +
      "  port in int i; " +
      "  port out int o; " +
      " varif(f1){ ComponentTypeWithIntParameter sub1(p.i); " +
      "varif(f2){" +
      "  ComponentTypeWithIntParameter sub2(p.o); " +
      "  ComponentTypeWithIntParameter sub3(p.i), sub4(p.i); " +
      "  ComponentTypeWithIntParameter sub3(p.o), sub4(p.o); }}" +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new NoPortInSubcomponentArgument4Family());

    ASTMACompilationUnit mainAST = compile(model);
    checker.checkAll(mainAST);

    String[] test = getLoggedErrorCodes();

    // Then
    assertThat(Log.getErrorCount() == 0);
  }

  private static <T> void addCoCoAs(T coco, Consumer<T> consumer) {
    consumer.accept(coco);
  }


  final static String SYMBOLS_DIR = "symbols";

  @BeforeEach
  public void setUp() {
    MontiArcMill.globalScope().setSymbolPath(
      new MCPath(Paths.get(TEST_RESOURCE, SYMBOLS_DIR))
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - No ports, no expressions
    "component Comp1 { }",
    // 2 - No expressions
    "component Comp2 { " +
      "  port in int i; " +
      "  port out int o; " +
      "}",
    // 3 - Subcomponent instantiation
    "import montiarc.test.ComponentType; " +
      "component Comp4 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentType sub; " +
      "}",
    // 4 - Subcomponent instantiation with literal argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp4 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(1); " +
      "  ComponentTypeWithIntParameter sub2(1), sub3(-1); " +
      "}",
    // 5 - Subcomponent instantiation with parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp5(int p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(p); " +
      "  ComponentTypeWithIntParameter sub2(p), sub3(p); " +
      "}",
    // 6 - Subcomponent instantiation with oo-type with field parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6(OOTypeWithFieldIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(p.i); " +
      "  ComponentTypeWithIntParameter sub2(p.o); " +
      "  ComponentTypeWithIntParameter sub3(p.i), sub4(p.i); " +
      "  ComponentTypeWithIntParameter sub3(p.o), sub4(p.o); " +
      "}",
    // 7 - Subcomponent instantiation with oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp7(OOTypeWithFunctionIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(p.i()); " +
      "  ComponentTypeWithIntParameter sub2(p.o()); " +
      "  ComponentTypeWithIntParameter sub3(p.i()), sub4(p.i()); " +
      "  ComponentTypeWithIntParameter sub5(p.o()), sub6(p.o()); " +
      "}",
    // 8 - Subcomponent instantiation with static field argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(OOTypeWithStaticFieldIO.i); " +
      "  ComponentTypeWithIntParameter sub2(OOTypeWithStaticFieldIO.o); " +
      "  ComponentTypeWithIntParameter sub3(OOTypeWithStaticFieldIO.i), sub4(OOTypeWithStaticFieldIO.i); " +
      "  ComponentTypeWithIntParameter sub5(OOTypeWithStaticFieldIO.o), sub6(OOTypeWithStaticFieldIO.o); " +
      "}",
    // 9 - Subcomponent instantiation with static function argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntParameter sub1(OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntParameter sub2(OOTypeWithStaticFunctionIO.o()); " +
      "  ComponentTypeWithIntParameter sub3(OOTypeWithStaticFunctionIO.i()), sub4(OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntParameter sub5(OOTypeWithStaticFunctionIO.o()), sub6(OOTypeWithStaticFunctionIO.o()); " +
      "}",
    // 10 - Subcomponent instantiation with named parameter and literal argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = 1); " +
      "  ComponentTypeWithIntIOParameters sub2(o = 1); " +
      "  ComponentTypeWithIntIOParameters sub3(i = 1), sub4(i = -1); " +
      "  ComponentTypeWithIntIOParameters sub5(o = 1), sub6(o = -1); " +
      "}",
    // 11 - Subcomponent instantiation with named parameter and parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp11(int p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = p); " +
      "  ComponentTypeWithIntIOParameters sub2(o = p); " +
      "  ComponentTypeWithIntIOParameters sub3(i = p), sub4(i = p); " +
      "  ComponentTypeWithIntIOParameters sub5(o = p), sub6(o = p); " +
      "}",
    // 12 - Subcomponent instantiation with named parameter and oo-type with field parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp12(OOTypeWithFieldIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = p.i); " +
      "  ComponentTypeWithIntIOParameters sub2(o = p.o); " +
      "  ComponentTypeWithIntIOParameters sub3(i = p.o); " +
      "  ComponentTypeWithIntIOParameters sub4(o = p.i); " +
      "  ComponentTypeWithIntIOParameters sub5(i = p.i), sub6(i = p.i); " +
      "  ComponentTypeWithIntIOParameters sub7(o = p.o), sub8(o = p.o); " +
      "}",
    // 13 - Subcomponent instantiation with named parameter and oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp13(OOTypeWithFunctionIO p) { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub2(o = p.o()); " +
      "  ComponentTypeWithIntIOParameters sub3(i = p.o()); " +
      "  ComponentTypeWithIntIOParameters sub4(o = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub5(i = p.i()), sub6(i = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub7(o = p.o()), sub8(o = p.o()); " +
      "}",
    // 14 - Subcomponent instantiation with named parameter and static field argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp14 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFieldIO.i); " +
      "  ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFieldIO.o); " +
      "  ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFieldIO.o); " +
      "  ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFieldIO.i); " +
      "  ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFieldIO.i), sub6(i = OOTypeWithStaticFieldIO.i); " +
      "  ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFieldIO.o), sub8(o = OOTypeWithStaticFieldIO.o); " +
      "}",
    // 15 - Subcomponent instantiation with named parameter and static function argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp15 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFunctionIO.o()); " +
      "  ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFunctionIO.o()); " +
      "  ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFunctionIO.i()), sub6(i = OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFunctionIO.o()), sub8(o = OOTypeWithStaticFunctionIO.o()); " +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSubcomponentArgument4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - No ports, no expressions
    "component Comp1 { feature f1; }",
    // 2 - No expressions
    "component Comp2 {" +
      "feature f1; " +
      "varif(f1){  port in int i; " +
      "  port out int o; }" +
      "}",
    // 3 - Subcomponent instantiation
    "import montiarc.test.ComponentType; " +
      "component Comp4 {" +
      "feature f1,f2; " +
      " varif(f1){ port in int i; " +
      "  port out int o; }" +
      "  varif(f2){ComponentType sub; }" +
      "constraint(f1 && f2);" +
      "}",
    // 4 - Subcomponent instantiation with literal argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp4 {" +
      "feature f1,f2; " +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(f1){ComponentTypeWithIntParameter sub1(1); }" +
      "  varif(f2){ComponentTypeWithIntParameter sub2(1), sub3(-1); }" +
      "}",
    // 5 - Subcomponent instantiation with parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "component Comp5(int p) { " +
      "feature f1;" +
      "  port in int i; " +
      "  port out int o; " +
      " varif(f1){}else{ ComponentTypeWithIntParameter sub1(p); " +
      "  ComponentTypeWithIntParameter sub2(p), sub3(p);} " +
      "}",
    // 6 - Subcomponent instantiation with oo-type with field parameter argument
//    "import montiarc.test.ComponentTypeWithIntParameter; " +
//      "import montiarc.test.OOTypeWithFieldIO; " +
//      "component Comp6(OOTypeWithFieldIO p) { " +
//      "feature f1,f2;" +
//      "  port in int i; " +
//      "  port out int o; " +
//      " varif(f1){ ComponentTypeWithIntParameter sub1(p.i); " +
//      "varif(f2){" +
//      "  ComponentTypeWithIntParameter sub2(p.o); " +
//      "  ComponentTypeWithIntParameter sub3(p.i), sub4(p.i); " +
//      "  ComponentTypeWithIntParameter sub3(p.o), sub4(p.o); }}" +
//      "}",
    // 7 - Subcomponent instantiation with oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp7(OOTypeWithFunctionIO p) { " +
      "feature f1,f2;" +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(f1){ComponentTypeWithIntParameter sub1(p.i()); " +
      "  ComponentTypeWithIntParameter sub2(p.o()); " +
      "  varif(f2){}else{ComponentTypeWithIntParameter sub3(p.i()), sub4(p.i()); " +
      "  ComponentTypeWithIntParameter sub5(p.o()), sub6(p.o()); }}" +
      "}",
    // 8 - Subcomponent instantiation with static field argument
//    "import montiarc.test.ComponentTypeWithIntParameter; " +
//      "import montiarc.test.OOTypeWithStaticFieldIO; " +
//      "component Comp8 {" +
//      "feature f1; " +
//      "  port in int i; " +
//      "  port out int o; " +
//      " varif(f1){ ComponentTypeWithIntParameter sub1(OOTypeWithStaticFieldIO.i); " +
//      "  ComponentTypeWithIntParameter sub2(OOTypeWithStaticFieldIO.o); " +
//      "  ComponentTypeWithIntParameter sub3(OOTypeWithStaticFieldIO.i), sub4(OOTypeWithStaticFieldIO.i); " +
//      "  ComponentTypeWithIntParameter sub5(OOTypeWithStaticFieldIO.o), sub6(OOTypeWithStaticFieldIO.o);} " +
//      "}",
    // 9 - Subcomponent instantiation with static function argument
    "import montiarc.test.ComponentTypeWithIntParameter; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 {" +
      "feature f1,f2,f3; " +
      "  port in int i; " +
      "  port out int o; " +
      " varif(f1){ ComponentTypeWithIntParameter sub1(OOTypeWithStaticFunctionIO.i()); " +
      " varif(f2){ ComponentTypeWithIntParameter sub2(OOTypeWithStaticFunctionIO.o()); " +
      " varif(f3){ ComponentTypeWithIntParameter sub3(OOTypeWithStaticFunctionIO.i()), sub4(OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntParameter sub5(OOTypeWithStaticFunctionIO.o()), sub6(OOTypeWithStaticFunctionIO.o()); }}}" +
      "}",
    // 10 - Subcomponent instantiation with named parameter and literal argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp10 { " +
      "feature f1,f2,f3,f4;" +
      "  port in int i; " +
      "  port out int o; " +
      "  varif(f1){ ComponentTypeWithIntIOParameters sub1(i = 1); " +
      "  varif(f2){ ComponentTypeWithIntIOParameters sub2(o = 1); " +
      "  varif(f3){ ComponentTypeWithIntIOParameters sub3(i = 1), sub4(i = -1); " +
      "  varif(f4){ ComponentTypeWithIntIOParameters sub5(o = 1), sub6(o = -1); }}}}" +
      "}",
    // 11 - Subcomponent instantiation with named parameter and parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "component Comp11(int p) {" +
      "feature f1; " +
      " varif(f1){ port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = p); " +
      "  ComponentTypeWithIntIOParameters sub2(o = p); " +
      "  ComponentTypeWithIntIOParameters sub3(i = p), sub4(i = p); " +
      "  ComponentTypeWithIntIOParameters sub5(o = p), sub6(o = p); } " +
      "}",
    // 12 - Subcomponent instantiation with named parameter and oo-type with field parameter argument
//    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
//      "import montiarc.test.OOTypeWithFieldIO; " +
//      "component Comp12(OOTypeWithFieldIO p) {" +
//      "feature f1,f2,f3; " +
//      "  port in int i; " +
//      "  port out int o; " +
//      "  varif(f1){ ComponentTypeWithIntIOParameters sub1(i = p.i); " +
//      "  varif(f2){ ComponentTypeWithIntIOParameters sub2(o = p.o); " +
//      "  varif(f3){ ComponentTypeWithIntIOParameters sub3(i = p.o); " +
//      "  ComponentTypeWithIntIOParameters sub4(o = p.i); " +
//      "  ComponentTypeWithIntIOParameters sub5(i = p.i), sub6(i = p.i); " +
//      "  ComponentTypeWithIntIOParameters sub7(o = p.o), sub8(o = p.o); }}}" +
//      "constraint(f1 && f2 && f3);" +
    //     "}",
    // 13 - Subcomponent instantiation with named parameter and oo-type with function parameter argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp13(OOTypeWithFunctionIO p) { " +
      "   feature f1;" +
      "  varif(f1){ port in int i; " +
      "  port out int o; " +
      "  ComponentTypeWithIntIOParameters sub1(i = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub2(o = p.o()); " +
      "  ComponentTypeWithIntIOParameters sub3(i = p.o()); " +
      "  ComponentTypeWithIntIOParameters sub4(o = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub5(i = p.i()), sub6(i = p.i()); " +
      "  ComponentTypeWithIntIOParameters sub7(o = p.o()), sub8(o = p.o()); }" +
      "constraint(f1);" +
      "}",
    // 14 - Subcomponent instantiation with named parameter and static field argument
//    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
//      "import montiarc.test.OOTypeWithStaticFieldIO; " +
//      "component Comp14 { " +
//      "feature f1,f2,f3;" +
//      "  port in int i; " +
//      "  port out int o; " +
//      " varif(1){ ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFieldIO.i); " +
//      "  ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFieldIO.o); }" +
//      " varif(f2){  ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFieldIO.o); " +
//      "  ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFieldIO.i); }" +
//      " varif(f3){  ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFieldIO.i), sub6(i = OOTypeWithStaticFieldIO.i); " +
//      "  ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFieldIO.o), sub8(o = OOTypeWithStaticFieldIO.o); }" +
//      "}",
    // 15 - Subcomponent instantiation with named parameter and static function argument
    "import montiarc.test.ComponentTypeWithIntIOParameters; " +
      "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp15 { " +
      "  feature f1,f2,f3;" +
      "  port in int i; " +
      "  port out int o; " +
      " varif(f1){ ComponentTypeWithIntIOParameters sub1(i = OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntIOParameters sub2(o = OOTypeWithStaticFunctionIO.o()); }" +
      " varif(f2){ ComponentTypeWithIntIOParameters sub3(i = OOTypeWithStaticFunctionIO.o()); " +
      "  ComponentTypeWithIntIOParameters sub4(o = OOTypeWithStaticFunctionIO.i());} " +
      " varif(f3){ ComponentTypeWithIntIOParameters sub5(i = OOTypeWithStaticFunctionIO.i()), sub6(i = OOTypeWithStaticFunctionIO.i()); " +
      "  ComponentTypeWithIntIOParameters sub7(o = OOTypeWithStaticFunctionIO.o()), sub8(o = OOTypeWithStaticFunctionIO.o()); }" +
      "constraint(f1 && f2 && f3);" +
      "}"
  })
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new NoPortInSubcomponentArgument4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoPortInSubcomponentArgument4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No input port in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp1 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 2 - No output port in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp2 { " +
          "  port out int o; " +
          "  ComponentTypeWithIntParameter sub(o); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 3 - No port in assignment in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp3 { " +
          "  port out int o; " +
          "  ComponentTypeWithIntParameter sub((o = 1)); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 4 - No port in inc prefix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp4 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(++i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 5 - No port in dec prefix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp5 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(--i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 6 - No port in inc suffix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp6 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i++); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 7 - No port in dec suffix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp7 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i--); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 8 - No port in boolean not expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp8 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub(~i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 9 - No port in logical not expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp9 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub(!i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 10 - No port in multiply expressions (left) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp10 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i * 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 11 - No port in multiply expressions (right) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp11 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(2 * i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 12 - No port in multiply expressions (both) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp12 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i * i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 13 - No input port in infix expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp13 { " +
          "  port in int i1, i2; " +
          "  port in boolean i3, i4; " +
          "  ComponentTypeWithIntParameter sub(2 / i2);  " +
          "  ComponentTypeWithIntParameter sub2(i2 / 2); " +
          "  ComponentTypeWithIntParameter sub3(2 % i2); " +
          "  ComponentTypeWithIntParameter sub4(i2 % 2); " +
          "  ComponentTypeWithIntParameter sub5(2 + i2); " +
          "  ComponentTypeWithIntParameter sub6(i2 + 2); " +
          "  ComponentTypeWithIntParameter sub7(2 - i2); " +
          "  ComponentTypeWithIntParameter sub8(i2 - 2); " +
          "  ComponentTypeWithBooleanParameter sub9(i2 <= 2); " +
          "  ComponentTypeWithBooleanParameter sub10(2 <= i2); " +
          "  ComponentTypeWithBooleanParameter sub11(i2 >= 2); " +
          "  ComponentTypeWithBooleanParameter sub12(2 >= i2); " +
          "  ComponentTypeWithBooleanParameter sub13(i2 < 2); " +
          "  ComponentTypeWithBooleanParameter sub14(2 < i2); " +
          "  ComponentTypeWithBooleanParameter sub15(i2 > 2); " +
          "  ComponentTypeWithBooleanParameter sub16(2 > i2); " +
          "  ComponentTypeWithBooleanParameter sub17(i2 == 2); " +
          "  ComponentTypeWithBooleanParameter sub18(2 == i2); " +
          "  ComponentTypeWithBooleanParameter sub19(i2 != 2); " +
          "  ComponentTypeWithBooleanParameter sub20(2 != i2); " +
          "  ComponentTypeWithBooleanParameter sub21(i4 && 2); " +
          "  ComponentTypeWithBooleanParameter sub22(2 && i4); " +
          "  ComponentTypeWithBooleanParameter sub23(i4 || 2); " +
          "  ComponentTypeWithBooleanParameter sub24(2 || i4); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 14 - No input port in conditional expressions (condition) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp14 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithIntParameter sub(i ? -2 : 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 15 - No input port in conditional expressions (then) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp15 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(true ? i : 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 16 - No input port in conditional expressions (else) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp16 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(true ? -2 : i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 17 - No input port in bracket expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp17 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub((i)); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 18 - No input port in shift expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp18 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(i << 1); " +
          "  ComponentTypeWithIntParameter sub2(1 << i); " +
          "  ComponentTypeWithIntParameter sub3(i >> 1); " +
          "  ComponentTypeWithIntParameter sub4(1 >> i); " +
          "  ComponentTypeWithIntParameter sub5(i >>> 1); " +
          "  ComponentTypeWithIntParameter sub6(1 >>> i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 19 - No input port in binary expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp19 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub1(i & true); " +
          "  ComponentTypeWithBooleanParameter sub2(true & i); " +
          "  ComponentTypeWithBooleanParameter sub3(i ^ true); " +
          "  ComponentTypeWithBooleanParameter sub4(true ^ i); " +
          "  ComponentTypeWithBooleanParameter sub5(i | true); " +
          "  ComponentTypeWithBooleanParameter sub6(true | i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 20 - No input port in method call argument in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.FunctionWithIntParameter; " +
          "component Comp20 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithIntParameter sub(FunctionWithIntParameter(i)); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 21 - No input port in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp21 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 22 - No output port in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp22 { " +
          "  port out int o; " +
          "  ComponentTypeWithIntParameter sub(p = o); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 23 - No port in assignment in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp23 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i = 1); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 24 - No port in inc prefix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp24 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = ++i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 25 - No port in dec prefix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp25 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = --i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 26 - No port in inc suffix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp26 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i++); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 27 - No port in dec suffix expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp27 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i--); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 28 - No port in boolean not expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp28 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub(p = ~i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 29 - No port in logical not expression in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp29 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub(p = !i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 30 - No port in multiply expressions (left) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp30 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i * 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 31 - No port in multiply expressions (right) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp31 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = 2 * i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 32 - No port in multiply expressions (both) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp32 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i * i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 33 - No input port in infix expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp33 { " +
          "  port in int i1, i2; " +
          "  port in boolean i3, i4; " +
          "  ComponentTypeWithIntParameter sub(p = 2 / i2);  " +
          "  ComponentTypeWithIntParameter sub2(p = i2 / 2); " +
          "  ComponentTypeWithIntParameter sub3(p = 2 % i2); " +
          "  ComponentTypeWithIntParameter sub4(p = i2 % 2); " +
          "  ComponentTypeWithIntParameter sub5(p = 2 + i2); " +
          "  ComponentTypeWithIntParameter sub6(p = i2 + 2); " +
          "  ComponentTypeWithIntParameter sub7(p = 2 - i2); " +
          "  ComponentTypeWithIntParameter sub8(p = i2 - 2); " +
          "  ComponentTypeWithBooleanParameter sub9(p = i2 <= 2); " +
          "  ComponentTypeWithBooleanParameter sub10(p = 2 <= i2); " +
          "  ComponentTypeWithBooleanParameter sub11(p = i2 >= 2); " +
          "  ComponentTypeWithBooleanParameter sub12(p = 2 >= i2); " +
          "  ComponentTypeWithBooleanParameter sub13(p = i2 < 2); " +
          "  ComponentTypeWithBooleanParameter sub14(p = 2 < i2); " +
          "  ComponentTypeWithBooleanParameter sub15(p = i2 > 2); " +
          "  ComponentTypeWithBooleanParameter sub16(p = 2 > i2); " +
          "  ComponentTypeWithBooleanParameter sub17(p = i2 == 2); " +
          "  ComponentTypeWithBooleanParameter sub18(p = 2 == i2); " +
          "  ComponentTypeWithBooleanParameter sub19(p = i2 != 2); " +
          "  ComponentTypeWithBooleanParameter sub20(p = 2 != i2); " +
          "  ComponentTypeWithBooleanParameter sub21(p = i4 && 2); " +
          "  ComponentTypeWithBooleanParameter sub22(p = 2 && i4); " +
          "  ComponentTypeWithBooleanParameter sub23(p = i4 || 2); " +
          "  ComponentTypeWithBooleanParameter sub24(p = 2 || i4); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 34 - No input port in conditional expressions (condition) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp34 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithIntParameter sub(p = i ? -2 : 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 35 - No input port in conditional expressions (then) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp35 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = true ? i : 2); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 36 - No input port in conditional expressions (else) in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp36 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = true ? -2 : i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 37 - No input port in bracket expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp37 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = (i)); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      ),
      // 38 - No input port in shift expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "component Comp38 { " +
          "  port in int i; " +
          "  ComponentTypeWithIntParameter sub(p = i << 1); " +
          "  ComponentTypeWithIntParameter sub2(p = 1 << i); " +
          "  ComponentTypeWithIntParameter sub3(p = i >> 1); " +
          "  ComponentTypeWithIntParameter sub4(p = 1 >> i); " +
          "  ComponentTypeWithIntParameter sub5(p = i >>> 1); " +
          "  ComponentTypeWithIntParameter sub6(p = 1 >>> i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 39 - No input port in binary expressions in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
          "component Comp39 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithBooleanParameter sub1(p = i & true); " +
          "  ComponentTypeWithBooleanParameter sub2(p = true & i); " +
          "  ComponentTypeWithBooleanParameter sub3(p = i ^ true); " +
          "  ComponentTypeWithBooleanParameter sub4(p = true ^ i); " +
          "  ComponentTypeWithBooleanParameter sub5(p = i | true); " +
          "  ComponentTypeWithBooleanParameter sub6(p = true | i); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT,
        PORT_REF_IN_STATIC_CONTEXT, PORT_REF_IN_STATIC_CONTEXT
      ),
      // 40 - No input port in method call argument in subcomponent argument
      arg("import montiarc.test.ComponentTypeWithIntParameter; " +
          "import montiarc.test.FunctionWithIntParameter; " +
          "component Comp40 { " +
          "  port in boolean i; " +
          "  ComponentTypeWithIntParameter sub(p = FunctionWithIntParameter(i)); " +
          "}",
        PORT_REF_IN_STATIC_CONTEXT
      )
    );
  }

    @ParameterizedTest
    @MethodSource("invalidModelsWithVariability")
    void shouldReportErrorWithVariability(@NotNull String model,
      @NotNull Error... errors) {
      Preconditions.checkNotNull(model);
      Preconditions.checkNotNull(errors);

      // Given
      ASTMACompilationUnit ast = compile(model);

      MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
      checker.get4FullVariant().addCoCo(new NoPortInSubcomponentArgument4Family());

      // When
      checker.checkAll(ast);

      // Then
      assertThat(getLoggedErrorCodes())
        .as(() -> "Findings: " + Log.getFindings().toString())
        .containsExactlyInAnyOrder(getErrorCodes(errors));
    }

    protected static Stream<Arguments> invalidModelsWithVariability() {
      return Stream.of(
        // 1 - No input port in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp1 {" +
            "feature f1; " +
            " varif(f1){ port in int i; " +
            "  ComponentTypeWithIntParameter sub(i); }" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 2 - No output port in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter;" +
            "component Comp2 { " +
            "feature f1;" +
            "varif(f1){  port out int o; " +
            "  ComponentTypeWithIntParameter sub(o); }" +
            "constraint(f1);" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 3 - No port in assignment in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp3 { " +
            "feature f1,f2;" +
            "varif(f1){  port out int o; }" +
            "varif(f2){  ComponentTypeWithIntParameter sub((o = 1)); }" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 4 - No port in inc prefix expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp4 { " +
            "feature f1,f2;" +
            "varif(f1){  port in int i; }" +
            "varif(f2){  ComponentTypeWithIntParameter sub(++i); }" +
            "constraint(f1 && f2);" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 5 - No port in dec prefix expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp5 {" +
            "feature f1; " +
            "varif(f1){  port in int i; " +
            "  ComponentTypeWithIntParameter sub(--i); }" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 6 - No port in inc suffix expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp6 { " +
            "feature f1,f2;" +
            "varif(f1){  port in int i; }" +
            "varif(f2){  ComponentTypeWithIntParameter sub(i++); }" +
            "constraint(f1 || f2);" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 7 - No port in dec suffix expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp7 {" +
            "feature f1,f2; " +
            "varif(f1){  port in int i; " +
            "varif(f2){  ComponentTypeWithIntParameter sub(i--); }}" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 8 - No port in boolean not expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
            "component Comp8 {" +
            "feature f1,f2; " +
            "varif(f1){  port in boolean i; " +
            "varif(f2){}else{  ComponentTypeWithBooleanParameter sub(~i);}} " +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 9 - No port in logical not expression in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithBooleanParameter; " +
            "component Comp9 { " +
            "feature f1,f2;" +
            "varif(f1){ port in boolean i; " +
            " varif(f2){}else{ ComponentTypeWithBooleanParameter sub(!i); }}" +
            "constraint(f1 && !f2);" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ),
        // 10 - No port in multiply expressions (left) in subcomponent argument
        arg("import montiarc.test.ComponentTypeWithIntParameter; " +
            "component Comp10 { " +
            "feature f1,f2;" +
            " varif(f1){}else{ port in int i; " +
            "  varif(f2){ComponentTypeWithIntParameter sub(i * 2); }}" +
            "}",
          PORT_REF_IN_STATIC_CONTEXT
        ));
    }
}

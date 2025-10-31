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

import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;
import static org.assertj.core.api.Assertions.assertThat;

public class NoNonSyncInputPortInCompute4FamilyTest extends MontiArcTestBase {

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
    Arguments simpleModel = arg(                "component Comp1 { " +
      "  port out int o; " +
      "  automaton { " +
      "    initial { o = 0; } " +
      "    state S; " +
      "  } " +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new NoNonSyncInputPortInCompute4Family());

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
    // 1 - Write literal to output port in compute block
    "component Comp1 { " +
      "  port out int o; " +
      "  compute { o = 0; } " +
      "}",
    // 2 - Write literal to synchronous output port in compute block
    "component Comp2 { " +
      "  port sync out int o; " +
      "  compute { o = 0; } " +
      "}",
    // 3 - Write value of component variable to output port in compute block
    "component Comp3 { " +
      "  port out int o; " +
      "  int v = 0; " +
      "  compute { o = v; } " +
      "}",
    // 5 - Write value of synchronous port to output port in compute block
    "component Comp4 { " +
      "  port sync in int i; " +
      "  port out int o; " +
      "  compute { o = i; } " +
      "}",
    // 5 - Write value of synchronous ports to output port in compute block
    "component Comp5 { " +
      "  port sync in int i1, i2; " +
      "  port out int o; " +
      "  compute { o = i1 + i2; } " +
      "}",
    // 6 - Read value from and write to field in compute block
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  compute { " +
      "    o = v.i; o = v.o; " +
      "    v.i = 0; v.i +=1; v.i++; --v.i; " +
      "    v.o = 0; v.o +=1; v.o++; --v.o; " +
      "  } " +
      "}",
    // 7 - Read value from method call in compute block
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp7 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  compute { " +
      "    o = v.i(); o = v.o(); " +
      "  } " +
      "}",
    // 8 - Read value from and write to static field in compute block
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = OOTypeWithStaticFieldIO.i; " +
      "    o = OOTypeWithStaticFieldIO.o; " +
      "  } " +
      "}",
    // 9 - Read value from static method call in compute block
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = OOTypeWithStaticFunctionIO.i(); " +
      "    o = OOTypeWithStaticFunctionIO.o(); " +
      "  } " +
      "}",
    // 10 - Variable declaration shadows port in compute block
    "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    int i = 0; " +
      "    o = i; " +
      "  } " +
      "}",
    // 11 - For control shadows port in compute block
    "component Comp11 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    for (int i = 0; i < 10; i++) { " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoNonSyncInputPortInCompute4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - Write literal to output port in compute block
    "component Comp1 {" +
      "feature f1; " +
      "varif(f1){  port out int o; " +
      "  compute { o = 0; } }" +
      "}",
    // 2 - Write literal to synchronous output port in compute block
    "component Comp2 {" +
      "feature f1; " +
      "varif(f1){  port sync out int o; " +
      "  compute { o = 0; } }" +
      "constraint(f1);" +
      "}",
    // 3 - Write value of component variable to output port in compute block
    "component Comp3 {" +
      "feature f1; " +
      "varif(f1){}else{  port out int o; " +
      "  int v = 0; " +
      "  compute { o = v; } }" +
      "}",
    // 5 - Write value of synchronous port to output port in compute block
    "component Comp4 {" +
      "feature f1,f2; " +
      "varif(f1){  port sync in int i; " +
      "  port out int o; }" +
      "varif(f2){  compute { o = i; } }" +
      "constraint(f1);" +
      "}",
    // 5 - Write value of synchronous ports to output port in compute block
    "component Comp5 {" +
      "feature f1,f2; " +
      "varif(f1){  port sync in int i1, i2; " +
      "  port out int o; " +
      "varif(f2){  compute { o = i1 + i2; } }}" +
      "}",
    // 6 - Read value from and write to field in compute block
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp6 {" +
      "feature f1,f2; " +
      "varif(f1){}else{  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); }" +
      "varif(f2){}else{  compute { " +
      "    o = v.i; o = v.o; " +
      "    v.i = 0; v.i +=1; v.i++; --v.i; " +
      "    v.o = 0; v.o +=1; v.o++; --v.o; " +
      "  }}" +
      "constraint(!f1); " +
      "}",
    // 7 - Read value from method call in compute block
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp7 {" +
      "feature f1,f2; " +
      " varif(f1){ port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "varif(f2){}else{  compute { " +
      "    o = v.i(); o = v.o(); " +
      "  }}} " +
      "}",
    // 8 - Read value from and write to static field in compute block
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp8 {" +
      "feature f1,f2,f3; " +
      " varif(f1){ port in int i; " +
      " varif(f2){ port out int o; " +
      "varif(f3){  compute { " +
      "    o = OOTypeWithStaticFieldIO.i; " +
      "    o = OOTypeWithStaticFieldIO.o; " +
      "  } }}}" +
      "}",
    // 9 - Read value from static method call in compute block
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 {" +
      "feature f1,f2; " +
      "varif(f1){varif(f2){  port in int i; " +
      "  port out int o; " +
      "  compute { " +
      "    o = OOTypeWithStaticFunctionIO.i(); " +
      "    o = OOTypeWithStaticFunctionIO.o(); " +
      "  }}} " +
      "}",
    // 10 - Variable declaration shadows port in compute block
    "component Comp10 {" +
      "feature f1,f2,f3; " +
      " varif(f1){ port in int i; " +
      "  port out int o; " +
      " varif(f2){varif(f3){ compute { " +
      "    int i = 0; " +
      "    o = i; " +
      "  } }}}" +
      "}",
    // 11 - For control shadows port in compute block
    "component Comp11 {" +
      "feature f1,f2,f3; " +
      "varif(f1){}else{  port in int i; " +
      " varif(f2){}else{ port out int o; " +
      " varif(f3){}else{ compute { " +
      "    for (int i = 0; i < 10; i++) { " +
      "      o = i; " +
      "    } " +
      "  }}}} " +
      "}",
  })
  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new NoNonSyncInputPortInCompute4Family());

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
    checker.addCoCo(new NoNonSyncInputPortInCompute4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No non-synchronous input port in var declaration statement in compute block
      arg("component Comp1 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 2 - No non-synchronous input port in assignment expressions in compute block
      arg("component Comp2 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 3 - No non-synchronous input port in inc prefix expressions in compute block
      arg("component Comp3 { " +
          "  port in int i; " +
          "  compute { " +
          "    ++i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 4 - No non-synchronous input port in dec prefix expressions in compute block
      arg("component Comp4 { " +
          "  port in int i; " +
          "  compute { " +
          "    --i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 5 - No non-synchronous input port in inc suffix expressions in compute block
      arg("component Comp5 { " +
          "  port in int i; " +
          "  compute { " +
          "    i++; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 6 - No non-synchronous input port in dec suffix expressions in compute block
      arg("component Comp6 { " +
          "  port in int i; " +
          "  compute { " +
          "    i--; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 7 - No non-synchronous input port in boolean not expressions in compute block
      arg("component Comp7 { " +
          "  port in boolean i; " +
          "  compute { " +
          "    boolean x = true; " +
          "    x = ~i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 8 - No non-synchronous input port in logical not expressions in compute block
      arg("component Comp8 { " +
          "  port in boolean i; " +
          "  compute { " +
          "    boolean x = true; " +
          "    x = !i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 9 - No non-synchronous input port in multiply expressions (right) in compute block
      arg("component Comp9 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = 2 * i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 10 - No non-synchronous input port in multiply expressions (left) in compute block
      arg("component Comp10 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i * 2; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 11 - No non-synchronous input port in multiply expressions (both) in compute block
      arg("component Comp11 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i * i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 12 - No non-synchronous input port in multiply expressions (both) in compute block
      arg("component Comp12 { " +
          "  port in int i1; " +
          "  port sync in int i2; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i1 * i2; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 13 - No non-synchronous input port in infix expressions in compute block
      arg("component Comp13 { " +
          "  port in int i1; " +
          "  port in boolean i2; " +
          "  compute { " +
          "    int x = 0; " +
          "    boolean y = true; " +
          "    x = 2 / i1;  x = i1 / 2; " +
          "    x = 2 % i1;  x = i1 % 2; " +
          "    x = 2 + i1;  x = i1 + 2; " +
          "    x = 2 - i1;  x = i1 - 2; " +
          "    y = i1 <= 2; y = 2 <= i1; " +
          "    y = i1 >= 2; y = 2 >= i1; " +
          "    y = i1 < 2;  y = 2 < i1; " +
          "    y = i1 > 2;  y = 2 > i1; " +
          "    y = i1 == 2; y = 2 == i1; " +
          "    y = i1 != 2; y = 2 != i1; " +
          "    y = i2 && 2; y = 2 && i2; " +
          "    y = i2 || 2; y = 2 || i2; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 14 - No non-synchronous input port in conditional expressions (condition) in compute block
      arg("component Comp14 { " +
          "  port in boolean i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i ? -2 : 2; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 15 - No non-synchronous input port in conditional expressions (then) in compute block
      arg("component Comp15 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = true ? i : 2; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 16 - No non-synchronous input port in conditional expressions (else) in compute block
      arg("component Comp16 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = true ? -2 : i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 17 - No non-synchronous input port in bracket expressions in compute block
      arg("component Comp17 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = (i); " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 18 - No non-synchronous input port in shift expressions in compute block
      arg("component Comp18 { " +
          "  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i << 1;  x = 1 << i; " +
          "    x = i >> 1;  x = 1 >> i; " +
          "    x = i >>> 1; x = 1 >>> i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 19 - No non-synchronous input port in binary expressions in compute block
      arg("component Comp19 { " +
          "  port in boolean i; " +
          "  compute { " +
          "    boolean x = true; " +
          "    x = i & true; x = true & i; " +
          "    x = i ^ true; x = true ^ i; " +
          "    x = i | true; x = true | i; " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 20 - No non-synchronous input port in if statement (condition) in compute block
      arg("component Comp20 { " +
          "  port in boolean i; " +
          "  compute { " +
          "    if (i) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 21 - No non-synchronous input port in if statement (then) in compute block
      arg("component Comp21 { " +
          "  port in int i; " +
          "  compute { " +
          "    if (true) { int x = i; } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 22 - No non-synchronous input port in if statement (else) in compute block
      arg("component Comp22 { " +
          "  port in int i; " +
          "  compute { " +
          "    if (true) { } else { int x = i; } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 23 - No non-synchronous input port in common for control (var dec) in compute block
      arg("component Comp23 { " +
          "  port in int i; " +
          "  compute { " +
          "    for (int j = i; j > 10; j++) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 24 - No non-synchronous input port in common for control (var assignment) in compute block
      arg("component Comp24 { " +
          "  port in int i; " +
          "  compute { " +
          "    int j = 0; " +
          "    for (j = i; j > 10; j++) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 25 - No non-synchronous input port in common for control (condition) in compute block
      arg("component Comp25 { " +
          "  port in int i; " +
          "  compute { " +
          "    for (int j = 1; i > 10; j++) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 26 - No non-synchronous input port in common for control (expression) in compute block
      arg("component Comp26 { " +
          "  port in int i; " +
          "  compute { " +
          "    for (int j = 0; j > 10; i++) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 27 - No non-synchronous input port in common for control (expression) in compute block
      arg("component Comp27 { " +
          "  port in int i; " +
          "  compute { " +
          "    for (int j = 0; j > 10; j++, i++) { } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
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
    checker.get4FullVariant().addCoCo(new NoNonSyncInputPortInCompute4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // 1 - No non-synchronous input port in var declaration statement in compute block
      arg("component Comp1 {" +
          "feature f1; " +
          "varif(f1){  port in int i; " +
          "  compute { " +
          "    int x = i; " +
          "  }} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 2 - No non-synchronous input port in assignment expressions in compute block
      arg("component Comp2 {" +
          "feature f1; " +
          "varif(f1){  port in int i; " +
          "  compute { " +
          "    int x = 0; " +
          "    x = i; " +
          "  }}" +
          "constraint(f1); " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 3 - No non-synchronous input port in inc prefix expressions in compute block
      arg("component Comp3 {" +
          "feature f1; " +
          "varif(f1){}else{  port in int i; " +
          "  compute { " +
          "    ++i; " +
          "  }} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 4 - No non-synchronous input port in dec prefix expressions in compute block
      arg("component Comp4 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i; }" +
          "varif(f2){  compute { " +
          "    --i; " +
          "  } }" +
          "constraint(f1);" +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 5 - No non-synchronous input port in inc suffix expressions in compute block
      arg("component Comp5 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i; " +
          "varif(f2){  compute { " +
          "    i++; " +
          "  }}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 6 - No non-synchronous input port in dec suffix expressions in compute block
      arg("component Comp6 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i; " +
          "varif(f2){}else{  compute { " +
          "    i--; " +
          "  } }}" +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 7 - No non-synchronous input port in boolean not expressions in compute block
      arg("component Comp7 { " +
          "feature f1,f2;" +
          "varif(f1){}else{  port in boolean i; " +
          "varif(f2){}else{  compute { " +
          "    boolean x = true; " +
          "    x = ~i; " +
          "  } }}" +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 8 - No non-synchronous input port in logical not expressions in compute block
      arg("component Comp8 {" +
          "feature f1,f2,f3; " +
          "varif(f1){  port in boolean i; " +
          "varif(f2){varif(f3){  compute { " +
          "    boolean x = true; " +
          "    x = !i; " +
          "  }}}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 9 - No non-synchronous input port in multiply expressions (right) in compute block
      arg("component Comp9 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i; " +
          "varif(f2){}else{  compute { " +
          "    int x = 0; " +
          "    x = 2 * i; " +
          "  }}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 10 - No non-synchronous input port in multiply expressions (left) in compute block
      arg("component Comp10 {" +
          "feature f1,f2,f3; " +
          "varif(f1){varif(f2){  port in int i; " +
          "varif(f3){  compute { " +
          "    int x = 0; " +
          "    x = i * 2; " +
          "  }}}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ));
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._cocos.NoOtherInputPortInMsgTransition;
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

public class NoOtherInputPortInMsgTransition4FamilyTest extends MontiArcTestBase {

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
    Arguments simpleModel = arg(            "component Comp5 { " +
      "  port sync in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { o = i; } " +
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
    checker.get4FullVariant().addCoCo(new NoOtherInputPortInMsgTransition());

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
    // 1 - Write literal to output port in time-event triggered transition
    "component Comp1 { " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  } " +
      "}",
    // 2 - Write literal to output port in message-event triggered transition
    "component Comp2 { " +
      "  port out int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { o = 0; } " +
      "  } " +
      "}",
    // 3 - Write value of component variable to output port in time-event triggered transition
    "component Comp3 { " +
      "  port out int o; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { o = v; } " +
      "  } " +
      "}",
    // 4 - Write value of component variable to output port in message-event triggered transition
    "component Comp4 { " +
      "  port out int i; " +
      "  port out int o; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { o = v; } " +
      "  } " +
      "}",
    // 5 - Write value of message to output port in time-event triggered transition
    "component Comp5 { " +
      "  port sync in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { o = i; } " +
      "  } " +
      "}",
    // 6 - Write value of message to output port in message-event triggered transition
    "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { o = i; } " +
      "  } " +
      "}",
    // 7 - Write value of message to output port in message-event triggered transition
    "component Comp7 { " +
      "  port in int i1, i2; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i1 / { o = i1; } " +
      "    S -> S i2 / { o = i2; } " +
      "  } " +
      "}",
    // 8 - Read value from and write to field in time-event triggered transition
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } " +
      "}",
    // 9 - Read value from and write to field in message-event triggered transition
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp9 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } " +
      "}",
    // 10 - Read value from method call in time-event triggered transition
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  } " +
      "}",
    // 11 - Read value from method call in message-event triggered transition
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp10 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  } " +
      "}",
    // 12 - Read value from and write to static field in time-event triggered transition
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp12 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    } " +
      "  } " +
      "}",
    // 13 - Read value from and write to static field in message-event triggered transition
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp13 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    } " +
      "  } " +
      "}",
    // 14 - Read value from static method call in time-event triggered transition
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp14 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = OOTypeWithStaticFunctionIO.i(); " +
      "      o = OOTypeWithStaticFunctionIO.o(); " +
      "    } " +
      "  } " +
      "}",
    // 15 - Read value from static method call in message-event triggered transition
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp15 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      o = OOTypeWithStaticFunctionIO.i(); " +
      "      o = OOTypeWithStaticFunctionIO.o(); " +
      "    } " +
      "  } " +
      "}",
    // 16 - Variable declaration shadows port in time-event triggered transition
    "component Comp16 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      int i = 0; " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
    // 17 - Variable declaration shadows port in message-event triggered transition
    "component Comp17 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      int i = 0; " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
    // 18 - For control shadows port in time-event triggered transition
    "component Comp18 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      for (int i = 0; i < 10; i++) { " +
      "        o = i; " +
      "      } " +
      "    } " +
      "  } " +
      "}",
    // 19 - For control shadows port in message-event triggered transition
    "component Comp19 { " +
      "  port in int i, j; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      for (int i = 0; i < 10; i++) { " +
      "        o = i; " +
      "      } " +
      "    } " +
      "  } " +
      "}"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new NoOtherInputPortInMsgTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - Write literal to output port in time-event triggered transition
    "component Comp1 {" +
      "feature f1; " +
      "  port out int o; " +
      "varif(f1){  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  }} " +
      "}",
    // 2 - Write literal to output port in message-event triggered transition
    "component Comp2 {" +
      "feature f1; " +
      "  port out int i; " +
      "  port out int o; " +
      "varif(f1){  automaton { " +
      "    state S;" +
      "    S -> S i / { o = 0; } " +
      "  }}" +
      "constraint(f1); " +
      "}",
    // 3 - Write value of component variable to output port in time-event triggered transition
    "component Comp3 {" +
      "feature f1,f2; " +
      "varif(f1){  port out int o; " +
      "  int v = 0; }" +
      "varif(f2){  automaton { " +
      "    state S;" +
      "    S -> S / { o = v; } " +
      "  } }" +
      "constraint(f1);" +
      "}",
    // 4 - Write value of component variable to output port in message-event triggered transition
    "component Comp4 {" +
      "feature f1,f2; " +
      "varif(f1){  port out int i; " +
      "  port out int o; " +
      "  int v = 0; " +
      "varif(f2){  automaton { " +
      "    state S;" +
      "    S -> S i / { o = v; } " +
      "  }}} " +
      "}",
    // 5 - Write value of message to output port in time-event triggered transition
    "component Comp5 { " +
      "feature f1,f2;" +
      "varif(f1){}else{  port sync in int i; " +
      "  port out int o; }" +
      "varif(f2){  automaton { " +
      "    state S;" +
      "    S -> S / { o = i; } " +
      "  } }" +
      "constraint(!f1);" +
      "}",
    // 6 - Write value of message to output port in message-event triggered transition
    "component Comp6 {" +
      "feature f1,f2,f3; " +
      "varif(f1){  port in int i; }" +
      "varif(f2){  port out int o; }" +
      "varif(f3){  automaton { " +
      "    state S;" +
      "    S -> S i / { o = i; } " +
      "  }}" +
      "constraint(f1 && f2 && f3); " +
      "}",
    // 7 - Write value of message to output port in message-event triggered transition
    "component Comp7 {" +
      "feature f1,f2,f3; " +
      "varif(f1){  port in int i1, i2; " +
      "varif(f2){  port out int o; " +
      "varif(f3){  automaton { " +
      "    state S;" +
      "    S -> S i1 / { o = i1; } " +
      "    S -> S i2 / { o = i2; } " +
      "  }}}} " +
      "}",
    // 8 - Read value from and write to field in time-event triggered transition
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp8 {" +
      "feature f1,f2; " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "varif(f1){varif(f2){}else{  automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } }}" +
      "}",
    // 9 - Read value from and write to field in message-event triggered transition
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp9 {" +
      "feature f1,f2; " +
      "varif(f1){}else{  port in int i, j; " +
      "  port out int o; }" +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      " varif(f2){}else{ automaton { " +
      "    state S;" +
      "    S -> S j / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } }" +
      "constraint(!f1 && !f2);" +
      "}",
    // 10 - Read value from method call in time-event triggered transition
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp10 {" +
      "feature f1; " +
      "  varif(f1){  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "automaton { " +
      "    state S;" +
      "    S -> S / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  } }" +
      "}"})

  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new NoOtherInputPortInMsgTransition4Family());

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
    checker.addCoCo(new NoOtherInputPortInMsgTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - No other input port in var declaration statement in message-event triggered transition
      arg("component Comp1 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 2 - No other input port in assignment expressions in message-event triggered transition
      arg("component Comp2 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 3 - No other input port in inc prefix expressions in message-event triggered transition
      arg("component Comp3 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      ++i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 4 - No other input port in dec prefix expressions in message-event triggered transition
      arg("component Comp4 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      --i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 5 - No other input port in inc suffix expressions in message-event triggered transition
      arg("component Comp5 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      i2++; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 6 - No other input port in dec suffix expressions in message-event triggered transition
      arg("component Comp6 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      i2--; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 7 - No other input port in boolean not expressions in message-event triggered transition
      arg("component Comp7 { " +
          "  port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      boolean x = true; " +
          "      x = ~i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 8 - No other input port in logical not expressions in message-event triggered transition
      arg("component Comp8 { " +
          "  port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      boolean x = true; " +
          "      x = !i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 9 - No other input port in multiply expressions (right) in message-event triggered transition
      arg("component Comp9 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = 2 * i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 10 - No other input port in multiply expressions (left) in message-event triggered transition
      arg("component Comp10 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2 * 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 11 - No other input port in multiply expressions (both) in message-event triggered transition
      arg("component Comp11 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2 * i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 12 - No other input port in multiply expressions (both) in message-event triggered transition
      arg("component Comp12 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i2 / { " +
          "      int x = 0; " +
          "      x = i1 * i1; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 13 - No other input port in infix expressions in message-event triggered transition
      arg("component Comp13 { " +
          "  port in int i1, i2; " +
          "  port in boolean i3, i4; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      boolean y = true; " +
          "      x = 2 / i2;  x = i2 / 2; " +
          "      x = 2 % i2;  x = i2 % 2; " +
          "      x = 2 + i2;  x = i2 + 2; " +
          "      x = 2 - i2;  x = i2 - 2; " +
          "      y = i2 <= 2; y = 2 <= i2; " +
          "      y = i2 >= 2; y = 2 >= i2; " +
          "      y = i2 < 2;  y = 2 < i2; " +
          "      y = i2 > 2;  y = 2 > i2; " +
          "      y = i2 == 2; y = 2 == i2; " +
          "      y = i2 != 2; y = 2 != i2; " +
          "    } " +
          "    S -> S i3 / { " +
          "      y = i4 && 2; y = 2 && i4; " +
          "      y = i4 || 2; y = 2 || i4; " +
          "    } " +
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
      // 14 - No other input port in conditional expressions (condition) in message-event triggered transition
      arg("component Comp14 { " +
          "  port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2 ? -2 : 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 15 - No other input port in conditional expressions (then) in message-event triggered transition
      arg("component Comp15 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = true ? i2 : 2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 16 - No other input port in conditional expressions (else) in message-event triggered transition
      arg("component Comp16 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = true ? -2 : i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 17 - No other input port in bracket expressions in message-event triggered transition
      arg("component Comp17 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = (i2); " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 18 - No other input port in shift expressions in message-event triggered transition
      arg("component Comp18 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2 << 1;  x = 1 << i2; " +
          "      x = i2 >> 1;  x = 1 >> i2; " +
          "      x = i2 >>> 1; x = 1 >>> i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 19 - No other input port in binary expressions in message-event triggered transition
      arg("component Comp19 { " +
          "  port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      boolean x = true; " +
          "      x = i2 & true; x = true & i2; " +
          "      x = i2 ^ true; x = true ^ i2; " +
          "      x = i2 | true; x = true | i2; " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT,
        IN_PORT_REF_IN_INVALID_CONTEXT, IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 20 - No other input port in if statement (condition) in message-event triggered transition
      arg("component Comp20 { " +
          "  port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      if (i2) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 21 - No other input port in if statement (then) in message-event triggered transition
      arg("component Comp21 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      if (true) { int x = i2; } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 22 - No other input port in if statement (else) in message-event triggered transition
      arg("component Comp22 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      if (true) { } else { int x = i2; } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 23 - No other input port in common for control (var dec) in message-event triggered transition
      arg("component Comp23 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      for (int j = i2; j > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 24 - No other input port in common for control (var assignment) in message-event triggered transition
      arg("component Comp24 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int j = 0; " +
          "      for (j = i2; j > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 25 - No other input port in common for control (condition) in message-event triggered transition
      arg("component Comp25 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      for (int j = 1; i2 > 10; j++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 26 - No other input port in common for control (expression) in message-event triggered transition
      arg("component Comp26 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      for (int j = 0; j > 10; i2++) { } " +
          "    } " +
          "  } " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 27 - No other input port in common for control (expression) in message-event triggered transition
      arg("component Comp27 { " +
          "  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      for (int j = 0; j > 10; i1++, i2++) { } " +
          "    } " +
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
    checker.get4FullVariant().addCoCo(new NoOtherInputPortInMsgTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // 1 - No other input port in var declaration statement in message-event triggered transition
      arg("component Comp1 {" +
          "feature f1; " +
          "varif(f1){  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = i2; " +
          "    } " +
          "  }} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 2 - No other input port in assignment expressions in message-event triggered transition
      arg("component Comp2 {" +
          "feature f1; " +
          "varif(f1){  port in int i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2; " +
          "    } " +
          "  }}" +
          "constraint(f1); " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 3 - No other input port in inc prefix expressions in message-event triggered transition
      arg("component Comp3 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i1, i2;} " +
          "varif(f2){  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      ++i2; " +
          "    } " +
          "  } }" +
          "constraint(f1);" +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 4 - No other input port in dec prefix expressions in message-event triggered transition
      arg("component Comp4 {" +
          "feature f1,f2; " +
          "varif(f1){  port in int i1, i2; " +
          "varif(f2){  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      --i2; " +
          "    } " +
          "  }}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 5 - No other input port in inc suffix expressions in message-event triggered transition
      arg("component Comp5 {" +
          "feature f1,f2; " +
          "varif(f1){}else{  port in int i1, i2; }" +
          "varif(f2){}else{  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      i2++; " +
          "    } " +
          "  }}" +
          "constraint(!f1 && !f2); " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 6 - No other input port in dec suffix expressions in message-event triggered transition
      arg("component Comp6 {" +
          "feature f1; " +
          "  port in int i1, i2; " +
          "varif(f1){  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      i2--; " +
          "    } " +
          "  }} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 7 - No other input port in boolean not expressions in message-event triggered transition
      arg("component Comp7 {" +
          "feature f1,f2,f3; " +
          "varif(f1){  port in boolean i1, i2; " +
          "varif(f2){ varif(f3){  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      boolean x = true; " +
          "      x = ~i2; " +
          "    } " +
          "  }}}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 8 - No other input port in logical not expressions in message-event triggered transition
      arg("component Comp8 { " +
          "feature f1;" +
          "varif(f1){}else{ port in boolean i1, i2; " +
          "  automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      boolean x = true; " +
          "      x = !i2; " +
          "    } " +
          "  }} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 9 - No other input port in multiply expressions (right) in message-event triggered transition
      arg("component Comp9 {" +
          "feature f1,f2; " +
          "varif(f1){}else{  port in int i1, i2; " +
          " varif(f2){}else{ automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = 2 * i2; " +
          "    } " +
          "  } }}" +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ),
      // 10 - No other input port in multiply expressions (left) in message-event triggered transition
      arg("component Comp10 {" +
          "feature f1,f2; " +
          "  port in int i1, i2; " +
          " varif(f1){varif(f2){}else{ automaton { " +
          "    state S;" +
          "    S -> S i1 / { " +
          "      int x = 0; " +
          "      x = i2 * 2; " +
          "    } " +
          "  }}} " +
          "}",
        IN_PORT_REF_IN_INVALID_CONTEXT
      ));
  }
}

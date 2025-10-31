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

import static montiarc.util.ArcError.READ_FROM_OUTGOING_PORT;
import static montiarc.util.ArcError.WRITE_TO_INCOMING_PORT;
import static org.assertj.core.api.Assertions.assertThat;

public class PortReadWriteInTransition4FamilyTest extends MontiArcTestBase {

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
    Arguments simpleModel = arg("component Comp3 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  int v = 1; " +
      "}");
    componentList.add(simpleModel);
    return componentList.stream();
  }

  @ParameterizedTest
  @MethodSource("provideUniqueSenderModel")
  public void TestModelRuntimeMontiArcCoCos(@NotNull String model) {

    Preconditions.checkNotNull(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortReadWriteInTransition4Family());

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
    // 1 - Write literal to output port in transition action
    "component Comp1 { " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  } " +
      "}",
    // 2 - Write input to output port in transition action
    "component Comp2 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S i / { o = i; } " +
      "  } " +
      "}",
    // 3 - Write value of variable to output port in transition action
    "component Comp3 { " +
      "  port out int o; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = v; } " +
      "  } " +
      "}",
    // 4 - Write value of input port to variable in transition action
    "component Comp4 { " +
      "  port in int i; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S i / { v = i; } " +
      "  } " +
      "}",
    // 5 - Read value from and write to field in transition action
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp5 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } " +
      "}",
    // 6 - Read value from method call in transition action
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp6 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  } " +
      "}",
    // 7 - Read value from and write to static field in transition action
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp7 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    } " +
      "  } " +
      "}",
    // 8 - Read value from static method call in transition action
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp8 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFunctionIO.i(); " +
      "      o = OOTypeWithStaticFunctionIO.o(); " +
      "    } " +
      "  } " +
      "}",
    // 9 - Pass input as argument to static method call in transition action
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFunctionIO.i(i); " +
      "      o = OOTypeWithStaticFunctionIO.o(i); " +
      "    } " +
      "  } " +
      "}",
    // 10 - Variable declaration shadows port in transition action
    "component Comp10 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      int i = 0; " +
      "      o = i; " +
      "    } " +
      "  } " +
      "}",
    // 11 - For control shadows port in transition action
    "component Comp11 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      for (int i = 0; i < 10; i++) { " +
      "        o = i; " +
      "      } " +
      "    } " +
      "  } " +
      "}",
    // 12 - Write input to output in transition action
    "component Comp12 { " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S i / { o = i; } " +
      "  } " +
      "}",
    // 13 - Read input port in transition guard
    "component Comp13 { " +
      "  port in boolean i; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S [i]; " +
      "  } " +
      "}",
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .isEmpty();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // 1 - Write literal to output port in transition action
    "component Comp1 {" +
      "feature f1; " +
      "varif(f1){  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = 0; } " +
      "  }} " +
      "}",
    // 2 - Write input to output port in transition action
    "component Comp2 {" +
      "feature f1; " +
      "varif(f1){  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S i / { o = i; } " +
      "  }}" +
      "constraint(f1); " +
      "}",
    // 3 - Write value of variable to output port in transition action
    "component Comp3 {" +
      "feature f1; " +
      "varif(f1){}else{  port out int o; " +
      "  int v = 0; " +
      "  automaton { " +
      "    state S; " +
      "    S -> S / { o = v; } " +
      "  }}" +
      "}",
    // 4 - Write value of input port to variable in transition action
    "component Comp4 {" +
      "feature f1,f2; " +
      "varif(f1){  port in int i; " +
      "  int v = 0; }" +
      "varif(f2){  automaton { " +
      "    state S; " +
      "    S -> S i / { v = i; } " +
      "  } }" +
      "constraint(f1);" +
      "}",
    // 5 - Read value from and write to field in transition action
    "import montiarc.test.OOTypeWithFieldIO; " +
      "component Comp5 {" +
      "feature f1,f2; " +
      "varif(f1){  port in int i; " +
      "  port out int o; " +
      "varif(f2){  OOTypeWithFieldIO v = OOTypeWithFieldIO.OOTypeWithFieldIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = v.i; o = v.o; " +
      "      v.i = 0; v.i +=1; v.i++; --v.i; " +
      "      v.o = 0; v.o +=1; v.o++; --v.o; " +
      "    } " +
      "  } }}" +
      "}",
    // 6 - Read value from method call in transition action
    "import montiarc.test.OOTypeWithFunctionIO; " +
      "component Comp6 {" +
      "feature f1,f2; " +
      "varif(f1){  port in int i; " +
      "  port out int o; " +
      "varif(f2){}else{  OOTypeWithFunctionIO v = OOTypeWithFunctionIO.OOTypeWithFunctionIO(); " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = v.i(); o = v.o(); " +
      "    } " +
      "  }}} " +
      "}",
    // 7 - Read value from and write to static field in transition action
    "import montiarc.test.OOTypeWithStaticFieldIO; " +
      "component Comp7 {" +
      "feature f1,f2,f3; " +
      "varif(f1){  port in int i; }" +
      "varif(f2){  port out int o;} " +
      "varif(f3){  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFieldIO.i; " +
      "      o = OOTypeWithStaticFieldIO.o; " +
      "    } " +
      "  }}" +
      "constraint(f1 && f2); " +
      "}",
    // 8 - Read value from static method call in transition action
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp8 {" +
      "feature f1,f2,f3; " +
      "varif(f1){  port in int i; " +
      "varif(f2){  port out int o; " +
      "varif(f3){  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFunctionIO.i(); " +
      "      o = OOTypeWithStaticFunctionIO.o(); " +
      "    } " +
      "  }}}} " +
      "}",
    // 9 - Pass input as argument to static method call in transition action
    "import montiarc.test.OOTypeWithStaticFunctionIO; " +
      "component Comp9 {" +
      "feature f1,f2; " +
      "varif(f1){}else{  port in int i; " +
      "  port out int o; }" +
      "varif(f2){}else{  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      o = OOTypeWithStaticFunctionIO.i(i); " +
      "      o = OOTypeWithStaticFunctionIO.o(i); " +
      "    } " +
      "  }}" +
      "constraint(!f1); " +
      "}",
    // 10 - Variable declaration shadows port in transition action
    "component Comp10 {" +
      "feature f1,f2; " +
      "varif(f1){  port in int i; " +
      "  port out int o; }" +
      "varif(f2){  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      int i = 0; " +
      "      o = i; " +
      "    } " +
      "  }}" +
      "constraint(f1 && f2); " +
      "}",
    // 11 - For control shadows port in transition action
    "component Comp11 {" +
      "feature f1,f2,f3;" +
      "varif(f1){varif(f2){varif(f3){ " +
      "  port in int i; " +
      "  port out int o; " +
      "  automaton { " +
      "    state S;" +
      "    S -> S i / { " +
      "      for (int i = 0; i < 10; i++) { " +
      "        o = i; " +
      "      } " +
      "    } " +
      "  }}}} " +
      "}",
    // 12 - Write input to output in transition action
    "component Comp12 {" +
      "feature f1,f2,f3; " +
      "varif(f1){  port in int i; " +
      "  port out int o; " +
      "varif(f2){varif(f3){  automaton { " +
      "    state S; " +
      "    S -> S i / { o = i; } " +
      "  }}}} " +
      "}",
    // 13 - Read input port in transition guard
    "component Comp13 {" +
      "feature f1,f2; " +
      "  port in boolean i; " +
      "varif(f1){}else{varif(f2){  automaton { " +
      "    state S; " +
      "    S -> S [i]; " +
      "  } }}" +
      "}",
  })

  void shouldNotReportErrorWithVariability(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortReadWriteInTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings())
      .as(() -> "Findings: " + Log.getFindings().toString())
      .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new PortReadWriteInTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // 1 - Do not write to incoming port in transition action
      arg("component Comp { " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i = 0; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT
      ),
      // 2 - Do not write to field of incoming port in transition action
      arg("component Comp2 { " +
          "port in montiarc.test.OOTypeWithField i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i.v = 0; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 3 - Do not access field of outgoing port in transition action
      arg("component Comp3 { " +
          "port out montiarc.test.OOTypeWithField o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o.v = 0; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 4 - Do not access method of outgoing port in transition action
      arg("component Comp4 { " +
          "port out montiarc.test.OOTypeWithFunction o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o.f(); } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 5 - Do not use port as argument of method call in transition action
      arg("component Comp5 { " +
          "port in montiarc.test.OOTypeWithFunction i; " +
          "port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i.f(o); } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 6 - Do not write to incoming port with inc suffix in transition action
      arg("component Comp6 { " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i++; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 7 - Do not read from outgoing port with inc suffix in transition action
      arg("component Comp7 { " +
          "port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o++; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 8 - Do not write to incoming port with dec suffix in transition action
      arg("component Comp8 { " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i--; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 9 - Do not read from outgoing port with dec suffix in transition action
      arg("component Comp9 { " +
          "port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o--; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 10 - Do not write to incoming port with inc prefix in transition action
      arg("component Comp10 { " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { ++i; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 11 - Do not read from outgoing port with inc prefix in transition action
      arg("component Comp11 { " +
          "port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { ++o; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 12 - Do not write to incoming port with dec prefix in transition action
      arg("component Comp12 { " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { --i; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 13 - Do not read from outgoing port with dec prefix in transition action
      arg("component Comp13 { " +
          "port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { --o; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 14 - Do not write to incoming port via assignment expression in transition action
      arg("component Comp14 { " +
          "port in int i1; " +
          "port in boolean i2; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i1 += 1; } " +
          "    S -> S i / { i1 -= 1; } " +
          "    S -> S i / { i1 *= 1; } " +
          "    S -> S i / { i1 /= 1; } " +
          "    S -> S i / { i1 %= 1; } " +
          "    S -> S i / { i1 >>= 1; } " +
          "    S -> S i / { i1 >>>= 1; } " +
          "    S -> S i / { i1 <<= 1; } " +
          "    S -> S i / { i2 &= true; } " +
          "    S -> S i / { i2 |= true; } " +
          "    S -> S i / { i2 ^= true; } " +
          "  }" +
          "}",
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT,
        WRITE_TO_INCOMING_PORT, WRITE_TO_INCOMING_PORT
      ),
      // 15 - Do not read from outgoing port via assignment expression in transition action
      arg("component Comp15 { " +
          "port out int o1; " +
          "port out boolean o2; " +
          "int v1 = 0; " +
          "boolean v2 = true; " +
          "  automaton { " +
          "    state S; " +
          "    state S; " +
          "    S -> S i / { v1 += o1; } " +
          "    S -> S i / { v1 -= o1; } " +
          "    S -> S i / { v1 *= o1; } " +
          "    S -> S i / { v1 /= o1; } " +
          "    S -> S i / { v1 %= o1; } " +
          "    S -> S i / { v1 >>= o1; } " +
          "    S -> S i / { v1 >>>= o1; } " +
          "    S -> S i / { v1 <<= o1; } " +
          "    S -> S i / { v2 &= o2; } " +
          "    S -> S i / { v2 |= o2; } " +
          "    S -> S i / { v2 ^= o2; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT
      ),
      // 16 - Do not read from outgoing port via infix expression in transition action
      arg("component Comp16 { " +
          "port out int o1; " +
          "port out boolean o2; " +
          "  automaton { " +
          "    state S; " +
          "    state S; " +
          "    S -> S i / { o1 + 1; } " +
          "    S -> S i / { o1 - 1; } " +
          "    S -> S i / { o1 * 1; } " +
          "    S -> S i / { o1 / 1; } " +
          "    S -> S i / { o1 % 1; } " +
          "    S -> S i / { o1 <= 1; } " +
          "    S -> S i / { o1 >= 1; } " +
          "    S -> S i / { o1 < 1; } " +
          "    S -> S i / { o1 > 1; } " +
          "    S -> S i / { o1 == 1; } " +
          "    S -> S i / { o1 != 1; } " +
          "    S -> S i / { o2 && true; } " +
          "    S -> S i / { o2 || true; } " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT, READ_FROM_OUTGOING_PORT,
        READ_FROM_OUTGOING_PORT
      ),
      // 17 - Do not read from outgoing port in transition guard
      arg("component Comp17 { " +
          "port out boolean o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S [o]; " +
          "  }" +
          "}",
        READ_FROM_OUTGOING_PORT
      )
    );
  }

  @ParameterizedTest
  @MethodSource("invalidModelsWithVariability")
  void shouldReportErrorWithVariability(@NotNull String model, @NotNull Error... expectedErrors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTMACompilationUnit ast = compile(model);
    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new PortReadWriteInTransition4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // 1 - Do not write to incoming port in transition action
      arg("component Comp {" +
          "feature f1;" +
          "varif(f1){ " +
          "port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i = 0; } " +
          "  }}" +
          "}",
        WRITE_TO_INCOMING_PORT
      ),
      // 2 - Do not write to field of incoming port in transition action
      arg("component Comp2 {" +
          "feature f1;" +
          "varif(f1){ " +
          "port in montiarc.test.OOTypeWithField i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i.v = 0; } " +
          "  }}" +
          "constraint(f1);" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 3 - Do not access field of outgoing port in transition action
      arg("component Comp3 { " +
          "feature f1;" +
          "varif(f1){}else{port out montiarc.test.OOTypeWithField o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o.v = 0; } " +
          "  }}" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 4 - Do not access method of outgoing port in transition action
      arg("component Comp4 {" +
          "feature f1,f2; " +
          "varif(f1){port out montiarc.test.OOTypeWithFunction o; }" +
          "varif(f2){  automaton { " +
          "    state S; " +
          "    S -> S / { o.f(); } " +
          "  }}" +
          "constraint(f1);" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 5 - Do not use port as argument of method call in transition action
      arg("component Comp5 {" +
          "feature f1,f2; " +
          "varif(f1){port in montiarc.test.OOTypeWithFunction i; " +
          "port out int o; " +
          "varif(f2){  automaton { " +
          "    state S; " +
          "    S -> S i / { i.f(o); } " +
          "  }}}" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 6 - Do not write to incoming port with inc suffix in transition action
      arg("component Comp6 { " +
          "feature f1,f2;" +
          "varif(f1){port in int i; " +
          "varif(f2){  automaton { " +
          "    state S; " +
          "    S -> S i / { i++; } " +
          "  }}}" +
          "constraint(f1 && f2);" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 7 - Do not read from outgoing port with inc suffix in transition action
      arg("component Comp7 {" +
          "feature f1,f2; " +
          "varif(f1){}else{port out int o; " +
          "varif(f2){}else{  automaton { " +
          "    state S; " +
          "    S -> S / { o++; } " +
          "  }}}" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 8 - Do not write to incoming port with dec suffix in transition action
      arg("component Comp8 {" +
          "feature f1,f2; " +
          "varif(f1){varif(f2){port in int i; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S i / { i--; } " +
          "  }}}" +
          "}",
        WRITE_TO_INCOMING_PORT),
      // 9 - Do not read from outgoing port with dec suffix in transition action
      arg("component Comp9 {" +
          "feature f1,f2,f3; " +
          "varif(f1){varif(f2){varif(f3){port out int o; " +
          "  automaton { " +
          "    state S; " +
          "    S -> S / { o--; } " +
          "  }}}}" +
          "}",
        READ_FROM_OUTGOING_PORT),
      // 10 - Do not write to incoming port with inc prefix in transition action
      arg("component Comp10 {" +
          "feature f1,f2;" +
          "port in int i; " +
          "varif(f1){varif(f2){}else{  automaton { " +
          "    state S; " +
          "    S -> S i / { ++i; } " +
          "  }}}" +
          "}",
        WRITE_TO_INCOMING_PORT));
  }

  }

/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcAbstractTest;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Integration test for the {@link MontiArcScopesGenitorDelegator} and the
 * {@link MontiArcScopesGenitorP2Delegator} that checks that the symbol
 * table is constructed fully and correctly with respect to basic properties.
 * That is, checks that all scopes, symbols and ast nodes have an enclosing
 * scope, and that all spanning symbols and ast nodes have a spanned scope.
 */
public class SymbolTableTest extends MontiArcAbstractTest {

  @BeforeAll
  public static void init() {
    Log.enableFailQuick(false);
  }

  @ParameterizedTest
  @MethodSource("componentProvider")
  public void checkSymbolTableCompletion(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);
    Preconditions.checkState(Log.getFindings().isEmpty());

    // Given
    MontiArcTool tool = new MontiArcTool();
    tool.initializeClass2MC();

    // When
    tool.createSymbolTable(ast);
    tool.completeSymbolTable(ast);

    // Then
    List<String> findings = SymbolTableChecker.checkComplete(ast);
    Assertions.assertTrue(SymbolTableChecker.checkComplete(ast).isEmpty(),
      "The symbol-table is not complete, findings: " + findings);
  }

  static Stream<Arguments> componentProvider() throws IOException {
    Optional<ASTMACompilationUnit> ast1 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Ports {\n"
          + "port in int i1;\n"
          + "port in int i2, i3;\n"
          + "port in int i4, i5,\n"
          + "in int i6,\n"
          + "out int o1,\n"
          + "out int o2, o3,\n"
          + "out int o3, o4, o5;\n"
          + "port out int o6;\n"
          + "\n"
          + "port in A i7;\n"
          + "port out A o7;\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast2 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Subcomponents {\n"
          + "component Sub { }\n"
          + "Sub sub1;\n"
          + "Sub sub2, sub3;\n"
          + "Sub sub4();\n"
          + "Sub sub5(), sub6();\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast3 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Connectors {\n"
          + "port in int i;\n"
          + "port out int o;\n"
          + "\n"
          + "component Sub {\n"
          + "port in int i;\n"
          + "port out int o;\n"
          + "}\n"
          + "\n"
          + "Sub sub1, sub2;\n"
          + "\n"
          + "i -> sub1.i;\n"
          + "sub1.o -> sub2.i;\n"
          + "sub2.o -> o;\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast4 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Variables {\n"
          + "int a = 1;\n"
          + "int b = 2;\n"
          + "int c = 3;\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast5 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Automaton {\n"
          + "port in int i;\n"
          + "port out int o;\n"
          + "automaton {\n"
          + "initial state s1;\n"
          + "initial { o = 1; } state s2;\n"
          + "state s3;\n"
          + "s1 -> s1 [ true ];\n"
          + "s1 -> s2 i;\n"
          + "s1 -> s3 / { o = 3; };\n"
          + "s2 -> s2 [ true ] i / { o = 2; };\n"
          + "s2 -> s3;\n"
          + "state s4 {\n"
          + "state s5;\n"
          + "state s6;\n"
          + "entry / { o = 3; }\n"
          + "exit / { o = 4; }\n"
          + "s5 -> s6;\n"
          + "};\n"
          + "}\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast6 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Parameters(int p1, int p2, int p3) {\n"
          + "component Sub1(int p4) { }\n"
          + "component Sub2(int p5, int p6) { }\n"
          + "\n"
          + "Sub1 sub1(p1);\n"
          + "Sub2 sub2(p1, p2);\n"
          + "Sub3 sub3(p1, p2), sub4(p2, p3);\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast7 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Expressions {\n"
          + "int a = 1;\n"
          + "int b = ~a;\n"
          + "int c = a + b;\n"
          + "int d = a - b;\n"
          + "int e = a * b;\n"
          + "int f = a / b;\n"
          + "int g = a % b;\n"
          + "int h = a++;\n"
          + "int i = a--;\n"
          + "int j = ++a;\n"
          + "int k = --a;\n"
          + "int l = a = b;\n"
          + "int m = a += b;\n"
          + "int n = a -= b;\n"
          + "int o = a *= b;\n"
          + "int p = a /= b;\n"
          + "int q = a %= b;\n"
          + "int r = a &= b;\n"
          + "int s = a |= b;\n"
          + "int t = a ^= b;\n"
          + "int u = a >>= b;\n"
          + "int v = a >>>= b;\n"
          + "int w = a <<= b;\n"
          + "boolean aa = true;\n"
          + "boolean ab = false;\n"
          + "boolean ac = !a;\n"
          + "boolean ad = a && b;\n"
          + "boolean ae = a || b;\n"
          + "boolean af = a <= b;\n"
          + "boolean ag = a >= b;\n"
          + "boolean ah = a < b;\n"
          + "boolean ai = a > b;\n"
          + "boolean aj = a == b;\n"
          + "boolean ak = a != b;\n"
          + "int al = age();\n"
          + "int am = person.age();\n"
          + "int an = boy.father.age();\n"
          + "int ao = boy.father().age();\n"
          + "int ap = age(boy, father);\n"
          + "String aq = \"Some string\";\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast8 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Statements {\n"
          + "automaton {\n"
          + "state s;\n"
          + "s -> s / {\n"
          + "final int a = 1;\n"
          + "int b = 2;\n"
          + "int c;\n"
          + "c = 3;\n"
          + "int d = 4, e = 5;\n"
          + "boolean f, g = true;\n"
          + "if(f) b = 0; else b = -1;\n"
          + "if(g) {\n"
          + "c = -2;\n"
          + "} else if (f) {\n"
          + "c = -3;\n"
          + "}\n"
          + "for (int i = 0; i < 5; i++) {\n"
          + "d = d + i;\n"
          + "}\n"
          + "for (; d > 5; d--) { }\n"
          + "for (; ; d--) { break; }\n"
          + "MyList<Integer> listOfInt;\n"
          + "for (Integer i : listOfInt) {\n"
          + "e = e + i;\n"
          + "}\n"
          + "while(f) {\n"
          + "f = false;\n"
          + "}\n"
          + "while(g) g = false;\n"
          + "do {\n"
          + "f = true;\n"
          + "} while (!f);\n"
          + "do g = true; while(!g);\n"
          + "switch(a) {\n"
          + "case 1 : b = 1;\n"
          + "case 2 : b = 2;\n"
          + "default : b = 0;\n"
          + "}\n"
          + "};\n"
          + "}\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast9 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Generics<X, Y, Z> {\n"
          + "port in X i;\n"
          + "port out Y o;\n"
          + " \n"
          + "component Sub1<U> { }\n"
          + "component Sub2<V, W> { }\n"
          + "component Sub3<P, Q, R> { }\n"
          + " \n"
          + "Sub1<X> sub1;\n"
          + "Sub2<X, Y> sub2;\n"
          + "Sub3<X, Y, Z> sub3;\n"
          + "Sub2<X, MyMap<Y, Z>> sub4;\n"
          + "Sub2<MyMap<X, Y>, Z> sub5;\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast10 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component Variability(int a, int b, int c) {\n"
          + "feature f1;\n"
          + "feature f2, f3, f4;\n"
          + " \n"
          + "if (f1) port in int i1;\n"
          + "if (!f1) port in int i2, i3, i4;\n"
          + "if (f1 || !f2 || (f3 && f4)) {\n"
          + "port in int i2;\n"
          + "port in int i3, i4,\n"
          + "out int o1,\n"
          + "out int o2, o3;\n"
          + " \n"
          + "component Sub {\n"
          + "port in int i;\n"
          + "port out int o;\n"
          + "}\n"
          + " \n"
          + "Sub q, r;\n"
          + " \n"
          + "i1 -> q.i;\n"
          + "q.o -> r.i;\n"
          + "r.o -> o1;\n"
          + "}\n"
          + " \n"
          + "constraint (f1 || f2);\n"
          + "constraint (!((f3 && !f4) || (!f3 && f4)));\n"
          + "}"
      );
    Optional<ASTMACompilationUnit> ast11 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        "component ModeAutomaton {\n"
          + "port in int i;\n"
          + "port out int o;\n"
          + "mode automaton {\n"
          + "initial mode m1 { }\n"
          + "mode m2 { }\n"
          + "mode m3 { }\n"
          + "m1 -> m2;\n"
          + "m2 -> m3;\n"
          + "m3 -> m1;\n"
          + "}\n"
          + "}"
      );
    
    Assertions.assertTrue(ast1.isPresent());
    Assertions.assertTrue(ast2.isPresent());
    Assertions.assertTrue(ast3.isPresent());
    Assertions.assertTrue(ast4.isPresent());
    Assertions.assertTrue(ast5.isPresent());
    Assertions.assertTrue(ast6.isPresent());
    Assertions.assertTrue(ast7.isPresent());
    Assertions.assertTrue(ast8.isPresent());
    Assertions.assertTrue(ast9.isPresent());
    Assertions.assertTrue(ast10.isPresent());
    Assertions.assertTrue(ast11.isPresent());

    return Stream.of(
      Arguments.of(Named.of(ast1.get().getComponentType().getName(), ast1.get())),
      Arguments.of(Named.of(ast2.get().getComponentType().getName(), ast2.get())),
      Arguments.of(Named.of(ast3.get().getComponentType().getName(), ast3.get())),
      Arguments.of(Named.of(ast4.get().getComponentType().getName(), ast4.get())),
      Arguments.of(Named.of(ast5.get().getComponentType().getName(), ast5.get())),
      Arguments.of(Named.of(ast6.get().getComponentType().getName(), ast6.get())),
      Arguments.of(Named.of(ast7.get().getComponentType().getName(), ast7.get())),
      Arguments.of(Named.of(ast8.get().getComponentType().getName(), ast8.get())),
      Arguments.of(Named.of(ast9.get().getComponentType().getName(), ast9.get())),
      Arguments.of(Named.of(ast10.get().getComponentType().getName(), ast10.get()))
    );
  }
}

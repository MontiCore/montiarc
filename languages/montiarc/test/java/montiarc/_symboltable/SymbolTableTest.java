/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.AbstractTest;
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
 * {@link MontiArcSymbolTableCompleterDelegator} that checks that the symbol
 * table is constructed fully and correctly with respect to basic properties.
 * That is, checks that all scopes, symbols and ast nodes have an enclosing
 * scope, and that all spanning symbols and ast nodes have a spanned scope.
 */
public class SymbolTableTest extends AbstractTest {

  @BeforeAll
  public static void setUp() {
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
        """ 
          component Ports {
            port in int i1;
            port in int i2, i3;
            port in int i4, i5,
                 in int i6,
                 out int o1,
                 out int o2, o3,
                 out int o3, o4, o5;
            port out int o6;
            
            port in A i7;
            port out A o7;
          }
          """
      );
    Optional<ASTMACompilationUnit> ast2 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Subcomponents {
            component Sub { }
            Sub sub1;
            Sub sub2, sub3;
            Sub sub4();
            Sub sub5(), sub6();
          }
          """
      );
    Optional<ASTMACompilationUnit> ast3 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Connectors {
            port in int i;
            port out int o;
            
            component Sub {
              port in int i;
              port out int o;
            }
            
            Sub sub1, sub2;
            
            i -> sub1.i;
            sub1.o -> sub2.i;
            sub2.o -> o;
          }
          """
      );
    Optional<ASTMACompilationUnit> ast4 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Variables {
            int a = 1;
            int b = 2;
            int c = 3;
          }
          """
      );
    Optional<ASTMACompilationUnit> ast5 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Automaton {
            port in int i;
            port out int o;
            automaton {
              initial state s1;
              initial { o = 1; } state s2;
              state s3;
              s1 -> s1 [ true ];
              s1 -> s2 i();
              s1 -> s3 / { o = 3; };
              s2 -> s2 [ true ] i() / { o = 2; };
              s2 -> s3;
              state s4 {
                state s5;
                state s6;
                entry / { o = 3; }
                exit / { o = 4; }
                s5 -> s6;
              };
            }
          }
          """
      );
    Optional<ASTMACompilationUnit> ast6 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Parameters(int p1, int p2, int p3) {
            component Sub1(int p4) { }
            component Sub2(int p5, int p6) { }
            
            Sub1 sub1(p1);
            Sub2 sub2(p1, p2);
            Sub3 sub3(p1, p2), sub4(p2, p3);
          }
          """
      );
    Optional<ASTMACompilationUnit> ast7 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Expressions {
            int a = 1;
            int b = ~a;
            int c = a + b;
            int d = a - b;
            int e = a * b;
            int f = a / b;
            int g = a % b;
            int h = a++;
            int i = a--;
            int j = ++a;
            int k = --a;
            int l = a = b;
            int m = a += b;
            int n = a -= b;
            int o = a *= b;
            int p = a /= b;
            int q = a %= b;
            int r = a &= b;
            int s = a |= b;
            int t = a ^= b;
            int u = a >>= b;
            int v = a >>>= b;
            int w = a <<= b;
            boolean aa = true;
            boolean ab = false;
            boolean ac = !a;
            boolean ad = a && b;
            boolean ae = a || b;
            boolean af = a <= b;
            boolean ag = a >= b;
            boolean ah = a < b;
            boolean ai = a > b;
            boolean aj = a == b;
            boolean ak = a != b;
            int al = age();
            int am = person.age();
            int an = boy.father.age();
            int ao = boy.father().age();
            int ap = age(boy, father);
            String aq = "Some string";
          }
          """
      );
    Optional<ASTMACompilationUnit> ast8 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Statements {
            automaton {
              state s;
              s -> s / {
                final int a = 1;
                int b = 2;
                int c;
                c = 3;
                int d = 4, e = 5;
                boolean f, g = true;
                if(f) b = 0; else b = -1;
                if(g) {
                  c = -2;
                } else if (f) {
                  c = -3;
                }
                for (int i = 0; i < 5; i++) {
                  d = d + i;
                }
                for (; d > 5; d--) { }
                for (; ; d--) { break; }
                MyList<Integer> listOfInt;
                for (Integer i : listOfInt) {
                  e = e + i;
                }
                while(f) {
                  f = false;
                }
                while(g) g = false;
                do {
                  f = true;
                } while (!f);
                do g = true; while(!g);
                switch(a) {
                  case 1 : b = 1;
                  case 2 : b = 2;
                  default : b = 0;
                }
              };
            }
          }
          """
      );
    Optional<ASTMACompilationUnit> ast9 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Generics<X, Y, Z> {
            port in X i;
            port out Y o;
            
            component Sub1<U> { }
            component Sub2<V, W> { }
            component Sub3<P, Q, R> { }
            
            Sub1<X> sub1;
            Sub2<X, Y> sub2;
            Sub3<X, Y, Z> sub3;
            Sub2<X, MyMap<Y, Z>> sub4;
            Sub2<MyMap<X, Y>, Z> sub5;
          }
          """
      );
    Optional<ASTMACompilationUnit> ast10 = MontiArcMill.parser()
      .parse_StringMACompilationUnit(
        """
          component Variability(int a, int b, int c) {
            feature f1;
            feature f2, f3, f4;
            
            if (f1) port in int i1;
            if (!f1) port in int i2, i3, i4;
            if (f1 || !f2 || (f3 && f4)) {
              port in int i2;
              port in int i3, i4,
                   out int o1,
                   out int o2, o3;
              
              component Sub {
                port in int i;
                port out int o;
              }
              
              Sub q, r;
              
              i1 -> q.i;
              q.o -> r.i;
              r.o -> o1;
            }
            
            constraint (f1 || f2);
            constraint (!((f3 && !f4) || (!f3 && f4)));
          }
          """
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

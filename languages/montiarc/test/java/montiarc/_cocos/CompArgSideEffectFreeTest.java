/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.CompArgSideEffectFree;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.ArcError.NON_PURE_EXPRESSION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link CompArgSideEffectFree}.
 */
class CompArgSideEffectFreeTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A { }");
    compile("package a.b; component B(int p) { }");
    compile("package a.b; component C(int p1, int p2) { }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new CompArgSideEffectFree());

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
    checker.addCoCo(new CompArgSideEffectFree());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      arg("""
        component ValidComp1 {
          a.b.A a();
        }
        """
      ),
      arg("""
        component ValidComp2 {
          a.b.B b(1);
        }
        """
      ),
      arg("""
        component ValidComp3 {
          a.b.B b(p = 1);
        }
        """
      ),
      arg("""
        component ValidComp4 {
          a.b.C c(1, 2);
        }
        """
      ),
      arg("""
        component ValidComp5 {
          a.b.C c(1, p2 = 2);
        }
        """
      ),
      arg("""
        component ValidComp6 {
          a.b.C c(p1 = 1, p2 = 2);
        }
        """
      ),
      arg("""
        component ValidComp7 {
          a.b.B b(m());
        }
        """
      ),
      arg("""
        component ValidComp8 {
          a.b.C c(m(), m());
        }
        """
      ),
      arg("""
        component ValidComp9 {
          a.b.B b(p = m());
        }
        """
      ),
      arg("""
        component ValidComp10 {
          a.b.C c(p1 = m(), p2 = m());
        }
        """
      ),
      arg("""
        component ValidComp11 extends a.b.B(1) { }
        """
      ),
      arg("""
        component ValidComp12 extends a.b.C(p1 = m(), p2 = 2) { }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg("""
          component InvalidComp1(int p) {
            a.b.B b(p = p = 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp2(int p) {
            a.b.B b(p = p += 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp3(int p) {
            a.b.B b(p = p -= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp4(int p) {
            a.b.B b(p = p *= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp5(int p) {
            a.b.B b(p = p /= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp6(int p) {
            a.b.B b(p = p /= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp7(int p) {
            a.b.B b(p = p &= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp8(int p) {
            a.b.B b(p = p |= 1);
          }
          """,
        NON_PURE_EXPRESSION),
      arg("""
          component InvalidComp9(int p) {
            a.b.B b(p = p ^= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp10(int p) {
            a.b.B b(p = p >>= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp11(int p) {
            a.b.B b(p = p <<= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp12(int p) {
            a.b.B b(p = p %= 1);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp13(int p) {
            a.b.C c(p1 = 1, p2 = p = 2);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp14(int p) {
            a.b.C c(p1 = p = 1, p2 = 2);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp15(int p1, int p2) {
            a.b.C c(p1 = p1 = 1, p2 = p2 = 2);
          }
          """,
        NON_PURE_EXPRESSION,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp16(int p) {
            a.b.B b(++p);
          }""",
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp17(int p) {
            a.b.B b(--p); }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp18(int p) {
            a.b.B b(p++);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp19(int p) {
            a.b.B b(p--);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp20(int p) {
            a.b.B b(p = ++p);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp21(int p) {
            a.b.B b(p = --p);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp22(int p) {
            a.b.B b(p = p++);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp23(int p) {
            a.b.B b(p = p--);
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp24(int p) extends a.b.B(p++) { }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp25(int p) extends a.b.C(p1 = p = 1, p2 = --p) { }
          """,
        NON_PURE_EXPRESSION,
        NON_PURE_EXPRESSION
      )
    );
  }
}

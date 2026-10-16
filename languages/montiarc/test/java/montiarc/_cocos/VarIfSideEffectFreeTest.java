/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.VarIfSideEffectFree;

import java.util.stream.Stream;

import static montiarc.util.ArcError.NON_PURE_EXPRESSION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarIfSideEffectFree}.
 */
class VarIfSideEffectFreeTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarIfSideEffectFree());

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
    checker.addCoCo(new VarIfSideEffectFree());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      arg("""
        component ValidComp1 { }
        """
      ),
      arg("""
        component ValidComp2 {
          feature f;
          varif (f) { }
        }
        """
      ),
      arg("""
        component ValidComp3 {
          feature f;
          varif (!f) { }
        }
        """
      ),
      arg("""
        component ValidComp4 {
          feature f;
          varif (f && !f) { }
        }
        """
      ),
      arg("""
        component ValidComp5 {
          feature f;
          varif (f || !f) { }
        }
        """
      ),
      arg("""
        component ValidComp6(int p) {
          varif (p > 0) { }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    return Stream.of(arg("""
          component InvalidComp1 {
            feature f;
            varif (f = true) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp2 {
            feature f;
            varif (f &= true) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp3 {
            feature f;
            varif (f |= true) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp4 {
            feature f;
            varif (f ^= true) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp5(int p) {
            varif ((p += 0) > 1) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp6(int p) {
            varif ((p -= 0) > 1) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp7(int p) {
            varif ((p *= 0) > 1) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp8(int p) {
            varif ((p /= 0) > 1) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp9(int p) {
            varif ((p %= 0) > 1) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp10(int p) {
            varif ((p >>= 1) > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp11(int p) {
            varif ((p >>>= 1) > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp12(int p) {
            varif ((p <<= 1) > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp13(int p) {
            varif (++p > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp14(int p) {
            varif (--p > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp15(int p) {
            varif (p++ > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      ),
      arg("""
          component InvalidComp16(int p) {
            varif (p-- > 0) { }
          }
          """,
        NON_PURE_EXPRESSION
      )
    );
  }
}

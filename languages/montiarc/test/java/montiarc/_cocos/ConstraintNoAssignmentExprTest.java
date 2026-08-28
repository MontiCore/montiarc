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
import org.junit.jupiter.params.provider.ValueSource;
import variablearc._cocos.ConstraintNoAssignmentExpr;

import java.util.stream.Stream;

import static montiarc.util.ArcError.INVALID_CONTEXT_ASSIGNMENT;
import static montiarc.util.ArcError.INVALID_CONTEXT_DEC_PREFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_DEC_SUFFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_INC_PREFIX;
import static montiarc.util.ArcError.INVALID_CONTEXT_INC_SUFFIX;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ConstraintNoAssignmentExpr}.
 */
class ConstraintNoAssignmentExprTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // component without a constraint
    "component ValidComp1 { }",
    // constraint referencing a feature
    "component ValidComp2 { feature f; constraint(f); }",
    // constraint negating a feature
    "component ValidComp3 { feature f; constraint(!f); }",
    // constraint conjunction of a feature and its negation
    "component ValidComp4 { feature f; constraint(f && !f); }",
    // constraint disjunction of a feature and its negation
    "component ValidComp5 { feature f; constraint(f || !f); }",
    // constraint comparing an int parameter
    "component ValidComp6(int p) { constraint(p > 0); }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new ConstraintNoAssignmentExpr());

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
    checker.addCoCo(new ConstraintNoAssignmentExpr());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // nested assignment (=) in constraint on a feature
      arg("component InvalidComp1 { feature f; constraint(f = true); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested and-assignment (&=) in constraint on a feature
      arg("component InvalidComp2 { feature f; constraint(f &= true); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested or-assignment (|=) in constraint on a feature
      arg("component InvalidComp3 { feature f; constraint(f |= true); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested xor-assignment (^=) in constraint on a feature
      arg("component InvalidComp4 { feature f; constraint(f ^= true); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested addition-assignment (+=) in constraint on an int parameter
      arg("component InvalidComp5(int p) { constraint((p += 0) > 1); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested subtraction-assignment (-=) in constraint on an int parameter
      arg("component InvalidComp6(int p) { constraint((p -= 0) > 1); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested multiplication-assignment (*=) in constraint on an int parameter
      arg("component InvalidComp7(int p) { constraint((p *= 0) > 1); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested division-assignment (/=) in constraint on an int parameter
      arg("component InvalidComp8(int p) { constraint((p /= 0) > 1); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested modulo-assignment (%=) in constraint on an int parameter
      arg("component InvalidComp9(int p) { constraint((p %= 0) > 1); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested right-shift-assignment (>>=) in constraint on an int parameter
      arg("component InvalidComp10(int p) { constraint((p >>= 1) > 0); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested unsigned-right-shift-assignment (>>>=) in constraint on an int parameter
      arg("component InvalidComp11(int p) { constraint((p >>>= 1) > 0); }", INVALID_CONTEXT_ASSIGNMENT),
      // nested left-shift-assignment (<<=) in constraint on an int parameter
      arg("component InvalidComp12(int p) { constraint((p <<= 1) > 0); }", INVALID_CONTEXT_ASSIGNMENT),
      // prefix increment expression in constraint
      arg("component InvalidComp13(int p) { constraint(++p > 0); }", INVALID_CONTEXT_INC_PREFIX),
      // prefix decrement expression in constraint
      arg("component InvalidComp14(int p) { constraint(--p > 0); }", INVALID_CONTEXT_DEC_PREFIX),
      // suffix increment expression in constraint
      arg("component InvalidComp15(int p) { constraint(p++ > 0); }", INVALID_CONTEXT_INC_SUFFIX),
      // suffix decrement expression in constraint
      arg("component InvalidComp16(int p) { constraint(p-- > 0); }", INVALID_CONTEXT_DEC_SUFFIX)
    );
  }
}

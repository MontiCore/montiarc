/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;
import variablearc._cocos.FeedbackStrongCausality4Family;

import java.util.stream.Stream;

import static montiarc.util.ArcError.FEEDBACK_CAUSALITY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link FeedbackStrongCausality4Family}.
 */
class FeedbackStrongCausality4FamilyTest extends FeedbackStrongCausalityTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability1",
    "InvalidCompWithVariability2",
    "InvalidCompWithVariability3",
    "InvalidCompWithVariability4",
    "InvalidCompWithVariability5"
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new FeedbackStrongCausality4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // direct weak-causal feedback loop, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          a.b.E sub;
          varif (false) {
            sub.o -> sub.i;
          }
        }
        """
      ),
      // indirect weak-causal feedback loop, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          a.b.E sub1, sub2;
          varif (false) {
            sub1.o -> sub2.i;
            sub2.o -> sub1.i;
          }
        }
        """
      ),
      // direct weak-causal feedback loop, dead feature
      arg("""
        component ValidCompWithVariability3 {
          feature f;
          a.b.E sub;
          varif (f) {
            sub.o -> sub.i;
          }
          constraint (!f);
        }
        """
      ),
      // indirect weak-causal feedback loop, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          a.b.E sub1, sub2;
          varif (f) {
            sub1.o -> sub2.i;
            sub2.o -> sub1.i;
          }
          constraint (!f);
        }
        """
      ),
      // conditional indirect weak-causal feedback loop
      arg("""
        component ValidCompWithVariability5 {
          feature f1;
          feature f2;
          a.b.E sub1;
          a.b.E sub2;
          varif (f1) {
            sub1.o -> sub2.i;
          }
          varif (f2) {
            sub2.o -> sub1.i;
          }
          constraint (!(f1 && f2));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // direct weak-causal feedback loop, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            a.b.E sub;
            varif (true) {
              sub.o -> sub.i;
            }
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // indirect weak-causal feedback loop, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            a.b.E sub1, sub2;
            varif (true) {
              sub1.o -> sub2.i;
              sub2.o -> sub1.i;
            }
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // direct weak-causal feedback loop, core feature
      arg("""
          component InvalidCompWithVariability3 {
            feature f;
            a.b.E sub;
            varif (f) {
              sub.o -> sub.i;
            }
            constraint (f);
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // indirect weak-causal feedback loop, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            a.b.E sub1, sub2;
            varif (f) {
              sub1.o -> sub2.i;
              sub2.o -> sub1.i;
            }
            constraint (f);
          }
          """,
        FEEDBACK_CAUSALITY
      ),
      // conditional indirect weak-causal feedback loop
      arg("""
          component InvalidCompWithVariability5 {
            feature f1;
            feature f2;
            a.b.E sub1;
            a.b.E sub2;
            varif (f1) {
              sub1.o -> sub2.i;
            }
            varif (f2) {
              sub2.o -> sub1.i;
            }
          }
          """,
        FEEDBACK_CAUSALITY
      )
    );
  }
}

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

import java.util.stream.Stream;

import static montiarc.util.MCError.FOR_EACH_EXPR_NOT_ITERABLE;
import static montiarc.util.MCError.FOR_EACH_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ForEachIsValid4Family}.
 */
class ForEachIsValid4FamilyTest extends ForEachIsValidTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidComp2 ",
    "ValidComp3 ",
    "ValidComp4 ",
    "ValidComp5 ",
    "ValidComp6 ",
    "ValidComp7 ",
    "ValidComp9 ",
    "ValidCompWithVariability2 ",
    "ValidCompWithVariability3 "
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ForEachIsValid4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidComp3 ",
    "InvalidComp4 ",
    "InvalidComp5 ",
    "InvalidComp6 ",
    "InvalidCompWithVariability3 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ForEachIsValid4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // for-each with expression of non-iterable, non-generic type, dead variation point
      arg("""
        import java.lang.Boolean;
        component ValidCompWithVariability1 {
          Boolean v = true;
          varif (false) {
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
        }
        """
      ),
      // for-each with expression of non-iterable, non-generic type, dead feature
      arg("""
        import java.lang.Boolean;
        component ValidCompWithVariability2 {
          feature f;
          Boolean v = true;
          varif (f) {
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // for-each with conditional type
      arg("""
        import java.lang.Boolean;
        import java.lang.Integer;
        import java.util.List;
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            List<Boolean> v = [ ];
          }
          else {
            List<Integer> v = [ ];
          }
          varif (f2) {
            automaton {
              initial state S;
              S -> S / {
                for (Boolean i : v) { }
              }
            }
          }
          constraint (!(f2 && !f1));
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // for-each with expression of non-iterable, non-generic type, tautological variation point
      arg("""
          import java.lang.Boolean;
          component InvalidCompWithVariability1 {
            Boolean v = true;
            varif (true) {
              automaton {
                initial state S;
                S -> S / {
                  for (Boolean i : v) { }
                }
              }
            }
          }
          """,
        FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with expression of non-iterable, non-generic type, core feature
      arg("""
          import java.lang.Boolean;
          component InvalidCompWithVariability2 {
            feature f;
            Boolean v = true;
            varif (f) {
              automaton {
                initial state S;
                S -> S / {
                  for (Boolean i : v) { }
                }
              }
            }
            constraint (f);
          }
          """,
        FOR_EACH_EXPR_NOT_ITERABLE
      ),
      // for-each with mismatching conditional type
      arg("""
          import java.lang.Boolean;
          import java.lang.Integer;
          import java.util.List;
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              List<Boolean> v = [ ];
            }
            else {
              List<Integer> v = [ ];
            }
            varif (f2) {
              automaton {
                initial state S;
                S -> S / {
                  for (Boolean i : v) { }
                }
              }
            }
          }
          """,
        FOR_EACH_TYPE_MISMATCH
      )
    );
  }
}

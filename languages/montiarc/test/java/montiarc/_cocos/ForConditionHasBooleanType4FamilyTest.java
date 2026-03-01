/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.params.DisableIfDisplayName;

import java.util.stream.Stream;

import static montiarc.util.MCError.FOR_CONDITION_NOT_BOOLEAN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link ForConditionHasBooleanType4Family}.
 */
class ForConditionHasBooleanType4FamilyTest extends ForConditionHasBooleanTypeTest {

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "ValidComp10 ",
    "ValidCompWithVariability4 ",
    "ValidCompWithVariability5 ",
    "ValidCompWithVariability6 ",
    "ValidCompWithVariability7 "
  })
  void shouldNotReportError(@NotNull String model) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ForConditionHasBooleanType4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @DisableIfDisplayName(contains = {
    "InvalidCompWithVariability8 ",
    "InvalidCompWithVariability9 "
  })
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcFullVariantCoCoChecker checker = new MontiArcFullVariantCoCoChecker();
    checker.get4FullVariant().addCoCo(new ForConditionHasBooleanType4Family());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // for condition not boolean in automaton, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          int v = 0;
          varif (false) {
            automaton {
              initial state S;
              S -> S / {
                for (; v; ) { }
              }
            }
          }
        }
        """
      ),
      // for condition not boolean in init block, dead variation point
      arg("""
        component ValidCompWithVariability2 {
          int v = 0;
          varif (false) {
            init {
              for (; v; ) { }
            }
          }
        }
        """
      ),
      // for condition not boolean in compute block, dead variation point
      arg("""
        component ValidCompWithVariability3 {
          int v = 0;
          varif (false) {
            compute {
              for (; v; ) { }
            }
          }
        }
        """
      ),
      // for condition not boolean in automaton, dead feature
      arg("""
        component ValidCompWithVariability4 {
          feature f;
          int v = 0;
          varif (f) {
            automaton {
              initial state S;
              S -> S / {
                for (; v; ) { }
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // for condition not boolean in init block, dead feature
      arg("""
        component ValidCompWithVariability5 {
          feature f;
          int v = 0;
          varif (f) {
            init {
              for (; v; ) { }
            }
          }
          constraint (!f);
        }
        """
      ),
      // for condition not boolean in compute block, dead feature
      arg("""
        component ValidCompWithVariability6 {
          feature f;
          int v = 0;
          varif (f) {
            compute {
              for (; v; ) { }
            }
          }
          constraint (!f);
        }
        """
      ),
      // automaton, conditional type
      arg("""
        component ValidCompWithVariability7 {
          feature f1;
          feature f2;
          varif (f1) {
            int v = 1;
          } else {
            boolean v = false;
          }
          varif (f2) {
            automaton {
              initial state S;
              S -> S / {
                for (; v; ) { }
              }
            }
          }
          constraint (!(f2 && !f1));
        }
        """
      ),
      // init block, conditional type
      arg("""
        component ValidCompWithVariability8 {
          feature f1;
          feature f2;
          varif (f1) {
            int v = 1;
          } else {
            boolean v = false;
          }
          varif (f2) {
            init {
              v = 1;
            }
          }
          constraint (!(f2 && !f1));
        }
        """
      ),
      // compute block, conditional type
      arg("""
        component ValidCompWithVariability9 {
          feature f1;
          feature f2;
          varif (f1) {
            int v = 1;
          } else {
            boolean v = false;
          }
          varif (f2) {
            compute {
              v = 1;
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
      // for condition not boolean in automaton, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            int v = 0;
            varif (true) {
              automaton {
                initial state S;
                S -> S / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in init block, tautological variation point
      arg("""
          component InvalidCompWithVariability2 {
            int v = 0;
            varif (true) {
              init {
                for (; v; ) { }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in compute block, tautological variation point
      arg("""
          component InvalidCompWithVariability3 {
            int v = 0;
            varif (true) {
              compute {
                for (; v; ) { }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in automaton, core feature
      arg("""
          component InvalidCompWithVariability4 {
            feature f;
            int v = 0;
            varif (f) {
              automaton {
                initial state S;
                S -> S / {
                  for (; v; ) { }
                }
              }
            }
            constraint (f);
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in init block, core feature
      arg("""
          component InvalidCompWithVariability5 {
            feature f;
            int v = 0;
            varif (f) {
              init {
                for (; v; ) { }
              }
            }
            constraint (f);
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in compute block, core feature
      arg("""
          component InvalidCompWithVariability6 {
            feature f;
            int v = 0;
            varif (f) {
              compute {
                for (; v; ) { }
              }
            }
            constraint (f);
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in automaton, conditional type
      arg("""
          component InvalidCompWithVariability7 {
            feature f1;
            feature f2;
            varif (f1) {
              int v = 1;
            } else {
              boolean v = false;
            }
            varif (f2) {
              automaton {
                initial state S;
                S -> S / {
                  for (; v; ) { }
                }
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in init block, conditional type
      arg("""
          component InvalidCompWithVariability8 {
            feature f1;
            feature f2;
            varif (f1) {
              int v = 1;
            } else {
              boolean v = false;
            }
            varif (f2) {
              init {
                v = 1;
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      ),
      // for condition not boolean in compute block, conditional type
      arg("""
          component InvalidCompWithVariability9 {
            feature f1;
            feature f2;
            varif (f1) {
              int v = 1;
            } else {
              boolean v = false;
            }
            varif (f2) {
              init {
                v = 1;
              }
            }
          }
          """,
        FOR_CONDITION_NOT_BOOLEAN
      )
    );
  }
}

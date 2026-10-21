/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis.context.WithinComponentResolver;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.VarDeclarationInitializationHasCorrectType4Family;
import variablearc.check.TypeCheck4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.MCError.VAR_DECLARATION_TYPE_MISMATCH;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VarDeclarationInitializationHasCorrectType4Family}.
 */
class VarDeclarationInitializationHasCorrectType4FamilyTest extends VarDeclarationInitializationHasCorrectTypeTest {

  protected MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType4Family(new TypeCheck4Family(withinComponentResolver,
      new PresenceResolver(withinComponentResolver))));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  @Override
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = this.checker();

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  @Override
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = this.checker();

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // type mismatch, var declaration with primitive type, dead variation point
      arg("""
        component ValidCompWithVariability1 {
          varif (false) {
            automaton {
              initial state S;
              S -> S / {
                int v = true;
              }
            }
          }
        }
        """
      ),
      // type mismatch, var declaration with primitive type, dead feature
      arg("""
        component ValidCompWithVariability2 {
          feature f;
          int v = 0;
          varif (f) {
            automaton {
              initial state S;
              S -> S / {
                int v = true;
              }
            }
          }
          constraint (!f);
        }
        """
      ),
      // var declaration with conditional type
      arg("""
        component ValidCompWithVariability3 {
          feature f1;
          feature f2;
          varif (f1) {
            int v = 0;
          }
          else {
            boolean v = false;
          }
          varif (f2) {
            automaton {
              initial state S;
              S -> S / {
                int w = v;
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
      // type mismatch, var declaration with primitive type, tautological variation point
      arg("""
          component InvalidCompWithVariability1 {
            varif (true) {
              automaton {
                initial state S;
                S -> S / {
                  int v = true;
                }
              }
            }
          }
          """,
        VAR_DECLARATION_TYPE_MISMATCH
      ),
      // type mismatch, var declaration with primitive type, core feature
      arg("""
          component InvalidCompWithVariability2 {
            feature f;
            int v = 0;
            varif (f) {
              automaton {
                initial state S;
                S -> S / {
                  int v = true;
                }
              }
            }
            constraint (f);
          }
          """,
        VAR_DECLARATION_TYPE_MISMATCH
      ),
      // var declaration with conditional type
      arg("""
          component InvalidCompWithVariability3 {
            feature f1;
            feature f2;
            varif (f1) {
              int v = 0;
            }
            else {
              boolean v = false;
            }
            varif (f2) {
              automaton {
                initial state S;
                S -> S / {
                  int w = v;
                }
              }
            }
          }
          """,
        VAR_DECLARATION_TYPE_MISMATCH
      )
    );
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scstatehierarchy.NoSubstatesHandler;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._visitor.MontiArcTraverser;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static montiarc.util.SCError.MISSING_INITIAL_STATE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtLeastOneInitialState}.
 */
class AtLeastOneInitialStateTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));

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
    MontiArcTraverser traverser = MontiArcMill.inheritanceTraverser();
    traverser.setSCStateHierarchyHandler(new NoSubstatesHandler());
    checker.addCoCo(new AtLeastOneInitialState(traverser));

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // no automaton
      arg("component ValidComp1 { }"),
      // automaton with initial state
      arg(
        """
          component ValidComp2 {
            automaton {
              initial state s;
            }
          }
          """
      ),
      // automaton with two initial states
      arg(
        """
          component ValidComp3 {
            automaton {
              initial state s1;
              initial state s2;
            }
          }
          """
      ),
      // inner automaton with initial state
      arg(
        """
          component ValidComp4 {
            component Inner {
              automaton {
                initial state s;
              }
            }
          }
          """
      ),
      // mode automaton with initial state
      arg(
        """
          component ValidComp5 {
            mode automaton {
              initial mode s { }
            }
          }
          """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // automaton without initial state
      arg(
        """
          component InvalidComp1 {
            automaton { }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // inner automaton without initial state
      arg(
        """
          component InvalidComp2 {
            component Inner {
              automaton { }
            }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // outer automaton without initial state, inner automaton with initial state
      arg(
        """
          component InvalidComp3 {
            automaton { }
            component Inner {
              automaton {
                initial state s;
              }
            }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // outer automaton with initial state, inner automaton without initial state
      arg(
        """
          component InvalidComp4 {
            automaton {
              initial state s;
            }
            component Inner {
              automaton { }
            }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // mode automaton without initial state
      arg(
        """
          component InvalidComp5 {
            mode automaton { }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // mode automaton without initial state, automaton with initial state
      arg(
        """
          component InvalidComp6 {
            mode automaton { }
            automaton {
              initial state s;
            }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // automaton without initial state, mode automaton with initial state
      arg(
        """
          component InvalidComp7 {
            automaton { }
            mode automaton {
              initial mode s { }
            }
          }
          """,
        MISSING_INITIAL_STATE
      ),
      // automaton without a top-level initial state, only within a nested state
      arg(
        """
          component InvalidComp8 {
            automaton {
              state s {
                initial state inners;
              }
            }
          }
          """,
        MISSING_INITIAL_STATE
      )
    );
  }
}

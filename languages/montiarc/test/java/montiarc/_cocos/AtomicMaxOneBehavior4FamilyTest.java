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
import variablearc._cocos.AtomicMaxOneBehavior4Family;
import variablearc.context.VariableArcWithinComponentResolver;
import variablearc.presence.PresenceResolver;

import java.util.stream.Stream;

import static montiarc.util.ArcError.MULTIPLE_BEHAVIOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtomicMaxOneBehavior4Family}.
 */
class AtomicMaxOneBehavior4FamilyTest extends AtomicMaxOneBehaviorTest {

  protected static MontiArcCoCoChecker checker() {
    WithinComponentResolver withinComponentResolver = new VariableArcWithinComponentResolver();
    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicMaxOneBehavior4Family(withinComponentResolver, new PresenceResolver(withinComponentResolver)));
    return checker;
  }

  @ParameterizedTest
  @MethodSource("validModels")
  @MethodSource("validModelsWithVariability")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  @MethodSource("invalidModelsWithVariability")
  void shouldReportError(@NotNull String model,
                         @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    // When
    checker().checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
    assertThat(Log.getFindings()).as("findings independent of unknown values are errors")
      .allMatch(Finding::isError);
  }

  protected static Stream<Arguments> validModelsWithVariability() {
    return Stream.of(
      // atomic component with an automaton in the if-branch and an automaton in the else-branch
      arg(
        """
          component ValidCompWithVariability1 {
            feature f1;
            varif(f1) { automaton { } }
            else { automaton { } }
          }
          """
      ),
      // atomic component with two variant automata, mutually exclusive via constraint(f1 ^ f2)
      arg(
        """
          component ValidCompWithVariability2 {
            feature f1, f2;
            varif(f1) { automaton { } }
            varif(f2) { automaton { } }
            constraint(f1 ^ f2);
          }
          """
      ),
      // inner component with two variant automata, mutually exclusive via constraint(f1 ^ f2)
      arg(
        """
          component ValidCompWithVariability3 {
            component Inner {
              feature f1, f2;
              varif(f1) { automaton { } }
              varif(f2) { automaton { } }
              constraint(f1 ^ f2);
            }
          }
          """
      ),
      // atomic component with three variant automata, pairwise mutually exclusive via constraints
      arg(
        """
          component ValidCompWithVariability4 {
            feature f1, f2, f3;
            varif(f1) { automaton { } }
            varif(f2) { automaton { } }
            varif(f3) { automaton { } }
            constraint(!(f1 && f2));
            constraint(!(f1 && f3));
            constraint(!(f2 && f3));
          }
          """
      )
    );
  }

  protected static Stream<Arguments> invalidModelsWithVariability() {
    return Stream.of(
      // atomic component with two automata (configuration f1 && f2)
      arg(
        """
          component InvalidCompWithVariability1 {
            feature f1, f2;
            varif(f1) { automaton { } }
            varif(f2) { automaton { } }
          }
          """,
        MULTIPLE_BEHAVIOR
      ),
      // atomic component with two ajava blocks (configuration f1 && f2)
      arg(
        """
          component InvalidCompWithVariability2 {
            feature f1, f2;
            varif(f1) { compute { } }
            varif(f2) { compute { } }
          }""",
        MULTIPLE_BEHAVIOR
      ),
      // atomic component with automaton and ajava (configuration f1 && f2)
      arg(
        """
          component InvalidCompWithVariability3 {
            feature f1,f2;
            varif(f1) { automaton { } }
            varif(f2) { compute { } }
          }
          """,
        MULTIPLE_BEHAVIOR
      ),
      // inner component with two automata (configuration f1 && f2)
      arg(
        """
          component InvalidCompWithVariability4 {
            component Inner {
              feature f1, f2;
              varif(f1) { automaton { } }
              varif(f2) { automaton { } }
            }
          }
          """,
        MULTIPLE_BEHAVIOR
      ),
      // atomic component with three automata (configuration f1 && f2 && f3)
      arg(
        """
          component InvalidCompWithVariability5 {
            feature f1, f2, f3;
            varif(f1) { automaton { } }
            varif(f2) { automaton { } }
            varif(f3) { automaton { } }
          }
          """,
        MULTIPLE_BEHAVIOR, MULTIPLE_BEHAVIOR
      ),
      // atomic component with three automata (configuration f1 && f2)
      arg(
        """
          component InvalidCompWithVariability6 {
            feature f1, f2;
            varif(f1) { automaton { } automaton { } }
            varif(f2) { automaton { } }
          }
          """,
        MULTIPLE_BEHAVIOR, MULTIPLE_BEHAVIOR
      ),
      // atomic component with two automata (configuration f1 && !f2)
      arg(
        """
          component InvalidCompWithVariability7 {
            feature f1, f2;
            varif(f1) { automaton { } automaton { } }
            varif(f2) { automaton { } }
            constraint(f1 ^ f2);
          }
          """,
        MULTIPLE_BEHAVIOR
      ),
      // atomic component with two automata in each of the two variants allowed by constraint(f1 ^ f2)
      arg(
        """
          component InvalidCompWithVariability8 {
            feature f1, f2;
            varif(f1) { automaton { } automaton { } }
            varif(f2) { automaton { } automaton { } }
            constraint(f1 ^ f2);
          }
          """,
        MULTIPLE_BEHAVIOR, MULTIPLE_BEHAVIOR
      ),
      // atomic component with two automata (configuration f2 && !f1)
      arg(
        """
          component InvalidCompWithVariability9 {
            feature f1, f2;
            varif(f1) { component Inner { } Inner sub; }
            varif(f2) { automaton { } automaton { } }
            constraint(f1 ^ f2);
          }
          """,
        MULTIPLE_BEHAVIOR
      )
    );
  }
}

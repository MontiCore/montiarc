/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicMaxOneBehavior;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static montiarc.util.ArcError.MULTIPLE_BEHAVIOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtomicMaxOneBehavior}.
 */
class AtomicMaxOneBehaviorTest extends MontiArcTestBase {

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicMaxOneBehavior());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicMaxOneBehavior());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes()).containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      arg("component ValidComp1 { }"),
      arg("component ValidComp2 { automaton { } }"),
      arg("component ValidComp3 { component Inner { automaton { } } }"),
      arg("component ValidComp4 { component Inner1 { automaton { } } component Inner2 { automaton { } } }"),
      arg("component ValidComp5 { compute { } }"),
      arg("component ValidComp6 { component Inner { compute { } } automaton { } }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic component with two automata
      arg(
        """
          component InvalidComp1 {
            automaton { }
            automaton { }
          }
          """, MULTIPLE_BEHAVIOR
      ),
      // atomic component with two ajava blocks
      arg(
        """
          component InvalidComp2 {
            compute { }
            compute { }
          }
          """, MULTIPLE_BEHAVIOR
      ),
      // atomic component with automaton and ajava
      arg(
        """
          component InvalidComp3 {
            automaton { }
            compute { }
          }
          """, MULTIPLE_BEHAVIOR
      ),
      // inner component with two automata
      arg(
        """
          component InvalidComp4 {
            component Inner {
              automaton { }
              automaton { }
            }
          }
          """, MULTIPLE_BEHAVIOR
      ),
      // atomic component with three automata
      arg(
        """
          component InvalidComp5 {
            automaton { }
            automaton { }
            automaton { }
          }
          """, MULTIPLE_BEHAVIOR, MULTIPLE_BEHAVIOR
      )
    );
  }
}

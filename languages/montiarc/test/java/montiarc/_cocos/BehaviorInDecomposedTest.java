/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.BehaviorInDecomposed;
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

import static montiarc.util.ArcError.DECOMPOSED_COMPONENT_WITH_BEHAVIOR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link BehaviorInDecomposed}.
 */
class BehaviorInDecomposedTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponents() {
    compile("package a.b; component A {}");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BehaviorInDecomposed());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  void shouldReportError(@NotNull String model,
                         @NotNull Error[] expectedErrors) {
    // Given
    ASTMACompilationUnit ast = MontiArcTestBase.compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new BehaviorInDecomposed());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // decomposed component (subcomponent instance) without its own behavior
      arg("""
        component ValidComp1 {
          a.b.A a;
        }
        """
      ),
      // atomic component with an uninstantiated nested component type and its own behavior
      arg("""
        component ValidComp2 {
          component A { }
          compute {}
        }
        """
      ),
      // atomic component with an automaton
      arg("""
        component ValidComp3 {
          automaton { }
        }
        """
      ),
      // atomic component without behavior
      arg("component ValidComp4 { }")
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // decomposed component with ajava behavior
      arg("""
          component InvalidComp1 {
            a.b.A a;
            compute {}
          }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // decomposed component with automaton behavior
      arg("""
          component InvalidComp2 {
            a.b.A a; automaton {
              initial state S;
            }
          }
          """,
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // decomposed component with ajava and automaton behavior
      arg("""
          component InvalidComp3 {
            a.b.A a;
            compute { }
            automaton {
              initial state S;
            }
          }
          """,
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      ),
      // nested decomposed component with ajava behavior
      arg("""
          component InvalidComp4 {
            component A {
              a.b.A a;
              compute { }
            }
          }""",
        DECOMPOSED_COMPONENT_WITH_BEHAVIOR
      )
    );
  }
}

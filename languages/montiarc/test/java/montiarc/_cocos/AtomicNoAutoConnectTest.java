/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.AtomicNoAutoConnect;
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

import static montiarc.util.ComfortableArcError.AUTOCONNECT_IN_ATOMIC_COMPONENT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtomicNoAutoConnect}.
 */
class AtomicNoAutoConnectTest extends MontiArcTestBase {

  @BeforeEach
  void setUp() {
    compile("component A { }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoAutoConnect());

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
    checker.addCoCo(new AtomicNoAutoConnect());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> validModels() {
    return Stream.of(
      // atomic (empty) no autoconnect
      arg("component ValidComp1 { }"),
      // atomic (automaton) no autoconnect
      arg("""
        component ValidComp2 {
          automaton { }
        }
        """
      ),
      // composed no autoconnect
      arg("""
        component ValidComp3 {
          A a;
        }
        """
      ),
      // composed autoconnect port
      arg("""
        component ValidComp4 {
          A a; autoconnect port;
        }
        """
      ),
      // composed autoconnect type
      arg("""
        component ValidComp5 {
          A a;
          autoconnect type;
        }
        """
      ),
      // composed autoconnect off
      arg("""
        component ValidComp6 {
          A a;
          autoconnect off;
        }
        """
      ),
      // composed (inner) no autoconnect
      arg("""
        component ValidComp7 {
          component Inner { }
          Inner sub;
        }
        """
      ),
      // composed (inner) autoconnect
      arg("""
        component ValidComp8 {
          component Inner { }
          Inner sub;
          autoconnect port;
        }
        """
      )
    );
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // atomic (empty) autoconnect port
      arg("""
          component InvalidComp1 {
            autoconnect port;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      ),
      // atomic (empty) autoconnect type
      arg("""
          component InvalidComp2 {
            autoconnect type;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      ),
      // atomic (empty) autoconnect off
      arg("""
          component InvalidComp3 {
            autoconnect off;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      ),
      // atomic (automaton) autoconnect port
      arg("""
          component InvalidComp4 {
            automaton { }
            autoconnect port;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      ),
      // atomic two autoconnects
      arg("""
          component InvalidComp5 {
            autoconnect port;
            autoconnect port;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      ),
      // atomic inner autoconnect
      arg("""
          component InvalidComp6 {
            component Inner {
              autoconnect port;
            }
            Inner sub;
          }
          """,
        AUTOCONNECT_IN_ATOMIC_COMPONENT
      )
    );
  }
}

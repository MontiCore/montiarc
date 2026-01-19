/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.AtomicNoConnector;
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

import static montiarc.util.ArcError.CONNECTORS_IN_ATOMIC;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link AtomicNoConnector}.
 */
class AtomicNoConnectorTest extends MontiArcTestBase {

  @BeforeEach
  public void setUp() {
    compile("component A { }");
  }

  @ParameterizedTest
  @MethodSource("validModels")
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new AtomicNoConnector());

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
    checker.addCoCo(new AtomicNoConnector());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  static Stream<Arguments> validModels() {
    return Stream.of(
      // atomic component without ports and connectors
      arg("""
        component ValidComp1 { }
        """
      ),
      // composed component with connector
      arg("""
        component ValidComp2 {
          port in int i;
          port out int o;
          A a;
          i -> o;
        }
        """
      ),
      // inner composed component with connector
      arg("""
        component ValidComp3 {
          component Inner {
            port in int i;
            port out int o;
            A a;
            i -> o;
          }
        }
        """
      )
    );
  }

  static Stream<Arguments> invalidModels() {
    // atomic component with connector
    return Stream.of(
      arg("""
        component InvalidComp1 {
          port in int i;
          port out int o;
          i -> o;
        }
        """,
        CONNECTORS_IN_ATOMIC
      ),
      // atomic component with two connectors
      arg("""
        component InvalidComp2 {
          port in int i;
          port out int o;
          i -> o;
          i -> o;
        }
        """,
        CONNECTORS_IN_ATOMIC, CONNECTORS_IN_ATOMIC
      ),
      // atomic component with connector and an inner component
      arg("""
        component InvalidComp3 {
          port in int i;
          port out int o;
          component Inner { }
          i -> o;
        }
        """,
        CONNECTORS_IN_ATOMIC
      ),
      // inner atomic component with connector
      arg("""
        component InvalidComp4 {
          component Inner {
            port in int i;
            port out int o;
            i -> o;
          }
        }
        """,
        CONNECTORS_IN_ATOMIC
      )
    );
  }
}

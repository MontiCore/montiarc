/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import com.google.common.base.Preconditions;
import comfortablearc._cocos.MaxOneAutoConnect;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.ComfortableArcError.MULTIPLE_AUTOCONNECTS;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MaxOneAutoConnect}.
 */
class MaxOneAutoConnectTest extends MontiArcTestBase {

  @BeforeEach
  protected void setUpComponent() {
    compile("component A { }");
  }

  @ParameterizedTest
  @ValueSource(strings = {
    // atomic component, no autoconnect
    "component ValidComp1 { }",
    // composed component, no autoconnect
    "component ValidComp2 { A a; }",
    // composed component with one autoconnect
    "component ValidComp3 { A a; autoconnect port; }",
    // outer and inner component each with one autoconnect
    "component ValidComp4 { component Inner { A a; autoconnect port; } Inner sub; autoconnect port; }"
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new MaxOneAutoConnect());

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
    checker.addCoCo(new MaxOneAutoConnect());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // two autoconnects of the same kind, reported as a single finding
      arg("""
        component InvalidComp1 {
          A a;
          autoconnect port;
          autoconnect port;
        }
        """,
        MULTIPLE_AUTOCONNECTS
      ),
      // two autoconnects of different kinds, reported as a single finding
      arg("""
        component InvalidComp2 {
          A a;
          autoconnect port;
          autoconnect type;
        }
        """,
        MULTIPLE_AUTOCONNECTS
      ),
      // three autoconnects, still reported as a single finding
      arg("""
        component InvalidComp3 {
          A a;
          autoconnect port;
          autoconnect port;
          autoconnect port;
        }
        """,
        MULTIPLE_AUTOCONNECTS
      )
    );
  }
}

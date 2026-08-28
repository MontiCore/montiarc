/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcprepost._cocos.TriggerPortExists;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static montiarc.util.PrePostError.TRIGGER_MISSING_PORT;
import static montiarc.util.PrePostError.TRIGGER_NOT_AN_IN_PORT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link TriggerPortExists}.
 */
class PrePostTriggerPortExistsTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // simple port
    """
      component ValidComp1 {
        port in int id;
        trigger: id;
        post: true;
      }
      """,
    // generic port
    """
      component ValidComp2<T> {
        port in T id;
        trigger: id;
        post: true;
      }
      """,
    // we check multiple ports in other cocos, here we only emit a debug message
    """
      component ValidComp3 {
        port in int id;
        port in int id;
        trigger: id;
        post: true;
      }
      """,
  })
  void shouldNotReportError(@NotNull String model) {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TriggerPortExists());

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
    checker.addCoCo(new TriggerPortExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      // trigger references an identifier that isn't declared as a port
      arg("""
        component InvalidComp1 {
          trigger: id;
          post: true;
        }
        """,
        TRIGGER_MISSING_PORT
      ),
      // trigger references a port name with a different case than the declared port
      arg("""
        component InvalidComp2 {
          port in int Id;
          trigger: id;
          post: true;
        }
        """,
        TRIGGER_MISSING_PORT
      ),
      // trigger references an output port instead of an input port
      arg("""
        component InvalidComp3 {
          port out int id;
          trigger: id;
          post: true;
        }
        """,
        TRIGGER_NOT_AN_IN_PORT
      ),
      // trigger references a field instead of a port
      arg("""
        component InvalidComp4 {
          int id = 0;
          trigger: id;
          post: true;
        }
        """,
        TRIGGER_MISSING_PORT
      )
    );
  }
}

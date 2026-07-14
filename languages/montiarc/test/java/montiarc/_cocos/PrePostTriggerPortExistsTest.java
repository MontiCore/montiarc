/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcprepost._cocos.TriggerPortExists;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTestBase;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Error;
import montiarc.util.PrePostError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PrePostTriggerPortExistsTest extends MontiArcTestBase {

  @ParameterizedTest
  @ValueSource(strings = {
    // simple port
    """
      component SimplePort {
        port in int id;
        trigger: id;
        post: true;
      }
   """,
    //generic port
    """ 
      component GenericPort<T> {
        port in T id;
        trigger: id;
        post: true;
      }
    """,
    // We check multiple ports in other cocos. Here we only emit a debug message
    """ 
      component MultiplePorts {
        port in int id;
        port in int id;
        trigger: id;
        post: true;
      }
    """,
  })
  public void shouldNotReportError(@NotNull String model) throws IOException {
    Preconditions.checkNotNull(model);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TriggerPortExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindingsCount()).as(Log.getFindings().toString()).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource("invalidModels")
  public void shouldReportError(@NotNull String model, @NotNull Error... errors) throws IOException {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    // Given
    ASTMACompilationUnit ast = compile(model);

    MontiArcCoCoChecker checker = new MontiArcCoCoChecker();
    checker.addCoCo(new TriggerPortExists());

    // When
    checker.checkAll(ast);

    // Then
    assertThat(Log.getFindings()).as(Log.getFindings().toString()).isNotEmpty();
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(errors));
  }

  protected static Stream<Arguments> invalidModels() {
    return Stream.of(
      arg(
        """ 
          component NoPort {
            trigger: id;
            post: true;
          }
        """,
        PrePostError.TRIGGER_MISSING_PORT),

      arg(
        """ 
          component WrongName {
            port in int Id;
            trigger: id;
            post: true;
          }
        """,
        PrePostError.TRIGGER_MISSING_PORT),
      arg(
        """ 
          component OutPort {
            port out int id;
            trigger: id;
            post: true;
          }
        """,
        PrePostError.TRIGGER_NOT_AN_IN_PORT),
      arg(
        """ 
          component PortIsAttribute {
            int id = 0;
            trigger: id;
            post: true;
          }
        """,
        PrePostError.TRIGGER_MISSING_PORT)

    );
  }
}

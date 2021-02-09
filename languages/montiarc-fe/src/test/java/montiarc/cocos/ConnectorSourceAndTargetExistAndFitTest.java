/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.ConnectorSourceAndTargetExistAndFit;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ConnectorSourceAndTargetExistAndFitTest extends AbstractCoCoTest {

  protected static final String MODEL_PATH = "montiarc/cocos/connectorSourceAndTargetExistAndFit/";

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("EverythingMismatched.arc", new ArcError[]{
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS, ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH, ArcError.SOURCE_PORT_NOT_EXISTS,
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.TARGET_PORT_NOT_EXISTS, ArcError.PORT_DIRECTION_MISMATCH
      }),
      Arguments.of("SourceAndTargetDoNotExist.arc", new ArcError[]{
        ArcError.SOURCE_PORT_NOT_EXISTS, ArcError.TARGET_PORT_NOT_EXISTS
      }),
      Arguments.of("SourceAndTargetTypeMismatch.arc", new ArcError[]{
        ArcError.SOURCE_AND_TARGET_TYPE_MISMATCH
      }),
      Arguments.of("SourceAndTargetWrongDirection.arc", new ArcError[]{
        ArcError.PORT_DIRECTION_MISMATCH, ArcError.PORT_DIRECTION_MISMATCH
      }),
      Arguments.of("SourceDoesNotExist.arc", new ArcError[]{
        ArcError.SOURCE_PORT_NOT_EXISTS
      }),
      Arguments.of("SourceWrongDirection.arc", new ArcError[]{
        ArcError.PORT_DIRECTION_MISMATCH
      }),
      Arguments.of("TargetDoesNotExist.arc", new ArcError[]{
        ArcError.TARGET_PORT_NOT_EXISTS
      }),
      Arguments.of("TargetWrongDirection.arc", new ArcError[]{
        ArcError.PORT_DIRECTION_MISMATCH
      })
    );
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new ConnectorSourceAndTargetExistAndFit());
  }

  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path pathToModel = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, model);
    ASTMACompilationUnit ast = this.getTool().parse(pathToModel).orElse(null);
    this.getTool().createSymbolTable(ast);
    return ast;
  }

  @ParameterizedTest
  @ValueSource(strings = {"AllSourcesAndTargetsExistAndFit.arc", "NoConnectors.arc"})
  public void connectorSourceAndTargetShouldFit(@NotNull String model) {
    Preconditions.checkNotNull(model);
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFind(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);
    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }
}
/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import arcbasis._cocos.ConnectorSourceAndTargetComponentDiffer;
import arcbasis._cocos.ConnectorSourceAndTargetDiffer;
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

public class ConnectorSourceAndTargetDifferTest extends AbstractCoCoTest {

  protected static final String MODEL_PATH = "montiarc/cocos/";

  protected static final String PACKAGE = "connectorSourceAndTargetDiffer";

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      Arguments.of("ConnectorSourceAndTargetFromSameComponent.arc",
        new ArcError[]{ArcError.SOURCE_AND_TARGET_SAME_COMPONENT}),
      Arguments.of("ConnectorSourceAndTargetSamePort.arc",
        new ArcError[]{ArcError.SOURCE_AND_TARGET_SAME_COMPONENT, ArcError.CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL}));
  }

  protected ASTMACompilationUnit parseAndLoadSymbols(@NotNull String model) {
    Preconditions.checkNotNull(model);
    Path pathToModel = Paths.get(RELATIVE_MODEL_PATH, MODEL_PATH, PACKAGE, model);
    ASTMACompilationUnit ast = this.getTool().parse(pathToModel).orElse(null);
    this.getTool().createSymbolTable(ast);
    return ast;
  }

  @ParameterizedTest
  @ValueSource(strings = {"ConnectorSourceAndTargetComponentDiffer.arc", "NoConnectors.arc"})
  public void sourceAndTargetShouldDiffer(@NotNull String model) {
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
  public void shouldDetectSourceAndTargetAreTheSame(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //then
    this.checkOnlyExpectedErrorsPresent(Log.getFindings(), errors);
  }

  @Override
  protected void registerCoCos() {
    this.getChecker().addCoCo(new ConnectorSourceAndTargetComponentDiffer())
      .addCoCo(new ConnectorSourceAndTargetDiffer());
  }
}
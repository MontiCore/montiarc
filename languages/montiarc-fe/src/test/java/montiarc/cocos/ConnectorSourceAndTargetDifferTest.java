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

import java.util.stream.Stream;

public class ConnectorSourceAndTargetDifferTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "connectorSourceAndTargetDiffer";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("ConnectorSourceAndTargetFromSameComponent.arc",
          ArcError.SOURCE_AND_TARGET_SAME_COMPONENT),
      arg("ConnectorSourceAndTargetSamePort.arc",
          ArcError.SOURCE_AND_TARGET_SAME_COMPONENT,
          ArcError.CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL));
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
    this.getChecker().addCoCo(new ConnectorSourceAndTargetComponentDiffer());
    this.getChecker().addCoCo(new ConnectorSourceAndTargetDiffer());
  }
}
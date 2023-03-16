/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.SubPortsConnected;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class SubPortsConnectedTest extends AbstractCoCoTest {

  protected static String PACKAGE = "subPortsConnected";

  @Override
  @BeforeEach
  public void init() {
    super.init();
  }

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new SubPortsConnected());
  }

  @ParameterizedTest
  @ValueSource(strings = {"AllSubPortsConnected.arc"})
  public void shouldApproveValidPortConnections(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateAndCompleteSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    Assertions.assertEquals(0, Log.getFindingsCount());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
            arg("SubIncomingPortNotConnected.arc",
                    ArcError.INCOMING_PORT_NOT_CONNECTED, ArcError.INCOMING_PORT_NOT_CONNECTED),
            arg("SubOutgoingPortNotConnected.arc",
                    ArcError.OUTGOING_PORT_NOT_CONNECTED, ArcError.OUTGOING_PORT_NOT_CONNECTED),
            arg("MissingSubPortConnections.arc",
                    ArcError.OUTGOING_PORT_NOT_CONNECTED, ArcError.OUTGOING_PORT_NOT_CONNECTED,
                    ArcError.INCOMING_PORT_NOT_CONNECTED, ArcError.INCOMING_PORT_NOT_CONNECTED)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindInvalidTypes(@NotNull String model, @NotNull Error... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndCreateSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }
}


/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import variablearc._cocos.VariableArcASTArcBlockCoCo;
import variablearc._cocos.VariableArcASTArcIfStatementCoCo;
import variablearc._cocos.VariableElementsUsage;
import variablearc._cocos.VariantCoCos;

import java.util.stream.Stream;

/**
 * Tests for {@link VariantCoCos}
 */
public class VariantCoCosTest extends AbstractCoCoTest {

  protected static String PACKAGE = "variantCoCos";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker);
    checker.addCoCo(new VariantCoCos());
    checker.addCoCo((VariableArcASTArcIfStatementCoCo) new VariableElementsUsage());
    checker.addCoCo((VariableArcASTArcBlockCoCo) new VariableElementsUsage());
    checker.addCoCo((ArcBasisASTComponentTypeCoCo) new VariableElementsUsage());
  }

  protected static Stream<Arguments> modelAndExpectedErrorsProvider() {
    return Stream.of(
      arg("ValidWithMultipleVariants.arc"),
      arg("ValidWithComposedVariants.arc"),
      arg("ValidWithVariantsAndComposedVariants.arc"),
      arg("InvalidConnectorSourceAndTarget.arc",
        ArcError.MISSING_PORT,
        ArcError.MISSING_PORT,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.CONNECTOR_TYPE_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH
      ),
      arg("InvalidConnectorSourceAndTargetTimings.arc",
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH,
        ArcError.CONNECTOR_TIMING_MISMATCH
      ),
      arg("InvalidUniqueIdentifiersNames.arc",
        ArcError.IN_PORT_UNUSED,
        ArcError.UNIQUE_IDENTIFIER_NAMES,
        ArcError.UNIQUE_IDENTIFIER_NAMES,
        ArcError.UNIQUE_IDENTIFIER_NAMES,
        ArcError.UNIQUE_IDENTIFIER_NAMES
      ),
//      arg("InvalidFeedbackLoop.arc",
//        ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED
//      ),
//      arg("InvalidNestedFeedbackLoop.arc",
//        ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED,
//        ArcError.FEEDBACK_LOOP_TIMING_NOT_DELAYED
//      ),
      arg("InvalidPortsConnected.arc",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED
      ),
      arg("InvalidPortsUniqueSender.arc",
        ArcError.PORT_MULTIPLE_SENDER
      ),
      arg("InvalidSubPortsConnected.arc",
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.IN_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED,
        ArcError.OUT_PORT_NOT_CONNECTED
      ),
//      arg("InvalidInheritedPortsTypeCorrect.arc",
//        ArcError.INHERITED_INCOMING_PORT_TYPE_MISMATCH,
//        ArcError.INHERITED_INCOMING_PORT_TYPE_MISMATCH,
//        ArcError.INHERITED_PORT_DIRECTION_MISMATCH,
//        ArcError.INHERITED_PORT_DIRECTION_MISMATCH
//      ),
      arg("InvalidUnderspecification.arc",
        ArcError.SOURCE_DIRECTION_MISMATCH,
        ArcError.TARGET_DIRECTION_MISMATCH
      )
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorsProvider")
  public void shouldFindErrors(@NotNull String model, @NotNull Error... errors) {
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

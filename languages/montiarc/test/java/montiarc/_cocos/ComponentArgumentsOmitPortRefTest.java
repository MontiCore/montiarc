/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.ComponentArgumentsOmitPortRef;
import com.google.common.base.Preconditions;
import montiarc._cocos.util.PortReferenceExtractor4CommonExpressions;
import montiarc.util.ArcError;
import montiarc.util.Error;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class ComponentArgumentsOmitPortRefTest extends AbstractCoCoTest {

  protected final static String PACKAGE = "instanceArgsOmitPortReferences";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(@NotNull MontiArcCoCoChecker checker) {
    Preconditions.checkNotNull(checker).addCoCo(new ComponentArgumentsOmitPortRef(
      new PortReferenceExtractor4CommonExpressions())
    );
  }

  @BeforeEach
  public void prepareModels() {
    // loading helper models into the symboltable
    this.parseAndCreateAndCompleteSymbols("toInstantiate/IndependentComp.arc");
    this.parseAndCreateAndCompleteSymbols("toInstantiate/IndependentChild.arc");
    this.parseAndCreateAndCompleteSymbols("toInstantiate/WithParams.arc");
  }

  @ParameterizedTest
  @ValueSource(strings = {"WithoutPortRef.arc"})
  void shouldNotFindPortReferences(@NotNull String model) {
    testModel(model);
  }

  protected static Stream<Arguments> modelAndExpectedErrorProvider() {
    return Stream.of(
      arg("WithExternalModelInheritedPortRef.arc",
        ArcError.COMP_ARG_PORT_REF, ArcError.COMP_ARG_PORT_REF),
      arg("WithExternalModelPortRef.arc",
        ArcError.COMP_ARG_PORT_REF, ArcError.COMP_ARG_PORT_REF),
      arg("WithInheritedPortRef.arc",
        ArcError.COMP_ARG_PORT_REF, ArcError.COMP_ARG_PORT_REF),
      arg("WithOwnPortRef.arc",
        ArcError.COMP_ARG_PORT_REF, ArcError.COMP_ARG_PORT_REF),
      arg("WithSubCompPortRef.arc",
        ArcError.COMP_ARG_PORT_REF, ArcError.COMP_ARG_PORT_REF)
    );
  }

  @ParameterizedTest
  @MethodSource("modelAndExpectedErrorProvider")
  void shouldFindIllegalPortReferences(@NotNull String model, @NotNull Error... errors) {
    testModel(model, errors);
  }
}

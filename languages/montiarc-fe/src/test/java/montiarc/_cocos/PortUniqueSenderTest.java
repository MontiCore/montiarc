/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._cocos.PortUniqueSender;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import montiarc._ast.ASTMACompilationUnit;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

/**
 * Holds tests for {@link arcbasis._cocos.PortUniqueSender}.
 */
public class PortUniqueSenderTest extends AbstractCoCoTest {

  protected static final String PACKAGE = "portUniqueSender";

  @Override
  protected String getPackage() {
    return PACKAGE;
  }

  @Override
  protected void registerCoCos(MontiArcCoCoChecker checker) {
    checker.addCoCo(new PortUniqueSender());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "HiddenChannel.arc",
    "IncomingPortForward.arc",
    "OutgoingPortForward.arc",
    "PortForwardAndHiddenChannel.arc"
  })
  public void shouldNotFindPortHasMultipleSenders(@NotNull String model) {
    Preconditions.checkNotNull(model);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(new ArcError[]{});
  }

  @ParameterizedTest
  @MethodSource("invalidModelsAndErrorProvider")
  public void shouldNotFindPortHasMultipleSenders(@NotNull String model, @NotNull ArcError... errors) {
    Preconditions.checkNotNull(model);
    Preconditions.checkNotNull(errors);

    //Given
    ASTMACompilationUnit ast = this.parseAndLoadSymbols(model);

    //When
    this.getChecker().checkAll(ast);

    //Then
    this.checkOnlyExpectedErrorsPresent(errors);
  }

  protected static Stream<Arguments> invalidModelsAndErrorProvider() {
    return Stream.of(
      Arguments.of("MultipleSenderIncomingPortForward.arc", new ArcError[]{ArcError.PORT_MULTIPLE_SENDER}),
      Arguments.of("MultipleSenderOutgoingPortForward.arc", new ArcError[]{ArcError.PORT_MULTIPLE_SENDER}),
      Arguments.of("MultipleSenderHiddenChannel.arc", new ArcError[]{ArcError.PORT_MULTIPLE_SENDER}),
      Arguments.of("MultipleSenderPortForwardAndHiddenChannel.arc", new ArcError[]{ArcError.PORT_MULTIPLE_SENDER,
        ArcError.PORT_MULTIPLE_SENDER, ArcError.PORT_MULTIPLE_SENDER})
    );
  }
}
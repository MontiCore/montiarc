/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcbasis._symboltable.ArcPortSymbol;
import com.google.common.base.Preconditions;
import modes.ModesAbstractTest;
import modes.ModesMill;
import modes._ast.ASTArcMode;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

/**
 * Tests for {@link ModeOmitPortDefinition}
 */
public class ModeOmitPortDefinitionTest extends ModesAbstractTest {

  protected static Stream<Arguments> numberOfPortSymbolsWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, new ModesError[]{}),
      Arguments.of(1, new ModesError[]{ModesError.MODE_CONTAINS_PORT_DEFINITION}),
      Arguments.of(2, new ModesError[]{ModesError.MODE_CONTAINS_PORT_DEFINITION, ModesError.MODE_CONTAINS_PORT_DEFINITION}),
      Arguments.of(3, new ModesError[]{ModesError.MODE_CONTAINS_PORT_DEFINITION, ModesError.MODE_CONTAINS_PORT_DEFINITION, ModesError.MODE_CONTAINS_PORT_DEFINITION})
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfPortSymbolsWithErrorProvider")
  public void testCocoWithNModeAutomata(int numberOfPortSymbols, @NotNull ModesError... expectedErrors) {
    Preconditions.checkArgument(numberOfPortSymbols >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTArcMode mode = ModesMill.arcModeBuilder()
      .setName("m")
      .setBody(ModesMill.componentBodyBuilder().build())
      .build();
    mode.setSpannedScope(ModesMill.scope());
    mode.setSymbol(ModesMill.arcModeSymbolBuilder().setName("m").setAstNode(mode).setSpannedScope(mode.getSpannedScope()).build());
    for (int i = 0; i < numberOfPortSymbols; i++) {
      mode.getSpannedScope().add(Mockito.mock(ArcPortSymbol.class));
    }

    // When
    ModeOmitPortDefinition coco = new ModeOmitPortDefinition();
    coco.check(mode);

    // Then
    checkOnlyExpectedErrorsPresent(expectedErrors);
  }
}

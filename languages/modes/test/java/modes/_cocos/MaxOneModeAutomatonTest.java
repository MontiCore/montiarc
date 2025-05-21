/* (c) https://github.com/MontiCore/monticore */
package modes._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentInstantiation;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import modes.ModesTestBase;
import modes._ast.ASTModeAutomaton;
import montiarc.util.ModesError;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MaxOneModeAutomaton}
 */
public class MaxOneModeAutomatonTest extends ModesTestBase {

  protected static Stream<Arguments> numberOfModeAutomataWithErrorProvider() {
    return Stream.of(
      Arguments.of(0, false, new ModesError[]{}),
      Arguments.of(1, false, new ModesError[]{}),
      Arguments.of(2, false, new ModesError[]{ModesError.MULTIPLE_MODE_AUTOMATA}),
      Arguments.of(3, false, new ModesError[]{ModesError.MULTIPLE_MODE_AUTOMATA, ModesError.MULTIPLE_MODE_AUTOMATA}),
      Arguments.of(0, true, new ModesError[]{}),
      Arguments.of(1, true, new ModesError[]{}),
      Arguments.of(2, true, new ModesError[]{}),
      Arguments.of(3, true, new ModesError[]{})
    );
  }

  @ParameterizedTest
  @MethodSource("numberOfModeAutomataWithErrorProvider")
  public void testCocoWithNModeAutomata(int numberOfModeAutomata, boolean atomic, @NotNull ModesError... expectedErrors) {
    Preconditions.checkArgument(numberOfModeAutomata >= 0);
    Preconditions.checkNotNull(expectedErrors);

    // Given
    ASTArcComponentType compType = ArcBasisMill.arcComponentTypeBuilder()
      .setName("Comp")
      .setBody(ArcBasisMill.componentBodyBuilder().build())
      .setHead(Mockito.mock(ASTComponentHead.class))
      .build();
    for (int i = 0; i < numberOfModeAutomata; i++) {
      compType.getBody().addArcElement(Mockito.mock(ASTModeAutomaton.class));
    }

    if (!atomic) {
      compType.getBody().addArcElement(Mockito.mock(ASTComponentInstantiation.class));
    }

    ArcBasisMill.scopesGenitorDelegator().createFromAST(compType);
    ArcBasisMill.scopesGenitorP2Delegator().createFromAST(compType);
    ArcBasisMill.scopesGenitorP3Delegator().createFromAST(compType);

    if (!atomic) {
      compType.getSpannedScope().add(Mockito.mock(SubcomponentSymbol.class));
    }

    // When
    MaxOneModeAutomaton coco = new MaxOneModeAutomaton();
    coco.check(compType);

    // Then
    assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(expectedErrors));
  }
}

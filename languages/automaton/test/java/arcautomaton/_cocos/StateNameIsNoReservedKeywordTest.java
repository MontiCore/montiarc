/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcbasis.ArcBasisAbstractTest;
import de.monticore.scbasis._ast.ASTSCModifier;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCSBody;
import de.monticore.scbasis._ast.ASTSCState;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

public class StateNameIsNoReservedKeywordTest extends ArcBasisAbstractTest {

  @Test
  public void checkPortNameMatchesKeyword() {
    // Given
    ASTSCState state = ArcAutomatonMill.sCStateBuilder()
      .setName("keyword")
      .setSCModifier(Mockito.mock(ASTSCModifier.class))
      .setSCSAnte(Mockito.mock(ASTSCSAnte.class))
      .setSCSBody(Mockito.mock(ASTSCSBody.class))
      .build();
    StateNameIsNoReservedKeyword coco =
      new StateNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESTRICTED_IDENTIFIER;

    // When
    coco.check(state);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  public void checkPortNameIsNoKeyword() {
    // Given
    ASTSCState state = ArcAutomatonMill.sCStateBuilder()
      .setName("noKeyword")
      .setSCModifier(Mockito.mock(ASTSCModifier.class))
      .setSCSAnte(Mockito.mock(ASTSCSAnte.class))
      .setSCSBody(Mockito.mock(ASTSCSBody.class))
      .build();
    StateNameIsNoReservedKeyword coco =
      new StateNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));

    // When
    coco.check(state);

    // Then
    checkOnlyExpectedErrorsPresent();
  }
}

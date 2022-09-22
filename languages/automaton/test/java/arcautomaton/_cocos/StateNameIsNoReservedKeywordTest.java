/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcbasis.AbstractTest;
import de.monticore.scbasis._ast.ASTSCModifier;
import de.monticore.scbasis._ast.ASTSCSAnte;
import de.monticore.scbasis._ast.ASTSCSBody;
import de.monticore.scbasis._ast.ASTSCState;
import montiarc.util.ArcError;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

class StateNameIsNoReservedKeywordTest extends AbstractTest {

  @Test
  void checkPortNameMatchesKeyword() {
    // Given
    ASTSCState state = ArcAutomatonMill.sCStateBuilder()
      .setName("keyword")
      .setSCModifier(Mockito.mock(ASTSCModifier.class))
      .setSCSAnte(Mockito.mock(ASTSCSAnte.class))
      .setSCSBody(Mockito.mock(ASTSCSBody.class))
      .build();
    StateNameIsNoReservedKeyword coco =
      new StateNameIsNoReservedKeyword("testLang", Collections.singleton("keyword"));
    ArcError expectedError = ArcError.RESERVED_KEYWORD_USED;

    // When
    coco.check(state);

    // Then
    checkOnlyExpectedErrorsPresent(expectedError);
  }

  @Test
  void checkPortNameIsNoKeyword() {
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

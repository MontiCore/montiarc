/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import comfortablearc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds tests for methods of {@link ASTArcAutoInstantiate}.
 */
public class AutoInstantiateTest extends AbstractTest {

  /**
   * Method under test {@link ASTArcAutoInstantiate#setArcAIMode(ASTArcAIMode)}.
   */
  @Test
  public void modeSetterShouldThrowException() {
    //Given
    ASTArcAutoInstantiate ai = createAIOff();

    //When && Then
    Assertions.assertThrows(NullPointerException.class, () -> ai.setArcAIMode(null));
  }

  /**
   * Method under test {@link ASTArcAutoInstantiate#isAIOff()}.
   */
  @Test
  public void shouldEvaluateAIOff() {
    //Given
    ASTArcAutoInstantiate ai = createAIOff();

    //When && Then
    Assertions.assertTrue(ai.isAIOff());
    Assertions.assertFalse(ai.isAIOn());
  }

  /**
   * Method under test {@link ASTArcAutoInstantiate#isAIOn()}.
   */
  @Test
  public void shouldEvaluateAIOn() {
    //Given
    ASTArcAutoInstantiate ai = createAIOn();

    //When && Then
    Assertions.assertFalse(ai.isAIOff());
    Assertions.assertTrue(ai.isAIOn());
  }
}
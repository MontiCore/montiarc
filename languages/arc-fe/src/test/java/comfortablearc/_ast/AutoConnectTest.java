/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import comfortablearc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds tests for methods of {@link ASTArcAutoConnect}.
 */
public class AutoConnectTest extends AbstractTest {

  /**
   * Method under test {@link ASTArcAutoConnect#setArcACMode(ASTArcACMode)}.
   */
  @Test
  public void modeSetterShouldException() {
    //Given
    ASTArcAutoConnect ac = createACOff();

    //When && Then
    Assertions.assertThrows(NullPointerException.class, () -> ac.setArcACMode(null));
  }

  /**
   * Method under test {@link ASTArcAutoConnect#isACOff()}.
   */
  @Test
  public void shouldEvaluateACOff() {
    //Given
    ASTArcAutoConnect ac = createACOff();

    //When && Then
    Assertions.assertTrue(ac.isACOff());
    Assertions.assertFalse(ac.isACPortActive());
    Assertions.assertFalse(ac.isACTypeActive());
  }

  /**
   * Method under test {@link ASTArcAutoConnect#isACPortActive()}.
   */
  @Test
  public void shouldEvaluateACPortActive() {
    //Given
    ASTArcAutoConnect ac = createACPort();

    //When && Then
    Assertions.assertFalse(ac.isACOff());
    Assertions.assertTrue(ac.isACPortActive());
    Assertions.assertFalse(ac.isACTypeActive());
  }

  /**
   * Method under test {@link ASTArcAutoConnect#isACTypeActive()}.
   */
  @Test
  public void shouldEvaluateACTypeActive() {
    //Given
    ASTArcAutoConnect ac = createACType();

    //When && Then
    Assertions.assertFalse(ac.isACOff());
    Assertions.assertFalse(ac.isACPortActive());
    Assertions.assertTrue(ac.isACTypeActive());
  }
}
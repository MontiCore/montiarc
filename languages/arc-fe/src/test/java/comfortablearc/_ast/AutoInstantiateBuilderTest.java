/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import comfortablearc.ComfortableArcMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ASTArcAutoInstantiateBuilder}.
 */
public class AutoInstantiateBuilderTest {

  /**
   * Method under test {@link ASTArcAutoInstantiateBuilder#setArcAIMode(ASTArcAIMode)}.
   */
  @Test
  public void modeSetterShouldThrowIllegalArgumentException() {
    //Given
    ASTArcAutoInstantiateBuilder builder = ComfortableArcMill.arcAutoInstantiateBuilder();

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setArcAIMode(null));
  }

  /**
   * Method under test {@link ASTArcAutoInstantiateBuilder#setAIModeOn()}.
   */
  @Test
  public void shouldSetAIModeOn() {
    //Given
    ASTArcAutoInstantiateBuilder builder = ComfortableArcMill.arcAutoInstantiateBuilder();

    //When
    builder.setAIModeOn();

    //Then
    Assertions.assertTrue(builder.isAIModeOn());
    Assertions.assertFalse(builder.isAIModeOff());
  }

  /**
   * Method under test {@link ASTArcAutoInstantiateBuilder#setAIModeOff()}.
   */
  @Test
  public void shouldSetAIModeOff() {
    //Given
    ASTArcAutoInstantiateBuilder builder = ComfortableArcMill.arcAutoInstantiateBuilder();

    //When
    builder.setAIModeOff();

    //Then
    Assertions.assertFalse(builder.isAIModeOn());
    Assertions.assertTrue(builder.isAIModeOff());
  }
}
/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import comfortablearc.ComfortableArcMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ASTArcAutoConnectBuilder}.
 */
public class AutoConnectBuilderTest {

  /**
   * Method under test {@link ASTArcAutoConnectBuilder#setArcACMode(ASTArcACMode)}.
   */
  @Test
  public void modeSetterShouldThrowIllegalArgumentException() {
    //Given
    ASTArcAutoConnectBuilder builder = ComfortableArcMill.arcAutoConnectBuilder();

    //When && Then
    Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setArcACMode(null));
  }

  /**
   * Method under test {@link ASTArcAutoConnectBuilder#setArcACModeOff()}.
   */
  @Test
  public void shouldSetACModeOff() {
    //Given
    ASTArcAutoConnectBuilder builder = ComfortableArcMill.arcAutoConnectBuilder();

    //When
    builder.setArcACModeOff();

    //Then
    Assertions.assertTrue(builder.isACModeOff());
    Assertions.assertFalse(builder.isACModePort());
    Assertions.assertFalse(builder.isACModeType());
  }

  /**
   * Method under test {@link ASTArcAutoConnectBuilder#setArcACModePort()}.
   */
  @Test
  public void shouldSetACModePort() {
    //Given
    ASTArcAutoConnectBuilder builder = ComfortableArcMill.arcAutoConnectBuilder();

    //When
    builder.setArcACModePort();

    //Then
    Assertions.assertFalse(builder.isACModeOff());
    Assertions.assertTrue(builder.isACModePort());
    Assertions.assertFalse(builder.isACModeType());
  }

  /**
   * Method under test {@link ASTArcAutoConnectBuilder#setArcACModeType()}.
   */
  @Test
  public void shouldSetACModeType() {
    //Given
    ASTArcAutoConnectBuilder builder = ComfortableArcMill.arcAutoConnectBuilder();

    //When
    builder.setArcACModeType();

    //Then
    Assertions.assertFalse(builder.isACModeOff());
    Assertions.assertFalse(builder.isACModePort());
    Assertions.assertTrue(builder.isACModeType());
  }
}
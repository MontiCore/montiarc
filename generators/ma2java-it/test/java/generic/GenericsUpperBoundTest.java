/* (c) https://github.com/MontiCore/monticore */
package generic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenericsUpperBoundTest {

  @Test
  public void OneGenericWithUpperBoundTest() {
    assertEquals(OneGenericWithUpperBound.class.getTypeParameters()[0].getBounds()[0], Number.class);
  }

  @Test
  public void OneGenericWithoutUpperBoundTest() {
    assertEquals(OneGenericWithoutUpperBound.class.getTypeParameters()[0].getBounds()[0], Object.class);
  }

  @Test
  public void TwoGenericsFirstUpperBoundTest() {
    assertEquals(TwoGenericsFirstUpperBound.class.getTypeParameters()[0].getBounds()[0], Number.class);
    assertEquals(TwoGenericsFirstUpperBound.class.getTypeParameters()[1].getBounds()[0], Object.class);
  }

  @Test
  public void twoGenericsSecondUpperBoundTest() {
    assertEquals(TwoGenericsSecondUpperBound.class.getTypeParameters()[0].getBounds()[0], Object.class);
    assertEquals(TwoGenericsSecondUpperBound.class.getTypeParameters()[1].getBounds()[0], String.class);
  }

  @Test
  public void TwoGenericsNoUpperBoundTest() {
    assertEquals(TwoGenericsWithoutUpperBound.class.getTypeParameters()[0].getBounds()[0], Object.class);
    assertEquals(TwoGenericsWithoutUpperBound.class.getTypeParameters()[1].getBounds()[0], Object.class);
  }

  @Test
  public void TwoGenericsBothUpperBoundTest() {
    assertEquals(TwoGenericsBothUpperBound.class.getTypeParameters()[0].getBounds()[0], Number.class);
    assertEquals(TwoGenericsBothUpperBound.class.getTypeParameters()[1].getBounds()[0], String.class);
  }
}

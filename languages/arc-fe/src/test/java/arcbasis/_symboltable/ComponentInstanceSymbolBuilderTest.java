/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceSymbolBuilder}.
 */
public class ComponentInstanceSymbolBuilderTest extends AbstractTest {

  @Test
  public void shouldBeValid() {
    ComponentInstanceSymbolBuilder builder = new ComponentInstanceSymbolBuilder();
    builder.setName("a").setType(mock(ComponentTypeSymbolSurrogate.class));
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentInstanceSymbolBuilder builderWithName = new ComponentInstanceSymbolBuilder();
    ComponentInstanceSymbolBuilder builderWithType = new ComponentInstanceSymbolBuilder();
    builderWithName.setName("b");
    builderWithType.setType(mock(ComponentTypeSymbolSurrogate.class));
    Assertions.assertFalse(builderWithName.isValid());
    Assertions.assertFalse(builderWithType.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    ComponentTypeSymbolSurrogate type =
      ArcBasisMill.componentTypeSymbolSurrogateBuilder().setName("Comp1").build();
    ComponentInstanceSymbol symbol =
      ArcBasisMill.componentInstanceSymbolBuilder().setName("c").setType(type).build();
    Assertions.assertEquals(symbol.type, type);
  }
}
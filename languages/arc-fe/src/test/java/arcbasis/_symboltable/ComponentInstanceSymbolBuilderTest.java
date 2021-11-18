/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.check.CompSymTypeExpression;
import arcbasis.check.SymTypeOfComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceSymbolBuilder}.
 */
public class ComponentInstanceSymbolBuilderTest extends AbstractTest {

  @Test
  public void shouldBeValid() {
    ComponentInstanceSymbolBuilder builder = new ComponentInstanceSymbolBuilder();
    builder.setName("a").setType(mock(CompSymTypeExpression.class));
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentInstanceSymbolBuilder builderWithType = new ComponentInstanceSymbolBuilder();
    builderWithType.setType(mock(CompSymTypeExpression.class));
    Assertions.assertFalse(builderWithType.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    ComponentTypeSymbol type = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp1").setSpannedScope(Mockito.mock(IArcBasisScope.class)).build();
    ComponentInstanceSymbol symbol =
      ArcBasisMill.componentInstanceSymbolBuilder().setName("c").setType(new SymTypeOfComponent(type)).build();
    Assertions.assertTrue(symbol.type instanceof SymTypeOfComponent);
    Assertions.assertEquals(symbol.type.getTypeInfo(), type);
  }
}
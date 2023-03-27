/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceSymbolBuilder}.
 */
public class ComponentInstanceSymbolBuilderTest extends ArcBasisAbstractTest {

  @Test
  public void shouldBeValid() {
    ComponentInstanceSymbolBuilder builder = new ComponentInstanceSymbolBuilder();
    builder.setName("a").setType(mock(CompTypeExpression.class));
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentInstanceSymbolBuilder builderWithType = new ComponentInstanceSymbolBuilder();
    builderWithType.setType(mock(CompTypeExpression.class));
    Assertions.assertFalse(builderWithType.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    ComponentTypeSymbol type = ArcBasisMill.componentTypeSymbolBuilder()
      .setName("Comp1").setSpannedScope(Mockito.mock(IArcBasisScope.class)).build();
    ComponentInstanceSymbol symbol =
      ArcBasisMill.componentInstanceSymbolBuilder().setName("c").setType(new TypeExprOfComponent(type)).build();
    Assertions.assertTrue(symbol.type instanceof TypeExprOfComponent);
    Assertions.assertEquals(symbol.type.getTypeInfo(), type);
  }
}
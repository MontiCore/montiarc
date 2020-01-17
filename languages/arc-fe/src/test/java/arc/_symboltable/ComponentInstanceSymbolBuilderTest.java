/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc.util.ArcError;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link ComponentInstanceSymbolBuilder}.
 */
public class ComponentInstanceSymbolBuilderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldBeValid() {
    ComponentInstanceSymbolBuilder builder = new ComponentInstanceSymbolBuilder();
    builder.setName("a").setType(mock(ComponentSymbolLoader.class));
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    ComponentInstanceSymbolBuilder builderWithName = new ComponentInstanceSymbolBuilder();
    ComponentInstanceSymbolBuilder builderWithType = new ComponentInstanceSymbolBuilder();
    builderWithName.setName("b");
    builderWithType.setType(mock(ComponentSymbolLoader.class));
    Assertions.assertFalse(builderWithName.isValid());
    Assertions.assertFalse(builderWithType.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    ComponentSymbolLoader type =
      ArcSymTabMill.componentSymbolLoaderBuilder().setName("Comp1").build();
    ComponentInstanceSymbol symbol =
      ArcSymTabMill.componentInstanceSymbolBuilder().setName("c").setType(type).build();
    Assertions.assertEquals(symbol.type, type);
  }
}
/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc.util.ArcError;
import de.monticore.types.typesymbols._symboltable.*;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link PortSymbolBuilder}.
 */
public class PortSymbolBuilderTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldBeValid() {
    PortSymbolBuilder builder = new PortSymbolBuilder();
    builder.setName("in1").setType(mock(TypeSymbolLoader.class));
    Assertions.assertTrue(builder.isValid());
  }

  @Test
  public void shouldBeInvalid() {
    PortSymbolBuilder builderWithName = new PortSymbolBuilder();
    PortSymbolBuilder builderWithType = new PortSymbolBuilder();
    builderWithName.setName("out1");
    builderWithType.setType(mock(TypeSymbolLoader.class));
    Assertions.assertFalse(builderWithName.isValid());
    Assertions.assertFalse(builderWithType.isValid());
  }

  @Test
  public void shouldBuildWithExpectedType() {
    TypeSymbolLoader type = TypeSymbolsSymTabMill.typeSymbolLoaderBuilder().setName("A").build();
    PortSymbol symbol = ArcSymTabMill.portSymbolBuilder().setName("in2").setType(type).build();
    Assertions.assertEquals(symbol.getType(), type);
  }
}
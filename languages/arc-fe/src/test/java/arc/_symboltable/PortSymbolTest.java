/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc.util.ArcError;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods of {@link PortSymbol}.
 */
public class PortSymbolTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldFindComponentType() {
    ComponentSymbol compSymbol = ArcSymTabMill.componentSymbolBuilder().setName("Comp")
      .setSpannedScope(new ArcScope()).build();
    PortSymbol portSymbol = ArcSymTabMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(TypeSymbolLoader.class)).build();
    compSymbol.getSpannedScope().add(portSymbol);
    Assertions.assertTrue(portSymbol.getComponent().isPresent());
  }

  @Test
  public void shouldNotFindComponentType() {
    PortSymbol portSymbol = ArcSymTabMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(TypeSymbolLoader.class)).build();
    Assertions.assertFalse(portSymbol.getComponent().isPresent());
  }
}
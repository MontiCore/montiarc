/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.util.ArcError;
import de.monticore.types.check.SymTypeExpression;
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
    ComponentTypeSymbol compSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("Comp")
      .setSpannedScope(new ArcBasisScope()).build();
    PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(SymTypeExpression.class)).build();
    compSymbol.getSpannedScope().add(portSymbol);
    Assertions.assertTrue(portSymbol.getComponent().isPresent());
  }

  @Test
  public void shouldNotFindComponentType() {
    PortSymbol portSymbol = ArcBasisMill.portSymbolBuilder().setName("p1")
      .setIncoming(true).setType(mock(SymTypeExpression.class)).build();
    Assertions.assertFalse(portSymbol.getComponent().isPresent());
  }
}
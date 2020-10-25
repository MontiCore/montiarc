/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods {@link ComponentInstanceSymbol}.
 */
public class ComponentInstanceSymbolTest extends AbstractTest {

  @Test
  public void shouldAddArguments() {
    ComponentInstanceSymbol symbol = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("a").setType(mock(ComponentTypeSymbolSurrogate.class)).build();
    Assertions.assertEquals(symbol.getArguments().size(), 0);
    symbol.addArguments(Arrays.asList(mock(ASTExpression.class), mock(ASTExpression.class)));
    Assertions.assertEquals(symbol.getArguments().size(), 2);
  }
}
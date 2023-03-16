/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.AbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
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
      .setName("a").setType(mock(CompTypeExpression.class)).build();
    Assertions.assertEquals(symbol .getArcArguments().size(), 0);
    symbol.addArcArguments(Arrays.asList(mock(ASTArcArgument.class), mock(ASTArcArgument.class)));
    Assertions.assertEquals(symbol.getArcArguments().size(), 2);
  }
}
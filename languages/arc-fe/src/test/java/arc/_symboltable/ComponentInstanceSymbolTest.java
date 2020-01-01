/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import arc.util.ArcError;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Holds tests for the handwritten methods {@link ComponentInstanceSymbol}.
 */
public class ComponentInstanceSymbolTest extends AbstractTest {

  @Override
  protected Pattern supplyErrorCodePattern() {
    return ArcError.ERROR_CODE_PATTERN;
  }

  @Test
  public void shouldAddArguments() {
    ComponentInstanceSymbol symbol = ArcSymTabMill.componentInstanceSymbolBuilder()
      .setName("a").setType(mock(ComponentSymbolLoader.class)).build();
    Assertions.assertEquals(symbol.getArguments().size(), 0);
    symbol.addArguments(Arrays.asList(mock(ASTExpression.class), mock(ASTExpression.class)));
    Assertions.assertEquals(symbol.getArguments().size(), 2);
  }
}
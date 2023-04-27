/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisAbstractTest;
import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
import arcbasis.check.TypeExprOfComponent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods {@link ComponentInstanceSymbol}.
 */
public class ComponentInstanceSymbolTest extends ArcBasisAbstractTest {

  @Test
  public void shouldAddArguments() {
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol typeSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A").setSpannedScope(scope).build();
    ComponentInstanceSymbol instanceSymbol = ArcBasisMill.componentInstanceSymbolBuilder()
      .setName("a").setType(new TypeExprOfComponent(typeSymbol)).build();
    Assertions.assertEquals(0, instanceSymbol.getType().getArcArguments().size());
    instanceSymbol.getType().addArcArguments(Arrays.asList(mock(ASTArcArgument.class), mock(ASTArcArgument.class)));
    Assertions.assertEquals(2, instanceSymbol.getType().getArcArguments().size());
  }
}
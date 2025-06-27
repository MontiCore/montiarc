/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis.ArcBasisTestBase;
import arcbasis._ast.ASTArcArgument;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindOfComponentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

/**
 * Holds tests for the handwritten methods {@link SubcomponentSymbol}.
 */
public class SubcomponentSymbolTest extends ArcBasisTestBase {

  @Test
  public void shouldAddArguments() {
    IArcBasisScope scope = ArcBasisMill.scope();
    ComponentTypeSymbol typeSymbol = ArcBasisMill.componentTypeSymbolBuilder().setName("A").setSpannedScope(scope).build();
    SubcomponentSymbol instanceSymbol = ArcBasisMill.subcomponentSymbolBuilder()
      .setName("a").setType(new CompKindOfComponentType(typeSymbol)).build();
    Assertions.assertEquals(0, instanceSymbol.getType().getArguments().size());
    instanceSymbol.getType().addArgument(Arrays.asList(mock(ASTArcArgument.class), mock(ASTArcArgument.class)));
    Assertions.assertEquals(2, instanceSymbol.getType().getArguments().size());
  }
}

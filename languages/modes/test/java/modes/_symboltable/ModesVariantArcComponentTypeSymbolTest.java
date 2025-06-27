/* (c) https://github.com/MontiCore/monticore */
package modes._symboltable;


import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.CompKindExpression;
import modes.ModesMill;
import modes.ModesTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

public class ModesVariantArcComponentTypeSymbolTest extends ModesTestBase {

  @Test
  public void shouldAddComponentInstanceSymbols() {
    // Given
    IModesScope modeScope = ModesMill.scope();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setEnclosingScope(ModesMill.scope()).setSpannedScope(ModesMill.scope()).build(), ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(modeScope).build());
    SubcomponentSymbol instanceSymbol = ModesMill.subcomponentSymbolBuilder().setName("c1").setType(Mockito.mock(CompKindExpression.class)).build();
    modeScope.add(instanceSymbol);

    // When
    List<SubcomponentSymbol> returnedInstances = variant.getSubcomponents();

    // Then
    Assertions.assertIterableEquals(Collections.singletonList(instanceSymbol), returnedInstances);
  }
}

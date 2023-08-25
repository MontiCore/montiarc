/* (c) https://github.com/MontiCore/monticore */
package modes._symboltable;


import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis.check.CompTypeExpression;
import modes.ModesAbstractTest;
import modes.ModesMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

public class ModesVariantComponentTypeSymbolTest extends ModesAbstractTest {

  @Test
  public void shouldAddComponentInstanceSymbols() {
    // Given
    IModesScope modeScope = ModesMill.scope();
    ModesVariantComponentTypeSymbol variant = new ModesVariantComponentTypeSymbol(ModesMill.componentTypeSymbolBuilder().setName("C").setSpannedScope(ModesMill.scope()).build(), ModesMill.arcModeSymbolBuilder().setName("m1").setSpannedScope(modeScope).build());
    ComponentInstanceSymbol instanceSymbol = ModesMill.componentInstanceSymbolBuilder().setName("c1").setType(Mockito.mock(CompTypeExpression.class)).build();
    modeScope.add(instanceSymbol);

    // When
    List<ComponentInstanceSymbol> returnedInstances = variant.getSubComponents();

    // Then
    Assertions.assertIterableEquals(Collections.singletonList(instanceSymbol), returnedInstances);
  }
}

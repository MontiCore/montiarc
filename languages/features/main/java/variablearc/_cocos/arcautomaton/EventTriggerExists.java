/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.arcautomaton;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.scevents._symboltable.SCEventDefSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Override the default behavior of the EventTriggerExists coco to filter symbols based on the variant
 */
public class EventTriggerExists extends arcautomaton._cocos.EventTriggerExists {

  @Override
  protected Predicate<SCEventDefSymbol> getSymbolPredicate() {
    Optional<ComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return v -> true;
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}

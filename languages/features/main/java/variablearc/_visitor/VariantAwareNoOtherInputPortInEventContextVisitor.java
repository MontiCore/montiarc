/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcautomaton._visitor.NoOtherInputPortInEventContextVisitor;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;
import java.util.function.Predicate;

public class VariantAwareNoOtherInputPortInEventContextVisitor extends NoOtherInputPortInEventContextVisitor {
  /**
   * @param event   the name of the event who's respective port may be referenced
   * @param context a human-readable string that describes the context where
   *                the value of other input ports are not available
   */
  public VariantAwareNoOtherInputPortInEventContextVisitor(String event, String context) {
    super(event, context);
  }

  @Override
  protected Predicate<VariableSymbol> getVariablePredicate() {
    Optional<ComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return v -> true;
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}

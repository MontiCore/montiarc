/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._visitor.NoPortInStaticContextVisitor;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;
import java.util.function.Predicate;

public class VariantAwareNoPortInStaticContextVisitor extends NoPortInStaticContextVisitor {

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

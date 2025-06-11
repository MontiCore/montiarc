/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._visitor.NoPortInStaticContextVisitor;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;
import java.util.function.Predicate;

public class VariantAwareNoPortInStaticContextVisitor extends NoPortInStaticContextVisitor {

  @Override
  protected Predicate<VariableSymbol> getVariablePredicate() {
    Optional<ArcComponentTypeSymbol> variant = VariableArcTypeCheck.getCurrentVariant();
    if (variant.isEmpty() || !(variant.get() instanceof VariableArcVariantComponentTypeSymbol)) {
      return v -> true;
    } else {
      return ((VariableArcVariantComponentTypeSymbol) variant.get())::containsSymbol;
    }
  }
}

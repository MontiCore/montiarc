/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._visitor.NoNonSyncInputPortInContextVisitor;
import arcbasis._symboltable.ArcComponentTypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.VariableArcVariantComponentTypeSymbol;
import variablearc.check.VariableArcTypeCheck;

import java.util.Optional;
import java.util.function.Predicate;

public class VariantAwareNoNonSyncInputPortInContextVisitor extends NoNonSyncInputPortInContextVisitor {

  /**
   * @param context a human-readable string that describes the context where
   *                the value of the input port is not available
   */
  public VariantAwareNoNonSyncInputPortInContextVisitor(@NotNull String context) {
    super(context);
  }

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

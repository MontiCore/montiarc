/* (c) https://github.com/MontiCore/monticore */
package modes.variability;

import arcbasis._symboltable.ArcComponentTypeSymbol;
import com.google.common.base.Preconditions;
import modes._ast.ASTModeAutomaton;
import modes._symboltable.IModesScope;
import modes._symboltable.ModesVariantComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.variability.IVariantCalculator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Calculate variants for a component using modes
 */
public class ModesVariantCalculator implements IVariantCalculator {

  protected final ArcComponentTypeSymbol componentTypeSymbol;

  public ModesVariantCalculator(@NotNull ArcComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);
    this.componentTypeSymbol = componentTypeSymbol;
  }

  @Override
  public List<? extends ArcComponentTypeSymbol> calculateVariants() {
    Optional<IModesScope> scope = componentTypeSymbol.getAstNode().getBody().streamArcElementsOfType(ASTModeAutomaton.class).findFirst().map(ASTModeAutomaton::getSpannedScope);
    if (scope.isEmpty()) return Collections.singletonList(componentTypeSymbol);
    return scope.get().getLocalArcModeSymbols().stream().map((mode) -> new ModesVariantComponentTypeSymbol(componentTypeSymbol, mode)).collect(Collectors.toList());
  }
}

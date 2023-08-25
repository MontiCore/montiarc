/* (c) https://github.com/MontiCore/monticore */
package modes.variability;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.variability.IVariantCalculator;
import variablearc.variability.VariableArcVariantCalculator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates variants with respect to VariableArc Variability and Modes
 */
public class VariableArcModesVariantCalculator implements IVariantCalculator {

  protected final IVariableArcComponentTypeSymbol componentTypeSymbol;

  public VariableArcModesVariantCalculator(@NotNull IVariableArcComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);
    this.componentTypeSymbol = componentTypeSymbol;
  }

  @Override
  public List<? extends ComponentTypeSymbol> calculateVariants() {
    return new VariableArcVariantCalculator(componentTypeSymbol).calculateVariants().stream().flatMap(variant ->
      new ModesVariantCalculator(variant).calculateVariants().stream()
    ).collect(Collectors.toList());
  }
}

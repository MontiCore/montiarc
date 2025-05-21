/* (c) https://github.com/MontiCore/monticore */
package variablearc.variability;

import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;

import java.util.List;

/**
 * Interface for implementing variant calculators
 */
public interface IVariantCalculator {

  List<? extends ComponentTypeSymbol> calculateVariants();
}

/* (c) https://github.com/MontiCore/monticore */
package variablearc.variability;

import arcbasis._symboltable.ComponentTypeSymbol;

import java.util.List;

public interface IVariantCalculator {

  List<? extends ComponentTypeSymbol> calculateVariants();
}

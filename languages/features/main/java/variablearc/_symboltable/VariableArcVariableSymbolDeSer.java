/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.VariableSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

/**
 * Serializes variables except those that adapt ports, subcomponents, or
 * features.
 */
public class VariableArcVariableSymbolDeSer extends VariableSymbolDeSer {

  @Override
  protected boolean isAdapter(VariableSymbol variable) {
    return super.isAdapter(variable) || variable instanceof ArcFeature2VariableAdapter;
  }
}

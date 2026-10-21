/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.Subcomponent2VariableAdapter;

public class VariableSymbolDeSer extends de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer {

  @Override
  public String serialize(VariableSymbol toSerialize, BasicSymbolsSymbols2Json s2j) {
    if (!this.isAdapter(toSerialize)) {
      return super.serialize(toSerialize, s2j);
    }
    return "";
  }

  /**
   * @return whether the variable adapts another symbol, which is serialized
   * itself
   */
  protected boolean isAdapter(VariableSymbol variable) {
    return variable instanceof Port2VariableAdapter || variable instanceof Subcomponent2VariableAdapter;
  }
}

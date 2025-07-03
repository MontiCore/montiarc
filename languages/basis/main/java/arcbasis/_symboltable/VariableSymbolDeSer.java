/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.BasicSymbolsSymbols2Json;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.Subcomponent2VariableAdapter;

public class VariableSymbolDeSer extends de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer {

  @Override
  public String serialize(VariableSymbol toSerialize, BasicSymbolsSymbols2Json s2j) {
    if (!(toSerialize instanceof Port2VariableAdapter || toSerialize instanceof Subcomponent2VariableAdapter)) {
      return super.serialize(toSerialize, s2j);
    }
    return "";
  }
}

/* (c) https://github.com/MontiCore/monticore */
package openmodeautomata.addition;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;

/**
 * the default deSer seems to discard package names, so this one exists to satisfy the requirements of situations that demand different behavior
 */
public class OOTypeSymbolDeSerWithFullNames extends OOTypeSymbolDeSer {

  @Override
  public String serialize(de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol toSerialize, de.monticore.symbols.oosymbols._symboltable.OOSymbolsSymbols2Json s2j) {
    String simpleName = toSerialize.getName();
    toSerialize.setName(toSerialize.getFullName());
    String series = super.serialize(toSerialize, s2j);
    toSerialize.setName(simpleName);
    return series;
  }
}
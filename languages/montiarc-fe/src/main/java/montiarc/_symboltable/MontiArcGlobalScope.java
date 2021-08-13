/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;

public class MontiArcGlobalScope extends MontiArcGlobalScopeTOP {

  @Override
  public MontiArcGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    this.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new FieldSymbolDeSer());
  }
}
/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.microsoft.z3.Context;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import variablearc._symboltable.VariableArcVariableSymbolDeSer;
import variablearc.smt.VariableArcDeriveSMTSort;

import java.util.Optional;

public class MontiArcGlobalScope extends MontiArcGlobalScopeTOP {

  protected Context presenceContext;

  @Override
  public MontiArcGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    this.init(false);
  }

  @Override
  public void init(boolean novar) {
    super.init();
    this.putSymbolDeSer("de.monticore.symbols.basicsymbols._symboltable.VariableSymbol", new VariableArcVariableSymbolDeSer());
    this.putSymbolDeSer("de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol", new MontiArcComponentTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol", new MontiArcSubcomponentSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new OOTypeSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol", new MethodSymbolDeSer());
    this.putSymbolDeSer("de.monticore.cdassociation._symboltable.CDRoleSymbol", new CDRole2FieldSymbolDeSer());
    this.closePresenceContext();
    if (!novar) {
      this.presenceContext = new Context();
    }
  }

  @Override
  public void clear() {
    this.closePresenceContext();
    super.clear();
  }

  @Override
  public Optional<Context> getPresenceContext() {
    return Optional.ofNullable(this.presenceContext);
  }

  @Override
  public void closePresenceContext() {
    if (this.presenceContext != null) {
      VariableArcDeriveSMTSort.release(this.presenceContext);
      this.presenceContext.close();
      this.presenceContext = null;
    }
  }
}

/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

public class ArcBasisGlobalScope extends ArcBasisGlobalScopeTOP {

  @Override
  public ArcBasisGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    this.putSymbolDeSer("de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol", new ArcBasisSubcomponentSymbolDeSer());
  }
}

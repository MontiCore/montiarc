/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

public class ComponentTypeSymbolSurrogateBuilder extends ComponentTypeSymbolSurrogateBuilderTOP {

  @Override
  public ComponentTypeSymbolSurrogate build() {
    ComponentTypeSymbolSurrogate symbolReference = new ComponentTypeSymbolSurrogate(name);
    symbolReference.setEnclosingScope(enclosingScope);
    this.parent.ifPresent(symbolReference::setParent);
    return symbolReference;
  }
}

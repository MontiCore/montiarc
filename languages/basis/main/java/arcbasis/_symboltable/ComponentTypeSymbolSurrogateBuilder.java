/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

public class ComponentTypeSymbolSurrogateBuilder extends ComponentTypeSymbolSurrogateBuilderTOP {

  @Override
  public ComponentTypeSymbolSurrogate build() {
    ComponentTypeSymbolSurrogate symbolReference = new ComponentTypeSymbolSurrogate(name);
    symbolReference.setEnclosingScope(enclosingScope);
    if (!this.isEmptySuperComponents()) {
      symbolReference.setSuperComponentsList(this.getSuperComponentsList());
    }
    return symbolReference;
  }
}

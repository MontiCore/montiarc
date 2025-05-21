/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

public class ArcComponentTypeSymbolSurrogateBuilder extends ArcComponentTypeSymbolSurrogateBuilderTOP {

  @Override
  public ArcComponentTypeSymbolSurrogate build() {
    ArcComponentTypeSymbolSurrogate symbolReference = new ArcComponentTypeSymbolSurrogate(name);
    symbolReference.setEnclosingScope(enclosingScope);
    if (!this.isEmptySuperComponents()) {
      symbolReference.setSuperComponentsList(this.getSuperComponentsList());
    }
    return symbolReference;
  }
}

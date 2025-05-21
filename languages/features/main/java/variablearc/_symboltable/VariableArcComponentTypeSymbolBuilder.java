/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ArcComponentTypeSymbol;
import arcbasis._symboltable.ArcComponentTypeSymbolBuilder;
import com.google.common.base.Preconditions;

public class VariableArcComponentTypeSymbolBuilder extends ArcComponentTypeSymbolBuilder {

  @Override
  public ArcComponentTypeSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getSpannedScope() != null);
    }
    ArcComponentTypeSymbol symbol = new VariableArcComponentTypeSymbol(name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    if (this.astNode.isPresent()) {
      symbol.setAstNode(this.astNode.get());
    } else {
      symbol.setAstNodeAbsent();
    }
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setSpannedScope(this.spannedScope);
    if (this.getParameterList() != null) {
      this.getParameterList().forEach(symbol.getSpannedScope()::add);
      symbol.addParameters(this.getParameterList());
    }
    if (this.getTypeParameters() != null) {
      this.getTypeParameters().forEach(symbol.getSpannedScope()::add);
    }
    symbol.setOuterComponent(this.getOuterComponent());
    symbol.setSuperComponentsList(this.superComponents);
    return symbol;
  }
}

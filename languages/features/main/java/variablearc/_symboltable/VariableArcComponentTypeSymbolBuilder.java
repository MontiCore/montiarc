/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import com.google.common.base.Preconditions;

public class VariableArcComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilder {

  @Override
  public ComponentTypeSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getSpannedScope() != null);
    }
    ComponentTypeSymbol symbol = new VariableArcComponentTypeSymbol(name);
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
    if (this.getParameters() != null) {
      this.getParameters().forEach(symbol.getSpannedScope()::add);
      symbol.addParameters(this.getParameters());
    }
    if (this.getTypeParameters() != null) {
      this.getTypeParameters().forEach(symbol.getSpannedScope()::add);
    }
    symbol.setOuterComponent(this.getOuterComponent());
    symbol.setSuperComponentsList(this.superComponents);
    return symbol;
  }
}

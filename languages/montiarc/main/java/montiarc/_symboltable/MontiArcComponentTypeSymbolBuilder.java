/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import com.google.common.base.Preconditions;

public class MontiArcComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilder {

  @Override
  public ComponentTypeSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getSpannedScope() != null);
    }
    ComponentTypeSymbol symbol = new MontiArcComponentTypeSymbol(name);
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
    if (this.getParentConfiguration() != null) {
      symbol.setParentConfigurationExpressions(this.getParentConfiguration());
    }
    symbol.setOuterComponent(this.getOuterComponent());
    if (this.parent.isPresent()) {
      symbol.setParent(this.parent.get());
    } else {
      symbol.setParentAbsent();
    }
    return symbol;
  }
}

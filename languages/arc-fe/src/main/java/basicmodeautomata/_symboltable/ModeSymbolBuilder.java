/* (c) https://github.com/MontiCore/monticore */
package basicmodeautomata._symboltable;

import arcbasis._ast.ASTComponentBody;
import basicmodeautomata.BasicModeAutomataMill;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._symboltable.ISCBasisScope;
import de.monticore.scbasis._symboltable.SCStateSymbolBuilder;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;

public class ModeSymbolBuilder extends SCStateSymbolBuilder {

  protected List<ASTComponentBody> body;

  /**
   *
   * @param body a full description of this modes body, not including its static parts
   * @return this
   */
  public ModeSymbolBuilder setDynamicBodyList(List<ASTComponentBody> body){
    this.body = body;
    return (ModeSymbolBuilder) this.realBuilder;
  }

  @Override
  public ModeSymbolBuilder setEnclosingScope(ISCBasisScope enclosingScope) {
    return (ModeSymbolBuilder) super.setEnclosingScope(enclosingScope);
  }

  @Override
  public ModeSymbolBuilder setName(String name) {
    return (ModeSymbolBuilder) super.setName(name);
  }

  @Override
  public ModeSymbolBuilder setFullName(String fullName) {
    return (ModeSymbolBuilder) super.setFullName(fullName);
  }

  @Override
  public ModeSymbolBuilder setPackageName(String packageName) {
    return (ModeSymbolBuilder) super.setPackageName(packageName);
  }

  @Override
  public ModeSymbolBuilder setAccessModifier(AccessModifier accessModifier) {
    return (ModeSymbolBuilder) super.setAccessModifier(accessModifier);
  }

  @Override
  public ModeSymbolBuilder setAstNode(ASTSCState astNode) {
    return (ModeSymbolBuilder) super.setAstNode(astNode);
  }

  @Override
  public ModeSymbolBuilder setAstNodeAbsent() {
    return (ModeSymbolBuilder) super.setAstNodeAbsent();
  }

  @Override
  public boolean isValid() {
    return
        body != null &&
        super.isValid();
  }

  @Override
  public ModeSymbol build() {
    ModeSymbol symbol = new ModeSymbol(getName());
    symbol.setName(this.name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setBody(BasicModeAutomataMill.getModeTool().mergeBodies(this.body));

    return symbol;
  }
}

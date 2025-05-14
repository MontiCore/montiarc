/* (c) https://github.com/MontiCore/monticore */
package variablearc._ast;

import arcbasis._ast.ASTPortAccess;

public class ASTVariantPortAccess extends ASTPortAccess {

  public ASTVariantPortAccess(ASTPortAccess original) {
    this.setPort(original.getPort());
    if (original.isPresentComponent()) {
      this.setComponent(original.getComponent());
    } else {
      this.setComponentAbsent();
    }
    this.setEnclosingScope(original.getEnclosingScope());
    this.set_SourcePositionStart(original.get_SourcePositionStart());
    this.set_SourcePositionEnd(original.get_SourcePositionEnd());
  }

  @Override
  protected void updateComponentSymbol() {
    // noop
  }

  @Override
  protected void updatePortSymbol() {
    // noop
  }
}

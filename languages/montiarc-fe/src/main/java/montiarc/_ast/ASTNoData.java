package montiarc._ast;

import de.monticore.java.javadsl._visitor.JavaDSLVisitor;
import de.monticore.literals.literals._ast.ASTNullLiteral;
import de.monticore.literals.literals._visitor.LiteralsVisitor;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import montiarc._visitor.MontiArcVisitor;

public class ASTNoData extends ASTNoDataTOP {
  
  @Override
  public void accept(MCExpressionsVisitor visitor) {
    accpt(visitor);
  }
  
  @Override
  public void accept(JavaDSLVisitor visitor) {
    accpt(visitor);
  }
  
  @Override
  public void accept(MontiArcVisitor visitor) {
    accpt(visitor);
  }
  
  @Override
  public void accept(LiteralsVisitor visitor) {
    accpt(visitor);
  }
  
  private void accpt(LiteralsVisitor visitor) {
    if (this instanceof ASTNoData) {
      ASTNullLiteral node = MontiArcMill.nullLiteralBuilder().build();
      node.setEnclosingScopeOpt(this.getEnclosingScopeOpt());
      visitor.handle(node);
    }
  }
  
}

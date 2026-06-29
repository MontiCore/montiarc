/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions.codegen.javagen.CommonExpressionsJavaGenVisitor;
import de.monticore.types3.TypeCheck3;

public class MACommonExpressionsJavaGenVisitor extends CommonExpressionsJavaGenVisitor {

  public MACommonExpressionsJavaGenVisitor(JavaGenVisitorState state) {
    super(state);
  }

  @Override
  public void traverse(ASTCallExpression node) {
    TypeCheck3.typeOf(node);
    super.traverse(node);
  }
}

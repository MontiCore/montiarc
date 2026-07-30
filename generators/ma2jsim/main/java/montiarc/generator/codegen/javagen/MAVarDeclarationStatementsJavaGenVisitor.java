/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements.codegen.javagen.MCVarDeclarationStatementsJavaGenVisitor;
import montiarc.generator.util.Helper;

public class MAVarDeclarationStatementsJavaGenVisitor extends MCVarDeclarationStatementsJavaGenVisitor {

  Helper helper = new Helper();

  public MAVarDeclarationStatementsJavaGenVisitor(JavaGenVisitorState state) {
    super(state);
  }

  @Override
  public void traverse(ASTVariableDeclarator node) {
    super.traverse(node);

    if (!node.isPresentVariableInit() && node.getDeclarator().getSymbol().getType().isPrimitive()) {
      getPrinter().print(" = ");
      getPrinter().print(helper.getTypeHelper().getNullLikeValue(node.getDeclarator().getSymbol().getType().asPrimitive()));
    }
  }
}

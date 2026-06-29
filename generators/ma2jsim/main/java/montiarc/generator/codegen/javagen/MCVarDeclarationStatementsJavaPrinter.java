/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen.javagen;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import montiarc.generator.util.Helper;

public class MCVarDeclarationStatementsJavaPrinter extends MCVarDeclarationStatementsPrettyPrinter {

  Helper helper = new Helper();

  public MCVarDeclarationStatementsJavaPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTVariableDeclarator node) {
    super.handle(node);

    if (!node.isPresentVariableInit() && node.getDeclarator().getSymbol().getType().isPrimitive()) {
      getPrinter().print(" = ");
      getPrinter().print(helper.getTypeHelper().getNullLikeValue(node.getDeclarator().getSymbol().getType().asPrimitive()));
    }
  }
}

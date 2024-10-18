/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatement;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsPrettyPrinter;
import montiarc.MontiArcMill;

public class MCCommonStatementsJavaPrinter extends MCCommonStatementsPrettyPrinter {

  public MCCommonStatementsJavaPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  public void handle(ASTIfStatement node) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, this.getPrinter());
    }

    this.getPrinter().print("if ");
    this.getPrinter().stripTrailing();
    this.getPrinter().print("(");
    node.getCondition().accept(this.getTraverser());
    this.getPrinter().stripTrailing();
    this.getPrinter().print(")");

    // Workaround for single statements that are expended into multiple (like port message sending: assignment + sending)
    if (MontiArcMill.typeDispatcher().isMCCommonStatementsASTMCJavaBlock(node.getThenStatement())) {
      node.getThenStatement().accept(this.getTraverser());
    } else {
      this.getPrinter().print("{");
      node.getThenStatement().accept(this.getTraverser());
      this.getPrinter().print("}");
    }

    if (node.isPresentElseStatement()) {
      this.getPrinter().print("else ");
      node.getElseStatement().accept(this.getTraverser());
    }

    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, this.getPrinter());
    }

  }
}

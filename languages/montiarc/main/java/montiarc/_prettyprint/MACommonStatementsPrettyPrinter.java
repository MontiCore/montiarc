/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._ast.ASTForStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mccommonstatements._prettyprint.MCCommonStatementsPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;

/**
 * Improved MCCommonStatements Pretty Printer that fixes formatting of
 * Java blocks, for-loops, and enhanced for-loops.
 */
public class MACommonStatementsPrettyPrinter extends MCCommonStatementsPrettyPrinter {

  public MACommonStatementsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTMCJavaBlock node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().stripTrailing();
    getPrinter().println(" {");
    getPrinter().indent();

    node.getMCBlockStatementList().forEach(n -> n.accept(getTraverser()));

    getPrinter().unindent();
    getPrinter().println("}");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTForStatement node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().print("for (");
    node.getForControl().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(")");
    node.getMCStatement().accept(getTraverser());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTCommonForControl node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Iterator<ASTExpression> iter = node.getExpressionList().iterator();

    if (node.isPresentForInit()) {
      node.getForInit().accept(getTraverser());
    }
    getPrinter().stripTrailing();
    getPrinter().print("; ");

    if (node.isPresentCondition()) {
      node.getCondition().accept(getTraverser());
    }
    getPrinter().stripTrailing();
    getPrinter().print("; ");

    if (iter.hasNext()) {
      iter.next().accept(getTraverser());
      while (iter.hasNext()) {
        getPrinter().stripTrailing();
        getPrinter().print(", ");
        iter.next().accept(getTraverser());
      }
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  @Override
  public void handle(ASTEnhancedForControl node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getFormalParameter().accept(getTraverser());
    getPrinter().stripTrailing();
    getPrinter().print(" : ");
    node.getExpression().accept(getTraverser());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}

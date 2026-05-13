/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;

public class MAVarDeclarationStatementsPrettyPrinter extends MCVarDeclarationStatementsPrettyPrinter {

  public MAVarDeclarationStatementsPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTLocalVariableDeclaration node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    Iterator<ASTVariableDeclarator> iter = node.getVariableDeclaratorList().iterator();
    if (iter.hasNext()) {
      node.getMCModifierList().forEach(n -> n.accept(getTraverser()));
      node.getMCType().accept(getTraverser());

      // Ensure space between type and variable name
      getPrinter().stripTrailing();
      getPrinter().print(" ");

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
  public void handle(ASTVariableDeclarator node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    node.getDeclarator().accept(getTraverser());

    if (node.isPresentVariableInit()) {
      getPrinter().stripTrailing();
      getPrinter().print(" = ");
      node.getVariableInit().accept(getTraverser());
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}

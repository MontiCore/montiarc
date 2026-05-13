/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.scbasis._prettyprint.SCBasisPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MASCBasisPrettyPrinter extends SCBasisPrettyPrinter {

  public MASCBasisPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTSCTransition node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    if (node.isPresentStereotype()) {
      node.getStereotype().accept(getTraverser());
    }

    node.getSource().accept(getTraverser());

    getPrinter().stripTrailing();
    getPrinter().print(" -> ");

    node.getTarget().accept(getTraverser());
    node.getSCTBody().accept(getTraverser());

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}

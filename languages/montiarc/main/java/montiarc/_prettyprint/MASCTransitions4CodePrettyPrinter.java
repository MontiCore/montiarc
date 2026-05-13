/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._prettyprint.SCTransitions4CodePrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MASCTransitions4CodePrettyPrinter extends SCTransitions4CodePrettyPrinter {

  public MASCTransitions4CodePrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTTransitionBody node) {
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }


    // Guard: [precondition]
    if (node.isPresentPre()) {
      getPrinter().stripTrailing();
      getPrinter().print(" [");
      node.getPre().accept(getTraverser());
      getPrinter().stripTrailing();
      getPrinter().print("]");
    }

    // Event
    if (node.isPresentSCEvent()) {
      node.getSCEvent().accept(getTraverser());
    }

    // Action or semicolon
    if (node.isPresentTransitionAction()) {
      getPrinter().stripTrailing();
      getPrinter().print(" /");
      node.getTransitionAction().accept(getTraverser());
    } else {
      getPrinter().stripTrailing();
      getPrinter().println(";");
    }

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}

/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._prettyprint;

import arcautomaton._ast.ASTArcStatechart;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcAutomatonPrettyPrinter extends ArcAutomatonPrettyPrinterTOP {

  public ArcAutomatonPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(@NotNull ASTArcStatechart node) {
    Preconditions.checkNotNull(node);
    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
    }

    getPrinter().println("automaton {");
    getPrinter().indent();

    // Group elements: no blank line between consecutive states
    // or consecutive transitions, blank line between other pairs
    ASTNode prevElement = null;
    for (ASTNode element : node.getSCStatechartElementList()) {
      if (prevElement != null) {
        boolean bothStates = (prevElement instanceof ASTSCState)
            && (element instanceof ASTSCState);
        boolean bothTransitions = (prevElement instanceof ASTSCTransition)
            && (element instanceof ASTSCTransition);
        if (!bothStates && !bothTransitions) {
          getPrinter().println();
        }
      }
      element.accept(getTraverser());
      prevElement = element;
    }

    getPrinter().unindent();
    getPrinter().println("}");

    if (this.isPrintComments()) {
      de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}

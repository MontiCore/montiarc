/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import arcautomaton._ast.ASTArcStatechart;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.scbasis._ast.ASTSCStatechartElement;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcAutomatonPrettyPrinter implements ArcAutomatonHandler, ArcAutomatonVisitor2 {

  protected IndentPrinter printer;

  public IndentPrinter getPrinter() {
    return printer;
  }

  ArcAutomatonTraverser traverser;

  @Override
  public ArcAutomatonTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ArcAutomatonTraverser traverser){
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public ArcAutomatonPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
  }

  @Override
  public void handle(@NotNull ASTArcStatechart node){
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getSCStatechartElementList());
    Preconditions.checkArgument(!node.getSCStatechartElementList().contains(null));
    getPrinter().println("automaton {");
    getPrinter().indent();
    for (ASTSCStatechartElement element : node.getSCStatechartElementList()){
      element.accept(this.getTraverser());
    }
    getPrinter().unindent();
    getPrinter().println("}");
  }
}

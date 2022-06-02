/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._ast.ASTArcElement;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.*;

import java.util.Iterator;
import java.util.List;

public class VariableArcPrettyPrinter implements VariableArcHandler, VariableArcVisitor2 {

  protected IndentPrinter printer;
  private VariableArcTraverser traverser;

  public VariableArcPrettyPrinter() {
    this(new IndentPrinter());
  }

  public VariableArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
  }

  @Override
  public VariableArcTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(@NotNull VariableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTVariableArcNode> void acceptSeparatedList(@NotNull List<T> list) {
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTArcFeatureDeclaration node) {
    this.getPrinter().print("feature ");
    acceptSeparatedList(node.getArcFeatureList());
    this.getPrinter().print(";");
  }

  @Override
  public void handle(ASTArcFeature node) {
    this.getPrinter().print(node.getName());
  }

  @Override
  public void handle(@NotNull ASTArcIfStatement node) {
    this.getPrinter().print("if (");
    node.getCondition().accept(this.getTraverser());
    this.getPrinter().print(")");
    node.getThenStatement().accept(this.getTraverser());
    if (node.isPresentElseStatement()) {
      this.getPrinter().print(" else ");
      node.getElseStatement().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTArcBlock node) {
    this.getPrinter().println(" { ");
    this.getPrinter().indent();
    for (ASTArcElement element : node.getArcElementList()) {
      element.accept(this.getTraverser());
    }
    this.getPrinter().unindent();
    this.getPrinter().println("}");
  }

  @Override
  public void handle(@NotNull ASTArcConstraintDeclaration node) {
    this.getPrinter().print("constraint (");
    node.getExpression().accept(this.getTraverser());
    this.getPrinter().print(");");
  }
}

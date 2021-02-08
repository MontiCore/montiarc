/* (c) https://github.com/MontiCore/monticore */
package genericarc._visitor;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericArcNode;
import genericarc._ast.ASTGenericComponentHead;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class GenericArcPrettyPrinter implements GenericArcHandler {

  private GenericArcTraverser traverser;
  protected IndentPrinter printer;

  public GenericArcPrettyPrinter() {
    this(new IndentPrinter());
  }

  public GenericArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public GenericArcTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(@NotNull GenericArcTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeparatedList(@NotNull List<T> list){
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

  public <T extends ASTGenericArcNode> void acceptGenericSeparatedList(@NotNull List<T> list){
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
  public void handle(ASTGenericComponentHead node) {
    this.getPrinter().print("<");
    acceptGenericSeparatedList(node.getArcTypeParameterList());
    this.getPrinter().print("> ");
    if(!node.isEmptyArcTypeParameters()) {
      this.getPrinter().print("(");
      acceptSeparatedList(node.getArcParameterList());
      this.getPrinter().print(") ");
    }
    if(node.isPresentParent()){
      this.getPrinter().print("extends ");
      node.getParent().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(ASTArcTypeParameter node) {
    this.getPrinter().print(node.getName() + " ");
    if(!node.isEmptyUpperBound()){
      this.getPrinter().print("extends ");
      Iterator<ASTMCType> iterator = node.getUpperBoundList().iterator();
      iterator.next().accept(this.getTraverser());
      while (iterator.hasNext()) {
        this.getPrinter().print(" & ");
        iterator.next().accept(this.getTraverser());
      }
    }
  }


}
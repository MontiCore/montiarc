/* (c) https://github.com/MontiCore/monticore */
package genericarc._visitor;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericArcNode;
import genericarc._ast.ASTGenericComponentHead;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class GenericArcPrettyPrinter implements GenericArcVisitor {

  private GenericArcVisitor realThis = this;
  protected IndentPrinter printer;

  public GenericArcPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    MCBasicTypesPrettyPrinter typePrinter = new MCBasicTypesPrettyPrinter(printer);
    this.printer = printer;
  }

  public GenericArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public GenericArcVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull GenericArcVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  public <T extends ASTGenericArcNode> void acceptGenericSeperatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTGenericComponentHead node) {
    this.getPrinter().print("<");
    acceptGenericSeperatedList(node.getArcTypeParameterList());
    this.getPrinter().print("> ");
    if(!node.isEmptyArcTypeParameters()) {
      this.getPrinter().print("(");
      acceptSeperatedList(node.getArcParameterList());
      this.getPrinter().print(") ");
    }
    if(node.isPresentParent()){
      this.getPrinter().print("extends ");
      node.getParent().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTArcTypeParameter node) {
    this.getPrinter().print(node.getName() + " ");
    if(!node.isEmptyUpperBounds()){
      this.getPrinter().print("extends ");
      Iterator<ASTMCType> iterator = node.getUpperBoundList().iterator();
      iterator.next().accept(this.getRealThis());
      while (iterator.hasNext()) {
        this.getPrinter().print(" & ");
        iterator.next().accept(this.getRealThis());
      }
    }
  }


}
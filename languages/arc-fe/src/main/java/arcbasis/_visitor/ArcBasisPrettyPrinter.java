/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class ArcBasisPrettyPrinter implements ArcBasisVisitor {

  private ArcBasisVisitor realThis = this;
  protected IndentPrinter printer;

  public ArcBasisPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public ArcBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ArcBasisVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ArcBasisVisitor realThis) {
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

  public <T extends ASTExpressionsBasisNode> void acceptSeperatedExpressionList(@NotNull List<T> list){
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
  public void handle(@NotNull ASTComponentType node) {
    this.getPrinter().print("component ");
    this.getPrinter().print(node.getName());
    node.getHead().accept(this.getRealThis());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getComponentInstanceList());
    node.getBody().accept(this.getRealThis());
  }

  @Override
  public void handle(@NotNull ASTComponentHead node) {
    if (!node.isEmptyArcParameters()) {
      this.getPrinter().print("(");
      acceptSeperatedList(node.getArcParameterList());
      this.getPrinter().print(")");
    }
    if (node.isPresentParent()) {
      this.getPrinter().print(" extends ");
      node.getParent().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(@NotNull ASTArcParameter node) {
    node.getMCType().accept(this.getRealThis());
    this.getPrinter().print(" ");
    this.getPrinter().print(node.getName());
    if(node.isPresentDefault()){
      this.getPrinter().print(" = ");
      node.getDefault().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(@NotNull ASTComponentBody node) {
    this.getPrinter().println(" { ");
    this.getPrinter().indent();
    for (ASTArcElement element : node.getArcElementList()) {
      element.accept(this.getRealThis());
    }
    this.getPrinter().unindent();
    this.getPrinter().println("}");
  }

  @Override
  public void handle(@NotNull ASTComponentInterface node) {
    this.getPrinter().print("port ");
    acceptSeperatedList(node.getPortDeclarationList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(@NotNull ASTPortDeclaration node) {
    node.getPortDirection().accept(this.getRealThis());
    node.getMCType().accept(this.getRealThis());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getPortList());
  }

  @Override
  public void handle(ASTPortDirectionIn node) {
    this.getPrinter().print("in ");
  }

  @Override
  public void handle(ASTPortDirectionOut node) {
    this.getPrinter().print("out ");
  }

  @Override
  public void handle(ASTPort node) {
    this.getPrinter().print(node.getName());
  }

  @Override
  public void handle(ASTArcFieldDeclaration node) {
    node.getMCType().accept(this.getRealThis());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getArcFieldList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTArcField node) {
    this.getPrinter().print(node.getName());
    this.getPrinter().print(" = ");
    node.getInitial().accept(this.getRealThis());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTComponentInstantiation node) {
    node.getMCType().accept(this.getRealThis());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getComponentInstanceList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTComponentInstance node) {
    this.getPrinter().print(node.getName());
    if(node.isPresentArcArguments()){
      node.getArcArguments().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTArcArguments node) {
    this.getPrinter().print("(");
    acceptSeperatedExpressionList(node.getExpressionList());
    this.getPrinter().print(")");
  }

  @Override
  public void handle(ASTConnector node) {
    node.getSource().accept(this.getRealThis());
    this.getPrinter().print(" -> ");
    acceptSeperatedList(node.getTargetList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTPortAccess node) {
    if(node.isPresentComponent()){
      this.getPrinter().print(node.getComponent());
      this.getPrinter().print(".");
    }
    this.getPrinter().print(node.getPort());
  }
}
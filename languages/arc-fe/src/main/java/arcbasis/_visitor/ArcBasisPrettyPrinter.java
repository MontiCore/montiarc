/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class ArcBasisPrettyPrinter implements ArcBasisHandler {

  private ArcBasisTraverser traverser;
  protected IndentPrinter printer;

  public ArcBasisPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ArcBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
  }

  @Override
  public ArcBasisTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(@NotNull ArcBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list){
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

  public boolean isCompletedStatement(){
    int lastIndex = this.getPrinter().getWrittenbuffer().lastIndexOf(";");
    if(lastIndex == -1){
      return false;
    }
    String s = StringUtils.deleteWhitespace(this.getPrinter().getWrittenbuffer().substring(lastIndex));
    return s.endsWith(";");
  }

  @Override
  public void handle(@NotNull ASTComponentType node) {
    this.getPrinter().print("component ");
    this.getPrinter().print(node.getName());
    node.getHead().accept(this.getTraverser());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getComponentInstanceList());
    node.getBody().accept(this.getTraverser());
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
      node.getParent().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTArcParameter node) {
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().print(" ");
    this.getPrinter().print(node.getName());
    if(node.isPresentDefault()){
      this.getPrinter().print(" = ");
      node.getDefault().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(@NotNull ASTComponentBody node) {
    this.getPrinter().println(" { ");
    this.getPrinter().indent();
    for (ASTArcElement element : node.getArcElementList()) {
      element.accept(this.getTraverser());
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
    node.getPortDirection().accept(this.getTraverser());
    node.getMCType().accept(this.getTraverser());
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
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getArcFieldList());
    if(!isCompletedStatement()) {
      this.getPrinter().println(";");
    }
  }

  @Override
  public void handle(ASTArcField node) {
    this.getPrinter().print(node.getName());
    this.getPrinter().print(" = ");
    node.getInitial().accept(this.getTraverser());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTComponentInstantiation node) {
    node.getMCType().accept(this.getTraverser());
    this.getPrinter().print(" ");
    acceptSeperatedList(node.getComponentInstanceList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTComponentInstance node) {
    this.getPrinter().print(node.getName());
    if(node.isPresentArguments()){
      node.getArguments().accept(this.getTraverser());
    }
  }

  @Override
  public void handle(ASTConnector node) {
    node.getSource().accept(this.getTraverser());
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
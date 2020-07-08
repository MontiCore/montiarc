/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis._ast.*;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.*;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;

public class ArcBasisPrettyPrinter implements ArcBasisVisitor {

  private ArcBasisVisitor realThis = this;
  protected IndentPrinter printer;
  protected MCBasicTypesPrettyPrinter typePrinter;

  public ArcBasisPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    MCBasicTypesPrettyPrinter typePrinter = new MCBasicTypesPrettyPrinter(printer);
    this.printer = printer;
    this.typePrinter = typePrinter;
  }

  public ArcBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
    this.typePrinter = new MCBasicTypesPrettyPrinter(printer);
  }

  public ArcBasisPrettyPrinter(@NotNull IndentPrinter printer,
    @NotNull MCBasicTypesPrettyPrinter typePrinter) {
    Preconditions.checkArgument(printer != null);
    Preconditions.checkArgument(typePrinter != null);
    this.printer = printer;
    this.typePrinter = typePrinter;
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

  public MCBasicTypesPrettyPrinter getTypePrinter() {
    return this.typePrinter;
  }

  @Override
  public void handle(@NotNull ASTComponentType node) {
    this.getPrinter().print("component ");
    this.getPrinter().print(node.getName());
    node.getHead().accept(this.getRealThis());
    node.getBody().accept(this.getRealThis());
  }

  @Override
  public void handle(@NotNull ASTComponentHead node) {
    if (!node.isEmptyArcParameters()) {
      this.getPrinter().print("(");
      Iterator<ASTArcParameter> iterator = node.getArcParameterList().iterator();
      iterator.next().accept(this.getRealThis());
      while (iterator.hasNext()) {
        if (iterator.hasNext()) {
          this.getPrinter().print(", ");
        }
      }
      this.getPrinter().print(")");
    }
    if (node.isPresentParent()) {
      this.getPrinter().print(" extends ");
      node.getParent().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(@NotNull ASTArcParameter node) {
    node.getMCType().printType(this.getTypePrinter());
    this.getPrinter().print(" ");
    this.getPrinter().print(node.getName());
  }

  @Override
  public void handle(@NotNull ASTComponentBody node) {
    this.getPrinter().print(" { ");
    this.getPrinter().indent();
    for (ASTArcElement element : node.getArcElementList()) {
      element.accept(this.getRealThis());
      this.getPrinter().indent();
    }
    this.getPrinter().print("}");
  }

  @Override
  public void handle(@NotNull ASTComponentInterface node) {
    this.getPrinter().print("port ");
    for (ASTPortDeclaration portDec : node.getPortDeclarationList()) {
      portDec.accept(this.getRealThis());
    }
    this.getPrinter().print(";");
  }

  @Override
  public void handle(@NotNull ASTPortDeclaration node) {
    
  }

  @Override
  public void handle(ASTPortDirectionIn node) {

  }

  @Override
  public void handle(ASTPortDirectionOut node) {

  }

  @Override
  public void handle(ASTPort node) {

  }

  @Override
  public void handle(ASTArcFieldDeclaration node) {

  }

  @Override
  public void handle(ASTArcField node) {

  }

  @Override
  public void handle(ASTComponentInstantiation node) {

  }

  @Override
  public void handle(ASTComponentInstance node) {

  }

  @Override
  public void handle(ASTArcArguments node) {

  }

  @Override
  public void handle(ASTConnector node) {

  }

  @Override
  public void handle(ASTPortAccess node) {

  }

  @Override
  public void handle(ASTArcElement node) {

  }

  @Override
  public void handle(ASTPortDirection node) {

  }
}
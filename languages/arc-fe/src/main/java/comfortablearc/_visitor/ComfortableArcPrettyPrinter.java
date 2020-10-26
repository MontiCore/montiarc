/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._visitor;

import com.google.common.base.Preconditions;
import comfortablearc._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcPrettyPrinter implements ComfortableArcVisitor {

  private ComfortableArcVisitor realThis = this;
  protected IndentPrinter printer;

  public ComfortableArcPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public ComfortableArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ComfortableArcVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ComfortableArcVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTArcAutoConnect node) {
    this.getPrinter().print("autoconnect");
    node.getArcACMode().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTArcACType node) {
    this.getPrinter().print("type ");
  }

  @Override
  public void handle(ASTArcACPort node) {
    this.getPrinter().print("port ");
  }

  @Override
  public void handle(ASTArcACOff node) {
    this.getPrinter().print("off ");
  }

  @Override
  public void handle(ASTArcAutoInstantiate node) {
    this.getPrinter().print("autoinstantiate ");
    node.getArcAIMode().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTArcAIOn node) {
    this.getPrinter().print("on ");
  }

  @Override
  public void handle(ASTArcAIOff node) {
    this.getPrinter().print("off ");
  }

  @Override
  public void handle(ASTConnectedComponentInstance node) {
    this.getPrinter().print(node.getName());
    this.getPrinter().print(" ");
    if(node.isPresentArguments()) {
      node.getArguments().accept(this.getRealThis());
    }
    this.getPrinter().print("[ ");
    node.getConnectorsList().stream().forEach((c)->c.accept(this.getRealThis()));
    this.getPrinter().print("] ");
  }
  
}
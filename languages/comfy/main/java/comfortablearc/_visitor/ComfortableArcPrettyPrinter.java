/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._visitor;

import com.google.common.base.Preconditions;
import comfortablearc._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcPrettyPrinter implements ComfortableArcHandler {

  private ComfortableArcTraverser traverser;
  protected IndentPrinter printer;

  public ComfortableArcPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ComfortableArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    this.printer = printer;
  }

  @Override
  public ComfortableArcTraverser getTraverser() {
    return this.traverser;
  }

  public void setTraverser(@NotNull ComfortableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTArcAutoConnect node) {
    this.getPrinter().print("autoconnect");
    node.getArcACMode().accept(this.getTraverser());
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
    node.getArcAIMode().accept(this.getTraverser());
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
      node.getArguments().accept(this.getTraverser());
    }
    this.getPrinter().print("[ ");
    node.getConnectorList().forEach((c)->c.accept(this.getTraverser()));
    this.getPrinter().print("] ");
  }
  
}
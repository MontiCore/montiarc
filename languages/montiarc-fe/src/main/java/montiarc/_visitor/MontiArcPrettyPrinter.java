/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcPrettyPrinter implements MontiArcHandler {

  private MontiArcTraverser traverser;
  protected IndentPrinter printer;

  public MontiArcPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public MontiArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }
  
  @Override
  public void setTraverser(@NotNull MontiArcTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTMACompilationUnit node) {
    if(node.isPresentPackage()){
      this.getPrinter().print("package ");
      node.getPackage().accept(this.getTraverser());
      this.getPrinter().println(";");
    }
    node.getImportStatementList().stream().forEach(n->n.accept(this.getTraverser()));
    node.getComponentType().accept(this.getTraverser());
  }

  @Override
  public void handle(ASTArcTiming node) {
    this.getPrinter().print("timing ");
    node.getArcTimeMode().accept(this.getTraverser());
    this.getPrinter().print(";");
  }

  @Override
  public void handle(ASTArcInstant node) {
    this.getPrinter().print("instant ");
  }

  @Override
  public void handle(ASTArcSync node) {
    this.getPrinter().print("sync ");
  }

  @Override
  public void handle(ASTArcUntimed node) {
    this.getPrinter().print("untimed ");
  }
}
/* (c) https://github.com/MontiCore/monticore */
package montiarc._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcPrettyPrinter implements MontiArcVisitor {

  private MontiArcVisitor realThis = this;
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
  public MontiArcVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull MontiArcVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTMACompilationUnit node) {
    if(node.isPresentPackage()){
      this.getPrinter().print("package ");
      node.getPackage().accept(this.getRealThis());
      this.getPrinter().println(";");
    }
    node.getImportStatementList().stream().forEach(n->n.accept(this.getRealThis()));
    node.getComponentType().accept(this.getRealThis());
  }

  @Override
  public void handle(ASTArcTiming node) {
    this.getPrinter().print("timing ");
    node.getArcTimeMode().accept(this.getRealThis());
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
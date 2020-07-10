/* (c) https://github.com/MontiCore/monticore */
package arccore._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcCorePrettyPrinter implements ArcCoreVisitor {

  private ArcCoreVisitor realThis = this;
  protected IndentPrinter printer;

  public ArcCorePrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    MCBasicTypesPrettyPrinter typePrinter = new MCBasicTypesPrettyPrinter(printer);
    this.printer = printer;
  }

  public ArcCorePrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ArcCoreVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ArcCoreVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }


}
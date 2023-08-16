/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JSimJavaPrinter extends MontiArcFullPrettyPrinter {

  public MA2JSimJavaPrinter() {
    this(new IndentPrinter());
  }

  public MA2JSimJavaPrinter(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MA2JSimJavaPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);
    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(printer, printComments);
    this.traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
  }
}

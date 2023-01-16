/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JavaFullPrettyPrinter extends MontiArcFullPrettyPrinter {

  public MA2JavaFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MA2JavaFullPrettyPrinter(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MA2JavaFullPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);
    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(printer, printComments);
    this.traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
  }
}

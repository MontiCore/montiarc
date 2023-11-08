/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JavaFullPrettyPrinter extends MontiArcFullPrettyPrinter {

  public MA2JavaFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MA2JavaFullPrettyPrinter(boolean printComments) {
    this(new IndentPrinter(), printComments);
  }

  public MA2JavaFullPrettyPrinter(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MA2JavaFullPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    this(new MontiArcTypeCalculator(), printer, printComments);
  }

  public MA2JavaFullPrettyPrinter(@NotNull IArcTypeCalculator tc,
                                  @NotNull IndentPrinter printer,
                                  boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);
    CommonExpressionsJavaPrinter cePrinter = new CommonExpressionsJavaPrinter(tc, printer, printComments);
    this.traverser.setCommonExpressionsHandler(cePrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(cePrinter);
  }
}

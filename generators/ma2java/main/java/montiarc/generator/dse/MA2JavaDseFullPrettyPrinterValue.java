/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JavaDseFullPrettyPrinterValue extends MontiArcFullPrettyPrinter {

  public MA2JavaDseFullPrettyPrinterValue() {
    this(new IndentPrinter());
  }

  public MA2JavaDseFullPrettyPrinterValue(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MA2JavaDseFullPrettyPrinterValue(@NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);


    DseExpressionBasisPrettyPrinterValue dseExpressionBasisPrettyPrinterValue = new DseExpressionBasisPrettyPrinterValue(printer, printComments);
    this.traverser.setExpressionsBasisHandler(dseExpressionBasisPrettyPrinterValue);
    this.traverser.getExpressionsBasisVisitorList().clear();
    this.traverser.add4ExpressionsBasis(dseExpressionBasisPrettyPrinterValue);

    DseCommonExpressionsJavaPrinterValue dseCommonExpressionsJavaPrinterValue = new DseCommonExpressionsJavaPrinterValue(printer, printComments);
    this.traverser.setCommonExpressionsHandler(dseCommonExpressionsJavaPrinterValue);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(dseCommonExpressionsJavaPrinterValue);
  }
}

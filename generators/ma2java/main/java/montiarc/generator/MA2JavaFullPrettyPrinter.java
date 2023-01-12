/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;

public class MA2JavaFullPrettyPrinter extends MontiArcFullPrettyPrinter {

  @Override
  protected void initCommonExpressionPrettyPrinter(IndentPrinter printer, boolean printComments) {
    Preconditions.checkNotNull(printer);

    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(printer, printComments);
    traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
  }
}

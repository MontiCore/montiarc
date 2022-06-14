/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._visitor.MontiArcFullPrettyPrinter;

public class MA2JavaFullPrettyPrinter extends MontiArcFullPrettyPrinter {

  @Override
  protected void initCommonExpressionPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);

    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
  }
}

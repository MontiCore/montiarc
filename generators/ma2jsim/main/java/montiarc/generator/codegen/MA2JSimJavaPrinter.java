/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.codegen;

import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JSimJavaPrinter extends MontiArcFullPrettyPrinter {

  public MA2JSimJavaPrinter() {
    this(new IndentPrinter());
  }

  public MA2JSimJavaPrinter(@NotNull IndentPrinter printer) {
    this(new MontiArcTypeCalculator(), Preconditions.checkNotNull(printer), true);
  }

  public MA2JSimJavaPrinter(@NotNull IArcTypeCalculator tc, @NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);
    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(tc, printer, printComments);
    this.traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
    ExpressionsBasisJavaPrinter expressionsBasisJavaPrinter = new ExpressionsBasisJavaPrinter(printer, printComments);
    this.traverser.setExpressionsBasisHandler(expressionsBasisJavaPrinter);
    this.traverser.getExpressionsBasisVisitorList().clear();
    this.traverser.add4ExpressionsBasis(expressionsBasisJavaPrinter);
    MCBasicTypesJavaPrinter mcBasicTypesJavaPrinter = new MCBasicTypesJavaPrinter(printer, printComments);
    this.traverser.setMCBasicTypesHandler(mcBasicTypesJavaPrinter);
    this.traverser.getMCBasicTypesVisitorList().clear();
    this.traverser.add4MCBasicTypes(mcBasicTypesJavaPrinter);
  }
}

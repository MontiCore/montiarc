/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.dse;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montiarc._prettyprint.MontiArcFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class MA2JavaDseFullPrettyPrinter extends MontiArcFullPrettyPrinter {

  public MA2JavaDseFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MA2JavaDseFullPrettyPrinter(@NotNull IndentPrinter printer) {
    this(Preconditions.checkNotNull(printer), true);
  }

  public MA2JavaDseFullPrettyPrinter(@NotNull IndentPrinter printer, boolean printComments) {
    super(Preconditions.checkNotNull(printer), printComments);

    DseCommonExpressionsJavaPrinter dseCommonExpressionsJavaPrinter = new DseCommonExpressionsJavaPrinter(printer, printComments);
    this.traverser.setCommonExpressionsHandler(dseCommonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(dseCommonExpressionsJavaPrinter);

    DseCommonAssignmentJavaPrinter dseCommonAssignmentJavaPrinter = new DseCommonAssignmentJavaPrinter(printer, printComments);
    this.traverser.setAssignmentExpressionsHandler(dseCommonAssignmentJavaPrinter);
    this.traverser.getAssignmentExpressionsVisitorList().clear();
    this.traverser.add4AssignmentExpressions(dseCommonAssignmentJavaPrinter);

    DseExpressionBasisPrettyPrinter dseExpressionBasisPrettyPrinter = new DseExpressionBasisPrettyPrinter(printer, printComments);
    this.traverser.setExpressionsBasisHandler(dseExpressionBasisPrettyPrinter);
    this.traverser.getExpressionsBasisVisitorList().clear();
    this.traverser.add4ExpressionsBasis(dseExpressionBasisPrettyPrinter);

    DseMCCommonLiteralsPrettyPrinter dseMCCommonLiteralsPrettyPrinter = new DseMCCommonLiteralsPrettyPrinter(printer, printComments);
    this.traverser.setMCCommonLiteralsHandler(dseMCCommonLiteralsPrettyPrinter);
    this.traverser.getMCCommonLiteralsVisitorList().clear();
    this.traverser.add4MCCommonLiterals(dseMCCommonLiteralsPrettyPrinter);
  }
}

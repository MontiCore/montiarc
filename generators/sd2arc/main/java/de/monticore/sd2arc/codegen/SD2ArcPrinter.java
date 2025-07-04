/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.codegen;

import de.monticore.lang.sd4components._prettyprint.SD4ComponentsFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class SD2ArcPrinter extends SD4ComponentsFullPrettyPrinter {

  public SD2ArcPrinter() {
    this(new IndentPrinter());
  }

  public SD2ArcPrinter(IndentPrinter printer) {
    this(printer, false);
  }

  public SD2ArcPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);

    CommonExpressionsJavaPrinter commonExpressionsJavaPrinter = new CommonExpressionsJavaPrinter(printer, printComments);
    this.traverser.setCommonExpressionsHandler(commonExpressionsJavaPrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(commonExpressionsJavaPrinter);
  }
}

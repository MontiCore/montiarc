/* (c) https://github.com/MontiCore/monticore */
package montiarc._prettyprint;

import de.monticore.prettyprint.IndentPrinter;

public class MontiArcFullPrettyPrinter extends MontiArcFullPrettyPrinterTOP {

  public MontiArcFullPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);

    MACommonExpressionsPrettyPrinter cePrinter =
        new MACommonExpressionsPrettyPrinter(printer, printComments);
    this.traverser.setCommonExpressionsHandler(cePrinter);
    this.traverser.getCommonExpressionsVisitorList().clear();
    this.traverser.add4CommonExpressions(cePrinter);

    MAAssignmentExpressionsPrettyPrinter aePrinter =
        new MAAssignmentExpressionsPrettyPrinter(printer, printComments);
    this.traverser.setAssignmentExpressionsHandler(aePrinter);
    this.traverser.getAssignmentExpressionsVisitorList().clear();
    this.traverser.add4AssignmentExpressions(aePrinter);

    MACommonStatementsPrettyPrinter mcsPrinter =
        new MACommonStatementsPrettyPrinter(printer, printComments);
    this.traverser.setMCCommonStatementsHandler(mcsPrinter);
    this.traverser.getMCCommonStatementsVisitorList().clear();
    this.traverser.add4MCCommonStatements(mcsPrinter);

    MAVarDeclarationStatementsPrettyPrinter vdPrinter =
        new MAVarDeclarationStatementsPrettyPrinter(printer, printComments);
    this.traverser.setMCVarDeclarationStatementsHandler(vdPrinter);
    this.traverser.getMCVarDeclarationStatementsVisitorList().clear();
    this.traverser.add4MCVarDeclarationStatements(vdPrinter);

    MASCBasisPrettyPrinter scbPrinter =
        new MASCBasisPrettyPrinter(printer, printComments);
    this.traverser.setSCBasisHandler(scbPrinter);
    this.traverser.getSCBasisVisitorList().clear();
    this.traverser.add4SCBasis(scbPrinter);

    MASCTransitions4CodePrettyPrinter sctPrinter =
        new MASCTransitions4CodePrettyPrinter(printer, printComments);
    this.traverser.setSCTransitions4CodeHandler(sctPrinter);
    this.traverser.getSCTransitions4CodeVisitorList().clear();
    this.traverser.add4SCTransitions4Code(sctPrinter);

    MAExpressionsBasisPrettyPrinter ebPrinter =
        new MAExpressionsBasisPrettyPrinter(printer, printComments);
    this.traverser.setExpressionsBasisHandler(ebPrinter);
    this.traverser.getExpressionsBasisVisitorList().clear();
    this.traverser.add4ExpressionsBasis(ebPrinter);
  }
}

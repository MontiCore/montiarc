/* (c) https://github.com/MontiCore/monticore */
package variablearc._visitor;

import arcbasis._visitor.ArcBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import variablearc.VariableArcMill;
import variablearc._ast.ASTVariableArcNode;

public class VariableArcFullPrettyPrinter {
  protected VariableArcTraverser traverser;

  protected IndentPrinter printer;

  public VariableArcFullPrettyPrinter() {
    this.printer = new IndentPrinter();
    traverser = VariableArcMill.traverser();

    MCBasicsPrettyPrinter mCBasicsVisitor = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(mCBasicsVisitor);
    MCBasicTypesPrettyPrinter mCBasicTypesVisitor = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(mCBasicTypesVisitor);
    traverser.setMCBasicTypesHandler(mCBasicTypesVisitor);
    ExpressionsBasisPrettyPrinter expressionsBasisVisitor = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(expressionsBasisVisitor);
    traverser.setExpressionsBasisHandler(expressionsBasisVisitor);
    ArcBasisPrettyPrinter arcBasisVisitor = new ArcBasisPrettyPrinter(printer);
    traverser.setArcBasisHandler(arcBasisVisitor);

    VariableArcPrettyPrinter variableArcPrettyPrinter = new VariableArcPrettyPrinter(printer);
    traverser.add4VariableArc(variableArcPrettyPrinter);
    traverser.setVariableArcHandler(variableArcPrettyPrinter);
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTVariableArcNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public VariableArcTraverser getTraverser() {
    return traverser;
  }
}


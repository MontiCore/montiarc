/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._visitor;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import comfortablearc.ComfortableArcMill;
import comfortablearc._ast.ASTComfortableArcNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class ComfortableArcFullPrettyPrinter {

  protected ComfortableArcTraverser traverser;

  protected IndentPrinter printer;

  public ComfortableArcFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ComfortableArcFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = ComfortableArcMill.traverser();
    traverser.setArcBasisHandler(new ArcBasisPrettyPrinter(printer));
    traverser.setComfortableArcHandler(new ComfortableArcPrettyPrinter(printer));
    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTComfortableArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTArcBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public ComfortableArcTraverser getTraverser() {
    return traverser;
  }
}

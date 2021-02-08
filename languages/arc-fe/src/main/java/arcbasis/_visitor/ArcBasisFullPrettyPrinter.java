package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBasisNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class ArcBasisFullPrettyPrinter {

  protected ArcBasisTraverser traverser;

  protected IndentPrinter printer;

  public ArcBasisFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ArcBasisFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = ArcBasisMill.traverser();
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
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
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

  public ArcBasisTraverser getTraverser() {
    return traverser;
  }
}

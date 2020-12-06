package comfortablearc._visitor;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import comfortablearc._ast.ASTComfortableArcNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;

public class ComfortableArcPrettyPrinterDelegator extends ComfortableArcDelegatorVisitor {

  protected ComfortableArcPrettyPrinterDelegator realThis = this;

  protected IndentPrinter printer = null;

  public ComfortableArcPrettyPrinterDelegator() {
    this.printer = new IndentPrinter();
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setComfortableArcVisitor(new ComfortableArcPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
  }

  public ComfortableArcPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setComfortableArcVisitor(new ComfortableArcPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTComfortableArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCBasicTypesNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTArcBasisNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public ComfortableArcPrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}

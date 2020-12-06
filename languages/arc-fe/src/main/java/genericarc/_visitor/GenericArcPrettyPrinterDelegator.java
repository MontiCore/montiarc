package genericarc._visitor;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import genericarc._ast.ASTGenericArcNode;

public class GenericArcPrettyPrinterDelegator extends GenericArcDelegatorVisitor {

  protected GenericArcPrettyPrinterDelegator realThis = this;

  protected IndentPrinter printer = null;

  public GenericArcPrettyPrinterDelegator() {
    this.printer = new IndentPrinter();
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setGenericArcVisitor(new GenericArcPrettyPrinter(printer));
  }

  public GenericArcPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setGenericArcVisitor(new GenericArcPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTGenericArcNode a) {
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
  public GenericArcPrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}

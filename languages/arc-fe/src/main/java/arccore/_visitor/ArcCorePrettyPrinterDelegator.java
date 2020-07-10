package arccore._visitor;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import arccore._ast.ASTArcCoreNode;
import comfortablearc._ast.ASTComfortableArcNode;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import genericarc._ast.ASTGenericArcNode;
import genericarc._visitor.GenericArcPrettyPrinter;

public class ArcCorePrettyPrinterDelegator extends ArcCoreDelegatorVisitor {

  protected ArcCorePrettyPrinterDelegator realThis = this;

  protected IndentPrinter printer = null;

  public ArcCorePrettyPrinterDelegator() {
    this.printer = new IndentPrinter();
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setComfortableArcVisitor(new ComfortableArcPrettyPrinter(printer));
    setGenericArcVisitor(new GenericArcPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setArcCoreVisitor(new ArcCorePrettyPrinter(printer));
  }

  public ArcCorePrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setComfortableArcVisitor(new ComfortableArcPrettyPrinter(printer));
    setGenericArcVisitor(new GenericArcPrettyPrinter(printer));
    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setArcCoreVisitor(new ArcCorePrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTArcCoreNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTComfortableArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
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
  public ArcCorePrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}

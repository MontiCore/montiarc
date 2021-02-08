package arccore._visitor;

import arcbasis._ast.ASTArcBasisNode;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import arccore.ArcCoreMill;
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

public class ArcCoreFullPrettyPrinter {

  protected ArcCoreTraverser traverser;

  protected IndentPrinter printer;

  public ArcCoreFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ArcCoreFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = ArcCoreMill.traverser();
    ArcBasisPrettyPrinter arcBasisPrettyPrinter = new ArcBasisPrettyPrinter(printer);
    traverser.setArcBasisHandler(arcBasisPrettyPrinter);
    traverser.setComfortableArcHandler(new ComfortableArcPrettyPrinter(printer));
    traverser.setGenericArcHandler(new GenericArcPrettyPrinter(printer));
    traverser.add4MCBasics(new MCBasicsPrettyPrinter(printer));
    traverser.add4MCBasicTypes(new MCBasicTypesPrettyPrinter(printer));
    traverser.add4ExpressionsBasis(new ExpressionsBasisPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTArcCoreNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTComfortableArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTGenericArcNode a) {
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

  public ArcCoreTraverser getTraverser() {
    return traverser;
  }
}

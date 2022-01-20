/* (c) https://github.com/MontiCore/monticore */
package arcbasis._visitor;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.mcbasics._ast.ASTMCBasicsNode;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcBasisFullPrettyPrinter implements IFullPrettyPrinter {

  protected ArcBasisTraverser traverser;

  protected IndentPrinter printer;

  public ArcBasisFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public ArcBasisFullPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
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

  public String prettyprint(@NotNull ASTMCBasicsNode a) {
    Preconditions.checkNotNull(a);
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(@NotNull ASTMCBasicTypesNode a) {
    Preconditions.checkNotNull(a);
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(@NotNull ASTExpressionsBasisNode a) {
    Preconditions.checkNotNull(a);
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(@NotNull ASTArcBasisNode a) {
    Preconditions.checkNotNull(a);
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public String prettyprint(@NotNull ASTExpression a) {
    Preconditions.checkNotNull(a);
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }
}

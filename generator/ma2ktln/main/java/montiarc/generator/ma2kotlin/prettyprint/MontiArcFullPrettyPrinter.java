/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.ma2kotlin.prettyprint;

import com.google.common.base.Preconditions;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * a pretty-printer that behaves like the normal one, but in some cases it treats some expressions differently
 */
public class MontiArcFullPrettyPrinter extends montiarc._visitor.MontiArcFullPrettyPrinter {

  public MontiArcFullPrettyPrinter(int initialIndent) {
    super(new IndentPrinter());
    getPrinter().indent(initialIndent);
  }

  @Override
  protected void initExpressionsBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrinter(printer);
    traverser.setExpressionsBasisHandler(prettyPrinter);
    traverser.add4ExpressionsBasis(prettyPrinter);
  }

  @Override
  protected void initCommonExpressionPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    CommonExpressionsPrettyPrinter prettyPrinter = new CommonExpressionsPrinter(printer);
    traverser.setCommonExpressionsHandler(prettyPrinter);
    traverser.add4CommonExpressions(prettyPrinter);
  }

  @Override
  protected void initAssignmentExpressionsPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    AssignmentExpressionsPrettyPrinter prettyPrinter = new AssignmentExpressionsPrinter(printer);
    traverser.setAssignmentExpressionsHandler(prettyPrinter);
    traverser.add4AssignmentExpressions(prettyPrinter);
  }

  @Override
  protected void initMCCommonStatementsPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    MCCommonStatementsPrettyPrinter prettyPrinter = new CommonStatementsPrinter(printer);
    traverser.setMCCommonStatementsHandler(prettyPrinter);
    traverser.add4MCCommonStatements(prettyPrinter);
  }

  @Override
  protected void initMCVarDeclarationStatementsPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    MCVarDeclarationStatementsPrettyPrinter prettyPrinter = new VariableDeclarationsPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(prettyPrinter);
    traverser.add4MCVarDeclarationStatements(prettyPrinter);
  }

  @Override
  protected void initMCBasicTypesPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    MCBasicTypesPrettyPrinter prettyPrinter = new MCBasicTypesPrinter(printer);
    traverser.setMCBasicTypesHandler(prettyPrinter);
    traverser.add4MCBasicTypes(prettyPrinter);
  }

  @Override
  protected void initMCCommonLiteralsPrettyPrinter(IndentPrinter printer) {
    Preconditions.checkNotNull(printer);
    MCCommonLiteralsPrettyPrinter prettyPrinter = new CommonLiteralsPrinter(printer);
    traverser.setMCCommonLiteralsHandler(prettyPrinter);
    traverser.add4MCCommonLiterals(prettyPrinter);
  }
}
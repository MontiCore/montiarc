/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTExpressionStatementCoCo;
import arcbasis.check.IArcTypeCalculator;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks whether expression statements are evaluable expressions, e.g. {@code 4 + 8}. An example for a violation is
 * {@code "Hello" / 2}, as strings must not be divided by integers
 */
public class ExpressionStatementWellFormedness implements MCCommonStatementsASTExpressionStatementCoCo {

  protected final IArcTypeCalculator typeCalculator;

  public ExpressionStatementWellFormedness(@NotNull IArcTypeCalculator typeCalculator) {
    this.typeCalculator = Preconditions.checkNotNull(typeCalculator);
  }

  protected IArcTypeCalculator getTypeCalculator() {
    return typeCalculator;
  }

  @Override
  public void check(@NotNull ASTExpressionStatement statement) {
    Preconditions.checkNotNull(statement);
    this.getTypeCalculator().deriveType(statement.getExpression());
  }
}

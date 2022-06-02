/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

/**
 * extends the code of {@link DeriveSymTypeOfExpression#calculateNameExpression(ASTNameExpression)} by one if clause
 * that searches for ports
 */
public class DeriveSymTypeOfExpressionWithPorts extends DeriveSymTypeOfExpression {

  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Optional<SymTypeExpression> type = Optional.empty();
    try {
      type = super.calculateNameExpression(expr);
    } catch (ResolvedSeveralEntriesForSymbolException | NullPointerException ignored) {}
    if (type.isEmpty()) {
      type = getEnclosingScope(expr).flatMap(s -> s.resolvePortMany(expr.getName()).stream().findFirst()).map(PortSymbol::getType);
      type.ifPresent(port -> typeCheckResult.setField());
    }
    return type;
  }

  /**
   * approximately analog to {@link DeriveSymTypeOfExpression#getScope(IExpressionsBasisScope)}
   *
   * @param node ast element whose enclosing scope should be found
   * @return enclosing scope of the given node, or {@link Optional#empty()} if the enclosing scope is no {@link
   * IArcBasisScope}
   */
  public Optional<IArcBasisScope> getEnclosingScope(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    if (node.getEnclosingScope() instanceof IArcBasisScope) {
      return Optional.of((IArcBasisScope) node.getEnclosingScope());
    }
    return Optional.empty();
  }
}
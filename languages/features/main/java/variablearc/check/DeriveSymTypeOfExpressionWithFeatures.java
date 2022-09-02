/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.Optional;

public class DeriveSymTypeOfExpressionWithFeatures extends DeriveSymTypeOfExpression {

  @Override
  protected Optional<SymTypeExpression> calculateNameExpression(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);
    Optional<SymTypeExpression> type = super.calculateNameExpression(expr);
    if (type.isEmpty()) {
      type = getEnclosingScope(expr).flatMap(s -> s.resolvePort(expr.getName())).map(PortSymbol::getType);
      type.ifPresent(port -> typeCheckResult.setField());
    }
    if (type.isEmpty()) {
      type = getEnclosingVariableScope(expr).flatMap(s -> s.resolveArcFeature(expr.getName())).map(feature -> SymTypeExpressionFactory.createPrimitive("boolean"));
      type.ifPresent(feature -> typeCheckResult.setField());
    }
    return type;
  }

  public Optional<IVariableArcScope> getEnclosingVariableScope(@NotNull ASTNode node) {
    Preconditions.checkNotNull(node);
    if (node.getEnclosingScope() instanceof IVariableArcScope) {
      return Optional.of((IVariableArcScope) node.getEnclosingScope());
    }
    return Optional.empty();
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

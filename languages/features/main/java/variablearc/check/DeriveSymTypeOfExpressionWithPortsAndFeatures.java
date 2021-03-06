/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.PortSymbol;
import arcbasis.check.DeriveSymTypeOfExpressionWithPorts;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;

import java.util.Optional;

public class DeriveSymTypeOfExpressionWithPortsAndFeatures extends DeriveSymTypeOfExpressionWithPorts {

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
}

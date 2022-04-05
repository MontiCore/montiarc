/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

public class NamePresenceChecker extends StatechartNameResolver implements ExpressionsBasisVisitor2 {

  /**
   * Creates a visitor that can check, whether referenced symbols (here fields, variables, and types) are present.
   * @param scope {@link #scope scope} in which the symbols should be found
   */
  public NamePresenceChecker(@NotNull IScope scope) {
    super(Preconditions.checkNotNull(scope));
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    String name = node.getName();
    if(!resolveField(name).isPresent() && !resolvePort(name).isPresent()
      && !(this.scope.resolveType(name).isPresent())) {
      Log.error(ArcError.SYMBOL_IN_STATECHART_MISSING.format(name, scope.getName()),
        node.get_SourcePositionStart(), node.get_SourcePositionEnd()
      );
    }
  }
}
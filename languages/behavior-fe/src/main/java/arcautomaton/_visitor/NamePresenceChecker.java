/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import arcbasis._symboltable.PortSymbol;
import arcbehaviorbasis.BehaviorError;
import arccore._symboltable.IArcCoreScope;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class NamePresenceChecker implements ExpressionsBasisVisitor2 {
  /**
   * scope that is spanned by the component containing the expression to visit
   */
  protected final IArcCoreScope scope;

  /**
   * creates a visitor that can check, whether references to fields, parameters or ports are valid
   * @param scope {@link #scope scope} in which the symbols should be found
   */
  public NamePresenceChecker(@NotNull IScope scope){
    Preconditions.checkNotNull(scope);
    Preconditions.checkArgument(scope instanceof IArcCoreScope);
    this.scope = (IArcCoreScope) scope;
  }

  /**
   * searches for a port of the component with the given variable
   */
  protected Optional<PortSymbol> resolvePort(String name){
    return scope.resolvePortLocally(name);
  }

  /**
   * tries to search for a parameter or a variable with the given name in this component
   */
  protected Optional<VariableSymbol> resolveField(String name){
    return scope.resolveVariableLocally(name);
  }

  @Override
  public void visit(@NotNull ASTNameExpression node) {
    Preconditions.checkNotNull(node);
    String name = node.getName();
    if(!resolveField(name).isPresent() && !resolvePort(name).isPresent()){
      BehaviorError.FIELD_IN_STATECHART_MISSING.logAt(node, name, scope.getName());
    }
  }
}
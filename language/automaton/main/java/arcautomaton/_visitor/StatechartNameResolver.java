/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._visitor;

import arcautomaton._symboltable.IArcAutomatonScope;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class StatechartNameResolver {
  /**
   * scope that is spanned by the component containing the expression to visit
   */
  protected final IArcAutomatonScope scope;

  /**
   * creates an object that facilitates resolving ports and variables in a scope
   * @param scope {@link #scope scope} in which the symbols should be found
   */
  public StatechartNameResolver(@NotNull IScope scope) {
    Preconditions.checkNotNull(scope);
    Preconditions.checkArgument(scope instanceof IArcAutomatonScope);
    this.scope = (IArcAutomatonScope) scope;
  }

  /**
   * @return the name of the assigned scope, or the name of one of it's enclosing scopes
   */
  public String getName() {
    IArcAutomatonScope scope = this.scope;
    while(!scope.isPresentName() && scope.getEnclosingScope() != null) {
      scope = scope.getEnclosingScope();
    }
    return scope.getName();
  }

  /**
   * searches for a port of the component with the given variable
   */
  public Optional<PortSymbol> resolvePort(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return scope.resolvePort(name);
  }

  /**
   * tries to search for a parameter or a variable with the given name in this component
   */
  public Optional<VariableSymbol> resolveField(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return scope.resolveVariable(name);
  }
}
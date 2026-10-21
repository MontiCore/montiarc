/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.microsoft.z3.Context;

import java.util.Optional;

public interface IVariableArcGlobalScope extends IVariableArcGlobalScopeTOP {

  /**
   * Initializes the global scope with a new Z3 context for the presence
   * formulas of its symbols, unless the analysis of variable components is
   * disabled, and closes the previous context. As creating and clearing the
   * global scope initialize it by {@link #init()}, i.e., with the analysis of
   * variable components, they create a new context, too.
   *
   * @param novar whether the analysis of variable components is disabled
   */
  void init(boolean novar);

  /**
   * Closes the Z3 context of the presence formulas, if any, e.g., before the
   * global scope is discarded by resetting the mill.
   */
  void closePresenceContext();

  /**
   * @return the Z3 context of the presence formulas of the symbols of the
   * global scope, empty if the analysis of variable components is disabled
   */
  Optional<Context> getPresenceContext();
}

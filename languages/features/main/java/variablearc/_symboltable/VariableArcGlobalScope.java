/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.microsoft.z3.Context;
import variablearc.smt.VariableArcDeriveSMTSort;

import java.util.Optional;

public class VariableArcGlobalScope extends VariableArcGlobalScopeTOP {

  protected Context presenceContext;

  @Override
  public VariableArcGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    this.init(false);
  }

  @Override
  public void init(boolean novar) {
    super.init();
    this.closePresenceContext();
    if (!novar) {
      this.presenceContext = new Context();
    }
  }

  @Override
  public void clear() {
    this.closePresenceContext();
    super.clear();
  }

  @Override
  public Optional<Context> getPresenceContext() {
    return Optional.ofNullable(this.presenceContext);
  }

  @Override
  public void closePresenceContext() {
    if (this.presenceContext != null) {
      VariableArcDeriveSMTSort.release(this.presenceContext);
      this.presenceContext.close();
      this.presenceContext = null;
    }
  }
}

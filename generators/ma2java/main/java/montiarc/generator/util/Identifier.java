/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.util;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;

/**
 * This class checks whether component parameter, variable, subcomponent instance, or port names are equal to names used
 * in the generated code.
 */
public class Identifier {

  protected final static String CURRENT_STATE_NAME = "currentState";

  protected final static String PREFIX = "r__";

  protected String currentStateName = CURRENT_STATE_NAME;

  /**
   * Checks whether component parameter, variable, subcomponent instance, or port names contain the identifier given as
   * the parameter.
   *
   * @param identifier The name to check
   * @return True, iff. there is at least one identifier that is equal to the given identifier
   */
  public boolean containsIdentifier(@NotNull ComponentTypeSymbol component, @NotNull String identifier) {
    Preconditions.checkNotNull(identifier);
    Preconditions.checkNotNull(component);
    return this.containsIdentifier(component.getPorts(), identifier)
      || this.containsIdentifier(component.getParameters(), identifier)
      || this.containsIdentifier(component.getFields(), identifier)
      || this.containsIdentifier(component.getSubComponents(), identifier);
  }

  protected boolean containsIdentifier(@NotNull Collection<? extends ISymbol> symbols, @NotNull String identifier) {
    Preconditions.checkNotNull(symbols);
    Preconditions.checkNotNull(identifier);
    for (ISymbol symbol : symbols) {
      if (this.nameEquals(symbol, identifier)) return true;
    }
    return false;
  }

  protected boolean nameEquals(@NotNull ISymbol symbol, @NotNull String identifier) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkNotNull(identifier);
    Preconditions.checkNotNull(symbol.getName());
    return symbol.getName().equals(identifier);
  }

  public String getCurrentStateName() {
    return this.currentStateName;
  }
}

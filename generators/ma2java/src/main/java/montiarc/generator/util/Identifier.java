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

  public Identifier(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);
    this.updateNameMapping(component);
  }

  /**
   * Used in freemarker templates to create a new identifier instance (needed, e.g., when generating inner components)
   *
   * @param component ComponentTypeSymbol used as constructor parameter
   * @return new Identifier instance
   */
  public static Identifier getNewIdentifier(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);
    return new Identifier(component);
  }

  protected final static String RESULT_NAME = "result";

  protected final static String INPUT_NAME = "input";

  protected final static String BEHAVIOR_IMPL_NAME = "behaviorImpl";

  protected final static String CURRENT_STATE_NAME = "currentState";

  protected final static String PREFIX = "r__";

  protected String resultName = RESULT_NAME;

  protected String inputName = INPUT_NAME;

  protected String behaviorImplName = BEHAVIOR_IMPL_NAME;

  protected String currentStateName = CURRENT_STATE_NAME;

  protected void setResultName(@NotNull String resultName) {
    Preconditions.checkNotNull(resultName);
    this.resultName = resultName;
  }

  protected void setInputName(@NotNull String inputName) {
    Preconditions.checkNotNull(inputName);
    this.inputName = inputName;
  }

  protected void setBehaviorImplName(@NotNull String behaviorImplName) {
    Preconditions.checkNotNull(behaviorImplName);
    this.behaviorImplName = behaviorImplName;
  }

  protected void setCurrentStateName(@NotNull String currentStateName) {
    Preconditions.checkNotNull(currentStateName);
    this.currentStateName = currentStateName;
  }

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

  public String getBehaviorImplName() {
    return this.behaviorImplName;
  }

  public String getResultName() {
    return this.resultName;
  }

  public String getInputName() {
    return this.inputName;
  }

  public String getCurrentStateName() {
    return this.currentStateName;
  }

  protected void updateNameMapping(@NotNull ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);
    if (this.containsIdentifier(component, RESULT_NAME)) {
      this.setResultName(PREFIX + RESULT_NAME);
    }
    if (this.containsIdentifier(component, INPUT_NAME)) {
      this.setInputName(PREFIX + INPUT_NAME);
    }
    if (this.containsIdentifier(component, BEHAVIOR_IMPL_NAME)) {
      this.setBehaviorImplName(PREFIX + BEHAVIOR_IMPL_NAME);
    }
    if (this.containsIdentifier(component, CURRENT_STATE_NAME)) {
      this.setCurrentStateName(PREFIX + CURRENT_STATE_NAME);
    }
  }
}
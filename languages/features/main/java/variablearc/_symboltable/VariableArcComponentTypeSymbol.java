/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.presence.IPresenceFormulasOwner;
import variablearc.presence.PresenceFormulas;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VariableArcComponentTypeSymbol extends ComponentTypeSymbol implements IVariableArcComponentTypeSymbol, IPresenceFormulasOwner {

  protected PresenceFormulas presenceFormulas;
  protected final Map<String, ASTExpression> parameterDefaults = new LinkedHashMap<>();

  /**
   * @param name the name of this component type.
   */
  protected VariableArcComponentTypeSymbol(String name) {
    super(name);
  }

  @Override
  public ComponentTypeSymbol getTypeInfo() {
    return this;
  }

  @Override
  public List<VariableSymbol> getFields() {
    return super.getFields().stream()
      .filter(f -> !(f instanceof ArcFeature2VariableAdapter))
      .collect(Collectors.toList());
  }

  @Override
  public Optional<PresenceFormulas> getPresenceFormulas() {
    return Optional.ofNullable(this.presenceFormulas);
  }

  @Override
  public void setPresenceFormulas(@NotNull PresenceFormulas formulas) {
    this.presenceFormulas = Preconditions.checkNotNull(formulas);
  }

  @Override
  public Optional<ASTExpression> getParameterDefault(@NotNull String parameter) {
    Preconditions.checkNotNull(parameter);
    return Optional.ofNullable(this.parameterDefaults.get(parameter));
  }

  @Override
  public void setParameterDefault(@NotNull String parameter, @NotNull ASTExpression value) {
    Preconditions.checkNotNull(parameter);
    Preconditions.checkNotNull(value);
    this.parameterDefaults.put(parameter, value);
  }
}

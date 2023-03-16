/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComponentInstanceSymbol extends ComponentInstanceSymbolTOP {

  protected CompTypeExpression type;
  protected List<ASTArcArgument> arguments;
  protected ImmutableMap<VariableSymbol, ASTArcArgument> parameterVarBindings;

  /**
   * @param name the name of this component.
   */
  public ComponentInstanceSymbol(String name) {
    super(name);
    this.arguments = new ArrayList<>();
    this.parameterVarBindings = ImmutableMap.<VariableSymbol, ASTArcArgument>builder().build();
  }

  /**
   * @return the type of this component
   */
  public CompTypeExpression getType() {
    Preconditions.checkState(this.type != null,
      "Type of component instance '%s' has not been set yet.", this.getFullName());
    return this.type;
  }

  /**
   * @return whether the type for this component instance has already been set.
   */
  public boolean isPresentType() {
    return this.type != null;
  }

  /**
   * @param type the type of this component
   */
  public void setType(CompTypeExpression type) {
    this.type = type;
  }

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTArcArgument> getArcArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArcArgument(@NotNull ASTArcArgument argument) {
    Preconditions.checkNotNull(argument);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArcArgument(ASTArcArgument)
   */
  public void addArcArguments(@NotNull List<ASTArcArgument> arguments) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTArcArgument argument : arguments) {
      this.addArcArgument(argument);
    }
  }

  public Optional<ASTArcArgument> getBindingFor(@NotNull VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParameterBindings().get(var));
  }

  public ImmutableMap<VariableSymbol, ASTArcArgument> getParameterBindings() {
    Preconditions.checkNotNull(parameterVarBindings);
    return parameterVarBindings;
  }


  public ImmutableList<ASTArcArgument> getBindingsAsList() {
    Preconditions.checkNotNull(parameterVarBindings);
    // We know guava immutable maps are ordered and thus .values represents the order of the arguments
    ImmutableList.Builder<ASTArcArgument> builder = ImmutableList.builder();
    builder.addAll(this.getParameterBindings().values());
    return builder.build();
  }

  public void bindParameters() {
    List<ASTArcArgument> parameterArguments = this.getArcArguments();

    int firstKeywordArgument = 0;
    Map<String, ASTArcArgument> keywordExpressionMap = new HashMap<>();
    ImmutableMap.Builder<VariableSymbol, ASTArcArgument> parameterVarBindingBuilder = ImmutableMap.builder();
    // We know guava immutable maps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getType().getTypeInfo().getParameters().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        if (!parameterArguments.get(i).isPresentName()) {
          parameterVarBindingBuilder.put(this.getType().getTypeInfo().getParameters().get(i), parameterArguments.get(i));
          firstKeywordArgument++;
        } else {
          keywordExpressionMap.put(parameterArguments.get(i).getName(), parameterArguments.get(i));
        }
    }

    // iterate over keyword-based arguments (CoCo assures that no position-based argument occurs
    // after the first keyword-based argument)
    for (int j = firstKeywordArgument; j < this.getType().getTypeInfo().getParameters().size(); j++) {
      if (keywordExpressionMap.containsKey(this.getType().getTypeInfo().getParameters().get(j).getName())) {
        parameterVarBindingBuilder.put(this.getType().getTypeInfo().getParameters().get(j),
          keywordExpressionMap.get(this.getType().getTypeInfo().getParameters().get(j).getName()));
      }
    }

    this.parameterVarBindings = parameterVarBindingBuilder.build();
  }
}

/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTArcVariableDeclarationBuilderTOP} with utility functions for easy
 * constructor of {@link ASTArcVariableDeclaration} nodes.
 */
public class ASTArcVariableDeclarationBuilder extends ASTArcVariableDeclarationBuilderTOP {

  protected ASTArcVariableDeclarationBuilder() {
    super();
  }
  
  /**
   * Creates a {@link ASTArcVariable} to be used by this builder corresponding the the provided
   * {@code String} argument and adds it to the list of declared variables at the given index. The
   * provided {@code String} argument is expected to be not null and a simple name (no parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index of the variable
   * @param variable  name of the variable
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTArcVariableDeclarationBuilder setVariable(int index, String variable) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(variable);
    Preconditions.checkArgument(!variable.contains("."));
    this.setVariable(index, this.doCreateVariable(variable));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTArcVariable} to be used by this builder corresponding to the provided
   * {@code String} arguments. The provided {@code String} arguments are expected to be not null
   * and a simple names (no parts separated by dots ".").
   *
   * @param variables names of the variables
   * @return this builder
   */
  public ASTArcVariableDeclarationBuilder setVariableList(String... variables) {
    Preconditions.checkNotNull(variables);
    this.setVariableList(this.doCreateVariableList(variables));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcVariable} corresponding to the provided {@code String} argument and
   * adds it to the list of variables to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots ".").
   *
   * @param variable name of the variable to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTArcVariableDeclarationBuilder addVariable(String variable) {
    Preconditions.checkNotNull(variable);
    Preconditions.checkArgument(!variable.contains("."));
    this.addVariable(this.doCreateVariable(variable));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcVariable} corresponding to the provided {@code String} arguments and
   * adds it to the list of variables to be used by this builder. The provided {@code String}
   * arguments are expected to be not null and a simple names (no parts separated by dots ".").
   *
   * @param variables names of the variables to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTArcVariableDeclarationBuilder addAllVariables(String... variables) {
    this.addAllVariables(this.doCreateVariableList(variables));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcVariable} corresponding to the provided {@code String} argument and, at the
   * given index, adds it to the list of variables to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots "."). The
   * index is expected to be zero or greater.
   *
   * @param index index at where to add the variable
   * @param variable  name of the variable to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTArcVariableDeclarationBuilder addVariable(int index, String variable) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(variable);
    Preconditions.checkArgument(!variable.contains("."));
    this.addVariable(index, this.doCreateVariable(variable));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcVariable} corresponding to the provided {@code String} arguments and,
   * at the given index, adds it to the list of variables to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a simple names (no parts separated
   * by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the variables
   * @param variables names of the variables to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTArcVariableDeclarationBuilder addAllVariables(int index, String... variables) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(variables);
    this.addAllVariables(index, this.doCreateVariableList(variables));
    return this.realBuilder;
  }

  protected ASTArcVariable doCreateVariable(String variable) {
    return ArcMill.arcVariableBuilder().setName(variable).build();
  }

  protected List<ASTArcVariable> doCreateVariableList(String... variables) {
    List<ASTArcVariable> variableList = new ArrayList<>();
    for (String variable : variables) {
      variableList.add(this.doCreateVariable(variable));
    }
    return variableList;
  }
}
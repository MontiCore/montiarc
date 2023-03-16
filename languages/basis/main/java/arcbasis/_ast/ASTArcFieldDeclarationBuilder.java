/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTArcFieldDeclarationBuilderTOP} with utility functions for easy
 * constructor of {@link ASTArcFieldDeclaration} nodes.
 */
public class ASTArcFieldDeclarationBuilder extends ASTArcFieldDeclarationBuilderTOP {

  public ASTArcFieldDeclarationBuilder() {
    super();
  }

  /**
   * Creates a {@link ASTArcField} to be used by this builder corresponding the provided
   * {@code String} argument and adds it to the list of declared fields at the given index. The
   * provided {@code String} argument is expected to be not null and a simple name (no parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index of the field
   * @param name name of the field
   * @param value initial value of the field
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTArcFieldDeclarationBuilder setArcFieldList(int index, @NotNull String name, @NotNull ASTExpression value) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isEmpty());
    Preconditions.checkNotNull(value);
    this.setArcField(index, this.doCreateArcField(name, value));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTArcField} to be used by this builder corresponding to the provided
   * {@code String} arguments. The provided {@code String} arguments are expected to be not null
   * and a simple names (no parts separated by dots ".").
   *
   * @param names names of the fields
   * @param values initial values of the fields
   * @return this builder
   */
  public ASTArcFieldDeclarationBuilder setArcFieldList(@NotNull String[] names, @NotNull ASTExpression[] values) {
    Preconditions.checkNotNull(names);
    Preconditions.checkNotNull(values);
    this.setArcFieldsList(this.doCreateArcFieldList(names, values));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcField} corresponding to the provided {@code String} argument and
   * adds it to the list of fields to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots ".").
   *
   * @param name name of the field to be added
   * @param value initial value of field to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTArcFieldDeclarationBuilder addArcField(@NotNull String name, @NotNull ASTExpression value) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isEmpty());
    Preconditions.checkNotNull(value);
    this.addArcField(this.doCreateArcField(name, value));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcField} corresponding to the provided {@code String} arguments and
   * adds it to the list of fields to be used by this builder. The provided {@code String}
   * arguments are expected to be not null and a simple names (no parts separated by dots ".").
   *
   * @param names names of the fields to be added
   * @param values values of the fields to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTArcFieldDeclarationBuilder addAllArcFields(@NotNull String[] names, @NotNull ASTExpression[] values) {
    Preconditions.checkNotNull(names);
    Preconditions.checkNotNull(values);
    Preconditions.checkArgument(names.length == values.length);
    this.addAllArcFields(this.doCreateArcFieldList(names, values));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcField} corresponding to the provided {@code String} argument and, at the
   * given index, adds it to the list of fields to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots "."). The
   * index is expected to be zero or greater.
   *
   * @param index index at where to add the field
   * @param name name of the field to be added
   * @param value value of the field to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTArcFieldDeclarationBuilder addArcField(int index, @NotNull String name, @NotNull ASTExpression value) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isEmpty());
    Preconditions.checkNotNull(value);
    this.addArcField(index, this.doCreateArcField(name, value));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcField} corresponding to the provided {@code String} arguments and,
   * at the given index, adds it to the list of fields to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a simple names (no parts separated
   * by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the fields
   * @param names names of the fields to be added
   * @param values values of the fields to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTArcFieldDeclarationBuilder addAllArcFields(int index, @NotNull String[] names,
                                                       @NotNull ASTExpression[] values) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(names);
    Preconditions.checkNotNull(values);
    this.addAllArcFields(index, this.doCreateArcFieldList(names, values));
    return this.realBuilder;
  }

  protected ASTArcField doCreateArcField(@NotNull String name, @NotNull ASTExpression value) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(value);
    return ArcBasisMill.arcFieldBuilder().setName(name).setInitial(value).build();
  }

  protected List<ASTArcField> doCreateArcFieldList(@NotNull String[] names, @NotNull ASTExpression[] values) {
    Preconditions.checkNotNull(names);
    Preconditions.checkNotNull(values);
    List<ASTArcField> fieldList = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      fieldList.add(this.doCreateArcField(names[i], values[i]));
    }
    return fieldList;
  }
}
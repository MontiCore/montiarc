/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import java.util.ArrayList;
import java.util.Arrays;
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
   * Creates a {@link ASTArcField} to be used by this builder corresponding the the provided
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
  public ASTArcFieldDeclarationBuilder setField(int index, String name, ASTExpression value) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkArgument(name != null);
    Preconditions.checkArgument(!name.contains("\\."));
    Preconditions.checkArgument(value != null);
    this.setField(index, this.doCreateField(name, value));
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
  public ASTArcFieldDeclarationBuilder setFieldList(String[] names, ASTExpression[] values) {
    Preconditions.checkNotNull(names);
    this.setFieldList(this.doCreateFieldList(names, values));
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
  public ASTArcFieldDeclarationBuilder addField(String name, ASTExpression value) {
    Preconditions.checkArgument(name != null);
    Preconditions.checkArgument(!name.contains("\\."));
    this.addField(this.doCreateField(name, value));
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
  public ASTArcFieldDeclarationBuilder addAllFields(String[] names, ASTExpression[] values) {
    Preconditions.checkArgument(names != null);
    Preconditions.checkArgument(!Arrays.asList(names).contains(null));
    Preconditions.checkArgument(values != null);
    Preconditions.checkArgument(!Arrays.asList(values).contains(null));
    Preconditions.checkArgument(names.length == values.length);
    this.addAllFields(this.doCreateFieldList(names, values));
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
  public ASTArcFieldDeclarationBuilder addField(int index, String name, ASTExpression value) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkArgument(name != null);
    Preconditions.checkArgument(!name.contains("\\."));
    this.addField(index, this.doCreateField(name, value));
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
  public ASTArcFieldDeclarationBuilder addAllFields(int index, String[] names,
    ASTExpression[] values) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkArgument(names != null);
    Preconditions.checkArgument(!Arrays.asList(names).contains(null));
    Preconditions.checkArgument(values != null);
    Preconditions.checkArgument(!Arrays.asList(values).contains(null));
    this.addAllFields(index, this.doCreateFieldList(names, values));
    return this.realBuilder;
  }

  protected ASTArcField doCreateField(String name, ASTExpression value) {
    return ArcBasisMill.arcFieldBuilder().setName(name).setValue(value).build();
  }

  protected List<ASTArcField> doCreateFieldList(String[] names, ASTExpression[] values) {
    List<ASTArcField> fieldList = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      fieldList.add(this.doCreateField(names[i], values[i]));
    }
    return fieldList;
  }
}
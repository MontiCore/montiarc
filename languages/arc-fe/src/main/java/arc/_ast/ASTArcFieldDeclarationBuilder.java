/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTArcFieldDeclarationBuilderTOP} with utility functions for easy
 * constructor of {@link ASTArcFieldDeclaration} nodes.
 */
public class ASTArcFieldDeclarationBuilder extends ASTArcFieldDeclarationBuilderTOP {

  protected ASTArcFieldDeclarationBuilder() {
    super();
  }
  
  /**
   * Creates a {@link ASTArcField} to be used by this builder corresponding the the provided
   * {@code String} argument and adds it to the list of declared fields at the given index. The
   * provided {@code String} argument is expected to be not null and a simple name (no parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index of the field
   * @param field  name of the field
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTArcFieldDeclarationBuilder setField(int index, String field) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(field);
    Preconditions.checkArgument(!field.contains("."));
    this.setField(index, this.doCreateField(field));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTArcField} to be used by this builder corresponding to the provided
   * {@code String} arguments. The provided {@code String} arguments are expected to be not null
   * and a simple names (no parts separated by dots ".").
   *
   * @param fields names of the fields
   * @return this builder
   */
  public ASTArcFieldDeclarationBuilder setFieldList(String... fields) {
    Preconditions.checkNotNull(fields);
    this.setFieldList(this.doCreateFieldList(fields));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcField} corresponding to the provided {@code String} argument and
   * adds it to the list of fields to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots ".").
   *
   * @param field name of the field to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTArcFieldDeclarationBuilder addField(String field) {
    Preconditions.checkNotNull(field);
    Preconditions.checkArgument(!field.contains("."));
    this.addField(this.doCreateField(field));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcField} corresponding to the provided {@code String} arguments and
   * adds it to the list of fields to be used by this builder. The provided {@code String}
   * arguments are expected to be not null and a simple names (no parts separated by dots ".").
   *
   * @param fields names of the fields to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTArcFieldDeclarationBuilder addAllFields(String... fields) {
    this.addAllFields(this.doCreateFieldList(fields));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTArcField} corresponding to the provided {@code String} argument and, at the
   * given index, adds it to the list of fields to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots "."). The
   * index is expected to be zero or greater.
   *
   * @param index index at where to add the field
   * @param field  name of the field to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTArcFieldDeclarationBuilder addField(int index, String field) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(field);
    Preconditions.checkArgument(!field.contains("."));
    this.addField(index, this.doCreateField(field));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTArcField} corresponding to the provided {@code String} arguments and,
   * at the given index, adds it to the list of fields to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a simple names (no parts separated
   * by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the fields
   * @param fields names of the fields to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTArcFieldDeclarationBuilder addAllFields(int index, String... fields) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(fields);
    this.addAllFields(index, this.doCreateFieldList(fields));
    return this.realBuilder;
  }

  protected ASTArcField doCreateField(String field) {
    return ArcMill.arcFieldBuilder().setName(field).build();
  }

  protected List<ASTArcField> doCreateFieldList(String... fields) {
    List<ASTArcField> fieldList = new ArrayList<>();
    for (String field : fields) {
      fieldList.add(this.doCreateField(field));
    }
    return fieldList;
  }
}
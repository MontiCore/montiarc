/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTComponentTypeBuilderTOP} with utility functions for easy constructor of
 * {@link ASTComponentType} nodes.
 */
public class ASTComponentTypeBuilder extends ASTComponentTypeBuilderTOP {
  
  public ASTComponentTypeBuilder() {
    super();
  }
  
  /**
   * Creates a {@link ASTComponentInstance} to be used by this builder corresponding the the provided
   * {@code String} argument and adds it to the list of instances at the given index. The 
   * provided {@code String} argument is expected to be not null and a simple name (no parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index where to set the component instance
   * @param instance  name of the component instance
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTComponentTypeBuilder setInstance(int index, String instance) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(!instance.contains("."));
    this.setComponentInstance(index, this.doCreateInstance(instance));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTComponentInstance} to be used by this builder corresponding to the provided
   * {@code String} arguments. The provided {@code String} arguments are expected to be not null
   * and a simple names (no parts separated by dots ".").
   *
   * @param instances names of the instances
   * @return this builder
   */
  public ASTComponentTypeBuilder setComponentInstanceList(String... instances) {
    Preconditions.checkNotNull(instances);
    this.setComponentInstancesList(this.doCreateInstanceList(instances));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTComponentInstance} corresponding to the provided {@code String} argument and
   * adds it to the list of instances to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots ".").
   *
   * @param instance name of the instance to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTComponentTypeBuilder addInstance(String instance) {
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(!instance.contains("."));
    this.addComponentInstance(this.doCreateInstance(instance));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTComponentInstance} corresponding to the provided {@code String} arguments and
   * adds it to the list of instances to be used by this builder. The provided {@code String}
   * arguments are expected to be not null and a simple names (no parts separated by dots ".").
   *
   * @param instances names of the instances to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTComponentTypeBuilder addAllInstances(String... instances) {
    this.addAllComponentInstances(this.doCreateInstanceList(instances));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTComponentInstance} corresponding to the provided {@code String} argument and, at the
   * given index, adds it to the list of instances to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots "."). The
   * index is expected to be zero or greater.
   *
   * @param index index at where to add the instance
   * @param instance  name of the instance to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTComponentTypeBuilder addInstance(int index, String instance) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(instance);
    Preconditions.checkArgument(!instance.contains("."));
    this.addComponentInstance(index, this.doCreateInstance(instance));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTComponentInstance} corresponding to the provided {@code String} arguments and,
   * at the given index, adds it to the list of instances to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a simple names (no parts separated
   * by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the instances
   * @param instances names of the instances to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTComponentTypeBuilder addAllInstances(int index, String... instances) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(instances);
    this.addAllComponentInstances(index, this.doCreateInstanceList(instances));
    return this.realBuilder;
  }

  protected ASTComponentInstance doCreateInstance(String instance) {
    return ArcBasisMill.componentInstanceBuilder().setName(instance).build();
  }

  protected List<ASTComponentInstance> doCreateInstanceList(String... instances) {
    List<ASTComponentInstance> instanceList = new ArrayList<>();
    for (String instance : instances) {
      instanceList.add(this.doCreateInstance(instance));
    }
    return instanceList;
  }
}
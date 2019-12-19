/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTConnectorBuilderTOP} with utility functions for easy constructor of
 * {@link ASTConnector} nodes.
 */
public class ASTConnectorBuilder extends ASTConnectorBuilderTOP {

  protected ASTConnectorBuilder() {
    super();
  }

  /**
   * Creates a {@link ASTPortReference} to be used by this builder corresponding the the provided
   * {@code String} argument. The string argument is expected to be not null and a qualified name
   * (parts separated by dots ".").
   *
   * @param source qualified name of the source port
   * @return this builder
   */
  public ASTConnectorBuilder setSource(String source) {
    Preconditions.checkNotNull(source);
    this.source = ArcMill.portReferenceBuilder().setQualifiedName(source).build();
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTPortReference} to be used by this builder corresponding the the provided
   * {@code String} argument and adds it to the list of target ports at the given index. The
   * provided {@code String} argument is expected to be not null and a qualified name (parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index  index of the target port
   * @param target qualified name of the target port
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTConnectorBuilder setTarget(int index, String target) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(target);
    this.setTarget(index, this.doCreateTarget(target));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTPortReference} to be used by this builder corresponding to the
   * provided {@code String} arguments. The provided {@code String} arguments are expected to be
   * not null and qualified names (parts separated by dots ".").
   *
   * @param targets qualified names of the target ports
   * @return this builder
   */
  public ASTConnectorBuilder setTargetList(String... targets) {
    Preconditions.checkNotNull(targets);
    this.setTargetList(this.doCreateTargetList(targets));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTPortReference} corresponding to the provided {@code String} argument and
   * adds it to the list of targets to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a qualified name (parts separated by dots ".").
   *
   * @param target name of the target port to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTConnectorBuilder addTarget(String target) {
    Preconditions.checkNotNull(target);
    this.addTarget(this.doCreateTarget(target));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTPortReference} corresponding to the provided {@code String}
   * arguments and adds it to the list of targets to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a qualified names (parts separated
   * by dots ".").
   *
   * @param targets names of the target ports to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTConnectorBuilder addAllTargets(String... targets) {
    Preconditions.checkNotNull(targets);
    this.addAllTargets(this.doCreateTargetList(targets));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTPortReference} corresponding to the provided {@code String} argument
   * and, at the given index, adds it to the list of targets to be used by this builder. The
   * provided {@code String} argument is expected to be not null and a qualified name (parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the target port
   * @param target  qualified name of the target port to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTConnectorBuilder addTarget(int index, String target) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(target);
    this.addTarget(index, this.doCreateTarget(target));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTPortReference} corresponding to the provided {@code String}
   * arguments and, at the given index, adds it to the list of targets to be used by this builder.
   * The provided {@code String} arguments are expected to be not null and a qualified names
   * (parts separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the target ports
   * @param targets qualified names of the target ports to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTConnectorBuilder addAllTargets(int index, String... targets) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(targets);
    this.addAllTargets(index, doCreateTargetList(targets));
    return this.realBuilder;
  }

  protected ASTPortReference doCreateTarget(String target) {
    return ArcMill.portReferenceBuilder().setQualifiedName(target).build();
  }

  protected List<ASTPortReference> doCreateTargetList(String... targets) {
    List<ASTPortReference> targetList = new ArrayList<>();
    for (String target : targets) {
      ASTPortReference portRef = ArcMill.portReferenceBuilder().setQualifiedName(target).build();
      targetList.add(portRef);
    }
    return targetList;
  }
}
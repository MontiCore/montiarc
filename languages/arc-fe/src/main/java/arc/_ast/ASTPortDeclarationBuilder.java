/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Extends the {@link ASTPortDeclarationBuilderTOP} with utility functions for easy constructor of
 * {@link ASTPortDeclaration} nodes.
 */
public class ASTPortDeclarationBuilder extends ASTPortDeclarationBuilderTOP {

  protected ASTPortDeclarationBuilder() {
    super();
  }

  /**
   * Sets the direction to be used in this builder to incoming if the {@code boolean} argument is
   * true and outgoing if it is false.
   *
   * @param incoming states if the direction of the port is incoming
   * @return this builder
   */
  public ASTPortDeclarationBuilder setIncoming(boolean incoming) {
    this.doSetIncoming(incoming);
    return this.realBuilder;
  }

  /**
   * Sets the direction to be used in this builder to outgoing if the {@code boolean} argument is
   * true and incoming if it is false.
   *
   * @param outgoing states if the direction of the port is outgoing
   * @return this builder
   */
  public ASTPortDeclarationBuilder setOutgoing(boolean outgoing) {
    this.doSetIncoming(!outgoing);
    return this.realBuilder;
  }

  protected void doSetIncoming(boolean incoming) {
    if (incoming) {
      this.direction = "in";
    }
    else {
      this.direction = "out";
    }
  }

  /**
   * Creates a {@link ASTPort} to be used by this builder corresponding the the provided
   * {@code String} argument and adds it to the list of declared ports at the given index. The
   * provided {@code String} argument is expected to be not null and a simple name (no parts
   * separated by dots "."). The index is expected to be zero or greater.
   *
   * @param index index of the port
   * @param port  name of the port
   * @return this builder
   * @see List#set(int, Object)
   */
  public ASTPortDeclarationBuilder setPort(int index, String port) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(port);
    Preconditions.checkArgument(!port.contains("."));
    this.setPort(index, this.doCreatePort(port));
    return this.realBuilder;
  }

  /**
   * Creates the list of {@link ASTPort} to be used by this builder corresponding to the provided
   * {@code String} arguments. The provided {@code String} arguments are expected to be not null
   * and a simple names (no parts separated by dots ".").
   *
   * @param ports names of the ports
   * @return this builder
   */
  public ASTPortDeclarationBuilder setPortList(String... ports) {
    Preconditions.checkNotNull(ports);
    this.setPortList(this.doCreatePortList(ports));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTPort} corresponding to the provided {@code String} argument and
   * adds it to the list of ports to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots ".").
   *
   * @param port name of the port to be added
   * @return this builder
   * @see List#add(Object)
   */
  public ASTPortDeclarationBuilder addPort(String port) {
    Preconditions.checkNotNull(port);
    Preconditions.checkArgument(!port.contains("."));
    this.addPort(this.doCreatePort(port));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTPort} corresponding to the provided {@code String} arguments and
   * adds it to the list of ports to be used by this builder. The provided {@code String}
   * arguments are expected to be not null and a simple names (no parts separated by dots ".").
   *
   * @param ports names of the ports to be added
   * @return this builder
   * @see List#addAll(Collection)
   */
  public ASTPortDeclarationBuilder addAllPorts(String... ports) {
    this.addAllPorts(this.doCreatePortList(ports));
    return this.realBuilder;
  }

  /**
   * Creates a {@link ASTPort} corresponding to the provided {@code String} argument and, at the
   * given index, adds it to the list of ports to be used by this builder. The provided {@code String}
   * argument is expected to be not null and a simple name (no parts separated by dots "."). The
   * index is expected to be zero or greater.
   *
   * @param index index at where to add the port
   * @param port  name of the port to be added
   * @return this builder
   * @see List#add(int, Object)
   */
  public ASTPortDeclarationBuilder addPort(int index, String port) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(port);
    Preconditions.checkArgument(!port.contains("."));
    this.addPort(index, this.doCreatePort(port));
    return this.realBuilder;
  }

  /**
   * Creates a list of {@link ASTPort} corresponding to the provided {@code String} arguments and,
   * at the given index, adds it to the list of ports to be used by this builder. The provided
   * {@code String} arguments are expected to be not null and a simple names (no parts separated
   * by dots "."). The index is expected to be zero or greater.
   *
   * @param index index at where to add the ports
   * @param ports names of the ports to be added
   * @return this builder
   * @see List#addAll(int, Collection)
   */
  public ASTPortDeclarationBuilder addAllPorts(int index, String... ports) {
    Preconditions.checkArgument(index >= 0);
    Preconditions.checkNotNull(ports);
    this.addAllPorts(index, this.doCreatePortList(ports));
    return this.realBuilder;
  }

  protected ASTPort doCreatePort(String port) {
    return ArcMill.portBuilder().setName(port).build();
  }

  protected List<ASTPort> doCreatePortList(String... ports) {
    List<ASTPort> portList = new ArrayList<>();
    for (String port : ports) {
      portList.add(this.doCreatePort(port));
    }
    return portList;
  }
}
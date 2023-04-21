/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents component declarations. Extends {@link ASTComponentTypeTOP} with utility functions
 * for easy access.
 */
public class ASTComponentType extends ASTComponentTypeTOP {

  protected ASTComponentType() {
    super();
  }

  protected List<ASTArcElement> getArcElementList() {
    return this.getBody()
      .getArcElementList();
  }

  protected <T extends ASTArcElement> List<T> getArcElementListOfType(@NotNull Class<T> typeToSearch) {
    return this.getBody().getElementsOfType(typeToSearch);
  }

  /**
   * Returns a list of all port declarations contained in the body of this component in no specific
   * order. The list is empty if this component contains no port declarations.
   *
   * @return a {@code List} of all port declarations of this component
   */
  public List<ASTPortDeclaration> getPortDeclarations() {
    return getArcElementListOfType(ASTComponentInterface.class).stream()
      .map(ASTComponentInterface::getPortDeclarationList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all ports declared in port declarations of this component in no specific
   * order. The list is empty if the component contains no port declarations.
   *
   * @return a {@code List} of all ports declared in port declarations of this component.
   */
  public List<ASTPort> getPorts() {
    return this.getPortDeclarations()
      .stream()
      .map(ASTPortDeclaration::getPortList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of names of all ports declared in port declarations of this component in no
   * specific order. The list is empty if the component contains no port declarations.
   *
   * @return a {@code List} of names of all ports declared in port declarations of this component.
   */
  public List<String> getPortNames() {
    return this.getPorts().stream().map(ASTPort::getName).collect(Collectors.toList());
  }

  /**
   * Returns a list of all field declarations contained in the body of this component in no
   * specific order. The list is empty if this component contains no field declarations.
   *
   * @return a {@code List} of all field declarations of this component
   */
  public List<ASTArcFieldDeclaration> getFieldDeclarations() {
    return getArcElementListOfType(ASTArcFieldDeclaration.class);
  }

  /**
   * Returns a list of all fields declared in field declarations of this component in no
   * specific order. The list is empty if the component contains no field declarations.
   *
   * @return a {@code List} of all fields declared in field declarations of this component.
   */
  public List<ASTArcField> getFields() {
    return this.getFieldDeclarations()
      .stream()
      .map(ASTArcFieldDeclaration::getArcFieldList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of names of all fields declared in field declarations of this
   * component in no specific order. The list is empty if the component contains no field
   * declarations.
   *
   * @return a {@code List} of names of all fields declared in field declarations of this
   * component.
   */
  public List<String> getFieldNames() {
    return this.getFields().stream().map(ASTArcField::getName).collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component in no specific
   * order. The list is empty if the component contains no connectors.
   *
   * @return a {@code List} of all connectors contained in the topology of this component.
   */
  public List<ASTConnector> getConnectors() {
    return getArcElementListOfType(ASTConnector.class);
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with source
   * matching the provided {@code String} argument. The returned list is in no specific order and
   * empty if the component contains no connector with matching source.
   *
   * @param source the qualified name of the source port
   * @return a {@code List} of all connectors with matching source
   */
  public List<ASTConnector> getConnectorsMatchingSource(@NotNull String source) {
    Preconditions.checkNotNull(source);
    return this.getConnectors().stream()
      .filter(connector -> connector.getSource().getQName().equals(source))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with target
   * matching the provided {@code String} argument. The returned list is in no specific order and
   * empty if the component contains no connector with matching target.
   *
   * @param target the port access name (i.e., instance name and port name)
   * @return a {@code List} of all connectors with matching target
   */
  public List<ASTConnector> getConnectorsMatchingTarget(@NotNull String target) {
    Preconditions.checkNotNull(target);
    return this.getConnectors().stream()
      .filter(connector -> connector.getTargetList().stream()
        .anyMatch(t -> t.getQName().equals(target)))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with source
   * matching the provided port. The returned list is in no specific order and empty if the
   * component contains no connector with matching source.
   *
   * @param source the source port
   * @return a {@code List} of all connectors with matching source
   */
  public List<ASTConnector> getConnectorsMatchingSource(@NotNull PortSymbol source) {
    Preconditions.checkNotNull(source);
    return this.getConnectors().stream()
      .filter(connector -> connector.getSource().isPresentPortSymbol()
        && connector.getSource().getPortSymbol().equals(source))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with target
   * matching the provided port. The returned list is in no specific order and empty if the
   * component contains no connector with matching target.
   *
   * @param target the target port
   * @return a {@code List} of all connectors with matching target
   */
  public List<ASTConnector> getConnectorsMatchingTarget(@NotNull PortSymbol target) {
    Preconditions.checkNotNull(target);
    return this.getConnectors().stream()
      .filter(connector -> connector.getTargetList().stream()
        .anyMatch(t -> t.isPresentPortSymbol()
          && t.getPortSymbol().equals(target)))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with source
   * matching the provided subcomponent. The returned list is in no specific order and empty if the
   * component contains no connector with matching source.
   *
   * @param source the source subcomponent
   * @return a {@code List} of all connectors with matching source
   */
  public List<ASTConnector> getConnectorsMatchingSource(@NotNull ComponentInstanceSymbol source) {
    Preconditions.checkNotNull(source);
    return this.getConnectors().stream()
      .filter(connector -> connector.getSource().isPresentComponent()
        && connector.getSource().isPresentComponentSymbol()
        && connector.getSource().getComponentSymbol().equals(source))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with target
   * matching the provided subcomponent. The returned list is in no specific order and empty if the
   * component contains no connector with matching target.
   *
   * @param target the target subcomponent
   * @return a {@code List} of all connectors with matching target
   */
  public List<ASTConnector> getConnectorsMatchingTarget(@NotNull ComponentInstanceSymbol target) {
    Preconditions.checkNotNull(target);
    return this.getConnectors().stream()
      .filter(connector -> connector.getTargetList().stream()
        .anyMatch(t -> t.isPresentComponent()
          && t.isPresentComponentSymbol()
          && t.getComponentSymbol().equals(target)))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all in port forwards. The returned list is in no
   * specific order and empty if the component contains no in port forwards.
   *
   * @return a {@code List} of all in port forwards
   */
  public List<ASTConnector> getInPortForwards() {
    return this.getConnectors().stream()
      .filter(connector -> !connector.getSource().isPresentComponent())
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all out port forwards. The returned list is in no
   * specific order and empty if the component contains no out port forwards.
   *
   * @return a {@code List} of all out port forwards
   */
  public List<ASTConnector> getOutPortForwards() {
    return this.getConnectors().stream()
      .filter(connector -> connector.getTargetList().stream()
        .anyMatch(t -> !t.isPresentComponent()))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all subcomponent instantiations contained in the body of this component in
   * no specific order. The list is empty if the component contains no sub-components instantiations.
   *
   * @return a {@code List} of all subcomponent instantiations contained in the body of this
   * component.
   */
  public List<ASTComponentInstantiation> getSubComponentInstantiations() {
    return getArcElementListOfType(ASTComponentInstantiation.class);
  }

  /**
   * Returns a list of component instances of all sub-components contained in the topology of this
   * component in no specific order. The list is empty if the component contains no sub-components.
   *
   * @return a {@code List} of component instances of all sub-components contained in the topology
   * of this component.
   */
  public List<ASTComponentInstance> getSubComponents() {
    List<ASTComponentInstance> subComponents = new ArrayList<>();
    subComponents.addAll(this.getSubComponentInstantiations()
      .stream()
      .map(ASTComponentInstantiation::getComponentInstanceList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList()));
    subComponents.addAll(this.getInnerComponents()
      .stream()
      .map(ASTComponentType::getComponentInstanceList)
      .flatMap(Collection::stream)
      .collect(Collectors.toList()));
    return subComponents;
  }

  /**
   * Returns a list of names of all sub-components contained in the topology of this
   * component in no specific order. The list is empty if the component contains no sub-components.
   *
   * @return a {@code List} of names of all sub-components declared in port declarations of this
   * component.
   */
  public List<String> getSubComponentNames() {
    return this.getSubComponents().stream().map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all inner component definitions contained in the body of this component in
   * no specific order. The list is empty if the component contains no inner component definitions.
   *
   * @return a {@code List} of all inner component definitions contained in the body of this
   * component.
   */
  public List<ASTComponentType> getInnerComponents() {
    return getArcElementListOfType(ASTComponentType.class);
  }

  /**
   * Returns the name of the instance at the specified position of the instances declared
   * in this component definition.
   *
   * @param index index of the instance whose name to return
   * @return the name of the instance at the specified position
   * @see super#getComponentInstance(int)
   */
  public String getInstanceName(int index) {
    return this.getComponentInstance(index).getName();
  }

  /**
   * Returns a list of names of all instances declared in this component definition. The list is
   * empty if this component definition contains no instance declarations.
   *
   * @return a list of names of all instances declared in this component definition.
   * @see super#getComponentInstanceList()
   */
  public List<String> getInstancesNames() {
    return this.getComponentInstanceList().stream().map(ASTComponentInstance::getName)
      .collect(Collectors.toList());
  }
}
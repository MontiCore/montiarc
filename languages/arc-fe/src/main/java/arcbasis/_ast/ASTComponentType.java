/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents component declarations. Extends {@link ASTComponentType} with utility functions
 * for easy access.
 */
public class ASTComponentType extends ASTComponentTypeTOP {

  protected ASTComponentType() {
    super();
  }

  /**
   * Returns a list of all port declarations contained in the body of this component in no specific
   * order. The list is empty if this component contains no port declarations.
   *
   * @return a {@code List} of all port declarations of this component
   */
  public List<ASTPortDeclaration> getPortDeclarations() {
    return this.getBody()
      .getArcElementList()
      .stream()
      .filter(element -> element instanceof ASTComponentInterface)
      .map(compInterface -> ((ASTComponentInterface) compInterface).getPortList())
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
    return this.getBody()
      .getArcElementList()
      .stream()
      .filter(element -> element instanceof ASTArcFieldDeclaration)
      .map(fieldDec -> (ASTArcFieldDeclaration) fieldDec)
      .collect(Collectors.toList());
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
      .map(ASTArcFieldDeclaration::getFieldList)
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
    return this.getBody()
      .getArcElementList()
      .stream()
      .filter(element -> element instanceof ASTConnector)
      .map(connector -> (ASTConnector) connector)
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with source
   * matching the provided {@code String} argument. The returned list is in no specific order and
   * empty if the component contains no connector with matching source.
   *
   * @param source the qualified name of the source port
   * @return a {@code List} of all connectors with matching source
   */
  public List<ASTConnector> getConnectorsMatchingSource(String source) {
    return this.getConnectors().stream()
      .filter(connector -> connector.getSource().getQName().equals(source))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all connectors contained in the topology of this component and with target
   * matching the provided {@code String} argument. The returned list is in no specific order and
   * empty if the component contains no connector with matching target.
   *
   * @param target the qualified name of the target port
   * @return a {@code List} of all connectors with matching target
   */
  public List<ASTConnector> getConnectorsMatchingTarget(String target) {
    return this.getConnectors().stream()
      .filter(astConnector -> astConnector.getTargetList().stream()
        .anyMatch(astPortReference -> astPortReference.getQName().equals(target)))
      .collect(Collectors.toList());
  }

  /**
   * Returns a list of all sub-component instantiations contained in the body of this component in
   * no specific order. The list is empty if the component contains no sub-components instantiations.
   *
   * @return a {@code List} of all sub-component instantiations contained in the body of this
   * component.
   */
  public List<ASTComponentInstantiation> getSubComponentInstantiations() {
    return this.getBody()
      .getArcElementList()
      .stream()
      .filter(element -> element instanceof ASTComponentInstantiation)
      .map(subComponent -> (ASTComponentInstantiation) subComponent)
      .collect(Collectors.toList());
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
    return this.getBody()
      .getArcElementList()
      .stream()
      .filter(element -> element instanceof ASTComponentType)
      .map(innerComponent -> (ASTComponentType) innerComponent)
      .collect(Collectors.toList());
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
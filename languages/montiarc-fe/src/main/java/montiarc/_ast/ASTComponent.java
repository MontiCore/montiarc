/* (c) https://github.com/MontiCore/monticore */
package montiarc._ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.types.types._ast.ASTTypeArguments;

/**
 * Repräsentation einer Komponenten
 *
 */
public class ASTComponent extends ASTComponentTOP {
  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._ast.ASTComponent
   */
  public ASTComponent() {
    super();
  }
  
  /**
   * Constructor for montiarc._ast.ASTComponent
   *
   * @param stereotype
   * @param name
   * @param head
   * @param instanceName
   * @param actualTypeArgument
   * @param body
   */
  protected ASTComponent(
      Optional<ASTStereotype> stereotype,
      String name,
      ASTComponentHead head,
      Optional<String> instanceName,
      Optional<ASTTypeArguments> actualTypeArgument,
      ASTComponentBody body) {
    super(stereotype, name, head, instanceName, actualTypeArgument, body);
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTPort> getPorts() {
    List<ASTPort> ret = new ArrayList<>();
    for (ASTElement element : this.getBody().getElementList()) {
      if (element instanceof ASTInterface) {
        ret.addAll(((ASTInterface) element).getPortsList());
      }
    }
    return ret;
  }
  
  public List<ASTVariableDeclaration> getVariables() {
    List<ASTVariableDeclaration> ret = new ArrayList<>();
    for (ASTElement element : this.getBody().getElementList()) {
      if (element instanceof ASTVariableDeclaration) {
        ret.add((ASTVariableDeclaration) element);
      }
    }
    return ret;
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTConnector> getConnectors() {
    return this.getBody().getElementList().stream().filter(a -> a instanceof ASTConnector)
        .map(a -> (ASTConnector) a).collect(Collectors.toList());
  }
  
  // do not use symbol table, since symbol table must not be created
  public Optional<ASTConnector> getConnector(String target) {
    return getConnectors().stream()
        .filter(astConnector -> astConnector.getTargetsList().stream()
            .anyMatch(astTarget -> astTarget.toString().equals(target))).findFirst();
    
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTSubComponent> getSubComponents() {
    return this.getBody().getElementList().stream().filter(a -> a instanceof ASTSubComponent)
        .map(a -> (ASTSubComponent) a).collect(Collectors.toList());
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTComponent> getInnerComponents() {
    return this.getBody().getElementList().stream().filter(a -> a instanceof ASTComponent)
        .map(a -> (ASTComponent) a).collect(Collectors.toList());
  }
}

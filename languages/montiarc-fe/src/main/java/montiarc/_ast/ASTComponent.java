/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.types.types._ast.ASTTypeArguments;

/**
 * Repräsentation einer Komponenten
 *
 * @author Robert Heim, Michael von Wenckstern, Andreas Wortmann
 */
public class ASTComponent extends ASTComponentTOP {
  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._ast.ASTComponent
   */
  public ASTComponent() {
    super();
  }
  
  protected ASTComponent(
      ASTStereotype stereotype,
      String name,
      ASTComponentHead head,
      String instanceName,
      ASTTypeArguments actualTypeArgument,
      ASTComponentBody body) {
    super(stereotype, name, head, instanceName, actualTypeArgument, body);
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTPort> getPorts() {
    List<ASTPort> ret = new ArrayList<>();
    for (ASTElement element : this.getBody().getElements()) {
      if (element instanceof ASTInterface) {
        ret.addAll(((ASTInterface) element).getPorts());
      }
    }
    return ret;
  }
  
  public List<ASTVariableDeclaration> getVariables() {
    List<ASTVariableDeclaration> ret = new ArrayList<>();
    for (ASTElement element : this.getBody().getElements()) {
      if (element instanceof ASTVariableDeclaration) {
        ret.add((ASTVariableDeclaration) element);
      }
    }
    return ret;
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTConnector> getConnectors() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTConnector)
        .map(a -> (ASTConnector) a).collect(Collectors.toList());
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTSubComponent> getSubComponents() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTSubComponent)
        .map(a -> (ASTSubComponent) a).collect(Collectors.toList());
  }
  
  // do not use symbol table, since symbol table must not be created
  public List<ASTComponent> getInnerComponents() {
    return this.getBody().getElements().stream().filter(a -> a instanceof ASTComponent)
        .map(a -> (ASTComponent) a).collect(Collectors.toList());
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.generator.visitor;

import java.util.Optional;

import montiarc._ast.ASTJavaPBehavior;
import montiarc._visitor.MontiArcVisitor;

/**
 * Visitor for getting JavaPBehavior from AST.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class BehaviorVisitor implements MontiArcVisitor {
  
  Optional<ASTJavaPBehavior> javaPBehavior = Optional.empty();
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#visit(montiarc._ast.ASTJavaPBehavior)
   */
  @Override
  public void visit(ASTJavaPBehavior node) {
    javaPBehavior = Optional.of(node);
  }
  
  /**
   * @return javaPBehavior
   */
  public Optional<ASTJavaPBehavior> getJavaPBehavior() {
    return this.javaPBehavior;
  }
  
}

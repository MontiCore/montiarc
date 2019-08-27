/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.visitor;

import de.monticore.symboltable.Scope;
import dynamicmontiarc._visitor.DynamicMontiArcVisitor;

public class AssignmentNameCompleter extends montiarc.visitor.AssignmentNameCompleter implements DynamicMontiArcVisitor {

  public AssignmentNameCompleter(Scope automatonScope) {
    super(automatonScope);
  }
}

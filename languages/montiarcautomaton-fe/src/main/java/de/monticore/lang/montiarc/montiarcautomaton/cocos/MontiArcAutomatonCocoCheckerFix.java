package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;

public class MontiArcAutomatonCocoCheckerFix extends MontiArcAutomatonCoCoChecker {
  
  // TODO We need this fix because we want to check a ASTComponent which is a
  // ASTMontiArc and not a ASTMontiArcAutomaton. Therefore we can't use {@link
  // #checkAll(de.monticore.lang.montiarc.montiarcautomaton._ast.ASTMontiArcAutomatonNode)}
  // due to wrong type. 
  // TODO wait for new MC release before deleting this workaround
  
  public void checkAll(ASTMontiArcNode node) {
    node.accept(getRealThis());
  }
  
}

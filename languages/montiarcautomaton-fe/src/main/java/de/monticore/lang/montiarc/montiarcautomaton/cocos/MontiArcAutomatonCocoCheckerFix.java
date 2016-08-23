package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTMontiArcNode;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;

public class MontiArcAutomatonCocoCheckerFix extends MontiArcAutomatonCoCoChecker {
  
  public void checkAll(ASTMontiArcNode node) {
    node.accept(getRealThis());
  }
}

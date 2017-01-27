package de.montiarcautomaton.automatongenerator.cocos;

import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;

/**
 * Creates a coco checker for MotniArcAutomaton cocos and adds use-case specific
 * cocos for this generator. Implemented here because added cocos are allowed in
 * general MAA or IO-Automaton.
 * 
 * @author Gerrit Leonhardt
 */
public class MontiArcAutomatonCocos {
  public static MontiArcAutomatonCoCoChecker createChecker() {
    MontiArcAutomatonCoCoChecker checker = new MontiArcAutomatonCoCoChecker();
    checker.addChecker(de.monticore.lang.montiarc.montiarcautomaton.cocos.MontiArcAutomatonCocos.createChecker());
    
    // add generator specific cocos
    checker.addCoCo(new MultipleInitialStates());
    checker.addCoCo(new UseOfAlternatives());
    checker.addCoCo(new UseOfValueLists());
    
    return checker;
  }
}

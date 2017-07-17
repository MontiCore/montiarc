package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.automaton.ioautomaton.cocos.IOAutomatonCoCos;
import de.monticore.automaton.ioautomaton.cocos.IOAutomatonJavaCoCos;
import de.monticore.lang.montiarc.cocos.MontiArcCoCos;
import de.monticore.lang.montiarc.cocos.automaton.AutomatonHasInput;
import de.monticore.lang.montiarc.cocos.automaton.AutomatonHasOutput;
import de.monticore.lang.montiarc.cocos.automaton.AutomatonUppercase;
import de.monticore.lang.montiarc.cocos.automaton.ImplementationInNonAtomicComponent;
import de.monticore.lang.montiarc.cocos.automaton.MultipleBehaviorImplementation;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;

public class MontiArcAutomatonCocos {
  public static MontiArcAutomatonCoCoChecker createChecker() {
    MontiArcAutomatonCoCoChecker checker = new MontiArcAutomatonCoCoChecker();
    checker.addChecker(MontiArcCoCos.createChecker());
    
    // add all required IO-Automaton cocos
    checker.addChecker(IOAutomatonJavaCoCos.createChecker());
    checker.addChecker(IOAutomatonCoCos.createChecker());
    
    // add all MontiArcAutomaton specific cocos
    return checker
        .addCoCo(new AutomatonUppercase()) // reimplement AutomatonUppercase
        .addCoCo(new AutomatonHasInput()) // inputs forbidden
        .addCoCo(new AutomatonHasOutput()) // outputs forbidden
        .addCoCo(new ImplementationInNonAtomicComponent())
        .addCoCo(new MultipleBehaviorImplementation())
        ;
  }

}

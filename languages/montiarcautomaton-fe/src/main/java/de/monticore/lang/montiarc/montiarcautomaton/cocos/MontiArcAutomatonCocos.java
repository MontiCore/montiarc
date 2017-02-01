package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.automaton.ioautomaton.cocos.IOAutomatonCoCos;
import de.monticore.automaton.ioautomaton.cocos.IOAutomatonJavaCoCos;
import de.monticore.lang.montiarc.cocos.MontiArcCoCos;
import de.monticore.lang.montiarc.montiarcautomaton._cocos.MontiArcAutomatonCoCoChecker;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonHasInput;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonHasOutput;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.AutomatonUppercase;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.ImplementationInNonAtomicComponent;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions.MultipleBehaviorImplementation;

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

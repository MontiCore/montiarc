/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos;

import de.monticore.lang.montiarc.ajava._cocos.AJavaCoCoChecker;
import de.monticore.lang.montiarc.ajava.cocos.conventions.AJavaDefinitionUpperCaseCoCo;
import de.monticore.lang.montiarc.ajava.cocos.conventions.MultipleBehaviorImplementation;
import de.monticore.lang.montiarc.ajava.cocos.correctness.UsedPortsExistCoCo;
import de.monticore.lang.montiarc.cocos.MontiArcCoCos;
import de.monticore.lang.montiarc.montiarcautomaton.cocos.MontiArcAutomatonCocos;

/**
 * Bundle of CoCos for the AJava language.
 *
 * @author Jerome Pfeiffer
 */
public class AJavaCoCos {
  public static AJavaCoCoChecker createChecker() {
    AJavaCoCoChecker checker =  new AJavaCoCoChecker();
    checker
    .addCoCo(new UsedPortsExistCoCo())
    .addCoCo(new AJavaDefinitionUpperCaseCoCo())
    .addCoCo(new MultipleBehaviorImplementation());
    
    checker.addChecker(MontiArcAutomatonCocos.createChecker());
    
    return checker;
  }
}

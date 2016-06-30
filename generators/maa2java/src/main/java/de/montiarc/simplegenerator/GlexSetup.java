/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.simplegenerator;

import java.text.DateFormat;
import java.util.Date;

import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * Creates a {@link GlobalExtensionManagement} instance with standard global values set.
 *
 * @author Robert Heim
 */
public class GlexSetup {
  private GlexSetup() {
  }
  
  /**
   * Creates a {@link GlobalExtensionManagement} instance with standard global values set.
   * 
   * @return the initialized glex instance
   */
  public static GlobalExtensionManagement create() {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.addGlobalValue("MontiArcGeneratorVersion", MontiArcGeneratorConstants.VERSION);
    Date now = new Date(System.currentTimeMillis());
    glex.addGlobalValue("TIME_NOW", DateFormat.getDateTimeInstance().format(now));
    glex.addGlobalValue("AComponent", MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME);
    glex.addGlobalValue("TIME_PARADIGM_STORAGE_KEY",
        MontiArcGeneratorConstants.TIME_PARADIGM_STORAGE_KEY);
        
    glex.addGlobalValue("CODEGEN_TIME_IN_PORTNAME",
        MontiArcGeneratorConstants.CODEGEN_TIME_IN_PORTNAME);
    return glex;
  }
}

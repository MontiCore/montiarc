/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator;

import java.text.DateFormat;
import java.util.Date;

import de.montiarc.generator.codegen.PortHelper;
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
    glex.addGlobalValue("IInPort", MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME);
    glex.addGlobalValue("IInSimPort", MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME);
    glex.addGlobalValue("IOutSimPort", MontiArcGeneratorConstants.OUT_SIM_PORT_INTERFACE_NAME);
    glex.addGlobalValue("IOutPort", MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME);
    glex.addGlobalValue("IPort", MontiArcGeneratorConstants.PORT_INTERFACE_NAME);
    glex.addGlobalValue("ISimComponent", MontiArcGeneratorConstants.SIM_COMPONENT_INTERFACE_NAME);
    glex.addGlobalValue("IComponent", MontiArcGeneratorConstants.COMPONENT_INTERFACE_NAME);
    glex.addGlobalValue("AComponent", MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME);
    glex.addGlobalValue("ATimedComponent", MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME);
    glex.addGlobalValue("IScheduler", MontiArcGeneratorConstants.SCHEDULER_INTERFACE_NAME);
    glex.addGlobalValue("ISimulationErrorHandler", MontiArcGeneratorConstants.SIM_ERROR_HANDLER_INTERFACE_NAME);
    glex.addGlobalValue("SimpleInPortInterface", MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME);
    glex.addGlobalValue("TIME_PARADIGM_STORAGE_KEY",
        MontiArcGeneratorConstants.TIME_PARADIGM_STORAGE_KEY);
    glex.addGlobalValue("portHelper", new PortHelper());
    glex.addGlobalValue("IForwardPort", MontiArcGeneratorConstants.FORWARD_PORT_INTERFACE_NAME);
    glex.addGlobalValue("DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX", MontiArcGeneratorConstants.DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX);    
    glex.addGlobalValue("CODEGEN_TIME_IN_PORTNAME",
        MontiArcGeneratorConstants.CODEGEN_TIME_IN_PORTNAME);
    return glex;
  }
}

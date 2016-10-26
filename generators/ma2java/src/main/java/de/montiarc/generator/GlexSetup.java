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
    glex.defineGlobalVar("MontiArcGeneratorVersion", MontiArcGeneratorConstants.VERSION);
    Date now = new Date(System.currentTimeMillis());
    glex.defineGlobalVar("TIME_NOW", DateFormat.getDateTimeInstance().format(now));
    glex.defineGlobalVar("IInPort", MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("IInSimPort", MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("IOutSimPort", MontiArcGeneratorConstants.OUT_SIM_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("IOutPort", MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("IPort", MontiArcGeneratorConstants.PORT_INTERFACE_NAME);
    glex.defineGlobalVar("ISimComponent", MontiArcGeneratorConstants.SIM_COMPONENT_INTERFACE_NAME);
    glex.defineGlobalVar("IComponent", MontiArcGeneratorConstants.COMPONENT_INTERFACE_NAME);
    glex.defineGlobalVar("AComponent", MontiArcGeneratorConstants.ABSTRACT_COMPONENT_NAME);
    glex.defineGlobalVar("ATimedComponent", MontiArcGeneratorConstants.ABSTRACT_TIMED_COMPONENT_NAME);
    glex.defineGlobalVar("IScheduler", MontiArcGeneratorConstants.SCHEDULER_INTERFACE_NAME);
    glex.defineGlobalVar("ISimulationErrorHandler", MontiArcGeneratorConstants.SIM_ERROR_HANDLER_INTERFACE_NAME);
    glex.defineGlobalVar("SimpleInPortInterface", MontiArcGeneratorConstants.SIMPLE_IN_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("TIME_PARADIGM_STORAGE_KEY",
        MontiArcGeneratorConstants.TIME_PARADIGM_STORAGE_KEY);
    glex.defineGlobalVar("portHelper", new PortHelper());
    glex.defineGlobalVar("IForwardPort", MontiArcGeneratorConstants.FORWARD_PORT_INTERFACE_NAME);
    glex.defineGlobalVar("DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX", MontiArcGeneratorConstants.DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX);    
    glex.defineGlobalVar("CODEGEN_TIME_IN_PORTNAME",
        MontiArcGeneratorConstants.CODEGEN_TIME_IN_PORTNAME);
    return glex;
  }
}

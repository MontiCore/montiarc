/**
 * 
 */
package de.montiarc.generator;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;
import sim.error.SimpleErrorHandler;
import sim.generic.AComponent;
import sim.generic.ASingleIn;
import sim.generic.ATimedComponent;
import sim.generic.ATimedSingleIn;
import sim.generic.IComponent;
import sim.generic.ISimComponent;
import sim.generic.ITimedComponent;
import sim.generic.SimpleInPortInterface;
import sim.port.IForwardPort;
import sim.port.IInPort;
import sim.port.IInSimPort;
import sim.port.IOutPort;
import sim.port.IOutSimPort;
import sim.port.IPort;
import sim.port.TestPort;
import sim.sched.SchedulerFactory;

/**
 * Constants for the MontiArc generator. <br>
 * <br>
 * Copyright (c) 2012 RWTH Aachen. All rights reserved.
 *
 * @author (last commit) $Author: ahaber $
 * @version $Date: 2015-01-19 16:58:12 +0100 (Mo, 19 Jan 2015) $<br>
 * $Revision: 3087 $
 */
public final class MontiArcGeneratorConstants {
  
  /**
   * Name of time in ports.
   */
  public static final String CODEGEN_TIME_IN_PORTNAME = "sourceTickPort";
  
  /**
   * Template name of the MontiArc component generator.
   */
  public static final String COMPONENT_GENERATOR = "mc.umlp.arc.ComponentMain";
  
  /**
   * Template name of the MontiArc factory generator.
   */
  public static final String FACTORY_GENERATOR = "mc.umlp.arc.ComponentFactoryMain";
  
  /**
   * Template name of the MontiArc interface generator.
   */
  public static final String INTERFACE_GENERATOR = "mc.umlp.arc.ComponentInterfaceMain";
  
  /**
   * Interface package
   */
  public static final String INTERFACES_PACKAGE = "interfaces";
  
  public static final String TICK_SOURCE_COMPONENT = "sim.comp.TickSource";
  
  public static final String TICK_SOURCE_SC_NAME = "_tickSourceInc";
  
  public static final String TICK_SOURCE_OUTPORT = "timeOut";
  
  /**
   * Factory package
   */
  public static final String FACTORIES_PACKAGE = "factories";
  
  /**
   * Template name of the MontiArc setup generator.
   */
  public static final String SETUP_GENERATOR = "mc.umlp.arc.ComponentSetupMain";
  
  /**
   * Stub generator for decomposed components.
   * 
   * @since 2.4.0
   */
  public static final String STUB_GENERATOR = "mc.umlp.arc.ComponentStubMain";
  
  /**
   * Template name of the MontiArc setup generator.
   */
  public static final String VISUALIZATION_GENERATOR = "mc.umlp.arc.Component2PlantUmlMain";
  
  /**
   * Contains all available MontiArc generator names.
   */
  public static final String[] ALL_GENERATORS = new String[] {
      COMPONENT_GENERATOR, FACTORY_GENERATOR, INTERFACE_GENERATOR, SETUP_GENERATOR,
      STUB_GENERATOR, VISUALIZATION_GENERATOR };
  
  /**
   * Contains all default generators.
   * 
   * @since 2.4.0
   */
  public static final String[] DEFAULT_GENERATORS = new String[] { INTERFACE_GENERATOR,
      FACTORY_GENERATOR, COMPONENT_GENERATOR };
  
  /**
   * Qualified name of the abstract {@link AComponent} class.
   * 
   * @since 2.3.0
   */
  public static final String ABSTRACT_COMPONENT_NAME = AComponent.class.getName();
  
  /**
   * Qualified name of the abstract {@link ASingleIn} class.
   * 
   * @since 2.3.0
   */
  public static final String ABSTRACT_SINGLE_IN_COMPONENT_NAME = ASingleIn.class.getName();
  
  /**
   * Qualified name of the abstract {@link ATimedComponent} class.
   * 
   * @since 2.3.0
   */
  public static final String ABSTRACT_TIMED_COMPONENT_NAME = ATimedComponent.class.getName();
  
  /**
   * Qualified name of the abstract {@link ATimedSingleIn} class.
   * 
   * @since 2.3.0
   */
  public static final String ABSTRACT_TIMED_SINGLE_IN_COMPONENT_NAME = ATimedSingleIn.class
      .getName();
  
  /**
   * Qualified name of the {@link IComponent} interface.
   * 
   * @since 2.3.0
   */
  public static final String COMPONENT_INTERFACE_NAME = IComponent.class.getName();
  
  /**
   * Qualified name of the {@link IForwardPort} interface.
   * 
   * @since 2.5.0
   */
  public static final String FORWARD_PORT_INTERFACE_NAME = IForwardPort.class.getName();
  
  /**
   * Turn this on to generate protocols.
   */
  protected static boolean generateProtocols = false;
  
  /**
   * Qualified name of the {@link IInPort} interface.
   * 
   * @since 2.3.0
   */
  public static final String IN_PORT_INTERFACE_NAME = IInPort.class.getName();
  
  /**
   * Qualified name of the {@link IInSimPort} interface.
   * 
   * @since 2.3.0
   */
  public static final String IN_SIM_PORT_INTERFACE_NAME = IInSimPort.class.getName();
  
  /**
   * Turn this on, if components with a single incoming port should be optimized
   * by merging port and component object.
   * 
   * @since 2.5.0
   */
  public static boolean optimizeSingleIn = false;
  
  /**
   * Qualified name of the {@link IOutPort} interface.
   * 
   * @since 2.3.0
   */
  public static final String OUT_PORT_INTERFACE_NAME = IOutPort.class.getName();
  
  /**
   * Qualified name of the {@link IOutSimPort} interface.
   * 
   * @since 2.5.0
   */
  public static final String OUT_SIM_PORT_INTERFACE_NAME = IOutSimPort.class.getName();
  
  /**
   * Qualified name of the {@link IPort} interface.
   * 
   * @since 2.3.0
   */
  public static final String PORT_INTERFACE_NAME = IPort.class.getName();
  
  /**
   * Qualified name of the {@link SchedulerFactory} class.
   * 
   * @since 2.5.0
   */
  public static final String SCHEDULER_FACORY_CLASS_NAME = SchedulerFactory.class.getName();
  
  /**
   * Qualified name of the {@link IScheduler} interface.
   * 
   * @since 2.5.0
   */
  public static final String SCHEDULER_INTERFACE_NAME = IScheduler.class.getName();
  
  /**
   * Qualified name of the {@link IComponent} interface.
   * 
   * @since 2.3.0
   */
  public static final String SIM_COMPONENT_INTERFACE_NAME = ISimComponent.class.getName();
  
  /**
   * Qualified name of the {@link ISimulationErrorHandler} interface.
   * 
   * @since 2.5.0
   */
  public static final String SIM_ERROR_HANDLER_INTERFACE_NAME = ISimulationErrorHandler.class
      .getName();
  
  /**
   * Qualified name of the {@link SimpleErrorHandler} interface.
   * 
   * @since 2.5.0
   */
  public static final String SIMPLE_ERROR_HANDLER_CLASS_NAME = SimpleErrorHandler.class.getName();
  
  /**
   * Qualified name of the {@link SimpleInPortInterface} interface.
   * 
   * @since 2.3.0
   */
  public static final String SIMPLE_IN_PORT_INTERFACE_NAME = SimpleInPortInterface.class.getName();
  
  /**
   * Qualified name of the {@link TestPort} class.
   * 
   * @since 2.5.0
   */
  public static final String TEST_PORT_CLASS_NAME = TestPort.class.getName();
  
  /**
   * Key used to store the time paradigm of the current component in the
   * {@link TemplateOperator}.
   * 
   * @since 2.3.0
   */
  public static final String TIME_PARADIGM_STORAGE_KEY = "TIME_PARADIGM_STORAGE_KEY";
  
  /**
   * Qualified name of the {@link ITimedComponent} interface.
   * 
   * @since 2.3.0
   */
  public static final String TIMED_COMPONENT_INTERFACE_NAME = ITimedComponent.class.getName();
  
  // TODO read it from pom, see old MA
  public static final String VERSION = "4.0.0";
  
  /** Default postfix for atomic component implementations. */
  public static final String DEFAULT_ATOMIC_COMPONENT_IMPL_POSTFIX = "Impl";
  
  /**
   * @return true, if protocols should be generated.
   */
  public static boolean generateProtocols() {
    return generateProtocols;
  }
  
  /**
   * @return true, if components with a single incoming port should be optimized
   * by merging port and component object.
   * @since 2.5.0
   */
  public static boolean optimizeSingleInComponents() {
    return optimizeSingleIn;
  }
  
  /**
   * Empty private constructor to prevent constant class instantiation.
   */
  private MontiArcGeneratorConstants() {
    
  }
  
}

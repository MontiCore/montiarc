/**
 * 
 */
package de.montiarc.generator.codegen;

import static de.montiarc.generator.MontiArcGeneratorConstants.IN_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.IN_SIM_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.OUT_PORT_INTERFACE_NAME;
import static de.montiarc.generator.MontiArcGeneratorConstants.SIM_COMPONENT_INTERFACE_NAME;

/**
 * Encapsulated expected generated types. <br>
 * <br>
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * @author (last commit) $Author:$
 * @version $Date:$<br>
 * $Revision:$
 * @since 2.2.0 (05.04.2013)
 */
public class GeneratorTestConstants {
  
  /**
   * Private constructor prevents utility class instantiation.
   */
  private GeneratorTestConstants() {
  
  }
  
  public static final String INPORT_ATTRIBUTE_TYPE = IN_SIM_PORT_INTERFACE_NAME;
  
  public static final String INPORT_GETTER_RETURN_TYPE = IN_PORT_INTERFACE_NAME;
  
  public static final String OUTPORT_ATTRIBUTE_TYPE = OUT_PORT_INTERFACE_NAME;
  
  public static final String OUTPORT_GETTER_RETURN_TYPE = OUT_PORT_INTERFACE_NAME;
  
  public static final String SIM_COMPONENT_INTERFACE_TYPE = SIM_COMPONENT_INTERFACE_NAME;

}

/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import java.io.File;

import montiarc.helper.AutoconnectMode;
import montiarc.helper.Timing;

/**
 * Constants for the MontiArc language. 
 *
 */
public final class MontiArcConstants {
  
  /**
   * Default auto connect mode = OFF.
   */
  public static final AutoconnectMode DEFAULT_AUTO_CONNECT = AutoconnectMode.OFF;
  
  /**
   * Default time paradigm = timed.
   *
   */
  public static final Timing DEFAULT_TIME_PARADIGM = Timing.INSTANT;
  
  /**
   * Default documentation directory.
   */
  public static final String DEFAULT_DOC_DIR = "target" + File.separator + "madoc";
  
  /**
   * Default output directory.
   */
  public static final String DEFAULT_GEN_DIR = "target" + File.separator +
      "generated-sources" + File.separator +
      "montiarc" + File.separator +
      "sourcecode";
  
  /**
   * Default model directory.
   */
  public static final String DEFAULT_MODEL_DIR = "src" + File.separator + "main" + File.separator
      + "models";
  
}

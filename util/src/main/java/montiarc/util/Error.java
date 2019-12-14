/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import java.util.regex.Pattern;

/**
 * This is the mixin interface for all MontiArc errors. This interface serves as an extension
 * point to add new error codes in submodules of the MontiArc framework.
 */
public interface Error {

  static Pattern ERROR_CODE_PATTERN = Pattern.compile("0xMA(?!(0)\\d{4})(\\d{4})*");

  /**
   * Returns the unique error code of this error.
   */
  String getErrorCode();
}
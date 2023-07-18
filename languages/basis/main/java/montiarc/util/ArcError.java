/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all arc basis errors. Implements the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1100 - 0xC1199
 */
public enum ArcError implements Error {
  CIRCULAR_INHERITANCE("0xC1100", "Circular inheritance of component '%s'"),
  MISSING_COMPONENT("0xC1101", "Cannot resolve component '%s'"),
  AMBIGUOUS_REFERENCE("0xC1102", "Ambiguous reference, both '%s' and '%s' match"),
  IN_PORT_UNUSED("0xC1103", "Port '%s' is never used"),
  OUT_PORT_UNUSED("0xC1104", "Port '%s' is never used"),
  PORT_MULTIPLE_SENDER("0xC1105", "Port '%s' is target of multiple connectors"),
  IN_PORT_NOT_CONNECTED("0xC1106", "Port '%s' is not connected"),
  OUT_PORT_NOT_CONNECTED("0xC1107", "Port '%s' is not connected"),
  MISSING_PORT("0xC1108", "Cannot resolve port '%s'"),
  MISSING_SUBCOMPONENT("0xC1109", "Cannot resolve subcomponent '%s'"),
  CONNECTOR_TYPE_MISMATCH("0xC1110", "Type mismatch, expected '%s' but provided '%s'"),
  SOURCE_DIRECTION_MISMATCH("0xC1111", "Direction mismatch, cannot observe '%s'"),
  TARGET_DIRECTION_MISMATCH("0xC1112", "Direction mismatch, cannot target '%s'"),
  CONNECTOR_TIMING_MISMATCH("0xC1113", "Timing mismatch, expected '%s' but provided '%s'"),
  MULTIPLE_TIMING_ANNOTATIONS("0xC1114", "Annotation error, multiple timing annotations"),
  MULTIPLE_BEHAVIOR("0xC1115", "Multiple conflicting behavior descriptions"),
  IN_PORT_DELAYED("0xC1116", "Annotation error, cannot delay incoming port '%s'"),
  FEEDBACK_CAUSALITY("0xC1117", "Feedback loop without strong causality"),
  TOO_FEW_ARGUMENTS("0xC1119", "Too few arguments, expected '%s' but provided '%S'"),
  TOO_MANY_ARGUMENTS("0xC1120", "Too many arguments, expected '%s' but provided '%s'"),
  COMP_ARG_MULTIPLE_VALUES("0xC1122", "Multiple values for argument '%s'"),
  COMP_ARG_TYPE_MISMATCH("0xC1123", "Type mismatch, expected '%s' but provided '%s'"),
  COMP_ARG_KEY_INVALID("0xC1124", "Unexpected key argument '%s'"),
  COMP_ARG_VALUE_AFTER_KEY("0xC1125", "Positional assignments after key argument"),
  OPTIONAL_PARAMS_LAST("0xC1126", "Mandatory parameter '%s' proceeds optional parameter '%s'"),
  SUBCOMPONENT_REFERENCE_CYCLE("0xC1127", "Infinite recursion, subcomponent reference cycle"),
  HERITAGE_TOO_FEW_ARGUMENTS("0xC1128", "Too few arguments, expected '%s' but provided '%S'"),
  HERITAGE_TOO_MANY_ARGUMENTS("0xC1129", "Too many arguments, expected '%s' but provided '%s'"),
  HERITAGE_COMP_ARG_TYPE_MISMATCH("0xC1130", "Type mismatch, expected '%s' but provided '%s'"),
  TYPE_REF_NO_EXPRESSION2("0xC1131", "Expected an expression"),
  HERITAGE_IN_PORT_TYPE_MISMATCH("0xC1132", "Incompatible types, clash with port of super component"),
  HERITAGE_OUT_PORT_TYPE_MISMATCH("0xC1133", "Incompatible types, clash with port of super component"),
  HERITAGE_PORT_DIRECTION_MISMATCH("0xC1134", "Incompatible direction, clash with port of super component"),
  PORT_REF_FIELD_INIT("0xC1135", "Port cannot be referenced in static context"),
  FIELD_INIT_TYPE_MISMATCH("0xC1137", "Type mismatch, expected '%s' but provided '%s'"),
  PORT_REF_DEFAULT_VALUE("0xC1138", "Port cannot be referenced in static context"),
  PARAM_DEFAULT_TYPE_MISMATCH("0xC1139", "Type mismatch, expected '%s' but provided '%s'"),
  COMP_ARG_PORT_REF("0xC1141", "Port cannot be referenced in static context"),
  COMP_ARG_MULTI_ASSIGNMENT("0xC1142", "Invalid syntax, no assignment in default value"),
  COMPONENT_LOWER_CASE("0xC1143", "Convention violation, components should be upper case"),
  SUBCOMPONENT_UPPER_CASE("0xC1144", "Convention violation, subcomponents should be lower case"),
  PORT_UPPER_CASE("0xC1145", "Convention violation, ports should be lower case"),
  PARAMETER_UPPER_CASE("0xC1146", "Convention violation, parameters should be lower case"),
  FIELD_UPPER_CASE("0xC1147", "Convention violation, component fields should be lower case"),
  UNIQUE_IDENTIFIER_NAMES("0xC1148", "Multiple identifiers called '%s' in the same scope"),
  RESTRICTED_IDENTIFIER("0xC1149", "The identifier '%s' is restricted and cannot be used here"),
  UNSUPPORTED_MODEL_ELEMENT("0xC1150", "The usage of '%s' is unsupported and thus ignored"),
  READ_FROM_OUTGOING_PORT("0xC1151", "Cannot read from the outgoing port '%s' of component '%s'."),
  WRITE_TO_INCOMING_PORT("0xC1152", "Cannot write to the incoming port '%s' of component '%s'."),
  WRITE_TO_READONLY_VARIABLE("0xC1153", "Cannot write to readonly variable '%s' of component '%s'."),
  INVALID_CONTEXT_ASSIGNMENT("0xC1154", "Invalid syntax, no assignments in this context"),
  INVALID_CONTEXT_INC_PREFIX("0xC1155", "Invalid syntax, no increment in this context"),
  INVALID_CONTEXT_DEC_PREFIX("0xC1156", "Invalid syntax, no decrement in this context"),
  INVALID_CONTEXT_INC_SUFFIX("0xC1157", "Invalid syntax, no increment in this context"),
  INVALID_CONTEXT_DEC_SUFFIX("0xC1158", "Invalid syntax, no decrement in this context"),
  KEY_NOT_UNIQUE("0xC1159", "Cannot use key '%s' of component '%s' multiple times to set parameters."),
  HERITAGE_KEY_NOT_UNIQUE("0xC1160", "Cannot use key '%s' of component '%s' multiple times to set parameters."),
  HERITAGE_COMP_ARG_MULTIPLE_VALUES("0xC1161", "Multiple values for argument '%s'"),
  HERITAGE_COMP_ARG_KEY_INVALID("0xC1162", "Unexpected key argument '%s'"),
  HERITAGE_COMP_ARG_VALUE_AFTER_KEY("0xC1163", "Positional assignments after key argument"),
  CONNECT_PRIMITIVE_TO_OBJECT("0xC1164", "Boxing not supported for connectors: Cannot connect primitive type port to object type port."),
  CONNECT_OBJECT_TO_PRIMITIVE("0xC1165", "Unboxing not supported for connectors: Cannot connect object type port to primitive type port.");

  private final String errorCode;
  private final String errorMessage;

  ArcError(String errorCode, String errorMessage) {
    assert (errorCode != null);
    assert (errorMessage != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * @return The unique error code of this error.
   */
  @Override
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String printErrorMessage() {
    return this.errorMessage;
  }

  @Override
  public String toString() {
    return this.getErrorCode() + ": " + this.printErrorMessage();
  }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #printErrorMessage()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}
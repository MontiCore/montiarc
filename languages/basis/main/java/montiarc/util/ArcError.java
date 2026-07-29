/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all arc basis errors. Implements the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1100 - 0xC1299
 */
public enum ArcError implements Error {
  CIRCULAR_INHERITANCE("0xC1100", "Circular inheritance of component '%s'"),
  AMBIGUOUS_REFERENCE("0xC1102", "Ambiguous reference, both '%s' and '%s' match"),
  IN_PORT_UNUSED("0xC1103", "Port '%s' is never used"),
  OUT_PORT_UNUSED("0xC1104", "Port '%s' is never used"),
  PORT_MULTIPLE_SENDER("0xC1105", "Port '%s' is the target of multiple connectors"),
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
  DECOMPOSED_COMPONENT_WITH_BEHAVIOR("0xC1116", "Cannot use behavior description in decomposed component"),
  FEEDBACK_CAUSALITY("0xC1117", "Feedback loop without strong causality"),
  TOO_FEW_ARGUMENTS("0xC1119", "Too few arguments, expected '%s' but provided '%S'"),
  TOO_MANY_ARGUMENTS("0xC1120", "Too many arguments, expected '%s' but provided '%s'"),
  COMP_ARG_MULTIPLE_VALUES("0xC1122", "Multiple values for argument '%s'"),
  COMP_ARG_TYPE_MISMATCH("0xC1123", "Type mismatch, expected '%s' but provided '%s'"),
  COMP_ARG_KEY_INVALID("0xC1124", "Unexpected key argument '%s'"),
  COMP_ARG_VALUE_AFTER_KEY("0xC1125", "Positional assignments after key argument"),
  OPTIONAL_PARAMS_LAST("0xC1126", "Mandatory parameter '%s' proceeds optional parameter '%s'"),
  COMPONENT_REFERENCE_CYCLE("0xC1127", "Component '%s' instantiates itself in a self-referential cycle:\n%s"),
  TYPE_REF_NO_EXPRESSION2("0xC1131", "Expected an expression"),
  HERITAGE_IN_PORT_TYPE_MISMATCH("0xC1132", "Incompatible types, clash with port of super component"),
  HERITAGE_OUT_PORT_TYPE_MISMATCH("0xC1133", "Incompatible types, clash with port of super component"),
  HERITAGE_PORT_DIRECTION_MISMATCH("0xC1134", "Incompatible direction, clash with port of super component"),
  PORT_REF_IN_STATIC_CONTEXT("0xC1135", "Value of port '%s' not available in static context"),
  FIELD_INIT_TYPE_MISMATCH("0xC1137", "Type mismatch, expected '%s' but provided '%s'"),
  PARAM_DEFAULT_TYPE_MISMATCH("0xC1139", "Type mismatch, expected '%s' but provided '%s'"),
  COMP_ARG_MULTI_ASSIGNMENT("0xC1142", "Invalid syntax, no assignment in default value"),
  COMPONENT_LOWER_CASE("0xC1143", "Convention violation, components should be upper case"),
  SUBCOMPONENT_UPPER_CASE("0xC1144", "Convention violation, subcomponents should be lower case"),
  PORT_UPPER_CASE("0xC1145", "Convention violation, ports should be lower case"),
  PARAMETER_UPPER_CASE("0xC1146", "Convention violation, parameters should be lower case"),
  FIELD_UPPER_CASE("0xC1147", "Convention violation, component fields should be lower case"),
  UNIQUE_IDENTIFIER_NAMES("0xC1148", "Multiple identifiers called '%s' in the same scope"),
  RESTRICTED_IDENTIFIER("0xC1149", "The identifier '%s' is restricted and cannot be used here"),
  UNSUPPORTED_MODEL_ELEMENT("0xC1150", "The usage of '%s' is unsupported and thus ignored"),
  READ_FROM_OUTGOING_PORT("0xC1151", "Cannot read from the outgoing port '%s'"),
  WRITE_TO_INCOMING_PORT("0xC1152", "Cannot write to the incoming port '%s'"),
  WRITE_TO_READONLY_VARIABLE("0xC1153", "Cannot write to readonly variable '%s' of component '%s'."),
  INVALID_CONTEXT_ASSIGNMENT("0xC1154", "Invalid syntax, no assignments in this context"),
  INVALID_CONTEXT_INC_PREFIX("0xC1155", "Invalid syntax, no increment in this context"),
  INVALID_CONTEXT_DEC_PREFIX("0xC1156", "Invalid syntax, no decrement in this context"),
  INVALID_CONTEXT_INC_SUFFIX("0xC1157", "Invalid syntax, no increment in this context"),
  INVALID_CONTEXT_DEC_SUFFIX("0xC1158", "Invalid syntax, no decrement in this context"),
  KEY_NOT_UNIQUE("0xC1159", "Cannot use key '%s' multiple times to set parameters."),
  CONNECTORS_IN_ATOMIC("0xC1174", "Connectors cannot be used inside atomic components"),
  TYPE_PARAMETER_UPPER_CASE("0xC1175", "Type parameters should start with an uppercase letter"),
  TYPE_ARG_IGNORES_UPPER_BOUND("0xC1176", "Type parameter '%s' does not respect its upper bound, should extend '%s'"),
  TOO_FEW_TYPE_ARGUMENTS("0xC1177", "Too few type arguments, expected `%s` but provided `%s`"),
  TOO_MANY_TYPE_ARGUMENTS("0xC1178", "Too many type arguments, expected `%s` but provided `%s`"),
  RAW_USE_OF_PARAMETRIZED_TYPE("0xC1182", "Raw usage of generic component type `%s`"),
  REFINEMENT_PORT_NAME_MISMATCH("0xC1184", "Interface mismatch during refinement, port '%s' exists in '%s' but not in '%s'."),
  REFINEMENT_PORT_DIRECTION_CHANGED("0xC1185", "Direction mismatch, port '%s' has direction '%s', mismatching the direction in the abstraction '%s'"),
  REFINEMENT_TIMING_MISMATCH_IN("0xC1186", "Timing mismatch, port '%s' changes timing from '%s' in the abstraction '%s' to '%s' which is illegal for incoming ports"),
  REFINEMENT_TIMING_MISMATCH_OUT("0xC1187", "Timing mismatch, port '%s' changes timing from '%s' in the abstraction '%s' to '%s' which is illegal for outgoing ports"),
  REFINEMENT_IN_PORT_TYPE_MISMATCH("0xC1188", "Type mismatch, input port '%s' has type '%s', which is not a supertype of the port's type '%s' in the abstraction '%s'"),
  REFINEMENT_OUT_PORT_TYPE_MISMATCH("0xC1189", "Type mismatch, output port '%s' has type '%s', which is not a subtype of the port's type '%s' in the abstraction '%s'"),
  CIRCULAR_FIELDS_DEPENDENCY("0xC1190", "Circular dependency concerning fields '%s'."),
  IN_PORT_REF_IN_INVALID_CONTEXT("0xC1191", "Value of port '%s' not available in %s."),
  OUT_PORT_MEMBER_ACCESSED("0xC1192", "Cannot access port '%s' or any of its members; output ports are write-only"),
  INVALID_PORT_TIMING_OVERRIDE("0xC1193", "Port '%s' cannot override timing from '%s' to '%s'"),
  INVALID_STATEMENT("0xC1194", "The expression is not a valid statement; only assignments or method calls are allowed as statements"),
  FIELD_REF_IN_STATIC_CONTEXT("0xC1195", "Value of field '%s' not available in static context"),
  UNEXPECTED_ORACLE_TYPE("0xC1196", "Value of '%s' is not a known oracle strategy");

  private final String errorCode;
  private final String errorMsgFormat;

  ArcError(String errorCode, String errorMsgFormat) {
    assert (errorCode != null);
    assert (errorMsgFormat != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMsgFormat = errorMsgFormat;
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
  public String getErrorMsgFormat() {
    return this.errorMsgFormat;
  }

  @Override
  public String toString() {
    return this.getErrorCode() + ": " + this.getErrorMsgFormat().replaceAll("\n", System.lineSeparator());
  }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #getErrorMsgFormat()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}

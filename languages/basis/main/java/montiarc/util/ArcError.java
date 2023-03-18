/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc basis errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1100 - 0xC1199
 */
public enum ArcError implements Error {
  CIRCULAR_INHERITANCE("0xC1100", "Circular inheritance of component '%s' detected."),
  MISSING_TYPE_OF_INHERITED_COMPONENT("0xC1101", "Cannot resolve parent type '%s' of "
      + "component '%s'."),
  MISSING_TYPE_OF_COMPONENT_INSTANCE("0xC1102", "Cannot resolve type '%s' of component "
      + "instance '%s'."),
  SYMBOL_TOO_MANY_FOUND("0xC1104", "Found too many Symbols `%s`."),
  INCOMING_PORT_NO_FORWARD("0xC1107", "Incoming port '%s' of component '%s' is not connected "
      + "as a source of a port forward."),
  OUTGOING_PORT_NO_FORWARD("0xC1108", "Outgoing port '%s' of component '%s' is not connected "
      + "as a target of a port forward."),
  PORT_MULTIPLE_SENDER("0xC1109", "Target port ''%s' of connector is already connected."),
  INCOMING_PORT_NOT_CONNECTED("0xC1112", "Incoming port '%s' is not connected."),
  OUTGOING_PORT_NOT_CONNECTED("0xC1113", "Outgoing port '%s' is not connected."),
  INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE("0xC1114", "Inner component '%s' must have an "
      + "instance defining its formal type parameters."),
  INHERITED_INCOMING_PORT_TYPE_MISMATCH("0xC1115", "Port '%s' is of type '%s' which cannot "
      + "override type '%s' of the inherited port. Override only with supertypes."),
  INHERITED_OUTGOING_PORT_TYPE_MISMATCH("0xC1116", "Port '%s' is of type '%s' which cannot "
      + "override type '%s' of the inherited port. Override only with subtypes."),
  INHERITED_PORT_DIRECTION_MISMATCH("0xC1117", "Port '%s' can not be %s, because it overrides an %s port."),
  OPTIONAL_CONFIG_PARAMS_LAST("0xC1118", "Configuration parameter '%s' at position '%d' of component '%s' should be " +
      "optional, as it is preceded by the optional configuration parameter '%s' at position '%s'. Mandatory parameters " +
      "(that do not specify default values) must be declared before all optional parameters (that specify default " +
      "values). Therefore provide a default value!"),
  SOURCE_AND_TARGET_SAME_COMPONENT("0xC1119", "Source and target of the connector "
      + "refer to the same subcomponent."),
  SOURCE_PORT_NOT_EXISTS("0xC1120", "Source port '%s' of component '%s' does not "
      + "exist."),
  TARGET_PORT_NOT_EXISTS("0xC1121", "Target port '%s' of component '%s' does not "
      + "exist."),
  SOURCE_AND_TARGET_TYPE_MISMATCH("0xC1122", "Type '%s' of source port and type '%s' of target "
      + "port of component '%s' are incompatible."),
  INNER_COMPONENT_EXTENDS_OUTER("0xC1123", "Inner component type '%s' extends the " +
      "component type %s which is also its outer component type."),
  VARIABLE_LOWER_CASE("0xC1124", "The name of the variable '%s' of component '%s' should start "
      + "with a lower case letter."),
  PORT_LOWER_CASE("0xC1125", "The name of the port '%s' of component '%s' should start with a "
      + "lower case letter."),
  PARAMETER_LOWER_CASE("0xC1126", "The name of the parameter '%s' of component '%s' should start"
      + " with a lower case letter."),
  PORT_DIRECTION_MISMATCH("0xC1127", "Port '%s' can not be a target of the connector, because it is %s." + ""),
  CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL("0xC1045", "The target port '%s' is identical to the"
      + " source port '%s'"),
  COMPONENT_NAME_UPPER_CASE("0xC1128", "Component names must be in upper-case."
      + " So '%s' is an unsuitable name"),
  INSTANCE_NAME_LOWER_CASE("0xC1129", "The name of the component instance '%s' should start"
      + " with a lower case letter."),
  UNIQUE_IDENTIFIER_NAMES("0xC1130", "Within '%s' there may not be multiple identifiers called '%s'. Occurrences: %s."),
  ROOT_COMPONENT_TYPES_MISS_INSTANCE_NAMES("0xC1131", "Root components must not have instance "
      + "names. Violated by component type '%s', named '%s'."),
  NO_SUBCOMPONENT_CYCLE("0xC1131", "Found an illegal subcomponent instantiation cycle: %s"),
  PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL("0xC1132", "The initializing expression for " +
      "field '%s' in component type '%s' illegally relies on the port '%s'."),
  FIELD_INIT_EXPRESSION_WRONG_TYPE("0xC1133", "Calculated type of initializer expression for variable '%s' was " +
      "calculated as '%s' but was expected to be '%s'."),
  DEFAULT_PARAM_EXPRESSION_WRONG_TYPE("0xC1134", "Calculated type of default value expression of configuration " +
      "parameter '%s' was calculated as '%s' but was expected to be '%s'."),
  PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL("0xC1139", "Default values of parameter '%s' in component type " +
      "'%s' illegally relies on port '%s'."),
  MISSING_TYPE("0xC1140", "Cannot resolve type '%s'."),
  SOURCE_PORT_COMPONENT_MISSING("0xC1141", "Component instance '%s' missing for source of connector."),
  TARGET_PORT_COMPONENT_MISSING("0xC1142", "Component instance '%s' missing for target of connector."),
  UNSUPPORTED_MODEL_ELEMENT("0xC1143", "%s"),
  PORT_REFERENCE_IN_INSTANTIATION_ARG_ILLEGAL("0xC1144", "An argument for the instantiation '%s' illegally relies " +
      "on the port '%s'"),
  TOO_FEW_INSTANTIATION_ARGUMENTS("0xC1145", "There are '%d' instantiation arguments for component instance '%s', " +
      "but component type '%s' has '%d' mandatory configuration parameters that all must be bound."),
  TOO_MANY_INSTANTIATION_ARGUMENTS("0xC1146", "There are '%d' instantiation arguments for component instance '%s', " +
      "but its component type '%s' only has '%d' configuration parameters. Please do not provide more arguments than " +
      "parameters exist."),
  INSTANTIATION_ARGUMENT_TYPE_MISMATCH("0xC1147", "The instantiation argument at position '%d' of component instance " +
      "'%s' is of type '%s' which is incompatible to type '%s' of the corresponding configuration parameter '%s' of " +
      "component type '%s'."),
  UNRESOLVABLE_IMPORT("0xC1148", "Can't resolve imported symbol '%s'."),
  MULTIPLE_BEHAVIOR("0xC1149",
      "The component type %s defines multiple behaviors, but only one is allowed at max"),
  BEHAVIOR_IN_COMPOSED_COMPONENT("0xC1150",
      "Only atomic components may have behavior specifications, but %s is composed."),
  READ_FROM_OUTGOING_PORT("0xC1151", "Cannot read from the outgoing port '%s' of component '%s'."),
  WRITE_TO_INCOMING_PORT("0xC1152", "Cannot write to the incoming port '%s' of component '%s'."),
  WRITE_TO_READONLY_VARIABLE("0xC1153", "Cannot write to readonly variable '%s' of component '%s'."),
  PARAM_DEFAULT_VALUE_IS_TYPE_REF("0xC1154", "The expression represents the type '%s'. Therefore it can not be used " +
      "as the default value for the configuration parameter '%s', as default value expressions must evaluate to values " +
      "(which type names do not do)."),
  CONFIG_PARAM_BINDING_IS_TYPE_REF("0xC1155", "The expression represents the type '%s'. Therefore it can not be " +
      "used to bind the configuration parameter '%s' at position '%s' of the subcomponent '%s %s', as configuration parameter bindings " +
      "must evaluate to values (which type names do not do)."),
  FIELD_INITIALIZATION_IS_TYPE_REF("0xC1156", "The expression represents the type '%s'. Therefore it can not be used " +
      "as initialization value for field '%s', as initialization expressions must evaluate to values (which type names " +
      "do not do)."),
  GENERIC_COMPONENT_TYPE_INSTANTIATION("0xC1157", "ArcBasis does not support generic components when " +
          "instantiating component types within their type declaration. But component type '%s' has type parameters %s."),
  MISSING_ASSIGNMENT_OF_ARC_FIELD("0xC1158", "Missing initial assignment for field '%s' of type '%s'."),
  TIMING_MULTIPLE("0xC1159", "Duplicate timing annotation."),
  DELAY_OUT_PORT_ONLY("0xC1160", "No ."),
  SOURCE_AND_TARGET_TIMING_MISMATCH("0xC1161", "Timing mismatch. Expected %s but provided %s."),
  FEEDBACK_LOOP_TIMING_NOT_DELAYED("0xC1162", "There is a feedback loop inside of component %s that is not delayed."),
  TOO_FEW_PARENT_INSTANTIATION_ARGUMENTS("0xC1163", "There are '%d' instantiation arguments for component '%s', " +
      "but component type '%s' has '%d' mandatory configuration parameters that all must be bound."),
  TOO_MANY_PARENT_INSTANTIATION_ARGUMENTS("0xC1164", "There are '%d' instantiation arguments for component '%s', " +
      "but its component type '%s' only has '%d' configuration parameters. Please do not provide more arguments than " +
      "parameters exist."),
  PARENT_INSTANTIATION_ARGUMENT_TYPE_MISMATCH("0xC1165", "The parent configuration argument at position '%d' of component " +
      "'%s' is of type '%s' which is incompatible to type '%s' of the corresponding configuration parameter '%s' of " +
      "component type '%s'."),
  PARENT_CONFIG_PARAM_BINDING_IS_TYPE_REF("0xC1166", "The expression represents the type '%s'. Therefore it can not be " +
      "used to bind the configuration parameter '%s' at position '%s' of the subcomponent '%s %s', as configuration parameter bindings " +
      "must evaluate to values (which type names do not do)."),
  RESERVED_KEYWORD_USED("0xC1167", "'%s' is a keyword reserved by '%s'. Thus it's usage as a identifier name is " +
    "illegal"),
  INSTANTIATION_ARGUMENT_INVALID_ASSIGNMENT("0xC1168", "Instantiation argument of component instance '%s', " +
    "consists of invalid assignments. Keyword binding of parameters allows for only one assignment expression with the '=' operator."),
  ASSIGNMENT_KEYWORD_NOT_A_PARAMETER("0xC1169", "Keyword assignment of component '%s' does not refer to a parameter " +
    "of its component type."),
  POSITIONAL_ASSIGNMENT_AFTER_KEYWORD_ASSIGNMENT("0xC1170", "Positional assignments of component '%s' must be referred to " +
          "before keyword based assignments"),
  MULTIPLE_VALUES_FOR_ARGUMENT("0xC1171", "component '%s' got multiple values for argument '%s'.");


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
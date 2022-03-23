/* (c) https://github.com/MontiCore/monticore */
package arcbasis.util;

/**
 * The enum of all Arc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum ArcError implements montiarc.util.Error {
  CIRCULAR_INHERITANCE("0xC1010", "Circular inheritance of component '%s' detected."),
  MISSING_TYPE_OF_INHERITED_COMPONENT("0xC1014", "Cannot resolve type of parent '%s' of "
    + "component '%s'."),
  MISSING_TYPE_OF_COMPONENT_INSTANCE("0xC1015", "Cannot resolve type '%s' of component "
    + "instance '%s'."),
  SYMBOL_NOT_FOUND("0xC1016", "Cannot find Symbol `%s`."),
  INCOMING_PORT_AS_TARGET("0xC1020", "Incoming port '%s' of component '%s' is target of a port "
    + "forward. Incoming ports may only be used as the source of a port forward."),
  OUTGOING_PORT_AS_SOURCE("0xC1021", "Outgoing port '%s' of component '%s' is source of a port "
    + "forward. Outgoing ports may only be used as the target of a port forward."),
  INCOMING_PORT_NO_FORWARD("0xC1022", "Incoming port '%s' of component '%s' is not connected "
    + "as a source of a port forward."),
  OUTGOING_PORT_NO_FORWARD("0xC1023", "Outgoing port '%s' of component '%s' is not connected "
    + "as a target of a port forward."),
  PORT_MULTIPLE_SENDER("0xC1024", "Target port ''%s' of connector is already connected."),
  INCOMING_PORT_AS_SOURCE("0xC1025", "Incoming port '%s' of subcomponent '%s' of component type "
    + "'%s' is source of a connector. Incoming ports of subcomponents may only be used as the "
    + "target of a connector."),
  OUTGOING_PORT_AS_TARGET("0xC1026", "Outgoing port '%s' of subcomponent '%s' of component type "
    + "'%S' is target of a connector. Outgoing ports of subcomponents may only be used as the "
    + "source of a connector."),
  INCOMING_PORT_NOT_CONNECTED("0xC1027", "Incoming port '%s' of subcomponent '%s' of component "
    + "type '%s' is not connected."),
  OUTGOING_PORT_NOT_CONNECTED("0xC1028", "Outgoing port '%s' of subcomponent '%s' of component "
    + "type '%s' is not connected."),
  INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE("0xC1029", "Inner component '%s' must have an "
    + "instance defining its formal type parameters."),
  TOO_FEW_INHERITED_CONFIG_PARAMS("0xC1030", "You defined component type '%s' to have '%s' parameters, but you must " +
    "provide at least '%s' parameters, matching the type signature of the parent component '%s'."),
  INHERITED_CONFIG_PARAM_TYPE_MISMATCH("0xC1031", "Configuration parameter '%s' at position '%d' of component '%s' " +
    "is of type '%s', but it is required to match the type '%s' of the inherited configuration parameter '%s' at " +
    "position '%d'."),
  INHERITED_CONFIG_PARAM_MISSES_DEFAULT_VALUE("0xC1032", "Configuration parameter '%s' at position %d of component " +
    "'%s' has no default value. However, it should have a default value, as the configuration parameter '%s' of " +
    "parent component '%s' at position '%d' has a default value, too."),
  OPTIONAL_CONFIG_PARAMS_LAST("0xC1033", "Configuration parameter '%s' at position '%d' of component '%s' should be " +
    "optional, as it is preceded by the optional configuration parameter '%s' at position '%s'. Mandatory parameters " +
    "(that do not specify default values) must be declared before all optional parameters (that specify default " +
    "values). Therefore provide a default value!"),
  SOURCE_AND_TARGET_SAME_COMPONENT("0xC1034", "Source and target of a connector of component "
    + "'%s' refer to the same subcomponent."),
  SOURCE_PORT_NOT_EXISTS("0xC1035", "Source port '%s' of connector '%s' of component '%s' does not "
    + "exist."),
  TARGET_PORT_NOT_EXISTS("0xC1036", "Target port '%s' of connector '%s' of component '%s' does not "
    + "exist."),
  SOURCE_AND_TARGET_TYPE_MISMATCH("0xC1037", "Type '%s' of source port and type '%s' of target "
    + "port of connector '%s' of component '%s' are incompatible."),
  INNER_COMPONENT_EXTENDS_OUTER("0xC1038", "Inner component type '%s' extends the " +
    "component type %s which is also its outer component type."),
  VARIABLE_LOWER_CASE("0xC1039", "The name of the variable '%s' of component '%s' should start "
    + "with a lower case letter."),
  PORT_LOWER_CASE("0xC1040", "The name of the port '%s' of component '%s' should start with a "
    + "lower case letter."),
  PARAMETER_LOWER_CASE("0xC1041", "The name of the parameter '%s' of component '%s' should start"
    + " with a lower case letter."),
  TYPE_PARAMETER_UPPER_CASE_LETTER("0xC1042", "The generic type parameter '%s' of component '%s'"
    + " should start with an upper case letter."),
  PORT_DIRECTION_MISMATCH("0xC1043", "Port '%s' can not be a %s of the connector '%s', because it is %s." + ""),
  CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL("0xC1045", "The target port '%s' is identical to the"
    + " source port '%s'"),
  COMPONENT_NAME_UPPER_CASE("0xC1055", "Component names must be in upper-case."
    + " So '%s' is an unsuitable name"),
  INSTANCE_NAME_LOWER_CASE("0xC1056", "The name of the component instance '%s' should start"
    + " with a lower case letter."),
  UNIQUE_IDENTIFIER_NAMES("0xC1061", "Within '%s' there may not be multiple identifiers called '%s'. Occurrences: %s."),
  ROOT_COMPONENT_TYPES_MISS_INSTANCE_NAMES("0xC1062", "Root components must not have instance "
    + "names. Violated by component type '%s', named '%s'."),
  NO_SUBCOMPONENT_CYCLE("0xC1064", "Found an illegal subcomponent instantiation cycle: %s"),
  PORT_REFERENCE_IN_FIELD_INIT_EXPRESSION_ILLEGAL("0xC1065", "The initializing expression for " +
    "field '%s' in component type '%s' illegally relies on the port '%s'."),
  FIELD_INIT_EXPRESSION_WRONG_TYPE("0xC1066", "Calculated type of initializer expression for variable '%s' was " +
    "calculated as '%s' but was expected to be '%s'."),
  DEFAULT_PARAM_EXPRESSION_WRONG_TYPE("0xC1067", "Calculated type of default value expression of configuration " +
    "parameter '%s' was calculated as '%s' but was expected to be '%s'."),
  PORT_REFERENCE_IN_PARAMETER_DEFAULT_VALUE_ILLEGAL("0xC1068", "Default values of parameter '%s' in component type " +
    "'%s' illegally relies on port '%s'."),
  MISSING_TYPE("0xC1069", "Cannot resolve type '%s'."),
  SOURCE_PORT_COMPONENT_MISSING("0xC1070", "Component instance '%s' missing for source of connector '%s'."),
  TARGET_PORT_COMPONENT_MISSING("0xC1071", "Component instance '%s' missing for target of connector '%s'."),
  TYPE_ARG_IGNORES_UPPER_BOUND("0xC1072", "Type argument '%s' does not respect upper bound '%s' for type parameter " +
    "'%s' of component type component type"),
  INSTANCE_NAME_NOT_UNIQUE_IN_MODE("0xC1073", "There are multiple subcomponents named '%s' in mode '%s'."),
  COMPONENT_NAME_NOT_UNIQUE_IN_MODE("0xC1074", "There are multiple components named '%s' in mode '%s'."),
  PORT_NAME_NOT_UNIQUE_IN_MODE("0xC1075", "There are multiple ports named '%s' in mode '%s'."),
  HIERARCHICAL_MODE_ELEMENTS("0xC1076", "Hierarchical modes are not allowed."),
  MULTIPLE_MODE_AUTOMATA("0xC1077", "Components may only have one mode-automaton at max."),
  MODES_WITHOUT_AUTOMATON("0xC1078", "The component '%s' defines modes, but no mode-automaton."),
  MODE_ELEMENTS_IN_ATOMIC_COMPONENTS("0xC1079", "Atomic components may not define modes and mode automata."),
  INITIAL_MODE_DOES_NOT_EXIST("0xC1080", "The initial mode '%s' is not defined anywhere in the component '%s'."),
  UNSUPPORTED_MODEL_ELEMENT("0xC1081", "%s"),
  PORT_REFERENCE_IN_INSTANTIATION_ARG_ILLEGAL("0xC1082", "An argument for the instantiation '%s' illegally relies " +
    "on the port '%s'"),
  TOO_FEW_INSTANTIATION_ARGUMENTS("0xC1083", "There are '%d' instantiation arguments for component instance '%s', " +
    "but component type '%s' has '%d' mandatory configuration parameters that all must be bound."),
  TOO_MANY_INSTANTIATION_ARGUMENTS("0xC1084", "There are '%d' instantiation arguments for component instance '%s', " +
    "but its component type '%s' only has '%d' configuration parameters. Please do not provide more arguments than " +
    "parameters exist."),
  INSTANTIATION_ARGUMENT_TYPE_MISMATCH("0xC1085", "The instantiation argument at position '%d' of component instance " +
    "'%s' is of type '%s' which is incompatible to type '%s' of the corresponding configuration parameter '%s' of " +
    "component type '%s'."),
  MALFORMED_EXPRESSION("0xC1086", "The expression at '%s' is malformed and can not be evaluated."),
  UNRESOLVABLE_IMPORT("0xC1087", "Can't resolve imported symbol '%s'.");

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
   * calls {@link String#format(String, Object...)} with this error message as template
   * @param args arguments for the format-call. The count has to match the string defined in {@link #printErrorMessage()}
   * @return properly formatted error message
   */
  public String format(Object... args){
    return String.format(toString(), args);
  }
}
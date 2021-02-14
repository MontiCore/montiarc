/* (c) https://github.com/MontiCore/monticore */
package arcbasis.util;

/**
 * The enum of all Arc errors. Extends the mixing interface {@link montiarc.util.Error}
 */
public enum ArcError implements montiarc.util.Error {
  CIRCULAR_INHERITANCE("0xMA1010", "Circular inheritance of component '%s' detected."),
  MISSING_TYPE_OF_PORT("0xMA1011", "Cannot resolve type of port '%s'."),
  MISSING_TYPE_OF_FIELD("0xMA1012", "Cannot resolve type of field '%s'."),
  MISSING_TYPE_OF_PARAMETER("0xMA1013", "Cannot resolve type of parameter '%s'."),
  MISSING_TYPE_OF_INHERITED_COMPONENT("0xMA1014", "Cannot resolve type of parent '%s' of "
    + "component '%s'."),
  MISSING_TYPE_OF_COMPONENT_INSTANCE("0xMA1015", "Cannot resolve type '%s' of component "
    + "instance '%s'."),
  INCOMING_PORT_AS_TARGET("0xMA1020", "Incoming port '%s' of component '%s' is target of a port "
    + "forward. Incoming ports may only be used as the source of a port forward."),
  OUTGOING_PORT_AST_SOURCE("0xMA1021", "Outgoing port '%s' of component '%s' is source of a port "
    + "forward. Outgoing ports may only be used as the target of a port forward."),
  INCOMING_PORT_NO_FORWARD("0xMA1022", "Incoming port '%s' of component '%s' is not connected "
    + "as a source of a port forward."),
  OUTGOING_PORT_NO_FORWARD("0xMA1023", "Outgoing port '%s' of component '%s' is not connected "
    + "as a target of a port forward."),
  PORT_MUlTIPPLE_SENDER("0xMA1024", "Target port ''%s' of connector is already connected."),
  INCOMING_PORT_AS_SOURCE("0xMA1025", "Incoming port '%s' of subcomponent '%s' of component type "
    + "'%s' is source of a connector. Incoming ports of subcomponents may only be used as the "
    + "target of a connector."),
  OUTGOING_PORT_AS_TARGET("0xMA1026", "Outgoing port '%s' of subcomponent '%s' of component type "
    + "'%S' is target of a connector. Outgoing ports of subcomponents may only be used as the "
    + "source of a connector."),
  INCOMING_PORT_NOT_CONNECTED("0xMA1027", "Incoming port '%s' of subcomponent '%s' of component "
    + "type '%s' is not connected."),
  OUTGOING_PORT_NOT_CONNECTED("0xMA1028", "Outgoing port '%s' of subcomponent '%s' of component "
    + "type '%s' is not connected."),
  INNER_WITH_TYPE_PARAMETER_REQUIRES_INSTANCE("0xMA1029", "Inner component '%s' must have an "
    + "instance defining its formal type parameters."),
  TO_FEW_CONFIGURATION_PARAMETER("0xMA1030", "There are too few configuration parameters. The "
    + "component '%s' requires at least %d configuration parameters"),
  CONFIGURATION_PARAMETER_TYPE_MISMATCH("0xMA1031",
    "Configuration parameter '%s' at position %d of "
      + "component %s is of type '%s', but it is required to be of type '%s'."),
  CONFIGURATION_PARAMETER_VALUE_MISMATCH("0xMA1032", "Configuration parameter '%s' at position %d"
    + " of component '%s' should have a default value but has none."),
  SOURCE_AND_TARGET_SAME_COMPONENT("0xMA1034", "Source and target of a connector of component "
    + "'%s' refer to the same subcomponent."),
  SOURCE_PORT_NOT_EXISTS("0xMA1035", "Source port '%s' of connector '%s' of component '%s' does not "
    + "exist."),
  TARGET_PORT_NOT_EXISTS("0xMA1036", "Target port '%s' of connector '%s' of component '%s' does not "
    + "exist."),
  SOURCE_AND_TARGET_TYPE_MISMATCH("0xMA1037", "Type '%s' of source port and type '%s' of target "
    + "port of connector '%s' of component '%s' are incompatible."),
  INNER_COMPONENT_EXTENDS_OUTER("0xMA1038", "Inner component type '%s' extends the " +
    "component type %s which is also its outer component type."),
  VARIABLE_LOWER_CASE("0xMA1039", "The name of the variable '%s' of component '%s' should start "
    + "with a lower case letter."),
  PORT_LOWER_CASE("0xMA1040", "The name of the port '%s' of component '%s' should start with a "
    + "lower case letter."),
  PARAMETER_LOWER_CASE("0xMA1041", "The name of the parameter '%s' of component '%s' should start"
    + " with a lower case letter."),
  TYPE_PARAMETER_UPPER_CASE_LETTER("0xMA1042", "The generic type parameter '%s' of component '%s'"
    + " should start with an upper case letter."),
  PORT_DIRECTION_MISMATCH("0xMA1043", "The %s-port '%s' can not be a %s of the connector '%s', because it is %s." + ""),
  CONNECTOR_SOURCE_AND_TARGET_ARE_IDENTICAL("0xMA1045", "The target port '%s' is identical to the"
    + " source port '%s'"),
  COMPONENT_NAME_UPPER_CASE("0xMA1055", "Component names must be in upper-case."
    + " So '%s' is an unsuitable name"),
  INSTANCE_NAME_LOWER_CASE("0xMA1056", "The name of the component instance '%s' should start"
    + " with a lower case letter.");

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
/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

/**
 * A Runtime exception if no value can be resolved for a component constructor parameter.
 */
public class ParameterResolutionException extends RuntimeException {

  public ParameterResolutionException() {
    super("No value for argument");
  }
}

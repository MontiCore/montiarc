/* (c) https://github.com/MontiCore/monticore */
package variability;

/**
 * Invalid model: Component field 'x' cannot be used in static context,
 * including default values of parameters, component arguments, constraints,
 * or varif conditions.
 */
component FieldReferenceInStaticContext(int p = x)
    extends ParameterizedSuperComponent(x) {

  component Inner(int p) { }

  int x = 1;

  Inner sub(x);
  constraint(x > 0);
  varif(x > 0) { }

}

/* (c) https://github.com/MontiCore/monticore */
package connectorSourceAndTargetTypesFit.subcomponentDefinitions;

/**
 * Valid model. (Given that List<T> is resolvable)
 */
component Generic<A> {
  port in A aIn;
  port out A aOut;
  port in List<A> aListIn;
  port out List<A> aListOut;
}

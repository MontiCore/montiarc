/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model.
 *
 * In the Java type system List<Integer> is not a subtype of List<Number>.
 * But ArrayList<Integer> is a subtype of List<Integer>
 */
component PortCompatibilityWithGenerics4{

  port in List<Integer> inIntList;
  port in ArrayList<Integer> inIntArrayList;

  component Inner1 myInner1{
    port in List<Number> inNumberList;
    port in List<Integer> inIntList;
  }

  connect inIntList -> myInner1.inNumberList; // Error: Types are not compatible
  connect inIntArrayList -> myInner1.inIntList; // Correct
}

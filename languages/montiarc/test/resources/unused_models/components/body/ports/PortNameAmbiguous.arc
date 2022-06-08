/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/*
 * Invalid model.
 * Port name 'foo' is ambiguous. Original name: "UniqueInnerComponentNames"
 *
 * @implements [Hab16] B1: All names of model elements within a component
 *  namespace have to be unique. (p. 59. Lst. 3.31)
 * @implements [Wor16] MU1: The name of each component variable is unique
 *  among ports, variables, and configuration parameters. (p. 54 Lst. 4.5)
 */
component PortNameAmbiguous(int foo) {
    // Parameter name is not unique
    
    port
      in String foo; // Port name is not unique
    
    component Inner foo {
    
    }
    
}

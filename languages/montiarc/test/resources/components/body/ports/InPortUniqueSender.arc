/* (c) https://github.com/MontiCore/monticore */
package components.body.ports;

/**
 * Valid model.
 */
component InPortUniqueSender {
  
  component A {
    port out String aOut;
  }
  
  component B {
    port in String bIn;
  }

  
  component B myB;
  
  component A myA
    [aOut -> myB.bIn];
}

/* (c) https://github.com/MontiCore/monticore */
package components.body.ajava;

/**
 * Invalid model. Only one init block per AJava component.
 */
component TwoInitBlocks {

  Integer testVariable;
  Integer testVariable2;

  init Block1 {
    testVariable = 10;
  }

  init Block2 {
    testVariable2 = 20;
  }
}

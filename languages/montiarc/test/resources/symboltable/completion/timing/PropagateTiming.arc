/* (c) https://github.com/MontiCore/monticore */
package completion.timing;

/*
 * Valid model.
 */
component PropagateTiming {
  port out int pDirectDelayed, pChainedDelayed, pNotDelayed;

  component Inner1 {
    port in int iIn;
    port <<delayed>> out int iOut;
  }

  component Inner2 {
    port in int iIn;
    port out int iOut;
  }

  component Inner3 {
    port in int iIn1, iIn2;
    port out int iOut;
  }

  Inner1 inner1;
  Inner2 inner2;

  inner1.iOut -> pDirectDelayed; // Port is delayed because of sOut
  inner1.iOut -> inner2.iIn;
  inner2.iOut -> pChainedDelayed; // Port should be considered delayed because of previous connection

  Inner2 inner22;
  Inner3 inner3;

  inner22.iOut -> inner3.iIn1;
  inner1.iOut -> inner3.iIn2;
  inner3.iOut -> pNotDelayed; // Port is not delayed
}
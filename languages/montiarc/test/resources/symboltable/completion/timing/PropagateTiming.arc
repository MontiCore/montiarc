/* (c) https://github.com/MontiCore/monticore */
package completion.timing;

/*
 * Valid model.
 */
component PropagateTiming {
  port in int iIn;
  port out int pDirectDelayed, pChainedDelayed, pNotDelayed, pNoPathToIn;

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

  component Inner4 {
    port out int iOut;
  }

  Inner1 inner1;
  Inner2 inner2;

  iIn -> inner1.iIn;
  inner1.iOut -> pDirectDelayed; // Port is delayed because of sOut, thus also strongly causal
  inner1.iOut -> inner2.iIn;
  inner2.iOut -> pChainedDelayed; // Port is not delayed, but strongly causal because inner1.iOut is delayed

  Inner2 inner22;
  Inner3 inner3;

  iIn -> inner22.iIn;
  inner22.iOut -> inner3.iIn1;
  inner1.iOut -> inner3.iIn2;
  inner3.iOut -> pNotDelayed; // Port is not delayed and not strongly causal because there is an undelayed path to an input port

  Inner4 inner4;
  inner4.iOut -> pNoPathToIn; // Port is not delayed but strongly causal because there is no path to an input port
}
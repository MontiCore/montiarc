/* (c) https://github.com/MontiCore/monticore */
package variantCoCos;

/**
 * Invalid model.
 */
component InvalidFeedbackLoop {
  feature f1, f2;

  A a1(true);
  A a2(true);
  A a3(f1);

  a1.pOut -> a2.pIn;
  a2.pOut -> a3.pIn;
  if (f2) {
    a3.pOut -> a1.pIn; // undelayed circle for (f1==true && f2 == true)
  } else {
    // Helpers so no other error is logged (e.g. unconnected a1.pIn)
    port <<sync>> in int pIn;
    port <<sync>> out int pOut;
    pIn -> a1.pIn;
    a3.pOut -> pOut;
  }

  component A(boolean b) {
    port <<sync>> in int pIn;
    if (b) {
      port <<sync>> out int pOut;
    } else {
      port <<sync, delayed>> out int pOut;
    }
  }
}

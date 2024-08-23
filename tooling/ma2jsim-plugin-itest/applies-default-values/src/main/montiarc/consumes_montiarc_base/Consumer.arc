/* (c) https://github.com/MontiCore/monticore */
package montiarc.consumes_montiarc_base;

// Tests that the montiarc-base library can be accessed
// (should be shipped together with the plugin)
import montiarc.lang.logic.gate.And;

component Consumer {
  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

   And gate;

   a -> gate.a;
   b -> gate.b;
   gate.q -> q;
}

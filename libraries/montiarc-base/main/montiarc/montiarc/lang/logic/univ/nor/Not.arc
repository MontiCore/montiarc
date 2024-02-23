/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import montiarc.lang.logic.gate.Nor;

component Not {

  port <<sync>> in boolean a;
  port <<sync>> out boolean q;

  Nor nor;
  a -> nor.a;
  a -> nor.b;

  nor.q -> q;
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import montiarc.lang.logic.gate.Nor;

component Nand {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nor notA;
  a -> notA.a;
  a -> notA.b;

  Nor notB;
  b -> notB.a;
  b -> notB.b;

  Nor nor;
  notA.q -> nor.a;
  notB.q -> nor.b;

  Nor not;
  nor.q -> not.a;
  nor.q -> not.b;

  not.q -> q;
}

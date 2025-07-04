/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.composition;

import montiarc.timed.automata.Nor;

component And {
  port in boolean a;
  port in boolean b;
  port out boolean q;

  Nor notA;
  a -> notA.a;
  a -> notA.b;

  Nor notB;
  b -> notB.a;
  b -> notB.b;

  Nor nor;
  notA.q -> nor.a;
  notB.q -> nor.b;

  nor.q -> q;
}

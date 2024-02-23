/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.univ.nor;

import montiarc.lang.logic.gate.Nor;

component Or {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  Nor nor1;
  a -> nor1.a;
  b -> nor1.b;

  Nor nor2;
  nor1.q -> nor2.a;
  nor1.q -> nor2.b;

  nor2.q -> q;
}

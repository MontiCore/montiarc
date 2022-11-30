/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.circuit;

import montiarc.lang.logic.gate.Or;

component FullAdder {

  // the augend bit
  port <<sync>> in boolean a;

  // the addend bit
  port <<sync>> in boolean b;

  // the input carry bit
  port <<sync>> in boolean caIn;

  // the sum of a and b
  port <<sync>> out boolean sum;

  // the output carry bit
  port <<sync>> out boolean caOut;

  HalfAdder ha0;

  a -> ha0.a;
  b -> ha0.b;

  HalfAdder ha1;

  caIn -> ha1.a;
  ha0.sum -> ha1.b;
  ha1.sum -> sum;

  Or or;

  ha0.ca -> or.a;
  ha1.ca -> or.b;
  or.q -> caOut;

}

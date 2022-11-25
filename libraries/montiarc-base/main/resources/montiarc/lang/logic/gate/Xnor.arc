/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

component Xnor {

  port <<sync>> in boolean a,
       <<sync>> in boolean b;
  port <<sync>> out boolean q;

  compute {
    q = (a && b) || (!a && !b);
  }
}

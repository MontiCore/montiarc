/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic.gate;

component Not {

  port <<sync>> in boolean a;
  port <<sync>> out boolean q;

  compute {
    q = !a;
  }
}

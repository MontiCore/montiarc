/* (c) https://github.com/MontiCore/monticore */
package montiarc.lang.logic;

component Delay {

  port <<sync>> in boolean i;
  port <<sync, delayed>> out boolean o;

  init {
    o = false;
  }

  compute {
    o = i;
  }

}

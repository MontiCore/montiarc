/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

component SimulationInteraction {

  port sync out Duration o;

  automaton {
    initial state S;

    S -> S / {
      o = Simulation.getTickLength();
      Simulation.stop();
    }
  }
}

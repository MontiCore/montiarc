/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

public interface EventInverterContext extends EventInverterInput, EventInverterOutput {

  EventInverterAut getBehavior();
}

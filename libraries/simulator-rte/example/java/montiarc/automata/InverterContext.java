/* (c) https://github.com/MontiCore/monticore */
package montiarc.automata;

public interface InverterContext extends InverterInput, InverterOutput {

  InverterAut getBehavior();
}

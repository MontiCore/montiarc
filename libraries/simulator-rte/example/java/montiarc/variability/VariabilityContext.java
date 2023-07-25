/* (c) https://github.com/MontiCore/monticore */
package montiarc.variability;

public interface VariabilityContext extends VariabilityInput, VariabilityOutput {
  
  VariabilityAut getBehavior();
}

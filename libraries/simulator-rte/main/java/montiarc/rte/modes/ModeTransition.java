/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.modes;

/**
 * Used as functional type for references to methods that define transition
 * execution logic
 */
@FunctionalInterface
public interface ModeTransition {
  void execute();
}

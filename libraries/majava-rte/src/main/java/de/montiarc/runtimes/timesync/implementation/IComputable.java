/* (c) https://github.com/MontiCore/monticore */
package de.montiarc.runtimes.timesync.implementation;

/**
 * The interface of component implementations used by the java delegation generator for time-sync
 * systems.
 */
public interface IComputable<I extends IInput, R extends IResult> {    
  R getInitialValues();
  R compute(I input);
}

/* (c) https://github.com/MontiCore/monticore */
package de.montiarcautomaton.runtimes.timesync.implementation;

/**
 * The interface of component implementations used by the generator
 * java-timesync-delegation
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public interface IComputable<I extends IInput, R extends IResult> {    
  public R getInitialValues();
  public R compute(I input); 
}

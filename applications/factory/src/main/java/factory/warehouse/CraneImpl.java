/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import de.montiarc.runtimes.timesync.implementation.IComputable;

public class CraneImpl implements IComputable<CraneInput, CraneResult> {
  
  private final static Position start = new Position();
  
  
  
  @Override
  public CraneResult getInitialValues() {
    return new CraneResult(start, CraneState.EMPTY, MovementState.WAITING, null);
  }
  
  @Override
  public CraneResult compute(CraneInput input) {
    return new CraneResult(start, CraneState.EMPTY, MovementState.WAITING, null);
  }
}
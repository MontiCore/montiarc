/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import de.montiarc.runtimes.timesync.implementation.IComputable;
import factory.warehouse.Warehouse.CraneState;
import factory.warehouse.Warehouse.MovementState;
import factory.warehouse.Warehouse.Position;

public class CraneImpl implements IComputable<CraneInput, CraneResult> {
  
  private final static Position start = new Position(0,0,0);
  
  
  
  @Override
  public CraneResult getInitialValues() {
    return new CraneResult(start, CraneState.EMPTY, MovementState.WAITING, null);
  }
  
  @Override
  public CraneResult compute(CraneInput input) {
    return new CraneResult(start, CraneState.EMPTY, MovementState.WAITING, null);
  }
}
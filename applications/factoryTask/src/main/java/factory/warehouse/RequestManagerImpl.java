/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import de.montiarc.runtimes.timesync.implementation.IComputable;
import factory.warehouse.Warehouse.Position;

public class RequestManagerImpl implements IComputable<RequestManagerInput, RequestManagerResult> {
  
  private final static Position start = new Position(0,0,0);
  
  @Override
  public RequestManagerResult getInitialValues() {
    return new RequestManagerResult(start, start, start, start, null, null);
  }
  
  @Override
  public RequestManagerResult compute(RequestManagerInput input) {
    return new RequestManagerResult(start, start, start, start, null, null);
  }
}
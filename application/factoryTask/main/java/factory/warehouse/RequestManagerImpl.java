/* (c) https://github.com/MontiCore/monticore */
package factory.warehouse;

import de.montiarc.runtimes.timesync.implementation.IComputable;

public class RequestManagerImpl implements IComputable<RequestManagerInput, RequestManagerResult> {
  
  private final static Position start = new Position();
  
  @Override
  public RequestManagerResult getInitialValues() {
    return new RequestManagerResult(start, start, start, start, null, null);
  }
  
  @Override
  public RequestManagerResult compute(RequestManagerInput input) {
    return new RequestManagerResult(start, start, start, start, null, null);
  }
}
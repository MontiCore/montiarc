
package portTest.ocStore;

import java.lang.*;
import java.util.*;
import de.montiarcautomaton.runtimes.timesync.implementation.IResult;


public class OutCompResult   
implements IResult 
 {
  
  private String outPort;
  
  public OutCompResult() {
  }
  
  public OutCompResult( String outPort ) {
    this.outPort = outPort; 
    
  }

  public void setOutPort(String outPort) {
    this.outPort = outPort;
  }
  
  public String getOutPort() {
    return this.outPort;
  }

  @Override
  public String toString() {
    String result = "[";
    result += "outPort: " + this.outPort + " ";
    return result + "]";
  }  
  
} 


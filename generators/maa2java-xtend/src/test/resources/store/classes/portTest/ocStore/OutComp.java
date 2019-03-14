
package portTest.ocStore;

import java.lang.*;
import java.util.*;
import portTest.ocStore
.OutCompInput;
import portTest.ocStore
.OutCompResult;
import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.runtimes.Log;

public class OutComp      
    implements IComponent {
    
  //ports
  
  protected Port<String> outPort;
  
  public Port<String> getPortOutPort() {
        return this.outPort;
  }
  
  public void setPortOutPort(Port<String> outPort) {
        this.outPort = outPort;
  }
  
  
  // component variables
  
  // config parameters

  // the components behavior implementation
  private final IComputable<OutCompInput, OutCompResult> behaviorImpl;
  
  @Override
  public void compute() {
  // collect current input port values
  final OutCompInput input = new OutCompInput
  ();
  
  try {
  // perform calculations
    final OutCompResult result = behaviorImpl.compute(input); 
    
    // set results to ports
    setResult(result);
    } catch (Exception e) {
  Log.error("OutComp", e);
    }
  }
  private void initialize() {
    // get initial values from behavior implementation
    final OutCompResult result = behaviorImpl.getInitialValues();
    
    // set results to ports
    setResult(result);
    this.update();
  }
  private void setResult(OutCompResult result) {
    this.getPortOutPort().setNextValue(result.getOutPort());
  }
  
  @Override
  public void setUp() {
  
  
  // set up output ports
  this.outPort = new Port<String>();
  
  this.initialize();
  
  }
  
  @Override
  public void init() {
  // set up unused input ports
  }
  
  @Override
  public void update() {
  
    // update computed value for next computation cycle in all outgoing ports
    this.outPort.update();
  }
  
  public OutComp() {
    
    behaviorImpl = new OutCompImpl(
  );
    // config parameters       
  }
  
  }
  

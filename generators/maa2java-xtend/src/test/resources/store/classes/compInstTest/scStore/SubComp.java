package compInstTest.scStore;


import java.lang.*;
import java.util.*;
import compInstTest.scStore.SubCompInput;
import compInstTest.scStore.SubCompResult;
import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.runtimes.Log;

public class SubComp      
    implements IComponent {
    
  //ports
  
  // component variables
  
  // config parameters

  // the components behavior implementation
  private final IComputable<SubCompInput, SubCompResult> behaviorImpl;
  
  @Override
  public void compute() {
  // collect current input port values
  final SubCompInput input = new SubCompInput
  ();
  
  try {
  // perform calculations
    final SubCompResult result = behaviorImpl.compute(input); 
    
    // set results to ports
    setResult(result);
    } catch (Exception e) {
  Log.error("SubComp", e);
    }
  }
  private void initialize() {
    // get initial values from behavior implementation
    final SubCompResult result = behaviorImpl.getInitialValues();
    
    // set results to ports
    setResult(result);
    this.update();
  }
  private void setResult(SubCompResult result) {
  }
  
  @Override
  public void setUp() {
  
  
  // set up output ports
  
  this.initialize();
  
  }
  
  @Override
  public void init() {
  // set up unused input ports
  }
  
  @Override
  public void update() {
  
    // update computed value for next computation cycle in all outgoing ports
  }
  
  public SubComp() {
    
    behaviorImpl = new SubCompImpl(
  );
    // config parameters       
  }
  
  }
  

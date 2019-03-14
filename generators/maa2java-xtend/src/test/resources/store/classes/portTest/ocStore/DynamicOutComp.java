package portTest.ocStore;


import java.lang.*;
import java.util.*;
import portTest.ocStore.OutCompInput;
import portTest.ocStore.OutCompResult;
import de.montiarcautomaton.runtimes.componentinstantiation.IDynamicComponent;
import de.montiarcautomaton.runtimes.componentinstantiation.InterfaceChecker;
import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.componentinstantiation.ILoader;
import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
import de.montiarcautomaton.runtimes.Log;
import java.util.*;

 
 public class DynamicOutComp      
      implements IDynamicComponent {
     
   //ports
   
   protected Port<String> outPort;
   
   public Port<String> getPortOutPort() {
         return this.outPort;
   }
   
   public void setPortOutPort(Port<String> outPort) {
         this.outPort = outPort;
   }
   
   
   // component variables
   private String instanceName = null;
   private String storeDir = null;
   private String targetDir = null;
   private LoaderManager loman;
   private ILoader loader;
   
   
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
   
   @Override
   public List<Port> reconfigure() {
   	return new ArrayList<>();
   }
   
   @Override
   public <T> void setPort(String name, Port<T> port) {
   if (name.equals("outPort")){
   	setPortOutPort((Port<String>) port);
   }
   	
   }
   @Override
   public <T> Port<T> getPort(String name) {
   if (name.equals("outPort")){
   	return (Port<T>) getPortOutPort();
   }
   	return null;     	
   }
   
   
   @Override
   public List<Port> getPorts(){
   	List<Port> ports = new ArrayList<>();
   	ports.add(getPortOutPort());
   	return ports;
   }
   
   @Override
   public void checkForCmp() {
   }
   
   @Override
     public void propagatePortChanges(List<Port> changedPorts) {
     	
       }
   
   
   @Override
     public void setLoaderConfiguration(String instanceName, String storeDir, String targetDir, LoaderManager loaderManager) {
       this.instanceName = instanceName;
       this.storeDir = storeDir;
       this.targetDir = targetDir;
       this.loman = loaderManager;
       
     }
   
   
   @Override
   public String getInstanceName() {
       return instanceName;
   }
   
   
   @Override
   public List<String> getInterface() {
   	List<String> compInterface = new ArrayList<>();
   	compInterface.add("Out-outPort-String");
   	
       return compInterface;
   }
   
   
   
   
   public DynamicOutComp() {
     
     behaviorImpl = new OutCompImpl(
   );
     // config parameters       
   }
   
   }
   

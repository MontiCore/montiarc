package compInstTest.scStore;


import java.lang.*;
import java.util.*;
import compInstTest.scStore.SubCompInput;
import compInstTest.scStore.SubCompResult;
import de.montiarcautomaton.runtimes.componentinstantiation.IDynamicComponent;
import de.montiarcautomaton.runtimes.componentinstantiation.InterfaceChecker;
import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
import de.montiarcautomaton.runtimes.componentinstantiation.LoaderManager;
import de.montiarcautomaton.runtimes.timesync.delegation.Port;
import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
import de.montiarcautomaton.generator.codegen.componentinstantiation.FileSystemLoader;
import de.montiarcautomaton.runtimes.Log;
import java.util.*;

 
 public class DynamicSubComp      
      implements IDynamicComponent {
     
   //ports
   
   // component variables
   private String instanceName = null;
   private String storeDir = null;
   private String targetDir = null;
   private LoaderManager loman;
   private FileSystemLoader loader;
   
   
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
   
   @Override
   public List<Port> reconfigure() {
   	return new ArrayList<>();
   }
   
   @Override
   public <T> void setPort(String name, Port<T> port) {
   	
   }
   @Override
   public <T> Port<T> getPort(String name) {
   	return null;     	
   }
   
   
   @Override
   public List<Port> getPorts(){
   	List<Port> ports = new ArrayList<>();
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
       return compInterface;
   }
   
   
   
   
   public DynamicSubComp() {
     
     behaviorImpl = new SubCompImpl(
   );
     // config parameters       
   }
   
   }
   

/* (c) https://github.com/MontiCore/monticore */
package sim.serialiser;

import sim.automaton.ComponentState;
import sim.comp.IComponent;
import sim.comp.ISimComponent;
import sim.message.TickedMessage;
import sim.port.IInSimPort;
import sim.port.IPort;
import sim.sched.IScheduler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BackTrackHandler implements Serializable{


  private Graph<Long> breadthsearchgraph;
  private String btpath;

  private long stateid;

  private Queue<Long> activepaths;

  private int calcstepsize;

  private int leftoversteps=0;

  private int stepTimeOut;

  private int currentStepTimeOut;

  private boolean isBreadthSearch = false;

  private List<IComponent> compList;

  private IScheduler scheduler;




  public BackTrackHandler(String btpath, int calcstepsize, int stepTimeOut) {
    File dir = new File(btpath);
    if (!dir.exists()) {
      dir.mkdir();
    }

    breadthsearchgraph = new Graph<>();
    stateid = 0L;
    breadthsearchgraph.addVertex(stateid);
    compList = new LinkedList<>();
    this.btpath=btpath;
    activepaths = new LinkedList<>();
    this.calcstepsize=calcstepsize;
    this.stepTimeOut=stepTimeOut;
    currentStepTimeOut=stepTimeOut;
  }

  /**
   * Serializes a given Object to the fileName
   * @param fileName Name of the file
   * @param data Object containing the data to be serialized
   */
  public void serialize(String fileName, Object data){
    try (FileOutputStream fos = new FileOutputStream(fileName);
         ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Object deserialize(String fileName){
    try (FileInputStream fis = new FileInputStream(fileName);
         ObjectInputStream ois = new ObjectInputStream(fis)) {
      return ois.readObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }


  /**
   * saves the current state of the system after a systemstate change
   * @param cs Last componentState that changed the Systemstate
   * @param stateid Pathid for the current Systemstate
   */
  public void serializeSystemState(ComponentState cs, Long stateid) {
    String fileName = btpath + stateid + "SystemState.ser";
    List<ComponentState> systemState = new LinkedList<>();

    for (IComponent comp : compList) {
      if (!comp.getComponentName().equals(cs.getComponentName())) {
        systemState.add(comp.getComponentState());
      }
      systemState.add(cs);
      serialize(fileName, systemState);
    }
  }

  /**
   * saves the current State of the system during a time without a systemstate change
   * @param stateid Pathid for the current Systemstate
   */

  public void serializeSystemState(Long stateid){
    String fileName = btpath + stateid + "SystemState.ser";
    List<ComponentState> systemState = new LinkedList<>();

    for (IComponent comp : compList) {
        systemState.add(comp.getComponentState());
      }
      serialize(fileName, systemState);
  }


  public List<ComponentState> deserializeSystemState(Long stateid){
    String fileName = btpath + stateid + "SystemState.ser";
    return (List<ComponentState>) deserialize(fileName);

  }

  public void saveMessageQueue(Long stateid){
    List<Queue<TickedMessage<?>>> portMessageQueques = new LinkedList<>();
    for (ISimComponent comp:scheduler.getComp2SimPorts().keySet()){
      for(IInSimPort port: scheduler.getComp2SimPorts().get(comp)){
        portMessageQueques.add(port.getMessageQueue());
      }
    }
    String fileName = btpath + stateid + "MessageQueue.ser";
    serialize(fileName, portMessageQueques);
  }

  public List<Queue<TickedMessage<?>>> loadMessageQueue(Long stateid){
    String fileName = btpath + stateid + "MessageQueue.ser";
    return (List<Queue<TickedMessage<?>>>) deserialize(fileName);
  }


  public void savePortMsg(IPort port, TickedMessage message) {
    String fileName = btpath + stateid + "Port" + port.getComponent().getComponentName() + port.getPortNumber() + ".ser";
    Stack<TickedMessage> portStack = new Stack<>();
    if (FileExists(fileName)) {
      portStack = loadPortMsg(port);
    }
    portStack.add(message);
    try (FileOutputStream fos = new FileOutputStream(fileName);
         ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(portStack);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Stack<TickedMessage> loadPortMsg(IPort port) {
    try (FileInputStream fis = new FileInputStream(btpath + stateid + "Port" + port.getComponent().getComponentName() + port.getPortNumber() + ".ser");
         ObjectInputStream ois = new ObjectInputStream(fis)) {
      return (Stack<TickedMessage>) ois.readObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Boolean FileExists(String fileName) {
    return Files.exists(Path.of(fileName));
  }

  public long msg2ID(TickedMessage msg, IPort port) {
    Stack<TickedMessage> portstack = loadPortMsg(port);
    return portstack.size() - portstack.indexOf(msg);
  }

  public Long saveSystemState(ComponentState cs){
    long newStateID = breadthsearchgraph.getVertexCount();
    if(cs.getOutMessages()!= null) {
      cs.setMessageNotSend(true);
    }
    activepaths.add(newStateID);
    serializeSystemState(cs, newStateID);
    saveMessageQueue(newStateID);
    breadthsearchgraph.addEdge(stateid, newStateID);
    return newStateID;
  }

  public ComponentState handleUnderspecified(List<ComponentState> componentStates) {
    Long activeID = (long) breadthsearchgraph.getVertexCount();
    for(ComponentState cs: componentStates){
      if(cs.getOutMessages() != null) {
        cs.setMessageNotSend(true);
      }
      activepaths.add(activeID);
      breadthsearchgraph.addEdge(stateid,activeID);
      serializeSystemState(cs, activeID);
      saveMessageQueue(activeID);
      activeID++;
    }

    return componentStates.get(0);
  }


  public ComponentState checkInComp(IComponent comp, List<ComponentState> componentStates){
    ComponentState output = null;
    //BreadthSearch is active
    if(isBreadthSearch){
      leftoversteps--;
      //Stepsize is reached
      if(leftoversteps == 0){

        if (componentStates.size() == 1) {
          saveSystemState(componentStates.get(0));
        }else {
          handleUnderspecified(componentStates);
        }
        stateid = choosenextpathID();
        setMessageQueue(loadMessageQueue(stateid));
        loadSystemstate(stateid);
      }else {
        if(componentStates.size() ==1) {
          stateid = saveSystemState(componentStates.get(0));
          output = componentStates.get(0);
        }
        //still in stepsize, but underspecified
        else{
          handleUnderspecified(componentStates);
          stateid = choosenextpathID();
          setMessageQueue(loadMessageQueue(stateid));
          loadSystemstate(stateid);
        }
      }
    }
    //no breadthsearch is active
    else {
      if (componentStates.size() == 1) {
        stateid = saveSystemState(componentStates.get(0));
        output = componentStates.get(0);
      }else {
        isBreadthSearch = true;
        leftoversteps = calcstepsize;
        handleUnderspecified(componentStates);
        stateid = choosenextpathID();
        setMessageQueue(loadMessageQueue(stateid));
        loadSystemstate(stateid);
      }
    }

    return output;
  }

  public void checkInSched(){
    if(isBreadthSearch){
      currentStepTimeOut--;
      if(currentStepTimeOut == 0){
        stateid = choosenextpathID();
        setMessageQueue(loadMessageQueue(stateid));
        loadSystemstate(stateid);
      }
    }
  }

  public void loadSystemstate(Long pathid){
    setComponentStates(deserializeSystemState(pathid));
  }

  public Long choosenextpathID(){
    if(activepaths.isEmpty()){
      isBreadthSearch=false;
      currentStepTimeOut = stepTimeOut;
      leftoversteps = calcstepsize;
      return stateid;
    }else {
      currentStepTimeOut = stepTimeOut;
      leftoversteps = calcstepsize;
      return activepaths.poll();
    }
  }

  public String getBtpath() {
    return btpath;
  }

  public void setBtpath(String btpath) {
    this.btpath = btpath;
  }

  public void addComptoCompList(IComponent comp){
    compList.add(comp);

  }

  /**
   * Sets each Component in the System to the specified ComponentState
   */
  public void setComponentStates(List<ComponentState> ComponentStates){
    for(ComponentState cs : ComponentStates){
      for(IComponent comp: compList){
        if(comp.getComponentName().equals(cs.getComponentName())){
          comp.setComponentState(cs);
        }
      }
    }
  }



  public void setMessageQueue(List<Queue<TickedMessage<?>>> messageQueue){
    int i = 0;
    for (ISimComponent comp:scheduler.getComp2SimPorts().keySet()){
      for(IInSimPort port: scheduler.getComp2SimPorts().get(comp)){
        port.setMessageQueue(messageQueue.get(i));
        i++;
      }
    }
  }

  public void setScheduler(IScheduler scheduler) {
    this.scheduler = scheduler;
  }
}

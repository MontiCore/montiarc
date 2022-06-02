/* (c) https://github.com/MontiCore/monticore */
package sim.serialiser;

import sim.automaton.ComponentState;
import sim.comp.IComponent;
import sim.message.TickedMessage;
import sim.port.IPort;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class BackTrackHandler {

  private String btpath;

  private long stateid;

  private Queue<ComponentState> breadthfirst;

  public BackTrackHandler(String btpath) {
    stateid = 0L;
    File dir = new File(btpath);
    if (!dir.exists()) {
      dir.mkdir();
    }
    this.btpath = btpath + String.valueOf(stateid);
    breadthfirst = new LinkedList<>();
  }

  public void saveComponentState(IComponent comp, ComponentState cs) {
    String fileName = btpath + "Comp" + comp.getComponentName() + ".ser";
    Stack<ComponentState> componentStatesStack = new Stack<>();
    Long currentIDPool = 0L;

    if (cs.getInMessage() != null) {
      cs.setInMessageID(msg2ID(cs.getInMessage(), cs.getInputPort()));
    }

    if (FileExists(fileName)) {
      componentStatesStack = loadComponentState(comp);
    }
    componentStatesStack.add(cs);
    try (FileOutputStream fos = new FileOutputStream(fileName);
         ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(componentStatesStack);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Stack<ComponentState> loadComponentState(IComponent comp) {
    try (FileInputStream fis = new FileInputStream(btpath + "Comp" + comp.getComponentName() + ".ser");
         ObjectInputStream ois = new ObjectInputStream(fis)) {
      return (Stack<ComponentState>) ois.readObject();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void savePortMsg(IPort port, TickedMessage message) {
    String fileName = btpath + "Port" + port.getComponent().getComponentName() + port.getPortNumber() + ".ser";
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
    try (FileInputStream fis = new FileInputStream(btpath + "Port" + port.getComponent().getComponentName() + port.getPortNumber() + ".ser");
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

  public ComponentState handleUnderspecified(IComponent comp, List<ComponentState> componentStates) {
    stateid++;
    setBtpath(btpath + stateid);
    ComponentState output = componentStates.get(0);
    componentStates.remove(0);
    breadthfirst.addAll(componentStates);
    return output;
  }

  public String getBtpath() {
    return btpath;
  }

  public void setBtpath(String btpath) {
    this.btpath = btpath;
  }
}

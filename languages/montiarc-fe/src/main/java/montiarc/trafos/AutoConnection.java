/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.trafos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcConstants;
import montiarc._ast.*;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc._symboltable.PortSymbol;
import montiarc.helper.AutoconnectMode;
import montiarc.helper.PortCompatibilityChecker;

/**
 * Creates further connectors depending on the auto connect mode (type, port, off)
 *
 * @author ahaber, Robert Heim
 * @since 1.0.0 (14.11.2012)
 */
public class AutoConnection {
  
  private Stack<List<AutoconnectMode>> modeStack = new Stack<List<AutoconnectMode>>();
  
  public static ASTConnector createASTConnector(ASTComponent node, String source, String target) {
    // get Connector Builder
    ASTConnectorBuilder builder = MontiArcMill.connectorBuilder();
    
    ASTQualifiedName sourceQN = TypesNodeFactory.createASTQualifiedName();
    sourceQN.setPartList(Splitters.DOT.splitToList(source));
    
    
    ASTQualifiedName targetQN = TypesNodeFactory.createASTQualifiedName();
    targetQN.setPartList(Splitters.DOT.splitToList(target));
    
    Optional<ASTMontiArcAutoConnect> auto = resolveAutoconnect(node);
    if (auto.isPresent()) {
      builder.set_SourcePositionStart(auto.get().get_SourcePositionStart());
      builder.set_SourcePositionEnd(auto.get().get_SourcePositionEnd());
    }
  
    builder.setSource(sourceQN).addTargets(targetQN);
    return builder.build();
  }
  
  /**
   * @param comp used component node.
   * @return searches the autoconnect statement in the given comp ast. returns empty, if it does not
   * exist.
   */
  public static Optional<ASTMontiArcAutoConnect> resolveAutoconnect(ASTComponent comp) {
    for (ASTElement element : comp.getBody().getElementList()) {
      if (element instanceof ASTMontiArcAutoConnect) {
        return Optional.of((ASTMontiArcAutoConnect) element);
      }
    }
    return Optional.empty();
  }
  
  public void transformAtStart(ASTComponent node, ComponentSymbol currentComp) {
    modeStack.push(new ArrayList<>());
  }
  
  public void transform(ASTMontiArcAutoConnect node, ComponentSymbol currentComp) {
    List<AutoconnectMode> modes = modeStack.peek();
    // add current mode
    if (node.isPort()) {
      modes.add(AutoconnectMode.AUTOCONNECT_PORT);
    }
    else if (node.isType()) {
      modes.add(AutoconnectMode.AUTOCONNECT_TYPE);
    }
    else if (node.isOff()) {
      modes.add(AutoconnectMode.OFF);
    }
  }
  
  public void transformAtEnd(ASTComponent node, ComponentSymbol currentComp) {
    List<AutoconnectMode> allModes = this.modeStack.peek();
    if (allModes.isEmpty()) {
      allModes.add(MontiArcConstants.DEFAULT_AUTO_CONNECT);
    }
    for (AutoconnectMode mode : allModes) {
      if (mode != AutoconnectMode.OFF && currentComp.getSubComponents().size() > 0) {
        createAutoconnections(currentComp, node, mode);
      }
    }
    this.modeStack.pop();
  }
  
  /**
   * Creates further connectors depending on the auto connect mode (type, port, off).
   *
   * @param currentComponent symbol table entry of the currently processed component
   * @param node component node in the AST
   * @param mode auto connection mode
   */
  private void createAutoconnections(ComponentSymbol currentComponent, ASTComponent node,
      AutoconnectMode mode) {
    Map<String, PorWithGenericBindings> unusedSenders = getUnusedSenders(
        currentComponent);
    Map<String, PorWithGenericBindings> unusedReceivers = getUnusedReceivers(
        currentComponent);
    for (Entry<String, PorWithGenericBindings> receiverEntry : unusedReceivers.entrySet()) {
      String receiver = receiverEntry.getKey();
      int indexReceiver = receiver.indexOf('.');
      List<ASTConnector> matches = new LinkedList<>();
      
      for (Entry<String, PorWithGenericBindings> senderEntry : unusedSenders.entrySet()) {
        String sender = senderEntry.getKey();
        int indexSender = sender.indexOf('.');
        
        boolean matched = false;
        
        PorWithGenericBindings senderPort = senderEntry.getValue();
        PorWithGenericBindings receiverPort = receiverEntry.getValue();
        
        boolean portTypesMatch = PortCompatibilityChecker.doPortTypesMatch(senderPort.port,
            senderPort.formalTypeParameters, senderPort.typeArguments, receiverPort.port,
            receiverPort.formalTypeParameters, receiverPort.typeArguments);
        // sender from current component, receiver from a reference
        if (portTypesMatch) {
          if (indexSender == -1 && indexReceiver != -1) {
            String receiverPortName = receiver.substring(indexReceiver + 1);
            
            if (mode == AutoconnectMode.AUTOCONNECT_PORT && receiverPortName.equals(sender)) {
              matched = true;
            }
            else if (mode == AutoconnectMode.AUTOCONNECT_TYPE) {
              matched = true;
            }
            
          }
          // sender from a reference, receiver from current component
          else if (indexSender != -1 && indexReceiver == -1) {
            String senderPortName = sender.substring(indexSender + 1);
            
            if (mode == AutoconnectMode.AUTOCONNECT_PORT && senderPortName.equals(receiver)) {
              matched = true;
            }
            else if (mode == AutoconnectMode.AUTOCONNECT_TYPE) {
              matched = true;
            }
            
          }
          // both from referenced components
          else if (indexSender != -1 && indexReceiver != -1) {
            String senderPortName = sender.substring(indexSender + 1);
            String receiverPortName = receiver.substring(indexReceiver + 1);
            String senderRef = sender.substring(0, indexSender);
            String receiverRef = receiver.substring(0, indexReceiver);
            // check if sender and receiver are from different references
            if (!senderRef.equals(receiverRef)) {
              if (mode == AutoconnectMode.AUTOCONNECT_PORT
                  && senderPortName.equals(receiverPortName)) {
                matched = true;
              }
              else if (mode == AutoconnectMode.AUTOCONNECT_TYPE) {
                matched = true;
              }
              
            }
          }
        }
        // create connector entry and add to matched
        if (matched) {
          ASTConnector conEntry = createASTConnector(node, sender, receiver);
          
          matches.add(conEntry);
        }
      }
      
      if (matches.size() == 1) {
        ASTConnector astConnector = matches.iterator().next();
        
        // add node to arc elements
        node.getBody().getElementList().add(astConnector);
        
        Log.info(node.get_SourcePositionStart() + " Created connector '" + astConnector + "'.",
            "AutoConnection");
      }
      else if (matches.size() > 1) {
        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate autoconnection matches for the connector target '");
        sb.append(matches.iterator().next().getTargets(0));
        sb.append("' with the sources '");
        sb.append(matches.stream().map(c -> c.getSource().toString()).collect(Collectors.joining("', '")));
        sb.append("'!");
        Log.warn(sb.toString());
      }
      else {
        Log.warn("Unable to autoconnect port '" + receiver + "'.");
      }
    }
  }
  
  /**
   * Finds all incoming ports of a given component (and all referenced components) which are not
   * connected.
   *
   * @param currentComponent the component for which to find the unused ports
   * @return unused incoming ports of a given component represented in a map from port names to
   * types
   */
  private Map<String, PorWithGenericBindings> getUnusedReceivers(
      ComponentSymbol currentComponent) {
    // portname, porttypebinding
    Map<String, PorWithGenericBindings> unusedReceivers = new HashMap<>();
    // check outgoing ports, b/c they must receive data from within the component
    for (PortSymbol receiver : currentComponent.getOutgoingPorts()) {
      if (!currentComponent.hasConnector(receiver.getName())) {
        unusedReceivers.put(receiver.getName(),
            PorWithGenericBindings.create(receiver, currentComponent.getFormalTypeParameters(),
                new ArrayList<>()));
      }
    }
    
    // check subcomponents incoming ports, b/c they must receive data to do their calculations
    for (ComponentInstanceSymbol ref : currentComponent.getSubComponents()) {
      String name = ref.getName();
      ComponentSymbolReference refType = ref.getComponentType();
      for (PortSymbol port : refType.getIncomingPorts()) {
        String portNameInConnector = name + "." + port.getName();
        if (!currentComponent.hasConnector(portNameInConnector)) {
          // store the the type parameters' bindings of the referenced component for this reference
          unusedReceivers.put(portNameInConnector,
              PorWithGenericBindings.create(port,
                  refType.getReferencedSymbol().getFormalTypeParameters(),
                  refType.getActualTypeArguments().stream()
                      .map(a -> (JTypeReference<?>) a.getType())
                      .collect(Collectors.toList())));
        }
      }
    }
    return unusedReceivers;
  }
  
  /**
   * Finds all outgoing ports of a given component (and all referenced components) which are not
   * connected.
   *
   * @param currentComponent the component for which to find the unused ports
   * @return unused outgoing ports of a given component represented in a map from port names to
   * types
   */
  private Map<String, PorWithGenericBindings> getUnusedSenders(
      ComponentSymbol currentComponent) {
    // portname, <port, formaltypeparameters for the subcomponent that the port is defined in, type
    // arguments for the subcomponent that the port is defined in>
    Map<String, PorWithGenericBindings> unusedSenders = new HashMap<>();
    // as senders could send to more then one receiver, all senders are added
    for (PortSymbol sender : currentComponent.getIncomingPorts()) {
      if (!currentComponent.hasConnectors(sender.getName())) {
        unusedSenders.put(sender.getName(),
            PorWithGenericBindings.create(sender, currentComponent.getFormalTypeParameters(),
                new ArrayList<>()));
      }
    }
    
    // check subcomponents outputs as they send data to the current component
    for (ComponentInstanceSymbol ref : currentComponent.getSubComponents()) {
      String name = ref.getName();
      ComponentSymbolReference refType = ref.getComponentType();
      for (PortSymbol port : refType.getOutgoingPorts()) {
        String portNameInConnector = name + "." + port.getName();
        if (!currentComponent.hasConnectors(portNameInConnector)) {
          // store the the type parameters' bindings of the referenced component for this reference
          unusedSenders.put(portNameInConnector,
              PorWithGenericBindings.create(port,
                  refType.getReferencedSymbol().getFormalTypeParameters(),
                  refType.getActualTypeArguments().stream()
                      .map(a -> (JTypeReference<?>) a.getType())
                      .collect(Collectors.toList())));
        }
      }
    }
    return unusedSenders;
  }
  
  private static class PorWithGenericBindings {
    PortSymbol port;
    
    List<JTypeSymbol> formalTypeParameters;
    
    List<JTypeReference<? extends JTypeSymbol>> typeArguments;
    
    public PorWithGenericBindings(
        PortSymbol port,
        List<JTypeSymbol> formalTypeParameters,
        List<JTypeReference<? extends JTypeSymbol>> typeArguments) {
      super();
      this.port = port;
      this.formalTypeParameters = formalTypeParameters;
      this.typeArguments = typeArguments;
    }
    
    public static PorWithGenericBindings create(PortSymbol port,
        List<JTypeSymbol> formalTypeParameters,
        List<JTypeReference<? extends JTypeSymbol>> typeArguments) {
      return new PorWithGenericBindings(port, formalTypeParameters, typeArguments);
    }
    
  }
}


/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.codegen;

import static de.se_rwth.commons.StringTransformations.capitalize;
import static de.se_rwth.commons.StringTransformations.uncapitalize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;
import javax.xml.stream.events.Characters;

import org.omg.CosNaming.NameHelper;

import de.montiarc.generator.MontiArcGeneratorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.lang.montiarc.helper.ArcTypePrinter;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTConnector;
import de.monticore.lang.montiarc.montiarc._ast.ASTPort;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentInstanceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbolReference;
import de.monticore.lang.montiarc.montiarc._symboltable.ConnectorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.types.types._ast.ASTQualifiedName;
import de.se_rwth.commons.StringTransformations;

/**
 * TODO: Write me!
 *
 * @author Robert Heim
 */
public class PortHelper {
  
  private static String printType(GlobalExtensionManagement glex, ASTPort port,
      boolean isComponentAtomic, boolean getter) {
    
    StringBuilder sb = new StringBuilder();
    if (isComponentAtomic) {
      String className = port.isIncoming()
          ? (String) glex.getGlobalValue(getter ? "IInPort" : "IInSimPort")
          : (String) glex.getGlobalValue("IOutPort");
      
      sb.append(className);
      sb.append("<");
      sb.append(ArcTypePrinter.printType(port.getType()));
      sb.append(">");
    }
    return sb.toString();
  }
  
  private static String printName(ASTPort port) {
    String type = ArcTypePrinter.printType(port.getType());
    return port.getName().isPresent()
        ? port.getName().get()
        : uncapitalize(type);
  }
  
  public static String getPortName(ComponentSymbol compSym, PortSymbol portSym) {
    String name = StringTransformations.uncapitalize(portSym.getName());
    boolean compIsSingleIn = (compSym.getIncomingPorts().size() == 1)
        && !compSym.getSuperComponent().isPresent();
    boolean singleIn = MontiArcGeneratorConstants.optimizeSingleIn && compIsSingleIn;
    if (compSym.isAtomic()) {
      if (portSym.isIncoming()) {
        if (singleIn) {
          name = name + "= this";
        }
      }
    }
    return name;
  }
  
  public static String getSetSubCompPort(PortSymbol port, ComponentSymbol comp) {
    if (port.isOutgoing()) {
      if (!comp.isAtomic()) {
        return printGetterForSender(port, comp);
      }
    }
    return null;
  }
  
  public static Collection<PortSymbol> getIncomingPortsOfSuperComponentToConnect(
      ComponentSymbol comp) {
    Optional<ComponentSymbolReference> sc = comp.getSuperComponent();
    if (comp.isDecomposed() && sc.isPresent()) {
      ComponentSymbolReference superC = sc.get();
      if (superC.getSubComponents().isEmpty()) {
        return superC.getIncomingPorts();
      }
    }
    return Collections.emptyList();
  }
  
  public static Optional<String> getPortType(GlobalExtensionManagement glex, PortSymbol portSym,
      ComponentSymbol compSym) {
    StringBuilder type = new StringBuilder();
    boolean compIsSingleIn = (compSym.getIncomingPorts().size() == 1)
        && !compSym.getSuperComponent().isPresent();
    boolean singleIn = MontiArcGeneratorConstants.optimizeSingleIn && compIsSingleIn;
    
    boolean result = true;
    if (compSym.isAtomic() && singleIn && portSym.isIncoming()) {
      result = false;
    }
    
    else if (compSym.isAtomic()) {
      String classname;
      if (portSym.isIncoming()) {
        classname = (String) glex.getGlobalValue("IInSimPort");
        type.append(classname);
        type.append("<");
        type.append(GeneratorHelper.printType(portSym.getTypeReference()));
        type.append(">");
      }
      else if (portSym.isOutgoing()) {
        classname = (String) glex.getGlobalValue("IOutPort");
        type.append(classname);
        type.append("<");
        type.append(GeneratorHelper.printType(portSym.getTypeReference()));
        type.append(">");
      }
      else {
        result = false;
      }
    }
    else {
      if (portSym.isIncoming()) {
        String componentName = compSym.getName();
        int receiverAmount = 0;
        for (ConnectorSymbol cs : compSym.getConnectors()) {
          if (cs.getSource().equals(portSym.getName())) {
            receiverAmount++;
          }
        }
        for (ComponentInstanceSymbol sc : compSym.getSubComponents()) {
          if (sc.getComponentType().getIncomingPorts().size() == 0) {
            receiverAmount++;
          }
        }
        if (receiverAmount > 1 || receiverAmount == 0) {
          String classname = (String) glex.getGlobalValue("IForwardPort");
          type.append(classname);
          type.append("<");
          type.append(GeneratorHelper.printType(portSym.getTypeReference()));
          type.append(">");
        }
        else {
          result = false;
        }
      }
      else {
        result = false;
      }
      
    }
    if (result) {
      return Optional.of(type.toString());
    }
    return Optional.empty();
  }
  
  public static boolean needsEncapsulation(ConnectorSymbol connector) {
    boolean needEncapsulation = false;
    Optional<ComponentSymbol> cmp = connector.getComponent();
    if (cmp.isPresent()) {
      ComponentSymbol comp = cmp.get();
      if (comp.isDecomposed() && connector.getSourcePort().getName().equals("")) {
        int amount = 0;
        String senderPort = connector.getSourcePort().getFullName();
        for (ConnectorSymbol cs : comp.getConnectors()) {
          if (connector.getSource().equals(senderPort)) {
            amount += 1;
          }
          if (amount > 1) {
            break;
          }
        }
        if (amount > 1) {
          needEncapsulation = true;
        }
      }
      
    }
    return needEncapsulation;
  }
  
  public static void transform(GlobalExtensionManagement glex, ASTComponent comp) {
    ComponentSymbol compSym = (ComponentSymbol) comp.getSymbol().get();
    
    for (ASTPort port : comp.getPorts()) {
      String type = printType(glex, port, compSym.isAtomic(), false);
      String name = printName(port);
      
      portGetter(glex, compSym, port);
      portSetter(glex, compSym, port);
      
      glex.replaceTemplate("templates.mc.Empty", port,
          new TemplateHookPoint("templates.mc.umlp.arc.component.port.PortAttribute", type, name));
    }
  }
  
  public static String getPortReturnType(GlobalExtensionManagement glex, PortSymbol portSym) {
    StringBuilder type = new StringBuilder();
    String classname;
    if (portSym.isIncoming()) {
      classname = (String) glex.getGlobalValue("IInPort");
    }
    else {
      classname = (String) glex.getGlobalValue("IOutPort");
    }
    type.append(classname);
    type.append("<");
    type.append(GeneratorHelper.printType(portSym.getTypeReference()));
    type.append(">");
    
    return type.toString();
  }
  
  public static boolean isSingleIn(ComponentSymbol compSym) {
    boolean compIsSingleIn = (compSym.getIncomingPorts().size() == 1)
        && !compSym.getSuperComponent().isPresent();
    boolean singleIn = MontiArcGeneratorConstants.optimizeSingleIn && compIsSingleIn;
    return singleIn;
  }
  
  public static String getPortReturnValue(ComponentSymbol compSym, PortSymbol portSym) {
    boolean compIsSingleIn = (compSym.getIncomingPorts().size() == 1)
        && !compSym.getSuperComponent().isPresent();
    boolean singleIn = MontiArcGeneratorConstants.optimizeSingleIn && compIsSingleIn;
    
    String returnValue = "null";
    if (compSym.isAtomic() && singleIn && portSym.isIncoming()) {
      returnValue = "this";
    }
    else if (compSym.isAtomic()) {
      returnValue = StringTransformations.uncapitalize(portSym.getName());
    }
    else {
      if (portSym.isIncoming()) {
        if (compSym.getConnectors().size() == 1) {
          List<String> receiver = getReceivers(portSym, compSym);
          if (receiver.size() == 1) {
            returnValue = receiver.get(0);
          }
          else {
            returnValue = StringTransformations.uncapitalize(portSym.getName());
          }
        }
      }
      else {
        returnValue = printGetterForSender(compSym, portSym);
      }
    }
    
    return returnValue;
  }
  
  /**
   * Prints getter gen1.getOutput() for Port gen1.output
   * 
   * @param compSym
   * @param portSym
   * @return
   */
  private static String printGetterForSender(ComponentSymbol compSym, PortSymbol portSym) {
    String sender = null;
    for (ConnectorSymbol cs : compSym.getConnectors()) {
      if (cs.getTarget().equals(portSym.getName())) {
        String instanceName = cs.getSource().substring(0, cs.getSource().lastIndexOf("."));
        String portName = cs.getSource().substring(cs.getSource().lastIndexOf(".")+1);
        sender = instanceName + ".get" + StringTransformations.capitalize(portName) + "()";
        break;
      }
    }
    return sender;
  }
  
  public static List<String> getReceivers(PortSymbol port, ComponentSymbol component) {
    List<String> receivers = new ArrayList<>();
    for (ConnectorSymbol cs : component.getConnectors()) {
      if (cs.getSource().equals(port.getName())) {
        receivers.add(cs.getTarget());
      }
    }
    
    return receivers;
  }
  
  private static String getSender(PortSymbol port, ComponentSymbol compSym) {
    String sender = null;
    for (ConnectorSymbol cs : compSym.getConnectors()) {
      if (cs.getTarget().equals(port.getName())) {
        sender = cs.getSource();
        break;
      }
    }
    return sender;
  }
  
  private static String printGetterForSender(PortSymbol portSym, ComponentSymbol compSym){
    String getter = null;
    for (ConnectorSymbol cs : compSym.getConnectors()) {
      if (cs.getTarget().equals(portSym.getName())) {
        String instanceName = cs.getSource().substring(0, cs.getSource().lastIndexOf("."));
        String portName = cs.getSource().substring(cs.getSource().lastIndexOf(".")+1);
        getter = instanceName + ".set" + StringTransformations.capitalize(portName);
        break;
      }
    }
    return getter;
  }
  
  public static String printTargetName(ConnectorSymbol cs) {
    String targetName = cs.getTarget();
    if (targetName.contains(".")) {
      targetName = targetName.substring(0, targetName.indexOf('.'));
    }
    return targetName;
  }
  
  public static String printSourceName(ConnectorSymbol cs) {
    String sourceName = cs.getSource();
    if (sourceName.contains(".")) {
      sourceName = sourceName.substring(0, sourceName.indexOf('.'));
    }
    return sourceName;
  }
  
  private static void portGetter(GlobalExtensionManagement glex, ComponentSymbol comp,
      ASTPort port) {
    
    String portName = printName(port);
    
    StringBuilder type = new StringBuilder();
    
    type.append(printType(glex, port, comp.isAtomic(), true));
    
    String returnValue = "null";
    
    if (comp.isAtomic()) {
      returnValue = portName;
    }
    // architectural component
    else {
      if (port.isIncoming()) {
        List<String> receiver = resolveReceiver(portName, comp);
        if (receiver.size() == 1) {
          // pass incoming port from sub component
          returnValue = receiver.get(0);
        }
        else {
          // return the architectural component port
          returnValue = portName;
        }
      }
      // outgoing port from an architectural component.
      else {
        returnValue = resolveGetSender(portName, comp).orElse("null");
      }
    }
    String nameCapitalized = capitalize(portName);
    String returnType = type.toString();
    
    glex.replaceTemplate("templates.mc.umlp.arc.component.port.PortAttributesGetterDummy", port,
        new TemplateHookPoint("templates.mc.umlp.arc.component.port.PortAttributesGetter",
            port, returnType, returnValue, comp.isAtomic(), nameCapitalized));
  }
  
  private static void portSetter(GlobalExtensionManagement glex, ComponentSymbol comp,
      ASTPort port) {
    
    String portName = printName(port);
    
    if (!port.isOutgoing()) {
      glex.replaceTemplate("templates.mc.umlp.arc.component.port.PortAttributesSetterDummy", port,
          new StringHookPoint("// no setter for incoming port " + portName + " \n"));
      
    }
    else {
      StringBuilder type = new StringBuilder();
      type.append(ArcTypePrinter.printType(port.getType()));
      
      Optional<String> setSubCompPort = comp.isAtomic()
          ? resolveSetSender(portName, comp)
          : Optional.empty();
      
      String IPort = MontiArcGeneratorConstants.PORT_INTERFACE_NAME;
      String IOutSimPort = MontiArcGeneratorConstants.OUT_SIM_PORT_INTERFACE_NAME;
      
      glex.replaceTemplate("templates.mc.umlp.arc.component.port.PortAttributesSetterDummy", port,
          new TemplateHookPoint("templates.mc.umlp.arc.component.port.PortAttributesSetter",
              portName, type.toString(), comp.isAtomic(), setSubCompPort, IPort, IOutSimPort));
    }
  }
  
  /**
   * @param portName
   * @param comp
   * @return the sender from an outgoing port
   */
  private static Optional<String> resolveGetSender(String portName, ComponentSymbol comp) {
    // Search the port in the receivers from all connectors
    for (ConnectorSymbol conSym : comp.getConnectors()) {
      ASTConnector con = (ASTConnector) conSym.getAstNode().get();
      for (ASTQualifiedName target : con.getTargets()) {
        if (target.getParts().size() == 1) {
          String targetName = target.getParts().get(0);
          String sourceName = con.getSource().getParts().get(0);
          if (uncapitalize(targetName).equals(uncapitalize(portName))) {
            String sender = "get" + capitalize(sourceName)
                + "().get" + capitalize(sourceName) + "()";
            return Optional.of(sender);
          }
        }
      }
    }
    return Optional.empty();
  }
  
  /**
   * @param port port that is the receiver
   * @param comp component that contains the port
   * @return the sender from an outgoing port
   */
  private static Optional<String> resolveSetSender(String portName, ComponentSymbol comp) {
    // Search the port in the receivers from all connectors
    for (ConnectorSymbol conSym : comp.getConnectors()) {
      ASTConnector con = (ASTConnector) conSym.getAstNode().get();
      for (ASTQualifiedName target : con.getTargets()) {
        if (target.getParts().size() == 1) {
          String targetName = target.getParts().get(0);
          String sourceName = con.getSource().getParts().get(0);
          if (uncapitalize(targetName).equals(uncapitalize(portName))) {
            String sender = "get" + capitalize(sourceName)
                + "().set" + capitalize(sourceName) + "(port)";
            return Optional.of(sender);
          }
        }
      }
    }
    return Optional.empty();
  }
  
  private static List<String> resolveReceiver(String portName, ComponentSymbol comp) {
    List<String> result = new ArrayList<String>();
    
    // TODO sub components without incoming ports
    for (ComponentInstanceSymbol sc : comp.getSubComponents()) {
      if (sc.getComponentType().getIncomingPorts().size() == 0) {
        // TODO awhat did these annotations do?!??!?!?!?!?
        // String key = comp.getName() + "." + sc.getName();
        
        // if (comp.get_Annotation(key) == null ||
        // comp.get_Annotation(key).equals(port.printName())) {
        // comp.add_Annotation(key, port.printName());
        // String rec = NameHelper.firstToUpper("get" + sc.getName()) +
        // "()._get "
        // + NameHelper.firstToUpper(ArcdConstants.CODEGEN_TIME_IN_PORTNAME) +
        // "()";
        // result.add(rec);
        // }
      }
    }
    
    // Search the port in the receivers from all connectors
    for (ConnectorSymbol conSym : comp.getConnectors()) {
      ASTConnector con = (ASTConnector) conSym.getAstNode().get();
      ASTQualifiedName source = con.getSource();
      if (source.getParts().size() == 1) {
        String sourceName = StringTransformations.uncapitalize(source.getParts().get(0));
        if (sourceName.equals(StringTransformations.uncapitalize(portName))) {
          for (ASTQualifiedName conTarget : con.getTargets()) {
            String returnValue = "get"
                + capitalize(conTarget.getParts().get(0))
                + "().get" +
                capitalize(conTarget.getParts().get(1)) + "()";
            result.add(returnValue);
          }
        }
      }
      
    }
    
    return result;
  }
  
}

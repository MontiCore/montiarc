package de.montiarcautomaton.generator.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.monticore.types.types._ast.ASTTypesNode;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTInterface;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTValuation;
import montiarc._ast.ASTValueInitialization;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ConnectorSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 *
 * @author Gerrit Leonhardt
 */
public class ComponentHelper {
  public static String DEPLOY_STEREOTYPE = "deploy";
  
  private final ComponentSymbol component;
  
  protected final ASTComponent componentNode;
  
  private final IndentPrinter pr = new IndentPrinter();
  
  private final TypesPrettyPrinterConcreteVisitor typesPrinter = new de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor(
      pr);
  
  private final JavaDSLPrettyPrinter javaPrinter = new JavaDSLPrettyPrinter(pr);
  
  public ComponentHelper(ComponentSymbol component) {
    this.component = component;
    if ((component.getAstNode().isPresent())
        && (component.getAstNode().get() instanceof ASTComponent)) {
      componentNode = (ASTComponent) component.getAstNode().get();
    }
    else {
      componentNode = null;
    }
  }
  
  public String printInitialValue(ASTParameter parameter) {
    String value;
    if (parameter.isPresentDefaultValue()) {
      ASTValuation defaultValue = parameter.getDefaultValue();
      value = javaPrinter.prettyprint(defaultValue.getExpression());
    }
    else {
      value = parameter.getName();
    }
    return value;
  }
  
  // TODO: Wer nutzt die und warum? Kann die raus?
  @Deprecated
  public String getPortTypeName(PortSymbol port) {
    // ASTComponent comp = null;
    //
    // final Optional<? extends ScopeSpanningSymbol> spanningSymbol =
    // port.getEnclosingScope().getSpanningSymbol();
    // if(spanningSymbol.isPresent() && spanningSymbol.get() instanceof
    // ComponentSymbol) {
    // final ComponentSymbol symbol = (ComponentSymbol) spanningSymbol.get();
    // final Optional<ASTNode> astNode = symbol.getAstNode();
    // if(astNode.isPresent()) {
    // return getPortTypeName((ASTComponent) astNode.get(), port);
    // }
    // }
    
    ASTPort astPort = (ASTPort) port.getAstNode().get();
    ASTTypesNode astTypeNode = (ASTTypesNode) astPort.getType();
    String portTypeName = autobox(typesPrinter.prettyprint(astTypeNode));
    return portTypeName;
    // return getPortTypeName(componentNode, port);
  }
  
  private static HashMap<String, String> PRIMITIVE_TYPES = new HashMap<String, String>() {
    {
      put("int", "Integer");
      put("double", "Double");
      put("boolean", "Boolean");
      put("byte", "Byte");
      put("char", "Character");
      put("long", "Long");
      put("float", "Float");
      put("short", "Short");
    }
  };
  
  /**
   * TODO: Write me!
   * 
   * @param prettyprint
   * @return
   */
  public static String autobox(String datatype) {
    
    String[] tokens = datatype.split("\\b");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tokens.length; i++) {
      if (PRIMITIVE_TYPES.containsKey(tokens[i])) {
        tokens[i] = autoboxType(tokens[i]);
      }
      sb.append(tokens[i]);
    }
    return sb.toString();
    
  }
  
  private static String autoboxType(String datatype) {
    String autoBoxedTypeName = datatype;
    if (PRIMITIVE_TYPES.containsKey(datatype)) {
      autoBoxedTypeName = PRIMITIVE_TYPES.get(datatype);
    }
    return autoBoxedTypeName;
  }
  
  // TODO: Wer nutzt die und warum? Kann die raus?
  @Deprecated
  public String getPortTypeName(ASTComponent comp, PortSymbol port) {
    //
    // ASTTypesNode astTypeNode = (ASTTypesNode) comp.getPorts().getType();
    // String portTypeName = visitor.prettyprint();
    // return portTypeName;
    return printFqnTypeName(comp, port.getTypeReference());
  }
  
  // TODO @MP in Templates übernehmen
  public String printPortTypeName(PortSymbol var) {
    String name = var.getName();
    Optional<ASTType> optType = findPortTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findPortTypeByName(String name) {
    for (ASTElement e : componentNode.getBody().getElementList()) {
      if (e instanceof ASTInterface) {
        ASTInterface itf = (ASTInterface) e;
        for (ASTPort port : itf.getPortsList()) {
          if (port.getNameList().contains(name)) {
            return Optional.of(port.getType());
          }
        }
      }
    }
    return Optional.empty();
  }
  
  // TODO @MP in Templates übernehmen
  public String printVariableTypeName(VariableSymbol var) {
    String name = var.getName();
    Optional<ASTType> optType = findVariableTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findVariableTypeByName(String name) {
    for (ASTElement e : componentNode.getBody().getElementList()) {
      if (e instanceof ASTVariableDeclaration) {
        ASTVariableDeclaration variableDeclaration = (ASTVariableDeclaration) e;
        if (variableDeclaration.getNameList().contains(name)) {
          return Optional.of(variableDeclaration.getType());
        }
      }
    }
    return Optional.empty();
  }
  
  public String printParamTypeName(JFieldSymbol param) {
    String name = param.getName();
    Optional<ASTType> optType = findParamTypeByName(name);
    return printTypeName(optType);
  }
  
  private Optional<ASTType> findParamTypeByName(String name) {
    for (ASTParameter p : componentNode.getHead().getParameterList()) {
      if (name.equals(p.getName())) {
        return Optional.of(p.getType());
      }
    }
    return Optional.empty();
  }
  
  private String printTypeName(Optional<ASTType> optType) {
    if (optType.isPresent()) {
      return autobox(typesPrinter.prettyprint(optType.get()).replaceAll(" ", ""));
    }
    else {
      Log.error("XX");
      return "TYPE-NOT-FOUND";
    }
  }
  
  public String getVariableTypeName(ASTComponent comp, VariableSymbol var) {
    return printFqnTypeName(comp, var.getTypeReference());
  }
  
  public String printInit(ASTValueInitialization init) {
    String ret = "";
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    String name = Names.getQualifiedName(init.getQualifiedName().getPartList());
    ret += name;
    ret += " = ";
    ret += printer.prettyprint(init.getValuation().getExpression());
    ret += ";";
    
    return ret;
    
  }
  
  public String getParamTypeName(JFieldSymbol param) {
    return getParamTypeName(componentNode, param);
  }
  
  public String getParamTypeName(ASTComponent comp, JFieldSymbol param) {
    return printFqnTypeName(comp, param.getType());
  }
  
  /**
   * Calculates the values of the parameters of a
   * {@link ComponentInstanceSymbol}. This takes default values for parameters
   * into account and adds them as required. Default values are only added from
   * left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instanciated with parameters <code>"Test String", 5</code> this
   * method adds <code>new Object()</code> as the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters
   * should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    // final List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments =
    // param.getConfigArguments();
    List<ASTExpression> configArguments = param.getConfigArguments();
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    
    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }
    
    // Append the default parameter values for as many as there are left
    final List<JFieldSymbol> configParameters = param.getComponentType().getConfigParameters();
    
    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();
    
    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponent astNode = (ASTComponent) param.getComponentType().getReferencedSymbol()
          .getAstNode().get();
      final List<ASTParameter> parameters = astNode.getHead().getParameterList();
      
      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = printer
            .prettyprint(astParameter.getDefaultValue().getExpression());
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }
    
    return outputParameters;
  }
  
  public String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    
    if (instance.getComponentType().getAstNode().isPresent()) {
      return typesPrinter
          .prettyprint((ASTTypesNode) instance.getComponentType().getAstNode().get());
    }
    else {
      return instance.getComponentType().getName();
    }
    
  }
  
  public boolean isIncomingPort(ComponentSymbol cmp, ConnectorSymbol conn, boolean isSource,
      String portName) {
    String subCompName = getConnectorComponentName(conn, isSource);
    String portNameUnqualified = getConnectorPortName(conn, isSource);
    Optional<PortSymbol> port = Optional.empty();
    // port is of subcomponent
    if (portName.contains(".")) {
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
          .<ComponentInstanceSymbol> resolve(subCompName, ComponentInstanceSymbol.KIND);
      Optional<ComponentSymbol> subComp = subCompInstance.get().getComponentType()
          .getReferencedComponent();
      port = subComp.get().getSpannedScope().<PortSymbol> resolve(portNameUnqualified,
          PortSymbol.KIND);
    }
    else {
      port = cmp.getSpannedScope().<PortSymbol> resolve(portName, PortSymbol.KIND);
    }
    
    if (port.isPresent()) {
      return port.get().isIncoming();
    }
    
    return false;
  }
  
  /**
   * Returns the component name of a connection.
   *
   * @param conn the connection
   * @param isSource <tt>true</tt> for siurce component, else <tt>false>tt>
   * @return
   */
  public String getConnectorComponentName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    if (name.contains(".")) {
      return name.split("\\.")[0];
    }
    return "this";
    
  }
  
  /**
   * Returns the port name of a connection.
   *
   * @param conn the connection
   * @param isSource <tt>true</tt> for siurce component, else <tt>false>tt>
   * @return
   */
  public String getConnectorPortName(ConnectorSymbol conn, boolean isSource) {
    final String name;
    if (isSource) {
      name = conn.getSource();
    }
    else {
      name = conn.getTarget();
    }
    
    if (name.contains(".")) {
      return name.split("\\.")[1];
    }
    return name;
  }
  
  /**
   * Returns <tt>true</tt> if the component is deployable.
   *
   * @return
   */
  public boolean isDeploy() {
    if (component.getStereotype().containsKey(DEPLOY_STEREOTYPE)) {
      if (!component.getConfigParameters().isEmpty()) {
        throw new RuntimeException("Config parameters are not allowed for a depolyable component.");
      }
      return true;
    }
    return false;
  }
  
  /**
   * Prints the type of the reference including dimensions.
   *
   * @param ref
   * @return
   */
  protected String printFqnTypeName(ASTComponent comp, JTypeReference<? extends JTypeSymbol> ref) {
    String name = ref.getName();
    if (isGenericTypeName(comp, name)) {
      return name;
    }
    Collection<JTypeSymbol> sym = ref.getEnclosingScope().<JTypeSymbol> resolveMany(ref.getName(),
        JTypeSymbol.KIND);
    if (!sym.isEmpty()) {
      name = sym.iterator().next().getFullName();
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return autobox(name);
  }
  
  /**
   * Checks whether the given typeName for the component comp is a generic
   * parameter.
   *
   * @param comp
   * @param typeName
   * @return
   */
  private boolean isGenericTypeName(ASTComponent comp, String typeName) {
    if (comp == null) {
      return false;
    }
    if (comp.getHead().isPresentGenericTypeParameters()) {
      List<ASTTypeVariableDeclaration> parameterList = comp.getHead().getGenericTypeParameters()
          .getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration type : parameterList) {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }
  
  public boolean hasSuperComp() {
    return component.getSuperComponent().isPresent();
  }
  
  public String getSuperComponentFqn() {
    if (component.getSuperComponent().isPresent()) {
      return component.getSuperComponent().get().getFullName();
    }
    return "ERROR";
  }
  
  public static Optional<ASTJavaPInitializer> getComponentInitialization(ComponentSymbol comp) {
    Optional<ASTJavaPInitializer> ret = Optional.empty();
    Optional<ASTNode> ast = comp.getAstNode();
    if (ast.isPresent()) {
      ASTComponent compAST = (ASTComponent) ast.get();
      for (ASTElement e : compAST.getBody().getElementList()) {
        if (e instanceof ASTJavaPInitializer) {
          ret = Optional.of((ASTJavaPInitializer) e);
          
        }
      }
    }
    return ret;
  }
  
  public boolean isGeneric() {
    return componentNode.getHead().isPresentGenericTypeParameters();
  }
  
  public List<String> getGenericParameters() {
    List<String> output = new ArrayList<>();
    if (componentNode.getHead().isPresentGenericTypeParameters()) {
      List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead()
          .getGenericTypeParameters().getTypeVariableDeclarationList();
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName());
      }
    }
    return output;
  }
  
  public List<PortSymbol> getSuperInPorts() {
    return component.getSuperComponent().isPresent()
        ? component.getSuperComponent().get().getAllIncomingPorts()
        : Collections.emptyList();
  }
  
  public List<PortSymbol> getAllInPorts() {
    return component.getAllIncomingPorts();
  }
  
  public List<PortSymbol> getSuperOutPorts() {
    return component.getSuperComponent().isPresent()
        ? component.getSuperComponent().get().getAllOutgoingPorts()
        : Collections.emptyList();
  }
  
  public List<PortSymbol> getAllOutPorts() {
    return component.getAllOutgoingPorts();
  }
}

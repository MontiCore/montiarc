package de.montiarcautomaton.generator.helper;

import de.monticore.ast.ASTNode;
import de.monticore.java.prettyprint.JavaDSLPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.symboltable.types.references.TypeReference;
import de.monticore.types.types._ast.ASTTypeVariableDeclaration;
import de.se_rwth.commons.Names;
import montiarc._ast.*;
import montiarc._symboltable.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

  public ComponentHelper(ComponentSymbol component) {
    this.component = component;
    if((component.getAstNode().isPresent()) && (component.getAstNode().get() instanceof ASTComponent)) {
      componentNode = (ASTComponent) component.getAstNode().get();
    } else {
      componentNode = null;
    }
  }

  public String getPortTypeName(PortSymbol port) {
//    ASTComponent comp = null;
//
//    final Optional<? extends ScopeSpanningSymbol> spanningSymbol = port.getEnclosingScope().getSpanningSymbol();
//    if(spanningSymbol.isPresent() && spanningSymbol.get() instanceof ComponentSymbol) {
//      final ComponentSymbol symbol = (ComponentSymbol) spanningSymbol.get();
//      final Optional<ASTNode> astNode = symbol.getAstNode();
//      if(astNode.isPresent()) {
//        return getPortTypeName((ASTComponent) astNode.get(), port);
//      }
//    }
    return getPortTypeName(componentNode, port);
  }

  public String getPortTypeName(ASTComponent comp, PortSymbol port) {
    return printFqnTypeName(comp, port.getTypeReference());
  }

  public String getVariableTypeName(VariableSymbol var) {
    return getVariableTypeName(componentNode, var);
  }

  public String getVariableTypeName(ASTComponent comp, VariableSymbol var) {
    return printFqnTypeName(comp, var.getTypeReference());
  }

  public String printInit(ASTValueInitialization init) {
    String ret = "";
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());
    String name = Names.getQualifiedName(init.getQualifiedName().getParts());
    ret += name;
    ret+= " = ";
    ret+= printer.prettyprint(init.getValuation().getExpression());
    ret+= ";";

    return ret;


  }

  public String getParamTypeName(JFieldSymbol param) {
    return getParamTypeName(componentNode, param);
  }

  public String getParamTypeName(ASTComponent comp, JFieldSymbol param) {
    return printFqnTypeName(comp, param.getType());
  }

  /**
   * Calculates the values of the parameters of a {@link ComponentInstanceSymbol}. This takes default values for parameters into account and adds them as required.
   * Default values are only added from left to right in order.
   * <br/>
   * Example:
   *
   * For a component with parameters <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code> that is instanciated with parameters <code>"Test String", 5</code> this method adds <code>new Object()</code> as the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    final List<ValueSymbol<TypeReference<TypeSymbol>>> configArguments = param.getConfigArguments();
    JavaDSLPrettyPrinter printer = new JavaDSLPrettyPrinter(new IndentPrinter());

    List<String> outputParameters = new ArrayList<>();
    for (ValueSymbol<TypeReference<TypeSymbol>> configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument.getValue());
      outputParameters.add(prettyprint);
    }

    // Append the default parameter values for as many as there are left
    final List<JFieldSymbol> configParameters = param.getComponentType().getConfigParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    // Get the AST node of the component and the list of parameters in the AST
    final ASTComponent astNode = (ASTComponent) param.getComponentType().getReferencedSymbol().getAstNode().get();
    final List<ASTParameter> parameters = astNode.getHead().getParameters();

    // Retrieve the parameters from the node and add them to the list
    for(int counter = 0; counter < numberOfMissingParameters; counter++) {
      // Fill up from the last parameter
      final ASTParameter astParameter = parameters.get(parameters.size() - 1 - counter);
      final String prettyprint = printer.prettyprint(astParameter.getDefaultValue().get().getExpression());
      outputParameters.add(outputParameters.size() - counter, prettyprint);
    }

    return outputParameters;
  }

  public String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    // Get the usual name of the class
    final String className = instance.getComponentType().getName();
    StringBuilder genericParameters = new StringBuilder();

    final Optional<ASTNode> astNode = instance.getComponentType().getReferencedSymbol().getAstNode();
    if (astNode.isPresent()) {
      ComponentHelper subCompHelper = new ComponentHelper(instance.getComponentType().getReferencedSymbol());
      if (subCompHelper.isGeneric()) {
        // Append the generic parameters
        genericParameters.append("<");
        final List<ActualTypeArgument> actualTypeArguments = instance.getComponentType().getActualTypeArguments();
        for (ActualTypeArgument typeArgument : actualTypeArguments) {
          genericParameters.append(typeArgument.getType().getName());
          if(actualTypeArguments.indexOf(typeArgument) < actualTypeArguments.size() - 1) {
            genericParameters.append(", ");
          }
        }

        genericParameters.append(">");
      }
    }
    return className + genericParameters;
  }

  public boolean isIncomingPort(ComponentSymbol cmp, ConnectorSymbol conn, boolean isSource, String portName) {
    String subCompName = getConnectorComponentName(conn, isSource);
    String portNameUnqualified = getConnectorPortName(conn, isSource);
    Optional<PortSymbol> port = Optional.empty();
    // port is of subcomponent
    if(portName.contains(".")) {
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope().<ComponentInstanceSymbol> resolve(subCompName, ComponentInstanceSymbol.KIND);
      Optional<ComponentSymbol> subComp = subCompInstance.get().getComponentType().getReferencedComponent();
      port =  subComp.get().getSpannedScope().<PortSymbol> resolve(portNameUnqualified, PortSymbol.KIND);
    }
    else {
      port = cmp.getSpannedScope().<PortSymbol> resolve(portName, PortSymbol.KIND);
    }


    if(port.isPresent()) {
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
    if(name.contains(".")) {
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

    if(name.contains(".")) {
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
    if(isGenericTypeName(comp, name)){
      return name;
    }
    Collection<JTypeSymbol> sym = ref.getEnclosingScope().<JTypeSymbol>resolveMany(ref.getName(), JTypeSymbol.KIND);
    if(!sym.isEmpty()){
      name = sym.iterator().next().getFullName();
    }
    for (int i = 0; i < ref.getDimension(); ++i) {
      name += "[]";
    }
    return name;
  }

  /**
   * Checks whether the given typeName for the component comp is a generic parameter.
   *
   * @param comp
   * @param typeName
   * @return
   */
  private boolean isGenericTypeName(ASTComponent comp, String typeName) {
    if(comp == null) {
      return false;
    }
    if(comp.getHead().getGenericTypeParameters().isPresent()) {
      List<ASTTypeVariableDeclaration> parameterList = comp.getHead().getGenericTypeParameters().get().getTypeVariableDeclarations();
      for(ASTTypeVariableDeclaration type : parameterList)
      {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }

  public static Optional<ASTJavaPInitializer> getComponentInitialization(ComponentSymbol comp) {
    Optional<ASTJavaPInitializer> ret = Optional.empty();
    Optional<ASTNode> ast = comp.getAstNode();
    if(ast.isPresent()) {
      ASTComponent compAST = (ASTComponent) ast.get();
      for(ASTElement e : compAST.getBody().getElements()) {
        if(e instanceof ASTJavaPInitializer) {
          ret = Optional.of((ASTJavaPInitializer) e);

        }
      }
    }
    return ret;
  }

  public boolean isGeneric(){
    return componentNode.getHead().getGenericTypeParameters().isPresent();
  }

  public List<String> getGenericParameters(){
    List<String> output = new ArrayList<>();
    if(componentNode.getHead().getGenericTypeParameters().isPresent()) {
      List<ASTTypeVariableDeclaration> parameterList = componentNode.getHead().getGenericTypeParameters().get().getTypeVariableDeclarations();
      for (ASTTypeVariableDeclaration variableDeclaration : parameterList) {
        output.add(variableDeclaration.getName());
      }
    }
    return output;
  }
}

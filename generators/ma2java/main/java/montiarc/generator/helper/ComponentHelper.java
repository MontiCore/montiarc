/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._ast.ASTPortAccessTOP;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import arcbasis._symboltable.PortSymbolTOP;
import arccompute._ast.ASTArcCompute;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc.generator.MA2JavaFullPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Helper class used in the template to generate target code of atomic or composed components.
 */
public class ComponentHelper {

  public static String DEPLOY_STEREOTYPE = "deploy";
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
  protected MA2JavaFullPrettyPrinter prettyPrinter;

  public ComponentHelper() {
    this.prettyPrinter = new MA2JavaFullPrettyPrinter();
  }

  protected MA2JavaFullPrettyPrinter getPrettyPrinter() {
    return this.prettyPrinter;
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This takes in to account whether the port is
   * inherited and possible required renamings due to generic type parameters and their actual arguments.
   *
   * @param portSymbol Symbol of the port for which the type name should be determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(PortSymbol portSymbol) {
    SymTypeExpression portType = portSymbol.getType();
    return portType.isPrimitive() ?
      ((SymTypePrimitive) portType).getBoxedPrimitiveName() :
      portType.isTypeVariable() ? portType.print() :
        portType.printFullName();
  }

  /**
   * Converts the given type reference {@param toAnalyze} into a String
   * representation where all occurrences of type arguments from the extending
   * or embedding component are replaced by their actual values. The actual
   * values are given as the second argument {@param typeParamMap}. <br/>
   * Example: For a given type argument {@code Map<T,K>} as an
   * {@link TypeReference} object and the {@link Map}
   * {@code ["T" -> "String", "K -> "Integer"]} the resulting String is
   * {@code Map<String, Integer>}.
   *
   * @param toAnalyze The type argument where the type parameters should be
   * replaced.
   * @param typeParamMap The map used to replace the type parameters
   * @return The resulting String representation with replaced type parameters
   */
  /*
  public static String insertTypeParamValueIntoTypeArg(
    SymTypeExpression toAnalyze,
    Map<String, String> typeParamMap) {

    StringBuilder result = new StringBuilder();
    result.append(toAnalyze.print());
    if (toAnalyze.getActualTypeArguments().isEmpty()) {
      // There are no type arguments. Check if the type itself is a type
      // parameter
      if (typeParamMap.containsKey(toAnalyze.getName())) {
        return typeParamMap.get(toAnalyze.getName());
      } else {
        return toAnalyze.getName();
      }
    } else {
      // There are type arguments and therefore they are processed recursively
      // and reprinted
      result.append("<");
      result.append(
        toAnalyze.getActualTypeArguments().stream()
          .map(typeArg -> insertTypeParamValueIntoTypeArg(typeArg.getType(), typeParamMap))
          .collect(Collectors.joining(", ")));
      result.append(">");
    }
    for (int i = 0; i < toAnalyze.getDimension(); i++) {
      result.append("[]");
    }
    return result.toString();
  }
  */

  //TODO: Fix surrogates printing wrong qualified type (omit scope name)
  public static String print(SymTypeExpression expr) {
    if (expr.getTypeInfo() instanceof TypeSymbolSurrogate) {
      return ((TypeSymbolSurrogate) expr.getTypeInfo()).lazyLoadDelegate().getFullName();
    } else {
      return expr.print();
    }
  }

  /**
   * Prints the java expression of the given AST expression node.
   */
  public String printExpression(ASTExpression expr) {
    String res = this.getPrettyPrinter().prettyprint(expr);
    return res;
  }

  /**
   * @return the printed java expression of the given {@link ASTMCBlockStatement} node.
   */
  public String printStatement(ASTMCBlockStatement statement) {
    return this.getPrettyPrinter().prettyprint(statement);
  }

  /**
   * Boxes datatype if applicable.
   *
   * @param datatype String representation of the datatype to box.
   * @return The boxed datatype.
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

  public static List<VariableSymbol> getComponentVariables(ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    List<VariableSymbol> vss = new ArrayList<>(comp.getFields());
    vss.removeAll(comp.getParameters());
    return vss;
  }

  public static boolean hasInitializerExpression(VariableSymbol sym) {
    Preconditions.checkNotNull(sym);
    return sym.isPresentAstNode() && sym.getAstNode() instanceof ASTArcField;
  }

  public static ASTExpression getInitializerExpression(VariableSymbol sym) {
    Preconditions.checkNotNull(sym);
    Preconditions.checkArgument(sym.isPresentAstNode());
    Preconditions.checkArgument(sym.getAstNode() instanceof ASTArcField);
    return ((ASTArcField) sym.getAstNode()).getInitial();
  }

  /**
   * Calculates the values of the parameters of a {@link ComponentInstanceSymbol}. This takes default values for
   * parameters into account and adds them as required. Default values are only added from left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instantiated with parameters <code>"Test String", 5</code> this method adds <code>new Object()</code> as
   * the last parameter.
   *
   * @param param The {@link ComponentInstanceSymbol} for which the parameters should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(ComponentInstanceSymbol param) {
    List<ASTExpression> configArguments = param.getArguments();

    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = this.getPrettyPrinter().prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<VariableSymbol> configParameters = param.getType().getTypeInfo().getParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponentType astNode = param.getType().getTypeInfo().getAstNode();
      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTArcParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = this.getPrettyPrinter()
          .prettyprint((ASTMontiArcNode) astParameter.getDefault());
        outputParameters.add(outputParameters.size() - counter, prettyprint);
      }
    }

    return outputParameters;
  }

  /**
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    ComponentTypeSymbol componentTypeReference = instance.getType().getTypeInfo();
    if (componentTypeReference instanceof ComponentTypeSymbolSurrogate) {
      componentTypeReference = ((ComponentTypeSymbolSurrogate) componentTypeReference).lazyLoadDelegate();
    }
    String packageName = ComponentHelper.printPackageWithoutKeyWordAndSemicolon(componentTypeReference);
    if (packageName != null && !packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentTypeReference.getName();
    /*
    if (componentTypeReference.hasActualTypeArguments()) {
      result += printTypeArguments(componentTypeReference.getActualTypeArguments());
    }
    */
    return result;
  }

  /**
   * Helper function used to determine package names.
   */
  public static String printPackageWithoutKeyWordAndSemicolon(final ComponentTypeSymbol comp) {
    if (comp.isInnerComponent()) {
      //TODO add check for outermost component being TOP-Class or remove this function?
      return printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent().get()) + "." + comp.getOuterComponent().get().getName();
    } else {
      return comp.getPackageName();
    }
  }

  public static List<PortSymbol> getLocalPortSymbolsSorted(@NotNull ComponentTypeSymbol comp) {
    return comp.getPorts().stream()
      .sorted(Comparator.comparing(PortSymbolTOP::getName))
      .collect(Collectors.toList());
  }

  public static List<ImportStatement> getImports(ComponentTypeSymbol symbol) {
    while (symbol.getOuterComponent().isPresent()) {
      symbol = symbol.getOuterComponent().get();
    }
    ASTComponentType ast = symbol.getAstNode();
    return ((MontiArcArtifactScope) ast.getEnclosingScope()).getImportsList();
  }

  public static List<ComponentTypeSymbol> getAllInnerComponents(ComponentTypeSymbol component) {
    List<ComponentTypeSymbol> subcomponents = new ArrayList<>(component.getInnerComponents());
    for (ComponentTypeSymbol innerComp : component.getInnerComponents()) {
      subcomponents.addAll(getAllInnerComponents(innerComp));
    }
    return subcomponents;
  }

  public Optional<ASTArcStatechart> getAutomatonBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().getArcElementList().stream()
      .filter(el -> el instanceof ASTArcStatechart)
      .map(el -> (ASTArcStatechart) el)
      .findFirst();
  }

  public Optional<ASTArcCompute> getComputeBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().getArcElementList().stream()
      .filter(el -> el instanceof ASTArcCompute)
      .map(el -> (ASTArcCompute) el)
      .findFirst();
  }

  /**
   * Derive a valid identifier from an ASTPortAccess based on the port's name.
   * Moved to own function because names must be derived the same way across multiple methods.
   *
   * @param portAccess The PortAccess from which the name should be derived
   * @return A valid variable identifier
   */
  private static String varNameFromPortAccess(@NotNull ASTPortAccess portAccess) {
    return portAccess.getQName().replace('.', '_');
  }

  /**
   * Get a map of variable names mapped to the types of their respective ports.
   * This is required by in our process of setting up connector representations:
   * We have to create a new Port instance for each hidden channel.
   * Connectors where at least one target is not a subcomponent are not considered hidden channels.
   *
   * @param comp the component for whose hidden channels the map is created
   * @return a map of variable names and port types
   */
  public static Map<String, String> getVarsForHiddenChannelsMappedToFullPortType(@NotNull ComponentTypeSymbol comp) {
    Map<String, String> res = new HashMap<>();
    comp.getAstNode().getConnectors().stream()
      .filter(
        conn -> conn.getSource().isPresentComponent()
          // this is where we decide that connectors with at least one port forward are not hidden channels
          && conn.getTargetList().stream().allMatch(ASTPortAccessTOP::isPresentComponent))
      .forEach(
        conn -> res.put(
          varNameFromPortAccess(conn.getSource()),
          getFullPortType(conn.getSource().getPortSymbol())));

    return res;
  }

  public static String getFullPortType(@NotNull PortSymbol port) {
    String portTiming = port.isDelayed() ? "Delayed" : "Undelayed";
    return "montiarc.rte.timesync." + portTiming + "Port<" + getRealPortTypeString(port) + ">";
  }

  /**
   * Transforms a nested map to a Map of Lists.
   * Keys of the outer map are not changed.
   * Values of the inner map are transferred into a list sorted by their key.
   * Keys of the inner map are discarded thereafter.
   *
   * @param map The nested map that should be transformed
   * @return The transformed map of lists
   */
  public static Map<String, List<String>> transformMapMapToSortedListMap(@NotNull Map<String, Map<String, String>> map) {
    Map<String, List<String>> res = new HashMap<>();
    for (String key : map.keySet()) {
      res.put(key, map.get(key).keySet().stream().sorted().map(innerKey -> map.get(key).get(innerKey)).collect(Collectors.toList()));
    }
    return res;
  }

  /**
   * Creates a mapping from subcomponent names to a map of
   * ports of that component to the variable they need to be set to.
   * <p>
   * Expects variable names for ports of hidden channels
   * as produced by getVarsForHiddenChannelsMappedToFullPortType.
   * Expects variable names for local ports to be exactly their name.
   *
   * @param comp The component for whose subcomponents mappings should be created
   * @return the mapping specified above
   */
  public static Map<String, Map<String, String>> mapSubCompNameToPortVariableMap(@NotNull ComponentTypeSymbol comp) {
    Map<String, Map<String, String>> res = new HashMap<>();

    // initialize result map
    for (ComponentInstanceSymbol subComponent : comp.getSubComponents()) {
      Map<String, String> innerMap = new HashMap<>();
      for (PortSymbol portOfSubcomponent : subComponent.getType().getTypeInfo().getPorts()) {
        String portKind = "";
        if (portOfSubcomponent.isIncoming()) {
          portKind = "Unconnected";
        } else {
          portKind = (portOfSubcomponent.isDelayed() ? "Delayed" : "Undelayed");
        }
        innerMap.put(portOfSubcomponent.getName(), "new " + portKind + "Port<>()");
        // notice: if a port is not connected we do not want it to be null, but we also don't want it to return values.
        // One might suggest simply using Port.EMPTY, but as long as it is an unmodified single instance, it's unusable.
        // Sharing a normal Port instance for all unconnected ports leads to problems because data can still be transferred across it.
      }
      res.put(subComponent.getName(), innerMap);
    }

    // iterate over all connectors in the component to fill the map
    for (ASTConnector conn : comp.getAstNode().getConnectors()) {
      String varName;

      // determine which variable represents the current connector (based on the "type" of connector)
      if (!conn.getSource().isPresentComponent()) { // incoming port forward
        varName = "this." + conn.getSourceName();
      } else if (conn.getTargetList().stream().anyMatch(pa -> !pa.isPresentComponent())) { // outgoing port forward (at least partial)
        varName = "this." + conn.getTargetList().stream().filter(pa -> !pa.isPresentComponent()).findAny().get().getPort();
      } else { // hidden channel
        varName = varNameFromPortAccess(conn.getSource());
      }

      // Add variable name to map in all relevant places
      if (conn.getSource().isPresentComponent()) {
        res.get(conn.getSource().getComponent()).put(conn.getSource().getPort(), varName);
      }
      for (ASTPortAccess pa : conn.getTargetList()) {
        if (pa.isPresentComponent()) {
          res.get(pa.getComponent()).put(pa.getPort(), varName);
        }
      }
    }

    return res;
  }

  public String boxPrimitive(SymTypeExpression symtype) {
    Preconditions.checkArgument(symtype instanceof SymTypePrimitive);
    return ((SymTypePrimitive) symtype).getBoxedPrimitiveName();
  }

  public List<Object> asList(Object... objects) {
    return Arrays.asList(objects);
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcautomaton._visitor.NamesInExpressionsVisitor;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import arccompute._ast.ASTArcCompute;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.IMontiArcScope;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._visitor.MontiArcFullPrettyPrinter;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * Helper class used in the template to generate target code of atomic or composed components.
 */
public class ComponentHelper {

  public static String DEPLOY_STEREOTYPE = "deploy";

  protected MontiArcFullPrettyPrinter prettyPrinter;

  protected MontiArcFullPrettyPrinter getPrettyPrinter() {
    return this.prettyPrinter;
  }

  private final ComponentTypeSymbol component;

  protected final ASTComponentType componentNode;

  public ComponentHelper(ComponentTypeSymbol component) {
    this.component = component;
    this.prettyPrinter = new MontiArcFullPrettyPrinter();
    if ((component.isPresentAstNode())
      && (component.getAstNode() instanceof ASTComponentType)) {
      componentNode = (ASTComponentType) component.getAstNode();
    } else {
      componentNode = null;
    }
  }

  /**
   * Prints the type of the given port respecting inherited ports and the actual type values
   *
   * @param port Symbol of the port of which to determine the type
   * @return The string representation of the type
   */
  public String getRealPortTypeString(PortSymbol port) {
    return getRealPortTypeString(this.component, port);
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This takes in to account whether the port is
   * inherited and possible required renamings due to generic type parameters and their actual arguments.
   *
   * @param componentSymbol Symbol of the component which contains the port
   * @param portSymbol      Symbol of the port for which the type name should be determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentTypeSymbol componentSymbol,
                                             PortSymbol portSymbol) {
    SymTypeExpression portType = portSymbol.getType();
    return portType.isTypeConstant() ?
      ((SymTypeConstant) portType).getBoxedConstName() :
      portType.printFullName();
  }

  //TODO: Fix surrogates printing wrong qualified type (omit scope name)
  public static String print(SymTypeExpression expr) {
    if (expr.getTypeInfo() instanceof TypeSymbolSurrogate) {
      return ((TypeSymbolSurrogate) expr.getTypeInfo()).lazyLoadDelegate().getFullName();
    } else {
      return expr.print();
    }
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

  /**
   * Prints the java expression of the given AST expression node.
   *
   * @param expr
   * @return
   */
  public String printExpression(ASTExpression expr) {
    return this.getPrettyPrinter().prettyprint(expr);
  }

  /**
   * @return the printed java expression of the given {@link ASTMCBlockStatement} node.
   */
  public String printStatement(ASTMCBlockStatement statement) {
    return this.getPrettyPrinter().prettyprint(statement);
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

  /**
   * Calculates the values of the parameters of a {@link ComponentInstanceSymbol}. This takes default values for
   * parameters into account and adds them as required. Default values are only added from left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instanciated with parameters <code>"Test String", 5</code> this method adds <code>new Object()</code> as
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

  /**
   * Determine whether the port of the given connector is an incoming or outgoing port.
   *
   * @param cmp      The component defining the connector
   * @param source   The source name of the connector which connects the port to check
   * @param target   The target name of the connector which connects the port to check
   * @param isSource Specifies whether the port to check is the source port of the connector or the target port
   * @return true, if the port is an incoming port. False, otherwise.
   */
  public boolean isIncomingPort(ComponentTypeSymbol cmp, ASTPortAccess source,
                                ASTPortAccess target, boolean isSource) {
    String subCompName = getConnectorComponentName(source, target, isSource);
    String portNameUnqualified = getConnectorPortName(source, target, isSource);
    Optional<PortSymbol> port = Optional.empty();
    String portName = isSource
      ? source.getQName()
      : target.getQName();
    // port is of subcomponent
    if (portName.contains(".")) {
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSubComponent(subCompName);
      ComponentTypeSymbol typeOfSubComp = subCompInstance.get().getType().getTypeInfo();
      port = typeOfSubComp.getPort(portNameUnqualified); // TODO: searchInherited?
    } else {
      port = cmp.getPort(portName); // TODO: searchInherited?
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }

  /**
   * Returns the component name of a connection.
   *
   * @param source   The source name of the connector which connects the port to check
   * @param target   The target name of the connector which connects the port to check
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorComponentName(ASTPortAccess source, ASTPortAccess target,
                                          boolean isSource) {
    if (isSource && source.isPresentComponent()) {
      return source.getComponent();
    } else if (!isSource && target.isPresentComponent()) {
      return target.getComponent();
    } else
      return "this";
  }

  /**
   * Returns the port name of a connection.
   *
   * @param source   The source name of the connector which connects the port to check
   * @param target   The target name of the connector which connects the port to check
   * @param isSource <tt>true</tt> for source component, else <tt>false>tt>
   * @return
   */
  public String getConnectorPortName(ASTPortAccess source, ASTPortAccess target,
                                     boolean isSource) {
    final String name;
    if (isSource) {
      return source.getPort();
    } else {
      return target.getPort();
    }
  }

  /**
   * Checks whether the given typeName for the component comp is a generic parameter.
   *
   * @param comp
   * @param typeName
   * @return
   */
  private boolean isGenericTypeName(ASTComponentType comp, String typeName) {
    if (comp == null) {
      return false;
    }
    if (comp.getHead() instanceof ASTGenericComponentHead) {
      List<ASTArcTypeParameter> parameterList =
        ((ASTGenericComponentHead) comp.getHead()).getArcTypeParameterList();
      for (ASTArcTypeParameter type : parameterList) {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }

  public static List<String> getSuperCompActualTypeArguments(ComponentTypeSymbol component) {
    //TODO implement (to be used in template for component heads instead of parameterless variant)
    return null;
  }

  public static Boolean existsHWCClass(File hwcPath, String cmpLocation) {
    File cmpPath = Paths.get(hwcPath.toString()
      + File.separator + cmpLocation.replaceAll("\\.",
      Matcher.quoteReplacement(File.separator)) + ".java").toFile();
    return cmpPath.isFile();
  }

  public static List<ImportStatement> getImports(ComponentTypeSymbol symbol) {
    while (symbol.getOuterComponent().isPresent()) {
      symbol = symbol.getOuterComponent().get();
    }
    ASTComponentType ast = symbol.getAstNode();
    return ((MontiArcArtifactScope) ast.getEnclosingScope()).getImportsList();
  }

  protected static List<String> getInheritedParams(final ComponentTypeSymbol component) {
    List<String> result = new ArrayList<String>();
    List<VariableSymbol> configParameters = component.getParameters();
    boolean _isPresentParentComponent = component.isPresentParentComponent();
    if (_isPresentParentComponent) {
      ComponentTypeSymbol superCompReference = component.getParent().getTypeInfo();
      if (superCompReference instanceof ComponentTypeSymbolSurrogate) {
        superCompReference = ((ComponentTypeSymbolSurrogate) superCompReference).lazyLoadDelegate();
      }
      List<VariableSymbol> superConfigParams = superCompReference.getParameters();
      boolean _isEmpty = configParameters.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        for (int i = 0; (i < superConfigParams.size()); i++) {
          result.add(configParameters.get(i).getName());
        }
      }
    }
    return result;
  }

  public static void appendAllInnerComponents(List<ComponentTypeSymbol> components) {
    List<ComponentTypeSymbol> innerComponents = getAllInnerComponents(components);
    components.addAll(innerComponents);
  }

  public static List<ComponentTypeSymbol> getAllInnerComponents(List<ComponentTypeSymbol> components) {
    List<ComponentTypeSymbol> innerComponents = new ArrayList<>();
    components.forEach(c -> innerComponents.addAll(getAllInnerComponents(c)));
    return innerComponents;
  }

  public static List<ComponentTypeSymbol> getAllInnerComponents(ComponentTypeSymbol component) {
    List<ComponentTypeSymbol> subcomponents = new ArrayList<>(component.getInnerComponents());
    for (ComponentTypeSymbol innerComp : component.getInnerComponents()) {
      subcomponents.addAll(getAllInnerComponents(innerComp));
    }
    return subcomponents;
  }

  public boolean hasAutomatonBehavior() {
    return getAutomatonBehavior().isPresent();
  }

  public boolean hasComputeBehavior() {
    return getComputeBehavior().isPresent();
  }

  public Optional<ASTArcStatechart> getAutomatonBehavior() {
    Preconditions.checkState(component != null);
    Preconditions.checkState(component.isPresentAstNode());

    return this.component.getAstNode().getBody().getArcElementList().stream()
      .filter(el -> el instanceof ASTArcStatechart)
      .map(el -> (ASTArcStatechart) el)
      .findFirst();
  }

  public Optional<ASTArcCompute> getComputeBehavior() {
    Preconditions.checkState(component != null);
    Preconditions.checkState(component.isPresentAstNode());

    return this.component.getAstNode().getBody().getArcElementList().stream()
      .filter(el -> el instanceof ASTArcCompute)
      .map(el -> (ASTArcCompute) el)
      .findFirst();
  }

  public static ArcAutomatonHelper automatonHelperFrom(@NotNull ASTArcStatechart stateChart) {
    return new ArcAutomatonHelper(stateChart);
  }

  public Set<ASTNameExpression> getNamesInExpression(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkArgument(expr.getEnclosingScope() != null);
    Preconditions.checkArgument(expr.getEnclosingScope() instanceof IMontiArcScope);
    NamesInExpressionsVisitor visitor = new NamesInExpressionsVisitor();
    MontiArcTraverser traverser = MontiArcMill.traverser();
    visitor.registerTo(traverser);
    visitor.setTraverser(traverser);
    expr.accept(traverser);

    // check if the expression refers to a field or port and not a Type (e.g. an Enum: Days.MONDAY)
    Collection<String> portsOfComp = this.component.getAllPorts().stream()
      .map(ISymbol::getName)
      .collect(Collectors.toSet());

    return visitor.getFoundNames().keySet().stream()
      .filter(astName ->
        portsOfComp.contains(astName.getName())
          || !((IMontiArcScope) expr.getEnclosingScope()).resolveVariableMany(astName.getName()).isEmpty()
      )
      .collect(Collectors.toSet());
  }
}

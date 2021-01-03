/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
<<<<<<< HEAD:generators/ma2java/src/main/java/de/montiarcautomaton/generator/helper/ComponentHelper.java
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
=======
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
>>>>>>> bb276d4fcc3784a5352ae1a8711ede81331f4772:generators/ma2java/src/main/java/montiarc/generator/helper/ComponentHelper.java
import de.monticore.symboltable.ImportStatement;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._visitor.MontiArcPrettyPrinterDelegator;
import montiarc.generator.codegen.xtend.util.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Helper class used in the template to generate target code of atomic or
 * composed components.
 */
public class ComponentHelper {

  public static String DEPLOY_STEREOTYPE = "deploy";

  private final ComponentTypeSymbol component;

  protected final ASTComponentType componentNode;

  public ComponentHelper(ComponentTypeSymbol component) {
    this.component = component;
    if ((component.isPresentAstNode())
      && (component.getAstNode() instanceof ASTComponentType)) {
      componentNode = (ASTComponentType) component.getAstNode();
    } else {
      componentNode = null;
    }
  }

  /**
   * Prints the type of the given port respecting inherited ports and the actual
   * type values
   *
   * @param port Symbol of the port of which to determine the type
   * @return The string representation of the type
   */
  public String getRealPortTypeString(PortSymbol port) {
    return getRealPortTypeString(this.component, port);
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This
   * takes in to account whether the port is inherited and possible required
   * renamings due to generic type parameters and their actual arguments.
   *
   * @param componentSymbol Symbol of the component which contains the port
   * @param portSymbol Symbol of the port for which the type name should be
   * determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ComponentTypeSymbol componentSymbol,
    PortSymbol portSymbol) {
    return portSymbol.getType().print();
  }
  /*

   */
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
   *//*

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
  public String printExpression(ASTExpression expr, boolean isAssignment) {
    IndentPrinter printer = new IndentPrinter();
    ExpressionsBasisPrettyPrinter prettyPrinter;
    if (isAssignment) {
      prettyPrinter = new AssignmentExpressionsPrettyPrinter(printer);
    } else {
      prettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    }
    expr.accept(prettyPrinter);
    return printer.getContent();
  }

  public String printExpression(ASTExpression expr) {
    return printExpression(expr, true);
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
    List<ASTExpression> configArguments = param.getArguments();
    MontiArcPrettyPrinterDelegator printer = new MontiArcPrettyPrinterDelegator(
      new IndentPrinter());

    List<String> outputParameters = new ArrayList<>();
    for (ASTExpression configArgument : configArguments) {
      final String prettyprint = printer.prettyprint(configArgument);
      outputParameters.add(autobox(prettyprint));
    }

    // Append the default parameter values for as many as there are left
    final List<VariableSymbol> configParameters = param.getType().getParameters();

    // Calculate the number of missing parameters
    int numberOfMissingParameters = configParameters.size() - configArguments.size();

    if (numberOfMissingParameters > 0) {
      // Get the AST node of the component and the list of parameters in the AST
      final ASTComponentType astNode = (ASTComponentType) param.getType().getAstNode();
      final List<ASTArcParameter> parameters = astNode.getHead().getArcParametersList();

      // Retrieve the parameters from the node and add them to the list
      for (int counter = 0; counter < numberOfMissingParameters; counter++) {
        // Fill up from the last parameter
        final ASTArcParameter astParameter = parameters.get(parameters.size() - 1 - counter);
        final String prettyprint = printer
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
    final ComponentTypeSymbol componentTypeReference = instance.getType();

    String packageName = Utils.printPackageWithoutKeyWordAndSemicolon(componentTypeReference);
    if (packageName != null && !packageName.equals("")) {
      result = packageName + ".";
    }
    result += componentTypeReference.getName();
/*    if (componentTypeReference.hasActualTypeArguments()) {
      result += printTypeArguments(componentTypeReference.getActualTypeArguments());
    }*/
    return result;
  }

  /**
   * Determine whether the port of the given connector is an incoming or
   * outgoing port.
   *
   * @param cmp The component defining the connector
   * @param source The source name of the connector which connects the port to check
   * @param target The target name of the connector which connects the port to check
   * @param isSource Specifies whether the port to check is the source port of
   * the connector or the target port
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
      Optional<ComponentInstanceSymbol> subCompInstance = cmp.getSpannedScope()
        .resolveComponentInstance(subCompName);
      ComponentTypeSymbol subComp = subCompInstance.get().getType();
      port = subComp.getSpannedScope().resolvePort(portNameUnqualified);
    } else {
      port = cmp.getSpannedScope().resolvePort(portName);
    }

    return port.map(PortSymbol::isIncoming).orElse(false);
  }

  /**
   * Returns the component name of a connection.
   *
   * @param source The source name of the connector which connects the port to check
   * @param target The target name of the connector which connects the port to check
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
   * @param source The source name of the connector which connects the port to check
   * @param target The target name of the connector which connects the port to check
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
   * Checks whether the given typeName for the component comp is a generic
   * parameter.
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
        ((ASTGenericComponentHead) comp.getHead()).getArcTypeParametersList();
      for (ASTArcTypeParameter type : parameterList) {
        if (type.getName().equals(typeName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @return A list of String representations of the actual type arguments
   * assigned to the super component
   */
  public List<String> getSuperCompActualTypeArguments() {
    final List<String> paramList = new ArrayList<>();
    if (component.isPresentParentComponent()) {
      final ComponentTypeSymbol componentSymbolReference = component.getParent();
/*      final List<ActualTypeArgument> actualTypeArgs = componentSymbolReference
        .getActualTypeArguments();
      String componentPrefix = this.component.getFullName() + ".";
      for (ActualTypeArgument actualTypeArg : actualTypeArgs) {
        final String printedTypeArg = SymbolPrinter.printTypeArgument(actualTypeArg);
        if (printedTypeArg.startsWith(componentPrefix)) {
          paramList.add(printedTypeArg.substring(componentPrefix.length()));
        } else {
          paramList.add(printedTypeArg);
        }
      }*/
    }
    return paramList;
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
}
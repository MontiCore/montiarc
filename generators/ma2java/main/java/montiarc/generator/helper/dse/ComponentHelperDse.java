/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper.dse;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ArcPortSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis.check.CompTypeExpression;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import montiarc.MontiArcMill;
import montiarc.generator.dse.MA2JavaDseFullPrettyPrinter;
import montiarc.generator.helper.ComponentHelper;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class used in the template to generate target code of atomic or composed components.
 */
public class ComponentHelperDse {

  protected MA2JavaDseFullPrettyPrinter prettyPrinter;

  public ComponentHelperDse() {
    this.prettyPrinter = new MA2JavaDseFullPrettyPrinter();
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This takes into
   * account whether the port is
   * inherited and possible required renamings due to generic type parameters and their actual
   * arguments.
   *
   * @param type Symbol of the port for which the type name should be determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(SymTypeExpression type) {
    return type.isPrimitive() ?
      ((SymTypePrimitive) type).getBoxedPrimitiveName() :
      type.isTypeVariable() ? type.print() :
        type.printFullName();
  }
  public static String getRealPortTypeString(ArcPortSymbol portSymbol) {
    return getRealPortTypeString(portSymbol.getType());
  }

  public static String getRealPortTypeString(VariableSymbol symbol){
    return getRealPortTypeString(symbol.getType());
  }

  /**
   * Returns a list with all TestControllers, defined by the user in the package main/java/controller
   */
  public static List<String> getPathStrategies() {

    List<String> result = new ArrayList<>();
    String moduleRelativePath = Paths.get("main/java/controller").toString();
    Path absolutePath = Paths.get("").toAbsolutePath().resolve(moduleRelativePath);
    File folder = new File(absolutePath.toAbsolutePath().toString());

    if (folder.exists()) {
      for (String file : folder.list()) {
        result.add(file.replace(".java", ""));
      }
    }
    return result;
  }

  /**
   * checks if the port has a character as type
   */
  public static boolean isCharacter(VariableSymbol port) {
    switch (port.getType().print()) {
      case "Character":
      case "char":
        return true;
      default:
        return false;
    }
  }

  /**
   * Prints the extraction of numbers of a String
   */
  public static String printCharReplace(ArcPortSymbol sym){
    switch (getRealPortTypeString(sym)){
      case "char":
      case "java.lang.Character":
        return ".replaceAll(\"[^0-9]\", \"\")";
      default:
        return "";
    }
  }

  /**
   * checks if the port has a float or double as type
   */
  public static boolean isFloatOrDouble(ArcPortSymbol symbol){
    switch (symbol.getType().print()){
      case "Double":
      case "double":
      case "Float":
      case "float":
        return true;
      default:
        return false;
    }
  }

  /**
   * checks if the port has a string as type
   */
  public static boolean isString(ArcPortSymbol symbol){
    return symbol.getType().print().equals("String");
  }

  /**
   * Prints the parsing phrase with the corresponding element to create an element of a sort
   */
  public static String getParameterType(VariableSymbol symbol) {
    switch (symbol.getType().print()) {
      case "Integer":
      case "int":
      case "Long":
      case "long":
        return "mkInt(Integer.parseInt" ;
      case "Boolean":
      case "boolean":
        return "mkBool(Boolean.parseBoolean";
      case "Character":
      case "char":
        return "charFromBv(ctx.mkBV'";
      case "String":
        return "mkString(";
      case "Float":
      case "double":
      case "Double":
      case "float":
        return "mkReal(String.valueOf";
      default:
        return "mkEnumSort(" + symbol.getType().print().toLowerCase();
    }
  }

  /**
   * Prints the parsing phrase according to the port type
   */
  public static String getParseType (ArcPortSymbol port){
    return getParseType(getRealPortTypeString(port));
  }

  /**
   * Prints the parsing phrase according to the symobl type
   */
  public static String getParseType (VariableSymbol symbol){
    return getParseType(getRealPortTypeString(symbol));
  }

  /**
   * Prints the parsing phrase according to the type
   */
  public static String getParseType(String name) {
    switch (name) {
      case "java.lang.Integer":
      case "int":
        return "Integer.parseInt";
      case "java.lang.Long":
      case "long":
        return "Long.parseLong";
      case "java.lang.Boolean":
      case "boolean":
        return "Boolean.parseBoolean";
      case "java.lang.Character":
      case "char":
        return "(char) Integer.parseInt";
      case "short":
      case "java.lang.String":
        return "";
      case "java.lang.Float":
      case "float":
        return "Float.parseFloat";
      case "java.lang.Double":
      case "double":
        return "Double.parseDouble";
      default:
        return name;
    }
  }

  /**
   * Returns a list of all enum values for an enum that is a type of ASTPort.
   */
  public static List<FieldSymbol> getEnumValues(ASTArcPort port) {
    if (isEnum(port.getSymbol())) {
      OOTypeSymbol test = (OOTypeSymbol) port.getSymbol().getTypeInfo();
      return test.getFieldList();
    }
    return null;
  }

  /**
   * Returns a list of all enum values for an enum that is a type of VariableSymbol
   */
  public static List<FieldSymbol> getEnumValues(VariableSymbol symbol) {
    if (isEnum(symbol)) {
      OOTypeSymbol test = (OOTypeSymbol) symbol.getType().getTypeInfo();
      return test.getFieldList();
    }
    return null;
  }

  /**
   * Creates a list of VariableSymbols that do not have duplicate types and do not overlap with
   * port types
   */
  public static List<VariableSymbol> getEnumSorts(ASTComponentType comp) {
    List<String> list = new ArrayList<>();
    List<VariableSymbol> portsWithOutDuplicates = new ArrayList<>();

    for (ASTArcPort element : getPortTypes(comp.getPorts())) {
      list.add(element.getSymbol().getType().print());
    }

    for (VariableSymbol parameter : comp.getSymbol().getParametersList()) {
      if (!list.contains(parameter.getType().print())) {
        list.add(parameter.getType().print());
        portsWithOutDuplicates.add(parameter);
      }
    }
    for (VariableSymbol variable : getComponentVariables(comp.getSymbol())) {
      if (!list.contains(variable.getType().print())) {
        list.add(variable.getType().print());
        portsWithOutDuplicates.add(variable);
      }
    }
    return portsWithOutDuplicates;
  }

  /**
   * Creates a list of ASTPorts that have no duplicates with respect to the port type
   *
   * @param ports List of ASTPorts
   * @return list of ASTPorts
   */
  public static List<ASTArcPort> getPortTypes(List<ASTArcPort> ports) {
    List<String> list = new ArrayList<>();
    List<ASTArcPort> portsWithOutDuplicates = new ArrayList<>();

    for (ASTArcPort port : ports) {
      if (!list.contains(port.getSymbol().getType().print())) {
        list.add(port.getSymbol().getType().print());
        portsWithOutDuplicates.add(port);
      }
    }
    return portsWithOutDuplicates;
  }

  /**
   * Checks if the variable symbol type is an enum or not
   *
   * @param symbol to be checked
   * @return if symbol type is an enum
   */
  public static boolean isEnum(VariableSymbol symbol) {
    if (symbol.getType().getTypeInfo() instanceof OOTypeSymbol) {
      return ((OOTypeSymbol) symbol.getType().getTypeInfo()).isIsEnum();
    }
    return false;
  }

  /**
   * Checks if the port type is an enum or not
   *
   * @param port to be checked
   * @return if port type is an enum
   */
  public static boolean isEnum(ArcPortSymbol port) {
    if (port.getTypeInfo() instanceof OOTypeSymbol) {
      return ((OOTypeSymbol) port.getType().getTypeInfo()).isIsEnum();
    }
    return false;
  }

  /**
   * Returns the sort type corresponding to the VariableSymbol type
   *
   * @param sym to get the type from
   * @return string sort type of the variable symbol type
   */
  public static String getPortTypeSort(VariableSymbol sym) {
    return getSort(sym.getType().printFullName());
  }

  /**
   * Returns the sort type corresponding to the port type
   *
   * @param port to get the type from
   * @return string sort type of the port type
   */
  public static String getPortTypeSort(ArcPortSymbol port) {
    return getSort(port.getTypeInfo().getFullName());
  }

  public static String getSort(String name) {
    switch (name) {
      case "java.lang.Integer":
      case "int":
      case "java.lang.Long":
      case "long":
        return "IntSort";
      case "java.lang.Boolean":
      case "boolean":
        return "BoolSort";
      case "java.lang.Character":
      case "char":
        return "CharSort";
      case "java.lang.String":
        return "SeqSort<CharSort>";
      case "java.lang.Float":
      case "double":
      case "java.lang.Double":
      case "float":
        return "RealSort";
      default:
        return "EnumSort<" + name + ">";
    }
  }

  /**
   * Returns the sort type to print mkSort, according  to the port type
   * @param symbol
   * @return
   */
  public static String getMkSort(ArcPortSymbol symbol) {
    switch (symbol.getType().printFullName()){
      case "java.lang.String":
        return "StringSort";
      default:
        return getSort(symbol.getType().printFullName());
    }
  }

  public static List<VariableSymbol> getComponentVariables(ComponentTypeSymbol comp) {
    Preconditions.checkNotNull(comp);
    List<VariableSymbol> vss = new ArrayList<>(comp.getFields());
    vss.removeAll(comp.getParametersList());
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
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(SubcomponentSymbol instance) {
    String result = "";
    ComponentSymbol componentTypeReference = instance.getType().getTypeInfo();
    if (componentTypeReference instanceof ComponentTypeSymbolSurrogate) {
      componentTypeReference =
        ((ComponentTypeSymbolSurrogate) componentTypeReference).lazyLoadDelegate();
    }
    String packageName =
      ComponentHelper.printPackageWithoutKeyWordAndSemicolon(componentTypeReference);
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
      return printPackageWithoutKeyWordAndSemicolon(comp.getOuterComponent()
        .get()) + "." + comp.getOuterComponent().get().getName();
    }
    else {
      return comp.getPackageName();
    }
  }

  protected MA2JavaDseFullPrettyPrinter getPrettyPrinter() {
    return this.prettyPrinter;
  }

  /**
   * Prints the java expression of the given AST expression node.
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

  /**
   * Calculates the values of the parameters of a {@link CompKindExpression}.
   *
   * @param expr The {@link CompKindExpression} for which the parameters should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(CompKindExpression expr) {
    return getParamValues(expr.getParamBindings(), expr.getTypeInfo());
  }

  /**
   * Calculates the values of hierarchical parameter instantiations of a
   * {@link ComponentTypeSymbol}.
   *
   * @param comp The {@link CompTypeExpression} for which the parameters should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParentParamValues(ComponentTypeSymbol comp) {
    return getParamValues(comp.getSuperComponents(0).getParamBindings(), comp.getSuperComponents(0).getTypeInfo());
  }

  /**
   * Calculates the values of the parameters for a given {@link Map} containing the
   * {@link VariableSymbol} and the
   * matching {@link ASTArcArgument}, given its referencing ComponentTypeSymbol. This takes
   * default values for parameters into account and adds them as
   * required. Default values are only added from left to right in order. <br/>
   * Example: For a component with parameters
   * <code>String stringParam, Integer integerParam = 2, Object objectParam = new Object()</code>
   * that is instantiated with parameters <code>"Test String", 5</code> this method adds
   * <code>new Object()</code> as
   * the last parameter.
   *
   * @param configArguments The {@link Map} that contains the parameter bindings.
   * @param comp            The {@link ComponentSymbol} for which the parameters should be
   *                        calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(Map<VariableSymbol, ASTExpression> configArguments,
                                           ComponentSymbol comp) {

    List<String> outputParameters = new ArrayList<>();

    //can only print default parameters if ASTNode exists.
    if (comp.isPresentAstNode() && MontiArcMill.typeDispatcher().isASTComponentType(comp.getAstNode())) {
      final ASTComponentType astNode = MontiArcMill.typeDispatcher().asASTComponentType(comp.getAstNode());

      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      Map<String, ASTExpression> defaultValues = new HashMap<>();
      for (ASTArcParameter parameter : parameters) {
        if (parameter.isPresentDefault()) {
          defaultValues.put(parameter.getName(), parameter.getDefault());
        }
      }
      for (VariableSymbol v : comp.getParametersList()) {
        if (configArguments.containsKey(v)) {
          final String prettyprint = this.getPrettyPrinter()
            .prettyprint(configArguments.get(v));
          outputParameters.add(prettyprint);
        }
        else {
          final String prettyprint = this.getPrettyPrinter()
            .prettyprint(defaultValues.get(v.getName()));
          outputParameters.add(prettyprint);
        }
      }
    }
    else {
      for (VariableSymbol v : comp.getParametersList()) {
        Preconditions.checkNotNull(configArguments.get(v));
        final String prettyprint = this.getPrettyPrinter()
          .prettyprint(configArguments.get(v));
        outputParameters.add(prettyprint);
      }
    }
    return outputParameters;
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

  public Optional<ASTArcInit> getInitBehavior(ASTComponentType component) {
    Preconditions.checkNotNull(component);

    return component.getBody().getArcElementList().stream()
      .filter(el -> el instanceof ASTArcInit)
      .map(el -> (ASTArcInit) el)
      .findFirst();
  }

  public String boxPrimitive(SymTypeExpression symType) {
    Preconditions.checkArgument(symType instanceof SymTypePrimitive);
    return ((SymTypePrimitive) symType).getBoxedPrimitiveName();
  }

  public List<Object> asList(Object... objects) {
    return Arrays.asList(objects);
  }
}
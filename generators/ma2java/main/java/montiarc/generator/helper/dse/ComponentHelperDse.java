/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper.dse;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.*;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import arcbasis.check.CompTypeExpression;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import montiarc.generator.dse.MA2JavaDseFullPrettyPrinter;
import montiarc.generator.helper.ComponentHelper;

import java.util.*;

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
   * Returns a list of all enum values for an enum that is a type of ASTPort.
   */
  public static List<FieldSymbol> getEnumValues(ASTPort port) {
    if (isEnum(port)) {
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

    for (ASTPort element : getPortTypes(comp.getPorts())) {
      list.add(element.getSymbol().getType().print());
    }

    for (VariableSymbol parameter : comp.getSymbol().getParameters()) {
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
  public static List<ASTPort> getPortTypes(List<ASTPort> ports) {
    List<String> list = new ArrayList<>();
    List<ASTPort> portsWithOutDuplicates = new ArrayList<>();

    for (ASTPort port : ports) {
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
  public static boolean isEnum(ASTPort port) {
    if (port.getSymbol().getTypeInfo() instanceof OOTypeSymbol) {
      return ((OOTypeSymbol) port.getSymbol().getType().getTypeInfo()).isIsEnum();
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
    return getSort(sym.getType().print());
  }

  /**
   * Returns the sort type corresponding to the port type
   *
   * @param port to get the type from
   * @return string sort type of the port type
   */
  public static String getPortTypeSort(PortSymbol port) {
    return getSort(port.getTypeInfo().getName());
  }

  public static String getSort(String name) {
    switch (name) {
      case "Integer":
      case "int":
      case "Long":
      case "long":
        return "IntSort";
      case "Boolean":
      case "boolean":
        return "BoolSort";
      case "Character":
      case "char":
        return "CharSort";
      case "String":
        return "SeqSort<CharSort>";
      case "Float":
      case "double":
      case "Double":
      case "float":
        return "RealSort";
      default:
        return "EnumSort<" + name + ">";
    }
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
   * Print the type of the specified subcomponent.
   *
   * @param instance The instance of which the type should be printed
   * @return The printed subcomponent type
   */
  public static String getSubComponentTypeName(ComponentInstanceSymbol instance) {
    String result = "";
    ComponentTypeSymbol componentTypeReference = instance.getType().getTypeInfo();
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
   * Calculates the values of the parameters of a {@link CompTypeExpression}.
   *
   * @param expr The {@link CompTypeExpression} for which the parameters should be calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(CompTypeExpression expr) {
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
    return getParamValues(comp.getParent().getParamBindings(), comp.getParent().getTypeInfo());
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
   * @param comp            The {@link ComponentTypeSymbol} for which the parameters should be
   *                        calculated.
   * @return The parameters.
   */
  public Collection<String> getParamValues(Map<VariableSymbol, ASTArcArgument> configArguments,
                                           ComponentTypeSymbol comp) {

    List<String> outputParameters = new ArrayList<>();

    //can only print default parameters if ASTNode exists.
    if (comp.isPresentAstNode()) {
      final ASTComponentType astNode = comp.getAstNode();

      final List<ASTArcParameter> parameters = astNode.getHead().getArcParameterList();

      Map<String, ASTExpression> defaultValues = new HashMap<>();
      for (ASTArcParameter parameter : parameters) {
        if (parameter.isPresentDefault()) {
          defaultValues.put(parameter.getName(), parameter.getDefault());
        }
      }
      for (VariableSymbol v : comp.getParameters()) {
        if (configArguments.containsKey(v)) {
          final String prettyprint = this.getPrettyPrinter()
            .prettyprint(configArguments.get(v).getExpression());
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
      for (VariableSymbol v : comp.getParameters()) {
        Preconditions.checkNotNull(configArguments.get(v));
        final String prettyprint = this.getPrettyPrinter()
          .prettyprint(configArguments.get(v).getExpression());
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

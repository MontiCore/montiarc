/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper.dse;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
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
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import montiarc.MontiArcMill;
import montiarc.generator.dse.MA2JavaDseFullPrettyPrinterValue;
import montiarc.generator.helper.ComponentHelper;

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
public class ComponentHelperDseValue {

  protected MA2JavaDseFullPrettyPrinterValue prettyPrinter;

  public ComponentHelperDseValue() {
    this.prettyPrinter = new MA2JavaDseFullPrettyPrinterValue();
  }

  /**
   * Determines the name of the type of the port represented by its symbol. This takes in to
   * account whether the port is
   * inherited and possible required renamings due to generic type parameters and their actual
   * arguments.
   *
   * @param portSymbol Symbol of the port for which the type name should be determined.
   * @return The String representation of the type of the port.
   */
  public static String getRealPortTypeString(ArcPortSymbol portSymbol) {
    SymTypeExpression portType = portSymbol.getType();
    return portType.isPrimitive() ?
      ((SymTypePrimitive) portType).getBoxedPrimitiveName() :
      portType.isTypeVariable() ? portType.print() :
        portType.printFullName();
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

  protected MA2JavaDseFullPrettyPrinterValue getPrettyPrinter() {
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
      for (VariableSymbol v : comp.getParameters()) {
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
      for (VariableSymbol v : comp.getParameters()) {
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

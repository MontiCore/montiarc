/* (c) https://github.com/MontiCore/monticore */
package montiarc.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolSurrogate;
import arcbasis._symboltable.PortSymbol;
import arccompute._ast.ASTArcCompute;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypePrimitive;
import montiarc._ast.ASTMontiArcNode;
import montiarc.generator.MA2JavaFullPrettyPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Helper class used in the template to generate target code of atomic or composed components.
 */
public class ComponentHelper {

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
      outputParameters.add(prettyprint);
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

  public String boxPrimitive(SymTypeExpression symType) {
    Preconditions.checkArgument(symType instanceof SymTypePrimitive);
    return ((SymTypePrimitive) symType).getBoxedPrimitiveName();
  }

  public List<Object> asList(Object... objects) {
    return Arrays.asList(objects);
  }
}

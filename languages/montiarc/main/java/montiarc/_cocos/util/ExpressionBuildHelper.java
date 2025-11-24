/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.types.check.SymTypeExpression;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import variablearc._cocos.util.ASTFieldAccessChangeContext;
import variablearc._cocos.util.ASTNameExpressionChangeContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExpressionBuildHelper {

  public static class PortInformation {

    private String portName;
    private PortSymbol arcPort;
    private SubcomponentSymbol subcomponentSymbol;

    public PortInformation(String portName, PortSymbol arcPort) {
      this.portName = portName;
      this.arcPort = arcPort;
    }

    public PortInformation(String portName, PortSymbol arcPort,SubcomponentSymbol subcomponentSymbol) {
      this.portName = portName;
      this.arcPort = arcPort;
      this.subcomponentSymbol = subcomponentSymbol;
    }

    public String getPortName() {
      return this.portName;
    }

    public PortSymbol getArcPort() {
      return this.arcPort;
    }

    public SubcomponentSymbol getSubcomponentSymbol() {return this.subcomponentSymbol; }

  }

  private static IArcBasisScope scope;

  public static IArcBasisScope getScope() {
    return scope;
  }

  public static void setScope(IArcBasisScope scope) {
    ExpressionBuildHelper.scope = scope;
  }

  public static ASTConnector changeConnectorPortNames(ASTConnector connector, Map<String, String> newPortName) {

    var source = connector.getSource();
    var sourceName = source.isPresentComponent() ? source.getComponent() + "." + connector.getSource().getPort() : source.getPort();
    var newSourceName = newPortName.get(sourceName);
    if (newSourceName != null) {
      connector.getSource().setPort(newPortName.get(sourceName));
      connector.getSource().setEnclosingScope(scope);
      connector.getSource().setPortSymbol(scope.getPortSymbols().entries().stream().filter(e -> e.getKey().equals(newPortName.get(sourceName))).map(k -> k.getValue()).collect(Collectors.toList()).get(0));
    }
    for (ASTPortAccess target : connector.getTargetList()) {
      var targetName = target.isPresentComponent() ? target.getComponent() + "." + target.getPort() : target.getPort();
      var newTargetName = newPortName.get(targetName);
      if (newTargetName != null) {
        target.setPort(newTargetName);
        target.setEnclosingScope(scope);
        target.setPortSymbol(scope.getPortSymbols().entries().stream().filter(e -> e.getKey().equals(newPortName.get(targetName))).map(k -> k.getValue()).collect(Collectors.toList()).get(0));
      }
    }
    return connector;
  }

  public static ASTExpression changeNameExpressionElements(ASTExpression expr, Map<String, String> newVariableNames) {
    MontiArcTraverser traverser = MontiArcMill.traverser();
    ASTNameCollector nameCollector = new ASTNameCollector();
    traverser.add4ExpressionsBasis(nameCollector);
    expr.accept(traverser);

    for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
      changeName(nameExpression, newVariableNames);
    }

    return expr;
  }

  private static void changeName(ASTNameExpression expr, Map<String, String> newVariableNames) {
    if (newVariableNames.containsKey(expr.getName())) {
      expr.setName(newVariableNames.get(expr.getName()));
      expr.setEnclosingScope(scope);
    }
  }

  private static void generateVariableCombinations(List<String> variables, Map<String, List<String>> variationsMap, Consumer<LinkedHashMap<String, String>> consumer) {
    LinkedHashMap<String, String> current = new LinkedHashMap<>();
    backtrack(variables, variationsMap, 0, current, consumer);
  }

  private static void backtrack(
    List<String> variables,
    Map<String, List<String>> variationsMap,
    int depth,
    Map<String, String> current,
    Consumer<LinkedHashMap<String, String>> consumer
  ) {
    if (depth == variables.size()) {
      consumer.accept(new LinkedHashMap<>(current));
      return;
    }

    String variable = variables.get(depth);
    List<String> options = variationsMap.get(variable);

    if (options == null || options.isEmpty()) {
      if (!variationsMap.isEmpty() && !variationsMap.containsKey(variable)) {
        backtrack(variables, variationsMap, depth + 1, current, consumer);
        return;
      } else {
        return;
      }
    }
    for (String variant : options) {
      current.put(variable, variant);
      backtrack(variables, variationsMap, depth + 1, current, consumer);
      current.remove(variable);
    }
  }

  public static List<ASTConnector> createPossibleConnectors(ASTConnector connector, Map<String, List<String>> portNameVariations) {
    List<ASTConnector> possibleConnectors = new ArrayList<>();
    List<String> presentPorts = getPortNames(connector);
    Map<String, String> alreadyCreated = new HashMap<>();
    generateVariableCombinations(presentPorts, portNameVariations, combination -> {

      if (alreadyCreated.entrySet().contains(combination))
        return;

      var updatedConnector = changeConnectorPortNames(connector.deepClone(), combination);
      if (!(updatedConnector.getSource().getPort() == null || updatedConnector.getTargetList().stream().filter(e -> e.getPort() == null).map(k -> k).collect(Collectors.toList()).size() > 0))
        possibleConnectors.add(updatedConnector);

    });
    return possibleConnectors;
  }

  public static List<ASTExpression> createPossibleGuardExpressions(ASTExpression expr, Map<String, List<String>> fieldNameVariations) {
    List<ASTExpression> guardExpressions = new ArrayList<>();
    List<String> presentVariables = new ArrayList<>();
    collectVariableNames(expr, presentVariables,false);
    int entryCounter = 1;
    generateVariableCombinations(presentVariables, fieldNameVariations, combination -> {



      ASTExpression exprCopy = expr.deepClone();
      ASTNameExpressionChangeContext changeContextNameExpression = new ASTNameExpressionChangeContext();
      ASTFieldAccessChangeContext changeContextFieldExpression = new ASTFieldAccessChangeContext();
      MontiArcTraverser traverser = MontiArcMill.traverser();
      traverser.add4ExpressionsBasis(changeContextNameExpression);
      traverser.add4CommonExpressions(changeContextFieldExpression);
      expr.accept(traverser);
      exprCopy.accept(traverser);
      changeContextNameExpression.clearScopeMap();
      changeContextFieldExpression.clearScopeMap();


      for (Map.Entry<String, String> entry : combination.entrySet()) {
        String variable = entry.getKey();
        String typeVariant = entry.getValue();
        if (!fieldNameVariations.containsKey(variable)) {
          guardExpressions.add(expr);
        } else {
          changeNameExpressionElements(exprCopy, Map.of(variable, typeVariant));
        }
      }
      guardExpressions.add(exprCopy);
    });
    return guardExpressions;
  }

  public static PortSymbol createPortSymbolForName(PortInformation portInformation, Map<String, List<String>> portNameVariations){

    SymTypeExpression portType;
   if(portInformation.getSubcomponentSymbol() != null && portInformation.getSubcomponentSymbol().isTypePresent() && portInformation.getSubcomponentSymbol().getType().isGenericComponentType()){
     var genericComponent = portInformation.getSubcomponentSymbol().getType().asGenericComponentType();
     portType = genericComponent.getTypeOfPort(portInformation.arcPort.getName()).get().deepClone();
   }else{
     portType = portInformation.arcPort.getType().deepClone();
   }


    PortSymbol createdSymbol = null;
    if (!portType.isNullType() && !portType.isObscureType()) {
      createdSymbol = MontiArcMill.portSymbolBuilder().setType(portType).setEnclosingScope(portInformation.arcPort.getEnclosingScope()).setName(portInformation.getPortName() + "_" + portType.print()).build();
    }

    if (createdSymbol == null) return null;

    if (!scope.getPortSymbols().containsKey(createdSymbol.getName())) {
      scope.add(createdSymbol);
    }

    var currentPortVariation = portNameVariations.get(portInformation.getPortName());
    if (currentPortVariation == null)
      currentPortVariation = new ArrayList<>();
    if (!currentPortVariation.contains(createdSymbol.getName())) {
      currentPortVariation.add(createdSymbol.getName());
    }
    portNameVariations.put(portInformation.getPortName(), currentPortVariation);
    return createdSymbol;
  }

  public static PortSymbol createPortSymbol(PortSymbol portSymbol, Map<String, List<String>> portNameVariations) {
    var portType = portSymbol.getType();

    PortSymbol createdSymbol = null;
    if (!portType.isNullType() && !portType.isObscureType()) {
      createdSymbol = MontiArcMill.portSymbolBuilder().setType(portType).setEnclosingScope(portSymbol.getEnclosingScope()).setName(portSymbol.getName() + "_" + (portType.print().hashCode() & 0x7fffffff)).build();
    }

    if (createdSymbol == null) return null;

    if (!scope.getPortSymbols().containsKey(createdSymbol.getName())) {
      scope.add(createdSymbol);
    }

    var currentPortVariation = portNameVariations.get(portSymbol.getName());
    if (currentPortVariation == null)
      currentPortVariation = new ArrayList<>();
    if (!currentPortVariation.contains(createdSymbol.getName())) {
      currentPortVariation.add(createdSymbol.getName());
    }
    portNameVariations.put(portSymbol.getName(), currentPortVariation);
    return createdSymbol;
  }

  public static VariableSymbol createVariableSymbol(VariableSymbol variableSymbol, Map<String, List<String>> fieldNameVariations) {
    var fieldType = variableSymbol.getType();
    VariableSymbol createdSymbol = null;
    if (!fieldType.isNullType() && !fieldType.isObscureType()) {
      createdSymbol = MontiArcMill.variableSymbolBuilder().setType(fieldType).setEnclosingScope(variableSymbol.getEnclosingScope()).setName(variableSymbol.getAstNode().getName() + "_" + (fieldType.print().hashCode() & 0x7fffffff)).build();
    }
    if (createdSymbol == null) return null;
    scope.add(createdSymbol);

    var currentFieldVariation = fieldNameVariations.get(variableSymbol.getName());
    if (currentFieldVariation == null)
      currentFieldVariation = new ArrayList<>();
    currentFieldVariation.add(createdSymbol.getName());
    fieldNameVariations.put(variableSymbol.getName(), currentFieldVariation);
    return createdSymbol;
  }

  public static VariableSymbol createParameterSymbol(ASTArcParameter parameter, Map<String, List<String>> fieldNameVariations) {
    if (scope == null) setScope(parameter.getEnclosingScope());

    var parameterType = parameter.getSymbol().getType();
    VariableSymbol createdSymbol = null;
    if (!parameterType.isNullType() && !parameterType.isObscureType()) {
      createdSymbol = MontiArcMill.variableSymbolBuilder().setType(parameterType).setEnclosingScope(parameter.getEnclosingScope()).setName(parameter.getName() + "_" + (parameterType.print().hashCode() & 0x7fffffff)).build();
    }
    if (createdSymbol == null) return null;
    scope.add(createdSymbol);

    var currentFieldVariation = fieldNameVariations.get(parameter.getName());
    if (currentFieldVariation == null)
      currentFieldVariation = new ArrayList<>();
    currentFieldVariation.add(createdSymbol.getName());
    fieldNameVariations.put(parameter.getName(), currentFieldVariation);
    return createdSymbol;
  }

  public static List<String> getVariableNames(ASTExpression expr) {
    List<String> result = new ArrayList<>();
    collectVariableNames(expr, result, false);
    return result;
  }

  public static List<String> getAllVariableOccurences(ASTExpression expr) {
    List<String> result = new ArrayList<>();
    collectVariableNames(expr, result, true);
    return result;
  }

  public static List<String> getPortNames(ASTConnector connector) {
    List<String> portNames = new ArrayList<>();
    var sourceName = connector.getSourceName();
    //connector.getSource().isPresentComponent() ? connector.getSource().getComponent() + "." + connector.getSourceName() :
    portNames.add(sourceName);
    for (ASTPortAccess target : connector.getTargetList()) {
      var targetName = target.isPresentComponent() ? target.getComponent() + "." + target.getPort() : target.getPort();
      portNames.add(targetName);
    }
    return portNames;
  }

  private static void collectVariableNames(ASTExpression expr, List<String> variableNames, boolean trackMultipleOccurences) {
    MontiArcTraverser traverser = MontiArcMill.traverser();
    ASTNameCollector nameCollector = new ASTNameCollector();
    traverser.add4ExpressionsBasis(nameCollector);
    expr.accept(traverser);

    for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
      if (trackMultipleOccurences) {
        variableNames.add(nameExpression.getName());
      } else {
        if (!variableNames.contains(nameExpression.getName()))
          variableNames.add(nameExpression.getName());
      }
    }

    //    if (expr instanceof ASTNameExpression) {
    //      variableNames.add(((ASTNameExpression) expr).getName());
    //    } else {
    //      if (expr instanceof ASTGreaterThanExpression) {
    //        collectVariableNames(((ASTGreaterThanExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTGreaterThanExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTGreaterEqualExpression) {
    //        collectVariableNames(((ASTGreaterEqualExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTGreaterEqualExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTLessThanExpression) {
    //        collectVariableNames(((ASTLessThanExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTLessThanExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTLessEqualExpression) {
    //        collectVariableNames(((ASTLessEqualExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTLessEqualExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTEqualsExpression) {
    //        collectVariableNames(((ASTEqualsExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTEqualsExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTAssignmentExpression){
    //        collectVariableNames(((ASTAssignmentExpression) expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTAssignmentExpression) expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTMinusExpression){
    //        collectVariableNames(((ASTMinusExpression)expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTMinusExpression)expr).getRight(), variableNames);
    //      } else if (expr instanceof ASTPlusExpression){
    //        collectVariableNames(((ASTPlusExpression)expr).getLeft(), variableNames);
    //        collectVariableNames(((ASTPlusExpression)expr).getRight(), variableNames);
    //      }
    //    }
    //  }
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.util.ExpressionBuildHelper;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;

public class TransitionPreconditionsAreBoolean4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    ExpressionSet constraints;
    List<Guard> allGuards;
    List<VariableSymbol> createdVariableSymbols;
    List<PortSymbol> createdPortSymbols;
    List<ASTArcParameter> allParameters;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTArcPort,BoolExpr> portConditions;
    Map<String, List<String>> fieldNameVariations;
    Map<ASTArcStatechart, BoolExpr> statechartConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();
    List<ASTArcParameter> mainParameters = node.getHead().getArcParameterList();

    // Getting alls features, ports and variations from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    createdVariableSymbols = new ArrayList<>();
    createdPortSymbols = new ArrayList<>();
    allGuards = new ArrayList<>();
    fieldConditions = new HashMap<>();
    portConditions = new HashMap<>();
    fieldNameVariations = new HashMap<>();
    allParameters = new ArrayList<>(mainParameters);

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      fieldConditions = ((ASTVariableArcFullVariantComponentType) node).getFieldConditions();
      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      statechartConditions = ((ASTVariableArcFullVariantComponentType) node).getStateChartConditions();

      for (Map.Entry<ASTArcStatechart, BoolExpr> chartEntry : statechartConditions.entrySet()) {

        var transitions = chartEntry.getKey().streamTransitions().toList();
        for (ASTSCTransition transition : transitions) {
          ASTTransitionBody body = transition.getSCTBody() instanceof ASTTransitionBody ? (ASTTransitionBody) transition.getSCTBody() : null;
          if (body == null) continue;

          if (!body.isPresentPre()) continue;
          var guardExpression = body.getPre();
          Guard variationGuard = new Guard(guardExpression, chartEntry.getValue(),body);
          allGuards.add(variationGuard);
        }
      }

    } else {
      constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

      List<ASTArcField> mainFields = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFieldDeclaration).map(v -> ((ASTArcFieldDeclaration) v).getArcFieldList()).flatMap(List::stream).toList();
      for (ASTArcField field : mainFields) {
        fieldConditions.put(field, ctx.mkTrue());
      }

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for(ASTArcPort mainPort : mainPorts){
        portConditions.put(mainPort,ctx.mkTrue());
      }

      var mainCharts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcStatechart).map(l -> (ASTArcStatechart) l).toList();
      for (ASTArcStatechart chart : mainCharts) {

        var transitions = chart.streamTransitions().toList();
        for (ASTSCTransition transition : transitions) {
          ASTTransitionBody body = transition.getSCTBody() instanceof ASTTransitionBody ? (ASTTransitionBody) transition.getSCTBody() : null;
          if (body == null) continue;

          if (!body.isPresentPre()) continue;
          var guardExpression = body.getPre();
          Guard mainGuard = new Guard(guardExpression, ctx.mkTrue(),body);
          allGuards.add(mainGuard);
        }
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (Guard guard : allGuards) {
      List<ASTExpression> guardExpressions = new ArrayList<>();
      if(node instanceof ASTVariableArcFullVariantComponentType) {

        // Step 1: Check if guard can be active
        List<BoolExpr> guardExpressionList = new ArrayList<>(List.of(featureConstraints, guard.requiredFeatures));
        var guardSatisfied = ExpressionSolverService.solve(guardExpressionList);
        if (guardSatisfied == Status.UNSATISFIABLE)
          continue;

        guardExpressionList.clear();

        // Step 2: Check which variable-combination can be active
        var variableNames = ExpressionBuildHelper.getVariableNames(guard.getExpression());
        List<ASTArcField> guardFields = new ArrayList<>();
        List<ASTArcPort> guardPorts = new ArrayList<>();
        if (!variableNames.isEmpty()) {
          for (String variableName : variableNames) {
            var possibleFields = fieldConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
            for (Map.Entry<ASTArcField, BoolExpr> entry : possibleFields) {
              guardExpressionList.addAll(List.of(guard.requiredFeatures,featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(guardExpressionList) == Status.SATISFIABLE) {
                guardFields.add(entry.getKey());
              }
              guardExpressionList.clear();
            }



            var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
            for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
              guardExpressionList.addAll(List.of(guard.requiredFeatures, featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(guardExpressionList) == Status.SATISFIABLE) {
                guardPorts.add(entry.getKey());
              }
              guardExpressionList.clear();
            }

              if (guardFields.isEmpty() && guardPorts.isEmpty()) {
                guardExpressions.add(guard.expression);
                break;
              }

              if (!guardFields.isEmpty()) {
                ExpressionBuildHelper.setScope(guardFields.get(0).getEnclosingScope());
                for (ASTArcField guardField : guardFields) {
                  Optional.ofNullable(ExpressionBuildHelper.createVariableSymbol(guardField.getSymbol(), fieldNameVariations)).ifPresent(createdVariableSymbols::add);
                }
              }

              if (!guardPorts.isEmpty()) {
                ExpressionBuildHelper.setScope(guardPorts.get(0).getEnclosingScope());
                for (ASTArcPort guardPort : guardPorts) {
                  Optional.ofNullable(ExpressionBuildHelper.createPortSymbol(guardPort.getSymbol(), fieldNameVariations)).ifPresent(createdPortSymbols::add);
                }
              }

              if (!allParameters.isEmpty()) {
                for (ASTArcParameter parameter : allParameters) {
                  Optional.ofNullable(ExpressionBuildHelper.createParameterSymbol(parameter, fieldNameVariations)).ifPresent(createdVariableSymbols::add);
                }
              }

              guardExpressions.addAll(ExpressionBuildHelper.createPossibleGuardExpressions(guard.expression, fieldNameVariations));
              fieldNameVariations = new HashMap<>();
          }
        } else {
          guardExpressions.add(guard.expression);
        }
      }else{
          guardExpressions.add(guard.getExpression());
      }

      for (ASTExpression guardExpression : guardExpressions) {
          SymTypeExpression preType = TypeCheck3.typeOf(guardExpression);
          if (preType.isObscureType()) {
            Log.debug(() -> String.format("Coco '%s' skipped for transition guard at %s. The type is obscure, an error should already have been logged.",
              this.getClass().getSimpleName(),
              guard.getTransitionBody().get_SourcePositionStart()
            ), "Cocos");
          } else if (!SymTypeRelations.isBoolean(preType)) {
            Log.error(String.format("0xCC111 Expected '%s' but provided '%s'.", BOOLEAN, preType.print()),
              guard.getTransitionBody().getPre().get_SourcePositionStart(),
              guard.getTransitionBody().getPre().get_SourcePositionEnd()
            );
          }
      }
      // Remove created variable-symbols
      for (VariableSymbol variableSymbol : createdVariableSymbols) {
          variableSymbol.getEnclosingScope().remove(variableSymbol);
      }

      // Remove created port-Symbols
      for (PortSymbol portSymbol : createdPortSymbols) {
          portSymbol.getEnclosingScope().remove(portSymbol);
      }
    }
  }

  protected static class Guard {

    private final ASTExpression expression;
    private final BoolExpr requiredFeatures;
    private final ASTTransitionBody transitionBody;

    Guard(ASTExpression expression, BoolExpr requiredFeatures, ASTTransitionBody transitionBody) {
      this.expression = expression;
      this.requiredFeatures = requiredFeatures;
      this.transitionBody = transitionBody;
    }

    public ASTExpression getExpression() {
      return expression;
    }

    public BoolExpr getRequiredFeatures() {
      return requiredFeatures;
    }

    public ASTTransitionBody getTransitionBody() {return transitionBody;}

  }
}

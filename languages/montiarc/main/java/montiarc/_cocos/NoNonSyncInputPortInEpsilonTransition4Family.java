/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arcbasis._symboltable.IArcBasisScope;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.Port2VariableAdapter;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTNameCollector;
import montiarc._cocos.util.ASTTransitionBodyCollector;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.VariableArcVariationPoint;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static de.monticore.symbols.compsymbols._symboltable.Timing.TIMED_SYNC;
import static montiarc.util.ArcError.IN_PORT_REF_IN_INVALID_CONTEXT;

public class NoNonSyncInputPortInEpsilonTransition4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if (DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    ExpressionSet constraints;

    Map<ASTTransitionBody, BoolExpr> transitionConditions;
    Map<PortSymbol,BoolExpr> portsymbolConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    transitionConditions = new HashMap<>();
    portsymbolConditions = new HashMap<>();

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTTransitionBodyCollector transitionCollector = new ASTTransitionBodyCollector();
    nodeTraverser.add4SCTransitions4Code(transitionCollector);
    node.accept(nodeTraverser);

    for (ASTTransitionBody transitionBody : transitionCollector.getExpressions()) {
      transitionConditions.put(transitionBody, ctx.mkTrue());
    }

    constraints = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();

      portsymbolConditions = ((ASTVariableArcFullVariantComponentType) node).getPortSymbolConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = VariationConditionHelper.getExpressionSetCopyWithContext(new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions())));
        varifExprSet.getExpressions().forEach(k -> VariationConditionHelper.changeNameExpressionInCondition(k.getAstExpression()));

        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        MontiArcTraverser varifTraverser = MontiArcMill.traverser();
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).toList();

        ASTTransitionBodyCollector varifTransitionCollector = new ASTTransitionBodyCollector();
        varifTraverser.add4SCTransitions4Code(varifTransitionCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
          for (ASTTransitionBody transition : varifTransitionCollector.getExpressions()) {
            transitionConditions.put(transition, variationExpr);
          }
          varifTransitionCollector.clearExpressions();
        }
      }
    } else {
      if (node.isPresentSymbol())
        constraints = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();

    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, constraints, allFeatures, expSolver);

    for (Map.Entry<ASTTransitionBody, BoolExpr> doEntry : transitionConditions.entrySet()) {

      if(doEntry.getKey().isPresentSCEvent()){
            continue;
      }

      // Step 1: Check if transition can be active
      List<BoolExpr> doEntryExpressionList = new ArrayList<>(List.of(featureConstraints, doEntry.getValue()));
      if (ExpressionSolverService.solve(doEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      doEntryExpressionList.clear();

      // Step 2: Get all nameExpressions from the transition
      MontiArcTraverser nameTraverser = MontiArcMill.traverser();
      ASTNameCollector nameCollector = new ASTNameCollector();
      nameTraverser.add4ExpressionsBasis(nameCollector);
      doEntry.getKey().accept(nameTraverser);
      List<String> variableNames = new ArrayList<>();

      for (ASTNameExpression nameExpression : nameCollector.getExpressions()) {
        variableNames.add(nameExpression.getName());
      }

      MontiArcTraverser variableTraverser = MontiArcMill.traverser();
      ASTVariableDeclaratorCollector variableCollector = new ASTVariableDeclaratorCollector();
      variableTraverser.add4MCVarDeclarationStatements(variableCollector);
      doEntry.getKey().accept(variableTraverser);

      // Step 3: Check if there is a potential violation
      if (!variableNames.isEmpty()) {
        for (String variableName : variableNames) {
          // Check if there are fields shadowing ports
          IArcBasisScope scope = (IArcBasisScope) doEntry.getKey().getEnclosingScope();
          List<VariableSymbol> ports = scope.resolveVariableMany(variableName, this.getVariablePredicate());
          var shadowingFields = variableCollector.getDeclarators().stream().filter(e -> e.getDeclarator().getName().equals(variableName)).toList();

          if (!ports.isEmpty()) {

            if (node instanceof ASTVariableArcFullVariantComponentType) {

              for(VariableSymbol portSymbol : ports){

                if(!(portSymbol instanceof Port2VariableAdapter))
                  continue;

              doEntryExpressionList.clear();
              
              var portConditions = portsymbolConditions.entrySet().stream().filter(e -> e.getKey().equals(((Port2VariableAdapter) portSymbol).getAdaptee())).map(Map.Entry::getValue).toList();
              if (portConditions.isEmpty())
                continue;

              for (BoolExpr expr : portConditions) {
                doEntryExpressionList.clear();
                doEntryExpressionList.addAll(List.of(featureConstraints, ctx.mkAnd(doEntry.getValue(), expr)));
                if (ExpressionSolverService.solve(doEntryExpressionList) == Status.UNSATISFIABLE)
                  continue;

                PortSymbol port = ((Port2VariableAdapter) portSymbol).getAdaptee();
                if (port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)) {
                  if (shadowingFields.isEmpty()) {
                    SourcePosition sourcePosition = node.get_SourcePositionStart();
                    Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, doEntry.getKey()), sourcePosition);
                  }
                }
              }
            }
            } else {
              if (!ports.isEmpty() && ports.get(0) instanceof Port2VariableAdapter) {
                PortSymbol port = ((Port2VariableAdapter) ports.get(0)).getAdaptee();
                if (port.isIncoming() && !port.getTiming().matches(TIMED_SYNC)) {
                  if (shadowingFields.isEmpty()) {
                    SourcePosition sourcePosition = node.get_SourcePositionStart();
                    Log.error(IN_PORT_REF_IN_INVALID_CONTEXT.format(variableName, doEntry.getKey()), sourcePosition);
                  }
                }
              }
            }
          }
          }
        }
    }

  }
  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }
}

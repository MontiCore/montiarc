/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcBehaviorElement;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInstantiationTOP;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AtomicMaxOneBehavior4Family implements ArcBasisASTArcComponentTypeCoCo {

  private static Context ctx;

  private static boolean evaluateCondition(Context ctx, Model model, BoolExpr condition) {
    AtomicMaxOneBehavior4Family.ctx = ctx;
    try {
      Expr<?> eval = model.evaluate(condition, true);
      return eval.isTrue();
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    ctx = ExpressionSolverService.getContext();

    Map<ASTArcBehaviorElement, BoolExpr> behaviorConditions = new HashMap<>();
    Map<ASTComponentInstance, BoolExpr> subcomponentConditions = new HashMap<>();

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    // Maintaining all features, variations, constraints, behaviors and subcomponents
    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    // Getting all features, variations, constraints, connectors and subcomponents from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      subcomponentConditions = ((ASTVariableArcFullVariantComponentType) node).getSubcomponentConditions();
      behaviorConditions = ((ASTVariableArcFullVariantComponentType) node).getBehaviorConditions();

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTComponentInstance> mainSubComps = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInstantiation).map(l -> (ASTComponentInstantiation) l).map(ASTComponentInstantiationTOP::getComponentInstanceList).flatMap(List::stream).collect(Collectors.toList());
      for (ASTComponentInstance mainSubComp : mainSubComps) {
        subcomponentConditions.put(mainSubComp, ctx.mkTrue());
      }

      List<ASTArcBehaviorElement> mainBehaviors = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcBehaviorElement).map(k -> (ASTArcBehaviorElement) k).collect(Collectors.toList());
      for (ASTArcBehaviorElement behavior : mainBehaviors) {
        behaviorConditions.put(behavior, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    // Adding all subcomponent-conditions
    List<BoolExpr> allSubcomponentConditions = new ArrayList<>();
    for (Map.Entry<ASTComponentInstance, BoolExpr> sub : subcomponentConditions.entrySet()) {
      allSubcomponentConditions.add(sub.getValue());
    }

    // Adding all connector-conditions
    List<BoolExpr> allBehaviorConditions = new ArrayList<>();
    for (Map.Entry<ASTArcBehaviorElement, BoolExpr> connector : behaviorConditions.entrySet()) {
      allBehaviorConditions.add(connector.getValue());
    }

    int[] behaviorWeights = new int[allBehaviorConditions.size()];
    Arrays.fill(behaviorWeights, 1);

    // At most one behavior, if component is atomic
    BoolExpr atMostOneBehavior = ctx.mkPBLe(behaviorWeights, behaviorConditions.values().toArray(new Expr[0]), 1);
    // There exist a subcomponent, if any of the conditions hold
    BoolExpr subComponentsExists = ctx.mkOr(allSubcomponentConditions.toArray(new BoolExpr[0]));
    // If no SubComponet exist
    BoolExpr noSubComponents = ctx.mkNot(subComponentsExists);
    // If the component is atomic and there are multiple behaviors
    BoolExpr violation = ctx.mkAnd(noSubComponents, ctx.mkNot(atMostOneBehavior));

    List<BoolExpr> multipleBehaviorExpressionList = new ArrayList<>(List.of(violation, featureConstraints));
    boolean first = true;
    if (ExpressionSolverService.solve(multipleBehaviorExpressionList) == Status.SATISFIABLE) {
      Model model = ExpressionSolverService.getModel();
      for (Map.Entry<ASTArcBehaviorElement, BoolExpr> behavior : behaviorConditions.entrySet()) {
        if (evaluateCondition(ctx, model, behavior.getValue())) {
          if (first) first = false;
          else
            Log.error(ArcError.MULTIPLE_BEHAVIOR.toString(), behavior.getKey().get_SourcePositionStart(), behavior.getKey().get_SourcePositionEnd());
        }
      }
    }
  }
}

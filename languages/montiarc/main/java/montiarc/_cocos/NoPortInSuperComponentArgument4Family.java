/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcParent;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc._cocos.util.ExpressionBuildHelper;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

public class NoPortInSuperComponentArgument4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTArcParent, BoolExpr> parentConditions;

    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());
    List<ASTArcParent> mainSupercomponents = node.getHead().getArcParentList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    parentConditions = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();

    } else {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).collect(Collectors.toList());
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }
    }

    for (ASTArcParent component : mainSupercomponents) {
      parentConditions.put(component, ctx.mkTrue());
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if (node instanceof ASTVariableArcFullVariantComponentType)
      featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTArcParent, BoolExpr> parentEntry : parentConditions.entrySet()) {

      // Step 1: Check if Subcomponent can be active
      List<BoolExpr> parentEntryExpressionList = new ArrayList<>(List.of(featureConstraints, parentEntry.getValue()));
      if (ExpressionSolverService.solve(parentEntryExpressionList) == Status.UNSATISFIABLE)
        continue;

      // Step 2: Get all parameters of the SubComponent and check if there is a violation
      if (parentEntry.getKey().isEmptyArcArguments())
        continue;

      var parentArguments = parentEntry.getKey().getArcArgumentList();
      for (ASTArcArgument argument : parentArguments) {
        var variableNames = ExpressionBuildHelper.getAllVariableOccurences(argument.getExpression());
        if (!variableNames.isEmpty()) {
          for (String variableName : variableNames) {
            var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
            for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
              parentEntryExpressionList.clear();
              parentEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(parentEntryExpressionList) == Status.SATISFIABLE) {
                SourcePosition sourcePosition = argument.getExpression().get_SourcePositionStart();
                Log.error(PORT_REF_IN_STATIC_CONTEXT.format(parentEntry.getKey()), sourcePosition);
              }
            }
          }
        }
      }
    }
  }
}

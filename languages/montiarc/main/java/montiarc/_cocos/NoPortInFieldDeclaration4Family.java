/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
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

import static montiarc.util.ArcError.PORT_REF_IN_STATIC_CONTEXT;

public class NoPortInFieldDeclaration4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    if(DuplicateElementsService.duplicateElementPresent(node))
      return;

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    Map<ASTArcPort, BoolExpr> portConditions;
    Map<ASTArcField, BoolExpr> fieldConditions;

    // Reading and processing parts of the Main-Component
    List<String> mainFeatures = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).toList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    portConditions = new HashMap<>();
    fieldConditions = new HashMap<>();

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      fieldConditions = ((ASTVariableArcFullVariantComponentType) node).getFieldConditions();

    } else {
      ExpressionSet mainConstraintSet = null;
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      List<ASTArcPort> mainPorts = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTComponentInterface).map(v -> ((ASTComponentInterface) v).getPortDeclarationList()).flatMap(List::stream).map(ASTPortDeclaration::getArcPortList).flatMap(List::stream).toList();
      for (ASTArcPort mainPort : mainPorts) {
        portConditions.put(mainPort, ctx.mkTrue());
      }

      List<ASTArcField> mainFields = node.getFields();
      for (ASTArcField field : mainFields) {
        fieldConditions.put(field, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTArcField, BoolExpr> fieldEntry : fieldConditions.entrySet()) {

      // Step 1: Check if field can be active
      List<BoolExpr> fieldEntryExpressionList = new ArrayList<>();
      fieldEntryExpressionList.addAll(List.of(featureConstraints, fieldEntry.getValue()));
      if (ExpressionSolverService.solve(fieldEntryExpressionList) == Status.UNSATISFIABLE)
        continue;



      var variableNames = ExpressionBuildHelper.getAllVariableOccurences(fieldEntry.getKey().getInitial());
      if (variableNames.size() > 0) {
        for (String variableName : variableNames) {
          var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).toList();
          for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
            fieldEntryExpressionList.clear();
            fieldEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
            if (ExpressionSolverService.solve(fieldEntryExpressionList) == Status.SATISFIABLE) {
              SourcePosition sourcePosition = fieldEntry.getKey().getInitial().get_SourcePositionStart();
              Log.error(PORT_REF_IN_STATIC_CONTEXT.format(fieldEntry.getKey().getName()), sourcePosition);
            }
          }
        }
      }

    }
  }
}

/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTArcPort;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import arccompute._ast.ASTArcCompute;
import arccompute._ast.ASTArcInit;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc._cocos.util.ASTVariableDeclaratorCollector;
import montiarc._cocos.util.ExpressionBuildHelper;
import montiarc._visitor.MontiArcTraverser;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
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
import java.util.stream.Collectors;

public class VarDeclarationInitializationHasCorrectType4Family implements ArcBasisASTArcComponentTypeCoCo {

  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;
    List<VariableSymbol> createdVariableSymbols;
    List<PortSymbol> createdPortSymbols;
    List<ASTArcParameter> allParameters;

    Map<ASTVariableDeclarator, BoolExpr> varDeclaratorConditions;
    Map<ASTArcField, BoolExpr> fieldConditions;
    Map<ASTArcPort, BoolExpr> portConditions;
    Map<String, BoolExpr> createdVariablesConditions;
    Map<String, List<String>> fieldNameVariations;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());
    ArrayList<ASTArcParameter> mainParameters = (ArrayList<ASTArcParameter>) node.getHead().getArcParameterList();

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    varDeclaratorConditions = new HashMap<>();
    createdVariableSymbols = new ArrayList<>();
    createdPortSymbols = new ArrayList<>();
    fieldConditions = new HashMap<>();
    portConditions = new HashMap<>();
    createdVariablesConditions = new HashMap<>();
    fieldNameVariations = new HashMap<>();
    allParameters = new ArrayList<>();
    if (!mainParameters.isEmpty())
      allParameters = new ArrayList<>(mainParameters);

    MontiArcTraverser nodeTraverser = MontiArcMill.traverser();
    ASTVariableDeclaratorCollector variableDeclaratorCollector = new ASTVariableDeclaratorCollector();
    nodeTraverser.add4MCVarDeclarationStatements(variableDeclaratorCollector);
    node.accept(nodeTraverser);

    for (ASTVariableDeclarator varDeclarator : variableDeclaratorCollector.getDeclarators()) {
      varDeclaratorConditions.put(varDeclarator, ctx.mkTrue());
    }

    ExpressionSet mainConstraintSet = null;
    if (node instanceof ASTVariableArcFullVariantComponentType) {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      portConditions = ((ASTVariableArcFullVariantComponentType) node).getPortConditions();
      fieldConditions = ((ASTVariableArcFullVariantComponentType) node).getFieldConditions();

      List<VariableArcVariationPoint> variationPoints = ((ASTVariableArcFullVariantComponentType) node).getVariationPoints();
      for (VariableArcVariationPoint variationPoint : variationPoints) {
        var varifExprSet = new ExpressionSet(new ArrayList<>(variationPoint.getAllConditions()));
        var expr = expSolver.convert(varifExprSet);

        if (expr.isEmpty())
          continue;

        BoolExpr variationExpr = ctx.mkAnd(expr.get());
        MontiArcTraverser varifTraverser = MontiArcMill.traverser();
        var statecharts = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcStatechart).map(k -> (ASTArcStatechart) k).collect(Collectors.toList());
        var arcInits = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcInit).map(k -> (ASTArcInit) k).collect(Collectors.toList());
        var arcComputes = variationPoint.getArcElements().stream().filter(e -> e instanceof ASTArcCompute).map(k -> (ASTArcCompute) k).collect(Collectors.toList());

        ASTVariableDeclaratorCollector varifVariableDeclaratorCollector = new ASTVariableDeclaratorCollector();
        varifTraverser.add4MCVarDeclarationStatements(varifVariableDeclaratorCollector);

        for (ASTArcStatechart statechart : statecharts) {
          statechart.accept(varifTraverser);
        }
        for (ASTArcInit arcInit : arcInits) {
          arcInit.accept(varifTraverser);
        }
        for (ASTArcCompute arcCompute : arcComputes) {
          arcCompute.accept(varifTraverser);
        }

        for (ASTVariableDeclarator varDeclarator : varifVariableDeclaratorCollector.getDeclarators()) {
          varDeclaratorConditions.put(varDeclarator, variationExpr);
        }
        varifVariableDeclaratorCollector.clearExpressions();
      }
    } else {
      if (node.isPresentSymbol())
        mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      ArrayList<ASTArcPort> mainPorts = (ArrayList<ASTArcPort>) node.getPorts();
      for (ASTArcPort port : mainPorts) {
        portConditions.put(port, ctx.mkTrue());
      }

      ArrayList<ASTArcField> mainFields = (ArrayList<ASTArcField>) node.getFields();
      for (ASTArcField field : mainFields) {
        fieldConditions.put(field, ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = ctx.mkTrue();
    if(node instanceof ASTVariableArcFullVariantComponentType)
        featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    for (Map.Entry<ASTVariableDeclarator, BoolExpr> declaratorEntry : varDeclaratorConditions.entrySet()) {

      List<ASTExpression> possibleExpressions = new ArrayList<>();
      if(node instanceof ASTVariableArcFullVariantComponentType) {
        // Step 1: Check if Expression can be active
        List<BoolExpr> declaratorEntryExpressionList = new ArrayList<>(List.of(featureConstraints, declaratorEntry.getValue()));
        var expressionSatisfied = ExpressionSolverService.solve(declaratorEntryExpressionList);
        if (expressionSatisfied == Status.UNSATISFIABLE)
          continue;

        declaratorEntryExpressionList.clear();


        // Step 2: Check if individual variables and ports are possible and create all combinations
        var variableNames = ExpressionBuildHelper.getVariableNames(((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression().deepClone());
        List<ASTArcField> expressionFields = new ArrayList<>();
        List<ASTArcPort> expressionPorts = new ArrayList<>();

        if (!variableNames.isEmpty()) {
          for (String variableName : variableNames) {
            var possibleFields = fieldConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
            for (Map.Entry<ASTArcField, BoolExpr> entry : possibleFields) {
              declaratorEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(declaratorEntryExpressionList) == Status.SATISFIABLE) {
                var field = entry.getKey();
                expressionFields.add(entry.getKey());
                createdVariablesConditions.put(field.getName() + "_" + (field.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              declaratorEntryExpressionList.clear();
            }

            var possiblePorts = portConditions.entrySet().stream().filter(e -> e.getKey().getName().equals(variableName)).collect(Collectors.toList());
            for (Map.Entry<ASTArcPort, BoolExpr> entry : possiblePorts) {
              declaratorEntryExpressionList.addAll(List.of(featureConstraints, entry.getValue()));
              if (ExpressionSolverService.solve(declaratorEntryExpressionList) == Status.SATISFIABLE) {
                var port = entry.getKey();
                expressionPorts.add(port);
                createdVariablesConditions.put(port.getName() + "_" + (port.getSymbol().getType().print().hashCode() & 0x7fffffff), entry.getValue());
              }
              declaratorEntryExpressionList.clear();
            }
          }


          if (expressionFields.isEmpty() && expressionPorts.isEmpty()) {
            possibleExpressions.add(((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression());
            break;
          } else {
            ExpressionBuildHelper.setScope(expressionFields.isEmpty() ? expressionPorts.get(0).getEnclosingScope() : expressionFields.get(0).getEnclosingScope());
            for (ASTArcField expressionField : expressionFields) {
              createdVariableSymbols.add(ExpressionBuildHelper.createVariableSymbol(expressionField.getSymbol(), fieldNameVariations));
            }
            for (ASTArcPort expressionPort : expressionPorts) {
              createdPortSymbols.add(ExpressionBuildHelper.createPortSymbol(expressionPort.getSymbol(), fieldNameVariations));
            }
          }



          if (!allParameters.isEmpty()) {
            for (ASTArcParameter parameter : allParameters) {
              createdVariableSymbols.add(ExpressionBuildHelper.createParameterSymbol(parameter, fieldNameVariations));
            }
          }

          var potentialExpressions = ExpressionBuildHelper.createPossibleGuardExpressions(((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression(), fieldNameVariations);

          // Step 3: Check if created combinations are possible
          for (ASTExpression potentialExpression : potentialExpressions) {
            List<String> variablesInExpr = ExpressionBuildHelper.getVariableNames(potentialExpression);

            declaratorEntryExpressionList.add(featureConstraints);
            for (String variableName : variablesInExpr) {
              BoolExpr variableCondition = createdVariablesConditions.get(variableName);
              if (variableCondition != null)
                declaratorEntryExpressionList.add(variableCondition);
            }
            if (ExpressionSolverService.solve(declaratorEntryExpressionList) == Status.SATISFIABLE)
              possibleExpressions.add(potentialExpression);
            declaratorEntryExpressionList.clear();
          }
          fieldNameVariations.clear();
        } else {
          if(declaratorEntry.getKey().isPresentVariableInit()) {
            possibleExpressions.add(((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression());
          }
        }
      }else{
        if(declaratorEntry.getKey().isPresentVariableInit()){
          possibleExpressions.add(((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression());
        }
      }
      for (ASTExpression possibleExpression : possibleExpressions) {
        if (declaratorEntry.getKey().isPresentVariableInit()) {
          var originalExpression = ((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression();
          ((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).setExpression(possibleExpression);
          if(ExpressionBuildHelper.getScope() != null) {
            ((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).getExpression().setEnclosingScope(ExpressionBuildHelper.getScope());
          }
          check(declaratorEntry.getKey());
          ((ASTSimpleInit) declaratorEntry.getKey().getVariableInit()).setExpression(originalExpression);
        }
      }
    }

    // Remove created variable-symbols
    for (VariableSymbol variableSymbol : createdVariableSymbols) {
      variableSymbol.getEnclosingScope().remove(variableSymbol);
    }

    for (PortSymbol portSymbol : createdPortSymbols) {
     portSymbol.getEnclosingScope().remove(portSymbol);
    }
    createdVariableSymbols = new  ArrayList<>();
    createdPortSymbols = new  ArrayList<>();
    ExpressionBuildHelper.setScope(null);
  }

  /**
   * Indicates that the type of the initialization expression is not compatible with the type of the assigned variable.
   */
  public static final String ERROR_CODE = "0xA0921";

  public static final String ERROR_MSG_FORMAT = "Incompatible type '%s' of the initialization expression for variable '%s' " +
    "that is of type '%s'.";

  public void check(ASTVariableDeclarator node) {
    if (!node.isPresentVariableInit() || !(node.getVariableInit() instanceof ASTSimpleInit)) {
      return; // We can only check initializations of the form of expressions (as defined in SimpleInit).

    } else if (!node.getDeclarator().isPresentSymbol()) {
      Log.error(String.format("Could not find a symbol for variable '%s', thus can not check coco '%s'. Check " +
          "whether you have run the symbol table creation before running this coco.",
        node.getDeclarator().getName(), this.getClass().getSimpleName()));

    } else { // Proceed with checking the coco
      SymTypeExpression varType = node.getDeclarator().getSymbol().getType();
      SymTypeExpression initType;

      ASTExpression initExpr = MCVarDeclarationStatementsMill.typeDispatcher()
        .asMCVarDeclarationStatementsASTSimpleInit(node.getVariableInit())
        .getExpression();
      initType = TypeCheck3.typeOf(initExpr, varType);

      if (initType.isObscureType()) {
        // The error is already printed by the IDerive visitors, thus we would spam the log if we would log an error
        // again. Therefore, we only leave a note in the debug log.
        Log.debug(String.format("As the initialization expression for variable '%s' at %s is invalid, coco '%s' " +
            "will not be checked.",
          node.getDeclarator().getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName()), "Cocos");

      } else if (!SymTypeRelations.isCompatible(varType, initType)) {
        Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
          initType.printFullName(), node.getDeclarator().getName(), varType.printFullName()));
      }
    }
  }
}

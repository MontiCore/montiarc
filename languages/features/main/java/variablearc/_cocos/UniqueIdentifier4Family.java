/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTArcComponentType;
import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcPort;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.DuplicateElementsService;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._cocos.util.VariationConditionHelper;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UniqueIdentifier4Family implements ArcBasisASTArcComponentTypeCoCo {


  private List<ElementCondition> allElementList = new ArrayList<>();

  protected static Optional<SourcePosition> optSourcePosOf(@NotNull ISymbol sym) {
    Preconditions.checkNotNull(sym);

    if (!sym.isPresentAstNode()) {
      return Optional.empty();
    }
    ASTNode node = sym.getAstNode();

    if (!node.isPresent_SourcePositionStart()) {
      return Optional.empty();
    }
    return Optional.of(node.get_SourcePositionStart());
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    Preconditions.checkNotNull(node);

    DuplicateElementsService.setComponent(node,false);

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    allElementList = new ArrayList<>();
    Map<ASTArcElement, BoolExpr> elementConditions;


    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();

    if (!node.getSymbol().isPresentAstNode())
      return;

    // Adding names from the main-component
    var mainParmeters = node.getSymbol().getAstNode().getSymbol().getParameterList();
    for (VariableSymbol mainParameterSymbol : mainParmeters) {
      allElementList.add(new ElementCondition(mainParameterSymbol.getName(), ctx.mkTrue(), optSourcePosOf(mainParameterSymbol).orElse(new SourcePosition(-1, -1))));
    }

    var mainTypeParameters = node.getSymbol().getAstNode().getSymbol().getTypeParameters();
    for (TypeVarSymbol mainTypeParamterSymbol : mainTypeParameters) {
      allElementList.add(new ElementCondition(mainTypeParamterSymbol.getName(), ctx.mkTrue(), optSourcePosOf(mainTypeParamterSymbol).orElse(new SourcePosition(-1, -1))));
    }

    if (node instanceof ASTVariableArcFullVariantComponentType) {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      elementConditions = ((ASTVariableArcFullVariantComponentType) node).getElementConditions();

      for (Map.Entry<ASTArcElement, BoolExpr> elementEntry : elementConditions.entrySet()) {
        ArrayList<ASTArcElement> elementList = new ArrayList<>(Collections.singletonList(elementEntry.getKey()));
        addFieldNameOccurences(elementList, elementEntry.getValue());
        addFeatureNameOccurences(elementList, elementEntry.getValue());
        addPortNameOccurences(elementList, elementEntry.getValue());
        addComponentNameOccurences(elementList, elementEntry.getValue());
      }

    } else {
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      // Adding names of the arc-elements that belong to the main-component
      var mainElements = (ArrayList<ASTArcElement>) node.getBody().getArcElementList();
      addFieldNameOccurences(mainElements, ctx.mkTrue());
      addFeatureNameOccurences(mainElements, ctx.mkTrue());
      addPortNameOccurences(mainElements, ctx.mkTrue());
      addComponentNameOccurences(mainElements, ctx.mkTrue());

    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    // Grouping elements by name
    Map<String, List<ElementCondition>> grouped = new LinkedHashMap<>();
    for (ElementCondition element : allElementList) {
      grouped.computeIfAbsent(element.name, e -> new ArrayList<>()).add(element);
    }

    Map<String, List<ElementCondition>> nameToConditions = new LinkedHashMap<>();

    // Grouping the entries by name
    for (ElementCondition entry : allElementList) {
      nameToConditions.computeIfAbsent(entry.name, e -> new ArrayList<>())
        .add(entry);
    }

    // Adding overlapping constraints for each name that has more than 1 entry
    Map<String, BoolExpr> nameConflicts = new LinkedHashMap<>();
    for (Map.Entry<String, List<ElementCondition>> entry : nameToConditions.entrySet()) {
      List<ElementCondition> group = entry.getValue();
      if (group.size() <= 1) continue; // No conflict possible

      List<BoolExpr> conflictPairs = new ArrayList<>();
      for (int i = 0; i < group.size(); i++) {
        for (int j = i + 1; j < group.size(); j++) {
          conflictPairs.add(ctx.mkAnd(group.get(i).condition, group.get(j).condition));
        }
      }

      if (!conflictPairs.isEmpty()) {
        nameConflicts.put(entry.getKey(), ctx.mkOr(conflictPairs.toArray(new BoolExpr[0])));
      }

    }

    // Adding all constraints for the possible conflicts to the solver
    List<BoolExpr> nameConflictExpressionList = new ArrayList<>(nameConflicts.values());
    nameConflictExpressionList.add(featureConstraints);

    if (nameConflicts.isEmpty()) {
      return;
    }

    if (ExpressionSolverService.solve(nameConflictExpressionList) == Status.SATISFIABLE) {
      Model model = ExpressionSolverService.getModel();

      for (Map.Entry<String, List<ElementCondition>> entry : nameToConditions.entrySet()) {
        List<ElementCondition> group = entry.getValue();
        List<ElementCondition> active = new ArrayList<>();
        for (ElementCondition element : group) {
          if (model.evaluate(element.condition, false).isTrue()) {
            active.add(element);
          }
        }

        if (active.size() > 1) {
          DuplicateElementsService.setComponent(node,true);
          Log.error(ArcError.UNIQUE_IDENTIFIER_NAMES.format(active.get(0).name),
            node.getSymbol().getAstNode().get_SourcePositionStart(), node.getSymbol().getAstNode().get_SourcePositionEnd());
        }
      }
    }
  }

  protected void addFieldNameOccurences(ArrayList<ASTArcElement> arcElements, BoolExpr constriantExpr) {
    arcElements.stream().filter(e -> e instanceof ASTArcFieldDeclaration).forEach(e -> {
      for (ASTArcField field : ((ASTArcFieldDeclaration) e).getArcFieldList()) {
        allElementList.add(new ElementCondition(field.getName(), constriantExpr, optSourcePosOf(field.getSymbol()).orElse(new SourcePosition(-1, -1))));
      }
    });
  }

  protected void addPortNameOccurences(ArrayList<ASTArcElement> arcElements, BoolExpr constraintExpr) {
    arcElements.stream().filter(e -> e instanceof ASTPortDeclaration).forEach(e -> {
      for (ASTArcPort port : ((ASTPortDeclaration) e).getArcPortList()) {
        allElementList.add(new ElementCondition(port.getName(), constraintExpr, optSourcePosOf(port.getSymbol()).orElse(new SourcePosition(-1, -1))));
      }
    });
    arcElements.stream().filter(e -> e instanceof ASTComponentInterface).forEach(e -> {
      for (ASTPortDeclaration portDeclaration : ((ASTComponentInterface) e).getPortDeclarationList()) {
        for (ASTArcPort port : portDeclaration.getArcPortList()) {
          allElementList.add(new ElementCondition(port.getName(), constraintExpr, optSourcePosOf(port.getSymbol()).orElse(new SourcePosition(-1, -1))));
        }
      }
    });
  }

  protected void addComponentNameOccurences(ArrayList<ASTArcElement> arcElements, BoolExpr constraintExpr) {
    arcElements.stream().filter(e -> e instanceof ASTArcComponentType).forEach(e -> {
      allElementList.add(new ElementCondition(((ASTArcComponentType) e).getSymbol().getName(), constraintExpr, optSourcePosOf(((ASTArcComponentType) e).getSymbol()).orElse(new SourcePosition(-1, -1))));
      for (ASTComponentInstance componentInstance : ((ASTArcComponentType) e).getComponentInstanceList()) {
        allElementList.add(new ElementCondition(componentInstance.getName(), constraintExpr, optSourcePosOf(componentInstance.getSymbol()).orElse(new SourcePosition(-1, -1))));
      }
    });
    arcElements.stream().filter(e -> e instanceof ASTComponentInstantiation).forEach(e -> {
      for (ASTComponentInstance componentInstance : ((ASTComponentInstantiation) e).getComponentInstanceList()) {
        allElementList.add(new ElementCondition(componentInstance.getName(), constraintExpr, optSourcePosOf(componentInstance.getSymbol()).orElse(new SourcePosition(-1, -1))));
      }
    });
  }

  protected void addFeatureNameOccurences(ArrayList<ASTArcElement> arcElements, BoolExpr constraintExpr) {
    arcElements.stream().filter(e -> e instanceof ASTArcFeatureDeclaration).forEach(e -> {
      for (ASTArcFeature feature : ((ASTArcFeatureDeclaration) e).getArcFeatureList()) {
        allElementList.add(new ElementCondition(feature.getName(), constraintExpr, optSourcePosOf(feature.getSymbol()).orElse(new SourcePosition(-1, -1))));
      }
    });
  }

  public static class ElementCondition {

    String name;
    BoolExpr condition;
    SourcePosition sourcePosition;

    ElementCondition(String name, BoolExpr condition, SourcePosition sourcePosition) {
      this.name = name;
      this.condition = condition;
      this.sourcePosition = sourcePosition;
    }
    public String getName(){
      return this.name;
    }
    public BoolExpr getCondition(){
      return this.condition;
    }
    public SourcePosition getSourcePosition(){
      return this.sourcePosition;
    }
  }

}

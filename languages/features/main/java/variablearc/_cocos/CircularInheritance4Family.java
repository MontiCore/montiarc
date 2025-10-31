/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.*;
import arcbasis._cocos.ArcBasisASTArcComponentTypeCoCo;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import montiarc.util.ArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcFeatureDeclaration;
import variablearc._ast.ASTVariableArcFullVariantComponentType;
import variablearc._cocos.util.ExpressionSolverService;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.ExpressionSet;
import variablearc.evaluation.ExpressionSolver;
import variablearc._cocos.util.VariationConditionHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CircularInheritance4Family implements ArcBasisASTArcComponentTypeCoCo {

  static List<ASTArcComponentType> alreadyCheckedComponents = new ArrayList<>();

  public static Set<BoolExpr> extractExtends(BoolExpr expr) {
    Set<BoolExpr> result = new HashSet<>();
    extract(expr, result);
    return result;
  }

  private static void extract(Expr e, Set<BoolExpr> out) {
    if (e.isConst() && e.isBool() && e.getFuncDecl().getName().toString().toLowerCase().contains("_extend_")) {
      out.add((BoolExpr) e);
    } else {
      for (Expr arg : e.getArgs()) {
        extract(arg, out);
      }
    }
  }

  protected BoolExpr[][] constructTransitiveClosure(Context ctx, BoolExpr[][] extendsDirectly) {
    int matrixLength = extendsDirectly.length;
    BoolExpr[][] reachable = new BoolExpr[matrixLength][matrixLength];

    // Initializing matrix reachable
    for (int i = 0; i < matrixLength; i++)
      System.arraycopy(extendsDirectly[i], 0, reachable[i], 0, matrixLength);

    // Creating closure
    for (int i = 0; i < matrixLength; i++) {
      for (int j = 0; j < matrixLength; j++) {
        for (int k = 0; k < matrixLength; k++) {
          BoolExpr viaConnection = ctx.mkAnd(reachable[j][i], reachable[i][k]);
          reachable[j][k] = ctx.mkOr(reachable[j][k], viaConnection);
        }
      }
    }
    return reachable;
  }

  protected static class ComponentExtension {

    ComponentTypeSymbol source;
    ComponentTypeSymbol target;
    BoolExpr condition;

    ComponentExtension(ComponentTypeSymbol source, ComponentTypeSymbol target, BoolExpr condition) {
      this.source = source;
      this.target = target;
      this.condition = condition;
    }

    void setCondition(BoolExpr condition) {
      this.condition = condition;
    }
  }

  protected List<ComponentExtension> getComponentExtensionList(@NotNull ASTArcComponentType root,
                                                               @NotNull ComponentTypeSymbol next, @NotNull List<ComponentTypeSymbol> visited, @NotNull List<ComponentExtension> extensions) {
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(next);
    Preconditions.checkNotNull(visited);
    Preconditions.checkNotNull(extensions);

    for (CompKindExpression parent : next.getSuperComponentsList()) {
      if (parent.getTypeInfo().equals(root.getSymbol())) {
        extensions.add(new ComponentExtension(next, parent.getTypeInfo(), null));
      } else if (!visited.contains(parent.getTypeInfo())) {
        visited.add(parent.getTypeInfo());
        extensions.add(new ComponentExtension(next, parent.getTypeInfo(), null));
        this.getComponentExtensionList(root, parent.getTypeInfo(), visited, extensions);
      }

    }
    return extensions;
  }

  @Override
  public void check(@NotNull ASTArcComponentType node) {
    ASTArcComponentType currentNode = node instanceof ASTVariableArcFullVariantComponentType ? ((ASTVariableArcFullVariantComponentType) node).getOriginal() : node;
    if (alreadyCheckedComponents.contains(currentNode))
      return;
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());

    ExpressionSolver expSolver = ExpressionSolverService.getExpressionSolver();
    Context ctx = ExpressionSolverService.getContext();

    Map<ASTArcComponentType, BoolExpr> componentConditions = new HashMap<>();

    List<String> allFeatures;
    List<ExpressionSet> allConstraints;

    // Reading and processing parts of the Main-Component
    ArrayList<String> mainFeatures = (ArrayList<String>) node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcFeatureDeclaration).map(v -> ((ASTArcFeatureDeclaration) v)).map(ASTArcFeatureDeclaration::getArcFeatureList).flatMap(List::stream).map(e -> node.getSymbol().getFullName() + "." + e.getSymbol().getName()).collect(Collectors.toList());

    // Getting alls features, variations and constraints from the Main-Component
    allFeatures = new ArrayList<>(mainFeatures);
    allConstraints = new ArrayList<>();
    List<ComponentExtension> currentComponentExtensions = new  ArrayList<>();

    if(node instanceof ASTVariableArcFullVariantComponentType){

      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) ((ASTVariableArcFullVariantComponentType) node).getOriginal().getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      componentConditions = ((ASTVariableArcFullVariantComponentType) node).getComponentConditions();

      for (Map.Entry<ASTArcComponentType, BoolExpr> componentEntry : componentConditions.entrySet()) {
        var componentExtensions = getComponentExtensionList(componentEntry.getKey(), componentEntry.getKey().getSymbol(), new ArrayList<>(), new ArrayList<>());
        for (ComponentExtension ext : componentExtensions) {
          var extSource = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(ext.source.getAstNode());
          var extTarget = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(ext.target.getAstNode());
          if (currentComponentExtensions.stream().anyMatch(e -> (e.source.equals(extSource.getSymbol()) && (e.target.equals(extTarget.getSymbol())))))
            continue;
          currentComponentExtensions.add(ext);
        }
      }

    }else{
      ExpressionSet mainConstraintSet = ((IVariableArcComponentTypeSymbol) node.getSymbol()).getConstraints();
      allConstraints.add(mainConstraintSet);

      componentConditions.put(node,ctx.mkTrue());
      currentComponentExtensions = getComponentExtensionList(node, node.getSymbol(), new ArrayList<>(), new ArrayList<>());

      var componentsInBody = node.getBody().getArcElementList().stream().filter(e -> e instanceof ASTArcComponentType).map(l -> (ASTArcComponentType) l).collect(Collectors.toList());
      for (ASTArcComponentType component : componentsInBody) {
        componentConditions.put(component,ctx.mkTrue());
        var componentExtensions = getComponentExtensionList(component, component.getSymbol(), new ArrayList<>(), new ArrayList<>());
        for (ComponentExtension ext : componentExtensions) {
          var extSource = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(ext.source.getAstNode());
          var extTarget = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(ext.target.getAstNode());
          if (currentComponentExtensions.stream().anyMatch(e -> (e.source.equals(extSource.getSymbol()) && (e.target.equals(extTarget.getSymbol())))))
            continue;
          currentComponentExtensions.add(ext);
        }
      }

      for (ComponentExtension ext : currentComponentExtensions) {
        ext.setCondition(ctx.mkTrue());
      }
    }

    // Adding Constraints
    BoolExpr featureConstraints = VariationConditionHelper.getFeatureConstraints(node, allConstraints, allFeatures, expSolver);

    // Setting the conditions for each extension
    for (ComponentExtension ext : currentComponentExtensions) {
      var extSource = VariableArcMill.typeDispatcher().asArcBasisASTArcComponentType(ext.source.getAstNode());
      if (componentConditions.containsKey(extSource)) {
        ext.condition = componentConditions.get(extSource);
      } else {
        ext.condition = ctx.mkTrue();
      }
    }

    // Manages the component symbols
    List<ComponentTypeSymbol> components = currentComponentExtensions.stream()
      .flatMap(e -> Stream.of(e.source, e.target))
      .distinct()
      .collect(Collectors.toList());

    int componentCount = components.size();

    // Mapping the symbols of components to indices
    Map<ComponentTypeSymbol, Integer> compIndex = new HashMap<>();
    int compId = 0;
    for (ComponentTypeSymbol component : components) {
      compIndex.put(component, compId++);
    }

    // Initializing the direct extension matrix
    BoolExpr[][] extendsDirectly = new BoolExpr[componentCount][componentCount];
    for (int i = 0; i < componentCount; i++)
      for (int j = 0; j < componentCount; j++)
        extendsDirectly[i][j] = ctx.mkFalse();

    // Filling the direct-extension-Matrix
    for (ComponentExtension ext : currentComponentExtensions) {
      extendsDirectly[compIndex.get(ext.source)][compIndex.get(ext.target)] = ctx.mkEq(ctx.mkBoolConst(ext.source.getName() + "_extend_" + ext.target.getName()), ext.condition);
    }

    // Compute transitive closure
    BoolExpr[][] reachable = constructTransitiveClosure(ctx, extendsDirectly);

    for (ASTArcComponentType componentType : componentConditions.keySet()) {

      int index = components.indexOf(componentType.getSymbol());
      if (index == -1) continue;

      ComponentTypeSymbol comp = components.get(index);
      BoolExpr selfDirectExtension = extendsDirectly[index][index];
      BoolExpr selfTransitiveExtension = reachable[index][index];
      Set<BoolExpr> selfExtVars = extractExtends(selfDirectExtension);
      Set<BoolExpr> transitiveExtVars = extractExtends(selfTransitiveExtension);
      transitiveExtVars.removeIf(e -> selfExtVars.stream().anyMatch(r -> e.getFuncDecl().equals(r.getFuncDecl())));

      List<BoolExpr> expressionList_DirectExtension = new ArrayList<>();

      expressionList_DirectExtension.add(ctx.mkAnd(selfDirectExtension, featureConstraints));
      for (BoolExpr selfVar : selfExtVars)
          expressionList_DirectExtension.add(ctx.mkEq(selfVar, ctx.mkTrue()));
      // Checking for direct extension
      if (ExpressionSolverService.solve(expressionList_DirectExtension) == Status.SATISFIABLE) {
        Log.error(ArcError.CIRCULAR_INHERITANCE.format(comp.getName()),
          comp.getAstNode().get_SourcePositionStart(), comp.getAstNode().get_SourcePositionEnd());
      }

      // Checking for transitive extension
      List<BoolExpr> expressionList_TransitiveExtension = new ArrayList<>(List.of(selfTransitiveExtension, ctx.mkNot(selfDirectExtension), featureConstraints));

      if (!transitiveExtVars.isEmpty()) {
        BoolExpr transitiveVariable = ctx.mkBoolConst("transitive");
        BoolExpr transitiveBody = ctx.mkTrue();
        for (BoolExpr extendVar : transitiveExtVars)
          transitiveBody = ctx.mkAnd(extendVar, transitiveBody);
        expressionList_TransitiveExtension.addAll(List.of(ctx.mkEq(transitiveVariable, transitiveBody),transitiveVariable));
      }

      if (ExpressionSolverService.solve(expressionList_TransitiveExtension) == Status.SATISFIABLE) {
        Log.error(ArcError.CIRCULAR_INHERITANCE.format(comp.getName()),
          comp.getAstNode().get_SourcePositionStart(), comp.getAstNode().get_SourcePositionEnd());
      }
    }
    alreadyCheckedComponents.addAll(componentConditions.keySet());
  }
}

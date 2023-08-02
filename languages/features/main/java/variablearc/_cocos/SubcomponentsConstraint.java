/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentInstanceSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import de.se_rwth.commons.logging.Log;
import montiarc.util.VariableArcError;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc.evaluation.ExpressionSolver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks if a component's subcomponents' features are bound correctly to a discrete value.
 * This is accomplished by building a new SMT formula based on the components constraints:
 * 1. Duplicate all constraints and substitute all subcomponents' features by a new variable (x.f -> x.f__dup__)
 * 2. Add constraint that at least one feature of the new feature has to be different from its original feature (x.f != x.f__dup__ || ...)
 * 3. If the new formula has a solution at least one subcomponents' feature can have multiple values independent of the component's feature assignment -> Error
 */
public class SubcomponentsConstraint implements ArcBasisASTComponentTypeCoCo {

  @Override
  public void check(@NotNull ASTComponentType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkArgument(node.isPresentSymbol());
    if (!(node.getSymbol() instanceof VariableComponentTypeSymbol)) return;

    VariableComponentTypeSymbol componentTypeSymbol = (VariableComponentTypeSymbol) node.getSymbol();

    ExpressionSolver solver = new ExpressionSolver();
    Optional<Expr<BoolSort>> eval = solver.convert(componentTypeSymbol.getConditions())
      .map(boolExprs -> solver.getContext().mkAnd(boolExprs).substitute(
        componentTypeSymbol.getSubComponents().stream()
          .flatMap(instance -> getFeaturesOfInstance(instance).stream()
            .map(feature -> solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName()))
          ).toArray(BoolExpr[]::new),
        componentTypeSymbol.getSubComponents().stream()
          .flatMap(instance -> getFeaturesOfInstance(instance).stream()
            .map(feature -> solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName() + "__dup__"))
          ).toArray(BoolExpr[]::new)
      ));

    Optional<Solver> smtSolver = solver.getSolver(componentTypeSymbol.getConditions());
    if (smtSolver.isPresent() && eval.isPresent()) {
      smtSolver.get().add(eval.get());
      smtSolver.get().add(
        solver.getContext().mkOr(
          componentTypeSymbol.getSubComponents().stream()
            .flatMap(instance -> getFeaturesOfInstance(instance).stream().map(feature -> solver.getContext().mkNot(
                solver.getContext().mkEq(solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName()),
                  solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName() + "__dup__"))
              ))
            ).toArray(BoolExpr[]::new)
        )
      );
      boolean result = smtSolver.get().check() == Status.SATISFIABLE;

      if (result) {
        // Collect which features caused this error (the list might not be complete)
        Model model = smtSolver.get().getModel();
        List<String> causingFeatures = componentTypeSymbol.getSubComponents().stream()
          .flatMap(instance -> getFeaturesOfInstance(instance).stream().filter(feature ->
            model.getConstInterp(solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName())).getBoolValue()
              != model.getConstInterp(solver.getContext().mkBoolConst(instance.getName() + "." + feature.getName() + "__dup__")).getBoolValue()
          ).map(feature -> instance.getName() + "." + feature.getName())).collect(Collectors.toList());

        Log.error(VariableArcError.SUBCOMPONENTS_NOT_CONSTRAINT.format(causingFeatures.toString()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      }
    } else {
      Log.debug(String.format(
        "'%s' Skipping constraint evaluation for '%s', could not calculate expression.",
        node.get_SourcePositionStart(), this.getClass().getSimpleName()), "SubcomponentsConstraint");
    }
    solver.close();
  }

  protected List<ArcFeatureSymbol> getFeaturesOfInstance(@NotNull ComponentInstanceSymbol instance) {
    Preconditions.checkNotNull(instance);

    if (instance.isPresentType()) {
      return ((IVariableArcScope) instance.getType().getTypeInfo().getSpannedScope())
        .getLocalArcFeatureSymbols();
    }
    return Collections.emptyList();
  }
}

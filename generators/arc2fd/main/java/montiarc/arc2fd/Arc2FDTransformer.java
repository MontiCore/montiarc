/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import de.monticore.featurediagram._ast.ASTFDCompilationUnit;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.arc2fd.solver.ConstraintSolver;
import montiarc.arc2fd.solver.SolverResult;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Transforms a MontiArc component into a feature diagram: collects its
 * features and constraints ({@link FeatureCollector}), analyzes them
 * ({@link ConstraintSolver}), and builds the AST
 * ({@link FeatureDiagramBuilder}). Reports all findings to the {@link Log}.
 */
public class Arc2FDTransformer {

  protected final FeatureCollector collector;
  protected final FeatureDiagramBuilder builder;

  /**
   * @param recursive Whether to include the features of subcomponents
   */
  public Arc2FDTransformer(boolean recursive) {
    this(new FeatureCollector(recursive), new FeatureDiagramBuilder());
  }

  public Arc2FDTransformer(@NotNull FeatureCollector collector, @NotNull FeatureDiagramBuilder builder) {
    this.collector = Preconditions.checkNotNull(collector);
    this.builder = Preconditions.checkNotNull(builder);
  }

  /**
   * @param ast AST of the MontiArc component, with symbol table
   * @return The feature diagram, iff no error occurred and the component contains at least one feature
   */
  public Optional<ASTFDCompilationUnit> transform(@NotNull ASTMACompilationUnit ast) {
    Preconditions.checkNotNull(ast);

    IVariableArcComponentTypeSymbol component =
      (IVariableArcComponentTypeSymbol) ast.getArcComponentType().getSymbol();
    String name = component.getTypeInfo().getName();

    Optional<Map<String, String>> features = collector.collectFeatures(component);
    if (features.isEmpty())
      return Optional.empty();

    if (features.get().isEmpty()) {
      Log.info(Arc2FDError.EMPTY_COMPONENT.format(name), getClass().getSimpleName());
      return Optional.empty();
    }

    if (features.get().containsKey(name)) {
      Log.error(Arc2FDError.RECURSIVE_COMPONENT.format(name));
      return Optional.empty();
    }

    List<String> pkg = ast.isPresentPackage() ? ast.getPackage().getPartsList() : List.of();

    try (Context context = new Context()) {
      Optional<List<BoolExpr>> constraints = collector.collectConstraints(component, context);
      if (constraints.isEmpty()) {
        Log.error(Arc2FDError.SMT_CONVERSION_ERROR.format(name));
        return Optional.empty();
      }

      Solver solver = context.mkSolver();
      solver.add(constraints.get().toArray(BoolExpr[]::new));

      Map<String, BoolExpr> vars = new LinkedHashMap<>();
      features.get().forEach((fdName, smtName) -> vars.put(fdName, context.mkBoolConst(smtName)));

      SolverResult result = new ConstraintSolver(context, solver, name, vars).trySolve();

      if (result instanceof SolverResult.Unsatisfiable) {
        Log.error(Arc2FDError.SMT_UNSAT.format(name));
        return Optional.empty();
      }
      if (!(result instanceof SolverResult.Solved solved)) {
        Log.error(Arc2FDError.SMT_UNKNOWN.format(name));
        return Optional.empty();
      }

      solved.deadFeatures().forEach(f -> Log.warn(Arc2FDError.DEAD_FEATURE.format(f, name)));
      if (solved.incomplete())
        Log.warn(Arc2FDError.SMT_STRICTNESS.format(name));

      return Optional.of(builder.build(solved.fd(), pkg));
    }
  }
}

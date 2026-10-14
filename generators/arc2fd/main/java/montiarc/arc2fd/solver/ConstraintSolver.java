/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.solver;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import montiarc.arc2fd.model.AndFeature;
import montiarc.arc2fd.model.FDData;
import montiarc.arc2fd.model.FeatureGroup;
import montiarc.arc2fd.model.FeaturePair;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <code>ConstraintSolver</code> analyzes the constraints over the features of
 * a MontiArc component and, if possible, computes a feature diagram that
 * corresponds to them.
 * <p>
 * The analysis is a sequence of steps, each of which takes the results of the
 * previous steps and returns its own result without modifying its inputs.
 * The analysis does not log; all findings are part of the {@link SolverResult}.
 * </p>
 */
public class ConstraintSolver {

  protected final Context context;
  protected final Solver solver;
  protected final String root;
  protected final Map<String, BoolExpr> vars;

  /**
   * Set if a solver query returned <code>UNKNOWN</code> during the current run of {@link #trySolve()}.
   */
  protected boolean incomplete = false;

  /**
   * @param context Context of <code>solver</code> and <code>vars</code>
   * @param solver  Solver that asserts the constraints of the component
   * @param root    Name of the root feature, i.e., the component
   * @param vars    Mapping from feature names to their SMT variables
   */
  public ConstraintSolver(@NotNull Context context, @NotNull Solver solver,
                          @NotNull String root, @NotNull Map<String, BoolExpr> vars) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(solver);
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(vars);

    this.context = context;
    this.solver = solver;
    this.root = root;
    this.vars = vars;
  }

  /**
   * @return The result of the analysis, containing the feature diagram iff the
   * constraints are satisfiable
   */
  public SolverResult trySolve() {
    incomplete = false;

    Status status = solver.check();
    if (status == Status.UNSATISFIABLE)
      return new SolverResult.Unsatisfiable();
    if (status == Status.UNKNOWN)
      return new SolverResult.Unknown();

    Classification features = classify();
    Map<String, List<String>> depends = dependencies(features.optional());
    Equivalences equivalences = equivalences(features.optional(), depends);
    Map<String, List<String>> direct = directImplications(equivalences.reps(), depends);
    Hierarchy hierarchy = hierarchy(equivalences.reps(), direct);
    Grouping grouping = findGroups(hierarchy.children(), equivalences.members());
    List<FeaturePair> excludes = exclusions(equivalences.reps(), features.dead(), grouping.groups());

    FDData fd = new FDData(root,
      andGroups(features.required(), features.dead(), grouping.requiredChildren(), grouping.optionalChildren()),
      grouping.groups(), hierarchy.requires(), excludes);

    return new SolverResult.Solved(fd, features.dead(), incomplete);
  }

  /* ---------------------------------------------------------------------- */
  /* Intermediate results                                                   */
  /* ---------------------------------------------------------------------- */

  /**
   * @param required Features that must be enabled
   * @param dead     Features that must be disabled
   * @param optional Features that may be either enabled or disabled
   */
  protected record Classification(List<String> required, List<String> dead, List<String> optional) { }

  /**
   * @param reps    Representatives of the equivalence classes
   * @param members Mapping from a representative to its (other) equivalent features
   */
  protected record Equivalences(List<String> reps, Map<String, List<String>> members) { }

  /**
   * @param children Mapping from a feature (or the root) to its children
   * @param requires Cross-tree requires relations of features with more than one direct dependency
   */
  protected record Hierarchy(Map<String, List<String>> children, List<FeaturePair> requires) { }

  /**
   * @param groups           Or- and xor-groups
   * @param optionalChildren Mapping from a feature to its children that are in no group and optional
   * @param requiredChildren Mapping from a feature to its children that are in no group and mandatory
   */
  protected record Grouping(List<FeatureGroup> groups,
                            Map<String, List<String>> optionalChildren,
                            Map<String, List<String>> requiredChildren) { }

  /* ---------------------------------------------------------------------- */
  /* Analysis steps                                                         */
  /* ---------------------------------------------------------------------- */

  /**
   * Splits the features into required, dead and optional features.
   */
  protected Classification classify() {
    List<String> required = new ArrayList<>();
    List<String> dead = new ArrayList<>();
    List<String> optional = new ArrayList<>();

    for (String s : vars.keySet()) {
      if (unsatisfiable(getNegatedExpr(s)))
        required.add(s);
      else if (unsatisfiable(getExpr(s)))
        dead.add(s);
      else
        optional.add(s);
    }

    return new Classification(required, dead, optional);
  }

  /**
   * Creates a mapping from the name of a feature to all features that must be
   * enabled for it to be enabled.
   *
   * @param optionalFeatures <code>List</code> of features that may be enabled or disabled
   * @return <code>Map</code> of features to their dependencies
   */
  protected Map<String, List<String>> dependencies(@NotNull List<String> optionalFeatures) {
    Preconditions.checkNotNull(optionalFeatures);

    Map<String, List<String>> ret = new LinkedHashMap<>();

    for (String s1 : optionalFeatures) {
      ret.put(s1, new ArrayList<>());
      for (String s2 : optionalFeatures) {
        if (!s1.equals(s2) && unsatisfiable(getExpr(s1), getNegatedExpr(s2)))
          ret.get(s1).add(s2);
      }
    }

    return ret;
  }

  /**
   * Computes all features that are equivalent, i.e., that must either be all
   * enabled or all disabled, and chooses a representative for each class.
   *
   * @param optionalFeatures <code>List</code> of features that may be enabled or disabled
   * @param dependencies     <code>Map</code> of features to their dependencies
   * @return The representatives and the members of their classes
   */
  protected Equivalences equivalences(@NotNull List<String> optionalFeatures,
                                      @NotNull Map<String, List<String>> dependencies) {
    Preconditions.checkNotNull(optionalFeatures);
    Preconditions.checkNotNull(dependencies);

    List<String> reps = new ArrayList<>();
    Map<String, List<String>> members = new LinkedHashMap<>();

    for (String s : optionalFeatures) {
      Optional<String> rep = reps.stream()
        .filter(r -> dependencies.get(s).contains(r) && dependencies.get(r).contains(s))
        .findFirst();

      if (rep.isPresent())
        members.computeIfAbsent(rep.get(), k -> new ArrayList<>()).add(s);
      else
        reps.add(s);
    }

    return new Equivalences(reps, members);
  }

  /**
   * Computes a <code>Map</code> from features to their direct dependencies, rather
   * than transitively, taking into account equivalent features.
   *
   * @param reps         <code>List</code> of representatives of equivalence classes
   * @param dependencies <code>Map</code> of features and their dependencies
   * @return <code>Map</code> of features to their direct dependencies
   */
  protected Map<String, List<String>> directImplications(@NotNull List<String> reps,
                                                         @NotNull Map<String, List<String>> dependencies) {
    Preconditions.checkNotNull(reps);
    Preconditions.checkNotNull(dependencies);

    Map<String, List<String>> ret = new LinkedHashMap<>();

    for (String s : reps) {
      List<String> succs = reps.stream().filter(dependencies.get(s)::contains).toList();

      ret.put(s, succs.stream()
        .filter(b -> succs.stream()
          .noneMatch(c -> !c.equals(b) && dependencies.get(c).contains(b)))
        .toList());
    }

    return ret;
  }

  /**
   * Builds the feature hierarchy from the direct dependencies.
   * <p>
   * If a feature only directly depends on one other feature, we can simply
   * make it its dependent's child. Otherwise, it is added as a child to the
   * root feature and its dependencies are recorded as cross-tree requires
   * relations.
   * </p>
   *
   * @param reps               <code>List</code> of representatives of equivalence classes
   * @param directImplications <code>Map</code> of features to their direct dependencies
   * @return The children of each feature and the cross-tree requires relations
   */
  protected Hierarchy hierarchy(@NotNull List<String> reps,
                                @NotNull Map<String, List<String>> directImplications) {
    Preconditions.checkNotNull(reps);
    Preconditions.checkNotNull(directImplications);

    Map<String, List<String>> children = new LinkedHashMap<>();
    List<FeaturePair> requires = new ArrayList<>();

    children.put(root, new ArrayList<>());
    reps.forEach(r -> children.put(r, new ArrayList<>()));

    for (String s : reps) {
      List<String> direct = directImplications.get(s);
      if (direct.size() == 1) {
        children.get(direct.getFirst()).add(s);
      } else {
        children.get(root).add(s);
        direct.forEach(b -> requires.add(new FeaturePair(s, b)));
      }
    }

    return new Hierarchy(children, requires);
  }

  /**
   * Computes the or- and xor-groups of the feature diagram. A "group" of a
   * single feature is a mandatory child. All other children remain optional.
   *
   * @param children         <code>Map</code> of features to their children
   * @param requiredChildren <code>Map</code> of features to their children which
   *                         must be taken along with its parent
   * @return The groups and the remaining optional and mandatory children
   */
  protected Grouping findGroups(@NotNull Map<String, List<String>> children,
                                @NotNull Map<String, List<String>> requiredChildren) {
    Preconditions.checkNotNull(children);
    Preconditions.checkNotNull(requiredChildren);

    List<FeatureGroup> groups = new ArrayList<>();
    Map<String, List<String>> optional = new LinkedHashMap<>();
    Map<String, List<String>> required = new LinkedHashMap<>();
    requiredChildren.forEach((parent, features) -> required.put(parent, new ArrayList<>(features)));

    for (String parent : children.keySet()) {
      List<String> features = new ArrayList<>(children.get(parent));
      BoolExpr[] constraints = parent.equals(root)
        ? new BoolExpr[0]
        : new BoolExpr[]{getExpr(parent)};

      while (features.size() >= 2 && unsatisfiable(negateAll(constraints, features))) {
        List<String> group = shrink(constraints, features);

        features.removeAll(group);
        if (group.size() < 2)
          required.computeIfAbsent(parent, k -> new ArrayList<>()).addAll(group);
        else
          groups.add(new FeatureGroup(parent, group, isXorGroup(constraints, group)));
      }

      optional.put(parent, features);
    }

    return new Grouping(groups, optional, required);
  }

  /**
   * Computes the mutually exclusive pairs of features that are not already
   * implied by a shared xor-group. Dead features are excluded with the root.
   *
   * @param reps   <code>List</code> of representatives of equivalence classes
   * @param dead   <code>List</code> of dead features
   * @param groups Feature groups computed by {@link #findGroups}
   * @return <code>List</code> of mutually exclusive feature pairs
   */
  protected List<FeaturePair> exclusions(@NotNull List<String> reps,
                                         @NotNull List<String> dead,
                                         @NotNull List<FeatureGroup> groups) {
    Preconditions.checkNotNull(reps);
    Preconditions.checkNotNull(dead);
    Preconditions.checkNotNull(groups);

    List<FeaturePair> ret = new ArrayList<>();

    for (int i = 0; i < reps.size(); i++) {
      for (int j = i + 1; j < reps.size(); j++) {
        String s1 = reps.get(i);
        String s2 = reps.get(j);
        boolean sameXorGroup = groups.stream().anyMatch(g ->
          g.xor() && g.features().contains(s1) && g.features().contains(s2));

        if (!sameXorGroup && unsatisfiable(getExpr(s1), getExpr(s2)))
          ret.add(new FeaturePair(s1, s2));
      }
    }

    dead.forEach(d -> ret.add(new FeaturePair(d, root)));

    return ret;
  }

  /**
   * Computes the and-groups for the features. The <code>required</code> and
   * <code>dead</code> features are added only as children to the <code>root</code>.
   * <p>
   * The features in <code>requiredChildren</code> and <code>optionalChildren</code>
   * are added as non-optional and optional features to their parent's and-group,
   * respectively.
   * </p>
   *
   * @param required         <code>List</code> of required features
   * @param dead             <code>List</code> of dead features
   * @param requiredChildren <code>Map</code> from features to their required children
   * @param optionalChildren <code>Map</code> from features to their optional children
   * @return <code>Map</code> from features to their and-group
   */
  protected Map<String, List<AndFeature>> andGroups(@NotNull List<String> required,
                                                    @NotNull List<String> dead,
                                                    @NotNull Map<String, List<String>> requiredChildren,
                                                    @NotNull Map<String, List<String>> optionalChildren) {
    Preconditions.checkNotNull(required);
    Preconditions.checkNotNull(dead);
    Preconditions.checkNotNull(requiredChildren);
    Preconditions.checkNotNull(optionalChildren);

    Map<String, List<AndFeature>> andGroups = new LinkedHashMap<>();

    for (String parent : optionalChildren.keySet()) {
      List<AndFeature> features = new ArrayList<>();

      if (parent.equals(root))
        required.forEach(f -> features.add(new AndFeature(f, false)));

      requiredChildren.getOrDefault(parent, List.of()).forEach(f -> features.add(new AndFeature(f, false)));

      optionalChildren.get(parent).forEach(f -> features.add(new AndFeature(f, true)));

      if (parent.equals(root))
        dead.forEach(f -> features.add(new AndFeature(f, true)));

      andGroups.put(parent, features);
    }

    return andGroups;
  }

  /* ---------------------------------------------------------------------- */
  /* Helpers                                                                */
  /* ---------------------------------------------------------------------- */

  /**
   * Shrinks a list of features to the absolutely required ones, given an array
   * of constraints which must hold.
   *
   * @param constraints Constraints which must hold
   * @param features    List of features
   * @return List of features which are required
   */
  protected List<String> shrink(@NotNull BoolExpr[] constraints,
                                @NotNull List<String> features) {
    Preconditions.checkNotNull(constraints);
    Preconditions.checkNotNull(features);

    List<String> group = new ArrayList<>(features);

    for (String s : features) {
      if (group.size() > 1) {
        List<String> smaller = new ArrayList<>(group);

        smaller.remove(s);
        if (unsatisfiable(negateAll(constraints, smaller)))
          // if the constraints cannot be satisfied without the features in
          // smaller, at least some are required
          group = smaller;
      }
    }
    return group;
  }

  /**
   * Checks whether the <code>BoolExpr</code>s of the features in <code>group</code>
   * are pair-wise mutually exclusive given an array of constraints.
   *
   * @param constraints Constraints which must hold
   * @param group       List of features to check
   * @return true, iff group is pair-wise mutually exclusive
   */
  protected boolean isXorGroup(@NotNull BoolExpr[] constraints,
                               @NotNull List<String> group) {
    Preconditions.checkNotNull(constraints);
    Preconditions.checkNotNull(group);

    for (int i = 0; i < group.size(); i++) {
      for (int j = i + 1; j < group.size(); j++) {
        if (!unsatisfiable(concat(constraints, Stream.of(getExpr(group.get(i)), getExpr(group.get(j))))))
          return false;
      }
    }
    return true;
  }

  /**
   * Creates a new array, consisting of <code>constraints</code> and the
   * negated <code>BoolExpr</code>s associated with a list of features.
   *
   * @param constraints Existing constraints
   * @param features    Additional features
   * @return Array with constraints and negated expressions
   */
  protected BoolExpr[] negateAll(@NotNull BoolExpr[] constraints,
                                 @NotNull List<String> features) {
    Preconditions.checkNotNull(constraints);
    Preconditions.checkNotNull(features);

    return concat(constraints, features.stream().map(this::getNegatedExpr));
  }

  protected static BoolExpr[] concat(@NotNull BoolExpr[] constraints, @NotNull Stream<BoolExpr> more) {
    return Stream.concat(Stream.of(constraints), more).toArray(BoolExpr[]::new);
  }

  /**
   * Checks whether a collection of <code>BoolExpr</code>s is unsatisfiable.
   * Additionally, sets <code>incomplete</code>to <code>true</code>, if the solver
   * returns <code>Status.UNKNOWN</code>.
   *
   * @param constraints Collection of constraints
   * @return true, iff the constraints are unsatisfiable
   */
  protected boolean unsatisfiable(BoolExpr... constraints) {
    Status status = solver.check(constraints);
    if (status == Status.UNKNOWN)
      this.incomplete = true;
    return status == Status.UNSATISFIABLE;
  }

  protected BoolExpr getExpr(@NotNull String feature) {
    return vars.get(feature);
  }

  protected BoolExpr getNegatedExpr(@NotNull String feature) {
    return context.mkNot(getExpr(feature));
  }
}

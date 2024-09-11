/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import arcbasis._ast.ASTArcElement;
import arcbasis._ast.ASTComponentBody;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpressionBuilder;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.arc2fd.smt.FDRelation;
import org.codehaus.commons.nullanalysis.NotNull;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.java_smt.api.BooleanFormulaManager;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.FormulaManager;
import variablearc._ast.ASTArcFeature;
import variablearc._ast.ASTArcVarIf;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MAExtractionHelper<T extends Formula> {

  /**
   * Final Expression which is always true (might be required if we evaluate
   * if-else statements)
   */
  public static final ASTLiteralExpression ALWAYS_TRUE_EXPRESSION =
    MontiArcMill.literalExpressionBuilder()
      .setLiteral(MontiArcMill.booleanLiteralBuilder()
        .setSource(ASTConstantsMCCommonLiterals.TRUE).build()).build();

  /**
   * Formula Manager (Java-SMT)
   */
  private final FormulaManager fmgr;

  /**
   * Boolean Formula Manager (Java-SMT)
   */
  private final BooleanFormulaManager bmgr;

  /**
   * Stores a Regex which is created in the Constructor. The purpose of it
   * is, that it combines all possible
   * Splitters, Delimiters, Separators, etc... that we used during our
   * Formula-Construction.
   * Then we can split each formula based on this Regex String to get a
   * complete list of used features.
   * From this, we can compute the difference between the original features
   * and the used features
   * and can thus determine the features that must be added as optionals.
   * Details of the application can be found in
   * {@link MAExtractionHelper#addOptionals}
   */
  private final String OPTIONALS_SPLITTING_REGEX =
    FDConfiguration.GET_SPLITTING_REGEX();

  /**
   * Store the ConvertHelper which is doing the acutal work of converting an
   * AST into an FD Storage
   */
  ExpressionToFDHelper<T> convertHelper;

  /**
   * Keep track of the total expression of an AST (to determine whether the
   * constraints & features are
   * satisfiable or not)
   */
  Set<ASTExpression> totalASTExpressions = new HashSet<>();

  /**
   * Stores a List of all ASTArcFeatures to recognize Conflicts (i.e.,
   * multiple use of the same formula
   * across subcomponents).
   */
  Set<String> featureList = new HashSet<>();

  /**
   * Stores a Map which maps ASTExpressions (as Strings) to different
   * ASTExpressions (as Strings).
   * The map then gets passed onto the corresponding processing steps and
   * will replace all occurrences
   * of (keys) by the corresponding (values).
   */
  Map<String, String> variableRemapping = new HashMap<>();

  /**
   * Initialize the Extraction Helper
   */
  public MAExtractionHelper() {
    // Initialize convertHelper...
    try {
      convertHelper = new ExpressionToFDHelper<>();
      // Extract Formula Managers from the Expression Helper (makes working
      // easier)
      this.fmgr = convertHelper.fmgr;
      this.bmgr = convertHelper.bmgr;
    } catch (
      InvalidConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Tries to convert all ARC-Files in the given file path and returns a list
   * of all results.
   *
   * @param path File Path from which we want to process all ".arc"-files
   * @return (Optional) List of all resulting storages, where each list
   * elements corresponds to one processed file
   */
  @SuppressWarnings("unchecked")
  public Optional<List<AST2FDResult<T>>> processASTsFromFile(@NotNull Path path) {
    // Get all the ASTs and Create Symbol Table
    MontiArcTool tool = new MontiArcTool();
    tool.init();
    Collection<ASTMACompilationUnit> asts;

    // Parse the file / all files in the directory depending on the case
    if (path.toFile().isFile()) {
      Optional<ASTMACompilationUnit> o = tool.parse(path);
      if (o.isPresent())
        asts = Collections.singleton(o.get());
      else
        asts = new ArrayList<>();
    } else {
      asts = tool.parse("arc", path);
    }

    tool.runAfterParsingTrafos(asts);
    tool.createSymbolTable(asts);
    tool.runSymbolTablePhase2(asts);
    tool.runSymbolTablePhase3(asts);
    tool.runAfterSymbolTablePhase3Trafos(asts);

    // Now we can go through each AST and process it individually
    if (asts.isEmpty()) {
      // Return warning if we didn't find any AST
      Log.warn(FDConstructionError.NO_ASTS_FOUND.format(path));
      return Optional.empty();
    }

    // Store the results
    List<AST2FDResult<T>> results = new ArrayList<>();

    asts.forEach(a -> {
      // Reset Values for each AST (to start fresh)
      totalASTExpressions = new HashSet<>();
      this.variableRemapping = new HashMap<>();
      this.featureList = new HashSet<>();

      // Process the AST
      ASTComponentType comp = a.getComponentType();
      Log.println("[Arc2FD]: Process Model \"" + comp.getName() + "\"");
      StorageCache<T> finalExp = processASTComponentType(comp);

      // Build the String-Representation (from current component name as root)
      finalExp.getStorage().buildStringRepresentation((T) bmgr.makeVariable(comp.getName()));

      // Add the storage to the list of final results (so that we can then
      // call "generateTemplate"...)
      results.add(new AST2FDResult<>(a, finalExp.getStorage()));
    });

    return Optional.of(results);
  }

  /**
   * Process a single AST ComponentType and returns the resulting storage
   *
   * @param comp Component to process
   * @return Storage which was constructed from the given ASTComponent
   */
  @SuppressWarnings("unchecked")
  private StorageCache<T> processASTComponentType(ASTComponentType comp) {
    return processArcElementList(MAStreamHelper.getArcElements(comp),
      (T) bmgr.makeVariable(comp.getName()));
  }

  /**
   * Process a single AST ComponentType and returns the resulting storage
   * (with given root name)
   *
   * @param comp     Component to process
   * @param rootName Name of the Root of the component (useful if we have
   *                 multiple instances)
   * @return Storage which was constructed from the given ASTComponent
   */
  private StorageCache<T> processASTComponentType(ASTComponentType comp,
                                                  T rootName) {
    return processArcElementList(MAStreamHelper.getArcElements(comp), rootName);
  }

  /**
   * Processes a List of ArcElements by extracting the features, constraints,
   * if-statements, ... and recursively
   * visiting all subcomponents. Afterwards, we combine the discovered
   * results, relations and formulas so
   * that we can return one single storage cache which contains all
   * information extracted during traversal.
   *
   * @param elements List of ArcElements which should be inspected, analyzed
   *                 and transformed
   * @param root     Root Element / Origin where the elements come from
   * @return Constructed storage which contains all the relations extracted
   * from the elements.
   */
  private StorageCache<T> processArcElementList(@NotNull List<ASTArcElement> elements, T root) {
    StorageCache<T> storageCache = new StorageCache<>();

    // Step 1: Get all necessary information...
    List<ASTArcFeature> features =
      MAStreamHelper.getArcFeatureDeclarationsFromElements(elements);
    List<ASTExpression> constraints =
      MAStreamHelper.getConstraintExpressionsFromArcElements(elements);
    List<ASTArcVarIf> varif =
      MAStreamHelper.getVarIfsFromArcElements(elements);
    List<ASTComponentType> innerComponents =
      MAStreamHelper.getInnerComponentsFromArcElements(elements);
    List<ASTComponentInstance> componentInstances =
      MAStreamHelper.getComponentInstancesFromArcElements(elements);

    // Step 2: Check for duplicated/conflicting features and output a warning
    // in case of duplicates
    addFeaturesAndCheckForDuplicates(features);

    // Step 3: Merge all Constraints into one single conjunction of all
    // constraints.
    ASTExpression combinedExpression =
      wrapInBrackets(mergeExpressions(constraints));

    // Step 4: Process If-Statements (new function here!) and add the found
    // expressions to the list to merge
    if (varif.size() > 0) {
      handleVarIfs(varif, root, storageCache);
      combinedExpression = mergeExpressions(List.of(combinedExpression,
        storageCache.getTotalExpression()));
    }

    // Step 5: Process InnerComponents
    if (innerComponents.size() > 0)
      processInnerComponents(innerComponents, root, storageCache);

    // Step 6: Process ComponentInstances
    if (componentInstances.size() > 0)
      processComponentInstances(componentInstances, root, storageCache);

    // Step 7: Finally create FD-Representation from ASTExpression
    try {
      // Finally, convert the created ASTExpression into a Feature Diagram
      // Representation
      Optional<FDConstructionStorage<T>> tmpStorage =
        convertHelper.convertASTExpressionToFD(
          combinedExpression, root, variableRemapping);

      // Ensure that our storage is present
      if (tmpStorage.isEmpty()) {
        Log.warn(FDConstructionError.STORAGE_EMPTY.format());
        return storageCache;
      }

      // Add the result to our storage
      storageCache.setTotalExpression(combinedExpression);
      storageCache.mergeWithStorage(tmpStorage.get());

      // If we're finally at the end, we can try to add all optional atoms...
      addOptionals(features, root, storageCache);
    } catch (
      InterruptedException e) {
      throw new RuntimeException(e);
    }

    return storageCache;
  }

  /**
   * Test if we have any features that are conflicting with previously
   * discovered features.
   *
   * @param features (new) features that should be added
   */
  private void addFeaturesAndCheckForDuplicates(List<ASTArcFeature> features) {
    features.forEach(f -> {
      String value = variableRemapping.getOrDefault(f.getName(), f.getName());
      // Note: If our variableRemapping contains the key, we shouldn't count
      // it (since it gets mapped
      // onto a different value anyway)
      if (featureList.contains(value) && !variableRemapping.containsKey(f.getName()))
        Log.warn(FDConstructionError.DUPLICATED_FEATURE.format(value));
      featureList.add(value);
    });
  }

  /**
   * Inspect the given features and storage and determine whether we have to
   * add optional features.
   * If a feature isn't included in any constraint and is just declared, we
   * assume it to be optional and
   * thus must add it manually (since the SMT-Solver would simply remove this
   * feature since it is
   * optional = Tautology = True)
   *
   * @param features     List of Features that should be contained in the
   *                     storage
   * @param root         Root Element / Origin where the elements come from
   * @param storageCache Previously created storage, where the new relations
   *                     should be added to
   */
  @SuppressWarnings("unchecked")
  private void addOptionals(@NotNull List<ASTArcFeature> features,
                            @NotNull T root,
                            @NotNull StorageCache<T> storageCache) {
    // List of all missing optionals
    FDRelation<T> missingOptionals = new FDRelation<>(root);

    // Get a Set of all atoms that are present on the right side of the
    // relations
    Set<T> allAtomsPresent = storageCache.getStorage().getAllRightSideAtoms();

        /*
        Due to our construction it can happen, that we still have atoms like
        "a_GT_3" left. To address this,
        we constructed a REGEX (in the constructor), that combines all the
        possible things that we could have
        added during construction. Thus, we can simply try to split each atom
         on the mentioned REGEX and
        flatten the resulting double-Nested Set to a single Set.
        This construction would for example break up the above formula into
        "a", "3"
         */
    Set<String> splittedAtoms =
      allAtomsPresent.stream().map(t -> t.toString().split(OPTIONALS_SPLITTING_REGEX))
        .flatMap(Arrays::stream).filter(Predicate.not(String::isEmpty)).collect(Collectors.toSet());

    // Check that we have all features in our formula at some place
    features.forEach(f -> {
      // Because of our instantiation and mapping we must first try to map
      // the name to the "new" name
      String value = variableRemapping.getOrDefault(f.getName(), f.getName());
      if (splittedAtoms.contains(value))
        return;
      missingOptionals.addRootRelation((T) bmgr.makeVariable(value));
    });

    storageCache.getStorage().addToOptionals(missingOptionals);
  }

  /**
   * Process a List of InnerComponents by extracting the relations and adding
   * it to the storage
   *
   * @param innerComponents List of (inner) Components to analyze
   * @param root            Root Element / Origin where the elements come from
   * @param storageCache    Previously created storage, where the new
   *                        relations should be added to
   */
  @SuppressWarnings("unchecked")
  private void processInnerComponents(@NotNull List<ASTComponentType> innerComponents,
                                      @NotNull T root,
                                      @NotNull StorageCache<T> storageCache) {
    Preconditions.checkNotNull(innerComponents);
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(storageCache);

    for (ASTComponentType c : innerComponents) {
      // Store & Reset the current variable mapping, since each component
      // should have its own mapping
      Map<String, String> backupMapping = new HashMap<>(this.variableRemapping);
      this.variableRemapping = new HashMap<>();

      // Process inner component recursively
      StorageCache<T> res = processASTComponentType(c);

      // Restore previous mapping
      this.variableRemapping = backupMapping;

      // Test if all constraints are still satisfiable
      totalASTExpressions.add(res.getTotalExpression());
      testIfAstIsUnsat();

      // Add a relation from the previous root to the current one and merge
      // the storages!
      String newRootName = c.getName();
      res.addRelation(root, (T) bmgr.makeVariable(newRootName));
      storageCache.mergeWithStorage(res);
    }
  }

  /**
   * Process a List Component Instances by extracting the relations and
   * adding it to the storage
   *
   * @param componentInstances List of (inner) Components to analyze
   * @param root               Root Element / Origin where the elements come
   *                           from
   * @param storageCache       Previously created storage, where the new
   *                           relations should be added to
   */
  @SuppressWarnings("unchecked")
  private void processComponentInstances(@NotNull List<ASTComponentInstance> componentInstances,
                                         @NotNull T root,
                                         @NotNull StorageCache<T> storageCache) {
    Preconditions.checkNotNull(componentInstances);
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(storageCache);

    for (ASTComponentInstance c : componentInstances) {
      // Store & Reset the current variable mapping, since each component
      // should have its own mapping
      Map<String, String> backupMapping = new HashMap<>(this.variableRemapping);
      this.variableRemapping = new HashMap<>();
      String newRootName = c.getName();

      // Get the Feature Bindings and replace add the bindings to our mapping
      // (so we can replace it properly)
//      if (c.getSymbol().getType().getTypeInfo().getSpannedScope() instanceof IVariableArcScope){
//        ImmutableMap<ArcFeatureSymbol, ASTExpression> newMapping =
//          ((TypeExprOfVariableComponent) c.getSymbol().getType()).getFeatureBindings();
//        newMapping.forEach((key, value) -> {
//          String stringValue = prettyPrinter.prettyprint(value);
//          if (MAExpressionsBasisVisitor.NumberToWord.isNumeric(stringValue)) {
//            stringValue =
//              MAExpressionsBasisVisitor.NumberToWord.convert(Integer.parseInt(stringValue));
//          }
//          variableRemapping.put(key.getName(), stringValue);
//        });
//      }

      // Process inner component recursively
      StorageCache<T> res =
        processASTComponentType(MAStreamHelper.getComponentTypeFromInstance(c), (T) bmgr.makeVariable(newRootName));

      // Restore previous mapping
      this.variableRemapping = backupMapping;

      // Test if all constraints are still satisfiable
      totalASTExpressions.add(res.getTotalExpression());
      testIfAstIsUnsat();

      // Add a relation from the previous root to the current one and merge
      // the storages!
      res.addRelation(root, (T) bmgr.makeVariable(newRootName));
      storageCache.mergeWithStorage(res);
    }
  }

  /**
   * Process a List of If-Statements by extracting the relations and adding
   * it to the storage
   *
   * @param varifs List of (inner) Components to analyze
   * @param root         Root Element / Origin where the elements come from
   * @param storageCache Previously created storage, where the new relations
   *                     should be added to
   */
  private void handleVarIfs(@NotNull List<ASTArcVarIf> varifs,
                            @NotNull T root,
                            @NotNull StorageCache<T> storageCache) {
    Preconditions.checkNotNull(varifs);
    Preconditions.checkNotNull(root);
    Preconditions.checkNotNull(storageCache);

    // Process the If-Statements
    StorageCache<T> res = processVarIfs(varifs, root);

    // Test if all constraints are still satisfiable
    totalASTExpressions.add(res.getTotalExpression());
    testIfAstIsUnsat();

    if (Objects.isNull(res.getStorage()))
      return;

    // Merge new storage with existing one
    storageCache.mergeWithStorage(res);
    storageCache.setTotalExpression(mergeExpressions(List.of(storageCache.getTotalExpression(),
      res.getTotalExpression())));
  }

  /**
   * Processes a List of If-Statements from a given rootName as origin by
   * evaluating the if-then-else part and
   * then recursively visiting the Sub-Components. In the end, we return a
   * StorageCache which stores all of our
   * results we found during traversal.
   *
   * @param varifs List of ASTArcVarIfs to process
   * @param root         Origin from where the If-Else-Statements come from
   * @return StorageCache constructed during traversal.
   */
  @SuppressWarnings("unchecked")
  private StorageCache<T> processVarIfs(@NotNull List<ASTArcVarIf> varifs,
                                        @NotNull T root) {
    Preconditions.checkNotNull(varifs);
    Preconditions.checkNotNull(root);

    // Init relevant variables (a list of expressions, the MontiArc
    // expression builder and a new storage)
    List<ASTExpression> expressionsToMerge = new ArrayList<>();
    ASTConditionalExpressionBuilder condExpBuilder =
      MontiArcMill.conditionalExpressionBuilder();
    StorageCache<T> tmpStorageCache = new StorageCache<>();
    int ifCounter = 0;

    // Loop over all Statements
    for (ASTArcVarIf varif : varifs) {
      // Reset the "Settings"
      condExpBuilder.setCondition(varif.getCondition());
      condExpBuilder.setTrueExpression(ALWAYS_TRUE_EXPRESSION);
      condExpBuilder.setFalseExpression(ALWAYS_TRUE_EXPRESSION);
      Set<String> featureListBackup = featureList;
      Set<String> ifFeatureList = new HashSet<>();
      Set<String> elseFeatureList = new HashSet<>();

      // Process If-Statement
      if (varif.getThen() instanceof ASTComponentBody) {
        T newRootName =
          (T) bmgr.makeVariable(root + "_If" + ((varifs.size() > 1) ? ifCounter : ""));
        List<ASTArcElement> elements =
          ((ASTComponentBody) varif.getThen()).getArcElementList();
        featureList = new HashSet<>();
        processIfElseBlock(tmpStorageCache, elements, newRootName);
        ifFeatureList = featureList;
        condExpBuilder.setTrueExpression(MontiArcMill.nameExpressionBuilder().setName(root + "_If").build());

      }

      // Process Else-Statement (is existent)
      if (varif.isPresentOtherwise() && varif.getThen() instanceof ASTComponentBody) {
        T newRootName =
          (T) bmgr.makeVariable(root + "_Else" + ((varifs.size() > 1) ? ifCounter : ""));
        List<ASTArcElement> elements =
          ((ASTComponentBody) varif.getThen()).getArcElementList();
        featureList = new HashSet<>();
        processIfElseBlock(tmpStorageCache, elements, newRootName);
        elseFeatureList = featureList;

        condExpBuilder.setFalseExpression(MontiArcMill.nameExpressionBuilder().setName(root + "_Else").build());
      }
      featureList = featureListBackup;
      featureList.addAll(ifFeatureList);
      featureList.addAll(elseFeatureList);

      // Add the expression we've discovered to the expressions we want to
      // merge at the end
      expressionsToMerge.add(condExpBuilder.build());
      ifCounter++;
    }

    // Merge all expressions and return the storage
    tmpStorageCache.setTotalExpression(mergeExpressions(expressionsToMerge));
    return tmpStorageCache;
  }

  /**
   * Small Helper to process an If-Else Block easier. This helper recursively
   * processes all elements
   * inside an if or else block respectively and adds the results to the
   * storage. As a final set, we return
   * the Expression we've constructed along our traversal.
   *
   * @param storage  Storage where the discovered Relations should be added to
   * @param elements Elements to Traverse
   * @param newRoot  Name of the Origin (where the If-Else "comes" from)
   */
  private void processIfElseBlock(@NotNull StorageCache<T> storage,
                                  @NotNull List<ASTArcElement> elements,
                                  @NotNull T newRoot) {
    Preconditions.checkNotNull(storage);
    Preconditions.checkNotNull(elements);
    Preconditions.checkNotNull(newRoot);

    // Get the storage from nested things...
    StorageCache<T> h = processArcElementList(elements, newRoot);
    storage.mergeWithStorage(h);
  }

  /**
   * Wraps an expression in brackets and returns it
   *
   * @param expr Expression to Wrap
   * @return Expression with Brackets
   */
  private ASTExpression wrapInBrackets(ASTExpression expr) {
    return CommonExpressionsMill.bracketExpressionBuilder().setExpression(expr).build();
  }

  private ASTExpression mergeExpressions(@NotNull List<ASTExpression> expressions) {
    Preconditions.checkNotNull(expressions);
    ASTBooleanAndOpExpressionBuilder commonExpBuilder =
      new ASTBooleanAndOpExpressionBuilder();

    // If we have multiple constraints => Combine all using "&&" (since all
    // must be fulfilled)
    if (expressions.size() == 0)
      return ALWAYS_TRUE_EXPRESSION;

    // We have >= 1 expressions
    commonExpBuilder.setLeft(wrapInBrackets(expressions.get(0)));
    commonExpBuilder.setOperator("&&");

    // Combine all constraints one after another (by setting left form to the
    // current expression,
    // conjunct it with the new form and then re-set the left form to the
    // current, newly created one...)
    int ctr = 0;
    for (ASTExpression e : expressions) {
      ctr++;
      if (ctr == 1)
        continue; // We don't want to prepend an "&&" before the first
      // constraint, so skip this
      ASTExpression new_exp =
        commonExpBuilder.setRight(wrapInBrackets(e)).build();
      commonExpBuilder.setLeft(new_exp);
    }
    return commonExpBuilder.getLeft();
  }

  /**
   * Test if the constructed AST (expressions) are satisfiable or not (if
   * not, we throw an error).
   */
  private void testIfAstIsUnsat() {
    ASTExpression expr = mergeExpressions(new ArrayList<>(totalASTExpressions));
    if (convertHelper.isUnsat(convertHelper.ma2smtFormulaConverter.convert(expr, variableRemapping))) {
      Log.error(FDConstructionError.FORMULA_IS_UNSAT.format(expr));
    }
  }

  /**
   * Helper-Class which holds the result of a processed AST.
   * ast contains the processed AST and the storage contains the information
   * to create the
   * FeatureDiagram (extracted from the AST)
   *
   * @param <R> Generic Type of the Result (should be BooleanFormula at the
   *            moment)
   */
  public static class AST2FDResult<R extends Formula> {
    public ASTMACompilationUnit ast;
    public FDConstructionStorage<R> storage;

    AST2FDResult(ASTMACompilationUnit ast, FDConstructionStorage<R> storage) {
      this.ast = ast;
      this.storage = storage;
    }
  }
}

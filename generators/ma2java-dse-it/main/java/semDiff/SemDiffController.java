/* (c) https://github.com/MontiCore/monticore */
package semDiff;

import com.google.common.base.Function;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import main.DSEMain;
import montiarc.rte.dse.EvaluationControllerI;
import montiarc.rte.dse.ListerI;
import montiarc.rte.dse.PathCondition;
import montiarc.rte.dse.ResultI;
import montiarc.rte.dse.StatesList;
import montiarc.rte.dse.TestController;
import montiarc.rte.dse.TestControllerI;
import org.apache.commons.lang3.tuple.Pair;
import results.ResultSemDiff;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This controller computes the semantic differences between two MontiArc models.
 */
public class SemDiffController<In1, Out1, In2, Out2> implements TestControllerI, EvaluationControllerI {

  protected Context ctx;
  protected List<BoolExpr> branchingConditions = new ArrayList<>();
  protected int usedOracleCount = 0;
  protected List<Boolean> oracles = new ArrayList<>();
  protected List<Boolean> oraclesRun = new ArrayList<>();
  protected PathCondition takenBranches;
  protected Set<StatesList> visitedStates = new LinkedHashSet<>();

  // converts the input of the first component to the inputType of the second component
  protected Function<Pair<In1, Out1>, In2> converter;

  // gets the output of the first component in a list of strings
  protected Function<Pair<In1, Out1>, List<String>> getEntriesResult1;

  // gets the output of the second component in a list of strings
  protected Function<Out2, List<String>> getEntriesResult2;

  // first component, will be analyzed through dse
  protected DSEMain component1;

  // second component, will be analyzed about the possibility of the input / output pairs of the first component
  protected Function<In2, Out2> component2;

  // for evaluation purpose, to track the number of solver calls
  protected int solverCalls = 0;

  // for evaluation purpose, to track the number of satisfiability paths
  protected int satPaths = 0;

  /***
   * This function generates all possible Boolean combinations, it is used to generate all Oracle combinations.
   */
  private static void generateBooleanCombinationsHelper(int n, List<Boolean> currentCombination,
                                                        List<List<Boolean>> combinations) {
    // Base case: current combination has reached the desired length
    if (currentCombination.size() == n) {
      combinations.add(new ArrayList<>(currentCombination));
      return;
    }

    // Recursively go through all possible values (true/false) for the current position of the combination
    currentCombination.add(true);
    generateBooleanCombinationsHelper(n, currentCombination, combinations);
    System.gc();
    currentCombination.remove(currentCombination.size() - 1);

    currentCombination.add(false);
    generateBooleanCombinationsHelper(n, currentCombination, combinations);
    System.gc();
    currentCombination.remove(currentCombination.size() - 1);
  }

  /**
   * sets all needed functions for semDiff, must be called before startSemDiff
   */
  public void setUp(Function<Pair<In1, Out1>, In2> converter, Function<Pair<In1, Out1>,
          List<String>> getEntriesResult1, Function<Out2, List<String>> getEntriesResult2, DSEMain component1, Function<In2, Out2> component2) {
    this.component1 = component1;
    this.component2 = component2;
    this.converter = converter;
    this.getEntriesResult1 = getEntriesResult1;
    this.getEntriesResult2 = getEntriesResult2;
  }

  /**
   * executes the actual calculation of the semantic difference between component1 and component2.
   * Takes as an input the inputLength for the components and if needed parameters
   */
  public ResultSemDiff<In1, Out1> startSemDiff(Integer inputLength, String... parameter) throws Exception {
    List<String> inputDef = new ArrayList<>();
    inputDef.add("");
    inputDef.add("PathCoverageControllerGC");
    inputDef.add(String.valueOf(inputLength));

    for (String param : parameter) {
      inputDef.add(param);
    }

    String[] comp1Input = inputDef.toArray(new String[0]);

    // starts the run of the first component
    ResultI<In1, Out1> resultComponent1 = component1.runController(comp1Input);

    // get solver calls and number of SAT paths for evaluation purpose
    if (TestController.getController() instanceof EvaluationControllerI) {
      EvaluationControllerI controllerEvaluation = (EvaluationControllerI) TestController.getController();
      solverCalls = controllerEvaluation.getSolverCalls();
      satPaths = controllerEvaluation.getSatPaths();
    }

    ResultSemDiff<In1, Out1> semDiff = new ResultSemDiff<>();

    //set TestController to SemDiffController to recognize the use of oracles
    this.init(TestController.getCtx());
    takenBranches = new PathCondition();

    for (Pair<In1, Out1> res : resultComponent1.getInterestingInputs()) {
      // reset oracle use
      usedOracleCount = 0;
      oraclesRun.clear();

      // Convert the input to be used for the second component
      In2 inputComp2 = converter.apply(res);

      Out2 resultComponent2 = component2.apply(inputComp2);

      // List of possible semDiff witnesses
      List<List<ListerI>> localSemDiff = new ArrayList<>();

      List<ListerI> result2 = (List<ListerI>) resultComponent2;

      /***
       * Expressions are compared via strings because it is not a hundred percent reliable to compare
       * expressions directly.
       */
      List<String> res1 = getEntriesResult1.apply(res);
      List<String> res2 = getEntriesResult2.apply(resultComponent2);

      /***If the results are not equal, the result of the first component is a possible semDiff witness
       * and is added to the localSemDiff.
       */
      if (!res2.equals(res1)) {
        localSemDiff.add(result2);
      }

      /***
       * If the result was added to localSemDiff, it is checked if the path was non-deterministic.
       * In this case, all possible oracle assignments are computed to test every possible path with this input.
       *
       * If the path is deterministic, the contents of localSemDiff are added to the list of SemDiff witness
       * of the two components.
       */
      if (localSemDiff.size() != 0) {
        boolean next = true;
        if (oraclesRun.size() != 0) {
          localSemDiff = checkAllOracleCombinations(inputComp2, localSemDiff, res1, next, allOracleCombinations(oraclesRun.size()));
        }
      }

      /***
       * If localSemDiff contains something, a semantic difference has been found and  are added to the list
       * of SemDiff witnesses
       */
      if (localSemDiff.size() != 0) {
        semDiff.addSemDiffPair(res);
      }
      System.gc();
    }

    return semDiff;
  }

  /**
   * This function checks all possible oracle path combinations to determine if a non-deterministic path of component 1
   * is reproducible in component 2
   *
   * @param inputComp2,   Input of Component 2
   * @param localSemDiff, Possible SemDiff witness where oracles were used
   * @param res1,         output of component 1 as a List of Strings
   * @param next
   * @param newOracles,   all possible oracle combinations for the size of the oracles used for this output in component 1
   * @return List of inputs that are semDiff witness
   */
  protected List<List<ListerI>> checkAllOracleCombinations(In2 inputComp2, List<List<ListerI>> localSemDiff, List<String> res1,
                                                           boolean next, List<List<Boolean>> newOracles) {
    // needed to check later if more oracles are used than in the beginning
    int localSizeOfOraclesRun = oraclesRun.size();

    usedOracleCount = 0;
    oraclesRun.clear();

    // Each oracle combination is checked to see if it reproduces the input/output combination of component 1 for component 2.
    for (List<Boolean> oracleList : newOracles) {
      if (next) {
        this.oracles = oracleList;
        Out2 resComponent2 = component2.apply(inputComp2);

        //check if more oracles were used than in the first run, if yes runs the function again with new oracles
        if (oraclesRun.size() > localSizeOfOraclesRun) {
          localSemDiff = checkAllOracleCombinations(inputComp2, localSemDiff, res1, next, allOracleCombinations(oraclesRun.size()));
          break;
        }
        usedOracleCount = 0;
        oraclesRun.clear();

        /***
         * Expressions are compared via strings because it is not a hundred percent reliable to compare
         * expressions directly.
         */
        List<ListerI> result2_ = (List<ListerI>) resComponent2;
        List<String> res22 = getEntriesResult2.apply(resComponent2);

        /**
         * if the results of component 1 and component 2 are the same, the non-deterministic path of component 1
         * is reproducible in component 2
         *
         * if the results of component 1 and component 2 differ, the remaining oracle combination is checked
         */
        if (!res22.equals(res1)) {
          localSemDiff.add(result2_);
        } else {
          localSemDiff.clear();
          next = false;
        }
      } else {
        System.gc();
        break;
      }
    }

    // List of semDiff witness, if this list is empty no semDiffWitness could be found
    return localSemDiff;
  }


  /**
   * This function calculates all possible oracle combinations for a given number of oracle occurrences
   *
   * @param oracleCount, number of oracle occurrences
   * @return List of all possible oracle combinations
   */
  public List<List<Boolean>> allOracleCombinations(Integer oracleCount) {
    List<List<Boolean>> combinations = new ArrayList<>();
    generateBooleanCombinationsHelper(oracleCount, new ArrayList<>(), combinations);
    return combinations;
  }

  public void init(Context ctx) {
    Map<String, String> cfg = new LinkedHashMap<>();
    cfg.put("model", "true");

    this.ctx = ctx;
    TestController.init(this);
  }

  @Override
  public Context getCtx() {
    return ctx;
  }

  @Override
  public boolean shouldEndRun() {
    return false;
  }

  @Override
  public boolean getIfOracle(String branchId) {
    BoolExpr booleExpr = ctx.mkBoolConst("oracle_" + usedOracleCount);

    boolean result = true;
    if (usedOracleCount < oracles.size()) {
      result = oracles.get(usedOracleCount);
    }

    oraclesRun.add(result);
    usedOracleCount++;

    return getIf(ctx.mkEq(booleExpr, ctx.mkBool(result)), result, branchId);
  }

  protected Boolean loadBoolValue(Model model, BoolExpr boolExpr) {
    return model.eval(boolExpr, true).isTrue();
  }

  @Override
  public boolean getIf(BoolExpr condition, boolean result, String branchID) {
    branchingConditions.add(ctx.mkEq(condition, ctx.mkBool(result)));
    montiarc.rte.log.Log.trace("BranchingCondition:   " + ctx.mkEq(condition, ctx.mkBool(result)));
    return result;
  }

  @Override
  public void addBranch(BoolExpr condition, String branchId) {
    takenBranches.addBranch(condition, branchId);
  }

  @Override
  public void saveStates(StatesList info) {
    visitedStates.add(info);
  }

  @Override
  public Set<StatesList> getVisitedStates() {
    return visitedStates;
  }

  @Override
  public int getSolverCalls() {
    return solverCalls;
  }

  @Override
  public int getSatPaths() {
    return satPaths;
  }
}

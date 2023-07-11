/* (c) https://github.com/MontiCore/monticore */
package evaluation;

import com.microsoft.z3.*;
import evaluation.bigModel.*;
import evaluation.smallModel.*;
import montiarc.rte.dse.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.codehaus.commons.nullanalysis.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Evaluation {

  private static final String TEST_FILE_PATH = System.getProperty("buildDir") + "/Evaluation.xls";
  HSSFWorkbook workbook = new HSSFWorkbook();
  HSSFSheet sheetSmallModel = workbook.createSheet("smallModel");
  HSSFSheet sheetBigModel = workbook.createSheet("bigModel");

  public static Stream<Arguments> specificationSmallModel() {

    // parameters for the component must be the fourth element in the args argument of the run
    // function
    Integer inputLength = 1;
    return Stream.of(
            Arguments.of(
                    (Callable<ResultI<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
                            List<ListerOutSmallModel>>>) () -> {
                      DSEMainSmallModel smallModel = new DSEMainSmallModel();

                      try {
                        return smallModel.runPathCoverageController(inputLength, new String[]{"", "", "",
                                "400000"});
                      } catch (Exception e) {
                        throw new RuntimeException(e);
                      }
                    },
                    false,
                    (1),
                    ("PathController"),
                    inputLength
            )
    );
  }

  public static Stream<Arguments> specificationBigModel() {

    Integer inputLength = 1;

    // parameters for the component must be the fourth element in the args argument of the run
    // function
    return Stream.of(
            Arguments.of(
                    (Callable<ResultI<Pair<List<ListerInElevatorSystem>, ListerParameterElevatorSystem>,
                            List<ListerOutElevatorSystem>>>) () -> {
                      DSEMainElevatorSystem bigModel = new DSEMainElevatorSystem();

                      try {
                        return bigModel.runPathCoverageController(inputLength, new String[]{"", "", "", "3"});
                      } catch (Exception e) {
                        throw new RuntimeException(e);
                      }
                    },
                    false,
                    (1),
                    ("PathController"),
                    inputLength
            )
    );
  }

  @BeforeAll
  void setup() {
    List<HSSFSheet> sheets = Arrays.asList(sheetSmallModel, sheetBigModel);
    for (HSSFSheet sheet : sheets) {
      setUpTable(sheet);
    }
  }

  /**
   * helper method to create Excel table
   */
  private void setUpTable(HSSFSheet sheet) {
    HSSFRow row = sheet.createRow(0);

    fillCell(sheet, 0, 1, "Time ns");
    fillCell(sheet, 0, 2, "duplicates");
    fillCell(sheet, 0, 3, "interesting inputs");
    fillCell(sheet, 0, 4, "taken transitions");
    fillCell(sheet, 0, 5, "existing transitions");
    fillCell(sheet, 0, 6, "visited enum states");
    fillCell(sheet, 0, 7, "existing enum states");
    fillCell(sheet, 0, 8, "visited states");
    fillCell(sheet, 0, 9, "existing states");
    fillCell(sheet, 0, 10, "redundant paths");
    fillCell(sheet, 0, 11, "non-deterministic paths");
  }

  @AfterAll
  void teardown() throws IOException {
    createAndWriteFile();
  }

  /**
   * helper method to create Excel table
   *
   * @throws IOException
   */
  private void createAndWriteFile() throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(TEST_FILE_PATH);
    workbook.write(fileOutputStream);
    fileOutputStream.close();
    workbook.close();
  }

  @ParameterizedTest
  @MethodSource("specificationSmallModel")
  public void evaluationSmallModel(@NotNull Callable<ResultI<Pair<List<ListerInSmallModel>,
          ListerParameterSmallModel>,
          List<ListerOutSmallModel>>> testController, @NotNull boolean calculateNonDetPaths,
                                   @NotNull int row, @NotNull String controller,
                                   @NotNull Integer inputLength) throws Exception {

    //  final long timeStartMili = System.currentTimeMillis();
    final long timeStartNano = System.nanoTime();

    ResultI<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
            List<ListerOutSmallModel>> result =
            testController.call();

    //   final long timeEndMili = System.currentTimeMillis();
    final long timeEndNano = System.nanoTime();

    assertThat(result).isNotNull();

    // setUp Excel
    createCell(sheetSmallModel, row, 0, controller + " " + inputLength);

    Context ctx = TestController.getCtx();

    // interesting inputs
    Set<Pair<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
            List<ListerOutSmallModel>>> interestingInputs =
            result.getInterestingInputs();

    // evaluation time
    System.out.println(controller + ": Time of the controller in nanoseconds: " + (timeEndNano - timeStartNano));

    fillCell(sheetSmallModel, row, 1, timeEndNano - timeStartNano);

    Set<List<ListerI>> simplifiedIInputs = new HashSet<>();

    // extract the simplified outputs
    for (Pair<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
            List<ListerOutSmallModel>> res : interestingInputs) {
      List<ListerOutSmallModel> listsOut = res.getValue();

      List<ListerI> listOutSimplified = new ArrayList<>();
      for (ListerOutSmallModel temp : listsOut) {
        listOutSimplified.add(temp.getExpression());
      }
      simplifiedIInputs.add(listOutSimplified);
    }

    // evaluation duplicates of interesting inputs
    int duplicates = getNumberOfDuplicates(simplifiedIInputs);

    System.out.println(controller + ": duplicates of interesting inputs: " + duplicates);
    System.out.println(controller + ": number of interesting inputs: " + interestingInputs.size());

    fillCell(sheetSmallModel, row, 2, duplicates);
    fillCell(sheetSmallModel, row, 3, interestingInputs.size());

    //evaluation completeness - transitions
    Set<String> allTransitions = setTransitionsSM();

    Set<InputAndCondition> condition = result.getInputsAndCondition();
    int transitions = evaluateTransitions(condition, allTransitions);

    // transitions will be -1 if transitions were taken, that were not in allTransitions
    System.out.println(controller + ": " + transitions + " out of " + allTransitions.size() + " " +
            "transition were taken");

    fillCell(sheetSmallModel, row, 4, transitions);
    fillCell(sheetSmallModel, row, 5, allTransitions.size());

    //evaluation completeness -  states (enums)
    Set<StatesList> visitedStates = new HashSet<>();

    if (TestController.getController() instanceof EvaluationControllerI) {
      EvaluationControllerI evaluationControllerI = (EvaluationControllerI) TestController.getController();
      visitedStates = evaluationControllerI.getVisitedStates();
    }

    assertThat(visitedStates).isNotNull();


    Set<Enum<? extends Enum>> visitedEnumsDistinctionModel = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsEvaluationModel = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsCounterMDSE = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsCounterSA = new HashSet<>();

    // fill expectedStates
    Set<StateInfo> expectedStatesDistinctionModel = new HashSet<>();
    expectedStatesDistinctionModel.add(StateInfo.newStateInfo(DistinctionModel.States.valueOf(
            "Idle"), new ArrayList<>(), "distinction"));

    Set<StateInfo> expectedStatesEvaluationModel = fillExpectedStatesEvaluationModel();
    Set<StateInfo> expectedStatesCounterSA = fillExpectedStatesCounterSA();
    Set<StateInfo> expectedStatesCounterMDSE = fillExpectedStatesCounterMDSE();

    Set<StateInfo> visitedStatesCompareDistinctionModel = new HashSet<>();
    Set<StateInfo> visitedStatesCompareEvaluationModel = new HashSet<>();
    Set<StateInfo> visitedStatesCompareCounterMDSE = new HashSet<>();
    Set<StateInfo> visitedStatesCompareCounterSA = new HashSet<>();

    int missedEnumStates = 0;

    // visited states are divided into the corresponding set of enumStates
    for (StatesList states : visitedStates) {
      for (StateInfo state : states.getStates()) {
        switch (state.getComponent()) {
          case "distinction":
            visitedEnumsDistinctionModel.add(DistinctionModel.States.valueOf(state.getState()
                    .name()));
            break;
          case "evaluation":
            visitedEnumsEvaluationModel.add(EvaluationModel.States.valueOf(state.getState()
                    .name()));
            break;
          case "counterMDSE":
            visitedEnumsCounterMDSE.add(Counter.States.valueOf(state.getState().name()));
            break;
          case "counterSA":
            visitedEnumsCounterSA.add(Counter.States.valueOf(state.getState().name()));
            break;
        }
      }
    }

    // calculation of missed enum states
    missedEnumStates += getMissedEnumStates(visitedEnumsDistinctionModel, Arrays.asList(DistinctionModel.States.values()));
    missedEnumStates += getMissedEnumStates(visitedEnumsEvaluationModel, Arrays.asList(EvaluationModel.States.values()));
    missedEnumStates += getMissedEnumStates(visitedEnumsCounterMDSE, Arrays.asList(Counter.States.values()));
    missedEnumStates += getMissedEnumStates(visitedEnumsCounterSA, Arrays.asList(Counter.States.values()));

    {
      // visitedEnumStates are evaluated with -1 if enum states were visited that were not
      // included in the expected enum states.
      int sizeOfAllStates =
              EvaluationModel.States.values().length + DistinctionModel.States.values().length + (2 * Counter.States.values().length);

      int visitedEnumStates =
              visitedEnumsCounterMDSE.size() + visitedEnumsCounterSA.size() + visitedEnumsEvaluationModel.size() + visitedEnumsDistinctionModel.size();

      resultVisitedEnumStates(row, controller, sheetSmallModel, missedEnumStates, sizeOfAllStates, visitedEnumStates);
    }

    //evaluation completeness -  states (enums + internal states)
    int missedStates = 0;

    // visited states are divided into the corresponding set of states
    for (StatesList states : visitedStates) {
      for (StateInfo state : states.getStates()) {
        switch (state.getComponent()) {
          case "distinction":
            visitedStatesCompareDistinctionModel.add(state);
            break;
          case "evaluation":
            visitedStatesCompareEvaluationModel.add(state);
            break;
          case "counterMDSE":
            visitedStatesCompareCounterMDSE.add(state);
            break;
          case "counterSA":
            visitedStatesCompareCounterSA.add(state);
            break;
        }
      }
    }

    missedStates += getMissedStates(expectedStatesDistinctionModel, visitedStatesCompareDistinctionModel);
    missedStates += getMissedStates(expectedStatesEvaluationModel, visitedStatesCompareEvaluationModel);
    missedStates += getMissedStates(expectedStatesCounterMDSE, visitedStatesCompareCounterMDSE);
    missedStates += getMissedStates(expectedStatesCounterSA, visitedStatesCompareCounterSA);

    {
      /* numberOfVisitedStates are evaluated with -1 if states were visited, that were not
       * included in the expected states (checked only for the DistinctionModel and
       * EvaluationModel subcomponents, since the Counter component has an infinite state space).
       */
      int sizeOfAllStates =
              expectedStatesEvaluationModel.size() + expectedStatesDistinctionModel.size();

      int numberOfVisitedStates =
              visitedStatesCompareEvaluationModel.size() + visitedStatesCompareDistinctionModel.size();


      resultVisitedStates(row, controller, sheetSmallModel, missedStates, numberOfVisitedStates, sizeOfAllStates, visitedStatesCompareCounterMDSE.size() + visitedEnumsCounterSA.size());
    }

    //check existence of redundant paths
    Set<Pair<List<ListerI>, Expr<BoolSort>>> output = new HashSet<>();

    // extract the simplified output with the corresponding branchConditions
    for (InputAndCondition<List<ListerInSmallModel>,
            List<ListerOutSmallModel>> temp : condition) {

      List<ListerI> listOutSimplified = new ArrayList<>();
      for (ListerOutSmallModel temps : temp.getOutput()) {
        listOutSimplified.add(temps.getExpression());
      }
      output.add(ImmutablePair.of(listOutSimplified, temp.getBranches().getBranchConditions()
              .simplify()));
    }

    int redundantPaths = getRedundantPaths(output);

    System.out.println(controller + ": The model has " + redundantPaths + " redundant paths");

    fillCell(sheetSmallModel, row, 10, redundantPaths);

    {
      /* set local variables to null to release them for the garbage collector and trigger it
       * this is done for performance reasons, with large models the evaluation generates a lot
       * of data
       * the last part of the evaluation is very computationally intensive, to improve this part
       * all unused data is deleted
       */
      result = null;
      interestingInputs = null;
      simplifiedIInputs = null;
      visitedStates = null;
      visitedEnumsEvaluationModel = null;
      visitedEnumsCounterMDSE = null;
      visitedEnumsCounterSA = null;
      visitedEnumsDistinctionModel = null;
      visitedStatesCompareCounterSA = null;
      visitedStatesCompareCounterMDSE = null;
      visitedStatesCompareDistinctionModel = null;
      visitedStatesCompareEvaluationModel = null;
      expectedStatesEvaluationModel = null;
      expectedStatesCounterMDSE = null;
      expectedStatesCounterSA = null;
      expectedStatesDistinctionModel = null;
      allTransitions = null;
      output = null;
      System.gc();
    }

    // check if component is non-deterministic
    if (calculateNonDetPaths) {
      int nonDetPaths = checkNumberOfNonDeterminism(condition, ctx);
      System.out.println(controller + ": The component has " + nonDetPaths + " non-deterministic " +
              "paths");
      fillCell(sheetSmallModel, row, 11, nonDetPaths);
    } else {
      boolean nonDeterministic = checkNonDeterminism(condition, ctx);
      if (nonDeterministic) {
        System.out.println(controller + ": The component has non-deterministic paths");
      } else {
        System.out.println(controller + ": The component has only deterministic paths");
      }
      fillCell(sheetSmallModel, row, 11, String.valueOf(nonDeterministic));
    }
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedStatesCounterMDSE() {
    Set<StateInfo> expectedStatesCounterMDSE = new HashSet<>();

    expectedStatesCounterMDSE.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 0.0), 0.0>")), "counterMDSE"));
    expectedStatesCounterMDSE.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 1.0), 1.0>")), "counterMDSE"));
    expectedStatesCounterMDSE.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 (/ 3.0 2.0)), 1.5>")), "counterMDSE"));

    return expectedStatesCounterMDSE;
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedStatesCounterSA() {
    Set<StateInfo> expectedStatesCounterSA = new HashSet<>();

    expectedStatesCounterSA.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 0.0), 0.0>")), "counterSA"));
    expectedStatesCounterSA.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 1.0), 1.0>")), "counterSA"));
    expectedStatesCounterSA.add(StateInfo.newStateInfo(Counter.States.valueOf("Idle"),
            new ArrayList<>(Collections.singleton("counter: " +
                    "<(+ 0.0 (/ 3.0 2.0)), 1.5>")), "counterSA"));

    return expectedStatesCounterSA;
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedStatesEvaluationModel() {
    Set<StateInfo> expectedStatesEvaluationModel = new HashSet<>();
    expectedStatesEvaluationModel.add(StateInfo.newStateInfo(EvaluationModel.States.valueOf("mdse"),
            new ArrayList<>(), "evaluation"));
    expectedStatesEvaluationModel.add(StateInfo.newStateInfo(EvaluationModel.States.valueOf("sa"),
            new ArrayList<>(), "evaluation"));
    expectedStatesEvaluationModel.add(StateInfo.newStateInfo(EvaluationModel.States.valueOf(
                    "nonModule"),
            new ArrayList<>(), "evaluation"));

    return expectedStatesEvaluationModel;
  }

  /**
   * this function counts the number of states of expectedStates that are not contained in visitedStates
   */
  private int getMissedStates(Set<StateInfo> expectedStates, Set<StateInfo> visitedStates) {
    int missedStates = 0;

    //  missedStates += expectedStates.stream().filter(expected -> !containsState(visitedStates, expected)).count();
    missedStates += expectedStates.stream().filter(expected -> !visitedStates.contains(expected)).count();

    return missedStates;
  }

  /**
   * this function counts the number of enum states of expectedStates that are not contained in visitedStates
   */
  private int getMissedEnumStates(Set<Enum<? extends Enum>> visitedStates, List<Enum<? extends Enum>> expectedStates) {
    int missedStates = 0;

    missedStates += expectedStates.stream().filter(expected -> !visitedStates.contains(expected)).count();
    return missedStates;

  }

  /**
   * this function calculates the number of redundant paths in the result
   */
  private static int getRedundantPaths(Set<Pair<List<ListerI>, Expr<BoolSort>>> output) {
    int redundantPaths = 0;

    Set<Pair<List<ListerI>, Expr<BoolSort>>> deletesOutput =
            new HashSet<>(output);

    for (Pair<List<ListerI>, Expr<BoolSort>> element : output) {

      deletesOutput.remove(element);

      for (Pair<List<ListerI>, Expr<BoolSort>> dElement :
              deletesOutput) {
        boolean found = false;

        if (element.getValue().equals(dElement.getValue())) {

          int i = 0;
          for (ListerI outExpr : dElement.getKey()) {
            if (outExpr.equals(element.getKey().get(i))) {
              found = true;
              break;
            }
            i++;
          }
        }
        if (found) {
          redundantPaths++;
          break;
        }
      }
    }
    return redundantPaths;
  }

  /**
   * this functions prints and stores the result of the visitedStates
   * extra states is used if the state space is infinitif and therefor they need to be excluded of the check if all states were either visited or missed
   * if the state space is finite extra states needs to be 0
   */
  private void resultVisitedStates(int row, String controller, HSSFSheet sheet, int missedStates, int numberOfVisitedStates, int sizeOfAllStates, int extraStates) {

    if (numberOfVisitedStates + missedStates != sizeOfAllStates) {
      numberOfVisitedStates = -1;
    }


    if (extraStates != 0) {
      int numberOfVisitedStatesFinal;
      if (numberOfVisitedStates != -1) {
        numberOfVisitedStatesFinal =
                numberOfVisitedStates + extraStates;
      } else {
        numberOfVisitedStatesFinal =
                numberOfVisitedStates;
      }
      System.out.println(controller + ": " + numberOfVisitedStatesFinal + " out of " +
              "infinitely many states where visited by" +
              " the controller");

      fillCell(sheet, row, 8, numberOfVisitedStatesFinal);
      fillCell(sheet, row, 9, "\u221E");
    } else {
      System.out.println(controller + ": " + numberOfVisitedStates + " out of " + sizeOfAllStates + " states where " +
              "visited by the controller");

      fillCell(sheet, row, 8, numberOfVisitedStates);
      fillCell(sheet, row, 9, sizeOfAllStates);
    }
  }

  /**
   * this function prints and stores the result of the visited EnumStates in the Excel file
   */
  private void resultVisitedEnumStates(int row, String controller, HSSFSheet sheet, int missedEnumStats, int sizeOfAllStates,
                                       int visitedEnumStates) {

    if (visitedEnumStates + missedEnumStats != sizeOfAllStates) {
      visitedEnumStates = -1;
    }
    System.out.println(controller + ": " + visitedEnumStates + " out of " + sizeOfAllStates +
            " enum-states " +
            "where visited by the controller");

    fillCell(sheet, row, 6, visitedEnumStates);
    fillCell(sheet, row, 7, sizeOfAllStates);
  }

  /**
   * this function calculates the number of duplicates of List of ListerI inside a Set
   */
  private static int getNumberOfDuplicates(Set<List<ListerI>> simplifiedIInputs) {

    int duplicates = 0;

    Set<List<ListerI>> noDuplicates = new HashSet<>();

    for (List<ListerI> element : simplifiedIInputs) {

      boolean isDuplicate = false;

      for (List<ListerI> nonElement : noDuplicates) {
        for (int i = 0; i < nonElement.size(); i++) {
          if (element.get(i).equals(nonElement.get(i))) {
            isDuplicate = true;
          } else {
            isDuplicate = false;
            break;
          }
        }
        if (isDuplicate) {
          break;
        }
      }
      if (!isDuplicate) {
        noDuplicates.add(element);
      } else {
        duplicates++;
      }
    }
    return duplicates;
  }

  @ParameterizedTest
  @MethodSource("specificationBigModel")
  public void evaluationBigModel(@NotNull Callable<ResultI<Pair<List<ListerInElevatorSystem>,
          ListerParameterElevatorSystem>,
          List<ListerOutElevatorSystem>>> testController, @NotNull boolean calculateNonDetPaths,
                                 @NotNull int row, @NotNull String controller,
                                 @NotNull Integer inputLength) throws Exception {

    //  final long timeStartMili = System.currentTimeMillis();
    final long timeStartNano = System.nanoTime();

    ResultI<Pair<List<ListerInElevatorSystem>, ListerParameterElevatorSystem>,
            List<ListerOutElevatorSystem>> result =
            testController.call();

    //   final long timeEndMili = System.currentTimeMillis();
    final long timeEndNano = System.nanoTime();

    assertThat(result).isNotNull();

    // setUp Excel
    createCell(sheetBigModel, row, 0, controller + " " + inputLength);

    Context ctx = TestController.getCtx();

    // interesting inputs
    Set<Pair<Pair<List<ListerInElevatorSystem>, ListerParameterElevatorSystem>,
            List<ListerOutElevatorSystem>>> interestingInputs =
            result.getInterestingInputs();

    // evaluation time
    System.out.println(controller + ": Time of the controller in nanoseconds: " + (timeEndNano - timeStartNano));

    fillCell(sheetBigModel, row, 1, timeEndNano - timeStartNano);

    Set<List<ListerI>> simplifiedIInputs = new HashSet<>();

    // extract the simplified outputs
    for (Pair<Pair<List<ListerInElevatorSystem>, ListerParameterElevatorSystem>,
            List<ListerOutElevatorSystem>> res : interestingInputs) {
      List<ListerOutElevatorSystem> listsOut = res.getValue();

      List<ListerI> listOutSimplified = new ArrayList<>();
      for (ListerOutElevatorSystem temp : listsOut) {
        listOutSimplified.add(temp.getExpression());
      }
      simplifiedIInputs.add(listOutSimplified);
    }

    // evaluation duplicates of interesting inputs
    int duplicates = getNumberOfDuplicates(simplifiedIInputs);

    System.out.println(controller + ": duplicates of interesting inputs: " + duplicates);
    System.out.println(controller + ": number of interesting inputs: " + interestingInputs.size());

    fillCell(sheetBigModel, row, 2, duplicates);
    fillCell(sheetBigModel, row, 3, interestingInputs.size());

    //evaluation completeness - transitions
    Set<String> allTransitions = setTransitionsBM();

    Set<InputAndCondition> condition = result.getInputsAndCondition();
    int transitions = evaluateTransitions(condition, allTransitions);

    // transitions will be -1 if transitions were taken, that were not in allTransitions
    System.out.println(controller + ": " + transitions + " out of " + allTransitions.size() + " " +
            "transition were taken");

    fillCell(sheetBigModel, row, 4, transitions);
    fillCell(sheetBigModel, row, 5, allTransitions.size());

    //evaluation completeness -  states (enums)
    Set<StatesList> visitedStates = new HashSet<>();

    if (TestController.getController() instanceof EvaluationControllerI) {
      EvaluationControllerI evaluationControllerI = (EvaluationControllerI) TestController.getController();
      visitedStates = evaluationControllerI.getVisitedStates();
    }

    assertThat(visitedStates).isNotNull();

    Set<Enum<? extends Enum>> visitedEnumsController = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsDoor = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsLift = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsSplitter = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsFloor1 = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsFloor2 = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsFloor3 = new HashSet<>();
    Set<Enum<? extends Enum>> visitedEnumsFloor4 = new HashSet<>();

    // Controller component
    Set<StateInfo> expectedStatesController = fillExpectedSatesController();

    // Door component
    Set<StateInfo> expectedStatesDoor = fillExpectedStatesDoor();

    // Lift component
    Set<StateInfo> expectedStatesLift = fillExpectedStatesLift();

    // Splitter component
    Set<StateInfo> expectedStatesSplitter = new HashSet<>();
    expectedStatesSplitter.add(StateInfo.newStateInfo(Splitter.States.valueOf("Idle"),
            new ArrayList<>(), "control.splitter"));

    // Floor1 component
    Set<StateInfo> expectedStatesFloor1 = new HashSet<>();
    expectedStatesFloor1.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOff"),
            new ArrayList<>(), "control.floor1"));
    expectedStatesFloor1.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOn"),
            new ArrayList<>(), "control.floor1"));

    // Floor2 component
    Set<StateInfo> expectedStatesFloor2 = new HashSet<>();
    expectedStatesFloor2.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOff"),
            new ArrayList<>(), "control.floor2"));
    expectedStatesFloor2.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOn"),
            new ArrayList<>(), "control.floor2"));

    // Floor3 component
    Set<StateInfo> expectedStatesFloor3 = new HashSet<>();
    expectedStatesFloor3.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOff"),
            new ArrayList<>(), "control.floor3"));
    expectedStatesFloor3.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOn"),
            new ArrayList<>(), "control.floor3"));

    // Floor4 component
    Set<StateInfo> expectedStatesFloor4 = new HashSet<>();
    expectedStatesFloor4.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOff"),
            new ArrayList<>(), "control.floor4"));
    expectedStatesFloor4.add(StateInfo.newStateInfo(FloorControl.States.valueOf("LightOn"),
            new ArrayList<>(), "control.floor4"));

    Set<StateInfo> visitedStatesCompareController = new HashSet<>();
    Set<StateInfo> visitedStatesCompareDoor = new HashSet<>();
    Set<StateInfo> visitedStatesCompareLift = new HashSet<>();
    Set<StateInfo> visitedStatesCompareSplitter = new HashSet<>();
    Set<StateInfo> visitedStatesCompareFloor1 = new HashSet<>();
    Set<StateInfo> visitedStatesCompareFloor2 = new HashSet<>();
    Set<StateInfo> visitedStatesCompareFloor3 = new HashSet<>();
    Set<StateInfo> visitedStatesCompareFloor4 = new HashSet<>();

    int missedEnumStats = 0;

    // visited states are divided into the corresponding set of enumStates
    for (StatesList states : visitedStates) {
      for (StateInfo state : states.getStates()) {
        switch (state.getComponent()) {
          case "elevator.ctrl":
            visitedEnumsController.add(Controller.States.valueOf(state.getState()
                    .name()));
            break;
          case "elevator.door":
            visitedEnumsDoor.add(Door.States.valueOf(state.getState()
                    .name()));
            break;
          case "elevator.lift":
            visitedEnumsLift.add(Lift.States.valueOf(state.getState().name()));
            break;
          case "control.splitter":
            visitedEnumsSplitter.add(Splitter.States.valueOf(state.getState().name()));
            break;
          case "control.floor1":
            visitedEnumsFloor1.add(FloorControl.States.valueOf(state.getState().name()));
            break;
          case "control.floor2":
            visitedEnumsFloor2.add(FloorControl.States.valueOf(state.getState().name()));
            break;
          case "control.floor3":
            visitedEnumsFloor3.add(FloorControl.States.valueOf(state.getState().name()));
            break;
          case "control.floor4":
            visitedEnumsFloor4.add(FloorControl.States.valueOf(state.getState().name()));
            break;
        }
      }
    }

    missedEnumStats += getMissedEnumStates(visitedEnumsController, Arrays.stream(Controller.States.values()).filter(value -> value != Controller.States.valueOf("Init") && value != Controller.States.valueOf("WaitReq")).collect(Collectors.toList()));
    missedEnumStats += getMissedEnumStates(visitedEnumsDoor, Arrays.asList(Door.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsLift, Arrays.asList(Lift.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsSplitter, Arrays.asList(Splitter.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsFloor1, Arrays.asList(FloorControl.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsFloor2, Arrays.asList(FloorControl.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsFloor3, Arrays.asList(FloorControl.States.values()));
    missedEnumStats += getMissedEnumStates(visitedEnumsFloor4, Arrays.asList(FloorControl.States.values()));

    {
      /* visitedEnumStates are evaluated with -1 if enum states were visited that were not
       * included in the expected enum states.
       * the controller component contains two superstates that cannot be visited directly,
       * so they are excluded from the size of the visitable states.
       */
      int sizeOfAllStates =
              (Controller.States.values().length - 2) + Door.States.values().length + Lift.States.values()
                      .length + Splitter.States.values().length + (4 * FloorControl.States.values().length);

      int visitedEnumStates =
              visitedEnumsLift.size() + visitedEnumsSplitter.size() + visitedEnumsDoor.size() + visitedEnumsController.size()
                      + visitedEnumsFloor1.size() + visitedEnumsFloor2.size() + visitedEnumsFloor3.size() + visitedEnumsFloor4.size();

      resultVisitedEnumStates(row, controller, sheetBigModel, missedEnumStats, sizeOfAllStates, visitedEnumStates);
    }

    //evaluation completeness -  states (enums + internal states)
    int missedStates = 0;

    // visited states are divided into the corresponding set of states
    for (StatesList states : visitedStates) {
      for (StateInfo state : states.getStates()) {
        switch (state.getComponent()) {
          case "elevator.ctrl":
            visitedStatesCompareController.add(state);
            break;
          case "elevator.door":
            visitedStatesCompareDoor.add(state);
            break;
          case "elevator.lift":
            visitedStatesCompareLift.add(state);
            break;
          case "control.splitter":
            visitedStatesCompareSplitter.add(state);
            break;
          case "control.floor1":
            visitedStatesCompareFloor1.add(state);
            break;
          case "control.floor2":
            visitedStatesCompareFloor2.add(state);
            break;
          case "control.floor3":
            visitedStatesCompareFloor3.add(state);
            break;
          case "control.floor4":
            visitedStatesCompareFloor4.add(state);
            break;
        }
      }
    }

    missedStates += getMissedStates(expectedStatesController, visitedStatesCompareController);
    missedStates += getMissedStates(expectedStatesDoor, visitedStatesCompareDoor);
    missedStates += getMissedStates(expectedStatesLift, visitedStatesCompareLift);
    missedStates += getMissedStates(expectedStatesSplitter, visitedStatesCompareSplitter);
    missedStates += getMissedStates(expectedStatesFloor1, visitedStatesCompareFloor1);
    missedStates += getMissedStates(expectedStatesFloor2, visitedStatesCompareFloor2);
    missedStates += getMissedStates(expectedStatesFloor3, visitedStatesCompareFloor3);
    missedStates += getMissedStates(expectedStatesFloor4, visitedStatesCompareFloor4);

    {
      /* numberOfVisitedStates are evaluated with -1 if states were visited that were not
       * included in the expected states.
       */
      int sizeOfAllStates =
              expectedStatesDoor.size() + expectedStatesController.size() + expectedStatesLift.size() + expectedStatesSplitter.size()
                      + expectedStatesFloor1.size() + expectedStatesFloor2.size() + expectedStatesFloor3.size() + expectedStatesFloor4.size();

      int numberOfVisitedStates =
              visitedStatesCompareLift.size() + visitedStatesCompareSplitter.size() + visitedStatesCompareDoor.size() + visitedStatesCompareController.size()
                      + visitedStatesCompareFloor1.size() + visitedStatesCompareFloor2.size() + visitedStatesCompareFloor3.size() + visitedStatesCompareFloor4.size();

      // last argument is 0 because no states need to be exculde of the safety check
      resultVisitedStates(row, controller, sheetBigModel, missedStates, numberOfVisitedStates, sizeOfAllStates, 0);
    }

    //check existence of redundant paths
    Set<Pair<List<ListerI>, Expr<BoolSort>>> output = new HashSet<>();

    // extract the simplified output with the corresponding branchConditions
    for (InputAndCondition<List<ListerInElevatorSystem>,
            List<ListerOutElevatorSystem>> temp : condition) {
      List<ListerI> listOutSimplified = new ArrayList<>();
      for (ListerOutElevatorSystem temps : temp.getOutput()) {
        listOutSimplified.add(temps.getExpression());
      }
      output.add(ImmutablePair.of(listOutSimplified, temp.getBranches().getBranchConditions()
              .simplify()));
    }

    // get number of redundant paths
    int redundantPaths = getRedundantPaths(output);

    System.out.println(controller + ": The model has " + redundantPaths + " redundant paths");

    fillCell(sheetBigModel, row, 10, redundantPaths);

    {
      /* set local variables to null to release them for the garbage collector and trigger it
       * this is done for performance reasons, with large models the evaluation generates a lot
       * of data
       * the last part of the evaluation is very computationally intensive, to improve this part
       * all unused data is deleted
       */
      result = null;
      interestingInputs = null;
      simplifiedIInputs = null;
      visitedStates = null;
      visitedEnumsDoor = null;
      visitedEnumsFloor1 = null;
      visitedEnumsFloor2 = null;
      visitedEnumsFloor3 = null;
      visitedEnumsFloor4 = null;
      visitedEnumsLift = null;
      visitedEnumsSplitter = null;
      visitedEnumsController = null;
      visitedStatesCompareDoor = null;
      visitedStatesCompareFloor1 = null;
      visitedStatesCompareFloor2 = null;
      visitedStatesCompareFloor3 = null;
      visitedStatesCompareFloor4 = null;
      visitedStatesCompareLift = null;
      visitedStatesCompareSplitter = null;
      visitedStatesCompareController = null;
      expectedStatesDoor = null;
      expectedStatesFloor1 = null;
      expectedStatesFloor2 = null;
      expectedStatesFloor3 = null;
      expectedStatesFloor4 = null;
      expectedStatesLift = null;
      expectedStatesSplitter = null;
      expectedStatesController = null;
      allTransitions = null;
      output = null;
      System.gc();
    }

    // check if component is non-deterministic
    if (calculateNonDetPaths) {
      int nonDetPaths = checkNumberOfNonDeterminism(condition, ctx);
      System.out.println(controller + ": The component has " + nonDetPaths + " non-deterministic " +
              "paths");
      fillCell(sheetBigModel, row, 11, nonDetPaths);
    } else {
      boolean nonDeterministic = checkNonDeterminism(condition, ctx);
      if (nonDeterministic) {
        System.out.println(controller + ": The component has non-deterministic paths");
      } else {
        System.out.println(controller + ": The component has only deterministic paths");
      }
      fillCell(sheetBigModel, row, 11, String.valueOf(nonDeterministic));
    }
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedStatesLift() {
    Set<StateInfo> expectedStatesLift = new HashSet<>();

    expectedStatesLift.add(StateInfo.newStateInfo(Lift.States.valueOf("Wait"),
            new ArrayList<>(), "elevator.lift"));
    expectedStatesLift.add(StateInfo.newStateInfo(Lift.States.valueOf("Up"),
            new ArrayList<>(), "elevator.lift"));
    expectedStatesLift.add(StateInfo.newStateInfo(Lift.States.valueOf("Down"),
            new ArrayList<>(), "elevator.lift"));

    return expectedStatesLift;
  }

  /**
   * creates a set of Strings, used to define the expected transitions of the big model
   */
  private Set<String> setTransitionsBM() {

    Set<String> allTransitions = new HashSet<>();

    // Splitter component
    allTransitions.add("control.splitterFromIdleToIdle0");
    allTransitions.add("control.splitterFromIdleToIdle1");
    allTransitions.add("control.splitterFromIdleToIdle2");
    allTransitions.add("control.splitterFromIdleToIdle3");
    allTransitions.add("control.splitterFromIdleToIdle4");

    // Lift component
    allTransitions.add("elevator.liftFromWaitToWait0");
    allTransitions.add("elevator.liftFromWaitToUp1");
    allTransitions.add("elevator.liftFromWaitToDown2");
    allTransitions.add("elevator.liftFromUpToWait0");
    allTransitions.add("elevator.liftFromUpToUp1");
    allTransitions.add("elevator.liftFromUpToDown2");
    allTransitions.add("elevator.liftFromDownToWait0");
    allTransitions.add("elevator.liftFromDownToUp1");
    allTransitions.add("elevator.liftFromDownToDown2");

    // Floor1 component
    allTransitions.add("control.floor1FromLightOffToLightOff0");
    allTransitions.add("control.floor1FromLightOffToLightOn1");
    allTransitions.add("control.floor1FromLightOnToLightOn0");
    allTransitions.add("control.floor1FromLightOnToLightOff1");

    // Floor2 component
    allTransitions.add("control.floor2FromLightOffToLightOff0");
    allTransitions.add("control.floor2FromLightOffToLightOn1");
    allTransitions.add("control.floor2FromLightOnToLightOn0");
    allTransitions.add("control.floor2FromLightOnToLightOff1");

    // Floor3 component
    allTransitions.add("control.floor3FromLightOffToLightOff0");
    allTransitions.add("control.floor3FromLightOffToLightOn1");
    allTransitions.add("control.floor3FromLightOnToLightOn0");
    allTransitions.add("control.floor3FromLightOnToLightOff1");

    // Floor4 component
    allTransitions.add("control.floor4FromLightOffToLightOff0");
    allTransitions.add("control.floor4FromLightOffToLightOn1");
    allTransitions.add("control.floor4FromLightOnToLightOn0");
    allTransitions.add("control.floor4FromLightOnToLightOff1");

    // Door component
    allTransitions.add("elevator.doorFromWaitToWait0");
    allTransitions.add("elevator.doorFromWaitToCloseDoor1");
    allTransitions.add("elevator.doorFromCloseDoorToCloseDoor0");
    allTransitions.add("elevator.doorFromCloseDoorToDoorIsClosed1");
    allTransitions.add("elevator.doorFromCloseDoorToOpenDoor2");
    allTransitions.add("elevator.doorFromCloseDoorToOpenDoor3");
    allTransitions.add("elevator.doorFromDoorIsClosedToDoorIsClosed0");
    allTransitions.add("elevator.doorFromDoorIsClosedToOpenDoor1");
    allTransitions.add("elevator.doorFromOpenDoorToOpenDoor0");
    allTransitions.add("elevator.doorFromOpenDoorToDoorIsOpen1");
    allTransitions.add("elevator.doorFromDoorIsOpenToDoorIsOpen0");
    allTransitions.add("elevator.doorFromDoorIsOpenToCloseDoor1");

    // Controller component
    allTransitions.add("elevator.ctrlFromWaitTimerToWaitTimer0");
    allTransitions.add("elevator.ctrlFromWaitTimerToCloseDoor1");
    allTransitions.add("elevator.ctrlFromCloseDoorToCloseDoor0");
    allTransitions.add("elevator.ctrlFromCloseDoorToOK1");
    allTransitions.add("elevator.ctrlFromCloseDoorToDriveDown2");
    allTransitions.add("elevator.ctrlFromDriveDownToDriveDown0");
    allTransitions.add("elevator.ctrlFromDriveDownToOK1");
    allTransitions.add("elevator.ctrlFromOKToWaitReqNoGuard0");
    allTransitions.add("elevator.ctrlFromSearchFloor1ToSearchFloor20");
    allTransitions.add("elevator.ctrlFromSearchFloor1ToSearchFloor41");
    allTransitions.add("elevator.ctrlFromSearchFloor1ToFound2");
    allTransitions.add("elevator.ctrlFromSearchFloor2ToSearchFloor30");
    allTransitions.add("elevator.ctrlFromSearchFloor2ToSearchFloor11");
    allTransitions.add("elevator.ctrlFromSearchFloor2ToFound2");
    allTransitions.add("elevator.ctrlFromSearchFloor3ToSearchFloor40");
    allTransitions.add("elevator.ctrlFromSearchFloor3ToSearchFloor21");
    allTransitions.add("elevator.ctrlFromSearchFloor3ToFound2");
    allTransitions.add("elevator.ctrlFromSearchFloor4ToSearchFloor10");
    allTransitions.add("elevator.ctrlFromSearchFloor4ToSearchFloor31");
    allTransitions.add("elevator.ctrlFromSearchFloor4ToFound2");
    allTransitions.add("elevator.ctrlFromFoundToContinue0");
    allTransitions.add("elevator.ctrlFromFoundToContinue1");
    allTransitions.add("elevator.ctrlFromFoundToContinue2");
    allTransitions.add("elevator.ctrlFromContinueToFloor10");
    allTransitions.add("elevator.ctrlFromContinueToFloor21");
    allTransitions.add("elevator.ctrlFromContinueToFloor32");
    allTransitions.add("elevator.ctrlFromContinueToFloor43");
    allTransitions.add("elevator.ctrlFromFloor1ToDoor10");
    allTransitions.add("elevator.ctrlFromFloor1ToFloor21");
    allTransitions.add("elevator.ctrlFromFloor1ToFloor22");
    allTransitions.add("elevator.ctrlFromFloor2ToDoor20");
    allTransitions.add("elevator.ctrlFromFloor2ToFloor31");
    allTransitions.add("elevator.ctrlFromFloor2ToFloor32");
    allTransitions.add("elevator.ctrlFromFloor2ToFloor13");
    allTransitions.add("elevator.ctrlFromFloor3ToDoor30");
    allTransitions.add("elevator.ctrlFromFloor3ToFloor41");
    allTransitions.add("elevator.ctrlFromFloor3ToFloor22");
    allTransitions.add("elevator.ctrlFromFloor3ToFloor23");
    allTransitions.add("elevator.ctrlFromFloor4ToDoor40");
    allTransitions.add("elevator.ctrlFromFloor4ToFloor31");
    allTransitions.add("elevator.ctrlFromFloor4ToFloor32");
    allTransitions.add("elevator.ctrlFromDoor1ToDoor10");
    allTransitions.add("elevator.ctrlFromDoor1ToSearchFloor11");
    allTransitions.add("elevator.ctrlFromDoor2ToDoor20");
    allTransitions.add("elevator.ctrlFromDoor2ToSearchFloor21");
    allTransitions.add("elevator.ctrlFromDoor3ToDoor30");
    allTransitions.add("elevator.ctrlFromDoor3ToSearchFloor31");
    allTransitions.add("elevator.ctrlFromDoor4ToDoor40");
    allTransitions.add("elevator.ctrlFromDoor4ToSearchFloor41");

    return allTransitions;
  }

  /**
   * creates a set of Strings, used to define the expected transitions of the small model
   */
  private Set<String> setTransitionsSM() {
    Set<String> allTransitions = new HashSet<>();

    allTransitions.add("counterMDSEFromIdleToIdleNoGuard0");
    allTransitions.add("counterSAFromIdleToIdleNoGuard0");

    allTransitions.add("distinctionFromIdleToIdle0");
    allTransitions.add("distinctionFromIdleToIdle1");

    allTransitions.add("evaluationFrommdseTomdse0");
    allTransitions.add("evaluationFrommdseTosa1");
    allTransitions.add("evaluationFromsaTosa0");
    allTransitions.add("evaluationFromsaTomdse1");
    allTransitions.add("evaluationFromnonModuleTomdse0");
    allTransitions.add("evaluationFromnonModuleTosa1");
    allTransitions.add("evaluationFrommdseTononModule2");
    allTransitions.add("evaluationFromsaTononModule2");
    allTransitions.add("evaluationFromnonModuleTononModule2");

    return allTransitions;
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedStatesDoor() {
    Set<StateInfo> expectedStatesDoor = new HashSet<>();

    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("Wait"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0)), 2.5>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("Wait"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0)), 1.25>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("Wait"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("Wait"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("CloseDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("CloseDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("OpenDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<3, 3.0>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsClosed"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsClosed"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("OpenDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<10, 10.0>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<10, 10.0>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0)), 5.0>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0)), 2.5>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 1.25>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsOpen"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));


    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("CloseDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("CloseDoor"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));

    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsClosed"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0), 0.625>")), "elevator.door"));
    expectedStatesDoor.add(StateInfo.newStateInfo(Door.States.valueOf("DoorIsClosed"),
            new ArrayList<>(Collections.singleton("timer: " +
                    "<(* 10.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.3125>")), "elevator.door"));


    return expectedStatesDoor;
  }

  /**
   * creates a set of StateInfo, used to define the expected states
   */
  private Set<StateInfo> fillExpectedSatesController() {
    Set<StateInfo> expectedStatesController = new HashSet<>();

    List<String> boolList = Arrays.asList("true", "false");
    List<String> enumList = Arrays.asList("UP", "DOWN");
    List<Enum<Controller.States>> excludedStates = Arrays.asList(Controller.States.valueOf(
                    "WaitTimer"), Controller.States.valueOf("CloseDoor"), Controller.States.valueOf("OK"),
            Controller.States.valueOf("DriveDown"), Controller.States.valueOf("Init"),
            Controller.States.valueOf("WaitReq"));

    /* every possible combination of EnumState and InternalStates are created
     * for the internal states is every combination possible in the range of 0 to 5
     */
    for (Enum<Controller.States> state : Controller.States.values()) {
      if (!excludedStates.contains(state)) {
        for (String enumElement : enumList) {
          for (String boolElement : boolList) {
            for (int i = 0; i < 5; i++) {
              for (int j = 1; j < 5; j++) {
                expectedStatesController.add(StateInfo.newStateInfo(state,
                        new ArrayList<>(Arrays.asList("directions: <" + enumElement + "," + enumElement + ">", "current: " + "<" + j + "," + j + ">", "target: " + "<" + i + "," + i + ">", "timer: " + "<(* 5.0" +
                                        " (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0.625>",
                                "stopNext: " + "<" + boolElement + "," + boolElement + ">")), "elevator.ctrl"));
              }
            }
          }
        }
      }
    }

    // these are the remaining possible states
    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("WaitTimer"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<5.0, 5.0>", "stopNext: " +
                    "<false, false>")), "elevator.ctrl"));

    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("WaitTimer"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0)), 2.5>", "stopNext: " +
                    "<false, false>")), "elevator.ctrl"));

    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("WaitTimer"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0)), 1.25>", "stopNext: " +
                    "<false, false>")), "elevator.ctrl"));
    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("WaitTimer"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0" +
                    ".625>", "stopNext: " + "<false, false>")), "elevator.ctrl"));
    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("CloseDoor"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0" +
                    ".625>", "stopNext: " + "<false, false>")), "elevator.ctrl"));

    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("DriveDown"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0" +
                    ".625>", "stopNext: " + "<false, false>")), "elevator.ctrl"));
    expectedStatesController.add(StateInfo.newStateInfo(Controller.States.valueOf("OK"),
            new ArrayList<>(Arrays.asList("directions: " + "<NA, NA>", "current: " + "<0, 0>", "target:" +
                    " " + "<0, 0>", "timer: " + "<(* 5.0 (/ 1.0 2.0) (/ 1.0 2.0) (/ 1.0 2.0)), 0" +
                    ".625>", "stopNext: " + "<false, false>")), "elevator.ctrl"));

    return expectedStatesController;
  }

  /**
   * This function calculates the number of non-deterministic paths in the analyzed model.
   * All paths are checked for equality and, in the case of inequality, for satisfiability.
   * Satisfiability is checked for the unequal partial expressions. These comparisons are very
   * time-consuming. Therefore, it is not recommended to use this function for large models with
   * large input lengths. For the 'bigModel' with an input length of 3, the number of
   * non-deterministic paths can be calculated in a reasonable amount of time. With an input
   * length of 4, this is no longer the case, and the calculation is still not finished after 6
   * hours.
   * For a desired execution of the function in the evaluation, the value of
   * "calculateNonDetPaths" must be set to "true".
   */
  public int checkNumberOfNonDeterminism(Set<InputAndCondition> inputAndConditions, Context ctx) throws ExecutionException, InterruptedException {
    int numberOfNonDetPaths = 0;
    List<List<Pair<Integer, Expr<?>>>> pathConditions = new ArrayList<>();

    for (InputAndCondition temp : inputAndConditions) {
      BoolExpr expr = temp.getBranches().getBranchConditions();
      List<Pair<Integer, Expr<?>>> individualConditions = new ArrayList<>();
      individualConditions.addAll(splitConditions(expr));

      // the first expr is removed because each path starts with the expr true
      individualConditions.remove(0);
      pathConditions.add(individualConditions);
      System.gc();
    }

    /*
     * is set to null to allow the garbage collector to free memory and thus improve performance
     * during the
     * because very large data structures can occur
     */
    inputAndConditions = null;

    List<List<Pair<Integer, Expr<?>>>> toBeDeleted = new ArrayList<>(pathConditions);
    List<List<Pair<Integer, Expr<?>>>> nonDetPaths = new ArrayList<>();

    for (List<Pair<Integer, Expr<?>>> path : pathConditions) {
      toBeDeleted.remove(path);
      toBeDeleted.removeAll(nonDetPaths);
      nonDetPaths.clear();

      for (List<Pair<Integer, Expr<?>>> toBe : toBeDeleted) {
        int i = 0;
        for (Pair<Integer, Expr<?>> expr : toBe) {

          if (i < path.size()) {
            int exprHashCode = (int) expr.getKey();
            int pathHashCode = (int) path.get(i).getKey();
            if (exprHashCode != pathHashCode) {
              Solver solver = ctx.mkSolver();

              solver.add((BoolExpr) toBe.get(i).getValue());
              solver.add((BoolExpr) path.get(i).getValue());
              Status status = solver.check();
              if (status == Status.SATISFIABLE) {
                numberOfNonDetPaths++;
                nonDetPaths.add(toBe);
              }
              solver = null;
              System.gc();
              break;
            }
          }
          if (i == toBe.size() - 1 && i == path.size() - 1) {
            numberOfNonDetPaths++;
            nonDetPaths.add(toBe);
          }
          i++;
          System.gc();
        }
      }
    }

    /*
     * required, since the first path that finds a non-deterministic partner is not counted,
     * since only the partner is counted
     */
    if (numberOfNonDetPaths > 0) {
      numberOfNonDetPaths++;
    }
    return numberOfNonDetPaths;
  }

  /**
   * This function calculates if the analyzed models have non-deterministic paths.
   * It returns true if a non-deterministic path is found. Since this function uses the
   * same algorithm as 'checkNumberOfNonDeterminism', it is possible that the calculation
   * cannot be done in a reasonable time if the model is large and deterministic.
   */
  public boolean checkNonDeterminism(Set<InputAndCondition> inputAndConditions, Context ctx) {
    List<List<Pair<Integer, Expr<?>>>> pathConditions = new ArrayList<>();

    for (InputAndCondition temp : inputAndConditions) {
      BoolExpr expr = temp.getBranches().getBranchConditions();
      List<Pair<Integer, Expr<?>>> individualConditions = new ArrayList<>();
      individualConditions.addAll(splitConditions(expr));

      // the first expr is removed because each path starts with the expr true
      individualConditions.remove(0);
      pathConditions.add(individualConditions);
      System.gc();
    }

    /*
     * is set to null to allow the garbage collector to free memory and thus improve performance
     * during the
     * because very large data structures can occur
     */
    inputAndConditions = null;

    List<List<Pair<Integer, Expr<?>>>> toBeDeleted = new ArrayList<>(pathConditions);

    for (List<Pair<Integer, Expr<?>>> path : pathConditions) {
      toBeDeleted.remove(path);

      for (List<Pair<Integer, Expr<?>>> toBe : toBeDeleted) {
        int i = 0;

        for (Pair<Integer, Expr<?>> expr : toBe) {
          if (i < path.size()) {
            int exprHashCode = (int) expr.getKey();
            int pathHashCode = (int) path.get(i).getKey();
            if (exprHashCode != pathHashCode) {
              Solver solver = ctx.mkSolver();

              solver.add((BoolExpr) toBe.get(i).getValue());
              solver.add((BoolExpr) path.get(i).getValue());
              Status status = solver.check();
              if (status == Status.SATISFIABLE) {
                return true;
              }
              solver = null;
              System.gc();
              break;
            }
          }
          if (i == toBe.size() - 1 && i == path.size() - 1) {
            return true;
          }
          i++;
          System.gc();
        }
      }
    }
    return false;
  }

  /**
   * separates a BoolExpr into individual BoolExpr. The separation happens between the and-components.
   */
  public List<Pair<Integer, Expr<?>>> splitConditions(BoolExpr input) {
    List<Pair<Integer, Expr<?>>> individualConditions = new ArrayList<>();

    if (input == null) {
      return individualConditions;
    }
    List<Expr<?>> conditions = Arrays.asList(input.getArgs());

    ListIterator<Expr<?>> iterator = conditions.listIterator(0);

    // add input manually if it is not a compound expression
    if (input.getNumArgs() == 0) {
      individualConditions.add(ImmutablePair.of(input.toString().hashCode(), input));
      return individualConditions;
    }

    // decompose the input
    while (iterator.hasNext()) {
      Expr<?> conditionExpr = iterator.next();
      if (conditionExpr.getNumArgs() > 0 && conditionExpr.isAnd()) {
        BoolExpr boolExpr = (BoolExpr) conditionExpr;
        individualConditions.addAll(splitConditions(boolExpr));
      } else {
        individualConditions.add(ImmutablePair.of(conditionExpr.toString()
                .hashCode(), conditionExpr));
      }
    }
    return individualConditions;
  }

  /**
   * evaluates how many transitions included in 'allTransitions' are in 'condition' included
   */
  private int evaluateTransitions(Set<InputAndCondition> condition, Set<String> allTransitions) {
    Set<String> takenTransitions = new HashSet<>();
    for (InputAndCondition<ListerI, ListerI> temp : condition) {
      takenTransitions.addAll(temp.getBranches().getBranchIds());
    }

    int transitions = 0;
    for (String transition : allTransitions) {
      if (!takenTransitions.contains(transition)) {
        transitions++;
      }
    }

    /*
     * transitions are set to -1 if transitions were taken that were not expected.
     */
    if (transitions + takenTransitions.size() != allTransitions.size()) {
      return -1;
    }

    return takenTransitions.size();
  }

  /**
   * helper method for creating an Excel table
   */
  private void createCell(HSSFSheet sheet, int rowNumber, int cellNumber, String input) {
    HSSFRow row = sheet.createRow(rowNumber);
    HSSFCell cell = row.createCell(cellNumber);
    cell.setCellValue(input);
  }

  /**
   * helper method for creating an Excel table
   */
  private void fillCell(HSSFSheet sheet, int rowNumber, int cellNumber, Integer input) {
    HSSFRow row = sheet.getRow(rowNumber);
    HSSFCell cell = row.createCell(cellNumber);
    cell.setCellValue(input);
  }

  /**
   * helper method for creating an Excel table
   */
  private void fillCell(HSSFSheet sheet, int rowNumber, int cellNumber, String input) {
    HSSFRow row = sheet.getRow(rowNumber);
    HSSFCell cell = row.createCell(cellNumber);
    cell.setCellValue(input);
  }

  /**
   * helper method for creating an Excel table
   */
  private void fillCell(HSSFSheet sheet, int rowNumber, int cellNumber, Long input) {
    HSSFRow row = sheet.getRow(rowNumber);
    HSSFCell cell = row.createCell(cellNumber);
    cell.setCellValue(input);
  }
}

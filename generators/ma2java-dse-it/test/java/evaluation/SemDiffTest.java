/* (c) https://github.com/MontiCore/monticore */
package evaluation;

import automata.evaluation.smallModel.DSEMainSemDiffSmallModel;
import automata.evaluation.smallModel.DSESmallModel;
import automata.evaluation.smallModel.ListerInSemDiffSmallModel;
import automata.evaluation.smallModel.ListerInSmallModel;
import automata.evaluation.smallModel.ListerOutSemDiffSmallModel;
import automata.evaluation.smallModel.ListerOutSmallModel;
import automata.evaluation.smallModel.ListerParameterSemDiffSmallModel;
import automata.evaluation.smallModel.ListerParameterSmallModel;
import com.google.common.base.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import results.ResultSemDiff;
import semDiff.SemDiffController;
import evaluation.helper.SemDiffHelperSmallModel;

import java.util.List;

public class SemDiffTest {

  // input length to be used in the SemDiffTest
  private int inputLength = 0;

  // parameters needed for the components
  private String parameters = "400000";

  /***
   * This function computes the semantic difference between the two models smallModel and SemDiffSmallModel
   *
   * SemDiffSmallModel is used as the first component because it has input-output pairs that are not
   * reproducible in SmallModel.
   */
  @Test
  public void SemDiffTest() throws Exception {

    /***
     * Definition of helper function converter for SemDiffSmallModel
     *
     * The converter filters the inputs from a result pair of the first component and converts
     * them to the inputType of the second component
     */
    Function<Pair<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
            List<ListerOutSemDiffSmallModel>>, Pair<List<ListerInSmallModel>, ListerParameterSmallModel>>
            converter = SemDiffHelperSmallModel::convertInput;

    /***
     * Definition of helper function getResultEntries1 for SemDiffSmallModel
     *
     * This function converts the output of the first component to a list of strings
     */

    Function<Pair<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
            List<ListerOutSemDiffSmallModel>>, List<String>>
            getEntriesResult1 = SemDiffHelperSmallModel::getResultEntries1;

    /***
     * Definition of helper function getResultEntries2
     *
     * This function converts the output of the second component to a list of strings.
     */
    Function<List<ListerOutSmallModel>, List<String>> getEntriesResult2
            = SemDiffHelperSmallModel::getResultEntries2;

    /***
     * Definition of function runOnce for SemDiffSmallModel
     *
     * This function is generated and controls a run of the specified model
     */
    Function<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
            List<ListerOutSmallModel>> runOnce = DSESmallModel::runOnce;


    // Initialisation of semDiffController
    SemDiffController<Pair<List<ListerInSemDiffSmallModel>,
            ListerParameterSemDiffSmallModel>, List<ListerOutSemDiffSmallModel>,
            Pair<List<ListerInSmallModel>, ListerParameterSmallModel>, List<ListerOutSmallModel>>
            semDiffController = new SemDiffController<>();

    // Instantiation of SemDiffSmallModel
    DSEMainSemDiffSmallModel diffSmallModel = new DSEMainSemDiffSmallModel();

    // For evaluation purposes, the runtime of the SemDiffController is measured
    final long timeStartNano = System.nanoTime();

    // Set up semDiffController
    semDiffController.setUp(converter, getEntriesResult1, getEntriesResult2, diffSmallModel, runOnce);

    /***
     * Start semDiff calculation
     *
     * The first parameter of this function is the input length
     * (for the SmallModel and SemDiffSmallModel comparison, an input length of three is required to calculate
     * any semantic differences)
     *
     * The second parameter is a string containing all the parameters needed by the models
     * (e.g., both models are initialized with parameter 400000)
     */
    ResultSemDiff<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
            List<ListerOutSemDiffSmallModel>>
            result = semDiffController.startSemDiff(inputLength, parameters);

    // For evaluation purposes, the runtime of the SemDiffController is measured
    final long timeEndNano = System.nanoTime();

    // Print runtime of SemDiff calculation
    System.err.println("time in ns: " + (timeEndNano - timeStartNano));

    // Print the semantic difference of SmallModel and SemDiffSmallModel
    for (Pair<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
            List<ListerOutSemDiffSmallModel>> semDiff : result.getSemDiff()) {

      for (int i = 0; i < semDiff.getLeft().getLeft().size(); i++) {

        System.out.println("semDiff_inModule : "
                + semDiff.getLeft().getLeft().get(i).getmodule().getValue().getExpr().toString()
                + " value : " + semDiff.getLeft().getLeft().get(i).getmodule().getValue().getValue());
        System.out.println("semDiff_inMtrNr : "
                + semDiff.getLeft().getLeft().get(i).getmtrNr().getValue().getExpr().toString()
                + " value : " + semDiff.getLeft().getLeft().get(i).getmtrNr().getValue().getValue());
        System.out.println("semDiff_outSA : "
                + semDiff.getRight().get(i).getvoteSA().getValue().getExpr().toString()
                + " value : " + semDiff.getRight().get(i).getvoteSA().getValue().getValue());
        System.out.println("semDiff_outMBSE : "
                + semDiff.getRight().get(i).getvoteMBSE().getValue().getExpr().toString()
                + " value : " + semDiff.getRight().get(i).getvoteMBSE().getValue().getValue());
      }
    }
    System.out.println(result.getSemDiff().size() + " semantic differences were found");

    System.out.println("solver calls: " + semDiffController.getSolverCalls());
    System.out.println("sat paths: " + semDiffController.getSatPaths());
  }
}
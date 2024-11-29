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
import semDiff.SemDiffHelperSmallModel;

import java.util.List;

public class SemDiffTest {

  // computation of the semantic difference between two models
  @Test
  public void SemDiffTest() throws Exception {

    // semdiff for SmallModelSemDiff and smallModel

    //define helper functions
    Function<Pair<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
      List<ListerOutSemDiffSmallModel>>, Pair<List<ListerInSmallModel>, ListerParameterSmallModel>>
      converter = SemDiffHelperSmallModel::convertInput;

    Function<Pair<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
      List<ListerOutSemDiffSmallModel>>, List<String>>
      getEntriesResult1 = SemDiffHelperSmallModel::getResultEntries1;

    Function<Pair<List<ListerInSmallModel>, ListerParameterSmallModel>,
      List<ListerOutSmallModel>> runOnce = DSESmallModel::runOnce;

    // initialise semDiffController
    Function<List<ListerOutSmallModel>, List<String>> getEntriesResult2
      = SemDiffHelperSmallModel::getResultEntries2;
    SemDiffController<Pair<List<ListerInSemDiffSmallModel>,
      ListerParameterSemDiffSmallModel>, List<ListerOutSemDiffSmallModel>,
      Pair<List<ListerInSmallModel>, ListerParameterSmallModel>, List<ListerOutSmallModel>>
      semDiffController = new SemDiffController<>();

    DSEMainSemDiffSmallModel diffSmallModel = new DSEMainSemDiffSmallModel();

    //  final long timeStartMili = System.currentTimeMillis();
    final long timeStartNano = System.nanoTime();

    //start semDiffController
    semDiffController.setUp(converter, getEntriesResult1, getEntriesResult2, diffSmallModel, runOnce);

    ResultSemDiff<Pair<List<ListerInSemDiffSmallModel>, ListerParameterSemDiffSmallModel>,
      List<ListerOutSemDiffSmallModel>>
      result = semDiffController.startSemDiff(0, "400000");

    //   final long timeEndMili = System.currentTimeMillis();
    final long timeEndNano = System.nanoTime();

    // evaluation time
    System.err.println("time in ns: " + (timeEndNano - timeStartNano));

    // print the semantic difference
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
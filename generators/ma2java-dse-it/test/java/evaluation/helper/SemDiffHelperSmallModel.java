/* (c) https://github.com/MontiCore/monticore */
package evaluation.helper;

import automata.evaluation.smallModel.*;
import montiarc.rte.dse.ListerI;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SemDiffHelperSmallModel {

  /**
   * filters out the inputs from a result pair of the first component and converts them to the
   * inputType of the second component
   *
   * @param inSemDiffSmallModel Result of the first component (SemDiffSmallModel)
   * @return inputType of the second component (SmallModel)
   */
  public static Pair<List<ListerInSmallModel>, ListerParameterSmallModel> convertInput(
          Pair<Pair<List<ListerInSemDiffSmallModel>,
                  ListerParameterSemDiffSmallModel>,
                  List<ListerOutSemDiffSmallModel>> inSemDiffSmallModel) {
    List<ListerInSmallModel> resIn = new ArrayList<>();
    for (ListerInSemDiffSmallModel inSmall : inSemDiffSmallModel.getLeft().getLeft()) {
      ListerInSmallModel inSmallModel = new ListerInSmallModel(inSmall.getmodule(), inSmall.getmtrNr());
      resIn.add(inSmallModel);
    }
    return ImmutablePair.of(resIn,
            new ListerParameterSmallModel(inSemDiffSmallModel.getLeft().getRight().getparameter()));
  }

  /**
   * converts the output of the first component (SemDiffSmallModel) to a list of strings.
   * The expressions of the output are simplified.
   *
   * @param input is the output of the first component SemDiffSmallModel
   * @return List of strings of the output
   */
  public static List<String> getResultEntries1(Pair<Pair<List<ListerInSemDiffSmallModel>,
          ListerParameterSemDiffSmallModel>, List<ListerOutSemDiffSmallModel>> input) {
    List<ListerOutSemDiffSmallModel> result = new ArrayList<>();
    result.addAll(input.getRight());

    List<String> result2 = new ArrayList<>();
    for (ListerOutSemDiffSmallModel model : result) {
      result2.add(model.getExpression().getEntries());
    }
    return result2;
  }

  /**
   * converts the output of the second component to a list of strings.
   * The expressions of the output are simplified.
   *
   * @param input is the output of the second component SmallModel
   * @return List of strings of the output
   */
  public static List<String> getResultEntries2(List<ListerOutSmallModel> input) {
    List<String> result = new ArrayList<>();
    for (ListerOutSmallModel model : input) {
      result.add(model.getExpression().getEntries());
    }
    return result;
  }
}
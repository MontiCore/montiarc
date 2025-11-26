/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.EnumSort;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestController {

  protected static TestControllerI controller;

  protected static Map<String, EnumSort<?>> enumSorts;

  public static void init(TestControllerI controller) {
    TestController.controller = controller;
    enumSorts = new LinkedHashMap<>();
  }

  public static TestControllerI getController() {
    return TestController.controller;
  }

  public static boolean shouldEndRun() {
    return controller.shouldEndRun();
  }

  public static boolean getIfOracle(String branchId) {
    return controller.getIfOracle(branchId);
  }

  public static boolean getIf(BoolExpr condition, boolean result, String branchID) {
    return controller.getIf(condition, result, branchID);
  }

  public static Context getCtx() {
    return controller.getCtx();
  }

  public static void selectTransition(List<Pair<Runnable, String>> possibleTransitions) {
    controller.selectTransition(possibleTransitions);
  }

  public static void addBranch(BoolExpr condition, String branchId) {
    controller.addBranch(condition, branchId);
  }

  public static void saveStates(StatesList info) {
    controller.saveStates(info);
  }

  public static <T> EnumSort<T> getEnumSort(String name, String... enumNames) {
    if (enumSorts.containsKey(name)) {
      return (EnumSort<T>) enumSorts.get(name);
    }
    EnumSort<T> enumSort = getCtx().mkEnumSort(name, enumNames);
    enumSorts.put(name, enumSort);
    return enumSort;
  }
}

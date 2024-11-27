/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.msg.Message;
import montiarc.rte.msg.MessageFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComplexHierarchyTestFixture {

  public static final String path0 = "aIni->aEn->a1En";
  public static final String path1 = "a1Ex->aEx->aToB->bEn";
  public static final String path2 = "bEx->bToC->cEn->c1En";
  public static final String path3 = "c1Ex->c1ToC2->c2En";
  public static final String path4 = "c2Ex->cEx->cToD->dEn->d2En";
  public static final String path5 = "d2Ex->d2ToD1->d1En";
  public static final String path6 = "d1Ex->dEx->d1ToE->eEn" /*+ "->e1Ini"*/ + "->e1En";
  public static final String path7 = "e1Ex->e1ToE2->e2En";
  public static final String path8 = "e2Ex->eEx->eToF11->fEn->f1En->f11En";
  public static final String path9 = "f11Ex->f1Ex->fEx->fToA->aEn" /*+ "->a1Ini"*/ + "->a1En";

  public static List<Message<String>> stringToMessages(String... s) {
    ArrayList<Message<String>> messages = new ArrayList<>();
    for (int i = 0; i < s.length; i++) {
      messages.addAll(Arrays.stream(s[i].split("->")).map(MessageFactory::msg).collect(Collectors.toList()));
      if (i > 0) messages.add(MessageFactory.tk());
    }
    return messages;
  }

  /*
   * Provider for the MaUnit test
   */
  public static List<List<String>> montiArcExpectedStringProvider(int ticks) {
    switch (ticks) {
      case 1:
        return stringToLists(path0 + "->" + path1);
      case 2:
        return stringToLists(path0 + "->" + path1, path2);
      case 3:
        return stringToLists(path0 + "->" + path1, path2, path3);
      case 4:
        return stringToLists(path0 + "->" + path1, path2, path3, path4);
      case 5:
        return stringToLists(path0 + "->" + path1, path2, path3, path4, path5);
      case 6:
        return stringToLists(path0 + "->" + path1, path2, path3, path4, path5, path6);
      case 7:
        return stringToLists(path0 + "->" + path1, path2, path3, path4, path5, path6, path7);
      case 8:
        return stringToLists(path0 + "->" + path1, path2, path3, path4, path5, path6, path7, path8);
      case 9:
        return stringToLists(path0 + "->" + path1, path2, path3, path4, path5, path6, path7, path8, path9);
    }
    return new ArrayList<>();
  }

  protected static List<List<String>> stringToLists(String... s) {
    ArrayList<List<String>> messages = new ArrayList<>();
    for (String string : s) {
      messages.add(Arrays.stream(string.split("->")).collect(Collectors.toList()));
    }
    return messages;
  }
}

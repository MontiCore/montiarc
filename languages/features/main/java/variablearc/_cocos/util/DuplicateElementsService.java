/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos.util;

import arcbasis._ast.ASTArcComponentType;
import java.util.LinkedHashMap;
import java.util.Map;

public class DuplicateElementsService {

  static Map<ASTArcComponentType,Boolean> duplicateElementForComponent = new LinkedHashMap<>();

  public static boolean duplicateElementPresent(ASTArcComponentType node){
    return duplicateElementForComponent.getOrDefault(node, false);
  }

  public static void setComponent(ASTArcComponentType node,boolean value){
    duplicateElementForComponent.put(node,value);
  }


}

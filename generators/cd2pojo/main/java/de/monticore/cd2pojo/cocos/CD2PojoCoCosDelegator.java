/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import de.monticore.cd4code._cocos.CD4CodeCoCoChecker;
import de.monticore.cd4code.cocos.CD4CodeCoCosDelegator;

public class CD2PojoCoCosDelegator extends CD4CodeCoCosDelegator {

  @Override
  protected void addCheckerForAllCoCos(CD4CodeCoCoChecker checker) {
    addCheckerForEbnfCoCos(checker);
    addCheckerForMcgCoCos(checker);
    addCheckerForMcg2EbnfCoCos(checker);
    checker.addCoCo(new CDAssociationNamesUnique());
    checker.addCoCo(new CDEllipsisParametersOnlyInLastPlace());
    checker.addCoCo(new CDRoleNamesUnique());
  }
}

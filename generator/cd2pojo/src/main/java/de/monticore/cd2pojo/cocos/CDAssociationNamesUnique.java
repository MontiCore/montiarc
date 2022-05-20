/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import com.google.common.base.Preconditions;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashSet;

public class CDAssociationNamesUnique implements CDBasisASTCDDefinitionCoCo {

  @Override
  public void check(@NotNull ASTCDDefinition ast) {
    Preconditions.checkNotNull(ast);
    HashSet<String> uniques = new HashSet<>();
    ast.getCDAssociationsList().stream()
      .filter(ASTCDAssociation::isPresentName)
      .map(ASTCDAssociation::getName)
      .filter(name -> !uniques.add(name))
      .forEach(name -> Log.error("The association name " + name +
        " is used multiple times the class diagram " + ast.getName() + "."));
  }
}
/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cd2pojo.cocos;

import com.google.common.base.Preconditions;
import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._cocos.CDBasisASTCDDefinitionCoCo;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CDRoleNamesUnique implements CDBasisASTCDDefinitionCoCo {
  
  @Override
  public void check(@NotNull ASTCDDefinition ast) {
    Preconditions.checkNotNull(ast);
    this.getSourceClassNamesFromAssociations(ast)
      .forEach(source -> this.checkRoleNamesUnique(source, ast));
  }
  
  protected Collection<String> getSourceClassNamesFromAssociations(@NotNull ASTCDDefinition ast) {
    Preconditions.checkNotNull(ast);
    Set<String> sourceClassNames = new HashSet<>();
    for (ASTCDAssociation association : ast.getCDAssociationsList()) {
      if (isDefinitiveNavigableRight(association)) {
        //add left as source class
        sourceClassNames.add(String.join(".", association.getLeftReferenceName()));
      }
      if (isDefinitiveNavigableLeft(association)) {
        //add right as source class
        sourceClassNames.add(String.join(".", association.getRightReferenceName()));
      }
    }
    return sourceClassNames;
  }
  
  protected void checkRoleNamesUnique(@NotNull String sourceClassName, @NotNull ASTCDDefinition ast) {
    Preconditions.checkNotNull(sourceClassName);
    Preconditions.checkNotNull(ast);
    Collection<ASTCDAssociation> associations = ast.getCDAssociationsList();
    Set<String> roleNames = new HashSet<>();
    for (ASTCDAssociation association : associations) {
      if (isNotDefinitiveNavigable(association)) {
        continue;
      }
      String errorMessage = "The role name %s would be used multiple times as an attribute name in the class %s";
      if (isBiDirectional(association)) {
        if (String.join(".", association.getRightReferenceName()).equals(sourceClassName)) {
          if (!roleNames.add(association.getLeft().getName())) {
            Log.error(String.format(errorMessage, association.getLeft().getName(), sourceClassName));
          }
        }
        if (String.join(".", association.getLeftReferenceName()).equals(sourceClassName)) {
          if (!roleNames.add(association.getRight().getName())) {
            Log.error(String.format(errorMessage, association.getRight().getName(), sourceClassName));
          }
        }
      } else if (isDefinitiveNavigableLeft(association)) {
        if (association.isPresentName() && association.getName().equals(sourceClassName)) {
          if (!roleNames.add(association.getLeft().getName())) {
            Log.error(String.format(errorMessage, association.getLeft().getName(), sourceClassName));
          }
        }
      } else if (isDefinitiveNavigableRight(association)) {
        if (association.isPresentName() && association.getName().equals(sourceClassName)) {
          if (!roleNames.add(association.getRight().getName())) {
            Log.error(String.format(errorMessage, association.getRight().getName(), sourceClassName));
          }
        }
      }
    }
  }
  
  protected static boolean isDefinitiveNavigableRight(@NotNull ASTCDAssociation association) {
    Preconditions.checkNotNull(association);
    return association.getCDAssocDir() instanceof ASTCDLeftToRightDir || isBiDirectional(association);
  }
  
  protected static boolean isDefinitiveNavigableLeft(@NotNull ASTCDAssociation association) {
    Preconditions.checkNotNull(association);
    return association.getCDAssocDir() instanceof ASTCDRightToLeftDir || isBiDirectional(association);
  }
  
  protected static boolean isBiDirectional(@NotNull ASTCDAssociation association) {
    Preconditions.checkNotNull(association);
    return association.getCDAssocDir() instanceof ASTCDBiDir;
  }
  
  protected static boolean isNotDefinitiveNavigable(@NotNull ASTCDAssociation association) {
    Preconditions.checkNotNull(association);
    return association.getCDAssocDir() instanceof ASTCDUnspecifiedDir;
  }
}
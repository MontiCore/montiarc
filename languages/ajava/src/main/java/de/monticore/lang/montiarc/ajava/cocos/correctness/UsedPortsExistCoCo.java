/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos.correctness;

import java.util.Collection;
import java.util.Optional;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.SimpleVariableSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks whether all used ports in the java expression exist in the component
 * definition.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UsedPortsExistCoCo
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentBodyCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol cmp = (ComponentSymbol) node.getSymbol().orElse(null);
    if (null == cmp) {
      Log.error(String.format("0x9AF6C ASTComponent node \"%s\" has no symbol. Did you forget to "
          + "run the SymbolTableCreator before checking cocos?", node.getName()));
      return;
    }
    Collection<AJavaDefinitionSymbol> ajavaDefinitions = cmp.getSpannedScope()
        .resolveLocally(AJavaDefinitionSymbol.KIND);
    
    if (ajavaDefinitions.size() > 1) {
      Log.error(
          "0xAA320 There must not be more than one ajava definitions in a component definition.",
          node.get_SourcePositionStart());
    }
    
    if (ajavaDefinitions.size() == 1) {
      AJavaDefinitionSymbol ajavaDef = ajavaDefinitions.iterator().next();
      Collection<SimpleVariableSymbol> usedVars = ajavaDef.getSpannedScope()
          .resolveLocally(SimpleVariableSymbol.KIND);
      for (SimpleVariableSymbol var : usedVars) {
        String varName = var.getName();
        Optional<PortSymbol> port = cmp.getSpannedScope().<PortSymbol> resolve(varName,
            PortSymbol.KIND);
        if (!port.isPresent()) {
          Optional<JavaFieldSymbol> definedField = ajavaDef.getSpannedScope()
              .<JavaFieldSymbol> resolveLocally(varName, JavaFieldSymbol.KIND);
          
          if (!definedField.isPresent()) {
            Log.error("0xAA330 Used variable " + varName + " in ajava definition is not a port.",
                ajavaDef.getAstNode().get().get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}

/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.ajava.cocos.correctness;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.EObjectOutputStream.Check;

import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.lang.montiarc.ajava._ast.ASTComponentInitialization;
import de.monticore.lang.montiarc.ajava._ast.ASTVariableInitialization;
import de.monticore.lang.montiarc.ajava._symboltable.AJavaDefinitionSymbol;
import de.monticore.lang.montiarc.ajava._symboltable.SimpleVariableSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentVariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Checks whether all used ports in the java expression exist in the component
 * definition.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class UsedPortsVariablesExistCoCo
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
    
    checkAJavaInitialization(node, cmp);
    
    checkAJavaDefinition(node, cmp);
  }
  
  private void checkAJavaInitialization(ASTComponent node, ComponentSymbol cmp) {
    for (ASTElement e : node.getBody().getElements()) {
      if (e instanceof ASTComponentInitialization) {
        ASTComponentInitialization init = (ASTComponentInitialization) e;
        for (ASTVariableInitialization i : init.getVariableInitializations()) {
          String name = Names.getQualifiedName(i.getQualifiedName().getParts());
          Optional<PortSymbol> port = cmp.getSpannedScope().<PortSymbol> resolve(name,
              PortSymbol.KIND);
          Optional<ComponentVariableSymbol> compVar = cmp.getSpannedScope()
              .<ComponentVariableSymbol> resolve(name, ComponentVariableSymbol.KIND);
          if (!port.isPresent() && !compVar.isPresent()) {
            Log.error("0xAA329 Used variable " + name
                + " in ajava initialization is not a port, component variable or locally defined variable.",
                i.get_SourcePositionStart());
          }
          
          if(port.isPresent()) {
            if(port.get().isIncoming()) {
              Log.error("0xAA331 Port "+ port.get().getName() +" is incoming, and thus must not be changed",
                  i.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
  
  private void checkAJavaDefinition(ASTComponent node, ComponentSymbol cmp) {
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
        Optional<JavaFieldSymbol> definedField = ajavaDef.getSpannedScope()
            .<JavaFieldSymbol> resolveLocally(varName, JavaFieldSymbol.KIND);
        Optional<ComponentVariableSymbol> compVar = cmp.getSpannedScope()
            .<ComponentVariableSymbol> resolve(varName, ComponentVariableSymbol.KIND);
        if (!port.isPresent() && !compVar.isPresent() && definedField.isPresent()) {
          Log.error("0xAA330 Used variable " + varName
              + " in ajava definition is not a port, component variable or locally defined variable.",
              ajavaDef.getAstNode().get().get_SourcePositionStart());
        }
        
        if(port.isPresent()) {
          if(port.get().isIncoming()) {
            Log.error("0xAA331 Port "+ port.get().getName() +" is incoming, and thus must not be changed",
                var.getAstNode().get().get_SourcePositionStart());
          }
        }
      }
    }
  }
}

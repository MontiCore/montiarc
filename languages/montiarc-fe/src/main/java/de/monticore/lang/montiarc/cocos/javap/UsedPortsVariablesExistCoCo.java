/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos.javap;

import java.util.Collection;
import java.util.Optional;

import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.lang.montiarc.montiarc._ast.ASTComponent;
import de.monticore.lang.montiarc.montiarc._ast.ASTElement;
import de.monticore.lang.montiarc.montiarc._ast.ASTJavaPInitializer;
import de.monticore.lang.montiarc.montiarc._ast.ASTValueInitialization;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.ComponentSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.JavaBehaviorSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.JavaVariableReferenceSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.PortSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Checks whether all used ports in the java expression exist in the component
 * definition.
 *
 * @author Andreas Wortmann
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
      if (e instanceof ASTJavaPInitializer) {
    	  ASTJavaPInitializer init = (ASTJavaPInitializer) e;
        for (ASTValueInitialization i : init.getValueInitializations()) {
          String name = Names.getQualifiedName(i.getQualifiedName().getParts());
          Optional<PortSymbol> port = cmp.getSpannedScope().<PortSymbol> resolve(name,
              PortSymbol.KIND);
          Optional<VariableSymbol> compVar = cmp.getSpannedScope()
              .<VariableSymbol> resolve(name, VariableSymbol.KIND);
          Optional<JFieldSymbol> cmpParameter = cmp.getConfigParameters().stream()
              .filter(p -> p.getName().equals(name)).findFirst();
          if (!port.isPresent() && !compVar.isPresent() && !cmpParameter.isPresent()) {
              Log.error("0xAA329 Used variable " + name
                  + " in ajava initialization is not a port, component variable or locally defined variable.",
                  i.get_SourcePositionStart());
          }
          
          if (port.isPresent()) {
            if (port.get().isIncoming()) {
              Log.error("0xAA331 Port " + port.get().getName()
                  + " is incoming, and thus must not be changed",
                  i.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
  
  private void checkAJavaDefinition(ASTComponent node, ComponentSymbol cmp) {
    Collection<JavaBehaviorSymbol> ajavaDefinitions = cmp.getSpannedScope()
        .resolveLocally(JavaBehaviorSymbol.KIND);
    
    if (ajavaDefinitions.size() == 1) { // else: MultipleBehaviorImplementation catches this
      JavaBehaviorSymbol ajavaDef = ajavaDefinitions.iterator().next();
      Collection<JavaVariableReferenceSymbol> usedVars = ajavaDef.getSpannedScope()
          .resolveLocally(JavaVariableReferenceSymbol.KIND);
      for (JavaVariableReferenceSymbol var : usedVars) {
        String varName = var.getName();
        Optional<PortSymbol> port = cmp.getSpannedScope().<PortSymbol> resolve(varName,
            PortSymbol.KIND);
        Optional<JavaFieldSymbol> definedField = ajavaDef.getSpannedScope()
            .<JavaFieldSymbol> resolveLocally(varName, JavaFieldSymbol.KIND);
        Optional<VariableSymbol> compVar = cmp.getSpannedScope()
            .<VariableSymbol> resolve(varName, VariableSymbol.KIND);
        Optional<JFieldSymbol> cmpParameter = cmp.getConfigParameters().stream()
            .filter(p -> p.getName().equals(varName)).findFirst();
        if (!port.isPresent() && !compVar.isPresent() && !definedField.isPresent() && !cmpParameter.isPresent()) {
          Log.error("0xAA330 Used variable " + varName
              + " in ajava definition is not a port, component variable or locally defined variable.",
              ajavaDef.getAstNode().get().get_SourcePositionStart());
        }
      }
    }
  }
}

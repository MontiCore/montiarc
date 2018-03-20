/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.Optional;

import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTElement;
import montiarc._ast.ASTJavaPInitializer;
import montiarc._ast.ASTValueInitialization;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Checks whether all used ports in the ajava initialisation exist in the component definition.
 * SymboltableCreator already ensures that used ports and variables in the ajava behavior are
 * consistent.
 *
 * @author Andreas Wortmann
 */
public class UsedPortsAndVariablesExist
    implements MontiArcASTComponentCoCo {
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentBodyCoCo#check(de.monticore.lang.montiarc.montiarc._ast.ASTComponentBody)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol cmp = (ComponentSymbol) node.getSymbol().orElse(null);
    if (null == cmp) {
      Log.error(String.format("0xMA010 ASTComponent node \"%s\" has no symbol. Did you forget to "
          + "run the SymbolTableCreator before checking cocos?", node.getName()));
      return;
    }
    
    checkAJavaInitialization(node, cmp);
    
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
            Log.error("0xMA030 Used variable " + name
                + " in ajava initialization is not a port, component variable or locally defined variable.",
                i.get_SourcePositionStart());
          }
          
          if (port.isPresent()) {
            if (port.get().isIncoming()) {
              Log.error("0xMA032 Port " + port.get().getName()
                  + " is incoming, and thus must not be changed",
                  i.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}

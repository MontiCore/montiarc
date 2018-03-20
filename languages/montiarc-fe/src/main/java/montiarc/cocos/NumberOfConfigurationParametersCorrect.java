/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._ast.ASTParameter;
import montiarc._ast.ASTSubComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * This coco compares the number of arguments passed to the subcomponent with
 * the parameters of the instantiated type. It considers default parameters that
 * are optional when instantiating a component. Type and Ordering is checked in
 * other cocos.
 * 
 * @implements [Wor16] MR1: Arguments of configuration parameters with default
 * values may be omitted during subcomponent declaration. (p. 58, Lst. 4.11)
 * 
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class NumberOfConfigurationParametersCorrect implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol sym = (ComponentSymbol) node.getSymbol().get();
    List<ASTSubComponent> subComps = node.getSubComponents();
    for (ASTSubComponent sub : subComps) {
      String type = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(sub.getType());
      Optional<ComponentSymbol> subcompSym = sym.getEnclosingScope().<ComponentSymbol> resolve(type,
          ComponentSymbol.KIND);
      if (subcompSym.isPresent()) {
        ASTComponent subcompType = (ASTComponent) subcompSym.get().getAstNode().get();
        List<ASTParameter> params = subcompType.getHead().getParameters();
        int numberOfNecessaryConfigParams = params.size() - getNumberOfDefaultParameters(params);
        if (numberOfNecessaryConfigParams > sub.getArguments().size() || sub.getArguments().size() > params.size()) {
          Log.error(String.format("0xMA082 Subcomponent of type \"%s\" is instantiated with "
              + sub.getArguments().size() + " arguments but requires "
              + numberOfNecessaryConfigParams + " arguments by type definition.", type),
              sub.get_SourcePositionStart());
        }
      }
      else {
        Log.error(String.format("0xMA004 Type \"%s\" could not be resolved", type),
            sub.get_SourcePositionStart());
      }
    }
  }
  
  private int getNumberOfDefaultParameters(List<ASTParameter> params) {
    int counter = 0;
    
    for (ASTParameter param : params) {
      if (param.getDefaultValue().isPresent()) {
        counter++;
      }
    }
    return counter;
  }
  
}

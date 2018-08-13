/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc.cocos;

import java.util.List;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.SymbolPrinter;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Checks, if the type parameters of a components super component are set
 * correctly.
 * 
 * @implements [Hab16] R15: Components that inherit from a generic component
 * have to assign concrete type arguments to all generic type parameters. (p.
 * 69, lst. 3.50)
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class AllGenericParametersOfSuperClassSet implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbol().get();
    
    if (compSym.getSuperComponent().isPresent()) {
      List<JTypeSymbol> typeParameters = compSym.getFormalTypeParameters();
      List<ActualTypeArgument> supersActualTypeParams = compSym.getSuperComponent().get()
          .getActualTypeArguments();
      List<JTypeSymbol> supersTypeParameters = compSym.getSuperComponent().get()
          .getReferencedSymbol().getFormalTypeParameters();
      
      // we have a generic component
      if (!supersTypeParameters.isEmpty()) {
        if (supersActualTypeParams.isEmpty()) {
          Log.error("0xMA087 Type parameters '"
              + SymbolPrinter.printFormalTypeParameters(supersTypeParameters)
              + "' of the extended super component " + compSym.getSuperComponent().get().getName()
              + " have to be set.",
              node.getHead().getSuperComponent().get_SourcePositionStart());
        }
        else if (supersActualTypeParams.size() != supersTypeParameters.size()) {
          Log.error("0xMA088 All type parameters "
              + SymbolPrinter.printFormalTypeParameters(supersTypeParameters)
              + " of the extended super component "
              + compSym.getSuperComponent().get().getName() + "have to be set",
              node.getHead().getSuperComponent().get_SourcePositionStart());
        }
        // same length of params
        else {
          for (int i = 0; i < supersTypeParameters.size(); i++) {
            ActualTypeArgument actualArg = supersActualTypeParams.get(i);
            JTypeSymbol superFormalType = supersTypeParameters.get(i);
            // restricted generic type
            if (!superFormalType.getInterfaces().isEmpty()) {
              List<? extends JTypeReference<? extends JTypeSymbol>> upperBounds = superFormalType
                  .getInterfaces();
              for (int j = 0; j < upperBounds.size(); j++) {
                JTypeReference<? extends JTypeSymbol> upperBound = upperBounds.get(j);
                // Case 1 formal type parameter is instantiated in extend
                // (component A
                // extends B<Integer>)
                if (!((JavaTypeSymbolReference) actualArg.getType()).getReferencedSymbol()
                    .isFormalTypeParameter()) {
                  if (upperBound.getActualTypeArguments().isEmpty()
                      && !upperBound.getReferencedSymbol().getFormalTypeParameters().isEmpty()) {
                    upperBound.setActualTypeArguments(supersActualTypeParams);
                  }
                  if (!TypeCompatibilityChecker.doTypesMatch(
                      ((JTypeReference<? extends JTypeSymbol>) actualArg.getType()),
                      ((JTypeReference<? extends JTypeSymbol>) actualArg.getType())
                          .getReferencedSymbol().getFormalTypeParameters().stream()
                          .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
                      ((JTypeReference<? extends JTypeSymbol>) actualArg.getType())
                          .getActualTypeArguments().stream()
                          .map(a -> (JavaTypeSymbolReference) a.getType())
                          .collect(Collectors.toList()),
                      upperBound,
                      upperBound.getReferencedSymbol().getFormalTypeParameters().stream()
                          .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
                      supersActualTypeParams.stream()
                          .map(a -> (JavaTypeSymbolReference) a.getType())
                          .collect(Collectors.toList()))) {
                    Log.error("0xMA089 Parameter " + SymbolPrinter.printTypeParameters(actualArg)
                        + " is not compatible with "
                        + upperBound.getName(),
                        node.getHead().getSuperComponent().get_SourcePositionStart());
                  }
                }
                
                // Case 2 formal type parameter is passed to extend (component
                // A<K extends
                // Number> extends B<K>)
                else {
                  int pos = getPositionInFormalTypeParameters(typeParameters,
                      (JTypeReference<? extends JTypeSymbol>) actualArg.getType());
                  
                  JTypeSymbol formalType = supersTypeParameters.get(pos);
                  if (!formalType.getInterfaces().isEmpty()) {
                    if (!TypeCompatibilityChecker.doTypesMatch(
                        ((JTypeReference<? extends JTypeSymbol>) actualArg.getType()),
                        ((JTypeReference<? extends JTypeSymbol>) actualArg.getType())
                            .getReferencedSymbol().getFormalTypeParameters().stream()
                            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
                        ((JTypeReference<? extends JTypeSymbol>) actualArg.getType())
                            .getActualTypeArguments().stream()
                            .map(a -> (JavaTypeSymbolReference) a.getType())
                            .collect(Collectors.toList()),
                        formalType.getInterfaces().get(j),
                        formalType.getInterfaces().get(j)
                            .getReferencedSymbol().getFormalTypeParameters().stream()
                            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
                        formalType.getInterfaces().get(j)
                            .getActualTypeArguments().stream()
                            .map(a -> (JavaTypeSymbolReference) a.getType())
                            .collect(Collectors.toList()))) {
                      Log.error("0xMA089 Parameter " + formalType.getName()
                          + " is not compatible with upper bound "
                          + upperBound.getName(),
                          node.getHead().getSuperComponent().get_SourcePositionStart());
                    }
                  }
                }
              }
            }
          }
        }
      }
      
    }
    
  }
  
  private int getPositionInFormalTypeParameters(List<JTypeSymbol> formalTypeParameters,
      JTypeReference<? extends JTypeSymbol> searchedFormalTypeParameter) {
    int positionInFormal = 0;
    for (JTypeSymbol formalTypeParameter : formalTypeParameters) {
      if (formalTypeParameter.getName().equals(searchedFormalTypeParameter.getName())) {
        break;
      }
      positionInFormal++;
    }
    return positionInFormal;
  }
}

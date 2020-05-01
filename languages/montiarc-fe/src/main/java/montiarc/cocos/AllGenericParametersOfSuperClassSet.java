/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.types._ast.ASTReferenceType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.ComponentSymbolReference;
import montiarc.helper.SymbolPrinter;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Checks, if the type parameters of a components super component are set
 * correctly.
 * 
 * @implements [Hab16] R15: Components that inherit from a generic component
 * have to assign concrete type arguments to all generic type parameters. (p.
 * 69, lst. 3.50)
 */
public class AllGenericParametersOfSuperClassSet implements MontiArcASTComponentCoCo {
  
  /**
   * @see montiarc._cocos.MontiArcASTComponentCoCo#check(montiarc._ast.ASTComponent)
   */
  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                            "symbol. Did you forget to run the " +
                            "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol compSym = (ComponentSymbol) node.getSymbolOpt().get();
    
    if (compSym.getSuperComponent().isPresent()) {
      final ComponentSymbolReference superCompRef = compSym.getSuperComponent().get();
      final ASTReferenceType superCompRefNode = node.getHead().getSuperComponent();

      List<JTypeSymbol> typeParameters = compSym.getFormalTypeParameters();
      List<ActualTypeArgument> supersActualTypeParams = superCompRef
          .getActualTypeArguments();
      List<JTypeSymbol> supersTypeParameters = superCompRef
          .getReferencedSymbol().getFormalTypeParameters();

      // we have a generic component
      if (!supersTypeParameters.isEmpty()) {
        if (supersActualTypeParams.isEmpty()) {
          Log.error("0xMA087 Type parameters '"
              + SymbolPrinter.printFormalTypeParameters(supersTypeParameters)
              + "' of the extended super component " + superCompRef.getName()
              + " have to be set.",
              superCompRefNode.get_SourcePositionStart());
        }
        else if (supersActualTypeParams.size() != supersTypeParameters.size()) {
          Log.error("0xMA088 All type parameters "
              + SymbolPrinter.printFormalTypeParameters(supersTypeParameters)
              + " of the extended super component "
              + superCompRef.getName() + "have to be set",
              superCompRefNode.get_SourcePositionStart());
        }
        // same length of params
        else {
          for (int i = 0; i < supersTypeParameters.size(); i++) {
            ActualTypeArgument actualArg = supersActualTypeParams.get(i);
            JTypeSymbol superFormalType = supersTypeParameters.get(i);
            // restricted generic type
            if (!superFormalType.getInterfaces().isEmpty()) {
              List<? extends JTypeReference<? extends JTypeSymbol>> upperBounds
                  = superFormalType.getInterfaces();
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
                      TypeCompatibilityChecker.toJTypeReferences(
                          actualArg.getType().getActualTypeArguments()),
                      upperBound,
                      TypeCompatibilityChecker.toJTypeSymbols(
                          upperBound.getReferencedSymbol().getFormalTypeParameters()),
                      TypeCompatibilityChecker.toJTypeReferences(supersActualTypeParams))) {
                    Log.error("0xMA089 Parameter " + SymbolPrinter.printTypeArgument(actualArg)
                        + " is not compatible with "
                        + upperBound.getName(),
                        superCompRefNode.get_SourcePositionStart());
                  }
                }

                // Case 2 formal type parameter is passed to extend (component
                // A<K extends
                // Number> extends B<K>)
                else {
                  int pos = TypeCompatibilityChecker.getPositionInFormalTypeParameters(typeParameters,
                      (JTypeReference<? extends JTypeSymbol>) actualArg.getType());

                  JTypeSymbol formalType = supersTypeParameters.get(pos);
                  List<JTypeReference<? extends JTypeSymbol>> formalTypeArgs = new ArrayList<>();
                  formalTypeArgs.add((JTypeReference<? extends JTypeSymbol>) actualArg.getType());
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
                            formalTypeArgs )) {
                      Log.error("0xMA089 Parameter " + formalType.getName()
                                    + " is not compatible with upper bound "
                                    + upperBound.getName(),
                          superCompRefNode.get_SourcePositionStart());
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // The super component has no type parameters.
        // It remains to check that there are no type arguments assigned.
        if(!supersActualTypeParams.isEmpty()){
          Log.error(
              String.format("0xMA071 Type parameters were assigned by " +
                                "component %s to super component %s, which " +
                                "does not declare generic type parameters.",
                  compSym.getName(),
                  superCompRef.getName()),
              superCompRefNode.get_SourcePositionStart());
        }
      }
    }
  }
}

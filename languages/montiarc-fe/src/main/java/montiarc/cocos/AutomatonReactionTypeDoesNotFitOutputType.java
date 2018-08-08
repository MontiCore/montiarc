package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.java.types.JavaDSLHelper;
import de.monticore.mcexpressions._ast.ASTArguments;
import de.monticore.mcexpressions._ast.ASTCallExpression;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if all assignments inside a reaction of a
 * transition are type-correct.
 *
 * @author Andreas Wortmann
 */
public class AutomatonReactionTypeDoesNotFitOutputType
    implements MontiArcASTTransitionCoCo, MontiArcASTInitialStateDeclarationCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isPresentReaction()) {
      for (ASTIOAssignment assign : node.getReaction().getIOAssignmentList()) {
        checkAssignment(assign, ((TransitionSymbol) node.getSymbolOpt().get()).getSpannedScope());
      }
    }
  }
  
  /**
   * @see montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo#check(montiarc._ast.ASTInitialStateDeclaration)
   */
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.isPresentBlock()) {
      for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
        checkAssignment(assign, node.getEnclosingScopeOpt().get());
      }
    }
  }
  
  private void checkAssignment(ASTIOAssignment assignment, Scope transitionScope) {
    if (assignment.isPresentName()) {
      String currentNameToResolve = assignment.getName();
      
      Optional<VariableSymbol> vSymbol = transitionScope.resolve(currentNameToResolve,
          VariableSymbol.KIND);
      Optional<PortSymbol> pSymbol = transitionScope.resolve(currentNameToResolve,
          PortSymbol.KIND);
      if (pSymbol.isPresent()) {
        if (pSymbol.get().isIncoming()) {
          Log.error("0xMA041 Did not find matching Variable or Output with name "
              + currentNameToResolve, assignment.get_SourcePositionStart());
        }
        else {
          checkTypeCorrectness(assignment, pSymbol.get().getTypeReference(),
              currentNameToResolve);
        }
      }
      else if (vSymbol.isPresent()) {
        checkTypeCorrectness(assignment, vSymbol.get().getTypeReference(), currentNameToResolve);
      }
    }
  }
  
  private void checkTypeCorrectness(ASTIOAssignment assign,
      JTypeReference<? extends JTypeSymbol> typeRef, String currentNameToResolve) {
    if (assign.isPresentValueList()) {
      for (ASTValuation val : assign.getValueList().getAllValuations()) {
        // check assignment
        if (assign.isAssignment()) {
          checkAssignedTypesFits(val, typeRef, currentNameToResolve);
        }
        // check call
        else if (assign.isCall()) {
          checkCall(val, typeRef);
        }
      }
    }
  }
  
  private void checkAssignedTypesFits(ASTValuation assignment,
      JTypeReference<? extends JTypeSymbol> varType, String referencedName) {
    Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker
        .getExpressionType(assignment.getExpression());
    if (!exprType.isPresent()) {
      Log.error(
          "0xMA044 Could not resolve type of expression for checking the reaction.",
          assignment.get_SourcePositionStart());
    }
    else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(),
        exprType.get().getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
        exprType.get().getActualTypeArguments().stream()
            .map(a -> (JavaTypeSymbolReference) a.getType())
            .collect(Collectors.toList()),
        varType,
        varType.getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
        varType.getActualTypeArguments().stream()
            .map(a -> (JavaTypeSymbolReference) a.getType())
            .collect(Collectors.toList()))) {
      Log.error("0xMA042 Type of Variable/Output \"" + referencedName
          + "\" in the reaction does not match the type of its assigned expression. Type "
          +
          exprType.get().getName() + " can not cast to type " + varType.getName()
          + ".", assignment.get_SourcePositionStart());
    }
  }
  
  private void checkCall(ASTValuation assignment,
      JTypeReference<? extends JTypeSymbol> varType) {
    if (assignment.getExpression() instanceof ASTCallExpression) {
      ASTCallExpression methodCallExpr = (ASTCallExpression) assignment.getExpression();
      ASTArguments args = methodCallExpr.getArguments();
      
      // 1. get types of parameters in guard method call
      List<JavaTypeSymbolReference> argTypes = new ArrayList<>();
      for (ASTExpression e : args.getExpressionList()) {
        Optional<? extends JavaTypeSymbolReference> argTypeOpt = TypeCompatibilityChecker
            .getExpressionType(e);
        if (argTypeOpt.isPresent()) {
          argTypes.add(argTypeOpt.get());
        }
        else {
          Log.error("0xMA100 argument type" + e + " could not be resolved.");
        }
      }
      
      // 2. find the method symbol of the called method
      String methName = ((ASTQualifiedNameExpression) methodCallExpr.getExpression())
          .getName();
      Optional<JavaMethodSymbol> methSym = findMethodSymbol(varType, methName);
      
      // 3. Check correct method parameters to passed parameters in the method
      // call in reaction.
      if (methSym.isPresent()) {
        List<JavaTypeSymbolReference> correctParameters = new ArrayList<>();
        
        correctParameters = JavaDSLHelper
            .getParameterTypes(methSym.get());
        
        if (argTypes.size() != correctParameters.size()) {
          Log.error("0xMA099 Number of method parameters does not fit.",
              methSym.get().getSourcePosition());
        }
        
        // 3.1 substitute possible generic parameters
        correctParameters = substituteFormalParameters(correctParameters, varType);
        
        // 3.2 compare actual and correct method parameters with each other
        List<JTypeSymbol> varTypeFormalTypeParams =  varType.getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList());
        
        List<JTypeReference<? extends JTypeSymbol>> varTypeActualTypeArgs = varType.getActualTypeArguments().stream()
            .map(a -> (JavaTypeSymbolReference) a.getType())
            .collect(Collectors.toList());
        
        if(!TypeCompatibilityChecker.hasNestedGenerics(varType)) {
          varTypeFormalTypeParams = new ArrayList<>();
          varTypeActualTypeArgs = new ArrayList<>();
        }
        
        for (int i = 0; i < argTypes.size(); i++) {
          if (!TypeCompatibilityChecker.doTypesMatch(argTypes.get(i),
              argTypes.get(i).getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              argTypes.get(i).getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()),
              correctParameters.get(i),
              varTypeFormalTypeParams,
              varTypeActualTypeArgs)) {
            Log.error("0xMA043 Types do not fit " + argTypes.get(i).getName() + ", "
                + correctParameters.get(i).getName());
          }
        }
        
      }
    }
  }
  
  /**
   * Finds a method symbol in the passed {@code type} with name
   * {@code methodname}.
   * 
   * @param type
   * @param methodName
   * @return
   */
  private Optional<JavaMethodSymbol> findMethodSymbol(JTypeReference<? extends JTypeSymbol> type,
      String methodName) {
    Optional<JavaMethodSymbol> methSym = ((JavaTypeSymbol) type.getReferencedSymbol())
        .getMethod(methodName);
    
    if (!methSym.isPresent()) {
      JavaTypeSymbol varJavaType = ((JavaTypeSymbol) type.getReferencedSymbol());
      List<JavaMethodSymbol> m1 = new ArrayList<>();
      if (varJavaType.getSuperClass().isPresent()) {
        m1 = JavaDSLHelper.getAccessibleMethods(methodName,
            varJavaType,
            varJavaType.getSuperClass().get(),
            false);
      }
      
      if (!m1.isEmpty()) {
        methSym = Optional.of(m1.get(0));
      }
    }
    
    return methSym;
  }
  
  /**
   * Substitutes formal paramters by their actual type and returns the resulting
   * list.
   * 
   * @param parameters
   * @param type
   * @return
   */
  private List<JavaTypeSymbolReference> substituteFormalParameters(
      List<JavaTypeSymbolReference> parameters,
      JTypeReference<? extends JTypeSymbol> type) {
    List<JavaTypeSymbolReference> substituted = parameters;
    
    for (int i = 0; i < substituted.size(); i++) {
      JavaTypeSymbolReference param = substituted.get(i);
      if (param.getReferencedSymbol().isFormalTypeParameter()) {
        int pos = TypeCompatibilityChecker.getPositionInFormalTypeParameters(
            type.getReferencedSymbol().getFormalTypeParameters().stream()
                .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
            param);
        
        substituted.set(i, (JavaTypeSymbolReference) type
            .getActualTypeArguments().get(pos).getType());
      }
    }
    
    return substituted;
  }
  
}

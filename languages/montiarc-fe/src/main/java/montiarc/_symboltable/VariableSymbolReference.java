/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montiarc._symboltable;

import montiarc._ast.ASTValuation;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class VariableSymbolReference extends VariableSymbol implements SymbolReference<VariableSymbol>{

  protected final SymbolReference<VariableSymbol> reference;

  /**
   * Constructor for de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbolReference
   * @param name
   */
  public VariableSymbolReference(String name, final Scope definingScopeOfReference) {
    super(name);
    reference = new CommonSymbolReference<>(name, VariableSymbol.KIND, definingScopeOfReference);
  }

  /**
   * @see de.monticore.symboltable.references.SymbolReference#getReferencedSymbol()
   */
  @Override
  public VariableSymbol getReferencedSymbol() {
    return reference.getReferencedSymbol();
  }

  /**
   * @see de.monticore.symboltable.references.SymbolReference#existsReferencedSymbol()
   */
  @Override
  public boolean existsReferencedSymbol() {
    return reference.existsReferencedSymbol();
  }

  /**
   * @see de.monticore.symboltable.references.SymbolReference#isReferencedSymbolLoaded()
   */
  @Override
  public boolean isReferencedSymbolLoaded() {
    return reference.isReferencedSymbolLoaded();
  }

  /*
   * Methods of Symbol interface
   */

   @Override
   public String getName() {
     return getReferencedSymbol().getName();
   }

   @Override
   public String getFullName() {
     return getReferencedSymbol().getFullName();
   }

   @Override
   public Scope getEnclosingScope() {
     return getReferencedSymbol().getEnclosingScope();
   }

   @Override
   public void setEnclosingScope(MutableScope scope) {
     getReferencedSymbol().setEnclosingScope(scope);
   }

   @Override
   public AccessModifier getAccessModifier() {
     return getReferencedSymbol().getAccessModifier();
   }

   @Override
   public void setAccessModifier(AccessModifier accessModifier) {
     getReferencedSymbol().setAccessModifier(accessModifier);
   }
   
   /**
   * @see de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol#setValuation(de.monticore.lang.montiarc.montiarc._ast.ASTValuation)
   */
  @Override
  public void setValuation(ASTValuation valuation) {
    getReferencedSymbol().setValuation(valuation);
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol#getValuation()
   */
  @Override
  public ASTValuation getValuation() {
   return getReferencedSymbol().getValuation();
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol#setTypeReference(de.monticore.symboltable.types.references.JTypeReference)
   */
  @Override
  public void setTypeReference(JTypeReference<? extends JTypeSymbol> typeReference) {
   getReferencedSymbol().setTypeReference(typeReference);
  }
  
  /**
   * @see de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol#getTypeReference()
   */
  @Override
  public JTypeReference<? extends JTypeSymbol> getTypeReference() {
   return getReferencedSymbol().getTypeReference();
  }

}

package de.monticore.lang.montiarc.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.java.symboltable.JavaFieldSymbol;
import de.monticore.java.symboltable.JavaMethodSymbol;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.SymbolAdapter;
import de.monticore.symboltable.types.JAttributeSymbolKind;
import de.monticore.symboltable.types.JMethodSymbolKind;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDMethodSymbol;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.monticore.umlcd4a.symboltable.CDTypes;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;


/**
 * Adapts CDTypeSymbols to JavaTypeSymbols
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class CDTypeSymbol2JavaType extends JavaTypeSymbol implements SymbolAdapter<CDTypeSymbol> {
  
  private final CDTypeSymbol adaptee;
  
  public CDTypeSymbol2JavaType(CDTypeSymbol adaptee) {
    super(adaptee.getName());
    
    this.adaptee = adaptee;
  }
  
  @Override
  public CDTypeSymbol getAdaptee() {
    return adaptee;
  }
  
  @Override
  public String getFullName() {
    return adaptee.getFullName();
  }
  
  @Override
  public AccessModifier getAccessModifier() {
    return adaptee.getAccessModifier();
  }
  
  @Override
  public Optional<ASTNode> getAstNode() {
    return adaptee.getAstNode();
  }
  
  @Override
  public Scope getEnclosingScope() {
    return adaptee.getEnclosingScope();
  }
  
  @Override
  public SymbolKind getKind() {
    return adaptee.getKind();
  }
  
  @Override
  public String getPackageName() {
    return adaptee.getPackageName();
  }
  
  @Override
  public void setAstNode(ASTNode node) {
    adaptee.setAstNode(node);
  }
  
  @Override
  public void setFullName(String fullName) {
    adaptee.setFullName(fullName);
  }
  
  @Override
  public void setPackageName(String packageName) {
    adaptee.setPackageName(packageName);
  }
  
  @Override
  public String toString() {
    return adaptee.toString();
  }
  
  @Override
  public Scope getSpannedScope() {
    return adaptee.getSpannedScope();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#getInterfaces()
   */
  @Override
  public List<JavaTypeSymbolReference> getInterfaces() {
    List<CDTypeSymbolReference> interfacesToAdapt = adaptee.getInterfaces();
    List<JavaTypeSymbolReference> adaptedInterfaces = new ArrayList<>();
    
    for (CDTypeSymbolReference interfaceToAdapt : interfacesToAdapt) {
      JavaTypeSymbolReference jSymbolRef = new JavaTypeSymbolReference(interfaceToAdapt.getName(),
          interfaceToAdapt.getEnclosingScope(), interfaceToAdapt.getDimension());
      jSymbolRef.setActualTypeArguments(interfaceToAdapt.getActualTypeArguments());
      jSymbolRef.setAccessModifier(interfaceToAdapt.getAccessModifier());
      adaptedInterfaces.add(jSymbolRef);
    }
    return adaptedInterfaces;
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#getFields()
   */
  @Override
  public List<JavaFieldSymbol> getFields() {
    List<CDFieldSymbol> fieldsToAdapt = adaptee.getFields();
    List<JavaFieldSymbol> adaptedFields = new ArrayList<>();
    for (CDFieldSymbol fieldToAdapt : fieldsToAdapt) {
      JavaTypeSymbolReference javaRef = getJavaRefFromCDRef(fieldToAdapt.getType(),
          fieldToAdapt.getEnclosingScope());      
      adaptedFields
          .add(getJavaFieldFromCDField(fieldToAdapt, javaRef));
    }
    return adaptedFields;
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#getField(java.lang.String)
   */
  @Override
  public Optional<JavaFieldSymbol> getField(String attributeName) {
    Optional<CDFieldSymbol> field = adaptee.getField(attributeName);
    if (field.isPresent()) {
      JavaTypeSymbolReference javaRef = getJavaRefFromCDRef(field.get().getType(),
          field.get().getEnclosingScope());
      
      return Optional.of(getJavaFieldFromCDField(field.get(), javaRef));
      
    }
    return Optional.empty();
  }
  
  public JavaFieldSymbol getJavaFieldFromCDField(CDFieldSymbol cdField,
      JavaTypeSymbolReference javaRef) {
    JavaFieldSymbol field = new JavaFieldSymbol(cdField.getName(),
        (JAttributeSymbolKind) cdField.getKind(),
        javaRef);
    
    field.setFullName(cdField.getFullName());
    return field;
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#getMethods()
   */
  @Override
  public List<JavaMethodSymbol> getMethods() {
    List<CDMethodSymbol> methodsToAdapt = adaptee.getMethods();
    List<JavaMethodSymbol> adaptedMethods = new ArrayList<>();
    for (CDMethodSymbol methToAdapt : methodsToAdapt) {
      JavaMethodSymbol javaMethod = new JavaMethodSymbol(methToAdapt.getName(),
          (JMethodSymbolKind) methToAdapt.getKind());
      javaMethod.setFullName(methToAdapt.getFullName());
      javaMethod.setReturnType(getJavaRefFromCDRef(methToAdapt.getReturnType(), methToAdapt.getEnclosingScope()));
      adaptedMethods.add(javaMethod);
    }
    return adaptedMethods;
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#getSuperClass()
   */
  @Override
  public Optional<JavaTypeSymbolReference> getSuperClass() {
    Optional<JavaTypeSymbolReference> optSuperClass = Optional.empty();
    Optional<CDTypeSymbolReference> optSupClassToAdapt = adaptee.getSuperClass();
    if (optSupClassToAdapt.isPresent()) {
      JavaTypeSymbolReference superClass = convertToJavaTypeReference(optSupClassToAdapt.get());
      optSuperClass = Optional.of(superClass);
    }
    return optSuperClass;
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isAbstract()
   */
  @Override
  public boolean isAbstract() {
    return adaptee.isAbstract();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isClass()
   */
  @Override
  public boolean isClass() {
    return adaptee.isClass();
  }
  
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isFinal()
   */
  @Override
  public boolean isFinal() {
    return adaptee.isFinal();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isFormalTypeParameter()
   */
  @Override
  public boolean isFormalTypeParameter() {
    return adaptee.isFormalTypeParameter();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isGeneric()
   */
  @Override
  public boolean isGeneric() {
    return adaptee.isGeneric();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isInnerType()
   */
  @Override
  public boolean isInnerType() {
    return adaptee.isInnerType();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isInterface()
   */
  @Override
  public boolean isInterface() {
    return adaptee.isInterface();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isPrivate()
   */
  @Override
  public boolean isPrivate() {
    return adaptee.isPrivate();
  }
  
  /**
   * @see de.monticore.symboltable.types.CommonJTypeSymbol#isProtected()
   */
  @Override
  public boolean isProtected() {
    return adaptee.isProtected();
  }
  
  private JavaTypeSymbolReference convertToJavaTypeReference(CDTypeSymbolReference cdRef) {
    JavaTypeSymbolReference javaSymbolRef = new JavaTypeSymbolReference(cdRef.getName(),
        cdRef.getEnclosingScope(), cdRef.getDimension());
    
    javaSymbolRef.setActualTypeArguments(cdRef.getActualTypeArguments());
    javaSymbolRef.setAccessModifier(cdRef.getAccessModifier());
    return javaSymbolRef;
  }
  
  private JavaTypeSymbolReference getJavaRefFromCDRef(CDTypeSymbolReference cdField,
      Scope enclosingScope) {
    JavaTypeSymbolReference ref = null;
    
    // check if field type is a cd type
    if (cdField.existsReferencedSymbol()) {
      ref = convertToJavaTypeReference(cdField);
    }
    // try to resolve as java type
    else {
      Optional<JavaTypeSymbol> sym = enclosingScope
          .<JavaTypeSymbol> resolve(cdField.getName(), JavaTypeSymbol.KIND);
      if (sym.isPresent()) {
        Scope definingScope = sym.get().getSpannedScope();
        String name = sym.get().getName();
        ref = new JavaTypeSymbolReference(name, definingScope, cdField.getDimension());
      }
    }
    
    return ref;
  }
  
}

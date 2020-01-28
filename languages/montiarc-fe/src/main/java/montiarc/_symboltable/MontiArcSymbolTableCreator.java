/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import de.monticore.ast.ASTNode;
import de.monticore.java.javadsl._ast.ASTDoWhileStatement;
import de.monticore.java.javadsl._ast.ASTEnhancedForControl;
import de.monticore.java.javadsl._ast.ASTForStatement;
import de.monticore.java.javadsl._ast.ASTFormalParameter;
import de.monticore.java.javadsl._ast.ASTIfStatement;
import de.monticore.java.javadsl._ast.ASTLocalVariableDeclaration;
import de.monticore.java.javadsl._ast.ASTSwitchBlockStatementGroup;
import de.monticore.java.javadsl._ast.ASTTryStatement;
import de.monticore.java.javadsl._ast.ASTVariableDeclarator;
import de.monticore.java.javadsl._ast.ASTWhileStatement;
import de.monticore.java.symboltable.JavaSymbolFactory;
import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.*;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.JTypeSymbolsHelper.JTypeReferenceFactory;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc.helper.JavaDefaultTypesManager;
import montiarc.helper.Timing;
import montiarc.trafos.AutoConnection;
import montiarc.trafos.SimpleConnectorToQualifiedConnector;
import montiarc.visitor.AssignmentNameCompleter;

/**
 * Visitor that creates the symboltable of a MontiArc AST.
 *
 * @author Robert Heim, Andreas Wortmann
 */
public class MontiArcSymbolTableCreator extends MontiArcSymbolTableCreatorTOP {
  
  protected String compilationUnitPackage = "";
  
  // extra stack of components that is used to determine which components are
  // inner components.
  protected Stack<ComponentSymbol> componentStack = new Stack<>();
  
  protected List<ImportStatement> currentImports = new ArrayList<>();
  
  protected AutoConnection autoConnectionTrafo = new AutoConnection();
  
  protected Stack<Boolean> autoinstantiate = new Stack<>();
  
  protected SimpleConnectorToQualifiedConnector simpleConnectorTrafo = new SimpleConnectorToQualifiedConnector();
  
  private ASTComponent currentComponent;
  
  private final static JavaSymbolFactory javaSymbolFactory = new JavaSymbolFactory();
  
  private final static JTypeReferenceFactory<JavaTypeSymbolReference> javaTypeRefFactory = (name,
      scope, dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }
  
  public MontiArcSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }
  
  @Override
  public void visit(ASTMACompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
        MontiArcSymbolTableCreator.class.getSimpleName());
    compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackageList());
    
    // imports
    List<ImportStatement> imports = new ArrayList<>();
    for (ASTImportStatementLOCAL astImportStatement : compilationUnit
        .getImportStatementLOCALList()) {
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport,
          astImportStatement.isStar());
      imports.add(importStatement);
    }
    JavaDefaultTypesManager.addJavaDefaultImports(imports);
    autoinstantiate.push(true);
    
    ArtifactScope artifactScope = new MontiArcArtifactScope(
        Optional.empty(),
        compilationUnitPackage,
        imports);
    this.currentImports = imports;
    artifactScope.setAstNode(compilationUnit);
    compilationUnit.setSpannedScope(artifactScope);
    putOnStack(artifactScope);
  }
  
  @Override
  public void endVisit(ASTMACompilationUnit node) {
    // artifact scope
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTPort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    List<String> names = node.getNameList();
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    
    for (String name : names) {
      PortSymbol sym = new PortSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      
      addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get());
      
      sym.setTypeReference(typeRef);
      sym.setDirection(node.isIncoming());
      
      // stereotype
      if (node.getStereotypeOpt().isPresent()) {
        for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
          sym.addStereotype(st.getName(), st.getValue());
        }
      }
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  @Override
  public void visit(ASTVariableDeclaration node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    
    List<String> names = node.getNameList();
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    for (String name : names) {
      VariableSymbol sym = new VariableSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get());
      
      sym.setTypeReference(typeRef);
      
      addToScopeAndLinkWithNode(sym, node);
    }
  }
  
  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef,
      ASTType astType, Scope definingScope) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, definingScope,
        javaTypeRefFactory);
  }

  private void addSubComponentSymbols(ASTSubComponent node) {
    String referencedCompName = TypesPrinter
        .printTypeWithoutTypeArgumentsAndDimension(node.getType());
    
    // String refCompPackage = Names.getQualifier(referencedCompName);
    String simpleCompName = Names.getSimpleName(referencedCompName);
    
    ComponentSymbolReference componentTypeReference = new ComponentSymbolReference(
        referencedCompName,
        currentScope().get());
    // actual type arguments
    addTypeArgumentsToComponent(componentTypeReference, node.getType());
    
    // ref.setPackageName(refCompPackage);
    List<ASTExpression> configArgs = new ArrayList<>();
    
    for (ASTExpression arg : node.getArgumentsList()) {
      arg.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(arg);
      configArgs.add(arg);
    }
    
    // instances
    if (!node.getInstancesList().isEmpty()) {
      // create instances of the referenced components.
      for (ASTSubComponentInstance i : node.getInstancesList()) {
        createInstance(i.getName(), i, componentTypeReference, configArgs);
      }
    }
    else {
      // auto instance because instance name is missing
      createInstance(StringTransformations.uncapitalize(simpleCompName), node,
          componentTypeReference, configArgs);
    }
    
    node.setEnclosingScope(currentScope().get());
  }
  
  /**
   * Creates the instance and adds it to the symTab.
   */
  private void createInstance(String name, ASTNode node,
      ComponentSymbolReference componentTypeReference,
      List<ASTExpression> configArguments) {
    ComponentInstanceSymbol instance = new ComponentInstanceSymbol(name,
        componentTypeReference);
    configArguments.forEach(v -> instance.addConfigArgument(v));
    // create a subscope for the instance
    // setLinkBetweenSymbolAndNode(instance, node);
    addToScopeAndLinkWithNode(instance, node);
    // remove the created instance's scope
    // removeCurrentScope();
  }
  
  @Override
  public void visit(ASTComponent node) {
    this.currentComponent = node;
    String componentName = node.getName();
    
    String componentPackageName = "";
    if (componentStack.isEmpty()) {
      // root component (most outer component of the diagram)
      componentPackageName = compilationUnitPackage;
    }
    else {
      // inner component uses its parents component full name as package
      componentPackageName = componentStack.peek().getFullName();
    }
    ComponentSymbol component = new ComponentSymbol(componentName);
    component.setImports(currentImports);
    component.setPackageName(componentPackageName);
    
    // generic type parameters
    addTypeParametersToComponent(component, node.getHead().getGenericTypeParametersOpt(),
        component.getSpannedScope());
    
    // parameters
    setParametersOfComponent(component, node.getHead());
    
    // stereotype
    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue st : node.getStereotype().getValuesList()) {
        component.addStereotype(st.getName(), Optional.of(st.getValue()));
      }
    }
    
    // check if this component is an inner component
    if (!componentStack.isEmpty()) {
      component.setDefiningComponent(componentStack.peek());
    }
    
    // timing
    component.setBehaviorKind(Timing.getBehaviorKind(node));
    
    componentStack.push(component);
    addToScopeAndLinkWithNode(component, node);
    
    // Transform SimpleConncetors to normal qaualified connectors
    for (ASTSubComponent astSubComponent : node.getSubComponents()) {
      for (ASTSubComponentInstance astSubComponentInstance : astSubComponent.getInstancesList()) {
        simpleConnectorTrafo.transform(astSubComponentInstance, component);
      }
    }
    
    autoinstantiate.push(autoinstantiate.peek());
    autoConnectionTrafo.transformAtStart(node, component);
  }
  
  @Override
  public void visit(ASTMontiArcAutoConnect node) {
    autoConnectionTrafo.transform(node, componentStack.peek());
  }
  
  @Override
  public void visit(ASTMontiArcAutoInstantiate node) {
    autoinstantiate.pop();
    autoinstantiate.push(node.isOn());
  }
  
  ;
  
  protected void setParametersOfComponent(final ComponentSymbol componentSymbol,
      final ASTComponentHead astMethod) {
    for (ASTParameter astParameter : astMethod.getParameterList()) {
      final String paramName = astParameter.getName();
      astParameter.setEnclosingScope(currentScope().get());
      setEnclosingScopeOfNodes(astParameter);
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astParameter.getType());
      JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
          TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astParameter
              .getType()),
          componentSymbol.getSpannedScope(), dimension);
      
      addTypeArgumentsToTypeSymbol(paramTypeSymbol, astParameter.getType(),
          componentSymbol.getSpannedScope());
      final JFieldSymbol parameterSymbol = javaSymbolFactory.createFormalParameterSymbol(paramName,
          (JavaTypeSymbolReference) paramTypeSymbol);
      componentSymbol.addConfigParameter(parameterSymbol);
    }
  }
  
  @Override
  public void endVisit(ASTComponent node) {
    ComponentSymbol component = componentStack.pop();
    
    // super component
    if (node.getHead().isPresentSuperComponent()) {
      ASTReferenceType superCompRef = node.getHead().getSuperComponent();
      String superCompName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(superCompRef);
      
      ComponentSymbolReference ref = new ComponentSymbolReference(superCompName,
          currentScope().get());
      ref.setAccessModifier(BasicAccessModifier.PUBLIC);
      // actual type arguments
      addTypeArgumentsToComponent(ref, superCompRef);
      
      component.setSuperComponent(ref);
    }

    for (ASTSubComponent subComp : node.getSubComponents()) {
      addSubComponentSymbols(subComp);
    }
    
    if (autoinstantiate.pop()) {
      Collection<ComponentInstanceSymbol> instanceSymbols = component.getSpannedScope()
          .resolveLocally(ComponentInstanceSymbol.KIND);
      Collection<ComponentSymbol> componentSymbols = new ArrayList<>(
          component.getSpannedScope().resolveLocally(ComponentSymbol.KIND));
      componentSymbols.removeIf(c -> instanceSymbols.stream()
          .anyMatch(i -> c.getName().equals(i.getComponentType().getName())));
      componentSymbols.forEach(componentSymbol -> {
        if (componentSymbol.hasConfigParameters() || componentSymbol.hasFormalTypeParameters()) {
          Log.error(String.format(
              "0xMA038 It was not possible to automatically create an instance " +
                  "of component %s because it has generic type or component parameters",
              componentSymbol.getName()));
        }
        else {
          ComponentSymbolReference symbolReference = new ComponentSymbolReference(
              componentSymbol.getName(), currentScope().get());
          String instanceName = StringTransformations.uncapitalize(componentSymbol.getName());
          ComponentInstanceSymbol instanceSymbol = new ComponentInstanceSymbol(instanceName
              , symbolReference);
          addToScope(instanceSymbol);
          
          ASTSubComponentInstance subComponentInstance = MontiArcMill.subComponentInstanceBuilder()
              .setName(instanceName).setSymbol(instanceSymbol)
              .setEnclosingScope(currentScope().get()).build();
          ASTSimpleReferenceType referenceType = MontiArcMill.simpleReferenceTypeBuilder()
              .addName(componentSymbol.getName()).build();
          ASTSubComponent subComponent = MontiArcMill.subComponentBuilder()
              .addInstances(subComponentInstance).setType(referenceType).build();
          node.getBody().addElement(subComponent);
          
          instanceSymbol.setAstNode(subComponentInstance);
          
          Log.debug("Automatically created component instance " + instanceSymbol.getName()
                  + " for inner component " + componentSymbol.getName(),
              MontiArcSymbolTableCreator.class.getSimpleName());
        }
      });
    }
    
    removeCurrentScope();
    
    // for inner components the symbol must be fully created to reference it.
    // Hence, in endVisit we
    // can reference it and put the instance of the inner component into its
    // parent scope.
    
    if (component.isInnerComponent()) {
      String referencedComponentTypeName = component.getFullName();
      ComponentSymbolReference refEntry = new ComponentSymbolReference(
          referencedComponentTypeName, component.getSpannedScope());
      refEntry.setReferencedComponent(Optional.of(component));
      
      if (node.getInstanceNameOpt().isPresent()) {
        if ((component.hasFormalTypeParameters()
                 && !node.getActualTypeArgumentOpt().isPresent())
                || component.hasConfigParameters()) {
          Log.error(String.format(
              "0xMA038 It was not possible to automatically create an instance " +
                  "of component %s because it has type or constructor " +
                  "parameters that were not assigned in the instance.",
              component.getName()));
        }
        else {
          // create instance
          String instanceName = node.getInstanceNameOpt().get();
          
          if (node.getActualTypeArgumentOpt().isPresent()) {
            setActualTypeArgumentsOfCompRef(refEntry,
                node.getActualTypeArgument().getTypeArgumentList());
          }
          
          ComponentInstanceSymbol instanceSymbol
              = new ComponentInstanceSymbol(instanceName, refEntry);
          Log.debug("Created component instance " + instanceSymbol.getName()
                  + " referencing component type " + referencedComponentTypeName,
              MontiArcSymbolTableCreator.class.getSimpleName());
          
          addToScope(instanceSymbol);
        }
      }
      // check whether there are already instances of the inner component type
      // defined in the component type. We then have to set the referenced
      // component.
      Collection<ComponentInstanceSymbol> instances = component.getEnclosingScope()
          .resolveLocally(ComponentInstanceSymbol.KIND);
      
      for (ComponentInstanceSymbol instance : instances) {
        if (instance.getComponentType().getName().equals(component.getName())) {
          instance.getComponentType().setReferencedComponent(Optional.of(component));
        }
      }
    }
    autoConnectionTrafo.transformAtEnd(node, component);
  }
  
  private void setActualTypeArgumentsOfCompRef(ComponentSymbolReference typeReference,
      List<ASTTypeArgument> astTypeArguments) {
    List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();
    for (ASTTypeArgument astTypeArgument : astTypeArguments) {
      if (astTypeArgument instanceof ASTWildcardType) {
        ASTWildcardType astWildcardType = (ASTWildcardType) astTypeArgument;
        
        // Three cases can occur here: lower bound, upper bound, no bound
        if (astWildcardType.isPresentLowerBound() || astWildcardType.isPresentUpperBound()) {
          // We have a bound.
          // Examples: Set<? extends Number>, Set<? super Integer>
          
          // new bound
          boolean lowerBound = astWildcardType.isPresentLowerBound();
          ASTType typeBound = lowerBound
              ? astWildcardType.getLowerBound()
              : astWildcardType.getUpperBound();
          int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(typeBound);
          JTypeReference<? extends JTypeSymbol> typeBoundSymbolReference = new JavaTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(typeBound),
              currentScope().get(), dimension);
          ActualTypeArgument actualTypeArgument = new ActualTypeArgument(lowerBound, !lowerBound,
              typeBoundSymbolReference);
          
          // init bound
          addTypeArgumentsToTypeSymbol(typeBoundSymbolReference, typeBound, currentScope().get());
          
          actualTypeArguments.add(actualTypeArgument);
        }
        else {
          // No bound. Example: Set<?>
          actualTypeArguments.add(new ActualTypeArgument(false, false, null));
        }
      }
      else if (astTypeArgument instanceof ASTType) {
        // Examples: Set<Integer>, Set<Set<?>>, Set<java.lang.String>
        ASTType astTypeNoBound = (ASTType) astTypeArgument;
        int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astTypeNoBound);
        JTypeReference<? extends JTypeSymbol> typeArgumentSymbolReference = new JavaTypeSymbolReference(
            TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astTypeNoBound),
            currentScope().get(), dimension);
        
        addTypeArgumentsToTypeSymbol(typeArgumentSymbolReference, astTypeNoBound,
            currentScope().get());
        
        actualTypeArguments.add(new ActualTypeArgument(typeArgumentSymbolReference));
      }
      else {
        Log.error("0xMA073 Unknown type argument " + astTypeArgument + " of type "
            + typeReference);
      }
    }
    typeReference.setActualTypeArguments(actualTypeArguments);
  }
  
  protected JTypeReferenceFactory<JavaTypeSymbolReference> typeRefFactory = (name, scope,
      dim) -> new JavaTypeSymbolReference(name, scope, dim);
  
  protected void addTypeArgumentsToComponent(ComponentSymbolReference typeReference,
      ASTType astType) {
    if (astType instanceof ASTSimpleReferenceType) {
      ASTSimpleReferenceType astSimpleReferenceType = (ASTSimpleReferenceType) astType;
      if (!astSimpleReferenceType.getTypeArgumentsOpt().isPresent()) {
        return;
      }
      setActualTypeArgumentsOfCompRef(typeReference,
          astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
    }
    else if (astType instanceof ASTComplexReferenceType) {
      ASTComplexReferenceType astComplexReferenceType = (ASTComplexReferenceType) astType;
      for (ASTSimpleReferenceType astSimpleReferenceType : astComplexReferenceType
          .getSimpleReferenceTypeList()) {
        /* ASTComplexReferenceType represents types like class or interface
         * types which always have ASTSimpleReferenceType as qualification. For
         * example: a.b.c<Arg>.d.e<Arg> */
        setActualTypeArgumentsOfCompRef(typeReference,
            astSimpleReferenceType.getTypeArguments().getTypeArgumentList());
      }
    }
    
  }
  
  /**
   * Adds the TypeParameters to the ComponentSymbol if it declares
   * TypeVariables. Since the restrictions on TypeParameters may base on the
   * JavaDSL its the actual recursive definition of bounds is respected and its
   * implementation within the JavaDSL is reused. Example:
   * <p>
   * component Bla<T, S extends SomeClass<T> & SomeInterface>
   * </p>
   * T and S are added to Bla.
   *
   * @param componentSymbol
   * @param optionalTypeParameters
   * @return currentScope
   * @see JTypeSymbolsHelper
   */
  protected static List<JTypeSymbol> addTypeParametersToComponent(
      ComponentSymbol componentSymbol, Optional<ASTTypeParameters> optionalTypeParameters,
      Scope currentScope) {
    if (optionalTypeParameters.isPresent()) {
      // component has type parameters -> translate AST to Java Symbols and add
      // these to the
      // componentSymbol.
      ASTTypeParameters astTypeParameters = optionalTypeParameters.get();
      for (ASTTypeVariableDeclaration astTypeParameter : astTypeParameters
          .getTypeVariableDeclarationList()) {
        // TypeParameters/TypeVariables are seen as type declarations.
        JavaTypeSymbol javaTypeVariableSymbol = javaSymbolFactory
            .createTypeVariable(astTypeParameter.getName());
        
        List<ASTType> types = new ArrayList<ASTType>(astTypeParameter.getUpperBoundList());
        // reuse JavaDSL
        JTypeSymbolsHelper.addInterfacesToType(javaTypeVariableSymbol, types, currentScope,
            javaTypeRefFactory);
        
        componentSymbol.addFormalTypeParameter(javaTypeVariableSymbol);
      }
    }
    return componentSymbol.getFormalTypeParameters();
  }
  
  /***************************************
   * Java/P integration
   ***************************************/
  
  @Override
  public void visit(ASTJavaPBehavior node) {
    // Add the presence of a behavior element to the component symbol
    if (currentScope().isPresent()) {
      if (currentScope().get().getSpanningSymbol().isPresent()) {
        ((ComponentSymbol) currentSymbol().get()).setHasBehavior(true);
      }
    }
    createScope(node);
  }
  
  @Override
  public void endVisit(ASTJavaPBehavior node) {
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  public void visit(ASTValueInitialization init) {
    Scope enclosingScope = currentScope().get();
    String qualifiedName = init.getQualifiedName().toString();
    Optional<VariableSymbol> var = enclosingScope
        .<VariableSymbol>resolve(qualifiedName, VariableSymbol.KIND);
    if (var.isPresent()) {
      var.get().setValuation(Optional.of(init.getValuation()));
    }
    
  }
  
  /**
   * Visits ASTLocalVariableDeclaration nodes that occur in AJava compute
   * blocks. A new VariableSymbol is created for declared variables and added to
   * the scope
   *
   * @param variableDeclaration ASTNode that is a variable declaration.
   */
  public void visit(ASTLocalVariableDeclaration variableDeclaration) {
    final ASTType type = variableDeclaration.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type);
    
    List<String> names = new ArrayList<>();
    
    for (ASTVariableDeclarator astVariableDeclarator : variableDeclaration
        .getVariableDeclaratorList()) {
      final String variableName = astVariableDeclarator.getDeclaratorId().getName();
      names.add(variableName);
    }
    
    if (names.isEmpty()) {
      names.add(StringTransformations.uncapitalize(typeName));
    }
    
    for (String name : names) {
      VariableSymbol variableSymbol = new VariableSymbol(name);
      
      int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(type);
      JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
          currentScope().get(), dimension);
      addTypeArgumentsToTypeSymbol(typeRef, type, currentScope().get());
      
      variableSymbol.setTypeReference(typeRef);
      
      addToScopeAndLinkWithNode(variableSymbol, variableDeclaration);
    }
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTEnhancedForControl)
   */
  @Override
  public void visit(ASTEnhancedForControl node) {
    ASTFormalParameter param = node.getFormalParameter();
    final ASTType type = param.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(type);
    
    VariableSymbol variableSymbol = new VariableSymbol(param.getDeclaratorId().getName());
    int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(type);
    JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
        currentScope().get(), dimension);
    addTypeArgumentsToTypeSymbol(typeRef, type, currentScope().get());
    
    variableSymbol.setTypeReference(typeRef);
    
    addToScopeAndLinkWithNode(variableSymbol, node);
  }
  
  /***************************************
   * I/O Automaton integration
   ***************************************/
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#visit(ASTAutomatonBehavior)
   */
  @Override
  public void visit(ASTAutomatonBehavior node) {
    // Add the presence of a behavior element to the component symbol
    if (currentScope().isPresent()) {
      if (currentScope().get().getSpanningSymbol().isPresent()) {
        ((ComponentSymbol) currentSymbol().get()).setHasBehavior(true);
      }
    }
    createScope(node);
  }
  
  /**
<<<<<<< HEAD
   * @see montiarc._visitor.MontiArcVisitor#endVisit(montiarc._ast.ASTAutomatonBehavior)
=======
   * @see montiarc._visitor.MontiArcVisitor#endVisit(ASTAutomatonBehavior)
>>>>>>> origin/xtend-generator
   */
  @Override
  public void endVisit(ASTAutomatonBehavior node) {
    removeCurrentScope();
  }
  
  /**
<<<<<<< HEAD
   * @see montiarc._visitor.MontiArcVisitor#visit(montiarc._ast.ASTAutomaton)
=======
   * @see montiarc._visitor.MontiArcVisitor#visit(ASTAutomaton)
>>>>>>> origin/xtend-generator
   */
  @Override
  public void visit(ASTAutomaton node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  @Override
  public void endVisit(ASTAutomaton node) {
    setEnclosingScopeOfNodes(node);
    // automaton core loaded & all enclosing scopes set, so we can reconstruct
    // the missing assignment names
    node.accept(new AssignmentNameCompleter(currentScope().get()));
  }
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#visit(ASTState)
   */
  @Override
  public void visit(ASTState node) {
    StateSymbol state = new StateSymbol(node.getName());
    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue value : node.getStereotype().getValuesList()) {
        state.addStereoValue(value.getName());
      }
    }
    addToScopeAndLinkWithNode(state, node);
  }
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#endVisit(montiarc._ast.ASTState)
   */
  @Override
  public void endVisit(ASTState node) {
    super.endVisit(node);
  }
  
  /**
   * @see montiarc._visitor.MontiArcVisitor#visit(ASTInitialStateDeclaration)
   */
  @Override
  public void visit(ASTInitialStateDeclaration node) {
    MutableScope scope = currentScope().get();
    for (String name : node.getNameList()) {
      scope.<StateSymbol>resolveMany(name, StateSymbol.KIND).forEach(c -> {
        c.setInitial(true);
        c.setInitialReactionAST(node.getBlockOpt());
        if (node.isPresentBlock()) {
          for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
            if (assign.isPresentName()) {
              Optional<VariableSymbol> var = currentScope().get()
                  .<VariableSymbol>resolve(assign.getName(), VariableSymbol.KIND);
              if (var.isPresent()) {
                if (assign.getValueListOpt().isPresent()
                    && !assign.getValueList().getAllValuations().isEmpty()) {
                  // This only covers the case "var i = somevalue"
                  ASTValuation v = assign.getValueList().getAllValuations().get(0);
                  var.get().setValuation(Optional.of(v));
                }
              }
            }
          }
        }
      });
    }
  }
  
  @Override
  public void visit(ASTTransition node) {
    // get target name, if there is no get source name (loop to itself)
    // TODO what about same transitions with other stimulus? -> name clash
    String targetName = node.getTargetOpt().orElse(node.getSource());
    
    StateSymbolReference source = new StateSymbolReference(node.getSource(), currentScope().get());
    StateSymbolReference target = new StateSymbolReference(targetName, currentScope().get());
    
    TransitionSymbol transition = new TransitionSymbol(node.getSource() + " -> " + targetName);
    transition.setSource(source);
    transition.setTarget(target);

    if(node.getGuardOpt().isPresent()) {
      transition.setGuardAST(node.getGuardOpt().get());
    }
    if(node.getReactionOpt().isPresent()) {
      transition.setReactionAST(node.getReactionOpt().get());
    }
    
    addToScopeAndLinkWithNode(transition, node); // introduces new scope
  }
  
  @Override
  public void endVisit(ASTTransition node) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTIOAssignment node) {
    node.setEnclosingScope(currentScope().get());
  }
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#visit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void visit(ASTStateDeclaration ast) {
    // to prevent creation of unnecessary scope we override with nothing
  }
  
  /**
   * @see montiarc._symboltable.MontiArcSymbolTableCreatorTOP#endVisit(montiarc._ast.ASTStateDeclaration)
   */
  @Override
  public void endVisit(ASTStateDeclaration ast) {
    // to prevent deletion of not existing scope we override with nothing
  }
  
  /////////////// JAVA/P scope spanning Statements
  /////////////// ////////////////////////////////////////
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTIfStatement)
   */
  @Override
  public void visit(ASTIfStatement node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTIfStatement)
   */
  @Override
  public void endVisit(ASTIfStatement node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTForStatement)
   */
  @Override
  public void visit(ASTForStatement node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTForStatement)
   */
  @Override
  public void endVisit(ASTForStatement node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTWhileStatement)
   */
  @Override
  public void visit(ASTWhileStatement node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTWhileStatement)
   */
  @Override
  public void endVisit(ASTWhileStatement node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTDoWhileStatement)
   */
  @Override
  public void visit(ASTDoWhileStatement node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTDoWhileStatement)
   */
  @Override
  public void endVisit(ASTDoWhileStatement node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTTryStatement)
   */
  @Override
  public void visit(ASTTryStatement node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTTryStatement)
   */
  @Override
  public void endVisit(ASTTryStatement node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#visit(de.monticore.java.javadsl._ast.ASTSwitchBlockStatementGroup)
   */
  @Override
  public void visit(ASTSwitchBlockStatementGroup node) {
    super.visit(node);
    createScope(node);
  }
  
  /**
   * @see de.monticore.java.javadsl._visitor.JavaDSLVisitor#endVisit(de.monticore.java.javadsl._ast.ASTSwitchBlockStatementGroup)
   */
  @Override
  public void endVisit(ASTSwitchBlockStatementGroup node) {
    super.endVisit(node);
    setEnclosingScopeOfNodes(node);
    removeCurrentScope();
  }
  
  protected void createScope(ASTNode node) {
    MutableScope spannedScope = new CommonScope(false);
    spannedScope.setResolvingFilters(currentScope().get().getResolvingFilters());
    spannedScope.setEnclosingScope(currentScope().get());
    node.setEnclosingScope(currentScope().get());
    node.setSpannedScope(spannedScope);
    this.scopeStack.addLast(spannedScope);
  }
  
}

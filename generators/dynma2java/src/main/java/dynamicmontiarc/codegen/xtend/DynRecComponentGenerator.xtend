/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend

import de.montiarcautomaton.generator.codegen.xtend.util.Utils
import de.montiarcautomaton.generator.helper.ComponentHelper
import dynamicmontiarc.helper.DynamicMontiArcHelper
import montiarc._ast.ASTComponent
import montiarc._symboltable.ComponentSymbol
import dynamicmontiarc._ast.ASTModeAutomaton
import java.util.List
import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentSymbolReference
import java.util.ArrayList
import de.montiarcautomaton.generator.codegen.xtend.util.Identifier
import dynamicmontiarc.codegen.xtend.util.UpdatePortReferences
import dynamicmontiarc.codegen.xtend.util.UpdateMode
import de.montiarcautomaton.generator.codegen.xtend.ComponentGenerator
import dynamicmontiarc.codegen.xtend.util.DynRecInit
import dynamicmontiarc.codegen.xtend.util.DynRecSetup
import dynamicmontiarc.codegen.xtend.util.DynRecUpdate

/**
 * Generates the component class for atomic and composed components.
 */
class DynRecComponentGenerator extends ComponentGenerator{
  var String generics;
  var ComponentHelper helper;
  
  new() {
    init = new DynRecInit
    setup = new DynRecSetup
    update = new DynRecUpdate
  }

  override generate(ComponentSymbol comp) {
    generics = Utils.printFormalTypeParameters(comp)
    helper = new ComponentHelper(comp);
	var compNode = comp.astNode.get as ASTComponent
	var isDynamic = DynamicMontiArcHelper.isDynamic(compNode)

    return '''
			«Utils.printPackage(comp)»
			
			«Utils.printImports(comp)»
			import «Utils.printPackageWithoutKeyWordAndSemicolon(comp)».«comp.name»Input;
			import «Utils.printPackageWithoutKeyWordAndSemicolon(comp)».«comp.name»Result;
			import de.montiarcautomaton.runtimes.timesync.delegation.IComponent;
			import de.montiarcautomaton.runtimes.timesync.delegation.Port;
			import de.montiarcautomaton.runtimes.timesync.implementation.IComputable;
			import de.montiarcautomaton.runtimes.Log;
			
			public class «comp.name»«generics»      
			  «IF comp.superComponent.present» extends «Utils.printSuperClassFQ(comp)» 
			  	    «IF comp.superComponent.get.hasFormalTypeParameters»<«FOR scTypeParams : helper.superCompActualTypeArguments SEPARATOR ','»
			  	    	«scTypeParams»«ENDFOR»>
			  	    «ENDIF»
			  «ENDIF»
			  implements IComponent {
			    
			  //ports
			  «ports.print(comp.ports)»
			  
			  // component variables
			  «Utils.printVariables(comp)»
			  
			  // config parameters
			  «Utils.printConfigParameters(comp)»
			
			  «IF isDynamic»
			    // State of the mode automaton
			    «comp.name»Mode currentMode = null;
			    
			    «printModeEnum(comp, DynamicMontiArcHelper.getModeAutomaton(compNode))»
			  «ENDIF»
			
«««			  CASE Composed Component, dynamic, or static
			  «IF comp.isDecomposed»
			  	// Embedded component instances
			  	«subcomponents.print(comp)»
			  	
			  	«IF isDynamic»
			  	«printComputeComposedDynamic(comp)»
			  	  «printReconfigureDynamic(comp)»
			  	«ELSE»
			  	  «printComputeComposed(comp)»
			  	  «printReconfigureComposedNotDynamic(comp)»
			  	«ENDIF»
			  	
«««			  CASE Atomic Component
			  «ELSE»
			  	// the components behavior implementation
			  	private final IComputable<«comp.name»Input«generics», «comp.name»Result«generics»> «Identifier.behaviorImplName»;
			  	
			  	«printComputeAtomic(comp)»
			  	private void initialize() {
			  	  // get initial values from behavior implementation
			  	  final «comp.name»Result«generics» result = «Identifier.behaviorImplName».getInitialValues();
			  	  
			  	  // set results to ports
			  	  setResult(result);
			  	  this.update();
			  	}
			  	private void setResult(«comp.name»Result«generics» result) {
			  	  «FOR portOut : comp.outgoingPorts»
			  	  	this.getPort«portOut.name.toFirstUpper»().setNextValue(result.get«portOut.name.toFirstUpper»());
			  	  «ENDFOR»
			  	}
			  	
			  	«printReconfigureAtomic()»
			  «ENDIF»
			  
			  «setup.print(comp)»
			  
			  «init.print(comp)»
			  
			  «update.print(comp)»
			  
			  «printConstructor(comp)»
			  
			  «IF isDynamic»
			  	«UpdateMode.printUpdateMode(comp, DynamicMontiArcHelper.getModeAutomaton(compNode))»
			  «ENDIF»
			  «UpdatePortReferences.printUpdatePortReferences(comp)»
			  }
			  
		'''
  }



  def printComputeComposedDynamic(ComponentSymbol comp) {
    var node = comp.astNode.get as ASTComponent
    var automaton = DynamicMontiArcHelper.getModeAutomaton(node)
    return '''
      @Override
      public void compute() {
      // trigger computation in all subcomponent instances
        «FOR modeName : automaton.modeNames»
          if(this.currentMode.equals(«node.getName()»Mode.«modeName»)) {
            «FOR instanceName : automaton.getActiveSubcomponentsInMode(modeName)»
            this.«instanceName».compute();
            «ENDFOR»
          }
        «ENDFOR»
      }
    '''
  }


  def printReconfigureDynamic(ComponentSymbol comp){
    return '''
  //Reconfigure
  public void reconfigure() {
  «FOR component : comp.subComponents»
    this.«component.name».reconfigure();
  «ENDFOR»
  this.updateMode();
  this.updatePortReferences();
  }
    '''
  }
  
  def printReconfigureComposedNotDynamic(ComponentSymbol comp) {
    return '''
      //Reconfigure
      public void reconfigure() {
      «FOR component : comp.subComponents»
        this.«component.getName()».reconfigure();
      «ENDFOR»
      }
    '''
  }
  
  def printReconfigureAtomic() {
  	return '''
	  	//Reconfigure will be called by supercomponents even for atomic components
	  	public void reconfigure() {}
  	'''
  }
  
  def printModeEnum(ComponentSymbol compSymbol, ASTModeAutomaton automaton){
  	return '''
  		enum «compSymbol.name»Mode {
  			«FOR modeName : automaton.getModeNames() SEPARATOR ','»
  			«modeName»
  			«ENDFOR»
  		}
  	'''
  }

}

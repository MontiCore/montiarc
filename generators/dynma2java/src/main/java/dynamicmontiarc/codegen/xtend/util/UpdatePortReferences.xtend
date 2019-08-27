/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.codegen.xtend.util

import montiarc._symboltable.ComponentSymbol
import montiarc._ast.ASTComponent
import dynamicmontiarc.helper.DynamicMontiArcHelper

/**
 * TODO: Write me!
 * 
 * @author  (last commit) Mutert
 * 
 */
class UpdatePortReferences {

	def static printUpdatePortReferences(ComponentSymbol comp) {

		var compNode = comp.astNode.get as ASTComponent
		var isDynamic = DynamicMontiArcHelper.isDynamic(compNode)

		if(comp.isAtomic) {
			return '''
				public void updatePortReferences() { 
					
				}
			'''
		}

		if(isDynamic) {
			var automaton = DynamicMontiArcHelper.getModeAutomaton(compNode)

			return '''
				public void updatePortReferences() { 
					«FOR modeName : automaton.modeNames»
					if(this.currentMode.equals(«comp.name»Mode.«modeName») ) {
						«FOR connector : automaton.getConnectorsInMode(modeName)»
							«FOR target : connector.targetsList»
							    «IF target.sizeParts == 1»
«««							    Target is a output port of the current component, source an embedded component
							    «target.toString» = «connector.source.partList.get(0)».getPort«connector.source.getPart(1).toFirstUpper»();
							    «ELSEIF connector.source.sizeParts == 1»
«««							    Source is an input port of the component, target a port of an embedded component
							    «target.getPart(0)».setPort«target.getPart(1).toFirstUpper»(this.getPort«connector.source.toString.toFirstUpper»());
							    «ELSE»
«««							    Source and target are embedded components
							    «target.getPart(0)».setPort«target.getPart(1).toFirstUpper»(«connector.source.getPart(0)».getPort«connector.source.getPart(1).toFirstUpper»());
							    «ENDIF»
							«ENDFOR»
						«ENDFOR»
						
						«FOR activeComp : automaton.getActiveSubcomponentsInMode(modeName)»
						  «activeComp».updatePortReferences();
						«ENDFOR»
					}
					«ENDFOR»
				}
			'''
		} else {
			return '''
				public void updatePortReferences() { 
					«FOR connector : compNode.connectors»
					  «FOR target : connector.targetsList»
					    «IF target.sizeParts == 1»
«««						Target is a output port of the current component, source an embedded component
					    «target.toString» = «connector.source.partList.get(0)».getPort«connector.source.getPart(1).toFirstUpper»();
					    «ELSEIF connector.source.sizeParts == 1»
«««						Source is an input port of the component, target a port of an embedded component
					    «target.getPart(0)».setPort«target.getPart(1).toFirstUpper»(this.getPort«connector.source.toString.toFirstUpper»());
					    «ELSE»
«««						Source and target are embedded components
					    «target.getPart(0)».setPort«target.getPart(1).toFirstUpper»(«connector.source.getPart(0)».getPort«connector.source.getPart(1).toFirstUpper»());
					    «ENDIF»
					  «ENDFOR»
					«ENDFOR»
					
					«FOR embeddedComp : comp.subComponents»
					«embeddedComp.name».updatePortReferences();
					«ENDFOR»
				}
			'''
		}

	}
}

/* (c) https://github.com/MontiCore/monticore */
package components.body.autoinstantiate;

/**
 * Valid model.
 */
component AutoInstanciateOn {

	autoinstantiate on;
	
	port in String strIn;
        
	// create
	component InnerNotInstanciated {
	   port in String strIn1;
	}
	
	// do not create 
	component InnerInstanciated {
		port in String strIn2;
		// create
		component InnerInnerNotInstanciated {
		  port in String strIn3;
		}
		
		// do not create
		component InnerInnerInstanciated {
		  port in String strIn4;
		}
		
		component InnerInnerInstanciated myInnerInnerInstanciated;
		
		connect strIn2 -> myInnerInnerInstanciated.strIn4, innerInnerNotInstanciated.strIn3;
	}
	
	component InnerInstanciated myInnerInstanciated;
	
	// do not create
	component InnerWithGenerics<K> {
	   port in String strIn5;
	}
	
	// do not create
	component InnerWithConfig(int i) {
	   port in String strIn6;
	}
	
	// do not create
	component InnerWithGenericsAndConfig<K>(int i) {
	   port in String strIn7;
	}
	
	connect strIn -> myInnerInstanciated.strIn2, innerNotInstanciated.strIn1;
}

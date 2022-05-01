/* (c) https://github.com/MontiCore/monticore */
package components.body.autoinstantiate;

/**
 * Valid model.
 */
component AutoInstanciateOff {

	autoinstantiate off;

	port 
        in String strIn;
	
	component InnerNotInstanciated {
	   port 
        in String strIn1;
	}
	
	component InnerInstanciated {
	    port 
            in String strIn10;
		component InnerInnerNotInstanciated {
		  port 
            in String strIn2;
		}
		
		component InnerInnerInstanciated {
		  port 
            in String strIn3;
		}
		
		component InnerInnerInstanciated myInnerInnerInstanciated;
		
		connect strIn10 -> myInnerInnerInstanciated.strIn3;
	}
	
	component InnerWithGenerics<K> {
	   port 
        in String strIn4;
	}
	
	component InnerWithConfig(int i) {
	   port 
        in String strIn5;
	}
	
	component InnerWithGenericsAndConfig<K>(int i) {
	   port 
        in String strIn6;
	}
	
	component InnerInstanciated myInnerInstanciated;
	
	connect strIn -> myInnerInstanciated.strIn10;
}

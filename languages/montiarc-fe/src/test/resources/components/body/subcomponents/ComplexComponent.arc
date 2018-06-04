package components.body.subcomponents;


import components.body.subcomponents.SimpleReferencedComponent;
import components.body.subcomponents.SimpleReferencedComponent.*;

/*
 * Valid model.
 */
component ComplexComponent {
    port
        in String input,
        out String output1,
        out String output2;
        
	component SimpleReferencedComponent src;
	
	component Sub1 b2;
	
	component Sub2<String> myC;
	
	component Sub3 c2;
	
	component components.body.subcomponents._subcomponents.Sub4 qFComponent1;
	
	component components.body.subcomponents._subcomponents.Sub5 qfc2;
	
	connect input -> src.input, myC.input;
	connect myC.output -> output1;
	connect src.output -> output2;
}
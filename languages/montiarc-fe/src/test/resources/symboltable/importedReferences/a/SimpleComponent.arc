package a;

import b.SimpleReferencedComponent;
import b.B2;
import c.*;

component SimpleComponent {
    port
        in String input,
        out String output1,
        out String output2;
        
	component SimpleReferencedComponent src;
	
	component B2;
	
	component C<String> myC;
	
	component C2;
	
	component d.QFComponent1;
	
	component d.QFComponent2 qfc2;
	
	connect input -> src.input, myC.input;
	connect myC.output -> output1;
	connect src.output -> output2;
}
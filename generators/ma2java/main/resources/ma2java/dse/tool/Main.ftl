<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ast", "componentNames", "imports")}

package main;

<#list imports as import>
import ${import}.*;
</#list>

public class MainDse{

	static private DSEMain dseMain;

	public static void main (String[] args) throws Exception {
 		switch(args[0]){
 			<#list componentNames as name>
 				case "${name}":
 					dseMain = new DSEMain${name}();
 					break;
 			</#list>
 			default:
 				montiarc.rte.log.Log.trace("no controller could be choosen");
 		}
 		dseMain.runController(args);
 	}
}

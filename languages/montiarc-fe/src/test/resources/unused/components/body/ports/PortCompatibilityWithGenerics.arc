package components.body.ports;

import types.GenericComponent;
import java.util.*;

/**
* Invalid component. See comments below.
*/
component PortCompatibilityWithGenerics {


    // Testing compatibility checks of generic types
    
    port
    	in String strIn1,
    	in Boolean boolIn1,
    	in List<Boolean> listBoolIn,
    	in List<String> listStringIn,
    	in List<List<Boolean>> listListBoolIn,
    	in List<List<String>> listListStringIn,
    	in List<Map<Integer, String>> listMapIntStringIn,
    	in List<Map<String, Integer>> listMapStringIntIn,
    	in List<Map<String, String>> listMapStringStringIn,
    	out Integer intOut3,
    	out Boolean boolOut1,
    	out List<Boolean> listBoolOut,
    	out List<String> listStringOut,
    	out List<Map<Integer, String>> listMapIntStringOut,
        out List<Map<String, Integer>> listMapStringIntOut;
    	
    component GenericComponent<String, Integer> myGenComp;
 
    connect strIn1 -> myGenComp.myKIn1; // compatible (String -> String)
    connect myGenComp.myVOut -> intOut3; // compatible (Integer -> Integer)
    
    connect boolIn1 -> myGenComp.myKIn2; // incompatible (Boolean -> String)
    connect myGenComp.myVOut -> boolOut1; // incompatible (Integer -> Boolean)
    
    connect listBoolIn -> myGenComp.myListKIn1; // incompatible (List<Boolean> -> List<String>)
    connect listStringIn -> myGenComp.myListKIn2; //compatible (List<String> -> List<String>)
    
    connect listMapIntStringIn -> myGenComp.myListMapKVIn1; // incompatible (List<Map<Integer,String> -> List<Map<String,Integer>>)
    connect listMapStringIntIn -> myGenComp.myListMapKVIn2; // compatible (List<Map<String,Integer>> -> List<Map<String,Integer>>)
    
    connect myGenComp.myListKOut -> listBoolOut; // incompatible (List<String> -> List<Boolean>)
    connect myGenComp.myListKOut -> listStringOut; //compatible (List<String> -> List<String>)
    
    connect myGenComp.myListMapKVOut -> listMapIntStringOut; // incompatible (List<Map<String,Integer> -> List<Map<Integer,String>>)
    connect myGenComp.myListMapKVOut -> listMapStringIntOut; // compatible (List<Map<String,Integer>> -> List<Map<String,Integer>>)

    connect listMapStringIntIn -> myGenComp.myListMapKKIn1; // incompatible (List<Map<String,Integer> -> List<Map<String,String>>)
    connect listMapStringStringIn -> myGenComp.myListMapKKIn2; // compatible (List<Map<String,String> -> List<Map<String,String>>)

    // Testing compatibility checks for generic types and references with nested generic types

    component GenericComponent<List<String>, Integer> myGenComp2;
    
    connect listBoolIn -> myGenComp2.myKIn1; // incompatible (List<Boolean> -> List<String>)
    connect listStringIn -> myGenComp2.myKIn2; // compatible (List<String> -> List<String>)

    connect listListBoolIn -> myGenComp2.myListKIn1; // incompatible (List<List<Boolean>> -> List<List<String>>)
    connect listListStringIn -> myGenComp2.myListKIn2; // compatible (List<List<String>> -> List<List<String>>)
    
    port
        in List<Map<List<String>, Integer>> in1,
        in List<Map<List<String>, List<String>>> in2,
        out Integer out1,
        out List<List<String>> out2,
        out List<Map<List<String>, Integer>> out3;
         
    connect in1 -> myGenComp2.myListMapKVIn1, myGenComp2.myListMapKVIn2; // compatible List<Map<List<String>, Integer>> -> List<Map<List<String>,Integer>>
    connect in2 -> myGenComp2.myListMapKKIn1, myGenComp2.myListMapKKIn2; // compatible List<Map<List<String>, List<String>>> -> List<Map<List<String>,List<String>>>
    connect myGenComp2.myVOut -> out1;          // compatible Integer -> Integer
    connect myGenComp2.myListKOut -> out2;      // compatible List<List<String>> -> List<List<String>>
    connect myGenComp2.myListMapKVOut -> out3;  // compatible List<Map<List<String>,Integer>> -> List<Map<List<String>, Integer>>
   
}
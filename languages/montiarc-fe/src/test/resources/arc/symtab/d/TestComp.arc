package d;

import java.util.*;

component TestComp<K,V>(List<K> liskK, List<Map<K,V>> listMapKV) {
  port 
    in GenericPort<Map<String, Boolean>, List<Map<Integer, Integer>>> portOne,
    in GenericPort<Integer, Double> portTwo,
    in List<List<String>> portThree;
    
  component GenericComp<List<List<String>>, GenericPort<Integer, Double>> refToGenericComp;
  
  component GenericComp<GenericPort<Map<String, Boolean>, List<Map<Integer, Integer>>>, String> refToGenericComp2;
  component GenericComp<GenericPort<Integer, Double>, String> refToGenericComp3;
  
  connect portOne -> refToGenericComp2.portIn;
  connect portTwo -> refToGenericComp3.portIn;
  connect portThree -> refToGenericComp.portIn;
  
}
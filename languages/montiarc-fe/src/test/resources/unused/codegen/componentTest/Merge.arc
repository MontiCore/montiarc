package componentTest;

component Merge<T>(String encoding) {
  
  port 
    in T input1,
    in T input2, 
    in T input3, 
    in T input4,
    out T output1,
    out T output2;   
}
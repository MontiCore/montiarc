package pretty;

<<author="Haber", test>>
component Stereotypes {
  
    <<test3>>
    port
        <<test4>> in Integer in1,
        <<test5>> out String out1,
        <<test5>> out String out2;
        
  
  <<test2>>
    component InnerC<T>(int x, int y) {
        <<stst>>
        port 
            <<asdasd>> in T inin,
            out T outout;
    }
  
    <<a, s, d, f>>
    component InnerC<String> 
        ic1 [<<fooo>> outout -> out1, out2; <<bar>> sth -> sthelse],
        ic2 [<<AW="Wurst">> outout -> ic1.inin]; 
  
    <<stst>> connect in1 -> InnerC;

}
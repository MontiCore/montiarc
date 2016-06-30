package a;

component TimeSync {

    timing sync;
    
    port 
        in String str1In,
        in String str2In,
        out String str1Out,
        out String str2Out;
}

package components;


component AtomicWithAJava{

  port in Integer inInt;
  port out String outString;
  port out List<Integer> outIntList;

  int x;

  init{
    x = 5;
  }

   compute ComputeBlock{
      List<Integer> list = new ArrayList<>();
      if( x < inInt){
        outString = "Less than " + x;
      } else {
        for(int i = 1; i < inInt; i++){
          list.append(i);
        }
        outIntList = list;
      }
   }
}
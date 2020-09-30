public class RecursionWithLoops {

   public static int sum(int num) {
      int result = 0;
      for (int i=1; i<num; ++i){
         result = result + i;
     }
        return result + num;
  }
      public static void main(String[] args) {
        System.out.println(sum(10));
     }
  }
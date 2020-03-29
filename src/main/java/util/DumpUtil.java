package util;

public class DumpUtil {

    public static int calcFirstOffset(int rank, int curLevel, int depth){
        int reverseLevel = depth - (curLevel + 1);
        if(rank%2==0){
            rank = rank+1;
        }
        int offset = (int)(Math.pow(rank,reverseLevel)-1)/2;
        return offset;
    }

    public static int calcDistanceOffset(int rank, int curLevel, int depth){
        int reverseLevel = depth - (curLevel + 1);
        if(rank%2==0){
            rank = rank+1;
        }
        return (int)Math.pow(rank,reverseLevel);
    }

    public static void main(String[] args) {
        for(int i=0;i<4;i++){
            int offset = DumpUtil.calcDistanceOffset(3,i-1,4);
            System.out.println("offset:"+offset);
        }
    }
}

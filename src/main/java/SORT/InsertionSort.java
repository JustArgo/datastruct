package SORT;

import java.util.*;

public class InsertionSort {


    public static void main(String[] args) {


        Random random = new Random();
        List<Integer> numList = new ArrayList<Integer>();
        /*for(int i=0;i<12;i++){
            numList.add(random.nextInt(100));
        }*/
        numList.addAll(Arrays.asList(78, 89, 69, 94, 27, 84, 62, 86, 16, 28, 73, 54));
        //numList.addAll(Arrays.asList(89, 78));
        System.out.println("before:"+numList);
        numList = insertionSort(numList);
        System.out.println("after:"+numList);

    }

    public static List<Integer> insertionSort(List<Integer> numList){

        List<Integer> sortedList = new ArrayList<Integer>(numList.size());


        while(numList.size() != 0){

            Iterator<Integer> ite = numList.iterator();

            while(ite.hasNext()){

                    Integer curNum = ite.next();

                    if(sortedList.size()==0 || curNum < sortedList.get(0)){
                        sortedList.add(0,curNum);
                        ite.remove();
                    }else{
                        for(int i=0;i<sortedList.size();i++){
                            if(curNum>sortedList.get(i) && ((i+1)==sortedList.size() || curNum <= sortedList.get(i+1))){
                                sortedList.add(i+1,curNum);
                                ite.remove();
                                break;
                            }
                        }
                    }

            }


        }



        return sortedList;
    }

}

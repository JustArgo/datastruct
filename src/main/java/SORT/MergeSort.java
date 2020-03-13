package SORT;

import java.util.*;

public class MergeSort {

    public static List<Integer> mergeSort(List<Integer> sortList){
        if(sortList.size()<=1){
            return new ArrayList<Integer>(sortList);
        }
        int mid = sortList.size()/2;
        List<Integer> leftSubList = sortList.subList(0,mid);
        List<Integer> rightSubList = sortList.subList(mid,sortList.size());
        leftSubList = mergeSort(leftSubList);
        rightSubList = mergeSort((rightSubList));
        Iterator<Integer> ite1 = leftSubList.iterator();
        Iterator<Integer> ite2 = rightSubList.iterator();
        List<Integer> resultList = new ArrayList<Integer>();

        Map<Integer,Integer> flag1Map = new HashMap<Integer,Integer>();
        Map<Integer,Integer> flag2Map = new HashMap<Integer,Integer>();

        /*while(ite1.hasNext()){
            Integer num1 = ite1.next();
            while(ite2.hasNext()){
                Integer num2 = ite2.next();
                if(num1<num2){
                    resultList.add(new Integer(num1));
                    while(ite1.hasNext()){
                        num1 = ite1.next();
                        if(num1<num2){
                            resultList.add(new Integer(num1));
                        }else{
                            resultList.add(new Integer(num2));
                            break;
                        }
                    }
                    if(!ite2.hasNext()){
                        resultList.add(new Integer(num2));
                    }
                }else{
                    resultList.add(new Integer(num2));
                    if(!ite1.hasNext()){
                        resultList.add(new Integer(num1));
                    }
                }

            }
        }*/

        return resultList;
    }

    public static List<Integer> mergeSort2(List<Integer> sortList){
        if(sortList.size()<=1){
            return new ArrayList<Integer>(sortList);
        }
        int mid = sortList.size()/2;
        List<Integer> leftSubList = sortList.subList(0,mid);
        List<Integer> rightSubList = sortList.subList(mid,sortList.size());
        leftSubList = mergeSort(leftSubList);
        rightSubList = mergeSort((rightSubList));
        List<Integer> resultList = new ArrayList<Integer>();

        Map<Integer,Integer> flag1Map = new HashMap<Integer,Integer>();
        Map<Integer,Integer> flag2Map = new HashMap<Integer,Integer>();

        for(int i=0;i<leftSubList.size();i++){
            if(flag1Map.get(i)==null){
                for(int j=0;j<rightSubList.size();j++){
                    if(flag2Map.get(j)==null){
                        if(leftSubList.get(i)<rightSubList.get(j)){
                            resultList.add(leftSubList.get(i));
                            flag1Map.put(i,1);
                            break;
                        }else{
                            resultList.add(rightSubList.get(j));
                            flag2Map.put(j,1);
                        }
                    }
                }
            }
        }
        if(flag1Map.size()!=leftSubList.size()){
            for(int i=0;i<leftSubList.size();i++){
                if(flag1Map.get(i)==null){
                    resultList.add(leftSubList.get(i));
                }
            }
        }
        for(int i=0;i<rightSubList.size();i++){
            if(flag2Map.get(i)==null){
                resultList.add(rightSubList.get(i));
            }
        }

        return resultList;
    }

    public static void main(String[] args) {
        Random random = new Random();
        List<Integer> numList = new ArrayList<Integer>();
        /*for(int i=0;i<12;i++){
            numList.add(random.nextInt(100));
        }*/
        numList.addAll(Arrays.asList(78, 89, 69, 94, 27, 84, 62, 86, 16, 28, 73, 54));
        //numList.addAll(Arrays.asList(89, 78));
        System.out.println("before:"+numList);
        numList = mergeSort(numList);
        System.out.println("after:"+numList);
    }
}

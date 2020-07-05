package common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Data
public class SkipList {

    private List<SkipListNode> allList = new ArrayList<SkipListNode>();

    private Integer allSize = 0;

    private List<SkipListNode> tmpFindList = new ArrayList<SkipListNode>();

    //private static List<Integer> globalRandomList = Arrays.asList(1,1,0,1,0,0,1,1,1,1,1,0,0,0,1,0,1,0,1,1,1,0,1,1,0,1,1,1,1,1,0,0,1,1,1,1,1,1,0,1,0,1,1,1,1,0,0,1,0,0,1);

    private static List<Integer> globalRandomList = new ArrayList<Integer>();

    private int globalIndex = 0;

    private void addNode(Integer key){
        System.out.println("key："+key);
        if(allList.size()==0){
            allList.add(new SkipListNode(key));
            allSize++;
        }else {
            //最后一个是原始列表
            SkipListNode currentNode = allList.get(0);
            SkipListNode prev = null;
            tmpFindList.clear();
            while(true){
                if(currentNode.getKey().intValue()==key){
                    break;
                }
                if(key<currentNode.getKey()){
                    if(prev==null){//代表加进来的key
                        tmpFindList.add(currentNode);
                        while(currentNode.getDown()!=null){
                            tmpFindList.add(currentNode.getDown());
                            currentNode = currentNode.getDown();
                        }
                        for(int i=tmpFindList.size()-1;i>=0;i--){
                            SkipListNode newNode = new SkipListNode(key);
                            newNode.setNext(tmpFindList.get(i));
                            allList.remove(i);
                            allList.add(i,newNode);
                        }
                        for(int i=0;i<allList.size()-1;i++){
                            allList.get(i).setDown(allList.get(i+1));
                        }
                        allSize++;
                        break;
                    }else{
                        if(prev.getDown()!=null){
                            tmpFindList.add(prev);
                            prev = prev.getDown();
                            currentNode = prev.getNext();
                        }else{
                            SkipListNode newNode = new SkipListNode(key);
                            newNode.setNext(currentNode);
                            prev.setNext(newNode);
                            allSize++;
                            tmpFindList.add(prev);
                            //变动层级指针
                            adjustNodeLink(tmpFindList,newNode);
                            break;
                        }
                    }
                }else if(key>currentNode.getKey()){
                    if(currentNode.getNext()==null){//该层的最后
                        tmpFindList.add(currentNode);
                        if(currentNode.getDown()==null){//说明是原始链表的最后一个
                            SkipListNode newNode = new SkipListNode(key);
                            currentNode.setNext(newNode);
                            adjustNodeLink(tmpFindList,newNode);
                            allSize++;
                            break;
                        }else{
                            currentNode = currentNode.getDown();
                            if(currentNode.getNext()!=null){
                                prev = currentNode;
                                currentNode = currentNode.getNext();
                            }
                        }
                    }else{
                        prev = currentNode;
                        currentNode = currentNode.getNext();
                    }
                }
            }
        }
    }

    private void adjustNodeLink(List<SkipListNode> tmpFindList,SkipListNode newNode) {
        Random random = new Random();
        SkipListNode tmpNode = newNode;
        //System.out.println(tmpFindList);

        //printAllList();

        for(int i = tmpFindList.size()-1;i>=0;i--){
            int rand = random.nextInt(2);
            globalRandomList.add(rand);

/*            int rand = globalRandomList.get(globalIndex);
            globalIndex++;*/

            if(rand==0){
                if(i==0){//说明要再加一层索引
                    SkipListNode upperLink = allList.get(0);
                    List<SkipListNode> skipListNodes = new ArrayList<SkipListNode>();
                    while(upperLink!=null){
                        skipListNodes.add(upperLink);
                        upperLink = upperLink.getNext();
                    }
                    //最顶层，至少5个节点，才有必要拆分链表
                    if(skipListNodes.size()>=5){
                        SkipListNode firstKey = skipListNodes.get(0);
                        SkipListNode middleKey = skipListNodes.get(skipListNodes.size()/2);

                        SkipListNode newFirstKey = new SkipListNode(firstKey.getKey());
                        SkipListNode newMiddleKey = new SkipListNode(middleKey.getKey());

                        newFirstKey.setNext(newMiddleKey);
                        newFirstKey.setDown(firstKey);
                        newMiddleKey.setDown(middleKey);
                        allList.add(0,newFirstKey);
                    }
                }else{
                    SkipListNode levelNode = new SkipListNode(newNode.getKey());
                    levelNode.setDown(tmpNode);
                    SkipListNode recordNode = tmpFindList.get(i-1);
                    SkipListNode recordNextNode = recordNode.getNext();
                    levelNode.setNext(recordNextNode);
                    recordNode.setNext(levelNode);
                    tmpNode = levelNode;
                }
            }else{
                break;
            }
        }

        //System.out.println("-----------------------------------");

        //printAllList();

        System.out.println("");
        System.out.println("");
        System.out.println("");

    }

    public static void main(String[] args) {
        SkipList skipList = new SkipList();
        Random random = new Random();
        List<Integer> originList = new ArrayList<Integer>();

        //这里注释
        for(int i=0;i<50;i++){
            Integer key = random.nextInt(100);
            skipList.addNode(key);
            originList.add(key);
        }
        for(Integer key:originList){
            System.out.print(key+",");
        }
        System.out.println("");
        System.out.println("");

        for(Integer key:globalRandomList){
            System.out.print(key+" ");
        }
        System.out.println("");
        System.out.println("");

        //或者 这里注释
/*        List<Integer> testList = Arrays.asList(69,63,34,7,95,7,78,8,56,75,3,60,74,43,40,18,75,30,4,54,31,17,81,55,56,27,82,2,80,72,71,23,33,88,1,6,25,75,37,9,56,52,33,89,62,99,74,58,10,48);
        for(Integer key:testList){
            skipList.addNode(key);
        }*/

        for(int i=0;i<skipList.getAllList().size();i++){
            SkipListNode node = skipList.getAllList().get(i);
            while(node!=null){
                System.out.print(node.getKey()+" ");
                node = node.getNext();
            }
            System.out.println("");
        }
    }

    private void printAllList(){
        for(int i=0;i<allList.size();i++){
            SkipListNode node = allList.get(i);
            while(node!=null){
                System.out.print(node.getKey()+" ");
                node = node.getNext();
            }
            System.out.println("");
        }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class SkipListNode{
    private Integer key;
    private SkipListNode next;
    private SkipListNode down;

    public SkipListNode(Integer key){
        this.key = key;
    }
}
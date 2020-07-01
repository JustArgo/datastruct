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

    private void addNode(Integer key){
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
                            for(int i=tmpFindList.size()-1;i>=0;i--){
                                tmpFindList.get(i).setNext(new SkipListNode(key));
                            }
                            allSize++;
                            break;
                        }else{
                            currentNode = currentNode.getDown();
                            prev = currentNode;
                            currentNode = currentNode.getNext();
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
            if(random.nextInt(2)==0){
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
        for(int i=0;i<50;i++){
            Integer key = random.nextInt(100);
            skipList.addNode(key);
            originList.add(key);
        }

        /*List<Integer> testList = Arrays.asList(24,98,19,61, 81, 70, 91, 60, 78, 43, 23, 66, 46, 96, 78, 3, 45, 1, 12, 22);
        for(Integer key:testList){
            skipList.addNode(key);
        }*/

        for(Integer key:originList){
            //System.out.print(key+" ");
        }
        System.out.println("");

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
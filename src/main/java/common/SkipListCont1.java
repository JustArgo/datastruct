package common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class SkipListCont1 {

    private List<SkipListNode> allList = new ArrayList<SkipListNode>();

    private List<Integer> levelCountList = new ArrayList<Integer>();

    private Integer allSize = 0;

    private List<SkipListNode> tmpFindList = new ArrayList<SkipListNode>();

    //private static List<Integer> globalRandomList = Arrays.asList(1,1,0,1,0,0,1,1,1,1,1,0,0,0,1,0,1,0,1,1,1,0,1,1,0,1,1,1,1,1,0,0,1,1,1,1,1,1,0,1,0,1,1,1,1,0,0,1,0,0,1);

    private static List<Integer> globalRandomList = new ArrayList<Integer>();

    private static Integer accuAddCount = 0;

    private int globalIndex = 0;

    private void addNode(Integer key){
        //System.out.println("key："+key);
        if(allList.size()==0){
            allList.add(new SkipListNode(key));
            allSize++;
            levelCountList.add(allSize);
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
                        accuAddCount++;
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
                            addLevelCount(i);
                        }
                        for(int i=0;i<allList.size()-1;i++){
                            allList.get(i).setDown(allList.get(i+1));
                        }
                        /*SkipListNode newNode = new SkipListNode(key);
                        newNode.setNext(currentNode);
                        allList.remove(allList.size()-1);
                        allList.add(allList.size(),newNode);*/
                        allSize++;

                        //连续5次都是加在前面，考虑重新调整
                        if(accuAddCount>=5){
                            //reallocateListNode();
                            System.out.println(" cc ");
                            accuAddCount = 0;
                        }
                        break;
                    }else{
                        if(prev.getDown()!=null){
                            tmpFindList.add(prev);
                            prev = prev.getDown();
                            currentNode = prev.getNext();
                        }else{
                            accuAddCount = 0;
                            SkipListNode newNode = new SkipListNode(key);
                            newNode.setNext(currentNode);
                            prev.setNext(newNode);
                            addLevelCount(levelCountList.size()-1);
                            allSize++;
                            tmpFindList.add(prev);
                            //变动层级指针
                            adjustNodeLink(tmpFindList,newNode,false);
                            break;
                        }
                    }
                }else if(key>currentNode.getKey()){
                    if(currentNode.getNext()==null){//该层的最后
                        tmpFindList.add(currentNode);
                        if(currentNode.getDown()==null){//说明是原始链表的最后一个
                            accuAddCount = 0;
                            SkipListNode newNode = new SkipListNode(key);
                            currentNode.setNext(newNode);
                            addLevelCount(levelCountList.size()-1);
                            adjustNodeLink(tmpFindList,newNode,false);
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

    private void reallocateListNode() {

        //最开始只有1层，直接搜索几个进行升级
        if(allList.size()==1){
            SkipListNode firstNode = allList.get(0);
            SkipListNode secondNextNode = firstNode.getNext().getNext();

            SkipListNode levelFirstNode = new SkipListNode(firstNode.getKey());
            SkipListNode levelSecondNextNode = new SkipListNode(secondNextNode.getKey());

            levelFirstNode.setNext(levelSecondNextNode);

            levelFirstNode.setDown(levelFirstNode);
            levelSecondNextNode.setDown(secondNextNode);

            allList.add(0,levelFirstNode);
        }else{
            for(int i=0;i<allList.size()-1;i++){
                randomRemoveLevelIndex(i);
            }
        }
    }

    private void randomRemoveLevelIndex(int level){
        SkipListNode curNode = allList.get(level);
        SkipListNode prev = curNode;
        curNode = curNode.getNext();

        Random random = new Random();
        int opCount = 0;
        List<Integer> upperLeft5Node = new ArrayList<Integer>();
        if(level>0){
            SkipListNode skipListNode = allList.get(level-1);
            while(skipListNode!=null && upperLeft5Node.size()<5){
                upperLeft5Node.add(skipListNode.getKey());
                skipListNode = skipListNode.getNext();
            }
        }
        //一次随机移除，最多仅允许移除两个节点
        while(curNode!=null && opCount<3){
            //1/2的概率对当前层第一个后面的节点，并且不在上面的节点进行移除，并且小于upperLeft5Node 最后1个
            if(random.nextInt(2)==1 && !upperLeft5Node.contains(curNode.getKey()) && (upperLeft5Node.size()==0 || curNode.getKey() < upperLeft5Node.get(upperLeft5Node.size()-1))){
                prev.setNext(curNode.getNext());
                curNode.setNext(null);
                curNode.setDown(null);
                opCount+=1;
            }
            prev = curNode;
            curNode = curNode.getNext();
        }
    }

    private void removeSpecifiedLevelNode(int level,int key){

    }

    private void adjustMostLeftNodeLink(List<SkipListNode> tmpFindList, SkipListNode newNode, boolean isLeft) {

    }

    private void adjustNodeLink(List<SkipListNode> tmpFindList, SkipListNode newNode, boolean isLeft) {
        Random random = new Random();
        SkipListNode tmpNode = newNode;
        //System.out.println(tmpFindList);

        //printAllList();

        for(int i = tmpFindList.size()-1;i>=0;i--){
            int rand = random.nextInt(2);
            globalRandomList.add(rand);

/*            int rand = globalRandomList.get(globalIndex);
            globalIndex++;*/

            if(rand==0 || isLeft){
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
                        levelCountList.add(0,2);
                    }
                }else{
                    SkipListNode levelNode = new SkipListNode(newNode.getKey());
                    levelNode.setDown(tmpNode);
                    SkipListNode recordNode = tmpFindList.get(i-1);
                    if(isLeft){
                        levelNode.setNext(recordNode);
                    }else{
                        SkipListNode recordNextNode = recordNode.getNext();
                        levelNode.setNext(recordNextNode);
                        recordNode.setNext(levelNode);
                    }
                    tmpNode = levelNode;
                    addLevelCount(i-1);
                }
            }else{
                break;
            }
        }
    }

    private int addLevelCount(int level){
        if(levelCountList==null || levelCountList.size()==0 || level >= levelCountList.size()){
            throw new RuntimeException("层数不对");
        }
        levelCountList.set(level,levelCountList.get(level)+1);
        return levelCountList.get(level);
    }

    private void printLevelCount(){
        System.out.print("levelCountList:  ");
        for(Integer levelCount:levelCountList){
            System.out.print(levelCount+" ");
        }
        System.out.println("");
        System.out.println("levelCount end ---------");
        System.out.println("");
    }

    public static void main(String[] args) {
        SkipListCont1 skipList = new SkipListCont1();
        Random random = new Random();
        List<Integer> originList = new ArrayList<Integer>();

        //这里注释
        for(int i=50;i>=0;i--){
            Integer key = random.nextInt(100);
            skipList.addNode(i);
            originList.add(i);
        }
/*        for(Integer key:originList){
            //System.out.print(key+",");
        }
        System.out.println("");
        System.out.println("");*/

/*        for(Integer key:globalRandomList){
            System.out.print(key+" ");
        }
        System.out.println("");
        System.out.println("");*/

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

        skipList.printLevelCount();
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


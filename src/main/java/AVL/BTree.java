package AVL;


import AVL.node.BTreeNode;
import lombok.Data;
import util.DumpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class BTree {

    /** 根节点 */
    private BTreeNode root;

    /** 阶数 */
    private int rank;

    public BTree(){

    }

    public BTree(int rank){
        this.rank = rank;
    }

    public void insert(int key){
        if(this.root==null){
            this.root = new BTreeNode(key);
            return;
        }
        //保证insertNode必然不为null
        BTreeNode insertNode = recursiveSearch(this.root,key);
        //代表该key已存在，不需要做任何操作
        if(insertNode.getInsertIndex()==-1){
            insertNode.setInsertIndex(0);
            return;
        }
        int insertIndex = insertNode.getInsertIndex();
        insertNode.setInsertIndex(0);
        insertNode.getKeyList().add(insertIndex,key);
        //insertNode.getChildList().add(insertIndex,new BTreeNode());
        //关键字的数量>=阶数
        if(insertNode.getKeyList().size()>=rank){
            splitNode(insertNode);
        }
    }

    public void remove(int key){
        if(this.root==null){
            return;
        }
        BTreeNode removeNode = findRemoveNode(this.root,key);
        //要移除的key不存在
        if(removeNode==null){
            return;
        }
        int removeIndex = 0;
        for(int i=0;i<removeNode.getKeyList().size();i++){
            if(removeNode.getKeyList().get(i)==key){
                removeIndex = i;
                break;
            }
        }

        //寻找后继关键字
        BTreeNode successor = findSuccessor(removeNode,removeIndex,key);
        //说明要删除的数据，正好在叶节点
        if(successor==removeNode){
        }else{
            removeNode.getKeyList().set(removeIndex,successor.getKeyList().get(0));
            removeIndex=0;
        }
        successor.getKeyList().remove(removeIndex);
        adjustNode(successor);
    }

    private void adjustNode(BTreeNode node){
        //如果key数目 小于 ceil(m/2)
        int minCount = (int)Math.ceil(rank/1.0/2)-1;
        if(node.getKeyList().size()<minCount){
            if(node.getParent()==null){
                if(node.getKeyList().size()==0){
                    this.root = null;
                }
            }else{
                BTreeNode parent = node.getParent();
                List<BTreeNode> brotherList = parent.getChildList();
                int curIndex = 0;
                for(int index=0;index<brotherList.size();index++){
                    if(brotherList.get(index)==node){
                        curIndex=index;
                        break;
                    }
                }
                //如果左兄弟存在，并且左兄弟的key值数量 > minCount
                if(curIndex>0){
                    BTreeNode prevBrotherNode = brotherList.get(curIndex-1);
                    int prevBKeySize = prevBrotherNode.getKeyList().size();
                    if(prevBKeySize>minCount){
                        node.getKeyList().add(0,parent.getKeyList().get(curIndex-1));
                        parent.getKeyList().set(curIndex-1,prevBrotherNode.getKeyList().get(prevBKeySize-1));
                        prevBrotherNode.getKeyList().remove(prevBKeySize-1);

                        //左兄弟有儿子
                        if(prevBrotherNode.getChildList()!=null && prevBrotherNode.getChildList().size()>0){
                            BTreeNode prevBrotherLastChild = prevBrotherNode.getChildList().remove(prevBKeySize);
                            node.getChildList().add(0,prevBrotherLastChild);
                            prevBrotherLastChild.setParent(node);
                        }

                        return;
                    }
                }
                //如果右兄弟存在，并且右兄弟的key数量 > ceil(rank/2)-1
                if(curIndex<brotherList.size()-1){
                    BTreeNode nextBrotherNode = brotherList.get(curIndex+1);
                    int nextBKeySize = nextBrotherNode.getKeyList().size();
                    if(nextBKeySize>minCount){
                        node.getKeyList().add(node.getKeyList().size(),parent.getKeyList().get(curIndex));
                        parent.getKeyList().set(curIndex,nextBrotherNode.getKeyList().get(0));
                        nextBrotherNode.getKeyList().remove(0);

                        //右兄弟有儿子
                        if(nextBrotherNode.getChildList()!=null && nextBrotherNode.getChildList().size()>0){
                            BTreeNode nextBrotherFirstChild = nextBrotherNode.getChildList().remove(0);
                            node.getChildList().add(node.getChildList().size(),nextBrotherFirstChild);
                            nextBrotherFirstChild.setParent(node);
                        }
                        return;
                    }
                }
                //左右兄弟都没有可以补充的点，往前合并
                if(curIndex>0){
                    int keyIndex = curIndex-1;
                    int mergedParentKey = parent.getKeyList().get(keyIndex);
                    parent.getKeyList().remove(Integer.valueOf(mergedParentKey));
                    brotherList.get(curIndex-1).getKeyList().add(mergedParentKey);
                    brotherList.get(curIndex-1).getKeyList().addAll(node.getKeyList());
                    node.setParent(null);
                    brotherList.remove(curIndex);
                    adjustNode(parent);
                }else{//前面没有兄弟，往后合并
                    int keyIndex = curIndex;
                    int mergedParentKey = parent.getKeyList().get(keyIndex);
                    parent.getKeyList().remove(Integer.valueOf(mergedParentKey));
                    brotherList.get(curIndex+1).getKeyList().add(0,mergedParentKey);
                    brotherList.get(curIndex+1).getKeyList().addAll(0,node.getKeyList());
                    node.setParent(null);
                    brotherList.remove(curIndex);
                    adjustNode(parent);
                }
            }

        }
    }

    private BTreeNode findSuccessor(BTreeNode removeNode, int removeIndex, int key) {
        if(removeNode.getChildList()==null || removeNode.getChildList().size()==0){
            return removeNode;
        }
        return successorInner(removeNode.getChildList().get(removeIndex+1));
    }

    private BTreeNode successorInner(BTreeNode node){
        if(node.getChildList()==null || node.getChildList().size()==0){
            return node;
        }
        return successorInner(node.getChildList().get(0));
    }

    public BTreeNode findRemoveNode(BTreeNode node, int key){
        //如果正好小于第一个key，在最左边查找
        if(key<node.getKeyList().get(0)){
            if(node.getChildList()!=null && node.getChildList().size()>0){
                return findRemoveNode(node.getChildList().get(0),key);
            }else{
                return null;
            }
        }
        //如果正好大于最后一个，则在最右边查找
        int keyLast = node.getKeyList().size()-1;
        if(key>node.getKeyList().get(keyLast)){
            if(node.getChildList()!=null && node.getChildList().size()>0){
                int nodeLast = node.getChildList().size()-1;
                return findRemoveNode(node.getChildList().get(nodeLast),key);
            }else{
                return null;
            }
        }
        //如果正好等于某个key
        for(int i=0;i<node.getKeyList().size();i++){
            if(node.getKeyList().get(i)==key){
                return node;
            }else if(node.getKeyList().get(i)<key && node.getKeyList().get(i+1)>key){
                return findRemoveNode(node.getChildList().get(i+1),key);
            }
        }
        return null;
    }

    /**
     * key数超过限定，进行分裂
     */
    private void splitNode(BTreeNode node){
        if(node.getKeyList().size()>=rank){
            int splitIndex = node.getKeyList().size()/2;
            int splitKey = node.getKeyList().get(splitIndex);
            BTreeNode parent = node.getParent();
            if(parent==null){
                parent = new BTreeNode(node.getKeyList().get(splitIndex));
                node.setParent(parent);
                CopyOnWriteArrayList<BTreeNode> childList = new CopyOnWriteArrayList<BTreeNode>();
                childList.addAll(splitNodeList(node,splitIndex));
                parent.setChildList(childList);
                this.root = parent;
                return;
            }else{
                List<BTreeNode> childList = parent.getChildList();
                int index = 0;
                for(int i=0;i<childList.size();i++){
                    if(childList.get(i)==node){
                        index = i;
                    }
                }
                List<BTreeNode> splitNodeList = splitNodeList(node,splitIndex);
                parent.getKeyList().add(index,splitKey);
                childList.remove(index);
                childList.addAll(splitNodeList);
                splitNode(parent);
            }
        }
    }

    private List<BTreeNode> splitNodeList(BTreeNode node, int splitIndex){
        BTreeNode node2 = new BTreeNode();
        node2.setKeyList(new CopyOnWriteArrayList<Integer>(node.getKeyList().subList(splitIndex+1,node.getKeyList().size())));
        node2.setParent(node.getParent());
        node.setKeyList(new CopyOnWriteArrayList<Integer>(node.getKeyList().subList(0,splitIndex)));
        if(node.getChildList()!=null && node.getChildList().size()>0){
            List<BTreeNode> node2ChildList = node.getChildList().subList(splitIndex+1,node.getChildList().size());
            for(BTreeNode bTreeNode:node2ChildList){
                bTreeNode.setParent(node2);
            }
            node2.setChildList(new CopyOnWriteArrayList<BTreeNode>(node2ChildList));
            node.setChildList(new CopyOnWriteArrayList<BTreeNode>(node.getChildList().subList(0,splitIndex+1)));
        }
        return new ArrayList<BTreeNode>(Arrays.asList(node,node2));
    }

    public BTreeNode recursiveSearch(BTreeNode node, int key){
        if(node==null){
            return null;
        }
        int index = node.getKeyList().size();
        for(int i=0;i<node.getKeyList().size();i++){
            int curKey = node.getKeyList().get(i);
            if(curKey==key){
                node.setInsertIndex(-1);
                return node;
            }else if(curKey>key){
                index = i;
            }
        }
        if(node.getChildList()==null || node.getChildList().size()==0){
            node.setInsertIndex(index);
            return node;
        }else{
            //必须保证，如果一个节点，有n个key，就必须有n+1个childList 或者0个childList
            return recursiveSearch(node.getChildList().get(index),key);
        }
    }

    private void dump(){
        //1、得到深度
        //2、打印每一层的节点
        BTreeNode node = this.root;
        int depth = node!=null?1:0;
        while(node!=null && node.getChildList()!=null && node.getChildList().size()>0){
            node = node.getChildList().get(0);
            depth++;
        }
        //System.out.println(depth);

        int length = this.calLength(depth);

        BTreeNode[] nodeArr = new BTreeNode[length];
        setArr(nodeArr,this.root,1);

        for(int i=0;i<depth;i++){
            int firstOffset = DumpUtil.calcFirstOffset(rank,i,depth);
            for(int tabTime=0;tabTime<firstOffset*(rank+1)-1;tabTime++){
                System.out.print("\t");
            }
            int rightBorder = (int)Math.pow(rank,i);
            for(int j=0;j<rightBorder;j++){
                int arrOff = ((int)Math.pow(3,i)-1)/2;
                BTreeNode bNode = nodeArr[arrOff+j];
                List<String> printList = new ArrayList<String>();
                if(bNode!=null){
                    for(int k=0;k<bNode.getKeyList().size();k++){
                        printList.add(bNode.getKeyList().get(k)+"");
                    }
                }
                for(int k=3-printList.size();k>0;k--){
                    printList.add("n");
                }
                for(int k=0;k<printList.size();k++){
                    System.out.print(String.format("%2s",printList.get(k)));
                    if(k!=printList.size()-1){
                        System.out.print("\t");
                    }
                }
                int distanceOffset = DumpUtil.calcDistanceOffset(rank,i,depth);
                for(int tabTime=0;tabTime<distanceOffset;tabTime++){
                    System.out.print("\t");
                }
            }
            System.out.println("");
        }
    }

    private void setArr(BTreeNode[] nodeArr, BTreeNode node, int index){
        if(index-1<nodeArr.length){
            nodeArr[index-1] = node;
        }
        if(node!=null && node.getChildList()!=null && node.getChildList().size()>0){
            for(int i=0;i<node.getChildList().size();i++){
                int offset = i+2-rank;
                setArr(nodeArr,node.getChildList().get(i),index*rank+offset);
            }
        }
    }

    private int calLength(int height){
        int sum = 0;
        for(int i=0;i<height;i++){
            sum += Math.pow(rank,i);
        }
        return sum;
    }

    public static void main(String[] args) {

        for(int i=1;i<20;i++){
            BTree tree = buildTree();
            tree.remove(i);
            tree.dump();
            System.out.println("---------------------");
        }
        //System.out.println(tree.calLength(4));
    }

    private static BTree buildTree(){
        BTree tree = new BTree(3);
        tree.insert(1);
        tree.insert(2);
        tree.insert(3);
        tree.insert(4);
        tree.insert(5);
        tree.insert(6);

        tree.insert(7);
        tree.insert(8);
        tree.insert(9);
        tree.insert(10);

        tree.insert(11);
        tree.insert(12);
        tree.insert(13);
        tree.insert(14);
        tree.insert(15);
        tree.insert(16);
        tree.insert(17);
        tree.insert(18);
        tree.insert(19);
        return tree;
    }

}

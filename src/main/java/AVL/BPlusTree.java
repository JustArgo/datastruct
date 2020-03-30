package AVL;

import AVL.node.BPlusTreeNode;
import AVL.node.BTreeNode;
import util.DumpUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BPlusTree {

    /** 根节点 */
    private BPlusTreeNode root;

    /** b+树的阶 */
    private int rank;

    public BPlusTree(){
    }

    public BPlusTree(int rank){
        this.rank = rank;
    }

    public void insert(int key){
        if(this.root==null){
            this.root = new BPlusTreeNode(key);
            return;
        }
        //保证insertNode必然不为null
        BPlusTreeNode insertNode = recursiveSearch(this.root,key);
        //代表该key已存在，不需要做任何操作
        if(insertNode==null){
            return;
        }
        int insertIndex = insertNode.getKeyList().size();
        for(int i=0;i<insertNode.getKeyList().size();i++){
            if(insertNode.getKeyList().get(i)>key){
                insertIndex = i;
                break;
            }
        }
        insertNode.getKeyList().add(insertIndex,key);
        //关键字的数量>=阶数
        if(insertNode.getKeyList().size()>=rank){
            splitNode(insertNode);
        }
    }

    private void remove(int key){
        if(this.root==null){
            return;
        }
        BPlusTreeNode removeNode = findRemoveNode(this.root,key);
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

        removeNode.getKeyList().remove(removeIndex);
        adjustNode(removeNode);
    }

    private void adjustNode(BPlusTreeNode node){
        //如果key数目 小于 ceil(m/2)
        int minCount = (int)Math.ceil(rank/1.0/2)-1;
        if(node.getKeyList().size()<minCount){
            if(node.getParent()==null){
                if(node.getKeyList().size()==0){
                    this.root = null;
                }
            }else{
                BPlusTreeNode parent = node.getParent();
                List<BPlusTreeNode> brotherList = parent.getChildList();
                int curIndex = 0;
                for(int index=0;index<brotherList.size();index++){
                    if(brotherList.get(index)==node){
                        curIndex=index;
                        break;
                    }
                }
                //如果左兄弟存在，并且左兄弟的key值数量 > minCount
                if(curIndex>0){
                    BPlusTreeNode prevBrotherNode = brotherList.get(curIndex-1);
                    int prevBKeySize = prevBrotherNode.getKeyList().size();
                    if(prevBKeySize>minCount){
                        int prevKey = prevBrotherNode.getKeyList().remove(prevBKeySize-1);
                        node.getKeyList().add(0,prevKey);
                        parent.getKeyList().set(curIndex-1,prevKey);

                        //左兄弟有儿子
                        if(prevBrotherNode.getChildList()!=null && prevBrotherNode.getChildList().size()>0){
                            BPlusTreeNode prevBrotherLastChild = prevBrotherNode.getChildList().remove(prevBKeySize);
                            node.getChildList().add(0,prevBrotherLastChild);
                            prevBrotherLastChild.setParent(node);
                        }
                        return;
                    }
                }
                //如果右兄弟存在，并且右兄弟的key数量 > ceil(rank/2)-1
                if(curIndex<brotherList.size()-1){
                    BPlusTreeNode nextBrotherNode = brotherList.get(curIndex+1);
                    int nextBKeySize = nextBrotherNode.getKeyList().size();
                    if(nextBKeySize>minCount){
                        int nextKey = nextBrotherNode.getKeyList().remove(0);
                        node.getKeyList().add(node.getKeyList().size(),nextKey);
                        parent.getKeyList().set(curIndex,nextBrotherNode.getKeyList().get(1));

                        //右兄弟有儿子
                        if(nextBrotherNode.getChildList()!=null && nextBrotherNode.getChildList().size()>0){
                            BPlusTreeNode nextBrotherFirstChild = nextBrotherNode.getChildList().remove(0);
                            node.getChildList().add(node.getChildList().size(),nextBrotherFirstChild);
                            nextBrotherFirstChild.setParent(node);
                        }
                        return;
                    }
                }
                //左右兄弟都没有可以补充的点，往前合并
                if(curIndex>0){
                    int keyIndex = curIndex-1;
                    //将前一个节点的第一个key设置给parent
                    parent.getKeyList().set(keyIndex,brotherList.get(curIndex-1).getKeyList().get(0));
                    brotherList.get(curIndex-1).getKeyList().addAll(node.getKeyList());
                    node.setParent(null);

                    //当前不是最后一个节点
                    if(node.getNext()!=null){
                        brotherList.get(curIndex-1).setNext(node.getNext());
                        node.getNext().setPrev(brotherList.get(curIndex-1));
                    }else{
                        brotherList.get(curIndex-1).setNext(null);
                        node.setPrev(null);
                    }

                    //当前节点有儿子 和 兄弟节点的儿子合并
                    if(node.getChildList()!=null && node.getChildList().size()>0){
                        BPlusTreeNode beforeNode = brotherList.get(curIndex-1).getChildList().get(brotherList.get(curIndex-1).getChildList().size()-1);
                        BPlusTreeNode afterNode = node.getChildList().get(0);
                        beforeNode.getKeyList().addAll(afterNode.getKeyList());
                        if(afterNode.getNext()!=null){
                            afterNode.getNext().setPrev(beforeNode);
                            beforeNode.setNext(afterNode.getNext());
                        }else{
                            beforeNode.setNext(null);
                        }
                    }

                    brotherList.remove(curIndex);
                    parent.getKeyList().remove(curIndex-1);

                    adjustNode(parent);
                }else{//前面没有兄弟，往后合并
                    int keyIndex = curIndex;

                    //如果当前节点还有其它key
                    if(node.getKeyList().size()>0){
                        parent.getKeyList().set(keyIndex,node.getKeyList().get(0));
                        brotherList.get(curIndex+1).getKeyList().addAll(0,node.getKeyList());
                    }
                    node.setParent(null);

                    //当前节点不是第一个节点
                    if(node.getPrev()!=null){
                        node.getPrev().setNext(node.getNext());
                        node.getNext().setPrev(node.getPrev());
                    }else{
                        if(node.getNext()!=null){
                            node.getNext().setPrev(null);
                        }
                        node.setNext(null);
                    }

                    //当前节点有儿子 和 兄弟节点的儿子合并
                    if(node.getChildList()!=null && node.getChildList().size()>0){
                        BPlusTreeNode afterNode = brotherList.get(curIndex+1).getChildList().get(0);
                        BPlusTreeNode beforeNode = node.getChildList().get(node.getChildList().size()-1);
                        afterNode.getKeyList().addAll(0,beforeNode.getKeyList());
                        if(beforeNode.getPrev()!=null){
                            beforeNode.getPrev().setNext(afterNode);
                            afterNode.setPrev(beforeNode.getPrev());
                        }else{
                            afterNode.setPrev(null);
                        }
                    }

                    brotherList.remove(curIndex);
                    parent.getKeyList().remove(curIndex);

                    adjustNode(parent);
                }
            }

        }
    }

    public BPlusTreeNode findRemoveNode(BPlusTreeNode node, int key){
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
                //说明node是叶子节点
                if(node.getChildList()==null || node.getChildList().size()==0){
                    return node;
                }else{
                    return findRemoveNode(node.getChildList().get(i+1),key);
                }
            }else if(node.getKeyList().get(i)<key && node.getKeyList().get(i+1)>key){
                return findRemoveNode(node.getChildList().get(i+1),key);
            }
        }
        return null;
    }

    /**
     * key数超过限定，进行分裂
     */
    private void splitNode(BPlusTreeNode node){
        if(node.getKeyList().size()>=rank){
            int splitIndex = node.getKeyList().size()/2;
            int splitKey = node.getKeyList().get(splitIndex);
            BPlusTreeNode parent = node.getParent();
            if(parent==null){
                parent = new BPlusTreeNode(node.getKeyList().get(splitIndex));
                node.setParent(parent);
                CopyOnWriteArrayList<BPlusTreeNode> childList = new CopyOnWriteArrayList<BPlusTreeNode>();
                childList.addAll(splitNodeList(node,splitIndex));
                parent.setChildList(childList);
                this.root = parent;
                return;
            }else{
                List<BPlusTreeNode> childList = parent.getChildList();
                int index = 0;
                for(int i=0;i<childList.size();i++){
                    if(childList.get(i)==node){
                        index = i;
                    }
                }
                parent.setPrev(null);
                parent.setNext(null);
                List<BPlusTreeNode> splitNodeList = splitNodeList(node,splitIndex);
                parent.getKeyList().add(index,splitKey);
                childList.remove(index);
                childList.addAll(splitNodeList);
                splitNode(parent);
            }
        }
    }

    private List<BPlusTreeNode> splitNodeList(BPlusTreeNode node, int splitIndex){
        boolean isLeaf = false;
        if(node.getChildList()==null || node.getChildList().size()==0){
            isLeaf = true;
        }
        BPlusTreeNode node2 = new BPlusTreeNode();
        node2.setKeyList(new CopyOnWriteArrayList<Integer>(node.getKeyList().subList(isLeaf?splitIndex:splitIndex+1,node.getKeyList().size())));
        node2.setParent(node.getParent());
        node.setKeyList(new CopyOnWriteArrayList<Integer>(node.getKeyList().subList(0,splitIndex)));
        if(node.getChildList()!=null && node.getChildList().size()>0){//有child 就肯定不是leaf
            List<BPlusTreeNode> node2ChildList = node.getChildList().subList(splitIndex+1,node.getChildList().size());
            for(BPlusTreeNode bTreeNode:node2ChildList){
                bTreeNode.setParent(node2);
            }
            node2.setChildList(new CopyOnWriteArrayList<BPlusTreeNode>(node2ChildList));
            node.setChildList(new CopyOnWriteArrayList<BPlusTreeNode>(node.getChildList().subList(0,splitIndex+1)));
        }else{
            node.setNext(node2);
            node2.setPrev(node);
        }

        return new ArrayList<BPlusTreeNode>(Arrays.asList(node,node2));
    }

    public BPlusTreeNode recursiveSearch(BPlusTreeNode node, int key){
        if(node==null){
            return null;
        }
        int index = node.getKeyList().size();
        for(int i=0;i<node.getKeyList().size();i++){
            int curKey = node.getKeyList().get(i);
            if(curKey==key){
                return null;
            }else if(curKey>key){
                index = i;
            }
        }
        if(node.getChildList()==null || node.getChildList().size()==0){
            return node;
        }else{
            //必须保证，如果一个节点，有n个key，就必须有n+1个childList 或者0个childList
            return recursiveSearch(node.getChildList().get(index),key);
        }
    }

    private void dump(){
        //1、得到深度
        //2、打印每一层的节点
        BPlusTreeNode node = this.root;
        int depth = node!=null?1:0;
        while(node!=null && node.getChildList()!=null && node.getChildList().size()>0){
            node = node.getChildList().get(0);
            depth++;
        }
        int length = this.calLength(depth);

        BPlusTreeNode[] nodeArr = new BPlusTreeNode[length];
        setArr(nodeArr,this.root,1);

        int fOffset[] = new int[]{40,13,4,1,0};

        int offset[] = new int[]{1,4,7,14,19,25,32};

        for(int i=0;i<depth;i++){
            int firstOffset = fOffset[i];
            //DumpUtil.calcFirstOffset(rank,i,depth);
            /*for(int tabTime=0;tabTime<firstOffset*(rank+1)-1;tabTime++){
                System.out.print("\t");
            }*/
            int rightBorder = (int)Math.pow(rank,i);
            for(int j=0;j<rightBorder;j++){
                int arrOff = ((int)Math.pow(3,i)-1)/2;
                BPlusTreeNode bNode = nodeArr[arrOff+j];
                List<String> printList = new ArrayList<String>();
                if(bNode!=null){
                    for(int k=0;k<bNode.getKeyList().size();k++){
                        printList.add(bNode.getKeyList().get(k)+"");
                    }
                }

                if(bNode!=null && bNode.getPrev()!=null){
                    //System.out.print("("+bNode.getPrev().getKeyList().get(bNode.getPrev().getKeyList().size()-1)+")");
                }

                for(int k=0;k<printList.size();k++){
                    System.out.print(String.format("%2s",printList.get(k)));
                    System.out.print(" ");
                }
                if(bNode!=null && bNode.getNext()!=null){
                    //System.out.print("("+bNode.getNext().getKeyList().get(0)+")");
                }
                int distanceOffset = offset[depth-i];
                //DumpUtil.calcDistanceOffset(rank,i,depth);
                for(int tabTime=0;tabTime<distanceOffset;tabTime++){
                    System.out.print("\t");
                }
            }
            System.out.println("");
        }
    }

    private void setArr(BPlusTreeNode[] nodeArr, BPlusTreeNode node, int index){
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
        BPlusTree tree = buildTree();
        tree.remove(1);
        tree.dump();
    }

    private static BPlusTree buildTree(){
        BPlusTree tree = new BPlusTree(3);
        List<Integer> list = Arrays.asList(1,2,3,5,6,8,9,4,7/*,11,13,15,17,19,20,21,22,99,87,63*/);
        //List<Integer> list = Arrays.asList(2,3,6,1,0,7,8/*,5,4,7,6,8,9*/);
        for(int i=0;i<list.size();i++){
            tree.insert(list.get(i));
        }
        return tree;
    }

}

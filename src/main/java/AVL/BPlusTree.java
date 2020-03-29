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
            }
        }
        insertNode.getKeyList().add(insertIndex,key);
        //关键字的数量>=阶数
        if(insertNode.getKeyList().size()>=rank){
            splitNode(insertNode);
        }
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

        int offset[] = new int[]{16,8,4,2};

        for(int i=0;i<depth;i++){
            int firstOffset = DumpUtil.calcFirstOffset(rank,i,depth);
            for(int tabTime=0;tabTime<firstOffset*(rank+1)-1;tabTime++){
                System.out.print("\t");
            }
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
                for(int k=0;k<printList.size();k++){
                    System.out.print(String.format("%2s",printList.get(k)));
                    int distanceOffset = offset[i];//DumpUtil.calcDistanceOffset(rank,i,depth);
                    for(int tabTime=0;tabTime<distanceOffset;tabTime++){
                        System.out.print("\t");
                    }
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
        tree.dump();
    }

    private static BPlusTree buildTree(){
        BPlusTree tree = new BPlusTree(3);
        for(int i=1;i<10;i++){
            tree.insert(i);
        }
        return tree;
    }

}

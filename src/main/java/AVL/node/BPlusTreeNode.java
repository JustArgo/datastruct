package AVL.node;

import AVL.BPlusTree;
import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class BPlusTreeNode {

    /** 关键字列表 */
    private CopyOnWriteArrayList<Integer> keyList;

    /** 子节点 */
    private CopyOnWriteArrayList<BPlusTreeNode> childList;

    /** 对应的父节点 */
    private BPlusTreeNode parent;

    /** 叶子节点，有指针指向下一个节点 */
    private BPlusTreeNode next;

    /** 叶子节点，有指针指向上一个节点 */
    private BPlusTreeNode prev;

    public BPlusTreeNode(){
    }

    public BPlusTreeNode(int key){
        this.keyList = new CopyOnWriteArrayList<Integer>();
        this.keyList.add(key);
    }
}

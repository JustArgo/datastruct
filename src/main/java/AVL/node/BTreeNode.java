package AVL.node;

import lombok.Data;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * b树节点
 */
@Data
public class BTreeNode {

    /** 几个关键字 */
    /*private int keyCount;*/

    /** 关键字列表 */
    private CopyOnWriteArrayList<Integer> keyList;

    /** 子节点 */
    private CopyOnWriteArrayList<BTreeNode> childList;

    /** 要插入的索引位置 新增的时候辅助使用，序列化不用加这个值 */
    private transient int insertIndex;

    /** 父节点 */
    private BTreeNode parent;

    public BTreeNode(){

    }

    public BTreeNode(int key){
        //this.keyCount = 1;
        this.keyList = new CopyOnWriteArrayList<Integer>();
        this.keyList.add(key);
    }
}

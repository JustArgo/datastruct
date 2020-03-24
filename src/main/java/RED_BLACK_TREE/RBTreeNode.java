package RED_BLACK_TREE;

import lombok.Data;

@Data
public class RBTreeNode {

    public static final byte BLACK = 1;
    public static final byte RED = 0;

    private byte color;

    private Integer key;

    private Integer value;

    private RBTreeNode parent;

    private RBTreeNode left;

    private RBTreeNode right;

    public RBTreeNode(){

    }

    public RBTreeNode(int key, int value){
        //默认红色节点
        this.color = RED;
        this.key = key;
        this.value = value;
    }

    public RBTreeNode(byte color, int key, int value){
        this.color = color;
        this.key = key;
        this.value = value;
    }

}

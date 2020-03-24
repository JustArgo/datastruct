package RED_BLACK_TREE;

import com.sun.org.apache.regexp.internal.RE;

import static RED_BLACK_TREE.RBTreeNode.*;

public class RBTree{

    private RBTreeNode root;

    public RBTree(){
        /*this.root = new RBTreeNode(0,0);

        RBTreeNode son = new RBTreeNode(1,1);
        RBTreeNode grandSon = new RBTreeNode(3,3);

        son.setLeft(grandSon);
        son.setRight(new RBTreeNode(4,4));

        root.setLeft(son);
        root.setRight(new RBTreeNode(2,2));*/

        //root.setRight(new RBTreeNode());
    }

    public void insert(int key, int value){
        if(root==null){
            root = new RBTreeNode(BLACK, key, value);
            return;
        }
        RBTreeNode node = findNode(root,root,key);

        if(node.getKey()==key){
            node.setValue(value);
            return;
        }
        RBTreeNode childNode = new RBTreeNode(key,value);
        childNode.setParent(node);
        if(key<node.getKey()){
            node.setLeft(childNode);
        }else{
            node.setRight(childNode);
        }
        adjustNode(childNode);
        //最后判断一下，如果根节点是红色，则改成黑色
        if(this.root.getColor()==RED){
            this.root.setColor(BLACK);
        }
    }

    private RBTreeNode findNode(RBTreeNode parent, RBTreeNode node, int key){
        if(node==null){
            return parent;
        }
        if(node.getKey()>key){
            return findNode(node, node.getLeft(),key);
        }
        if(node.getKey()<key){
            return findNode(node, node.getRight(),key);
        }
        return node;
    }

    /**
     * 找到需要移除的节点
     */
    private RBTreeNode findRemoveNode(RBTreeNode node, int key){
        if(node==null || node.getKey()==null){
            return null;
        }
        if(node.getKey()==key){
            return node;
        }
        if(node.getKey()>key){
            return findRemoveNode(node.getLeft(),key);
        }else{
            return findRemoveNode(node.getRight(),key);
        }
    }

    private void adjustNode(RBTreeNode node){
        if(node==null || node.getParent()==null){
            return;
        }
        RBTreeNode father = node.getParent();
        //如果父节点是黑色，不处理
        if(father.getColor()==BLACK){
            return;
        }
        //如果父节点是红色
        if(father.getColor()==RED){
            /** 分成5种情况 */
            //1、叔叔也是红色
            //2、叔叔是黑色或null，父是左，我是左
            //3、叔叔是黑色或null，父是左，我是右
            //4、叔叔是黑色或null，父是右，我是左
            //5、叔叔是黑色或null，父是右，我是右
            RBTreeNode grandFather = father.getParent();
            RBTreeNode uncle = null;
            if(grandFather.getLeft()!=null && grandFather.getLeft().getKey()==father.getKey()){
                uncle = grandFather.getRight();
            }else{
                uncle = grandFather.getLeft();
            }
            //叔叔是红色
            if(uncle!=null && uncle.getColor()==RED){
                father.setColor(BLACK);
                uncle.setColor(BLACK);
                grandFather.setColor(RED);
                adjustNode(grandFather);
                return;
            }
            //父节点是左儿子
            if(father.getKey()<grandFather.getKey()){
                //当前节点是左儿子
                if(node.getKey()<father.getKey()){
                    father.setColor(BLACK);
                    grandFather.setColor(RED);
                    RRotate(grandFather);
                }else{
                    LRotate(father);
                    adjustNode(father);
                }
            }else{
                //当前节点是右儿子
                if(node.getKey()>father.getKey()){
                    father.setColor(BLACK);
                    grandFather.setColor(RED);
                    LRotate(grandFather);
                }else{
                    RRotate(father);
                    adjustNode(father);
                }
            }
        }
    }

    /**
     * 对某个节点进行左旋
     */
    private void LRotate(RBTreeNode node){
        if(node==null || node.getRight()==null){
            return;
        }
        RBTreeNode father = node.getParent();
        RBTreeNode rightSon = node.getRight();
        RBTreeNode leftGrandSon = node.getRight().getLeft();

        //判断当前节点是左儿子，还是右儿子
        if(father!=null){
            boolean isLeft = father.getLeft()!=null && father.getLeft().getKey() == node.getKey();
            if(isLeft){
                father.setLeft(rightSon);
            }else{
                father.setRight(rightSon);
            }
        }

        //孙子节点和自己改变关系
        if(leftGrandSon!=null){
            leftGrandSon.setParent(node);
            node.setRight(leftGrandSon);
        }else{
            node.setRight(null);
        }

        //右子节点和自己改变关系
        rightSon.setParent(father);
        rightSon.setLeft(node);
        node.setParent(rightSon);

    }
    
    /**
     * 对某个节点进行右旋
     */
    private void RRotate(RBTreeNode node){
        if(node==null || node.getLeft()==null){
            return;
        }
        RBTreeNode father = node.getParent();
        RBTreeNode leftSon = node.getLeft();
        RBTreeNode rightGrandSon = node.getLeft().getRight();
        
        //判断当前节点是左儿子，还是右儿子
        if(father!=null){
            boolean isLeft = father.getLeft()!=null && father.getLeft().getKey() == node.getKey();
            if(isLeft){
                father.setLeft(leftSon);
            }else{
                father.setRight(leftSon);
            }
        }

        //孙子节点和自己改变关系
        if(rightGrandSon!=null){
            rightGrandSon.setParent(node);
            node.setLeft(rightGrandSon);
        }else{
            node.setLeft(null);
        }

        //左子节点和自己改变关系
        leftSon.setParent(father);
        leftSon.setRight(node);
        node.setParent(leftSon);

    }

    private void dump(int dumpType){
        int height = this.calcHeight();
        int length = (int)Math.pow(2,height)-1;
        //System.out.println("length:"+length);
        RBTreeNode[] nodeArr = new RBTreeNode[length+1];
        setArr(nodeArr,this.root,1);

        /*for(int i=0;i<nodeArr.length;i++){
            if(nodeArr[i]!=null){
                System.out.println(nodeArr[i].getKey()+"-"+nodeArr[i].getValue()+"-"+transColor(nodeArr[i].getColor()));
            }
        }*/
        for(int i=0;i<height;i++){
            for(int tabTime=0;tabTime<(Math.pow(2,height-i-1)-1);tabTime++){
                System.out.print("\t");
            }
            int rightBorder = (int)Math.pow(2,i);
            for(int j=0;j<rightBorder;j++){
                RBTreeNode node = nodeArr[rightBorder-1+j];
                if(node!=null){
                    //System.out.print(String.format("%s-%s-%s",node.getKey(),node.getValue(),transColor(node.getColor())));
                    switch (dumpType){
                        case 0:
                            System.out.print(String.format("%d",node.getKey()));
                            break;
                        case 1:
                            System.out.print(String.format("%d",node.getValue()));
                            break;
                        case 2:
                            System.out.print(String.format("%s",transColor(node.getColor())));
                            break;
                        default:
                            break;
                    }
                }else{
                    switch (dumpType){
                        case 0:
                            System.out.print("n");
                            break;
                        case 1:
                            System.out.print("n");
                            break;
                        case 2:
                            System.out.print("nil");
                            break;
                        default:
                            break;
                    }
                }
                if(i!=0){
                    for(int tabTime=0;tabTime<Math.pow(2,height-i);tabTime++){
                        System.out.print("\t");
                    }
                }
            }
            System.out.println("");
        }

        //recursive(this.root);
    }

    private void setArr(RBTreeNode[] nodeArr, RBTreeNode node, int index){
        if(index-1<nodeArr.length){
            nodeArr[index-1] = node;
        }
        if(node!=null){
            setArr(nodeArr,node.getLeft(),index*2);
            setArr(nodeArr,node.getRight(),index*2+1);
        }
    }

    private void recursive(RBTreeNode node){
        if(node==null){
            System.out.println("null-null");
            return;
        }
        if(node.getLeft()!=null){
            System.out.print(node.getLeft().getKey()+"-"+node.getLeft().getValue()+"-"+transColor(node.getLeft().getColor()));
        }else{
            System.out.print("null-null");
        }
        System.out.print("\t");
        System.out.print(node.getKey()+"-"+node.getValue());
        System.out.print("\t");
        if(node.getRight()!=null){
            System.out.print(node.getRight().getKey()+"-"+node.getRight().getValue()+"-"+transColor(node.getRight().getValue()));
        }else{
            System.out.print("null-null");
        }
        System.out.println("");
        recursive(node.getLeft());
        recursive(node.getRight());
    }

    private int calcHeight(){
        RBTreeNode node = this.root;
        if(node==null){
            return 0;
        }
        int leftHeight = height(node.getLeft(),0);
        int rightHeiht = height(node.getRight(),0);
        return leftHeight>=rightHeiht?leftHeight+1:rightHeiht+1;
    }

    private int height(RBTreeNode node,int curHeight){
        if(node==null){
            return curHeight;
        }
        int left = height(node.getLeft(),curHeight+1);
        int right = height(node.getRight(),curHeight+1);
        if(left>right){
            return left;
        }
        return right;
    }

    private void remove(int key){
        RBTreeNode node = findRemoveNode(this.root, key);
        if(node==null){
            return;
        }
        //removeNode(node);
        int color = node.getColor();
        //查找出后继节点
        RBTreeNode successor = successor(node);
        //没有后继节点，直接删除当前节点
        if(successor==null){
            //如果是根节点
            if(node==this.root){
                if(node.getLeft()!=null){
                    this.root = node.getLeft();
                    this.root.setParent(null);
                }else{
                    this.root = null;
                }
            }else{
                RBTreeNode father = node.getParent();
                RBTreeNode brother = father.getLeft();
                RBTreeNode leftSon = node.getLeft();
                boolean isLeft = false;
                if(father.getLeft()!=null && father.getLeft().getKey()==node.getKey()){
                    isLeft = true;
                    brother = father.getRight();
                }
                detachRelWithFather(node,leftSon);
                if(node.getColor()==BLACK){
                    balanceTree(node,father,brother,isLeft);
                }
            }
        }else{
            node.setKey(successor.getKey());
            node.setValue(successor.getValue());
            RBTreeNode father = successor.getParent();
            RBTreeNode brother = null;
            RBTreeNode rightSon = successor.getRight();
            boolean isLeft = false;
            if(father.getLeft()!=null && father.getLeft().getKey()==successor.getKey()){
                isLeft = true;
                brother = father.getRight();
            }else{
                brother = father.getLeft();
            }
            detachRelWithFather(successor,rightSon);
            //如果是红色
            if(successor.getColor()==BLACK){
                balanceTree(successor,father,brother,isLeft);
            }
        }
    }

    private void balanceTree(RBTreeNode node, RBTreeNode father, RBTreeNode brother, boolean isLeft){
        RBTreeNode nephewLeft = null;
        RBTreeNode nephewRight = null;
        RBTreeNode nephewOne = null;
        RBTreeNode nephewTwo = null;
        if(brother!=null){
            nephewLeft = brother.getLeft();
            nephewRight = brother.getRight();
        }
        if(isLeft){
            nephewOne = nephewLeft;
            nephewTwo = nephewRight;
        }else{
            nephewOne = nephewRight;
            nephewTwo = nephewLeft;
        }
        //两种情况是镜像关系
        //兄弟节点是红色, 父节点必然是黑色
        if(brother!=null && brother.getColor()==RED){
            brother.setColor(BLACK);
            father.setColor(RED);
            switchRotate(father,isLeft);
            balanceTree(father,brother,nephewTwo,true);
        }else{//兄弟节点是黑色
            //1、 兄弟节点右子为红，左子任意 isLeft==false的时候相反
            if(nephewTwo!=null && nephewTwo.getColor()==RED){
                byte tmpColor = brother.getColor();
                brother.setColor(father.getColor());
                father.setColor(tmpColor);
                nephewTwo.setColor(BLACK);
                switchRotate(father,isLeft);
            }else if((nephewTwo==null || (nephewTwo!=null && nephewTwo.getColor()==BLACK))
                        && nephewOne!=null && nephewOne.getColor()==RED){//2、兄弟节点的右子为黑，左子为红
                brother.setColor(RED);
                nephewOne.setColor(BLACK);
                switchRotate(brother,!isLeft);
                balanceTree(node,father,nephewOne,isLeft);
            }else{//这种情况就是, 两个都是黑色, null也代表黑色
                brother.setColor(RED);
                RBTreeNode grandFather = father.getLeft();
                RBTreeNode uncle = null;
                boolean fatherLeft = false;
                if(grandFather!=null && grandFather.getLeft()!=null && grandFather.getLeft().getKey()==father.getKey()){
                    fatherLeft = true;
                    uncle = grandFather.getRight();
                }else if(grandFather!=null){
                    uncle = grandFather.getRight();
                }
                balanceTree(father,grandFather,uncle,fatherLeft);
            }
        }
    }

    /**
     * 根据传进来的标志位, 进行不同的旋转
     */
    private void switchRotate(RBTreeNode node, boolean flag){
        if(flag){
            LRotate(node);
        }else{
            RRotate(node);
        }
    }

    /**
     * 跟父节点解除关系, 并用另一个节点代替
     */
    private void detachRelWithFather(RBTreeNode node, RBTreeNode successor){
        if(node==null || node.getParent()==null){
            return;
        }
        if(node.getParent().getLeft()!=null && node.getParent().getLeft().getKey()==node.getKey()){
            node.getParent().setLeft(successor);
            if(successor!=null){
                successor.setParent(node.getParent());
            }
            node.setParent(null);
            return;
        }
        if(node.getParent().getRight()!=null && node.getParent().getRight().getKey()==node.getKey()){
            node.getParent().setRight(successor);
            if(successor!=null){
                successor.setParent(node.getParent());
            }
            node.setParent(null);
        }
    }

    /**
     * 查找某个节点的后继节点
     */
    private RBTreeNode successor(RBTreeNode node){
        if(node==null || node.getRight()==null){
            return null;
        }
        RBTreeNode successor = node.getRight();
        while(successor.getLeft()!=null){
            successor = successor.getLeft();
        }
        return successor;
    }

    //该方法为最开始推理的方法，内容不正确
    @Deprecated
    private void removeNode(RBTreeNode node){
        if(node==null){
            return;
        }
        //先处删除的节点是根节点的情况
        if(node==this.root){
            this.root = null;
            return;
        }
        RBTreeNode father =  node.getParent();
        RBTreeNode fatherLeft = null;
        RBTreeNode fatherRight = null;
        RBTreeNode brother = null;
        RBTreeNode leftSon = node.getLeft();
        RBTreeNode rightSon = node.getRight();
        //该节点是父节点的左儿子吗
        boolean isLeft = false;
        if(father!=null){
            fatherLeft = father.getLeft();
            fatherRight = father.getRight();
            if(fatherLeft!=null && fatherLeft==node){
                isLeft = true;
                brother = fatherRight;
            }else{
                brother = fatherLeft;
            }
        }
        //如果移除的是红色节点
        if(node.getColor()==RED){
            //该节点既没有左儿子，也没有右儿子
            if(node.getLeft()==null && node.getRight()==null){
                //移除的节点是父节点的左儿子
                if(fatherLeft!=null && fatherLeft==node){
                    father.setLeft(null);
                }else{
                    father.setRight(null);
                }
                return;
            }
            //移除的节点有左儿子，无右儿子，把左儿子的值设置给自己，直接代替移除的节点
            if(leftSon!=null && rightSon==null){
                leftSon.setParent(father);
                if(isLeft){
                    father.setLeft(leftSon);
                }else{
                    father.setRight(rightSon);
                }
                return;
            }
            //移除的节点有右儿子，无左儿子，把右儿子的值设置给自己，直接代替移除的节点
            if(leftSon==null && rightSon!=null){
                rightSon.setParent(father);
                if(isLeft){
                    father.setLeft(rightSon);
                }else{
                    father.setRight(rightSon);
                }
                return;
            }
            //有两个儿子，则只能都是黑儿子，把左儿子的值赋给自己，当成是删除左儿子
            node.setKey(leftSon.getKey());
            node.setValue(leftSon.getValue());
            //TODO 需要旋转吗
            removeNode(leftSon);
        }else{//删除的节点是黑色的
            //两个儿子都是null
            if(leftSon==null && rightSon==null){
                //兄弟节点肯定存在
                //父节点是红色，兄弟节点是黑色(只能是黑色)
                if(father.getColor()==RED && /*brother!=null &&*/ brother.getColor()==BLACK){
                    father.setColor(BLACK);
                    brother.setColor(RED);
                    if(brother.getLeft()!=null){
                        brother.getLeft().setColor(BLACK);
                    }
                    if(brother.getRight()!=null){
                        brother.getRight().setColor(BLACK);
                    }
                }
                //父黑，兄弟黑
                if(father.getColor()==BLACK && /*brother!=null &&*/ brother.getColor()==BLACK){
                    //TODO
                }
                //父黑 兄弟红
                if(father.getColor()==BLACK && brother.getColor()==RED){
                    node.setKey(father.getKey());
                    node.setValue(father.getValue());
                    father.setKey(brother.getKey());
                    father.setValue(brother.getValue());
                    removeNode(brother);
                    return;
                }
            }
            //一个儿子是红，一个儿子是null
            if((leftSon!=null && leftSon.getColor()==RED && rightSon==null)
                || rightSon!=null && rightSon.getColor()==RED && leftSon==null){
                if(leftSon!=null){
                    node.setKey(leftSon.getKey());
                    node.setValue(leftSon.getValue());
                    removeNode(leftSon);
                }else{
                    node.setKey(rightSon.getKey());
                    node.setValue(rightSon.getValue());
                    removeNode(rightSon);
                }
            }
            //一个儿子是黑，一个儿子是null，不存在这种情况，因为之前已经是平衡的
            /*if((leftSon!=null && leftSon.getColor()==BLACK && rightSon==null)
                    || rightSon!=null && rightSon.getColor()==BLACK && leftSon==null){

            }*/
            //一个儿子是红，一个儿子是黑，把红的值设置给当前节点，当成是删除红节点
            if(leftSon!=null && rightSon!=null && leftSon.getColor()!=rightSon.getColor()){
                if(leftSon.getColor()==RED){
                    node.setKey(leftSon.getKey());
                    node.setValue(leftSon.getValue());
                    removeNode(leftSon);
                }else{
                    node.setKey(rightSon.getKey());
                    node.setValue(rightSon.getValue());
                    removeNode(rightSon);
                }
            }
            //两个儿子都是红
            if(leftSon!=null && rightSon!=null && leftSon.getColor()==RED && rightSon.getColor()==RED){
                if(isLeft){
                    node.setKey(leftSon.getKey());
                    node.setValue(leftSon.getValue());
                    removeNode(leftSon);
                }else{
                    node.setKey(rightSon.getKey());
                    node.setValue(rightSon.getValue());
                    removeNode(rightSon);
                }
            }
            //两个儿子都是黑
            if(leftSon!=null && rightSon!=null && leftSon.getColor()==BLACK && rightSon.getColor()==BLACK){

            }
        }
    }

    private String transColor(int color){
        if(color==RED){
            return "red";
        }else{
            return "bla";
        }
    }

    public static void main(String[] args) {


        /*tree.remove(4);
        tree.remove(2);
        tree.remove(3);*/

        /*for(int i=9;i<=15;i++){
            RBTree tree = insertAll();
            tree.remove(i);
            //System.out.println(tree.calcHeight());
            tree.dump(0);
            System.out.println("");
            tree.dump(1);
            System.out.println("");
            tree.dump(2);
            System.out.println(i+"  ---------------------------------------");
        }*/

        RBTree tree = insertAll();
        for(int i=15;i>=14;i--){
            tree.remove(i);
            //System.out.println(tree.calcHeight());
            tree.dump(0);
            System.out.println("");
            tree.dump(1);
            System.out.println("");
            tree.dump(2);
            System.out.println(i+"  ---------------------------------------");
        }


    }

    private static RBTree insertAll(){
        RBTree tree = new RBTree();
        tree.insert(8,8);
        tree.insert(3,3);
        tree.insert(13,13);
        tree.insert(4,4);
        tree.insert(12,12);
        tree.insert(5,5);
        tree.insert(11,11);

        tree.insert(6,6);
        tree.insert(10,10);
        tree.insert(7,7);
        tree.insert(9,9);

        tree.insert(2,2);
        tree.insert(14,14);

        tree.insert(1,1);
        tree.insert(15,15);
        return tree;
    }
}
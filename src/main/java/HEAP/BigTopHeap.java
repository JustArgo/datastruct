package HEAP;

import lombok.Data;

@Data
public class BigTopHeap {

    private Integer[] heap;
    /** 代表这个堆需要多少个节点 */
    private int k;
    /** 代表这个堆，当前有多少个节点 */
    private int nodeCount = 0;

    public static void main(String[] args) {
        BigTopHeap bigTopHeap = new BigTopHeap();
        bigTopHeap.setK(15);
        bigTopHeap.addNode(558);
        bigTopHeap.addNode(555);
        /*bigTopHeap.addNode(2347);
        bigTopHeap.addNode(1349);
        bigTopHeap.addNode(6234);
        bigTopHeap.addNode(132);
        bigTopHeap.addNode(13455);
        bigTopHeap.addNode(7112);
        bigTopHeap.addNode(656);
        bigTopHeap.addNode(51);
        bigTopHeap.addNode(154);
        bigTopHeap.addNode(367);
        bigTopHeap.addNode(3645);
        bigTopHeap.addNode(1343);
        bigTopHeap.addNode(31234);
        bigTopHeap.addNode(1912);
        for(int i=100;i<176;i+=5){
            bigTopHeap.addNode(i);
        }*/
        bigTopHeap.printHeap();
    }

    public void addNode(int value){
        if(this.heap==null){
            this.heap = new Integer[k];
            this.heap[nodeCount] = value;
            nodeCount++;
            return;
        }
        if(nodeCount>=k){
            if(value>=this.heap[0]){
                return;
            }else{
                this.heap[0] = value;
                siftDown(0);
            }
        }else{
            this.heap[nodeCount] = value;
            siftUp(nodeCount);
            nodeCount++;
        }
    }

    public void siftUp(Integer currentPos){
        int parentPos = (currentPos-1)/2;
        while(parentPos>=0 && this.heap[parentPos]<this.heap[currentPos]){
            Integer tmp = this.heap[parentPos];
            this.heap[parentPos] = this.heap[currentPos];
            this.heap[currentPos] = tmp;
            currentPos = parentPos;
            parentPos = (currentPos-1)/2;
        }
    }

    public void siftDown(Integer currentPos){
        Integer siftDownPos = findSiftDownReplacePos(currentPos);
        while(siftDownPos!=null){
            Integer tmp = this.heap[siftDownPos];
            this.heap[siftDownPos] = this.heap[currentPos];
            this.heap[currentPos] = tmp;
            currentPos = siftDownPos;
            siftDownPos = findSiftDownReplacePos(currentPos);
        }
    }

    /**
     * 找出可以下沉的位置
     */
    private Integer findSiftDownReplacePos(Integer currentPos){
        Integer siftDownPos = null;
        int leftSonPos = currentPos*2+1;
        int rightSonPos = currentPos*2+2;
        if(leftSonPos<nodeCount){
            Integer leftValue = this.heap[leftSonPos];
            Integer rightValue = rightSonPos<nodeCount?this.heap[rightSonPos]:null;
            Integer curValue = this.heap[currentPos];
            //1、左儿子有值，但是右儿子没值，并且左儿子大于 当前值
            //2、左儿子有值，右儿子也有值，找出最大的，并且大于当前值
            //3、其它情况，都不处理
            if(leftValue!=null && leftValue>curValue && rightValue==null){
                siftDownPos = leftSonPos;
            }else if(leftValue!=null && rightValue!=null){
                if(leftValue>rightValue && leftValue>curValue){
                    siftDownPos = leftSonPos;
                }else if(rightValue>curValue){
                    siftDownPos = rightSonPos;
                }
            }
        }
        return siftDownPos;
    }

    public void printHeap(){
        /*for(int i=0;i<this.heap.length;i++){
            System.out.println(this.heap[i]);
        }*/
        double level = Math.sqrt(this.nodeCount+1);
        long posLevel = Math.round(Math.ceil(level));
        System.out.println("posLevel:"+posLevel);
        for(int i=0;i<posLevel;i++){
            if(i!=posLevel-1){
                for(int tabTime=0;tabTime<(Math.pow(2,posLevel-i-1)-1);tabTime++){
                    System.out.print("\t");
                }
            }
            int pow = (int)Math.pow(2,i);
            for(int j=0;j<pow;j++){
                if(this.nodeCount>(pow-1+j)){
                    if(this.heap[pow-1+j]!=null){
                        System.out.print(String.format("%d",this.heap[pow-1+j]));
                    }else{
                        System.out.print("null");
                    }
                    if(i!=0){
                        for(int tabTime=0;tabTime<Math.pow(2,posLevel-i);tabTime++){
                            System.out.print("\t");
                        }
                    }
                }
            }
            System.out.println("");
        }
    }

}

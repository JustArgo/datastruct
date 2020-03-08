package FST;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class FSTService {

    private Node root;

    private void init(){
        root = new Node();
        root.setArcList(new ArrayList<Arc>());
        root.setRoot(true);
    }

    public void add(String word, int value){
        /*List<Arc> arcList = root.getArcList();
        Arc beginArc = null;
        char[] charArr = word.toCharArray();
        for(Arc arc:arcList){
            if(containLabel(arc,charArr[0])){
                beginArc = arc;
            }
        }
        if(beginArc==null){
            beginArc = new Arc();
            beginArc.setLabel(charArr[0]);
            beginArc.setValue(value);
            root.getArcList().add(beginArc);
        }
        Arc arc = beginArc;*/
        char[] charArr = word.toCharArray();
        Node node = this.root;
        Arc arc = null;
        boolean isFinal = false;
        int charIndex = 0;
        for(char c:charArr){
            charIndex ++;
            arc = findOrCreateArc(node,c,value);
            if(charIndex==charArr.length){
                arc.getNextNode().setFinal(true);
            }
            if(value>=arc.getValue()){
                value -= arc.getValue();
                if(arc.getNextNode().isFinal() && arc.getNextNode().getFinalOut()==0){
                    arc.getNextNode().setFinalOut(value);
                }
            }else{
                int temp = arc.getValue()-value;
                arc.setValue(value);
                addValue(arc.getNextNode(),temp);
                value = 0;
            }
            node = arc.getNextNode();
        }
    }

    private Arc findOrCreateArc(Node node, char c, int value){
        if(node==null){
            return null;
        }
        if(node.getArcList()==null){
            node.setArcList(new ArrayList<Arc>());
        }
        Arc resultArc = null;
        for(Arc tmpArc:node.getArcList()){
            if(tmpArc!=null && tmpArc.getLabel() == c){
                resultArc = tmpArc;
            }
        }
        if(resultArc==null){
            resultArc = new Arc();
            resultArc.setLabel(c);
            resultArc.setValue(value);
            Node nextNode = new Node();
            resultArc.setNextNode(nextNode);
            node.getArcList().add(resultArc);
        }
        return resultArc;
    }

    private void printFst(Node node, int depth){
        if(node!=null && node.getArcList()!=null){
            for(Arc arc:node.getArcList()){
                for(int i=0;i<depth;i++){
                    System.out.print("\t");
                }
                String finalStr = "";
                if(arc.getNextNode()!=null && arc.getNextNode().isFinal()){
                    finalStr = "("+arc.getNextNode().getFinalOut()+")";
                }
                System.out.println(arc.getLabel()+":"+arc.getValue()+finalStr);
                printFst(arc.getNextNode(),depth+1);
            }
        }
    }

    private boolean containLabel(Arc arc, char c){
        return arc != null && arc.getLabel() == c;
    }

    private void addValue(Node node, int value){
        if(node==null || node.getArcList()==null){
            return;
        }
        List<Arc> arcList = node.getArcList();
        for(Arc arc:arcList){
            if(arc.getNextNode()!=null && arc.getNextNode().isFinal() && arc.getNextNode().getFinalOut()==0){
                arc.getNextNode().setFinalOut(arc.getValue()+value);
                arc.setValue(0);
            }else{
                arc.setValue(arc.getValue()+value);
            }
        }
    }

    private Integer getValue(String word){
        if(word==null || word.length()==0){
            return null;
        }
        Integer value = 0;
        char[] charArr = word.toCharArray();
        int charIndex = 0;
        Node node = this.root;
        for(char c:charArr){
            charIndex ++;
            Arc arc = findArc(node,c);
            if(arc==null){
                return null;
            }
            if(charIndex==charArr.length && arc.getNextNode().isFinal()==false){
                return null;
            }
            value += arc.getValue();
            if(arc.getNextNode().isFinal() && charIndex==charArr.length){
                value += arc.getNextNode().getFinalOut();
            }
            node = arc.getNextNode();
        }
        return value;
    }

    private Arc findArc(Node node, char c){
        if(node==null){
            return null;
        }
        if(node.getArcList()!=null){
            for(Arc arc:node.getArcList()){
                if(arc.getLabel()==c){
                    return arc;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        FSTService fstService = new FSTService();
        fstService.init();
        fstService.add("cat",5);
        fstService.add("deep",10);
        fstService.add("do",15);
        fstService.add("dog",2);
        fstService.add("dee",17);
        fstService.printFst(fstService.root,0);

        System.out.println(fstService.getValue("cat"));
        System.out.println(fstService.getValue("deep"));
        System.out.println(fstService.getValue("do"));
        System.out.println(fstService.getValue("dog"));
        System.out.println(fstService.getValue("dee"));

        System.out.println(fstService.getValue("ca"));

    }
}

@Data
class Node{
    private boolean isRoot;
    private List<Arc> arcList;
    private boolean isFinal;
    private int finalOut;
}

@Data
class Arc{
    private char label;
    private Integer value;
    private Node previousNode;
    private Node nextNode;
}

package FST;

import lombok.Data;

import javax.xml.bind.annotation.XmlInlineBinaryData;

public class FSTService {

    private Node root;

    private void init(){
        root = new Node();
        root.setRoot(true);
    }

    public void add(String word, int value){
        for(String c:word.split("")){
            Arc arc = root.getNextArc();
            if(containLabel(arc,c)){
                if(value>=arc.getOut()){
                    value -= arc.getOut();
                }else{
                    int temp = arc.getOut()-value;
                    arc.setOut(value);
                    addValue(arc.getNextNode(),temp);
                }
            }
        }
    }

    private boolean containLabel(Arc arc, String c){
        return arc != null && arc.getLabel() != null && arc.getLabel().equals(c);
    }

    private void addValue(Node node, int value){
        if(node==null || node.getNextArc()==null){
            return;
        }
        Arc arc = node.getNextArc();
        arc.setOut(arc.getOut()+value);
        while(arc.getNextNode()!=null && arc.getNextNode().getNextArc()!=null){
            arc.getNextNode().getNextArc().setOut(arc.getNextNode().getNextArc().getOut()+value);
            arc = arc.getNextNode().getNextArc();
        }
    }

    public static void main(String[] args) {
        String word = "abc";
        String[] arr = word.split("");
        for(String s:arr){
            System.out.println(s);
        }
    }

}

@Data
class Node{
    private boolean isRoot;
    private Arc previousArc;
    private Arc nextArc;
}

@Data
class Arc{
    private String label;
    private Integer out;
    private Node previousNode;
    private Node nextNode;
}

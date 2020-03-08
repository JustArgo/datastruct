package datastructure.trie;

import java.util.ArrayList;
import java.util.List;

public class AVL {

	public static class AVLNode{
	    int key;            //����ֵ
	    int height;         //���ĸ߶ȣ������Ϊ0
	    AVLNode left;      //����
	    AVLNode right;     //�Һ���

	    public AVLNode(int k, AVLNode left, AVLNode right){
	    	this.key = k;
	    	this.height = 0;
	    	this.left = left;
	    	this.right = right;
	    }

		public int getKey() {
			return key;
		}

		public void setKey(int key) {
			this.key = key;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public AVLNode getLeft() {
			return left;
		}

		public void setLeft(AVLNode left) {
			this.left = left;
		}

		public AVLNode getRight() {
			return right;
		}

		public void setRight(AVLNode right) {
			this.right = right;
		}
	    
	}
	
	
	private AVLNode root;
	private List<AVLNode> nodeList;
	
	/**
	 * ����ĳ���ڵ�ĸ߶�
	 * @param node
	 * @return
	 */
	private int height(AVLNode node){
		if(node!=null){
			return node.height;
		}
		return 0;
	}
	
	/**
	 * ������ת
	 * @return
	 */
	private AVLNode llRotate(AVLNode node){
		AVLNode leftNode = node.left;
		AVLNode left_right = leftNode.right;
		node.left = left_right;
		leftNode.right = node;
		
		leftNode.height = Math.max(height(leftNode.left),height(leftNode.right))+1;
		node.height = Math.max(height(node.left),height(node.right))+1;
		
		return leftNode;
	}
	
	/**
	 * ������ת
	 * @return
	 */
	private AVLNode lrRotate(AVLNode node){
		node.left = rrRotate(node.left);
		return llRotate(node);
	}
	
	/**
	 * ������ת
	 * @return
	 */
	private AVLNode rrRotate(AVLNode node){
		AVLNode rightNode = node.right;
		AVLNode right_left = rightNode.left;
		node.right = right_left;
		rightNode.left = node;
		
		rightNode.height = Math.max(height(rightNode.left), height(rightNode.right)) + 1;
		node.height = Math.max(height(node.left), height(node.right)) + 1;
		
		return rightNode;
	}
	
	/**
	 * ������ת
	 * @return
	 */
	private AVLNode rlRotate(AVLNode node){
		node.right = llRotate(node.right);
		return rrRotate(node);
	}
	
	public void printAVL(){
		if(this.nodeList==null){
			this.nodeList=new ArrayList<AVLNode>();
		}
		recurseAdd(this.root);
		for(int i=0;i<this.height(this.root);i++){
			//�ȴ�ӡ���tab
			for(int j=this.root.height-1-i;j>0;j--){
				System.out.print("\t");
			}
			for(int j=0;j<this.nodeList.size();j++){
				if(this.nodeList.get(j).height==this.root.height-i){
					String tab = "";
					for(int k=0;k<Math.pow(2, i);k++){
						tab += "\t";
					}
					System.out.print(this.nodeList.get(j).getKey()+tab);
				}
			}
			System.out.println("");
		}
	}
	
	public AVLNode insert(AVLNode root,int key){
		if(root==null){
			root = new AVLNode(key, null, null);
		}else if(key<root.key){
			root.left = insert(root.left,key);
			//�����ߵĸ߶ȱ��ұߵĸ߶ȳ���1
			if(height(root.left)-height(root.right)>1){
				//llRotate
				if(key<root.left.key){
					root = llRotate(root);
				}else{
					root = lrRotate(root);
				}
			}
		}else if(key>root.key){
			root.right = insert(root.right,key);
			//����ҵĸ߶ȱ���ߵĸ߶ȳ���1
			if(height(root.left)-height(root.right)>1){
				//rrRotate
				if(key>root.right.key){
					root = rlRotate(root);
				}else{
					root = rrRotate(root);
				}
			}
		}
		root.height = Math.max(height(root.left), height(root.right)) + 1;
		return root;
	}
	
	public AVLNode remove(AVLNode root, AVLNode node){
		if(root==null){
			return null;
		}
		//ɾ����ߵ�ĳ���ڵ�
		if(node.key<root.key){
			root.left = remove(root.left, node);
			if(height(root.right)-height(root.left)>1){
				AVLNode rightNode = root.right;
				if(height(rightNode.left)-height(rightNode.right)>1){
					root = rlRotate(root);
				}else{
					root = rrRotate(root);
				}
			}
		}else if(node.key>root.key){
			root.right = remove(root.right, node);
			if(height(root.left)-height(root.right)>1){
				AVLNode leftNode = root.left;
				if(height(leftNode.left)-height(leftNode.right)>1){
					root = llRotate(root);
				}else{
					root = lrRotate(root);
				}
			}
		}else{//�ҵ�Ҫɾ���Ľڵ�
			if(root.left!=null && root.right!=null){
				if(height(root.left)>height(root.right)){
					AVLNode maxNode = maximus(root.left);
	                root.key = maxNode.key;
	                root.left = remove(root.left, maxNode);
				}else{
					AVLNode minNode = minimus(root.right);
	                root.key = minNode.key;
	                root.right = remove(root.right, minNode);
				}
			}else{
				AVLNode tmp = root;
	            root = (root.left != null) ? root.left : root.right;
			}
		}
		
		return root;
	}
	
	private AVLNode minimus(AVLNode root) {
		AVLNode current = root;
		while(current!=null && current.left!=null){
			current = current.left;
		}
		return current;
	}

	private AVLNode maximus(AVLNode root) {
		AVLNode current = root;
		while(current!=null && current.right!=null){
			current = current.right;
		}
		return current;
	}

	private void recurseAdd(AVLNode node){
		if(node!=null){
			this.nodeList.add(node);
			recurseAdd(node.left);
			recurseAdd(node.right);
		}
	}

	public AVLNode getRoot() {
		return root;
	}

	public void setRoot(AVLNode root) {
		this.root = root;
	}

	public List<AVLNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<AVLNode> nodeList) {
		this.nodeList = nodeList;
	}
}

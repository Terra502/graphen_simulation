import com.sun.source.tree.Tree;

public class TreeNode {
    private String value;
    private TreeNode parentNode = null;
    private TreeNode leftChild = null;
    private TreeNode rightChild = null;

    public TreeNode(String value){
        this.value = value;
    }

    public TreeNode(String value, TreeNode parentNode){
        this.value = value;
        this.parentNode = parentNode;
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public TreeNode getLeftChild() {
        return leftChild;
    }

    public TreeNode getRightChild() {
        return rightChild;
    }

    public void setLeftChild(TreeNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setParentNode(TreeNode parentNode) {
        this.parentNode = parentNode;
    }

    public void setRightChild(TreeNode rightChild) {
        this.rightChild = rightChild;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}

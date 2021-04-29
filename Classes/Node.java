package Classes;

import java.lang.Comparable;

public class Node implements Comparable<Node>{
	public char c; //caractere armazenado
	public int freq; 
	Node left;
	Node right;
	
	
	public Node(char c, int freq, Node left, Node right){
		this.c = c;
		this.freq = freq;
		this.left = left;
		this.right = right;
	}
	
	public boolean isLeaf() {
		if(this.left == null && this.right == null) return true;
		else return false;
	}
	
	
	
	public int compareTo(Node o) {
		Node other = o;
		return this.freq - other.freq;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + c;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (c != other.c)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Node [Char=" + c + ", Freq=" + freq + "]";
	}
	
	
}

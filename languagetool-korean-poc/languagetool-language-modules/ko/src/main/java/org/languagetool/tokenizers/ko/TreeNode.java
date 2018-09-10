package org.languagetool.tokenizers.ko;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kr.co.shineware.util.common.model.Pair;

public class TreeNode<T>{
	public Pair<String,String> data;
	public TreeNode<T> parent;
	public List<TreeNode<T>> children;
	
	public TreeNode(Pair<String,String> data) {
		this.data = data;
		this.children = new LinkedList<TreeNode<T>>();
	}
	public TreeNode<T> addChild(Pair<String,String> data){
		TreeNode<T> childNode = new TreeNode<T>(data);
		childNode.parent = this;
		this.children.add(childNode);
		return childNode;
	}
	public TreeNode<T> addChild(TreeNode<T> child){
		child.parent = this;
		this.children.add(child);
		return child;
	}
}


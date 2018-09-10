package org.languagetool.tokenizers.ko;

import kr.co.shineware.util.common.model.Pair;

class DQNode{
	Pair<String,String> data;
	DQNode rlink;
	DQNode llink;
}

class Deque{
	DQNode front;
	DQNode rear;
	
	public Deque() {
		front = null;
		rear = null;
	}
	
	public boolean isEmpty() {
		return (front == null);
	}
	
	public void insertFront(Pair<String,String> item) {
		DQNode newNode = new DQNode();
		newNode.data = item;
		if(isEmpty()) {
			front = newNode;
			rear = newNode;
			newNode.rlink = null;
			newNode.llink=null;
			
		}
		else {
			front.llink = newNode;
			newNode.rlink = front;
			newNode.llink = null;
			front = newNode;
		}
	}
	
	public void insertRear(Pair<String,String> item) {
		DQNode newNode = new DQNode();
		newNode.data = item;
		if(isEmpty()) {
			front = newNode;
			rear = newNode;
			newNode.rlink = null;
			newNode.llink = null;
		}
		else {
			rear.rlink = newNode;
			newNode.rlink = null;
			newNode.llink = rear;
			rear = newNode;			
		}	
	}
	
	public Pair<String,String> deleteFront() {
		if(isEmpty()) {
			return null;
		}
		else {
			Pair<String,String> item = front.data;
			if(front.rlink == null) {
				front = null;
				rear = null;
			}
			else {
				front = front.rlink;
				front.llink = null;
			}
			return item;
		}
	}
	
	public Pair<String,String> deleteRear() {
		if(isEmpty()) {
			return null;
		}
		else {
			Pair<String,String> item = rear.data;
			if(rear.llink==null) {
				rear = null;
				front = null;
				
			}
			else {
				rear = rear.llink;
				rear.rlink = null;
			}
			return item;
		}
	}
	
	public void removeFront() {
		if(isEmpty()) {
			System.out.println("Front Removing fail! DQueue is empty!!");
		}
		else {
			if(front.rlink ==null) {
				front = null;
				rear = null;
			}
			else {
				front = front.rlink;
				front.llink = null;
			}
		}
	}
	
	public void removeRear() {
		if(isEmpty()) {
			System.out.println("Rear Removing fail! DQueue is empty!!");
		}
		else {
			if(rear.llink ==null) {
				rear = null;
				front = null;
			}
			else {
				rear= rear.llink;
				rear.rlink = null;
			}
		}
	}
	
	public Pair<String,String> peekFront() {
		if(isEmpty()) {
			System.out.println("Front Peeking fail! DQueue is empty!!");
			return null;
		}
		else return front.data;
		
	}
	
	public Pair<String,String> peekRear() {
		if(isEmpty()) {
			System.out.println("Rear Peeking fail! DQueue is empty!!");
			return null;
		}
		else return rear.data;
		
	}
}
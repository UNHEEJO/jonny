
package org.languagetool.tokenizers.ko;

import kr.co.shineware.util.common.model.Pair;

public class Stack {
    
    private Node top;
    
    // 노드 class 단순연결리스트와 같다.
    private class Node{
        
        private Pair<String,String> data;
        private Node nextNode;
        
        Node(Pair<String,String> data){
            this.data = data;
            this.nextNode = null;
        }
    }
    
    // 생성자, stack이 비어있으므로 top은 null이다.
    public Stack(){
        this.top = null;
    }
    
    // 스택이 비어있는지 확인
    public boolean isEmpty(){
        return (top == null);
    }
    
    // item 을 스택의 top에 넣는다.
    public void push(Pair<String,String> item){
        
        Node newNode = new Node(item);
        newNode.nextNode = top;
        top = newNode;
        
    }
    
    // top 노드의 데이터를 반환한다.
    public Pair<String,String> peek(){
        if(this.isEmpty()) throw new ArrayIndexOutOfBoundsException();
        return top.data;
    }
    
    // top 노드를 스택에서 제거한다.
    public Pair<String,String> pop(){
        Pair<String,String> item = new Pair();
        item = this.peek();
        top = top.nextNode;
        return item;
    }

}

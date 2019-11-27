/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

/**
 *
 * @author kostis
 */
public class LinkedListNode <T> {
    // Prev -> Node -> Next
    LinkedListNode _next,_prev;
    private T _data;

    public LinkedListNode(T data){
        _next=null;
        _prev=null;
        _data=data;
    }

    public T get(){
        return _data;
    }

    public void set(T data){
        _data=data;
    }

    public LinkedListNode getNext(){
        return _next;
    }

    public void setNext(LinkedListNode that){
        _next=that;
    }

    public LinkedListNode getPrev(){
        return _prev;
    }

    public void setPrev(LinkedListNode that){
        _prev=that;
    }
    
    public void link(LinkedListNode that){
        link(this,that);
    }
    
    public void remove(){
        if (this.getPrev()!=null)
            getPrev().setNext(this.getNext());
        if (this.getNext()!=null)
            this.getNext().setPrev(this.getPrev());
        this.setNext(null);
        this.setPrev(null);
    }

    public void insertAfter(LinkedListNode that){
        assert that!=null: "trying to link insert null in a list";
        that.link(this.getNext());
        this.link(that);
    }
    
    public static void link(LinkedListNode one,LinkedListNode two){
        LinkedListNode ex;
        if (one!=null){
            ex = one.getNext();
            one.setNext(two);
            if (ex!=null)
                ex.setPrev(null);
        }
        
        if (two!=null){
            ex = two.getPrev();
            two.setPrev(one);
            if (ex!=null)
                ex.setNext(null);
        }
    }
}
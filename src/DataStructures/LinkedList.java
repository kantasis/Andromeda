/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

/**
 *
 * @author GeorgeKantasis
 */
public class LinkedList <T>{
    
    // the head has no previous
    private LinkedListNode _head;
    // the tail has no next
    private LinkedListNode _tail;
    
    public LinkedList(){
        _head=null;
        _tail=null;
    }
    
    public LinkedListNode getHead(){
        return _head;
    }
    
    public LinkedListNode getTail(){
        return _tail;
    }
    
    public void insertAtTail(T data){
        LinkedListNode nuevo = new LinkedListNode(data);
        if (_tail!=null)
            _tail.link(nuevo);
        _tail=nuevo;
        if (_head==null)
            _head=nuevo;
    }
    
    public void insertAtHead(T data){
        LinkedListNode nuevo = new LinkedListNode(data);
        if (_head!=null)
            nuevo.link(_head);
        _head=nuevo;
        if (_tail==null)
            _tail=nuevo;
    }
    
    public void removeHead(){
        LinkedListNode nuevo = _head.getNext();
        LinkedListNode.link(_head, null);
        _head=nuevo;
        if (nuevo==null)
            _tail=null;
    }
    
    public void removeTail(){
        LinkedListNode nuevo = _tail.getPrev();
        LinkedListNode.link(null, _tail);
        _tail=nuevo;
        if (nuevo==null)
            _head=null;
    }
    
}

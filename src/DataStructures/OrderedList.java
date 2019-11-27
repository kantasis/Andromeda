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
public class OrderedList <T> {
    
    private LinkedList<Datum<T>> _data;
    
    public OrderedList(){
        _data = new LinkedList();
    }
    
    public void add(Comparable key, T value){
        
    }
    /*
    public T getValue(Comparable key){
        LinkedListNode <Datum<T>>iterator = _data.getHead();
        while (iterator!=null){
            Comparable iter_key = iterator.get().getKey();
            if (key.compareTo(iter_key)==0)
                return Z
            
            iterator=iterator.getNext();
        }        
    }
*/
}

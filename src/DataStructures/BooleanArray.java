package DataStructures;

public class BooleanArray {
    
    public static boolean any(boolean[] array){
        for (boolean current: array)
            if (current)
                return true;
        return false;
    }
    
    public static boolean all(boolean[] array){
        for (boolean current: array)
            if (current)
                return false;
        return true;
    }
    
    public static boolean[] or(boolean[] one, boolean[] other ){
        _assertSizeAlignment(one,other);
        int N = one.length;
        boolean[] result = new boolean[N];
        for(int i=0;i<N;i++)
            result[i] = one[i] || other[i];
        return result;
    }

    public static boolean[] and(boolean[] one, boolean[] other ){
        _assertSizeAlignment(one,other);
        int N = one.length;
        boolean[] result = new boolean[N];
        for(int i=0;i<N;i++)
            result[i] = one[i] && other[i];
        return result;
    }
    
    public static boolean[] not(boolean[] one){
        int N = one.length;
        boolean[] result = new boolean[N];
        for(int i=0;i<N;i++)
            result[i] = ! one[i];
        return result;
    }
    
    public static int count(boolean[] one){
        int result = 0;
        int N = one.length;
        for(int i=0;i<N;i++)
            if(one[i])
                result++;
        return result;
    }
    
    /**
     * Assertion whether the two arrays have the same length
     * @param that the matrix to be compared to
     */
    private static void _assertSizeAlignment(boolean[] one, boolean[] other){
        assert  one.length==other.length:
            String.format("Incompatible Arrays %s / %s",one.length,other.length);
    }
    
}

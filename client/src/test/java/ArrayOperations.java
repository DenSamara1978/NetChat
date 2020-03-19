import java.util.Arrays;

public class ArrayOperations {
    public static boolean checkArrayForOneAndFour ( int[] arr )
    {
        int oneCount = 0;
        int fourCount = 0;
        for ( int value : arr )
        {
            if (value == 1)
                ++oneCount;
            else if (value == 4)
                ++fourCount;
            else
                return false;
        }
        return (( oneCount > 0 ) && ( fourCount > 0 ));
    }

    public static int[] getArrayAfterLastFour ( int[] arr )
    {
        if ( arr.length == 0 )
            throw new RuntimeException ( "Array is empty" );

        int startPosition = -1;
        for ( int i = 0; i < arr.length; ++i )
        {
            if ( arr [i] == 4 )
                startPosition = i + 1;
        }
        if ( startPosition == -1 )
            throw new RuntimeException ( "Array is empty" );

        int[] res = Arrays.copyOfRange ( arr, startPosition, arr.length );
        return res;
    }
}

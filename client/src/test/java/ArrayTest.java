import org.junit.Assert;
import org.junit.Test;

public class ArrayTest {
    @Test
    public void testOneAndFourConsisting ()
    {
        Assert.assertFalse ( ArrayOperations.checkArrayForOneAndFour ( new int [] {} ));
        Assert.assertFalse ( ArrayOperations.checkArrayForOneAndFour ( new int [] {1} ));
        Assert.assertFalse ( ArrayOperations.checkArrayForOneAndFour ( new int [] {4} ));
        Assert.assertFalse ( ArrayOperations.checkArrayForOneAndFour ( new int [] {0} ));
        Assert.assertTrue ( ArrayOperations.checkArrayForOneAndFour ( new int [] {1,4} ));
        Assert.assertFalse ( ArrayOperations.checkArrayForOneAndFour ( new int [] {1,4,3} ));
        Assert.assertTrue ( ArrayOperations.checkArrayForOneAndFour ( new int [] {1,4,4,1} ));
    }

    @Test
    public void testArrayAfterLastFour ()
    {
        Assert.assertArrayEquals ( new int [] {2,3}, ArrayOperations.getArrayAfterLastFour ( new int [] {1,4,2,3} ));
        Assert.assertArrayEquals ( new int [] {3}, ArrayOperations.getArrayAfterLastFour ( new int [] {1,2,4,3} ));
        Assert.assertArrayEquals ( new int [] {}, ArrayOperations.getArrayAfterLastFour ( new int [] {1,2,3,4} ));
        Assert.assertArrayEquals ( new int [] {5,6}, ArrayOperations.getArrayAfterLastFour ( new int [] {1,4,2,3,4,5,6} ));
    }

    @Test(expected = RuntimeException.class)
    public void testArrayAfterLastFourExcepted1 ()
    {
        Assert.assertArrayEquals ( new int [] {}, ArrayOperations.getArrayAfterLastFour ( new int [] {} ));
    }

    @Test(expected = RuntimeException.class)
    public void testArrayAfterLastFourExcepted2 ()
    {
        Assert.assertArrayEquals ( new int [] {}, ArrayOperations.getArrayAfterLastFour ( new int [] {1,2,3} ));
    }
}

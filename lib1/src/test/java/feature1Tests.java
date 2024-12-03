import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class feature1Tests {
    @Test
    public void function1Test(){
        var feature1 = new feature1();

        var result = feature1.function1();

        assertEquals(1, result);
    }

    @Test
    public void function1Test2(){
        var feature1 = new feature1();

        var result = feature1.function1();

        assertEquals(2, result);
    }
}

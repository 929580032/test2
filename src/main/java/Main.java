import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        long before = System.currentTimeMillis();

        String text = FileUtil.readFile(args[1]);
        WordFilter.setArgs(args);
        WordFilter.init();
        StringBuilder result = WordFilter.doFilter(text);
        FileUtil.writeFile(args[2], String.valueOf(result), WordFilter.getResultCount());

        long after = System.currentTimeMillis();
        System.out.println(after - before);
        System.out.println(WordFilter.getResultCount());
    }


    @Test
    public void testDoFilter() {
        String text = "这一次，FaLG连话都不想跟f                 uck说了，摆明了是不想理睬f                 uck套近乎的举动。\n" +
                "　　f435678u4567c!*&^%$#k也不是对每个女生都会这样主动套近乎，他只是……算了，既然FaLG不想提，他就权当那次邂逅只是过往的一段回忆好了。";
        StringBuilder actualAnswear = WordFilter.doFilter(text);
        StringBuilder expectedAnswear = new StringBuilder("Total: 5\n" +
                "Line1: <法轮功> FaLG\n" +
                "Line1: <fuck> f                 uck\n" +
                "Line1: <fuck> f                 uck\n" +
                "Line2: <fuck> f435678u4567c!*&^%$#k\n" +
                "Line2: <法轮功> FaLG\n");
        Assertions.assertEquals(expectedAnswear, actualAnswear);
    }
}

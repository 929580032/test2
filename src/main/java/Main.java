import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String text = FileUtil.readFile(args[1]);
        WordFilter.setArgs(args);
        WordFilter.init();
        StringBuilder result = WordFilter.doFilter(text);
        FileUtil.writeFile(args[2], String.valueOf(result), WordFilter.getResultCount());
    }
}

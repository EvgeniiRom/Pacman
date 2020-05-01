import sun.text.resources.iw.FormatData_iw_IL;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PacField {
    private int width = 0;
    private int height = 0;
    private int[][] field = new int[0][0];

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getField() {
        return field;
    }

    public void read(String path) throws IOException {
        File file = new File(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        List<String> lines = new ArrayList<>();
        Stream<String> ss = bufferedReader.lines();
        ss.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                lines.add(s);
            }
        });
        width = lines.get(0).split(" ").length;
        height = lines.size();
        field = new int[height][width];
        for(int i = 0; i<height; i++) {
            String line = lines.get(i);
            String[] blocks = line.split(" ");
            for(int j = 0; j<width; j++){
                field[i][j] = Integer.valueOf(blocks[j]);
            }
        }
        bufferedReader.close();
    }

    public void printField(){
        for (int[] row : field) {
            for (int block : row) {
                System.out.print(block + " ");
            }
            System.out.println();
        }
    }
}

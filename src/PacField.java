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
    private List<Coord<Integer>> sweets = new ArrayList<>();
    private List<Coord<Integer>> bots = new ArrayList<>();
    private List<Coord<Integer>> boosts = new ArrayList<>();
    private Coord<Integer> player = new Coord<>(1, 1);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[][] getField() {
        return field;
    }

    public List<Coord<Integer>> getSweets() {
        return sweets;
    }

    public List<Coord<Integer>> getBots() {
        return bots;
    }

    public List<Coord<Integer>> getBoosts() {
        return boosts;
    }

    public Coord<Integer> getPlayer() {
        return player;
    }

    public int getBlock(Coord<Integer> blockIndex){
        if(!validBlockIndex(blockIndex)){
            return -1;
        }
        return field[blockIndex.y][blockIndex.x];
    }

    public boolean validBlockIndex(Coord<Integer> blockIndex) {
        return blockIndex.x >= 0 && blockIndex.x <  width && blockIndex.y >= 0 && blockIndex.y < height;
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

        for (int i = 0; i < height; i++) {
            String line = lines.get(i);
            String[] blocks = line.split(" ");
            for (int j = 0; j < width; j++) {
                if (blocks[j].equals("1")) {
                    field[i][j] = 1;
                }
                if (blocks[j].equals("g")) {
                    field[i][j] = 2;
                }
                if (blocks[j].equals("s")) {
                    sweets.add(new Coord<>(j, i));
                }
                if (blocks[j].equals("b")) {
                    bots.add(new Coord<>(j, i));
                }
                if (blocks[j].equals("p")) {
                    player = new Coord<>(j, i);
                }
                if (blocks[j].equals("B")) {
                    boosts.add(new Coord<>(j, i));
                }
            }
        }


        bufferedReader.close();
    }

    public void printField() {
        for (int[] row : field) {
            for (int block : row) {
                System.out.print(block + " ");
            }
            System.out.println();
        }
    }
}

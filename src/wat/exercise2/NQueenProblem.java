package wat.exercise2;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NQueenProblem {

    private final int[][] result;
    private final int size;
    public NQueenProblem(int n) {
        size = n;
        result = new int[size][size];
    }

    class Position {
        int row;
        int col;
        Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
        @Override
        public String toString() {
            return "P<" + row + ", " + col + ">";
        }

        int calculateScore() {
            int diagonalUpLeft, diagonalDownRight, diagonalDownLeft, diagonalUpRight;
            diagonalUpLeft = Math.min(row, col);
            diagonalDownRight = Math.min(size - row - 1, size - col);
            diagonalDownLeft = Math.min(size - row, col - 1);
            diagonalUpRight = Math.min(row, size - col);
            return diagonalUpLeft + diagonalDownRight + diagonalDownLeft + diagonalUpRight;
        }
    }



    private static void prettyPrint(int[][] result) {
        System.out.println(Arrays.stream(result).map(Arrays::toString).collect(Collectors.joining("\n")) + "\n");
    }

    private void solve() {
        if (getSolution(size, 0)) {
            System.out.println("Found solution.");
            prettyPrint(result);
        } else {
            System.out.println("Solution not available.");
        }
    }

    private boolean getSolution(int n, int row) {
        if (n <= 3) return false;
        else if (row == n) return true;
        final List<Position> localPositions = new LinkedList<>();
        for (int col = 0; col < n; col++) {
            if(isQueenSafe(row, col)) {
                localPositions.add(new Position(row, col));
            }
        }

        if(localPositions.isEmpty()) return false;
        localPositions.sort(Comparator.comparingDouble(Position::calculateScore).reversed());
        for(Position position : localPositions) {
            result[position.row][position.col] = 1;
            if (getSolution(n, row + 1)) return true;
            result[position.row][position.col] = 0;
        }
        return false;
    }

    private boolean isQueenSafe(int row, int col) {
        return isQueenSafeRowAndColumn(row, col) && isQueenSafeDiagonal(row, col) && isQueenSafeAntidiagonal(row, col);
    }
    private boolean isQueenSafeRowAndColumn(int row, int col) {
        for(int i = 0; i < size; i++) {
            if(result[i][col] == 1 || result[row][i] == 1) return false;
        }
        return true;
    }

    private boolean isQueenSafeDiagonal(int row, int col) {
        int rowStart = row;
        int colStart = col;
        while(rowStart > 0 && colStart > 0) {
            rowStart--;
            colStart--;
        }
        while(rowStart < size && colStart < size) {
            if(result[rowStart][colStart] == 1) return false;
            rowStart++;
            colStart++;
        }
        return true;
    }

    private boolean isQueenSafeAntidiagonal(int row, int col) {
        int rowStart = row;
        int colStart = col;
        while(rowStart > 0 && colStart < size - 1) {
            rowStart--;
            colStart++;
        }
        while(rowStart < size && colStart > 0) {
            if(result[rowStart][colStart] == 1) return false;
            rowStart++;
            colStart--;
        }
        return true;
    }

    public static void main(String[] args) {
        Integer input = 35;
        NQueenProblem nQueenProblem = new NQueenProblem(input);
        System.out.println(String.format("Finding solution for a %sx%s chessboard...", input, input));
        long startTime = System.nanoTime();
        nQueenProblem.solve();
        System.out.println(String.format("Estimated time: %s", ((double) System.nanoTime() - startTime) / 1_000_000.0));
    }

}

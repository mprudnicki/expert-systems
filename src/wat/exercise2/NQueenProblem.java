package wat.exercise2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NQueenProblem {

    private final Position[] positions;
    private final int[][] result;
    private final int size;
    public NQueenProblem(int n) {
        size = n;
        positions = new Position[size];
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
            boolean isSafe = true;
            for (int placedQueen = 0; placedQueen < row; placedQueen++) {
                if (isQueenNotSafe(placedQueen, col, row)) {
                    isSafe = false;
                }
            }

            if (isSafe) {
                localPositions.add(new Position(row, col));
            }
        }

        if(localPositions.isEmpty()) return false;

        for (int col = 0; col < n; col++) {
            boolean isSafe = true;
            positions[row] = new Position(row, col);

            for (int placedQueen = 0; placedQueen < row; placedQueen++) {
                if (isQueenNotSafe(placedQueen, col, row)) {
                    isSafe = false;
                }
            }

            if (isSafe) {
                Position localBestSolution = pickBestPositionMaxLeft(localPositions);
                result[localBestSolution.row][localBestSolution.col] = 1;
                if (getSolution(n, row + 1)) return true;
                result[localBestSolution.row][localBestSolution.col] = 0;
            }
        }
        return false;
    }

    private Position pickBestPositionMaxLeft(List<Position> positions) {
        final Comparator<Position> positionComparator = Comparator.comparingInt(position -> position.row);
        positions.sort(positionComparator);
        if(positions.isEmpty()) return null;
        else return positions.get(0);
    }

    private boolean isQueenNotSafe(int placedQueen, int col, int row) {
        return checkColumn(placedQueen, col)
                || checkDiagonal(placedQueen, col, row)
                || checkAntidiagonal(placedQueen, col, row);
    }

    private boolean checkColumn(int placedQueen, int col) {
        return positions[placedQueen].col == col;
    }

    private boolean checkDiagonal(int placedQueen, int col, int row) {
        return positions[placedQueen].row - positions[placedQueen].col == row - col;
    }

    private boolean checkAntidiagonal(int placedQueen, int col, int row) {
        return positions[placedQueen].row + positions[placedQueen].col == row + col;
    }

    public static void main(String[] args) {
        Integer input = 5;
        NQueenProblem nQueenProblem = new NQueenProblem(input);
        nQueenProblem.solve();
    }

}

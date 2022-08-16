package app.Twenty48;

import java.util.ArrayList;
import java.util.function.IntConsumer;

class Controller {

  static boolean isGridSolved(int[][] grid) {
    for (var row:grid) for (var col:row) if (col == 2048) return true;
    return false;
  }

  static boolean isGridFull(int[][] grid) {
    for (var row:grid) for (var col:row) if (col == 0) return false;
    return true;
  }

  static int[][] spawnNumber(int[][] grid) { // new grid[x][y]
    var newGrid = copyOf(grid);
    var number = generateNumber();
    var coordinates = locateSpawnCoordinates(newGrid);
    return updateGrid(newGrid, coordinates, number);
  }

  static int generateNumber() {
    return Math.random() > 0.10 ? 2 : 4;
  }

  static int[] locateSpawnCoordinates(int[][] grid) {
    var emptyCells = new ArrayList<int[]>();
    for (var x = 0; x < grid.length; x++) {
      var row = grid[x];
      for (var y = 0; y < row.length; y++) {
        if (row[y] == 0) emptyCells.add(new int[]{x,y});
      }
    }
    return emptyCells.isEmpty() ? null : emptyCells.get((int)(Math.random() * (emptyCells.size() - 1)));
  }

  static int[][] updateGrid(int[][] grid, int[] at, int value) {
    grid[at[0]][at[1]] = value;
    return grid; // copyOf
  }

  static int[][] manipulateGrid(int[][] grid, Action input) {
    return switch(input) {
      case left -> shiftCellsLeft(grid);
      case right -> shiftCellsRight(grid);
      case up -> shiftCellsUp(grid);
      case down -> shiftCellsDown(grid);
    };
  }

  static int[][] shiftCellsLeft(int[][] grid) {
    var rows = new int[grid.length][];
    for (int r = 0; r < rows.length; ++r) {
      rows[r] = mergeAndOrganizeCells(grid[r]);
    }
    return rows; // copyOf
  }

  static int[][] shiftCellsRight(int[][] grid) {
    var rows = new int[grid.length][];
    for (int r = 0; r < rows.length; r++) {
       rows[r] = reverse(mergeAndOrganizeCells(reverse(grid[r])));
    }
    return rows; // copyOf
  }

  static int[][] shiftCellsUp(int[][] grid) {
    assert grid != null : "grid";
    var rows = transpose(grid); // copyOf
    for (var r = 0; r < rows.length; r++) {
      rows[r] = mergeAndOrganizeCells(rows[r]);
    }
    return transpose(rows);
  }

  static int[][] shiftCellsDown(int[][] grid) {
    assert grid != null : "grid";
    var rows = transpose(grid); // copyOf
    for (var r = 0; r < rows.length; r++) {
      rows[r] = reverse(mergeAndOrganizeCells(reverse(rows[r])));
    }
    return transpose(rows);
  }

  static int[] mergeAndOrganizeCells(int[] row) {
    return organize(merge(row,0,1),0,1); // organize(merge(row.copyOf())).copyOf()
  }

  static IntConsumer mergeListener = i -> {}; // kludge for Game.class

  static int[] merge(int[] row, int idxToMatch, int idxToCompare) {
    if (idxToMatch >= row.length) {
      return row;
    } else if (idxToCompare >= row.length) {
      return merge(row, idxToMatch + 1, idxToMatch + 2);
    } else if (row[idxToMatch] == 0) {
      return merge(row, idxToMatch + 1, idxToMatch + 2);
    } else if (row[idxToMatch] == row[idxToCompare]) {
      row[idxToMatch] *= 2;
      row[idxToCompare] = 0;
      mergeListener.accept(row[idxToMatch]);
      return merge(row, idxToMatch + 1, idxToMatch + 2);
    } else if (row[idxToCompare] != 0) {
      return merge(row, idxToMatch + 1, idxToMatch + 2);
    } else {
      return merge(row, idxToMatch, idxToCompare + 1);
    }
  }

  static int[] organize(int[] row, int idxToMatch, int idxToCompare) {
    if (idxToMatch >= row.length) {
      return row;
    } else if (idxToCompare >= row.length) {
      return organize(row, idxToMatch + 1, idxToMatch + 2);
    } else if (row[idxToMatch] != 0) {
      return organize(row, idxToMatch + 1, idxToMatch + 2);
    } else if (row[idxToCompare] != 0) {
      row[idxToMatch] = row[idxToCompare];
      row[idxToCompare] = 0;
      return organize(row, idxToMatch + 1, idxToMatch + 2);
    } else {
      return organize(row, idxToMatch, idxToCompare + 1);
    }
  }

  static int[][] copyOf(int[][] grid) {
    var newGrid = new int[grid.length][];
    for (var r = 0; r < newGrid.length; r++) newGrid[r] = copyOf(grid[r]);
    return newGrid;
  }

  static int[] copyOf(int[] row) {
    var newRow = new int[row.length];
    for (var c = 0; c < newRow.length; c++) newRow[c] = row[c];
    return newRow;
  }

  static int[] reverse(int[] row) {
    for (int i = 0, j = row.length - 1; i < j; i++, j--) {
      var t = row[i];
      row[i] = row[j];
      row[j] = t;
    }
    return row;
  }

  static int[][] transpose(int[][] grid) { // new grid[y][x]
    // assert rectangular(grid);
    var gT = new int[grid[0].length][grid.length];
    for (var x = 0; x < grid.length; x++) {
      var row = grid[x];
      for (var y = 0; y < row.length; y++) {
        gT[y][x] = row[y];
      }
    }
    return gT;
  }

  static boolean rectangular(int[][] grid) {
    var n = grid[0].length;
    for (var i = 1; i < grid.length; i++) {
      if (n != grid[i].length) return false;
    }
    return true;
  }


}

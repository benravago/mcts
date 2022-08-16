package app.Twenty48;

record Position(int[][] grid) {

  @Override
  public String toString() {
    return java.util.Arrays.deepToString(grid);
  }
}

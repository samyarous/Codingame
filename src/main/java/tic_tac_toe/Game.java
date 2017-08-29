package tic_tac_toe;

public class Game {

    public static final String X_SYMBOL="X";
    public static final String O_SYMBOL="O";
    public static final String NEUTRAL_SYMBOL=".";

    public static enum Side {
        NEUTRAL,
        X,
        O;

        @Override
        public String toString() {
            switch (this){
                case X:
                    return X_SYMBOL;
                case O:
                    return O_SYMBOL;
            }
            return NEUTRAL_SYMBOL;
        }
    }

    public static enum Outcome{
        UNDETERMINED,
        DRAW,
        X_WON,
        O_WON;

    }

    public static class Board{
        public static final int BOARD_SIZE    = 3;


        private Side[][] cells = new Side[BOARD_SIZE][BOARD_SIZE];

        public Board(){
            for (int x = 0; x < Board.BOARD_SIZE; x++) {
                for (int y = 0; y < Board.BOARD_SIZE; y++) {
                    cells[x][y] = Side.NEUTRAL;
                }
            }
        }

        public void setCell(Side state, int x, int y){
            Side current = cells[x][y];
            if (current == Side.NEUTRAL){
                cells[x][y] = state;
            } else {
                throw new RuntimeException(String.format("Cell [(%s,%s):%s] is not empty", x, y, current));
            }
        }

        public Side getCell(int x, int y){
            return cells[x][y];
        }

    }


    public Side getCurrentSide() {
        return currentSide;
    }

    private Side currentSide = Side.X; // X start first
    private Board board = new Board();
    private int[] rowsSums = new int[Board.BOARD_SIZE];
    private int[] colsSums = new int[Board.BOARD_SIZE];
    private int firstDiagSum = 0;
    private int secondDiagSum = 0;
    private int totalSum = 0;

    public void playTurn(Point p){
        this.board.setCell(currentSide, p.x, p.y);
        totalSum ++;
        if (currentSide == Side.X){
            rowsSums[p.y]++;
            colsSums[p.x]++;
            if(p.x == p.y){ // diagonal
                firstDiagSum++;
            }
            if(p.x + p.y + 1 == Board.BOARD_SIZE){
                secondDiagSum++;
            }
            currentSide = Side.O;
        }
        else if (currentSide == Side.O){
            rowsSums[p.y]--;
            colsSums[p.x]--;
            if(p.x == p.y){ // diagonal
                firstDiagSum--;
            }
            if(p.x + p.y + 1 == Board.BOARD_SIZE){
                secondDiagSum--;
            }
            currentSide = Side.X;
        }
    }

    public Outcome getGameOutcome(){
        for (int x=0; x < Board.BOARD_SIZE; x++){
            if(colsSums[x] == Board.BOARD_SIZE){
                return Outcome.X_WON;
            }
            if(colsSums[x] == -Board.BOARD_SIZE){
                return Outcome.O_WON;
            }
        }
        for (int y=0; y < Board.BOARD_SIZE; y++){
            if(rowsSums[y] == Board.BOARD_SIZE){
                return Outcome.X_WON;
            }
            if(rowsSums[y] == -Board.BOARD_SIZE){
                return Outcome.O_WON;
            }
        }
        if (firstDiagSum == Board.BOARD_SIZE){
            return Outcome.X_WON;
        }
        if (firstDiagSum == -Board.BOARD_SIZE){
            return Outcome.O_WON;
        }
        if (secondDiagSum == Board.BOARD_SIZE){
            return Outcome.X_WON;
        }
        if (secondDiagSum == -Board.BOARD_SIZE){
            return Outcome.O_WON;
        }
        if (totalSum == (Board.BOARD_SIZE * Board.BOARD_SIZE)){
            return Outcome.DRAW;
        }
        return Outcome.UNDETERMINED;
    }

    public Board getBoard(){
        return this.board;
    }

    public void draw() {
        StringBuffer str = new StringBuffer();
        for (int y = 0; y < Board.BOARD_SIZE; y++){
            for (int x = 0; x < Board.BOARD_SIZE; x++){
                str.append(board.getCell(x, y).toString());
            }
            str.append("\n");
        }
        if (getGameOutcome() == Outcome.UNDETERMINED){
            str.append(String.format("Player %s turn", getCurrentSide()));
        } else {
            str.append(String.format("Game Over: %s", getGameOutcome()));
        }
        System.out.println(str.toString());
    }
}

package tic_tac_toe;

public class Game {

    public static final int BOARD_SIZE    = 3;
    static final String X_SYMBOL="X";
    static final String O_SYMBOL="O";
    static final String NEUTRAL_SYMBOL=".";
    private int hashValue = 0;
    private boolean invalidateHash = false;
    private Outcome outcome = Outcome.UNDETERMINED;
    private boolean invalidateOutcome = false;
    private Side[][] cells = new Side[BOARD_SIZE][BOARD_SIZE];
    private int[] rowsSums = new int[Game.BOARD_SIZE];
    private int[] colsSums = new int[Game.BOARD_SIZE];
    private int firstDiagSum = 0;
    private int secondDiagSum = 0;
    private int totalCount = 0;

    public Game () {
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
            for (int x = 0; x < Game.BOARD_SIZE; x++) {
                cells[x][y] = Side.NEUTRAL;
            }
        }
    }

    public static Game fromString(String str){
        Game result = new Game();
        int index = 0;
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
            for (int x = 0; x < Game.BOARD_SIZE; x++) {
                Side s = Side.fromString(Character.toString(str.charAt(index)));
                result.setCell(x, y, s);
                index ++; // next char
            }
            index ++; // skip col separator
        }
        return result;
    }

    public static Game fromHash(int hash){
        Game result = new Game();
        int val;
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
            for (int x = 0; x < Game.BOARD_SIZE; x++) {
                val = hash & 3;
                Side s = Side.NEUTRAL;
                if (val == 0) s = Side.NEUTRAL;
                if (val == 1) s = Side.X;
                if (val == 2) s = Side.O;
                result.setCell(x, y, s);
                hash >>= 2;
            }
        }
        return result;
    }

    public void setCell(int x, int y){
        setCell(x, y, getCurrentSide());
    }

    public void setCell(int x, int y, Side side){
        if(cells[x][y] == Side.NEUTRAL && side != Side.NEUTRAL){
            this.totalCount++;
        }
        if(cells[x][y] != Side.NEUTRAL && side == Side.NEUTRAL){
            this.totalCount--;
        }
        int diff = side.toValue() - cells[x][y].toValue();
        colsSums[x] += diff;
        rowsSums[y] += diff;
        if(x == y){
            firstDiagSum += diff;
        }
        if(x + y == BOARD_SIZE - 1){
            secondDiagSum += diff;
        }
        cells[x][y] = side;
        invalidateHash = true;
        invalidateOutcome = true;

    }

    public boolean isFull(){
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
            for (int x = 0; x < Game.BOARD_SIZE; x++) {
                if (cells[x][y] == Side.NEUTRAL) return false;
            }
        }
        return true;
    }

    public Side getCell(int x, int y){
        return cells[x][y];
    }

    public void draw(){
        StringBuffer str = new StringBuffer();
        for (int y = 0; y < Game.BOARD_SIZE; y++){
            for (int x = 0; x < Game.BOARD_SIZE; x++){
                str.append(this.getCell(x, y).toString());
            }
            str.append("\n");
        }
        System.out.println(str.toString());
        if (getGameOutcome() == Outcome.UNDETERMINED){
            System.out.println(String.format("Player %s turn", getCurrentSide()));
        } else {
            System.out.println(String.format("Game Over: %s", getGameOutcome()));
        }
    }



    @Override
    public boolean equals(Object o) {
        return o.hashCode() == this.hashCode();
    }

    public Side getCurrentSide() {
        if(this.totalCount % 2 == 0){
            return Side.X;
        } else {
            return Side.O;
        }
    }

    public void playTurn(Point p){
        this.setCell(p.x, p.y);
    }

    private Outcome recomputeGameOutcome(){
        for (int x = 0; x < Game.BOARD_SIZE; x++) {
            if (colsSums[x] == Game.BOARD_SIZE) {
                return Outcome.X_WON;
            }
            if (colsSums[x] == -Game.BOARD_SIZE) {
                return Outcome.O_WON;
            }
        }
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
            if (rowsSums[y] == Game.BOARD_SIZE) {
                return Outcome.X_WON;
            }
            if (rowsSums[y] == -Game.BOARD_SIZE) {
                return Outcome.O_WON;
            }
        }
        if (firstDiagSum == Game.BOARD_SIZE) {
            return Outcome.X_WON;
        }
        if (firstDiagSum == -Game.BOARD_SIZE) {
            return Outcome.O_WON;
        }
        if (secondDiagSum == Game.BOARD_SIZE) {
            return Outcome.X_WON;
        }
        if (secondDiagSum == -Game.BOARD_SIZE) {
            return Outcome.O_WON;
        }
        if (totalCount == (Game.BOARD_SIZE * Game.BOARD_SIZE)) {
            return Outcome.DRAW;
        }
        return Outcome.UNDETERMINED;
    }

    public Outcome getGameOutcome(){
        if(invalidateOutcome) {
            outcome = recomputeGameOutcome();
            invalidateOutcome = false;
        }
        return outcome;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int y = 0; y < Game.BOARD_SIZE; y++){
            for (int x = 0; x < Game.BOARD_SIZE; x++){
                buffer.append(this.getCell(x, y).toString());
            }
            if (y < Game.BOARD_SIZE - 1) buffer.append('|');
        }
        buffer.append(String.format(" [P:%s, W:%s]", this.getCurrentSide(), this.getGameOutcome()));
        return buffer.toString();
    }

    @Override
    public int hashCode() {
        if(invalidateHash){
            int off = 0;
            int val = 0;
            hashValue = 0;
            for (int x = 0; x < Game.BOARD_SIZE; x++) {
                for (int y = 0; y < Game.BOARD_SIZE; y++) {
                    Side side = getCell(x, y);
                    if (side == Side.X) {
                        val = 1;
                    }
                    else if (side == Side.O) {
                        val = 2;
                    } else {
                        val = 0;
                    }
                    hashValue |= val << off; // set the corresponding 2 bits
                    off += 2; // increment offset by 2 since we are using 2 bits per square
                }
            }
            invalidateHash = false;
        }
        return hashValue;
    }


    public static enum Side {
        NEUTRAL,
        X,
        O;

        private Side other;

        public int toValue(){
            switch (this){
                case X:
                    return 1;
                case O:
                    return -1;
                default:
                    return 0;
            }
        }

        public static Side fromString(String symbol){
            switch(symbol) {
                case "X":
                    return Side.X;
                case "O":
                    return Side.O;

                default:
                    return Side.NEUTRAL;
            }
        }

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

        public Side getOther() {
            switch (this){
                case X:
                    return O;
                case O:
                    return X;
            }
            return NEUTRAL;
        }
    }

    public static enum Outcome{
        UNDETERMINED,
        DRAW,
        X_WON,
        O_WON;

    }
}

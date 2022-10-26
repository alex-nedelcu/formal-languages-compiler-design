package program_internal_form;

public class Pair {

    private final String symbol;
    private final int position;

    public Pair(String symbol, int position) {
        this.symbol = symbol;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "symbol='" + symbol + '\'' +
                ", position=" + position +
                '}';
    }
}

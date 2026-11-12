import java.util.ArrayList;

/**
 * Holds the input values, target, and shared result state used by the worker threads.
 *
 * <p>The input values are stored in an {@link ArrayList} of {@link Integer}s. The
 * result array remains a {@code boolean[]} whose entries correspond to input positions.</p>
 */
public class SharedData {
    /** Input values being checked. */
    private ArrayList<Integer> array;
    /** Input positions selected by a successful solution, or {@code null} until one is found. */
    private boolean[] winArray;
    /** Whether any worker has found a solution. */
    private boolean flag;
    /** Target sum to check. */
    private final int b;

    /**
     * Creates shared state for the supplied values and target.
     *
     * @param array values to check, in their original order
     * @param b target sum
     */
    public SharedData(ArrayList<Integer> array, int b) {
        this.array = array;
        this.b = b;
    }

    /**
     * Returns the selection markers for a successful solution.
     *
     * @return a {@code boolean[]} indexed like the input values, or {@code null} if
     *         no worker has published a solution
     */
    public boolean[] getWinArray() { return winArray; }

    /**
     * Publishes the selection markers for a successful solution.
     *
     * @param winArray selection markers indexed like the input values
     */
    public void setWinArray(boolean[] winArray) { this.winArray = winArray; }

    /**
     * Returns the input values.
     *
     * @return the mutable {@code ArrayList<Integer>} supplied to the constructor
     */
    public ArrayList<Integer> getArray() { return array; }

    /**
     * Returns the target sum.
     *
     * @return the target sum
     */
    public int getB() { return b; }

    /**
     * Reports whether a worker has found a solution.
     *
     * @return {@code true} if a solution has been found; {@code false} otherwise
     */
    public boolean getFlag() { return flag; }

    /**
     * Updates whether a worker has found a solution.
     *
     * @param flag {@code true} when a solution has been found
     */
    public void setFlag(boolean flag) { this.flag = flag; }
}

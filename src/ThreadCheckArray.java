import java.util.ArrayList;

/**
 * Searches a shared list of integers for a subset whose sum equals the target.
 *
 * <p>Two instances are run concurrently, each exploring a different choice for
 * the final input position. The result markers are stored in a {@code boolean[]}
 * with one entry per input value.</p>
 */
public class ThreadCheckArray implements Runnable {
    /** Whether this worker has found a solution. */
    private boolean flag;
    /** Selection markers for this worker's solution. */
    private boolean[] winArray;
    /** State shared by both workers. */
    SharedData sd;
    /** Input values to search. */
    ArrayList<Integer> array;
    /** Target sum to search for. */
    int b;

    /**
     * Creates a worker using the input and target in the shared state.
     *
     * @param sd shared input and result state
     */
    public ThreadCheckArray(SharedData sd) {
        this.sd = sd;
        synchronized (sd) {
            array = sd.getArray();
            b = sd.getB();
        }
        winArray = new boolean[array.size()];
    }

    /**
     * Recursively searches the first {@code n} values for a subset totaling {@code b}.
     *
     * @param n number of values from the beginning of the list to consider
     * @param b remaining target sum
     */
    void rec(int n, int b) {
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        if (n == 1) {
            if(b == 0 || b == array.get(n-1)) {
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
            if (b == array.get(n-1)) winArray[n-1] = true;
            return;
        }

        rec(n-1, b - array.get(n-1));
        if (flag) winArray[n-1] = true;
        synchronized (sd) {
            if (sd.getFlag()) return;
        }
        rec(n-1, b);
    }

    /**
     * Runs this worker's portion of the search and publishes its result if found.
     */
    public void run() {
        if (array.size() != 1)
            if (Thread.currentThread().getName().equals("thread1"))
                rec(array.size()-1, b - array.get(array.size() - 1));
            else
                rec(array.size()-1, b);
        if (array.size() == 1)
            if (b == array.get(0) && !flag) {
                winArray[0] = true;
                flag = true;
                synchronized (sd) { sd.setFlag(true); }
            }
        if (flag) {
            if (Thread.currentThread().getName().equals("thread1"))
                winArray[array.size() - 1] = true;
            synchronized (sd) { sd.setWinArray(winArray); }
        }
    }
}

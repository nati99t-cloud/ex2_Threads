
import java.util.ArrayList;

/**
 * Runnable that checks combinations of array values against a target value
 * using recursive processing across threads.
 */
public class ThreadCheckArray implements Runnable {
    /** Indicates whether a valid combination has been found. */
    private boolean flag;

    /** Indicates which array positions belong to a valid combination. */
    private boolean[] winArray;

    /** Shared data used for synchronization and result storage. */
    SharedData sd;

    /** Values checked by the recursive algorithm, stored as {@code ArrayList<Integer>}. */
    ArrayList<Integer> array;

    /** Target value used by the recursive algorithm. */
    int b;

    /**
     * Creates a thread task using the supplied shared data.
     *
     * @param sd shared data containing the {@code ArrayList<Integer>} and the target value
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
package benchmark;

import mpi.*;
import java.util.Arrays;
import java.util.Random;

public class StencilCommunication {
    public int rank;
    public int nprocess;
    private static int num_neigh = 4;
    private int ndims;
    private boolean reorder = true;
    private int[] dims, neigh_def = new int[num_neigh];
    private int upside_rank, downside_rank, right_rank, left_rank;
    private boolean[] periods;
    private static final long SEED = 10101010;
    private double value;
    private double[] sendbuf = new double[1];
    private double[] recvbuf = new double[1];
    private Timer T = new Timer("Stencil Benchmark");
    Cartcomm comm_cart;
    CartParms comm_parms;

    Random R = new Random(SEED);

    public StencilCommunication(int rank, int nprocess) {
        this.rank = rank;
        this.nprocess = nprocess;
        this.ndims = 2;
        dims = new int[ndims];
        periods = new boolean[ndims];
        Arrays.fill(periods, true);
        value = R.nextDouble() * 1e-6;
    }

    public void setUpBenchmark() throws MPIException{
        int i, j;

        Cartcomm.Dims_create(nprocess, dims);
        comm_cart = MPI.COMM_WORLD.Create_cart(dims, periods, reorder);
        comm_parms = comm_cart.Get();
        i = comm_parms.coords[0];
        j = comm_parms.coords[1];
        neigh_def[0] = i-1;
        neigh_def[1] = j;
        left_rank = comm_cart.Rank(neigh_def);
        neigh_def[0] = i+1;
        neigh_def[1] = j;
        right_rank = comm_cart.Rank(neigh_def);
        neigh_def[0] = i;
        neigh_def[1] = j - 1;
        downside_rank = comm_cart.Rank(neigh_def);
        neigh_def[0] = i;
        neigh_def[1] = j + 1;
        upside_rank = comm_cart.Rank(neigh_def);
        if (rank == 0) {
            System.out.println("Process topology -> " + dims[0] + " x " + dims[1]);
        }
        System.out.println(rank + " rank: My coords x " + i + ", y " + j);
        //System.out.println(rank + " rank: My left rank is " + left_rank);
        //System.out.println(rank + " rank: My right rank is " + right_rank);
        //System.out.println(rank + " rank: My downside rank is " + downside_rank);
        //System.out.println(rank + " rank: My upside rank is " + upside_rank);
    }

    public void tearDownBenchmark() {

    }

    public void sendAndRecv(int send_rank, int recv_rank) throws MPIException{
        sendbuf[0] = value;

        //System.out.println("Rank " + rank + " send " + sendbuf[0] + " to " + send_rank);
        MPI.COMM_WORLD.Sendrecv(sendbuf, 0, 1, MPI.DOUBLE, send_rank, 1, recvbuf, 0, 1, MPI.DOUBLE, recv_rank, 1);
        //System.out.println("Rank " + rank + " receive " + recvbuf[0] + " from " + recv_rank);

        value = R.nextDouble() * 1e-6;
    }

    public void kernel() throws MPIException{
        // send data to left and recv data from right
        sendAndRecv(left_rank, right_rank);
        // send data to right and recv data from left
        sendAndRecv(right_rank, left_rank);
        // send data to downside and recv data from upside
        sendAndRecv(downside_rank, upside_rank);
        // send data to upside and recv data from downside
        sendAndRecv(upside_rank, downside_rank);
        MPI.COMM_WORLD.Barrier();
    }

    public void run() throws MPIException{
        setUpBenchmark();
        final int COUNT = 1000000;
        if (rank == 0) {
            T.start();
        }
        for (int i = 0; i < COUNT; i++) {
            kernel();
        }
        if (rank == 0) {
            T.stop();
            T.printperf();
        }
        tearDownBenchmark();
    }
}

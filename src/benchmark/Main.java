package benchmark;

import mpi.*;

public class Main {
    public static void main(String[] args) throws MPIException{
        int loop_count;
        try {
            loop_count = new Integer(args[0]);
        } catch (Exception e){
            loop_count = 1000000;
        }
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int nprocess = MPI.COMM_WORLD.Size();

        StencilCommunication sc = new StencilCommunication(rank, nprocess, loop_count);
        sc.run();

        MPI.Finalize();
    }
}

package benchmark;

import mpi.*;

public class Main {
    public static void main(String[] args) throws MPIException{
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int nprocess = MPI.COMM_WORLD.Size();

        StencilCommunication sc = new StencilCommunication(rank, nprocess);
        sc.run();

        MPI.Finalize();
    }
}

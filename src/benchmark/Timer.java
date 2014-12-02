package benchmark;

public class Timer {
    public String name;
    public double time;
    public long calls;

    private long start_time;
    private boolean on;

    public Timer(String name){
        this.name = name;
        reset();
    }

    public void start(){
        if (on) System.out.println("Warning timer " + name + " was already turned on");
        on = true;
        start_time = System.currentTimeMillis();
    }

    public void stop(){
        time += (double) (System.currentTimeMillis()-start_time) / 1000.;
        if (!on) System.out.println("Warning timer " + name + " wasn't turned on");
        calls++;
        on = false;
    }

    public void reset(){
        time = 0.0;
        calls = 0;
        on = false;
    }

    public void printperf(){

        String name;
        name = this.name;

        System.out.println(name + "\t TIME: " + time);
    }
}

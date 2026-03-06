package simscli.jobs;

public final class JoblessJob implements Job {
    @Override
    public String name() {
        return "Jobless";
    }

    @Override
    public double salary(int level) {
        return 0;
    }

    @Override
    public boolean canWork() {
        return false;
    }
}
package simscli.jobs;

public final class DoctorJob implements Job {
    @Override public String name() { return "Doctor"; }
    @Override public double salary(int level) { return 120 + level * 35; }
    @Override public boolean canWork() { return true; }
}
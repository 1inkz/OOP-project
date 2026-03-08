package simscli.jobs;

public final class EngineerJob implements Job {
    @Override public String name() { return "Engineer"; }
    @Override public double salary(int level) { return 110 + level * 30; }
    @Override public boolean canWork() { return true; }
    
    @Override public String getWorkLocation() { return "bank"; }
}
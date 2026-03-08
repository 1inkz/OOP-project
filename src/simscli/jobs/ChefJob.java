package simscli.jobs;

public final class ChefJob implements Job {
    @Override public String name() { return "Chef"; }
    @Override public double salary(int level) { return 90 + level * 22; }
    @Override public boolean canWork() { return true; }
    
    @Override public String getWorkLocation() { return "restaurant"; }
}
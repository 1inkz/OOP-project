package simscli.jobs;

public final class InfluencerJob implements Job {
    @Override public String name() { return "Influencer"; }
    @Override public double salary(int level) { return 70 + level * 18; }
    @Override public boolean canWork() { return true; }
    
    @Override public String getWorkLocation() { return "Park"; }
}

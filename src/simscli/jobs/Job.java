package simscli.jobs;

public interface Job {
    String name();
    double salary(int level);
    boolean canWork();
    
    String getWorkLocation();
}
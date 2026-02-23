package action;

import java.util.ArrayList;
import java.util.List;

public class Outcome {
    public NeedDelta needDelta = new NeedDelta();
    public int moneyDelta = 0;
    public final List<String> messages = new ArrayList<>();

    public Outcome msg(String m){
        messages.add(m);
        return this;
    }

    public Outcome money(int delta){
        moneyDelta += delta;
        return this;
    }

    public Outcome needs(NeedDelta d){
        needDelta = d;
        return this;
    }
}

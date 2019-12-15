package com.example.liubq.experimenttwo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MessageEvent { /* Additional fields if needed */
    public String name;
    public String bgcolor;
    public String kind;
    public String contain;
    public String circle;
    MessageEvent(String _name, String _bgcolor, String _kind, String _contain, String _circle){
        name = _name;
        bgcolor = _bgcolor;
        kind = _kind;
        contain = _contain;
        circle = _circle;
    }
}

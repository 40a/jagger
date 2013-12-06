package com.griddynamics.jagger.webclient.client.components.control.model;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/27/13
 */
public class SessionScopePlotsNode extends SimpleNode {

    List<SessionPlotNode> plots;

    public SessionScopePlotsNode() {
    }

    public SessionScopePlotsNode(String id, String displayName) {
        super(id, displayName);
    }

    public List<SessionPlotNode> getPlots() {
        return plots;
    }

    public void setPlots(List<SessionPlotNode> plots) {
        this.plots = plots;
    }

    @Override
    public List<? extends SimpleNode> getChildren() {
        return plots == null ? Collections.EMPTY_LIST : plots;
    }
}

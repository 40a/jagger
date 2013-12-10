package com.griddynamics.jagger.webclient.client.components.control.model;

import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;

import java.util.Collections;
import java.util.List;

/**
 * User: amikryukov
 * Date: 12/4/13
 */
public class SessionPlotNode extends AbstractIdentifyNode {
    private PlotNameDto plotNameDto;

    public SessionPlotNode (PlotNameDto metricName) {
        this.plotNameDto = metricName;
    }

    public SessionPlotNode() {}

    public PlotNameDto getPlotNameDto() {
        return plotNameDto;
    }

    public void setPlotNameDto(PlotNameDto plotNameDto) {
        this.plotNameDto = plotNameDto;
    }

    @Override
    public List<? extends AbstractIdentifyNode> getChildren() {
        return Collections.EMPTY_LIST;
    }
}

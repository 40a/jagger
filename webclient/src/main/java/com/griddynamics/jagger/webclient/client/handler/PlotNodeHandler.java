package com.griddynamics.jagger.webclient.client.handler;

import com.griddynamics.jagger.webclient.client.components.control.model.PlotNode;
import com.griddynamics.jagger.webclient.client.dto.PlotNameDto;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 12/2/13
 */
public class PlotNodeHandler extends TreeAwareHandler<PlotNode> {
    @Override
    public void onCheckChange(CheckChangeEvent<PlotNode> event) {

        PlotNameDto plotName = event.getItem().getPlotNameDto();
        if (Tree.CheckState.CHECKED.equals(event.getChecked())) {
            testPlotFetcher.fetchPlot(plotName, true);
        } else {
            testPlotFetcher.removePlot(plotName);
        }
    }
}

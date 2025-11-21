package net.gausman.ftl.view.charts;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.FlowEntity;
import org.jfree.chart.entity.NodeEntity;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.flow.FlowPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.data.flow.FlowDataset;
import org.jfree.data.flow.FlowDatasetUtils;
import org.jfree.data.flow.FlowKey;
import org.jfree.data.flow.NodeKey;


import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GausmanFlowPlot<K extends Comparable<K>> extends FlowPlot {
    public GausmanFlowPlot(FlowDataset dataset) {
        super(dataset);
        setNodeWidth(100.0);
    }

    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        Args.nullNotPermitted(g2, "g2");
        Args.nullNotPermitted(area, "area");

        EntityCollection entities = null;
        if (info != null) {
            info.setPlotArea(area);
            entities = info.getOwner().getEntityCollection();
        }
        RectangleInsets insets = getInsets();
        insets.trim(area);
        if (info != null) {
            info.setDataArea(area);
        }

        // use default JFreeChart background handling
        drawBackground(g2, area);

        // we need to ensure there is space to show all the inflows and all 
        // the outflows at each node group, so first we calculate the max
        // flow space required - for each node in the group, consider the 
        // maximum of the inflow and the outflow
        double flow2d = Double.POSITIVE_INFINITY;
        double nodeMargin2d = this.getNodeMargin() * area.getHeight();
        int stageCount = this.getDataset().getStageCount();
        for (int stage = 0; stage < this.getDataset().getStageCount(); stage++) {
            List<K> sources = this.getDataset().getSources(stage);
            int nodeCount = sources.size();
            double flowTotal = 0.0;
            for (K source : sources) {
                double inflow = FlowDatasetUtils.calculateInflow(this.getDataset(), source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(this.getDataset(), source, stage);
                flowTotal = flowTotal + Math.max(inflow, outflow);
            }
            if (flowTotal > 0.0) {
                double availableH = area.getHeight() - (nodeCount - 1) * nodeMargin2d;
                flow2d = Math.min(availableH / flowTotal, flow2d);
            }

            if (stage == this.getDataset().getStageCount() - 1) {
                // check inflows to the final destination nodes...
                List<K> destinations = this.getDataset().getDestinations(stage);
                int destinationCount = destinations.size();
                flowTotal = 0.0;
                for (K destination : destinations) {
                    double inflow = FlowDatasetUtils.calculateInflow(this.getDataset(), destination, stage + 1);
                    flowTotal = flowTotal + inflow;
                }
                if (flowTotal > 0.0) {
                    double availableH = area.getHeight() - (destinationCount - 1) * nodeMargin2d;
                    flow2d = Math.min(availableH / flowTotal, flow2d);
                }
            }
        }

        double stageWidth = (area.getWidth() - ((stageCount + 1) * this.getNodeWidth())) / stageCount;
        double flowOffset = area.getWidth() * this.getFlowMargin();

        Map<NodeKey<K>, Rectangle2D> nodeRects = new HashMap<>();
        boolean hasNodeSelections = FlowDatasetUtils.hasNodeSelections(this.getDataset());
        boolean hasFlowSelections = FlowDatasetUtils.hasFlowSelections(this.getDataset());

        // iterate over all the stages, we can render the source node rects and
        // the flows ... we should add the destination node rects last, then
        // in a final pass add the labels
        for (int stage = 0; stage < this.getDataset().getStageCount(); stage++) {

            double stageLeft = area.getX() + (stage + 1) * this.getNodeWidth() + (stage * stageWidth);
            double stageRight = stageLeft + stageWidth;

            // calculate the source node and flow rectangles
            Map<FlowKey<K>, Rectangle2D> sourceFlowRects = new HashMap<>();
            double nodeY = area.getY();
            for (Object raw : this.getDataset().getSources(stage)) {
                K source = (K) raw;
                double inflow = FlowDatasetUtils.calculateInflow(this.getDataset(), source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(this.getDataset(), source, stage);
                double nodeHeight = (Math.max(inflow, outflow) * flow2d);
                Rectangle2D nodeRect = new Rectangle2D.Double(stageLeft - this.getNodeWidth(), nodeY, this.getNodeWidth(), nodeHeight);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(stage, source), nodeRect, source.toString()));
                }
                nodeRects.put(new NodeKey<>(stage, source), nodeRect);
                double y = nodeY;
                for (Object raw2 : this.getDataset().getDestinations(stage)) {
                    K destination = (K) raw2;
                    Number flow = this.getDataset().getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageLeft - this.getNodeWidth(), y, this.getNodeWidth(), height);
                        sourceFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                        y = y + height;
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }

            // calculate the destination rectangles
            Map<FlowKey<K>, Rectangle2D> destFlowRects = new HashMap<>();
            nodeY = area.getY();
            for (Object raw : this.getDataset().getDestinations(stage)) {
                K destination = (K) raw;
                double inflow = FlowDatasetUtils.calculateInflow(this.getDataset(), destination, stage + 1);
                double outflow = FlowDatasetUtils.calculateOutflow(this.getDataset(), destination, stage + 1);
                double nodeHeight = Math.max(inflow, outflow) * flow2d;
                nodeRects.put(new NodeKey<>(stage + 1, destination), new Rectangle2D.Double(stageRight, nodeY, this.getNodeWidth(), nodeHeight));
                double y = nodeY;
                for (Object raw2 : this.getDataset().getSources(stage)) {
                    K source = (K) raw2;
                    Number flow = this.getDataset().getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageRight, y, this.getNodeWidth(), height);
                        y = y + height;
                        destFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }

            for (Object raw : this.getDataset().getSources(stage)) {
                K source = (K) raw;
                NodeKey<K> nodeKey = new NodeKey<>(stage, source);
                Rectangle2D nodeRect = nodeRects.get(nodeKey);
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(this.getDataset().getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);

                for (Object raw2 : this.getDataset().getDestinations(stage)) {
                    K destination = (K) raw2;
                    FlowKey<K> flowKey = new FlowKey<>(stage, source, destination);
                    Rectangle2D sourceRect = sourceFlowRects.get(flowKey);
                    if (sourceRect == null) {
                        continue;
                    }
                    Rectangle2D destRect = destFlowRects.get(flowKey);

                    Path2D connect = new Path2D.Double();
                    connect.moveTo(sourceRect.getMaxX() + flowOffset, sourceRect.getMinY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, sourceRect.getMinY(), stageLeft + stageWidth / 2.0, destRect.getMinY(), destRect.getX() - flowOffset, destRect.getMinY());
                    connect.lineTo(destRect.getX() - flowOffset, destRect.getMaxY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, destRect.getMaxY(), stageLeft + stageWidth / 2.0, sourceRect.getMaxY(), sourceRect.getMaxX() + flowOffset, sourceRect.getMaxY());
                    connect.closePath();
                    Color nc = lookupNodeColor(nodeKey);
                    if (hasFlowSelections) {
                        if (!Boolean.TRUE.equals(this.getDataset().getFlowProperty(flowKey, FlowKey.SELECTED_PROPERTY_KEY))) {
                            int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                            nc = new Color(g, g, g, ncol.getAlpha());
                        }
                    }

                    GradientPaint gp = new GradientPaint((float) sourceRect.getMaxX(), 0, nc, (float) destRect.getMinX(), 0, new Color(nc.getRed(), nc.getGreen(), nc.getBlue(), 128));
                    Composite saved = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                    g2.setPaint(gp);
                    g2.fill(connect);
                    if (entities != null) {
                        String toolTip = null;
                        if (this.getToolTipGenerator() != null) {
                            toolTip = this.getToolTipGenerator().generateLabel(this.getDataset(), flowKey);
                        }
                        entities.add(new FlowEntity(flowKey, connect, toolTip, ""));
                    }
                    g2.setComposite(saved);
                }

            }
        }

        // now draw the destination nodes
        int lastStage = this.getDataset().getStageCount() - 1;
        for (Object raw : this.getDataset().getDestinations(lastStage)) {
            K destination = (K) raw;
            NodeKey<K> nodeKey = new NodeKey<>(lastStage + 1, destination);
            Rectangle2D nodeRect = nodeRects.get(nodeKey);
            if (nodeRect != null) {
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(this.getDataset().getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(lastStage + 1, destination), nodeRect, destination.toString()));
                }
            }
        }

        // Custom logic for labels
        g2.setFont(this.getDefaultNodeLabelFont());
        g2.setPaint(this.getDefaultNodeLabelPaint());

        for (NodeKey<K> key : nodeRects.keySet()){
            Rectangle2D r = nodeRects.get(key);
            double inflow = FlowDatasetUtils.calculateInflow(this.getDataset(), key.getNode().toString(), key.getStage());
            double outflow = FlowDatasetUtils.calculateOutflow(this.getDataset(), key.getNode().toString(), key.getStage());
            double maxFlow = Math.max(inflow, outflow);

            String displayString = String.format("%s:%s", key.getNode().toString(), (int) maxFlow);
            TextUtils.drawAlignedString(displayString, g2,
                    (float) r.getCenterX(), (float) (r.getCenterY()), TextAnchor.CENTER);

//            TextUtils.drawAlignedString(key.getNode().toString(), g2,
//                    (float) r.getCenterX(), (float) (r.getCenterY() - 7.5), TextAnchor.CENTER);
//            TextUtils.drawAlignedString(Integer.toString((int) maxFlow), g2,
//                    (float) r.getCenterX(), (float) (r.getCenterY() + 7.5), TextAnchor.CENTER);
        }

//        // now draw all the labels over top of everything else
//        g2.setFont(this.getDefaultNodeLabelFont());
//        g2.setPaint(this.getDefaultNodeLabelPaint());
//        for (NodeKey<K> key : nodeRects.keySet()) {
//            Rectangle2D r = nodeRects.get(key);
//            if (key.getStage() < this.getDataset().getStageCount()) {
//                TextUtils.drawAlignedString(key.getNode().toString(), g2,
//                        (float) (r.getMaxX() + flowOffset + this.getNodeLabelOffsetX()),
//                        (float) labelY(r), TextAnchor.CENTER_LEFT);
//            } else {
//                TextUtils.drawAlignedString(key.getNode().toString(), g2,
//                        (float) (r.getX() - flowOffset - this.getNodeLabelOffsetX()),
//                        (float) labelY(r), TextAnchor.CENTER_RIGHT);
//            }
//        }
    }
}

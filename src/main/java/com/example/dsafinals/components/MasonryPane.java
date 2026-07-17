package com.example.dsafinals.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

public class MasonryPane extends Pane {

    private final DoubleProperty columnWidth = new SimpleDoubleProperty(150) {
        @Override
        protected void invalidated() { requestLayout(); }
    };

    private final DoubleProperty hgap = new SimpleDoubleProperty(12) {
        @Override
        protected void invalidated() { requestLayout(); }
    };

    private final DoubleProperty vgap = new SimpleDoubleProperty(12) {
        @Override
        protected void invalidated() { requestLayout(); }
    };

    public double getColumnWidth() { return columnWidth.get(); }
    public void setColumnWidth(double v) { columnWidth.set(v); }
    public DoubleProperty columnWidthProperty() { return columnWidth; }

    public double getHgap() { return hgap.get(); }
    public void setHgap(double v) { hgap.set(v); }
    public DoubleProperty hgapProperty() { return hgap; }

    public double getVgap() { return vgap.get(); }
    public void setVgap(double v) { vgap.set(v); }
    public DoubleProperty vgapProperty() { return vgap; }

    @Override
    protected double computePrefWidth(double height) {
        return Double.MAX_VALUE;
    }

    private double[] layoutMetrics() {
        Insets insets = getInsets();
        double availWidth = Math.max(0, getWidth() - insets.getLeft() - insets.getRight());
        double hg = hgap.get();
        double vg = vgap.get();
        double minW = columnWidth.get();
        int cols = Math.max(1, (int) ((availWidth + hg) / (minW + hg)));
        double actualW = cols > 0 ? (availWidth - (cols - 1) * hg) / cols : minW;
        return new double[]{actualW, hg, vg, cols, insets.getLeft(), insets.getTop()};
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets insets = getInsets();
        double availWidth = (width == -1 ? getWidth() : width) - insets.getLeft() - insets.getRight();
        if (availWidth <= 0) return 0;

        double minW = columnWidth.get();
        double hg = hgap.get();
        double vg = vgap.get();
        int cols = Math.max(1, (int) ((availWidth + hg) / (minW + hg)));
        double actualW = cols > 0 ? (availWidth - (cols - 1) * hg) / cols : minW;

        List<Node> managed = getManagedChildren();
        if (managed.isEmpty()) return 0;

        double[] colHeights = new double[cols];

        for (Node child : managed) {
            int col = shortestColumn(colHeights);
            double childHeight = child.prefHeight(actualW);
            colHeights[col] += childHeight + vg;
        }

        double max = 0;
        for (double h : colHeights) {
            if (h > max) max = h;
        }
        return max - vg + insets.getTop() + insets.getBottom();
    }

    @Override
    protected void layoutChildren() {
        double[] m = layoutMetrics();
        double actualW = m[0], hg = m[1], vg = m[2];
        int cols = (int) m[3];
        double left = m[4], top = m[5];
        if (cols <= 0) return;

        List<Node> managed = getManagedChildren();
        double[] colHeights = new double[cols];

        for (Node child : managed) {
            int col = shortestColumn(colHeights);
            double x = left + col * (actualW + hg);
            double y = top + colHeights[col];
            double childHeight = child.prefHeight(actualW);
            child.resizeRelocate(x, y, actualW, childHeight);
            colHeights[col] = y + childHeight + vg;
        }
    }

    private int shortestColumn(double[] heights) {
        int col = 0;
        for (int i = 1; i < heights.length; i++) {
            if (heights[i] < heights[col]) col = i;
        }
        return col;
    }
}

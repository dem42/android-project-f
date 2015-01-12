package com.premature.floscript.scripts.ui;

import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by martin on 11/01/15.
 * </p>
 * This class represents a common base class for all objects to which arrows can connect
 */
public abstract class ArrowTargetableDiagramElement<SELF_TYPE extends DiagramElement<SELF_TYPE>> extends DiagramElement<SELF_TYPE> {

    private static final String TAG = "ARROW_TARGET";
    /**
     * This map stores information about where arrows are anchored on this element
     * thread safe might be executed from different
     * {@link com.premature.floscript.scripts.ui.DiagramEditorView.ElementMover} runnables
     */
    private final ConcurrentHashMap<ArrowUiElement, ArrowAnchorPoint> mArrowToAnchor;

    protected ArrowTargetableDiagramElement(float xPos, float yPos, int width, int height) {
        super(xPos, yPos, width, height);
        mArrowToAnchor = new ConcurrentHashMap<>();
    }

    public abstract Iterable<ArrowAnchorPoint> getAnchorPoints();

    /**
     * Connects an arrow to this diagram element
     * @param arrow
     * @param arrowXPosDp
     * @param arrowYPosDp
     * @return returns the location where the arrow was anchored
     */
    public ArrowAnchorPoint connectArrow(ArrowUiElement arrow, int arrowXPosDp, int arrowYPosDp) {
        ArrowAnchorPoint nearest = getNearestAnchorPoint(arrowXPosDp, arrowYPosDp);
        ArrowAnchorPoint existingAnchor = null;//mArrowToAnchor.putIfAbsent(arrow, nearest);
        return existingAnchor != null ? existingAnchor : nearest;
    }

    protected ArrowAnchorPoint getNearestAnchorPoint(int arrowXPosDp, int arrowYPosDp) {
        ArrowAnchorPoint closest = null;
        int minManhDist = Integer.MAX_VALUE;
        for (ArrowAnchorPoint anchorPoint : getAnchorPoints()) {
            int manhDist = Math.abs(anchorPoint.getXPosDip() - arrowXPosDp) + Math.abs(anchorPoint.getYPosDip() - arrowYPosDp);
            if (manhDist < minManhDist) {
                Log.d(TAG, "Anchor Point " + anchorPoint + " is closer to (" + arrowXPosDp + ", " + arrowYPosDp + ") with " + manhDist + " than " + closest);
                minManhDist = manhDist;
                closest = anchorPoint;
            }
        }
        return closest;
    }

    public ArrowAnchorPoint getAnchorFor(ArrowUiElement arrowUiElement) {
        return mArrowToAnchor.get(arrowUiElement);
    }

    /**
     * This class represents a point on the diagram element where an arrow can
     * be connected
     */
    public static final class ArrowAnchorPoint {
        private final ArrowTargetableDiagramElement<?> mOwner;
        private final int mXPosDip, mYPosDip;

        public ArrowAnchorPoint(int mXPosDip, int mYPosDip, ArrowTargetableDiagramElement<?> owner) {
            this.mXPosDip = mXPosDip;
            this.mYPosDip = mYPosDip;
            this.mOwner = owner;
        }

        public int getXPosDip() {
            return (int)(mXPosDip + mOwner.getXPos());
        }
        public int getYPosDip() {
            return (int)(mYPosDip + mOwner.getYPos());
        }

        @Override
        public String toString() {
            return "ArrowAnchorPoint{" +
                    "mXPosDip=" + getXPosDip() +
                    ", mYPosDip=" + getYPosDip() +
                    ", objCoordX=" + mXPosDip +
                    ", objCoordY=" + mYPosDip +
                    '}';
        }
    }
}

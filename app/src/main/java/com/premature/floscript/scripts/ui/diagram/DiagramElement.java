package com.premature.floscript.scripts.ui.diagram;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.premature.floscript.util.FloDrawableUtils;

/**
 * Created by martin on 05/01/15.
 * <p/>
 * A drawable floscript diagram element which has a position and size. It presents a fluent api for
 * moving the drawable around the canvas with the methods {@link DiagramElement#moveTo(float, float) moveTo}
 * and {@link DiagramElement#advanceBy(float, float) advanceBy}
 */
public abstract class DiagramElement {

    private final Diagram mDiagram;
    protected float mXPos;
    protected float mYPos;
    protected int mWidth;
    protected int mHeight;
    protected boolean mPinned = false;

    protected DiagramElement(Diagram diagram, float xPos, float yPos, int width, int height) {
        this.mDiagram = diagram;
        this.mXPos = xPos;
        this.mYPos = yPos;
        this.mWidth = width;
        this.mHeight = height;
    }

    public void moveTo(float xPos, float yPos) {
        if (!mPinned) {
            this.mXPos = xPos;
            this.mYPos = yPos;
        }
    }

    public void moveCenterTo(float xPos, float yPos) {
        if (!mPinned) {
            this.mXPos = xPos - mWidth / 2;
            this.mYPos = yPos - mHeight / 2;
        }
    }

    public void advanceBy(float xStep, float yStep) {
        if (!mPinned) {
            this.mXPos += xStep;
            this.mYPos += yStep;
        }
    }

    public ContainsResult contains(int xPosDps, int yPosDps) {
        if (mXPos <= xPosDps && xPosDps <= mXPos + mWidth
                && mYPos <= yPosDps && yPosDps <= mYPos + mHeight) {
            return new ContainsResult((float) FloDrawableUtils.distance(mXPos, mYPos, xPosDps, yPosDps));
        }
        return NOT_CONTAINED;
    }

    public float getXPos() {
        return mXPos;
    }

    public float getYPos() {
        return mYPos;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * A mPinned diagram element cannot be moved until its unpinned using {@link #setPinned(boolean)}
     *
     * @return <code>true</code> if the element is mPinned to the background
     */
    public boolean isPinned() {
        return mPinned;
    }

    /**
     * Change the mPinned state of the element
     *
     * @param pinned the new mPinned state of the element
     */
    public void setPinned(boolean pinned) {
        this.mPinned = pinned;
    }

    public abstract void draw(Canvas canvas, int xOffset, int yOffset);

    public abstract Drawable getDrawable();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "mXPos=" + mXPos +
                ", mYPos=" + mYPos +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mPinned=" + mPinned +
                '}';
    }

    public abstract boolean isShowingPopupButton(DiagramEditorPopupButtonType buttonType);

    public static final ContainsResult NOT_CONTAINED = new ContainsResult(Float.MAX_VALUE);

    public static final class ContainsResult implements Comparable<ContainsResult> {
        public final float distance;

        public ContainsResult(float distance) {
            this.distance = distance;
        }

        /**
         * This method compares the contains results and returns the one for the element that has a smaller
         * distance.
         */
        @Override
        public int compareTo(ContainsResult another) {
            return Float.compare(distance, another.distance);
        }
    }
}

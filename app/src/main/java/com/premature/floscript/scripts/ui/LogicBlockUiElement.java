package com.premature.floscript.scripts.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by martin on 06/01/15.
 * <p/>
 * This class encapsulates the view logic of a flowscript logic-block. A logic block
 * contains some code logic that will get executed should the control flow reach this element.
 */
public class LogicBlockUiElement extends ArrowTargetableDiagramElement<LogicBlockUiElement> {

    private static final int WIDTH = 70;
    private static final int HEIGHT = 70;

    private final List<ArrowAnchorPoint> mAnchorPoints;

    private Path logicBlockPath;
    private PathShape logicBlockShape;
    private ShapeDrawable logicBlock;

    public LogicBlockUiElement() {
        super(0f, 0f, WIDTH, HEIGHT);
        ArrayList<ArrowAnchorPoint> list = new ArrayList<>();
        list.add(new ArrowAnchorPoint(0,0, this));
        list.add(new ArrowAnchorPoint(0,HEIGHT/2, this));
        list.add(new ArrowAnchorPoint(0,HEIGHT, this));
        list.add(new ArrowAnchorPoint(WIDTH/2,0, this));
        list.add(new ArrowAnchorPoint(WIDTH,0, this));
        list.add(new ArrowAnchorPoint(WIDTH,HEIGHT/2, this));
        list.add(new ArrowAnchorPoint(WIDTH/2,HEIGHT, this));
        list.add(new ArrowAnchorPoint(WIDTH,HEIGHT, this));
        this.mAnchorPoints = Collections.unmodifiableList(list);

        initShape();
    }

    private void initShape() {
        logicBlockPath = new Path();
        logicBlockPath.moveTo(0f, 0f);
        logicBlockPath.lineTo(0f, 2f);
        logicBlockPath.lineTo(2f, 2f);
        logicBlockPath.lineTo(2f, .25f);
        logicBlockPath.lineTo(1.75f, .25f);
        logicBlockPath.lineTo(1.75f, 0f);
        logicBlockPath.lineTo(2f, .25f);
        logicBlockPath.lineTo(1.75f, 0f);
        logicBlockPath.close();
        logicBlockShape = new PathShape(logicBlockPath, 2f, 2f);
        logicBlock = new ShapeDrawable(logicBlockShape);
        logicBlock.getPaint().setAntiAlias(true);
        logicBlock.getPaint().setStyle(Paint.Style.STROKE);
        logicBlock.getPaint().setStrokeWidth(0.05f);
        logicBlock.getPaint().setColor(Color.GREEN);
        logicBlock.setBounds(0, 0, mWidth, mHeight);
    }

    @Override
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.translate(mXPos, mYPos);
        logicBlock.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public Iterable<ArrowAnchorPoint> getAnchorPoint() {
        return mAnchorPoints;
    }
}

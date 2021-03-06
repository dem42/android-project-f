package com.premature.floscript.scripts.ui.diagram;

import android.support.annotation.Nullable;

import com.premature.floscript.scripts.logic.Script;
import com.premature.floscript.util.DiagramUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by martin on 15/01/15.
 * <p/>
 * A diagram is a visual representation of a flowscript. Essentially it is a flowchart. It
 * contains references to the elements that this flowchart is composed of and can be turned into
 * a {@link com.premature.floscript.scripts.logic.Script} using the {@link com.premature.floscript.scripts.logic.DiagramToScriptCompiler}
 */
public final class Diagram {
    // there should be only one and it will also be inside the elements and connectables list
    private StartUiElement mEntryElement;
    private List<DiagramElement> elements = new ArrayList<>();
    private List<ArrowUiElement> arrows = new ArrayList<>();
    private List<ConnectableDiagramElement> connectables = new ArrayList<>();

    @Nullable
    private Script compiledDiagram; // the diagram may not have been compiled yet
    private String mName;
    @Nullable
    private String originalName;
    private String mDescription;
    private int version;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(@Nullable String originalName) {
        this.originalName = originalName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setEntryElement(StartUiElement startUiElement) {
        this.mEntryElement = startUiElement;
        elements.add(mEntryElement);
        connectables.add(mEntryElement);
    }

    public StartUiElement getEntryElement() {
        return mEntryElement;
    }

    public List<ConnectableDiagramElement> getConnectables() {
        return connectables;
    }

    public void addConnectable(ConnectableDiagramElement connectable) {
        connectables.add(connectable);
        elements.add(connectable);
    }

    public void addArrow(ArrowUiElement arrow) {
        DiagramUtils.fixArrowOverlapIfGoingBack(arrow, arrows);
        arrows.add(arrow);
        elements.add(arrow);
    }

    public List<DiagramElement> getElements() {
        return elements;
    }

    public List<ArrowUiElement> getArrows() {
        return arrows;
    }

    @Nullable
    public Script getCompiledDiagram() {
        return compiledDiagram;
    }

    public void setCompiledDiagram(@Nullable Script compiledDiagram) {
        this.compiledDiagram = compiledDiagram;
    }

    @Override
    public String toString() {
        return "Diagram{" +
                ", arrows=" + arrows +
                ", connectables=" + connectables +
                ", mName='" + mName + '\'' +
                ", version=" + version +
                '}';
    }

    public void removeArrow(ArrowUiElement arrowUiElement) {
        arrowUiElement.unanchor();
        arrows.remove(arrowUiElement);
        elements.remove(arrowUiElement);
    }

    public void remove(ConnectableDiagramElement touchedElement) {
        connectables.remove(touchedElement);
        elements.remove(touchedElement);
        for (ArrowUiElement arrow : touchedElement.getAnchoredArrows()) {
            removeArrow(arrow);
        }
    }
}

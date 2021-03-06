package com.premature.floscript.scripts.ui.diagram;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.premature.floscript.scripts.logic.ArrowCondition;
import com.premature.floscript.scripts.logic.CompilationErrorCode;
import com.premature.floscript.util.FloBus;
import com.premature.floscript.util.FloEvents;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by martin on 22/01/15.
 * <p/>
 * This class is responsible for encapsulating the logic required to validate a diagram. This
 * validation is required for the diagram editor IDE
 */
public final class DiagramValidator {

    private static final String TAG = "DIAG_VALIDTR";
    private final DiagramEditorView mEditorView;

    public DiagramValidator(DiagramEditorView editorView) {
        this.mEditorView = editorView;
    }

    public boolean validateArrowAddition(ConnectableDiagramElement startPoint, @Nullable ConnectableDiagramElement endPoint) {
        if (endPoint == null) {
            return checkAndNotify(!startPoint.hasAllArrowsConnected(), CompilationErrorCode.MAX_CHILDREN_REACHED);
        } else {
            if (checkAndNotify(!(endPoint instanceof StartUiElement), CompilationErrorCode.CANNOT_CONNECT_TO_ENTRY)) {
                return checkAndNotify(!hasAlwaysTrueLoop(startPoint, endPoint), CompilationErrorCode.HAS_ALWAYS_TRUE_LOOP);
            } else {
                return false;
            }
        }
    }

    public boolean allReachable() {
        Set<ConnectableDiagramElement> visited = new HashSet<>();
        searchReachable(mEditorView.getDiagram().getEntryElement(), visited, false);
        for (ConnectableDiagramElement connectable : mEditorView.getDiagram().getConnectables()) {
            if (!visited.contains(connectable)) {
                return checkAndNotify(false, CompilationErrorCode.NOT_ALL_DIAGRAM_ELEMENTS_ARE_REACHABLE);
            }
        }
        return true;
    }

    public boolean allHaveScripts() {
        for (ConnectableDiagramElement elem : mEditorView.getDiagram().getConnectables()) {
            if (elem.getScript() == null) {
                return checkAndNotify(false, CompilationErrorCode.UNSCRIPTED_ELEMENTS);
            }
        }
        return true;
    }


    public boolean allDiamondArrowsHaveLabels() {
        for (ConnectableDiagramElement elem : mEditorView.getDiagram().getConnectables()) {
            if (DiamondUiElement.TYPE_TOKEN.equals(elem.getTypeDesc())) {
                int cY = 0, cN = 0, cArr = 0;
                for (ArrowUiElement anchoredArrow : elem.getAnchoredArrows()) {
                    if (!DiamondUiElement.TYPE_TOKEN.equals(anchoredArrow.getStartPoint().getTypeDesc())) {
                        // we only care about arrows that leave this element
                        continue;
                    }
                    cArr++;
                    if (anchoredArrow.getCondition() == ArrowCondition.YES) cY++;
                    else if (anchoredArrow.getCondition() == ArrowCondition.NO) cN++;
                }
                if (cArr != 2 || cN != 1 || cY != 1) {
                    return checkAndNotify(false, CompilationErrorCode.DIAMOND_ARROW_NO_LABEL);
                }
            }
        }
        return true;
    }

    private boolean checkAndNotify(boolean result, CompilationErrorCode code) {
        if (!result) {
            FloBus.getInstance().post(new FloEvents.DiagramValidationEvent(code));
            return false;
        }
        return true;
    }

    private boolean hasAlwaysTrueLoop(ConnectableDiagramElement startPoint, ConnectableDiagramElement endPoint) {
        if (!(startPoint instanceof LogicBlockUiElement) || !(endPoint instanceof LogicBlockUiElement)) {
            return false;
        }
        Set<ConnectableDiagramElement> visited = new HashSet<>();
        searchReachable(endPoint, visited, true);
        return visited.contains(startPoint);
    }

    private void searchReachable(ConnectableDiagramElement startPoint,
                                 Set<ConnectableDiagramElement> visited, boolean onlyCheckLogicBlocks) {
        Log.d(TAG, "For startPoint " + startPoint + " visited are " + visited);
        visited.add(startPoint);
        for (Pair<ConnectableDiagramElement, ?> connected : startPoint.getConnectedElements()) {
            if (onlyCheckLogicBlocks && !(connected.first instanceof LogicBlockUiElement))
                continue;
            if (visited.contains(connected.first)) {
            } else {
                searchReachable(connected.first, visited, onlyCheckLogicBlocks);
            }
        }
    }
}

package com.premature.floscript.scripts.logic;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.premature.floscript.R;
import com.premature.floscript.scripts.ui.diagram.ArrowUiElement;
import com.premature.floscript.scripts.ui.diagram.ConnectableDiagramElement;
import com.premature.floscript.scripts.ui.diagram.Diagram;
import com.premature.floscript.scripts.ui.diagram.StartUiElement;
import com.premature.floscript.util.ResourceAndFileUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by martin on 15/01/15.
 * <p/>
 * This class turns a diagram representation of a floscript into its source code
 * representation
 */
public final class DiagramToScriptCompiler {
    private static final String TAG = "COMPILER";
    private String mCodeShell;

    public DiagramToScriptCompiler(Context ctx) {
        mCodeShell = ResourceAndFileUtils.readFile(ctx, R.raw.script_shell, true);
    }

    public Script compile(Diagram diagram) throws ScriptCompilationException {
        StringBuilder code = new StringBuilder("function runScript (env) {\n");

        Map<ConnectableDiagramElement, String> generatedFunNames = generateFunNames(diagram.getConnectables());

        StartUiElement entryElement = diagram.getEntryElement();
        if (entryElement == null) {
            throw new ScriptCompilationException(CompilationErrorCode.DIAGRAM_MUST_HAVE_ENTRY_ELEM);
        }

        List<Pair<ConnectableDiagramElement, ArrowUiElement>> connectedElements = entryElement.getConnectedElements();
        if (connectedElements.size() > 1) {
            Log.d(TAG, "connected to start are " + connectedElements);
            throw new ScriptCompilationException(CompilationErrorCode.ENTRY_MUST_HAVE_SINGLE_CHILD);
        }
        HashSet<ConnectableDiagramElement> visited = new HashSet<>();
        depthFirstCompile(entryElement, connectedElements, code, generatedFunNames, visited);

        code.append(mCodeShell).append("return function_stack.length == 0;\n}\n");
        return new Script(code.toString(), diagram.getName(), Script.Type.FUNCTION, diagram.getDescription());
    }

    private Map<ConnectableDiagramElement, String> generateFunNames(List<ConnectableDiagramElement> connectables) {
        Map<ConnectableDiagramElement, String> result = new HashMap<>();
        int counter = 0;
        String base = "function";
        for (ConnectableDiagramElement elem : connectables) {
            if (elem.getTypeDesc() == StartUiElement.TYPE_TOKEN) {
                result.put(elem, Scripts.ENTRY_POINT_SCRIPT.getName());
            } else {
                result.put(elem, base + (++counter));
            }
        }
        return result;
    }

    private void depthFirstCompile(ConnectableDiagramElement elem,
                                   List<Pair<ConnectableDiagramElement, ArrowUiElement>> connectedElements,
                                   StringBuilder code,
                                   Map<ConnectableDiagramElement, String> generatedFunNames, Set<ConnectableDiagramElement> visited) throws ScriptCompilationException {
        if (visited.contains(elem)) {
            return;
        }
        visited.add(elem);

        if (connectedElements.size() == 0) {
            // empty script
            code.append(Scripts.createFunctionWrapper(elem.getScript(), generatedFunNames.get(elem), null, null));
        } else if (connectedElements.size() == 1) {
            Pair<ConnectableDiagramElement, ArrowUiElement> connectedElement = connectedElements.get(0);
            // we wrap and append the entryFunction which the code shell uses as the entry point
            code.append(Scripts.createFunctionWrapper(elem.getScript(), generatedFunNames.get(elem), generatedFunNames.get(connectedElement.first), null));
            depthFirstCompile(connectedElement.first, connectedElement.first.getConnectedElements(), code, generatedFunNames, visited);
        } else {
            Pair<ConnectableDiagramElement, ArrowUiElement> connectedElement1 = connectedElements.get(0);
            Pair<ConnectableDiagramElement, ArrowUiElement> connectedElement2 = connectedElements.get(1);

            ConnectableDiagramElement yes = (connectedElement1.second.getCondition() == ArrowCondition.YES) ? connectedElement1.first : connectedElement2.first;
            ConnectableDiagramElement no = (connectedElement1.second.getCondition() == ArrowCondition.NO) ? connectedElement1.first : connectedElement2.first;
            code.append(Scripts.createFunctionWrapper(elem.getScript(), generatedFunNames.get(elem), generatedFunNames.get(yes), generatedFunNames.get(no)));
            depthFirstCompile(yes, yes.getConnectedElements(), code, generatedFunNames, visited);
            depthFirstCompile(no, no.getConnectedElements(), code, generatedFunNames, visited);
        }
    }
}

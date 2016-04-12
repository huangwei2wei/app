// 
// Decompiled by Procyon v0.5.30
// 

package atavism.server.engine;

import org.python.core.__builtin__;
import org.python.core.PyString;
import java.io.OutputStream;
import org.python.core.PyFile;
import java.io.ByteArrayOutputStream;
import org.python.core.CompilerFlags;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Reader;
import atavism.server.util.Log;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import org.python.core.imp;
import org.python.core.Py;
import org.python.core.PyStringMap;
import atavism.server.util.AORuntimeException;
import org.mozilla.javascript.ImporterTopLevel;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.PyModule;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Context;

public class ScriptManager
{
    private Context cx;
    private ScriptableObject scope;
    private static PyModule aomodule;
    private static PySystemState pySystemState;
    private PyObject pyLocals;
    
    public ScriptManager() {
        this.cx = null;
        this.scope = null;
    }
    
    public void init() {
        if (this.cx != null) {
            return;
        }
        this.initPythonState();
        this.pyLocals = null;
        try {
            this.cx = Context.enter();
            this.scope = (ScriptableObject)new ImporterTopLevel(this.cx, true);
        }
        catch (Exception e) {
            throw new AORuntimeException(e.toString());
        }
    }
    
    public void initLocal() {
        if (this.cx != null) {
            return;
        }
        this.initPythonState();
        this.pyLocals = new PyModule("main", (PyObject)new PyStringMap()).__dict__;
    }
    
    private void initPythonState() {
        synchronized (this.getClass()) {
            if (ScriptManager.pySystemState == null) {
                PySystemState.initialize();
                (ScriptManager.pySystemState = Py.defaultSystemState).setClassLoader(this.getClass().getClassLoader());
                ScriptManager.aomodule = imp.addModule("aomodule");
            }
        }
    }
    
    public synchronized Object runJSBuffer(final String buf) throws JavaScriptException {
        final Object result = this.cx.evaluateString((Scriptable)this.scope, buf, "ScriptManager", 1, (Object)null);
        return result;
    }
    
    public synchronized void runFile(final String filename) throws JavaScriptException, FileNotFoundException, IOException, AORuntimeException {
        try {
            if (filename.endsWith(".py")) {
                this.runPYFile(filename);
            }
            else {
                if (!filename.endsWith(".js")) {
                    throw new AORuntimeException("Unknown script file type");
                }
                this.runJSFile(filename);
            }
        }
        catch (FileNotFoundException e2) {}
        catch (AORuntimeException e) {
            throw e;
        }
        catch (Exception ex) {}
    }
    
    public synchronized void runFileWithThrow(final String filename) throws JavaScriptException, FileNotFoundException, IOException, AORuntimeException {
        if (filename.endsWith(".py")) {
            this.runPYFile(filename);
        }
        else {
            if (!filename.endsWith(".js")) {
                throw new AORuntimeException("Unknown script file type");
            }
            this.runJSFile(filename);
        }
    }
    
    public synchronized Object runJSFile(final String filename) throws JavaScriptException, FileNotFoundException, IOException {
        Reader in = null;
        try {
            in = new FileReader(filename);
        }
        catch (FileNotFoundException e) {
            Log.warn("ScriptManager.runJSFile: file not found: " + filename);
            throw e;
        }
        Object result = null;
        try {
            result = this.cx.evaluateReader((Scriptable)this.scope, in, filename, 1, (Object)null);
        }
        catch (IOException e2) {
            Log.exception("ScriptManager.runJSFile file=" + filename, e2);
            throw e2;
        }
        catch (RuntimeException e3) {
            Log.exception("ScriptManager.runJSFile file=" + filename, e3);
            throw e3;
        }
        return result;
    }
    
    public synchronized boolean runPYFile(final String filename) throws FileNotFoundException {
        if (Log.loggingDebug) {
            Log.debug("runPYFile: file=" + filename);
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(filename);
        }
        catch (FileNotFoundException e) {
            Log.warn("ScriptManager.runPYFile: file not found: " + filename);
            throw e;
        }
        try {
            Py.setSystemState(ScriptManager.pySystemState);
            Py.runCode(Py.compile_flags((InputStream)in, filename, "exec", (CompilerFlags)null), this.pyLocals, ScriptManager.aomodule.__dict__);
        }
        catch (RuntimeException e2) {
            Log.exception("ScriptManager.runPYFile: file=" + filename, e2);
            throw e2;
        }
        return true;
    }
    
    public synchronized String getResultString(final Object resultObj) {
        return Context.toString(resultObj);
    }
    
    public synchronized ScriptOutput runPYScript(final String script) {
        final ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        final ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        final PyObject saveStdout = ScriptManager.pySystemState.stdout;
        final PyObject saveStderr = ScriptManager.pySystemState.stderr;
        ScriptManager.pySystemState.stdout = (PyObject)new PyFile((OutputStream)stdout);
        ScriptManager.pySystemState.stderr = (PyObject)new PyFile((OutputStream)stderr);
        Py.setSystemState(ScriptManager.pySystemState);
        Py.exec((PyObject)Py.compile_flags(script, "<string>", "exec", (CompilerFlags)null), ScriptManager.aomodule.__dict__, this.pyLocals);
        ScriptManager.pySystemState.stdout = saveStdout;
        ScriptManager.pySystemState.stderr = saveStderr;
        return new ScriptOutput(stdout.toString(), stderr.toString());
    }
    
    public synchronized PyObject evalPYScript(final String script) {
        Py.setSystemState(ScriptManager.pySystemState);
        return __builtin__.eval((PyObject)new PyString(script), ScriptManager.aomodule.__dict__, this.pyLocals);
    }
    
    public synchronized String evalPYScriptAsString(final String script) {
        Py.setSystemState(ScriptManager.pySystemState);
        final PyObject result = __builtin__.eval((PyObject)new PyString(script), ScriptManager.aomodule.__dict__, this.pyLocals);
        if (result == null) {
            return null;
        }
        return result.toString();
    }
    
    static {
        ScriptManager.aomodule = null;
        ScriptManager.pySystemState = null;
    }
    
    public static class ScriptOutput
    {
        public String stdout;
        public String stderr;
        
        public ScriptOutput(final String out, final String err) {
            this.stdout = out;
            this.stderr = err;
        }
    }
}

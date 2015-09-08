package com.winterbe.react;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class React {
	
	private static final Logger logger = LoggerFactory.getLogger(React.class);

    private ThreadLocal<NashornScriptEngine> engineHolder = new ThreadLocal<NashornScriptEngine>() {
        @Override
        protected NashornScriptEngine initialValue() {
            NashornScriptEngine nashornScriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
            try {
            	logger.info("Initializing nashorn engine...");
            	long before = System.currentTimeMillis();
                nashornScriptEngine.eval(read("static/nashorn-polyfill.js"));
                nashornScriptEngine.eval(read("static/vendor/react.js"));
                nashornScriptEngine.eval(read("static/vendor/showdown.min.js"));
                nashornScriptEngine.eval(read("static/commentBox.js"));
            	logger.info("Evaluated all files in " + (System.currentTimeMillis() - before) + "ms");
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
            return nashornScriptEngine;
        }
    };

    public  String renderCommentBox(List<Comment> comments) {
        try {
            Object html = engineHolder.get().invokeFunction("renderServer", comments);
            return String.valueOf(html);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to render react component", e);
        }
    }

    private Reader read(String path) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        return new InputStreamReader(in);
    }
}
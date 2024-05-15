package com.nova.haodf.util;

import org.jsoup.nodes.Element;

import java.util.function.Function;

public class HtmlInstruction {
    private final Class<?> clazz;
    private final Function<Element, Object> parseAndTransform;

    public HtmlInstruction(Class<?> clazz, Function<Element, Object> parseAndTransform) {
        this.clazz = clazz;
        this.parseAndTransform = parseAndTransform;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Function<Element, Object> getParseAndTransform() {
        return parseAndTransform;
    }

    public Object applyParseAndTransform(Element element) {
        return element != null && parseAndTransform != null
                ? parseAndTransform.apply(element) : element;
    }

    @Override
    public String toString() {
        return "HtmlInstruction {" +
                "clazz=" + clazz +
                ", parseAndTransform=" + parseAndTransform +
                '}';
    }
}

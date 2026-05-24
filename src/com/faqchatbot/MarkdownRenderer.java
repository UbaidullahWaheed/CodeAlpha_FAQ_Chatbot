package com.faqchatbot;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownRenderer {

    private static final Parser parser;
    private static final HtmlRenderer renderer;

    static {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    public static String renderToHTML(String markdown) {
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        return wrapInHTML(html);
    }

    private static String wrapInHTML(String body) {
        return "<!DOCTYPE html><html><head><style>" +
                "body { font-family: 'Segoe UI', sans-serif; font-size: 13px; " +
                "color: #e6edf3; background: #161b22; margin: 8px; line-height: 1.6; }" +
                "code { background: #0d1117; color: #79c0ff; padding: 2px 6px; " +
                "border-radius: 4px; font-family: 'Consolas', monospace; font-size: 12px; }" +
                "pre { background: #0d1117; padding: 12px; border-radius: 8px; " +
                "border-left: 3px solid #1f6feb; overflow-x: auto; }" +
                "pre code { background: transparent; padding: 0; color: #79c0ff; }" +
                "h1,h2,h3 { color: #58a6ff; margin: 8px 0; }" +
                "h1 { font-size: 16px; } h2 { font-size: 15px; } h3 { font-size: 14px; }" +
                "ul,ol { padding-left: 20px; margin: 6px 0; }" +
                "li { margin: 3px 0; }" +
                "blockquote { border-left: 3px solid #1f6feb; margin: 8px 0; " +
                "padding-left: 12px; color: #8b949e; }" +
                "strong { color: #ffa657; }" +
                "em { color: #d2a8ff; }" +
                "a { color: #58a6ff; }" +
                "table { border-collapse: collapse; width: 100%; margin: 8px 0; }" +
                "th { background: #1f6feb; color: white; padding: 8px; text-align: left; }" +
                "td { border: 1px solid #30363d; padding: 6px 8px; }" +
                "tr:nth-child(even) { background: #0d1117; }" +
                "hr { border: none; border-top: 1px solid #30363d; margin: 12px 0; }" +
                "</style></head><body>" + body + "</body></html>";
    }

    public static String renderToHTMLLight(String markdown) {
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        return wrapInHTMLLight(html);
    }

    private static String wrapInHTMLLight(String body) {
        return "<!DOCTYPE html><html><head><style>" +
                "body { font-family: 'Segoe UI', sans-serif; font-size: 13px; " +
                "color: #1a2a3a; background: #ffffff; margin: 8px; line-height: 1.6; }" +
                "code { background: #f0f4f8; color: #0550ae; padding: 2px 6px; " +
                "border-radius: 4px; font-family: 'Consolas', monospace; font-size: 12px; }" +
                "pre { background: #f6f8fa; padding: 12px; border-radius: 8px; " +
                "border-left: 3px solid #1f6feb; overflow-x: auto; }" +
                "pre code { background: transparent; padding: 0; color: #0550ae; }" +
                "h1,h2,h3 { color: #0550ae; margin: 8px 0; }" +
                "strong { color: #e36209; }" +
                "em { color: #6639ba; }" +
                "a { color: #0550ae; }" +
                "table { border-collapse: collapse; width: 100%; margin: 8px 0; }" +
                "th { background: #1f6feb; color: white; padding: 8px; text-align: left; }" +
                "td { border: 1px solid #d0d7de; padding: 6px 8px; }" +
                "tr:nth-child(even) { background: #f6f8fa; }" +
                "blockquote { border-left: 3px solid #1f6feb; margin: 8px 0; " +
                "padding-left: 12px; color: #57606a; }" +
                "hr { border: none; border-top: 1px solid #d0d7de; margin: 12px 0; }" +
                "</style></head><body>" + body + "</body></html>";
    }
}
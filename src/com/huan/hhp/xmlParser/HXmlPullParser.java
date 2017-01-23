package com.huan.hhp.xmlParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by tjy on 2016/11/17 0017.
 */
public class HXmlPullParser {

    /**
     * 队列，用于解析缓存
     */
    private final LinkedList<Element> parsingQueue = new LinkedList<Element>();

    public interface OnXmlPullParserListener {
        void onBegin(Element element);
        void onEnd(Element parent, Element element);
    }

    /**
     * 属性
     */
    public static class Attribute {
        private String name;
        private String namespace;
        private String type;
        private String value;
        private String prefix;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    /**
     * 节点
     */
    public static class Element {
        private String namespace;
        private String name;
        private String text;
        private XmlPullParser parser;
        private Attribute[] attributes;
        private HashMap<String, Attribute> attributeMapping = new HashMap<String, Attribute>(0);
        private Object obj;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
            //Log.i("HXmlPullParser", this.name+" -> text="+text);
        }

        public Attribute[] getAttributes() {
            return attributes;
        }

        public void setAttributes(Attribute[] attributes) {
            this.attributes = attributes;
            for(Attribute attribute : attributes){
                attributeMapping.put(attribute.getName(), attribute);
            }
        }

        public Attribute attribute(String name){
            return attributeMapping.get(name);
        }

        public String attributeValue(String name){
            Attribute attribute = attribute(name);
            if(attribute != null){
                return attribute.getValue();
            }
            return null;
        }

        public XmlPullParser getParser() {
            return parser;
        }

        public void setParser(XmlPullParser parser) {
            this.parser = parser;
        }

        public <T> T getObj() {
            return (T) obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }
    }

    private StringWriter stringWriter;
    private OnXmlPullParserListener onXmlPullParserListener;
    public void setOnXmlPullParserListener(OnXmlPullParserListener onXmlPullParserListener) {
        this.onXmlPullParserListener = onXmlPullParserListener;
    }

    public void load(InputStream inputStream) throws IOException, XmlPullParserException {
        String DEBUT_TAG = "H-XML-PULL-PARSER";
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser mXmlPullParser = factory.newPullParser();
        mXmlPullParser.setInput(inputStream, "utf-8");

        XmlSerializer serializer = factory.newSerializer();

        int eventType = mXmlPullParser.getEventType();
        boolean powerOff = false;
        while(!powerOff) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:{
                    //Log.d(DEBUT_TAG, "start_document");
                    serializer.setOutput(stringWriter=new StringWriter());
                    serializer.startDocument("utf-8", true);
                    parsingQueue.clear();
                    break;
                }
                case XmlPullParser.START_TAG:{
                    //Log.d(DEBUT_TAG, "start_tag " + mXmlPullParser.getName());
                    Element element = new Element();
                    element.setNamespace(mXmlPullParser.getNamespace());
                    element.setName(mXmlPullParser.getName());
                    element.setText(mXmlPullParser.getText());
                    element.setParser(mXmlPullParser);
                    serializer.startTag(element.getNamespace(), element.getName());
                    int attrCount = mXmlPullParser.getAttributeCount();
                    Attribute[] attributes = new Attribute[attrCount];
                    int i = 0;
                    for(; i < attrCount; i++){
                        Attribute attribute = new Attribute();
                        attribute.setName(mXmlPullParser.getAttributeName(i));
                        attribute.setNamespace(mXmlPullParser.getAttributeNamespace(i));
                        attribute.setPrefix(mXmlPullParser.getAttributePrefix(i));
                        attribute.setType(mXmlPullParser.getAttributeType(i));
                        attribute.setValue(mXmlPullParser.getAttributeValue(i));
                        attributes[i] = attribute;
                        serializer.attribute(attribute.getNamespace(), attribute.getName(), attribute.getValue());
                    }
                    element.setAttributes(attributes);
                    parsingQueue.add(0, element); // 加入队列
                    onXmlPullParserListener.onBegin(element);
                    break;
                }
                case XmlPullParser.TEXT:{
                    //Log.d(DEBUT_TAG, "text...");
                    Element element = parsingQueue.get(0);
                    element.setText(mXmlPullParser.getText());
                    serializer.text(element.getText());
                    break;
                }
                case XmlPullParser.END_TAG:{
                    //Log.d(DEBUT_TAG, "end_tag "+mXmlPullParser.getName());
                    Element parent = null;
                    Element element = parsingQueue.remove(0);
                    if(parsingQueue.size() > 0){
                        parent = parsingQueue.get(0);
                    }
                    onXmlPullParserListener.onEnd(parent, element);
                    serializer.endTag(element.getNamespace(), element.getName());
                    break;
                }
                case XmlPullParser.END_DOCUMENT:{
                    //Log.d(DEBUT_TAG, "end_document");
                    serializer.endDocument();
                    powerOff = true;
                    break;
                }
            }
            eventType = mXmlPullParser.next();
        }
    }

    public String asXml(){
        if(stringWriter == null)
            return  null;
        return stringWriter.toString();
    }
}

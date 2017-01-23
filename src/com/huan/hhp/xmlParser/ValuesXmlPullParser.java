package com.huan.hhp.xmlParser;

import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/22 0022.
 */
public class ValuesXmlPullParser extends HXmlPullParser {
    private HashMap<String, String> values = new HashMap<String, String>();
    public ValuesXmlPullParser(File file) throws IOException, XmlPullParserException {
        this.setOnXmlPullParserListener(new OnXmlPullParserListener() {
            @Override
            public void onBegin(Element element) {

            }

            @Override
            public void onEnd(Element parent, Element element) {
                if(element.attribute("name") != null){
                    values.put(element.attributeValue("name"), element.getText());
                }
            }
        });
        this.load(new FileInputStream(file));
        Log.i("ValuesXmlPullParser", "values="+values);
    }

    public String getString(String name){
        return values.get(name);
    }
}

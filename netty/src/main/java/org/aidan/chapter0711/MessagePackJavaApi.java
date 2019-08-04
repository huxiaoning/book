package org.aidan.chapter0711;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MessagePackJavaApi {
    public static void main(String[] args) throws IOException {
        // 创建一些待序列化的对象
        List<String> src = new ArrayList<>();
        src.add("msgpack");
        src.add("kumofs");
        src.add("viver");

        MessagePack msgpack = new MessagePack();

        // 序列化
        byte[] raw = msgpack.write(src);
        // 直接使用模板反序列化
        List<String> dst1 = msgpack.read(raw, Templates.tList(Templates.TString));
        System.out.println(dst1.get(0));
        System.out.println(dst1.get(1));
        System.out.println(dst1.get(2));

        // false 主要是用来传输数据的，并不复用对象
        System.out.println(src == dst1);
    }
}

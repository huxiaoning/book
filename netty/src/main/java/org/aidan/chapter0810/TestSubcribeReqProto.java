package org.aidan.chapter0810;

import com.google.protobuf.InvalidProtocolBufferException;
import org.aidan.chapter0810.protobuf.SubscribeReqProto;

import java.util.ArrayList;
import java.util.List;

public class TestSubcribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        // 编码
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        // 解码
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        /**
         * POJO对象属性的赋值操作，在编程习惯上稍有不同
         */
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqId(1);
        builder.setUserName("Lilinfeng");
        builder.setProductName("Netty Book");
        List<String> address = new ArrayList<>();
        address.add("NanJing YuHuaTai");
        address.add("BeiJing LiuLiChang");
        address.add("ShenZhen HongShuLin");
        builder.addAllAddress(address);
        return builder.build();
    }


    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode : " + req.toString());

        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode : " + req2.toString());

        // true : 可以查看 equals的实现逻辑(所有字段都相等时返回true)
        System.out.println("Assert equal : --> " + req2.equals(req));
        // false
        System.out.println("Assert == : --> " + (req2 == req));
    }
}

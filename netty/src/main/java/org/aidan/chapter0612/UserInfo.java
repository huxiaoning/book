package org.aidan.chapter0612;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class UserInfo implements Serializable {

    /**
     * 默认的序列号
     */
    private static final long serialVersionUID = 1L;

    private String userName;

    private int userId;

    public UserInfo buildUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public UserInfo buildUserId(int userId) {
        this.userId = userId;
        return this;
    }

    /**
     * @return the userName
     */
    public final String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public final void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userId
     */
    public final int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public final void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 使用 ByteBuffer 的通用二进制编解码技术，对UserInfo对象进行编码，编码结果仍然是 byte[]
     * <p>
     * 编码的结果可以与传统 JDK序列化后的码流大小进行比较
     *
     * @return
     */
    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(this.userId);
        buffer.flip();
        value = null;
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public static void main(String[] args) throws IOException {
        UserInfo info = new UserInfo();
        info.buildUserId(100).buildUserName("Welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(info);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        // 115
        System.out.println("JDK序列化对象后的字节数组长度为：" + b.length);
        bos.close();
        System.out.println("---------------");
        // 24
        System.out.println("字节数组序列化长度：" + info.codeC().length);
    }
}

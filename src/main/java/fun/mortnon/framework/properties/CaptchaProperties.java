package fun.mortnon.framework.properties;

/**
 * @author dongfangzan
 * @date 30.4.21 10:21 上午
 */
public class CaptchaProperties {

    private boolean enable = true;

    private String type = "arithmetic";

    private long expireSeconds = 600L;

    private int width = 130;

    private int height = 48;

    private int length = 5;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}

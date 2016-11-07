package org.tguduru.guava.domain;

/**
 * A simple pojo holds data about a mobile phone
 * @author Guduru, Thirupathi Reddy
 * @modified 11/12/15
 */
public class MobilePhone {
    private final String model;
    private final Color color;
    private final OS os;

    public MobilePhone(final String model, final Color color, final OS os) {
        this.model = model;
        this.color = color;
        this.os = os;
    }

    public String getModel() {
        return model;
    }

    public Color getColor() {
        return color;
    }

    public OS getOs() {
        return os;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(200);
        sb.append("MobilePhone [");
        sb.append("model='").append(model).append('\'');
        sb.append(", color=").append(color);
        sb.append(", os=").append(os);
        sb.append("]");
        return sb.toString();
    }
}

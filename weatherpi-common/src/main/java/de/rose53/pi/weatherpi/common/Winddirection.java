package de.rose53.pi.weatherpi.common;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum Winddirection {

    N(0.0,"north"),
    NNE(22.5,"north-northeast"),
    NE(45.0,"northeast"),
    ENE(67.5,"east-northeast"),
    E(90.0,"east"),
    ESE(112.5,"east-southeast"),
    SE(135.0,"southeast"),
    SSE(157.5,"south-southeast"),
    S(180.0,"south"),
    SSW(202.5,"south-southwest"),
    SW(225.0,"southwest"),
    WSW(247.5,"west-southwest"),
    W(270.0,"west"),
    WNW(292.5,"west-northwest"),
    NW(315.0,"northwest"),
    NNW(337.5,"north-northwest");

    private static final Map<Double,Winddirection> winddirectionMap;

    private final double degree;
    private final String directionName;

    private Winddirection(double degree, String directionName) {
        this.degree        = degree;
        this.directionName = directionName;
    }

    public double getDegree() {
        return degree;
    }

    public String getDirectionName() {
        return directionName;
    }

    static {
        Map<Double,Winddirection> map = new ConcurrentHashMap<Double,Winddirection>();
        for (Winddirection instance : Winddirection.values()) {
          map.put(instance.getDegree(),instance);
        }
        winddirectionMap = Collections.unmodifiableMap(map);
      }

    public static Winddirection fromDegrees(double degree) {

        if (degree < 0) {
            return null;
        }

        degree = 22.5 * Math.round((degree % 360) / 22.5);
        return winddirectionMap.get(degree % 360);
    }
}

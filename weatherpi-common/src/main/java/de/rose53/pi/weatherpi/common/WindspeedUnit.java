package de.rose53.pi.weatherpi.common;

public enum WindspeedUnit {
    MS("m/s") {
        @Override
        public double toMS(double windspeed) {
            return windspeed;
        }

        @Override
        public double fromMS(double windspeed) {
            return windspeed;
        }

        @Override
        public String getDescription(double windspeed) {

            return BFT.getDescription(BFT.fromMS(windspeed));
        }
    },
    KMH("km/h") {
        @Override
        public double toMS(double windspeed) {
            return 0.278 * windspeed;
        }

        @Override
        public double fromMS(double windspeed) {
            return 3.6 * windspeed;
        }

        @Override
        public String getDescription(double windspeed) {
            return BFT.getDescription(BFT.fromMS(toMS(windspeed)));
        }
    },
    MPH("mph") {
        @Override
        public double toMS(double windspeed) {
            return 0.447 * windspeed;
        }

        @Override
        public double fromMS(double windspeed) {
            return 2.237 * windspeed;
        }

        @Override
        public String getDescription(double windspeed) {
            return BFT.getDescription(BFT.fromMS(toMS(windspeed)));
        }
    },
    KN("kn") {
        @Override
        public double toMS(double windspeed) {
            return 0.514 * windspeed;
        }

        @Override
        public double fromMS(double windspeed) {
            return 1.944 * windspeed;
        }

        @Override
        public String getDescription(double windspeed) {
            return BFT.getDescription(BFT.fromMS(toMS(windspeed)));
        }
    },
    BFT("") {
        @Override
        public double toMS(double windspeed) {
            return -1.0;  // not possible
        }

        @Override
        public double fromMS(double windspeed) {
            if (windspeed < 0.3) {
                return 0;
            } else if (0.3 <= windspeed && windspeed < 1.6) {
                return 1;
            } else if (1.6 <= windspeed && windspeed < 3.4) {
                return 2;
            } else if (3.4 <= windspeed && windspeed < 5.5) {
                return 3;
            } else if (5.5 <= windspeed && windspeed < 8.0) {
                return 4;
            } else if (8.0 <= windspeed && windspeed < 10.8) {
                return 5;
            } else if (10.8 <= windspeed && windspeed < 13.9) {
                return 6;
            } else if (13.9 <= windspeed && windspeed < 17.2) {
                return 7;
            } else if (17.2 <= windspeed && windspeed < 20.8) {
                return 8;
            } else if (20.8 <= windspeed && windspeed < 24.5) {
                return 9;
            } else if (24.5 <= windspeed && windspeed < 28.5) {
                return 10;
            } else if (28.5 <= windspeed && windspeed < 32.7) {
                return 11;
            } else {
                return 12;
            }
        }

        @Override
        public String getDescription(double windspeed) {
            int bft = (int) Math.round(windspeed);
            String retVal = null;
            switch (bft) {
            case 0:
                retVal = "Calm";
                break;
            case 1:
                retVal = "Light Air";
                break;
            case 2:
                retVal = "Light Breeze";
                break;
            case 3:
                retVal = "Gentle Breeze";
                break;
            case 4:
                retVal = "Moderate Breeze";
                break;
            case 5:
                retVal = "Fresh Breeze";
                break;
            case 6:
                retVal = "Strong Breeze";
                break;
            case 7:
                retVal = "Near Gale";
                break;
            case 8:
                retVal = "Gale";
                break;
            case 9:
                retVal = "Strong Gale";
                break;
            case 10:
                retVal = "Storm";
                break;
            case 11:
                retVal = "Violent Storm";
                break;
            case 12:
                retVal = "Hurricane Force";
                break;
            }
            return retVal;
        }
    };

    private final String unit;

    private WindspeedUnit(String unit) {
        this.unit        = unit;
    }

    public String getUnit() {
        return unit;
    }

    public abstract String getDescription(double windspeed);

    public abstract double toMS(double windspeed);
    public abstract double fromMS(double windspeed);

    public static double convert( double windspeed, WindspeedUnit from, WindspeedUnit to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to must be not null");
        }

        if (from == to) {
            return windspeed;
        }

        if (BFT == from) {
            throw new IllegalArgumentException("can not convert from BFT to another unit");
        }
        return to.fromMS(from.toMS(windspeed));
    }

    public static String getDescription(double windspeed, WindspeedUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("from and to must be not null");
        }

        return unit.getDescription(windspeed);
    }

    public static WindspeedUnit fromString(String text) {

        for (WindspeedUnit b : WindspeedUnit.values()) {
            if (b.toString().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return MS;
    }
}

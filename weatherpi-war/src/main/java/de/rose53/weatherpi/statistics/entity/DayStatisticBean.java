package de.rose53.weatherpi.statistics.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="DAY_STATISTIC")
@NamedQueries({
    @NamedQuery(name = DayStatisticBean.COUNT,
                query= "SELECT count(d) FROM DayStatisticBean d"),
    @NamedQuery(name = DayStatisticBean.FIND_BY_DAY,
                query= "SELECT d FROM DayStatisticBean d WHERE d.day = :day"),
    @NamedQuery(name = DayStatisticBean.FIND_RANGE,
                query= "SELECT d FROM DayStatisticBean d WHERE d.day BETWEEN :startDate AND :endDate ORDER BY d.day ASC")
})
public class DayStatisticBean {

    public static final String COUNT       = "DayStatisticBean.count";
    public static final String FIND_BY_DAY = "DayStatisticBean.findByDay";
    public static final String FIND_RANGE  = "DayStatisticBean.findRange";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Temporal(TemporalType.DATE)
    @Column(name="DAY", updatable=false, nullable=false)
    private Date day;

    @Column(name="T_MIN", updatable=false, nullable=true)
    Double tMin;

    @Column(name="T_MAX", updatable=false, nullable=true)
    Double tMax;

    @Column(name="T_MED", updatable=false, nullable=true)
    Double tMed;

    @Column(name="T0", updatable=false, nullable=true)
    Double t0;

    @Column(name="T1", updatable=false, nullable=true)
    Double t1;

    @Column(name="T2", updatable=false, nullable=true)
    Double t2;

    @Column(name="T3", updatable=false, nullable=true)
    Double t3;

    @Column(name="T4", updatable=false, nullable=true)
    Double t4;

    @Column(name="T5", updatable=false, nullable=true)
    Double t5;

    @Column(name="T6", updatable=false, nullable=true)
    Double t6;

    @Column(name="T7", updatable=false, nullable=true)
    Double t7;

    @Column(name="T8", updatable=false, nullable=true)
    Double t8;

    @Column(name="T9", updatable=false, nullable=true)
    Double t9;

    @Column(name="T10", updatable=false, nullable=true)
    Double t10;

    @Column(name="T11", updatable=false, nullable=true)
    Double t11;

    @Column(name="T12", updatable=false, nullable=true)
    Double t12;

    @Column(name="T13", updatable=false, nullable=true)
    Double t13;

    @Column(name="T14", updatable=false, nullable=true)
    Double t14;

    @Column(name="T15", updatable=false, nullable=true)
    Double t15;

    @Column(name="T16", updatable=false, nullable=true)
    Double t16;

    @Column(name="T17", updatable=false, nullable=true)
    Double t17;

    @Column(name="T18", updatable=false, nullable=true)
    Double t18;

    @Column(name="T19", updatable=false, nullable=true)
    Double t19;

    @Column(name="T20", updatable=false, nullable=true)
    Double t20;

    @Column(name="T21", updatable=false, nullable=true)
    Double t21;

    @Column(name="T22", updatable=false, nullable=true)
    Double t22;

    @Column(name="T23", updatable=false, nullable=true)
    Double t23;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }


    public Double gettMin() {
        return tMin;
    }

    public void settMin(Double tMin) {
        this.tMin = tMin;
    }

    public Double gettMax() {
        return tMax;
    }

    public void settMax(Double tMax) {
        this.tMax = tMax;
    }

    public Double gettMed() {
        return tMed;
    }

    public void settMed(Double tMed) {
        this.tMed = tMed;
    }

    public Double getT0() {
        return t0;
    }

    public void setT0(Double t0) {
        this.t0 = t0;
    }

    public Double getT1() {
        return t1;
    }

    public void setT1(Double t1) {
        this.t1 = t1;
    }

    public Double getT2() {
        return t2;
    }

    public void setT2(Double t2) {
        this.t2 = t2;
    }

    public Double getT3() {
        return t3;
    }

    public void setT3(Double t3) {
        this.t3 = t3;
    }

    public Double getT4() {
        return t4;
    }

    public void setT4(Double t4) {
        this.t4 = t4;
    }

    public Double getT5() {
        return t5;
    }

    public void setT5(Double t5) {
        this.t5 = t5;
    }

    public Double getT6() {
        return t6;
    }

    public void setT6(Double t6) {
        this.t6 = t6;
    }

    public Double getT7() {
        return t7;
    }

    public void setT7(Double t7) {
        this.t7 = t7;
    }

    public Double getT8() {
        return t8;
    }

    public void setT8(Double t8) {
        this.t8 = t8;
    }

    public Double getT9() {
        return t9;
    }

    public void setT9(Double t9) {
        this.t9 = t9;
    }

    public Double getT10() {
        return t10;
    }

    public void setT10(Double t10) {
        this.t10 = t10;
    }

    public Double getT11() {
        return t11;
    }

    public void setT11(Double t11) {
        this.t11 = t11;
    }

    public Double getT12() {
        return t12;
    }

    public void setT12(Double t12) {
        this.t12 = t12;
    }

    public Double getT13() {
        return t13;
    }

    public void setT13(Double t13) {
        this.t13 = t13;
    }

    public Double getT14() {
        return t14;
    }

    public void setT14(Double t14) {
        this.t14 = t14;
    }

    public Double getT15() {
        return t15;
    }

    public void setT15(Double t15) {
        this.t15 = t15;
    }

    public Double getT16() {
        return t16;
    }

    public void setT16(Double t16) {
        this.t16 = t16;
    }

    public Double getT17() {
        return t17;
    }

    public void setT17(Double t17) {
        this.t17 = t17;
    }

    public Double getT18() {
        return t18;
    }

    public void setT18(Double t18) {
        this.t18 = t18;
    }

    public Double getT19() {
        return t19;
    }

    public void setT19(Double t19) {
        this.t19 = t19;
    }

    public Double getT20() {
        return t20;
    }

    public void setT20(Double t20) {
        this.t20 = t20;
    }

    public Double getT21() {
        return t21;
    }

    public void setT21(Double t21) {
        this.t21 = t21;
    }

    public Double getT22() {
        return t22;
    }

    public void setT22(Double t22) {
        this.t22 = t22;
    }

    public Double getT23() {
        return t23;
    }

    public void setT23(Double t23) {
        this.t23 = t23;
    }

    @Transient
    public void setT(int hour, Double t) {
        switch (hour) {
        case 0:
            setT0(t);
            break;
        case 1:
            setT1(t);
            break;
        case 2:
            setT2(t);
            break;
        case 3:
            setT3(t);
            break;
        case 4:
            setT4(t);
            break;
        case 5:
            setT5(t);
            break;
        case 6:
            setT6(t);
            break;
        case 7:
            setT7(t);
            break;
        case 8:
            setT8(t);
            break;
        case 9:
            setT9(t);
            break;
        case 10:
            setT10(t);
            break;
        case 11:
            setT11(t);
            break;
        case 12:
            setT12(t);
            break;
        case 13:
            setT13(t);
            break;
        case 14:
            setT14(t);
            break;
        case 15:
            setT15(t);
            break;
        case 16:
            setT16(t);
            break;
        case 17:
            setT17(t);
            break;
        case 18:
            setT18(t);
            break;
        case 19:
            setT19(t);
            break;
        case 20:
            setT20(t);
            break;
        case 21:
            setT21(t);
            break;
        case 22:
            setT22(t);
            break;
        case 23:
            setT23(t);
            break;
        }
    }

    @PreUpdate
    @PrePersist
    void calculate() {
        List<Double> values = new ArrayList<>();

        if (getT0() != null) {
            values.add(getT0());
        }

        if (getT1() != null) {
            values.add(getT1());
        }

        if (getT2() != null) {
            values.add(getT2());
        }

        if (getT3() != null) {
            values.add(getT3());
        }

        if (getT4() != null) {
            values.add(getT4());
        }

        if (getT5() != null) {
            values.add(getT5());
        }

        if (getT6() != null) {
            values.add(getT6());
        }

        if (getT7() != null) {
            values.add(getT7());
        }

        if (getT8() != null) {
            values.add(getT8());
        }
        if (getT9() != null) {
            values.add(getT9());
        }
        if (getT10() != null) {
            values.add(getT10());
        }
        if (getT11() != null) {
            values.add(getT11());
        }
        if (getT12() != null) {
            values.add(getT12());
        }
        if (getT13() != null) {
            values.add(getT13());
        }
        if (getT14() != null) {
            values.add(getT14());
        }
        if (getT15() != null) {
            values.add(getT15());
        }
        if (getT16() != null) {
            values.add(getT16());
        }
        if (getT17() != null) {
            values.add(getT17());
        }
        if (getT18() != null) {
            values.add(getT18());
        }
        if (getT19() != null) {
            values.add(getT19());
        }
        if (getT20() != null) {
            values.add(getT20());
        }
        if (getT21() != null) {
            values.add(getT21());
        }
        if (getT22() != null) {
            values.add(getT22());
        }
        if (getT23() != null) {
            values.add(getT23());
        }

        values.stream().mapToDouble(a -> a).average().ifPresent(d -> settMed(d));
        values.stream().mapToDouble(a -> a).min().ifPresent(d -> settMin(d));
        values.stream().mapToDouble(a -> a).max().ifPresent(d -> settMax(d));
    }
}

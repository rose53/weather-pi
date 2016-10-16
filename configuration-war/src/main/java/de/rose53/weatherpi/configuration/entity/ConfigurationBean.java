package de.rose53.weatherpi.configuration.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "CONFIGURATION")
@NamedQueries({
     @NamedQuery(name = ConfigurationBean.findValueByKey,
                 query= "SELECT c.value FROM ConfigurationBean c WHERE c.key = :key"),
     @NamedQuery(name = ConfigurationBean.findValuesByKey,
                 query= "SELECT c.value FROM ConfigurationBean c WHERE c.key LIKE :key")
})
public class ConfigurationBean {

    public static final String findValueByKey = "ConfigurationBean.findValueByKey";
    public static final String findValuesByKey = "ConfigurationBean.findValuesByKey";

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name="KEY", updatable=false,nullable=false,unique=true)
    private String key;

    @Column(name="VALUE", nullable=false, updatable=false)
    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}

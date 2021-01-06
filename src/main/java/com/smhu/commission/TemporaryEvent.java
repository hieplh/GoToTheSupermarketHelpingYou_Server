package com.smhu.commission;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

public class TemporaryEvent implements Comparable<TemporaryEvent> {

    private String id;
    private String desc;
    private Date dateCreated;
    private Time timeCreated;
    private Date dateStart;
    private Date dateEnd;
    private Time timeStart;
    private Time timeEnd;
    private double bonusShipping;
    private double bonusShopping;

    public TemporaryEvent() {
    }

    public TemporaryEvent(String id, String desc, Date dateCreated, Time timeCreated, Date dateStart, Date dateEnd, Time timeStart, Time timeEnd, double bonusShipping, double bonusShopping) {
        this.id = id;
        this.desc = desc;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.bonusShipping = bonusShipping;
        this.bonusShopping = bonusShopping;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Time getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Time timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Time getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Time timeStart) {
        this.timeStart = timeStart;
    }

    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time timeEnd) {
        this.timeEnd = timeEnd;
    }

    public double getBonusShipping() {
        return bonusShipping;
    }

    public void setBonusShipping(double bonusShipping) {
        this.bonusShipping = bonusShipping;
    }

    public double getBonusShopping() {
        return bonusShopping;
    }

    public void setBonusShopping(double bonusShopping) {
        this.bonusShopping = bonusShopping;
    }

    @Override
    public String toString() {
        return "TemporaryEvent{" + "id=" + id + ", desc=" + desc + ", dateCreated=" + dateCreated + ", timeCreated=" + timeCreated + ", dateStart=" + dateStart + ", dateEnd=" + dateEnd + ", timeStart=" + timeStart + ", timeEnd=" + timeEnd + ", bonusShipping=" + bonusShipping + ", bonusShopping=" + bonusShopping + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TemporaryEvent other = (TemporaryEvent) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(TemporaryEvent o) {
        return this.timeStart.compareTo(o.timeStart) == 0 ? this.timeEnd.compareTo(o.timeEnd) : this.timeStart.compareTo(o.timeStart);
    }

}

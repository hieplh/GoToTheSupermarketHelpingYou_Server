package com.smhu.commission;

import java.sql.Date;
import java.sql.Time;

public class Commission {

    private String id;
    private Date dateCreated;
    private Time timeCreated;
    private Date dateApply;
    private Time timeApply;
    private int firstShipping;
    private int firstShopping;

    private Time timeMorning;
    private double fsiMorCost;
    private double nsiMorCost;
    private double fsoMorCost;
    private double nsoMorCost;

    private Time timeMidday;
    private double fsiMidCost;
    private double nsiMidCost;
    private double fsoMidCost;
    private double nsoMidCost;

    private Time timeAfternoon;
    private double fsiAfCost;
    private double nsiAfCost;
    private double fsoAfCost;
    private double nsoAfCost;

    private Time timeEvening;
    private double fsiEveCost;
    private double nsiEveCost;
    private double fsoEveCost;
    private double nsoEveCost;

    private int commissionShipping;
    private int commissionShopping;

    public Commission() {
    }

    public Commission(String id, Date dateCreated, Time timeCreated, Date dateApply, Time timeApply, int firstShipping, int firstShopping, Time timeMorning, double fsiMorCost, double nsiMorCost, double fsoMorCost, double nsoMorCost, Time timeMidday, double fsiMidCost, double nsiMidCost, double fsoMidCost, double nsoMidCost, Time timeAfternoon, double fsiAfCost, double nsiAfCost, double fsoAfCost, double nsoAfCost, Time timeEvening, double fsiEveCost, double nsiEveCost, double fsoEveCost, double nsoEveCost, int commissionShipping, int commissionShopping) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.dateApply = dateApply;
        this.timeApply = timeApply;
        this.firstShipping = firstShipping;
        this.firstShopping = firstShopping;
        this.timeMorning = timeMorning;
        this.fsiMorCost = fsiMorCost;
        this.nsiMorCost = nsiMorCost;
        this.fsoMorCost = fsoMorCost;
        this.nsoMorCost = nsoMorCost;
        this.timeMidday = timeMidday;
        this.fsiMidCost = fsiMidCost;
        this.nsiMidCost = nsiMidCost;
        this.fsoMidCost = fsoMidCost;
        this.nsoMidCost = nsoMidCost;
        this.timeAfternoon = timeAfternoon;
        this.fsiAfCost = fsiAfCost;
        this.nsiAfCost = nsiAfCost;
        this.fsoAfCost = fsoAfCost;
        this.nsoAfCost = nsoAfCost;
        this.timeEvening = timeEvening;
        this.fsiEveCost = fsiEveCost;
        this.nsiEveCost = nsiEveCost;
        this.fsoEveCost = fsoEveCost;
        this.nsoEveCost = nsoEveCost;
        this.commissionShipping = commissionShipping;
        this.commissionShopping = commissionShopping;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getDateApply() {
        return dateApply;
    }

    public void setDateApply(Date dateApply) {
        this.dateApply = dateApply;
    }

    public Time getTimeApply() {
        return timeApply;
    }

    public void setTimeApply(Time timeApply) {
        this.timeApply = timeApply;
    }

    public int getFirstShipping() {
        return firstShipping;
    }

    public void setFirstShipping(int firstShipping) {
        this.firstShipping = firstShipping;
    }

    public int getFirstShopping() {
        return firstShopping;
    }

    public void setFirstShopping(int firstShopping) {
        this.firstShopping = firstShopping;
    }

    public Time getTimeMorning() {
        return timeMorning;
    }

    public void setTimeMorning(Time timeMorning) {
        this.timeMorning = timeMorning;
    }

    public double getFsiMorCost() {
        return fsiMorCost;
    }

    public void setFsiMorCost(double fsiMorCost) {
        this.fsiMorCost = fsiMorCost;
    }

    public double getNsiMorCost() {
        return nsiMorCost;
    }

    public void setNsiMorCost(double nsiMorCost) {
        this.nsiMorCost = nsiMorCost;
    }

    public double getFsoMorCost() {
        return fsoMorCost;
    }

    public void setFsoMorCost(double fsoMorCost) {
        this.fsoMorCost = fsoMorCost;
    }

    public double getNsoMorCost() {
        return nsoMorCost;
    }

    public void setNsoMorCost(double nsoMorCost) {
        this.nsoMorCost = nsoMorCost;
    }

    public Time getTimeMidday() {
        return timeMidday;
    }

    public void setTimeMidday(Time timeMidday) {
        this.timeMidday = timeMidday;
    }

    public double getFsiMidCost() {
        return fsiMidCost;
    }

    public void setFsiMidCost(double fsiMidCost) {
        this.fsiMidCost = fsiMidCost;
    }

    public double getNsiMidCost() {
        return nsiMidCost;
    }

    public void setNsiMidCost(double nsiMidCost) {
        this.nsiMidCost = nsiMidCost;
    }

    public double getFsoMidCost() {
        return fsoMidCost;
    }

    public void setFsoMidCost(double fsoMidCost) {
        this.fsoMidCost = fsoMidCost;
    }

    public double getNsoMidCost() {
        return nsoMidCost;
    }

    public void setNsoMidCost(double nsoMidCost) {
        this.nsoMidCost = nsoMidCost;
    }

    public Time getTimeAfternoon() {
        return timeAfternoon;
    }

    public void setTimeAfternoon(Time timeAfternoon) {
        this.timeAfternoon = timeAfternoon;
    }

    public double getFsiAfCost() {
        return fsiAfCost;
    }

    public void setFsiAfCost(double fsiAfCost) {
        this.fsiAfCost = fsiAfCost;
    }

    public double getNsiAfCost() {
        return nsiAfCost;
    }

    public void setNsiAfCost(double nsiAfCost) {
        this.nsiAfCost = nsiAfCost;
    }

    public double getFsoAfCost() {
        return fsoAfCost;
    }

    public void setFsoAfCost(double fsoAfCost) {
        this.fsoAfCost = fsoAfCost;
    }

    public double getNsoAfCost() {
        return nsoAfCost;
    }

    public void setNsoAfCost(double nsoAfCost) {
        this.nsoAfCost = nsoAfCost;
    }

    public Time getTimeEvening() {
        return timeEvening;
    }

    public void setTimeEvening(Time timeEvening) {
        this.timeEvening = timeEvening;
    }

    public double getFsiEveCost() {
        return fsiEveCost;
    }

    public void setFsiEveCost(double fsiEveCost) {
        this.fsiEveCost = fsiEveCost;
    }

    public double getNsiEveCost() {
        return nsiEveCost;
    }

    public void setNsiEveCost(double nsiEveCost) {
        this.nsiEveCost = nsiEveCost;
    }

    public double getFsoEveCost() {
        return fsoEveCost;
    }

    public void setFsoEveCost(double fsoEveCost) {
        this.fsoEveCost = fsoEveCost;
    }

    public double getNsoEveCost() {
        return nsoEveCost;
    }

    public void setNsoEveCost(double nsoEveCost) {
        this.nsoEveCost = nsoEveCost;
    }

    public int getCommissionShipping() {
        return commissionShipping;
    }

    public void setCommissionShipping(int commissionShipping) {
        this.commissionShipping = commissionShipping;
    }

    public int getCommissionShopping() {
        return commissionShopping;
    }

    public void setCommissionShopping(int commissionShopping) {
        this.commissionShopping = commissionShopping;
    }

    @Override
    public String toString() {
        return "Commission{" + "id=" + id + ", dateCreated=" + dateCreated + ", timeCreated=" + timeCreated + ", dateApply=" + dateApply + ", timeApply=" + timeApply + ", firstShipping=" + firstShipping + ", firstShopping=" + firstShopping + ", timeMorning=" + timeMorning + ", fsiMorCost=" + fsiMorCost + ", nsiMorCost=" + nsiMorCost + ", fsoMorCost=" + fsoMorCost + ", nsoMorCost=" + nsoMorCost + ", timeMidday=" + timeMidday + ", fsiMidCost=" + fsiMidCost + ", nsiMidCost=" + nsiMidCost + ", fsoMidCost=" + fsoMidCost + ", nsoMidCost=" + nsoMidCost + ", timeAfternoon=" + timeAfternoon + ", fsiAfCost=" + fsiAfCost + ", nsiAfCost=" + nsiAfCost + ", fsoAfCost=" + fsoAfCost + ", nsoAfCost=" + nsoAfCost + ", timeEvening=" + timeEvening + ", fsiEveCost=" + fsiEveCost + ", nsiEveCost=" + nsiEveCost + ", fsoEveCost=" + fsoEveCost + ", nsoEveCost=" + nsoEveCost + ", commissionShipping=" + commissionShipping + ", commissionShopping=" + commissionShopping + '}';
    }

}

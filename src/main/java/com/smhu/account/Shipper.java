package com.smhu.account;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

public class Shipper extends Account implements Comparable<Shipper> {

    private int maxOrder;
    private String lat;
    private String lng;

    public Shipper() {
    }

    public Shipper(Account account) {
        super(account.getId(), account.getUsername(), account.getFirstName(), account.getMiddleName(), account.getLastName(),
                account.getEmail(), account.getPhone(), account.getDob(), account.getRole(),
                account.getNumSuccess(), account.getNumCancel(), account.getWallet(), null);
    }

    public Shipper(Account account, int maxOrder) {
        super(account.getId(), account.getUsername(), account.getFirstName(), account.getMiddleName(), account.getLastName(),
                account.getEmail(), account.getPhone(), account.getDob(), account.getRole(),
                account.getNumSuccess(), account.getNumCancel(), account.getWallet(), null);
        this.maxOrder = maxOrder;
    }

    public Shipper(String id, String username, String firstName, String middleName, String lastName,
            String email, String phone, Date dob, String role, int numSuccess, int numCancel, double wallet, List<Address> addresses,
            int maxOrder, String lat, String lng) {
        super(id, username, firstName, middleName, lastName, email, phone, dob, role, numSuccess, numCancel, wallet, addresses);
        this.maxOrder = maxOrder;
        this.lat = lat;
        this.lng = lng;
    }

    public int getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(int maxOrder) {
        this.maxOrder = maxOrder;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Shipper{" + "id=" + this.getId() + ", maxOrder=" + maxOrder + ", lat=" + lat + ", lng=" + lng + '}';
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
        final Shipper other = (Shipper) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Shipper o) {
        int sumO1 = this.getNumSuccess() + this.getNumCancel();
        int sumO2 = o.getNumSuccess() + o.getNumCancel();
        if (Math.round((double) this.getNumSuccess() / sumO1 * 1000) / 1000.0
                == Math.round((double) o.getNumSuccess() / sumO2 * 1000) / 1000.0) {
            if (this.getNumSuccess() > o.getNumSuccess()) {
                return 1;
            } else {
                return -1;
            }
        } else if (Math.round((double) this.getNumSuccess() / sumO1 * 1000) / 1000.0
                > Math.round((double) o.getNumSuccess() / sumO2 * 1000) / 1000.0) {
            return 1;
        } else {
            return -1;
        }
    }
}

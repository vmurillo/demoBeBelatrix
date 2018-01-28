package com.exchange.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private String origin;
    private String destination;
    private float rate;

    public ExchangeRate(String origin, String destination, float rate) {
        this.origin = origin;
        this.destination = destination;
        this.rate = rate;
    }

    protected ExchangeRate() {}

    public Long getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public float getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "ExchangeRate{" +
                "id=" + id +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", rate=" + rate +
                '}';
    }
}

package com.example.retrofit2stub;


public class Response {
    int total, totalHits;
    Hit[] hits;

    @Override
    public String toString() {
        return "totalHits = " + totalHits;
    }
}

package com.bioproj.live;

@FunctionalInterface
public interface Action<T> {
    void perform(T param);
}
package com.bioproj.k8s;

import java.util.List;
import java.util.Map;

public interface ApiSuccessCallback<T> {
//    void onSuccess(T var1, int var2, Map<String, List<String>> var3);
    void onFinish(T var1, int var2, Map<String, List<String>> var3);
}

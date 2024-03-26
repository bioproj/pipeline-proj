package com.bioproj.service;

import com.bioproj.pojo.Repos;
import com.bioproj.pojo.Repos1;

import java.util.List;

public interface IGiteaService {
    List<Repos> listRepos(String org);
}

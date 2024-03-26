package com.bioproj.service;

import com.bioproj.pojo.Ports;

public interface IPortsService {
    Ports getPorts();


    Ports delPort(Integer port);

    Ports save(Ports portsParam);
}

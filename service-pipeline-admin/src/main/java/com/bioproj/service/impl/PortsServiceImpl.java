package com.bioproj.service.impl;

import com.bioproj.pojo.Ports;
import com.bioproj.repository.PortsRepository;
import com.bioproj.service.IPortsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class PortsServiceImpl implements IPortsService {
    @Autowired
    PortsRepository portsRepository;

    @PostConstruct
    public void init() {
        List<Ports> portsList = portsRepository.findAll();
        if(portsList.size()==0){
            for (Integer i =0;i<100;i++){
                Ports ports = new Ports();
                ports.setIsUse(false);
                ports.setPort(32000+i);
                portsList.add(ports);
            }
            portsRepository.saveAll(portsList);
        }
    }

    @Override
    public synchronized Ports getPorts(){

        List<Ports> portsList = portsRepository.findAll(Example.of(Ports.builder().isUse(false).build()));
        if(portsList.size()==0){
            throw new RuntimeException("没有可用的端口！");
        }
        Ports ports = portsList.get(0);
        ports.setIsUse(true);
        return portsRepository.save(ports);

    }

    public Ports findByPort(Integer port){
        Ports ports = portsRepository.findOne(Example.of(Ports.builder().port(port).build())).orElse(null);
        return ports;
    }

    @Override
    public synchronized Ports delPort(Integer port){
        Ports ports = findByPort(port);
        ports.setIsUse(false);
        ports.setUserId(null);
        return portsRepository.save(ports);
    }


    @Override
    public Ports save(Ports portsParam){
        return portsRepository.save(portsParam);
    }


}

package com.bioproj.controller;

import com.bioproj.domain.BaseResponse;
import com.bioproj.pojo.Repos;
import com.bioproj.service.store.IStoreService;
import com.bioproj.service.IK8sApiService;
import com.bioproj.service.IWorkflowService;
import com.mbiolance.cloud.auth.domain.PageModel;
import com.mbiolance.cloud.auth.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//@RestController
@RequestMapping
@RestController
public class TestController {


//    @Qualifier("FluxExecutorImpl")
//    @Autowired
//    private IExecutorsService fluxExecutorImpl;
//
//
//    @Qualifier("SlurmExecutorImpl")
//    @Autowired
//    private IExecutorsService slurmExecutorImpl;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    IK8sApiService ik8sApiService;


    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private IWorkflowService taskService;

    @GetMapping("/node")
    public String node(String args){
        ik8sApiService.listJobs(ik8sApiService.getJobApi(),"nextflow", Map.of("",""));
        return "node";
    }

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    @GetMapping("/test/producer")
    public BaseResponse producer(){
        Map<String,String> map = new HashMap<>();
        map.put("userName","zhangsan");
        map.put("number","number");
        map.put("sampleType","sampleType");
        map.put("fastq1","fastq1");
        map.put("fastq2","fastq2");
        kafkaTemplate.send("analysis-ngs","submit1",map);
        return BaseResponse.ok("success");
    }
    @GetMapping("/test/trace")
    public BaseResponse trace(){
        Map<String,String> map = new HashMap<>();
        map.put("userName","zhangsan");
        map.put("number","number");
        map.put("sampleType","sampleType");
        map.put("fastq1","fastq1");
        map.put("fastq2","fastq2");
        kafkaTemplate.send("nextflow-trace","submit1",map);
        return BaseResponse.ok("success");
    }



//    @GetMapping("/proxy")
//    public ResponseEntity<String> proxyRequest() {
//        String targetUrl = "http://192.168.10.30:31000"; // 目标URL
//
//        // 发送GET请求到目标URL
//        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, null, String.class);
//
//        // 将目标URL的响应返回给客户端
//        return response;
//
//    }
    @RequestMapping(value = "/proxy/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public void proxyRequest(HttpServletRequest request, HttpServletResponse response) {
        String targetUrl = "http://localhost/pip/node"; // 目标URL
        String proxyPath = request.getRequestURI().replace(request.getContextPath(), ""); // 获取代理路径
//        String targetUri = UriComponentsBuilder.fromHttpUrl(targetUrl).path(proxyPath).build().toUriString(); // 构建目标URI

        // 将请求转发到目标URL
        try {
            request.getRequestDispatcher(targetUrl).forward(request, response);
        } catch (Exception e) {
            // 处理转发异常
            e.printStackTrace();
        }
    }
    @PostMapping("/weblog")
    public String weblog(String args){
        return "";
    }

    @GetMapping("/hello")
    public String hello(String args){

        return "hello";
    }

    @GetMapping("/error1")
    public String hello1(String args){
        throw new RuntimeException("hello！");

    }
//    @GetMapping("a")
//    public void a(){
//        Task task = taskService.findById("1");
//        fluxExecutorImpl.submit(task);
//    }
//    @GetMapping("b")
//    public void b(){
//        Task task = taskService.findById("1");
//        slurmExecutorImpl.submit(task);
//    }


}

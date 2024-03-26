package com.bioproj.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/sequence")
@Slf4j
@Api(tags={"测序"})
public class SequenceController {

//    @Autowired
//    ISequenceService sequenceService;
//    @Autowired
////    private SeqSampleFeignService seqSampleFeignService;
////    @Autowired
////    LabSampleProcessFeignService labSampleProcessFeignService;
//
//    @ApiOperation("分页")
//    @PostMapping("page")
//    public R<PageModel<SeqSampleDto>> page(@RequestBody PageModelQueryVo vo) {
//        if (vo.getNumber() == null) {
//            vo.setNumber(0);
//        }
//        if (vo.getSize() == null) {
//            vo.setSize(10);
//        }
//        R<PageModel<SeqSampleDto>> page = seqSampleFeignService.page(vo);
//        return page;
//    }
}

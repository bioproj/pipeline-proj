package com.bioproj.service.sequence;



//import com.mbiolance.cloud.platform.domain.dto.sequence.SeqSampleDataDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SequenceServiceImpl implements ISequenceService{
//    @Override
//    public Page<SeqSampleDataDto> page(Pageable pageable) {
//        return null;
//    }
    //    @Autowired
//    private SeqSampleFeignService seqSampleFeignService;
//
//
//    @Override
//    public Page<SeqSampleDto>  page(Pageable pageable){
//        PageModelQueryVo pageModelQueryVo = PageModelQueryVo
//                .builder()
//                .size(pageable.getPageSize())
//                .number(pageable.getPageNumber()+1)
//                .build();
//
//        R<PageModel<SeqSampleDto>> page = seqSampleFeignService.page(pageModelQueryVo);
//        PageModel<SeqSampleDto> data = page.getData();
//
//        PageImpl<SeqSampleDto> seqSampleDtos = new PageImpl<>(data.getContent(), PageRequest.of(data.getNumber(), pageable.getPageSize()), data.getCount());
//        return seqSampleDtos;
//    }



}

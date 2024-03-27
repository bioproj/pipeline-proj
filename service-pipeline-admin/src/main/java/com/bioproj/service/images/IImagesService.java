package com.bioproj.service.images;

import com.bioproj.domain.PageModel;
import com.bioproj.domain.vo.ImagesVo;
import com.bioproj.pojo.Images;

public interface IImagesService {

    void refresh();

    PageModel<Images> page(Integer number, Integer size, String name);
    PageModel<Images> page(Integer number, Integer size, ImagesVo imagesVo);
    Images byName(String name);


    Images findById(String id);

    Images del(String s);

    Images save(Images images);

    Images update(String id, Images imagesParams);
}

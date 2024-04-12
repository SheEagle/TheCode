package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TopicCreateVO {
    @Min(1)
    @Max(5)
    Integer type;
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;

}

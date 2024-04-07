package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.service.annotation.PatchExchange;

@Data
public class PrivacySaveVO {

    @Pattern(regexp = "(phone|email|qq|wx|gender)")
    String type;

    boolean status;

}

package com.study.gyh.settings.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class NicknameForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ᄀ-ᄒ가-힣a-z0-9_-]{3,20}$")
    private String nickname;
}

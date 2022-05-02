package jp.kobe_u.cs27.WorkSupportService.application.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 作業名フォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WnameForm {
    /** 作業名 */
    @NotBlank
    private String wname;
}

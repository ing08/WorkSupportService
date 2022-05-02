package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import jp.kobe_u.cs27.WorkSupportService.domain.entity.Work;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
// import lombok.NonNull;
import lombok.Setter;

/**
 * 作業フォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkForm {
    /** 作業ID */
    @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
    @NotNull
    private String wid;

    /** 作業名 */
    // @NonNull
    @NotBlank
    private String wname;

    // /** ユーザID */
    // private String uid;

    /**
     * フォームをエンティティに変換
     *
     * @return Work
     */
    public Work toEntity() {
        return new Work(this.getWid(), this.getWname());
    }
}

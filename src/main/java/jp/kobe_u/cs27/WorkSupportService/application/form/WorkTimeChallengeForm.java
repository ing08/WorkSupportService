package jp.kobe_u.cs27.WorkSupportService.application.form;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 目標作業時間フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeChallengeForm {
    // /** 誰のチャレンジ情報か */
    // @NotNull
    // private String uid;

    /** いつまでのチャレンジか */
    @NotNull
    private String untilst;

    /** 目標作業時間 */
    @NotNull
    private int hour;
}
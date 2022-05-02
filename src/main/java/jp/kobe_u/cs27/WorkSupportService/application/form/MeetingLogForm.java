package jp.kobe_u.cs27.WorkSupportService.application.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import jp.kobe_u.cs27.WorkSupportService.domain.dto.WorkLogWithWnameDto;

/**
 * ミーティングログ作成フォーム
 *
 * @author ing
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingLogForm {
    /** 作業時間のリスト */
    @NonNull
    private List<WorkLogWithWnameDto> times;

    // /** いつから */
    // private String since;

    // /** いつまで */
    // private String until;
}
